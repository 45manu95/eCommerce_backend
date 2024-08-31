package com.manuel.ecommerce.services;

import com.manuel.ecommerce.configurations.JwtAuthConverter;
import com.manuel.ecommerce.entities.Cart;
import com.manuel.ecommerce.entities.Composition;
import com.manuel.ecommerce.entities.Product;
import com.manuel.ecommerce.entities.User;
import com.manuel.ecommerce.repositories.CartRepository;
import com.manuel.ecommerce.repositories.CompositionRepository;
import com.manuel.ecommerce.repositories.ProductRepository;
import com.manuel.ecommerce.repositories.UserRepository;
import com.manuel.ecommerce.support.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtAuthConverter jwtAuthConverter;

    @Transactional(propagation = Propagation.REQUIRED)
    public Composition addProductCart(String barCode, String userCF, int quantity) throws ProductNotExistException, CartNotExistException, IllegalQuantityException, UniqueCostraintException, InvalidTokenException {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getToken();

        // Verifica del codice fiscale tramite il token
        String tokenUserCF = jwtAuthConverter.getUserCFFromToken(jwt);
        if (!userCF.equals(tokenUserCF)) {
            throw new InvalidTokenException();
        }

        Cart cart = cartRepository.findCart(userCF);
        Product product = productRepository.findByBarCode(barCode);
        if(compositionRepository.existsByProductAndCart(product, cart))
            throw new UniqueCostraintException();
        if(!productRepository.existsById(barCode))
            throw new ProductNotExistException();
        if(!cartRepository.existsById(cart.getId()))
            throw new CartNotExistException();
        if(quantity <= 0 || quantity > product.getQuantity())
            throw new IllegalQuantityException();
        double subtotal = quantity * product.getPrice();
        Composition composizione = new Composition();
        composizione.setCart(cart);
        composizione.setProduct(product);
        composizione.setQuantity(quantity);
        composizione.setSubTotal(subtotal);
        cart.setTotalPrice(cart.getTotalPrice() + subtotal);
        return compositionRepository.save(composizione);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Cart removeProductCart(Long id) throws CompositionNotExistException {
        if(!compositionRepository.existsById(id))
            throw new CompositionNotExistException();
        Composition composition = compositionRepository.getReferenceById(id);
        Cart cart = cartRepository.getReferenceById(composition.getCart().getId());
        cart.setTotalPrice(cart.getTotalPrice() - composition.getSubTotal());
        compositionRepository.deleteById(id);
        return cart;
    }

    @Transactional(readOnly = true)
    public List<Composition> showCart(String cf) throws UserNotExistException, InvalidTokenException {

        if(!userRepository.existsById(cf))
            throw new UserNotExistException();
        User user = userRepository.getReferenceById(cf);
        List<Composition> results = userRepository.findProductsCart(user);
        if(!results.isEmpty()){
            return results;
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Cart updateQuantity(Long id, int quantity) throws CompositionNotExistException, CartNotExistException, IllegalQuantityException {
        Composition update = compositionRepository.getReferenceById(id);
        if(!compositionRepository.existsByProductAndCart(update.getProduct(),update.getCart()))
            throw new CompositionNotExistException();
        Product product = productRepository.getReferenceById(update.getProduct().getBarCode());
        if(quantity <= 0)
            throw new IllegalQuantityException();
        update.setQuantity(quantity);
        Cart cart = cartRepository.getReferenceById(update.getCart().getId());
        cart.setTotalPrice(cart.getTotalPrice() - update.getSubTotal());
        double subTotal = update.getQuantity() * product.getPrice();
        update.setSubTotal(subTotal);
        cart.setTotalPrice(cart.getTotalPrice() + subTotal);
        return cart;
    }

}

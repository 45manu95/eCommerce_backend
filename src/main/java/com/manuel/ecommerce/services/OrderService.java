package com.manuel.ecommerce.services;

import com.manuel.ecommerce.configurations.JwtAuthConverter;
import com.manuel.ecommerce.entities.*;
import com.manuel.ecommerce.repositories.*;
import com.manuel.ecommerce.support.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssociatedRepository associatedRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JwtAuthConverter jwtAuthConverter;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ProductNotExistException.class, IllegalQuantityException.class, NegativePriceException.class})
    public Order createOrder(Order order) throws OrderAlreadyExistException, ProductNotExistException, NegativePriceException, CartNoSuchElementException, UserNotExistException, IllegalQuantityException, NegativeQuantityException, InvalidTokenException {
        User user = userRepository.getReferenceById(order.getUserOrder().getCF());

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getToken();

        // Verifica del codice fiscale tramite il token
        String tokenUserCF = jwtAuthConverter.getUserCFFromToken(jwt);
        if (!order.getUserOrder().getCF().equals(tokenUserCF)) {
            throw new InvalidTokenException();
        }


        if(!userRepository.existsById(user.getCF()))
            throw new UserNotExistException();
        Cart cart = cartRepository.getReferenceById(user.getCart().getId());
        order.setTotalPrice(cart.getTotalPrice());
        Date data = new Date();
        order.setPurchaseDate(data);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        List<Composition> CartProducts = orderRepository.findProductCart(cart);
        if(CartProducts.isEmpty())
            throw new CartNoSuchElementException();
        order.setState("Completo");
        orderRepository.save(order);
        order.setId(order.getId());
        for(Composition c:CartProducts){
            Associated associated = new Associated();
            associated.setOrder(order);
            associated.setProduct(c.getProduct());
            associated.setQuantity(c.getQuantity());
            associated.setPrice(c.getSubTotal());
            associatedRepository.save(associated);
            productService.removeProduct(c.getProduct().getBarCode(),c.getQuantity());
            compositionRepository.delete(c);
        }
        cart.setTotalPrice(0);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOrder(Long id) throws OrderNotExistException, IllegalQuantityException, ProductNotExistException {
        Order order = orderRepository.findByOrderNumber(id);
        if(!orderRepository.existsById(id) || order.getState().equals("Annullato"))
            throw new OrderNotExistException();
        List<Associated> orderProducts = associatedRepository.findByOrder(order);
        for(Associated a: orderProducts){
            productService.updateProductQuantity(a.getProduct().getBarCode(), (a.getProduct().getQuantity() + a.getQuantity()));
        }
        order.setState("Annullato");

    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Order> pageResult = orderRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Order> searchUserOrder(String cf, Long orderNumber, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Order> pageResult = orderRepository.findByOrderNumberStartingWith(cf,orderNumber,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Order> filterOrder(String state, Integer year, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Order> pageResult = orderRepository.filterOrder(state, year, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Order> filterOrderUser(String cf,String state, Integer year, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Order> pageResult = orderRepository.filterOrderUser(cf, state, year, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getAllUserOrders(String cf, int pageNumber, int pageSize, String sortBy) throws UserNotExistException {
        if(!userRepository.existsById(cf))
            throw new UserNotExistException();
        User user = userRepository.findById(cf).get();
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Order> pageResult = orderRepository.findByUserOrder(user, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateOrderState(Long id, String state) throws OrderNotExistException {
        if(!orderRepository.existsById(id))
            throw new OrderNotExistException();
        Order modified = orderRepository.findByOrderNumber(id);
        modified.setState(state);
    }

    @Transactional(readOnly = true)
    public List<Order> searchOrder(Long id, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Order> pageResult = orderRepository.findById(id, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Associated> showOrderDetails(Long idOrder) throws OrderNotExistException {
        if(!orderRepository.existsById(idOrder))
            throw new OrderNotExistException();
        List<Associated> pageResult = orderRepository.findOrderProducts(idOrder);
        if(!pageResult.isEmpty()){
            return pageResult;
        }else{
            return new LinkedList<>();
        }
    }
}

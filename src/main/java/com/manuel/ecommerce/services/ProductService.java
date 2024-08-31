package com.manuel.ecommerce.services;

import com.manuel.ecommerce.entities.Product;
import com.manuel.ecommerce.entities.Review;
import com.manuel.ecommerce.repositories.CategoryRepository;
import com.manuel.ecommerce.repositories.ProductRepository;
import com.manuel.ecommerce.support.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Product addProduct(Product product) throws IllegalQuantityException, NegativePriceException, CategoryNotExistException {
        if(product.getQuantity() <= 0)
            throw new IllegalQuantityException();
        if(product.getPrice() <= 0)
            throw new NegativePriceException();
        if(!categoryRepository.existsById(product.getCategory().getName()))
            throw new CategoryNotExistException();
        if(productRepository.existsById(product.getBarCode())){
            Product p = productRepository.getReferenceById(product.getBarCode());
            p.setQuantity(p.getQuantity() + product.getQuantity());
            p.setProductImages(product.getProductImages());
            return productRepository.save(p);
        }else{
            product.setState("Disponibile");
            product.setVisible(true);
            return productRepository.save(product);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeProduct(String barCode) throws ProductNotExistException {
        if(!productRepository.existsById(barCode))
            throw new ProductNotExistException();
        Product prodotto = productRepository.getReferenceById(barCode);
        prodotto.setQuantity(0);
        prodotto.setState("Eliminato");
        prodotto.setVisible(false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void restoreProduct(String barCode) throws ProductNotExistException {
        if(!productRepository.existsById(barCode))
            throw new ProductNotExistException();
        Product product = productRepository.getReferenceById(barCode);
        product.setState("Disponibile");
        product.setVisible(true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeProduct(String barCode, int quantity) throws ProductNotExistException, NegativeQuantityException, IllegalQuantityException {
        if(!productRepository.existsById(barCode))
            throw new ProductNotExistException();
        if(quantity <= 0)
            throw new NegativeQuantityException();
        Product p = productRepository.getReferenceById(barCode);
        if(p.getQuantity() < quantity)
            throw new IllegalQuantityException();
        p.setQuantity(p.getQuantity() - quantity);
        if(p.getQuantity() == 0)
            p.setState("Esaurito");

    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pageResult = productRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> getAllDeletedProducts(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pageResult = productRepository.findDeletedProduct(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> searchProduct(String barCode, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pageResult = productRepository.findByBarCodeStartingWith(barCode,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductName(String name, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pageResult = productRepository.findByNameStartingWith(name,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public double calcAVGReviewsProduct(String barCode) throws ProductNotExistException {
        if(!productRepository.existsById(barCode))
            throw new ProductNotExistException();
        return productRepository.avgReviewProduct(barCode);
    }

    @Transactional(readOnly = true)
    public List<Product> searchDeletedProduct(String barCode, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pageResult = productRepository.searchDeletedProduct(barCode,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public  List<Product> filterProduct(Integer quantity,String state, String category, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pageResult = productRepository.filterProduct(quantity,state,category,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateProductPrice(String barCode, double newPrice) throws ProductNotExistException, NegativePriceException {
        Product update = productRepository.getReferenceById(barCode);
        if(!productRepository.existsById(barCode) || !update.isVisible())
            throw new ProductNotExistException();
        if(newPrice<=0)
            throw new NegativePriceException();
        update.setPrice(newPrice);
        productRepository.save(update);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateProductQuantity(String barCode, int newQuantity) throws ProductNotExistException, IllegalQuantityException {
        Product update = productRepository.getReferenceById(barCode);
        if(!productRepository.existsById(barCode) || !update.isVisible())
            throw new ProductNotExistException();
        if(newQuantity <= 0)
            throw new IllegalQuantityException();
        update.setState("Disponibile");
        update.setQuantity(newQuantity);
        productRepository.save(update);
    }

    @Transactional(readOnly = true)
    public List<Review> getAllProductReviews(String barCode, int pageNumber, int pageSize, String sortBy) throws ProductNotExistException {
        Product product = productRepository.getReferenceById(barCode);
        if(!productRepository.existsById(barCode) || !product.isVisible())
            throw new ProductNotExistException();
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Review> pageResult = productRepository.findProductReviews(product, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }


}

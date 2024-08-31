package com.manuel.ecommerce.services;

import com.manuel.ecommerce.entities.Category;
import com.manuel.ecommerce.entities.Product;
import com.manuel.ecommerce.repositories.CategoryRepository;
import com.manuel.ecommerce.repositories.ProductRepository;
import com.manuel.ecommerce.support.exceptions.CategoryAlreadyExistException;
import com.manuel.ecommerce.support.exceptions.CategoryNotExistException;
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
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Category addCategory(Category category) throws CategoryAlreadyExistException {
        if(categoryRepository.existsById(category.getName()))
            throw new CategoryAlreadyExistException();
        return categoryRepository.save(category);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeCategory(String name) throws CategoryNotExistException {
        if(!categoryRepository.existsById(name))
            throw new CategoryNotExistException();
        List<Product> products = productRepository.findByCategory(name);
        for(Product p:products)
            p.setVisible(false);
        categoryRepository.deleteById(name);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Category> pageResult = categoryRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Category> searchCategory(String categoryName, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Category> pageResult = categoryRepository.findByNameStartingWith(categoryName,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

}

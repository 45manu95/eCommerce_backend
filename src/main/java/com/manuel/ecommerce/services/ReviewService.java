package com.manuel.ecommerce.services;

import com.manuel.ecommerce.entities.Product;
import com.manuel.ecommerce.entities.Review;
import com.manuel.ecommerce.entities.User;
import com.manuel.ecommerce.repositories.*;
import com.manuel.ecommerce.support.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssociatedRepository associatedRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Review addReview(Review review) throws ReviewAlreadyExistException, StarOutOfBoundsException, ProductNotExistException, UserNotExistException, AssociatedNotExistException {
        if(!productRepository.existsById(review.getReviewedProduct().getBarCode()))
            throw new ProductNotExistException();
        if(!userRepository.existsById(review.getUserReview().getCF()))
            throw new UserNotExistException();
        if(reviewRepository.existsByUserReviewAndReviewedProduct(review.getUserReview(), review.getReviewedProduct()))
            throw new ReviewAlreadyExistException();
        if(review.getStars() < 1 || review.getStars() > 5)
            throw new StarOutOfBoundsException();
        if(!associatedRepository.isUserProductPurchased(review.getUserReview().getCF(),review.getReviewedProduct().getBarCode()))
            throw new AssociatedNotExistException();
        review.setDate(new Date());
        Product product = productRepository.getReferenceById(review.getReviewedProduct().getBarCode());
        Review saved = reviewRepository.save(review);
        double average = productService.calcAVGReviewsProduct(product.getBarCode());
        product.setAvgReview(average);
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeReview(Long id) throws ReviewNotExistException, ProductNotExistException {
        if(!reviewRepository.existsById(id))
            throw new ReviewNotExistException();
        Review review = reviewRepository.getReferenceById(id);
        Product product = productRepository.getReferenceById(review.getReviewedProduct().getBarCode());
        double average = productService.calcAVGReviewsProduct(product.getBarCode());
        product.setAvgReview(average);
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Review> getAllReviews(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Review> pageResult = reviewRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Review> getAllUserReviews(String cf,int pageNumber, int pageSize, String sortBy) throws UserNotExistException {
        if(!userRepository.existsById(cf))
            throw new UserNotExistException();
        User cliente = userRepository.findById(cf).get(); //utilizzato questo perchè mi dava problemi nella serializzazione del JSON di risposta
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Review> pageResult = reviewRepository.findByUserReview(cliente, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }
}

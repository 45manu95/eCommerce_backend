package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.Review;
import com.manuel.ecommerce.services.ReviewService;
import com.manuel.ecommerce.support.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<Object> addReview(@RequestBody @Valid Review review){
        try{
            Review added = reviewService.addReview(review);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (ReviewAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("hai già recensito questo prodotto");
        }catch (StarOutOfBoundsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inserire una valutazione tra 1 e 5");
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }catch (ProductNotExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch(AssociatedNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("non hai acquistato il seguente prodotto");
        }
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<String> removeReview(@PathVariable Long id){
        try{
            reviewService.removeReview(id);
            return ResponseEntity.status(HttpStatus.OK).body("Recensione rimossa con successo");
        }catch (ReviewNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recensione inesistente");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }

    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllReviews(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   @RequestParam(value = "sortBy", defaultValue = "date") String sortBy){
        List<Review> result = reviewService.getAllReviews(pageNumber, pageSize, sortBy);
        if(result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/user/{cf}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllRecensioniCliente(@PathVariable String cf,@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "sortBy", defaultValue = "date") String sortBy){
        try {
            List<Review> result = reviewService.getAllUserReviews(cf, pageNumber, pageSize, sortBy);
            if(result.isEmpty())
                return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }
}

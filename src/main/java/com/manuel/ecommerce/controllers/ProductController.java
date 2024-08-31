package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.Image;
import com.manuel.ecommerce.entities.Product;
import com.manuel.ecommerce.entities.Review;
import com.manuel.ecommerce.services.ProductService;
import com.manuel.ecommerce.support.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> addProduct(@RequestPart("product") Product product, @RequestPart("imageFile") MultipartFile[] file){
        try{
            Set<Image> images = uploadImage(file);
            product.setProductImages(images);
            Product added = productService.addProduct(product);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (IllegalQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero di elementi inseriti non validi");
        }catch (NegativePriceException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Costo inserito non valido");
        }catch (CategoryNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria inesistente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossibile caricare l'immagine");
        }
    }

    public Set<Image> uploadImage(MultipartFile[] multipartFiles) throws IOException {
        Set<Image> images = new HashSet<>();
        for(MultipartFile file: multipartFiles){
            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setType(file.getContentType());
            image.setContent(file.getBytes());
            images.add(image);
        }
        return images;
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeProduct(@PathVariable String barCode){
        try{
            productService.removeProduct(barCode);
            return ResponseEntity.status(HttpStatus.OK).body("Prodotto rimosso");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @PutMapping("/restore/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> restoreProduct(@PathVariable String barCode){
        try{
            productService.restoreProduct(barCode);
            return ResponseEntity.status(HttpStatus.OK).body("Prodotto recuperato!");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @DeleteMapping("/remove/{barCode}/{quantity}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeProductQuantity(@PathVariable String barCode, @PathVariable int quantity){
        try{
            productService.removeProduct(barCode,quantity);
            return ResponseEntity.status(HttpStatus.OK).body("Aggiornamento quantità prodotto effettuata con successo");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (NegativeQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero di elementi inseriti non validi");
        }catch (IllegalQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero elementi inferiori a quanto richiesto");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> getAllProducts(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 @RequestParam(value = "sortBy", defaultValue = "barCode") String sortBy){
        List<Product> result = productService.getAllProducts(pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/all/onlyDelete")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllDeletedProducts(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "sortBy", defaultValue = "barCode") String sortBy){
        List<Product> result = productService.getAllDeletedProducts(pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchProduct(@PathVariable String barCode, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 @RequestParam(value = "sortBy", defaultValue = "barCode") String sortBy){
        List<Product> result = productService.searchProduct(barCode, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/searchName/{name}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> searchProductName(@PathVariable String name, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                     @RequestParam(value = "sortBy", defaultValue = "barCode") String sortBy){
        List<Product> result = productService.searchProductName(name, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/AVGReviewsProduct/{id}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> calcAVGReviewsProduct(@PathVariable String barCode){
        try{
            double media = productService.calcAVGReviewsProduct(barCode);
            return ResponseEntity.status(HttpStatus.OK).body(media);
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @GetMapping("/searchDelete/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchDeletedProduct(@PathVariable String id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "sortBy", defaultValue = "barCode") String sortBy){
        List<Product> result = productService.searchDeletedProduct(id, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> filterProduct( @RequestParam(required = false) Integer quantity, @RequestParam(required = false) String state, @RequestParam(required = false) String category,
                                                  @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                  @RequestParam(value = "sortBy", defaultValue = "barCode") String sortBy) {
        List<Product> result = productService.filterProduct(quantity, state, category, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/updateProductPrice")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> updateProductPrice(@RequestParam String barCode,@RequestParam double newPrice){
        try{
            productService.updateProductPrice(barCode, newPrice);
            return ResponseEntity.status(HttpStatus.OK).body("Prezzo aggiornato con successo");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (NegativePriceException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Costo inserito non valido");
        }
    }

    @PutMapping("/updateProductQuantity")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> updateProductQuantity(@RequestParam String barCode, @RequestParam int newQuantity){
        try{
            productService.updateProductQuantity(barCode, newQuantity);
            return ResponseEntity.status(HttpStatus.OK).body("Quantità aggiornata con successo");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (IllegalQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero elementi inseriti non validi");
        }
    }

    @GetMapping("/{barCode}/review/all")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllProductReviews(@PathVariable String barCode, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                           @RequestParam(value = "sortBy", defaultValue = "barCode") String sortBy){
        try {
            List<Review> result = productService.getAllProductReviews(barCode, pageNumber, pageSize, sortBy);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }
}

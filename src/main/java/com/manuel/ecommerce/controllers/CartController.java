package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.Cart;
import com.manuel.ecommerce.entities.Composition;
import com.manuel.ecommerce.services.CartService;
import com.manuel.ecommerce.support.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/addProduct/{barCode}/{cf}/{quantity}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> addCartProduct(@PathVariable String barCode, @PathVariable String cf, @PathVariable int quantity){
        try{
            Composition added = cartService.addProductCart(barCode, cf, quantity);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto non presente");
        }catch (CartNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Carrello non esistente");
        }catch (IllegalQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantità non valida");
        }catch(UniqueCostraintException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto gia presente nel carrello");
        } catch(InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente incorretto (token errato)");
        }
    }

    @DeleteMapping("/removeProduct/{barCode}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> removeCartProduct(@PathVariable Long barCode){
        try{
            Cart update = cartService.removeProductCart(barCode);
            return ResponseEntity.status(HttpStatus.OK).body(update);
        }catch (CompositionNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto non presente nel carrello");
        }
    }

    @GetMapping("/show/{cf}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> showCart(@PathVariable String cf){
        try {
            List<Composition> result = cartService.showCart(cf);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("No results!");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente inesistente");
        } catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente incorretto (token errato)");
        }
    }

    @PutMapping("/updateQuantity/{id}/{quantity}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> updateQuantity(@PathVariable Long id,@PathVariable int quantity) {
        try{
            Cart update = cartService.updateQuantity(id, quantity);
            return ResponseEntity.status(HttpStatus.OK).body(update);
        } catch (CompositionNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto non presente nel carrello");
        } catch (CartNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Carrello inesistente");
        } catch (IllegalQuantityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantità inserita non valida");
        }
    }
}

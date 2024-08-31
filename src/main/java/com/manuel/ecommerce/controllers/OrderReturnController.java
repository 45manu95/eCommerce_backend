package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.OrderReturn;
import com.manuel.ecommerce.services.OrderReturnService;
import com.manuel.ecommerce.support.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderReturn")
public class OrderReturnController {
    @Autowired
    private OrderReturnService orderReturnService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> addOrderReturn(@Valid @RequestBody OrderReturn orderReturn){
        try{
            orderReturnService.addOrderReturn(orderReturn);
            return ResponseEntity.status(HttpStatus.OK).body("Reso effettuato con successo");
        }catch (OrderReturnAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reso già richiesto");
        }catch (OrderNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossibile richiedere reso. Ordine inesistente");
        }catch (IllegalQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("quantità inserita non valida");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<String> removeOrderReturn(@PathVariable Long id){
        try {
            orderReturnService.removeOrderReturn(id);
            return ResponseEntity.status(HttpStatus.OK).body("Reso rimosso con successo");
        }catch (OrderReturnNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reso inesistente");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (NegativeQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero di elementi inseriti non validi");
        }catch (IllegalQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero elementi inferiori a quanto richiesto");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllOrderReturns(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                             @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<OrderReturn> result = orderReturnService.getAllOrderReturns(pageNumber, pageSize, sortBy);
        if(result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{cf}/all")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllUserOrderReturns(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                    @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        try {
            List<OrderReturn> result = orderReturnService.getAllUserOrderReturns(cf, pageNumber, pageSize, sortBy);
            if (result.isEmpty())
                return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.OK).body("Cliente inesistente");
        }
    }
}

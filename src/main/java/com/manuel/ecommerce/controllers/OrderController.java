package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.Associated;
import com.manuel.ecommerce.entities.Order;
import com.manuel.ecommerce.services.OrderService;
import com.manuel.ecommerce.support.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody Order order){
        try{
            Order added = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        }catch (OrderAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine già presente");
        } catch (ProductNotExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }catch (IllegalQuantityException | NegativeQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Non ci sono abbastanza prodotti");
        }catch (NegativePriceException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantità inserita non valida");
        }catch (CartNoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nessun prodotto presente nel carrello");
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore autenticazione");
        }
    }

    @DeleteMapping("/remove/{orderNumber}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeOrder(@PathVariable Long orderNumber){
        try{
            orderService.removeOrder(orderNumber);
            return ResponseEntity.status(HttpStatus.OK).body("Ordine rimosso con successo");
        }catch (OrderNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine inesistente");
        }catch (IllegalQuantityException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("quantità inserita non valida");
        }catch (ProductNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prodotto inesistente");
        }
    }

    @PutMapping("/updateState")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> updateOrderState(@RequestParam Long id, @RequestParam String state){
        try {
            orderService.updateOrderState(id, state);
            return ResponseEntity.status(HttpStatus.OK).body("Stato ordine aggiornato");
        }catch (OrderNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine inesistente");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllOrders(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                               @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Order> result = orderService.getAllOrders(pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun Risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{id}/{cf}")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> searchUserOrder( @PathVariable String cf, @PathVariable Long id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                       @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Order> result = orderService.searchUserOrder(cf,id, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun Risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> filterOrder(@RequestParam(required = false) String state, @RequestParam(required = false) Integer year,
                                               @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                               @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Order> result = orderService.filterOrder(state, year, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun Risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{id}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchOrder(@PathVariable Long id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                               @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Order> result = orderService.searchOrder(id, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/filter/{cf}")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<Object> filterOrderUser(@PathVariable String cf, @RequestParam(required = false) String state, @RequestParam(required = false) Integer year,
                                                      @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                      @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        List<Order> result = orderService.filterOrderUser(cf,state, year, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun Risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/all/{cf}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> getAllUserOrders(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                      @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        try{
            List<Order> result = orderService.getAllUserOrders(cf, pageNumber, pageSize, sortBy);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("Nessun Risultato");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente non presente");
        }
    }

    @GetMapping("/details/{idOrder}")
    @PreAuthorize("hasRole('role_user') or hasRole('role_admin')")
    public ResponseEntity<Object> showOrderDetails(@PathVariable Long idOrder){
        try{
            List<Associated> result = orderService.showOrderDetails(idOrder);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("Nessun Risultato");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (OrderNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ordine inesistente");
        }
    }
}

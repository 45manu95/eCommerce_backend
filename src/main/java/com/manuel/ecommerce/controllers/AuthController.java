package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.User;
import com.manuel.ecommerce.support.exceptions.*;
import com.manuel.ecommerce.services.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/subscribe")
    @PermitAll
    public ResponseEntity<String> subscribeUser(@RequestBody @Valid User user){
        try {
            authService.subscribeUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("cliente registrato con successo");
        }catch (UserAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente già presente");
        }catch (EmailAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email esistente");
        }catch (CellularNumberAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numero telefonico esistente");
        }catch (UserNotSubscribeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore. Cliente non registrato");
        }
    }

    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<String> removeCliente(@PathVariable String userId){
        try {
            authService.removeUser(userId);
            return ResponseEntity.status(HttpStatus.OK).body("Cliente rimosso con successo");
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }

    @PutMapping("/restore/{cf}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> restoreUser(@PathVariable String cf){
        try{
            authService.restoreUser(cf);
            return ResponseEntity.status(HttpStatus.OK).body("Utente recuperato con successo");
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente inesistente");
        }
    }
}

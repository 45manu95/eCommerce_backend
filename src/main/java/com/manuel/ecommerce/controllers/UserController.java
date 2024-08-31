package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.User;
import com.manuel.ecommerce.services.UserService;
import com.manuel.ecommerce.support.exceptions.*;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/update")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<String> updateUser(@RequestBody @Valid User user){
        try {
            userService.updateUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Registrazione effettuata");
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente inesistente");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllUsers(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(value = "sortBy", defaultValue = "surname") String sortBy){
        List<User> result = userService.getAllUsers(pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{cf}")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> getUser(@PathVariable String cf){
        try {
            User result = userService.getUser(cf);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente inesistente");
        }
    }

    @GetMapping("/search/{cf}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchUser(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(value = "sortBy", defaultValue = "surname") String sortBy){
        List<User> result = userService.searchUser(cf, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/filter")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> filterUser(@RequestParam(required = false) String name, @RequestParam(required = false) String surname, @RequestParam(required = false) String city,
                                                @RequestParam(required = false) String address, @RequestParam(required = false) Integer cap,
                                                @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(value = "sortBy", defaultValue = "surname") String sortBy) {

        List<User> result = userService.filterUser(name, surname, city, address, cap, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun Risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/checkVisible/{username}")
    @PermitAll
    public ResponseEntity<Object> isVisible(@PathVariable String username){
        try{
            boolean isVisibile = userService.isVisible(username);
            return ResponseEntity.status(HttpStatus.OK).body(isVisibile);
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/all/onlyDelete")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> getAllClientiEliminati(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                         @RequestParam(value = "sortBy", defaultValue = "CF") String sortBy){
        List<User> result = userService.getAllDeletedUsers(pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/searchDelete/{cf}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchDeletedUser(@PathVariable String cf, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                         @RequestParam(value = "sortBy", defaultValue = "CF") String sortBy){
        List<User> result = userService.searchDeletedUser(cf, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("No results!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}

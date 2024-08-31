package com.manuel.ecommerce.services;

import com.manuel.ecommerce.configurations.Credentials;
import com.manuel.ecommerce.entities.Cart;
import com.manuel.ecommerce.entities.EnumRole;
import com.manuel.ecommerce.entities.Role;
import com.manuel.ecommerce.entities.User;
import com.manuel.ecommerce.repositories.RoleRepository;
import com.manuel.ecommerce.repositories.UserRepository;
import com.manuel.ecommerce.support.exceptions.*;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    private UsersResource usersResource;

    @Transactional(propagation = Propagation.REQUIRED)
    public void subscribeUser(User user) throws UserAlreadyExistException, EmailAlreadyExistException, CellularNumberAlreadyExistException, UserNotSubscribeException {
        if (userRepository.existsById(user.getCF()))
            throw new UserAlreadyExistException();
        if (userRepository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyExistException();
        if (userRepository.existsByCellularNumber(user.getCellularNumber()))
            throw new CellularNumberAlreadyExistException();
        Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Response response = subscribeUserToKeycloak(user);

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            Cart carrello = new Cart();
            carrello.setUser(user);
            carrello.setTotalPrice(0.0);
            user.setCart(carrello);
            user.setRole(userRole);
            user.setVisible(true);
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
        } else {
            throw new UserNotSubscribeException();
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUser(String cf) throws UserNotExistException {
        if (!userRepository.existsById(cf))
            throw new UserNotExistException();
        User user = userRepository.getReferenceById(cf);
        user.setVisible(false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void restoreUser(String cf) throws UserNotExistException {
        if (!userRepository.existsById(cf))
            throw new UserNotExistException();
        User user = userRepository.getReferenceById(cf);
        String userId = getKeycloakUserIdByCF(user.getUsername());
        if (userId == null) {
            Response response = subscribeUserToKeycloak(user);

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                user.setVisible(true);
            }
        } else {
            user.setVisible(true);
        }
    }

    private String getKeycloakUserIdByCF(String username) {
        RealmResource realmResource = keycloak.realm("eCommerce");
        usersResource = realmResource.users();
        List<UserRepresentation> users = usersResource.search(username);
        if (users != null && !users.isEmpty()) {
            return users.get(0).getId();
        }
        return null;
    }

    private Response subscribeUserToKeycloak(User user) {
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(user.getPassword());
        UserRepresentation user1 = new UserRepresentation();
        user1.setUsername(user.getUsername());
        user1.setFirstName(user.getName());
        user1.setLastName(user.getSurname());
        user1.setEmail(user.getEmail());
        user1.setCredentials(Collections.singletonList(credential));
        user1.setAttributes(Collections.singletonMap("cf", Collections.singletonList(user.getCF())));
        user1.setClientRoles(Collections.singletonMap(user.getCF(), List.of("user")));
        user1.setEmailVerified(false);
        user1.setEnabled(true);
        RealmResource realmResource = keycloak.realm("eCommerce");
        usersResource = realmResource.users();
        Response response = usersResource.create(user1);
        if (response.getStatus() == 200 || response.getStatus() == 201) {
            //getting client
            ClientRepresentation client = realmResource.clients()
                    .findByClientId("eCommerce_login").get(0);
            String userId = getKeycloakUserIdByCF(user.getUsername());
            UserResource userResource = usersResource.get(userId);
            RoleRepresentation userClientRole = realmResource.clients().get(client.getId())
                    .roles().get("user").toRepresentation();
            userResource.roles()
                    .clientLevel(client.getId()).add(Collections.singletonList(userClientRole));
        }
        return response;
    }
}

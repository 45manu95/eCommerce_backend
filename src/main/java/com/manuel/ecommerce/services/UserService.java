package com.manuel.ecommerce.services;

import com.manuel.ecommerce.entities.User;
import com.manuel.ecommerce.repositories.UserRepository;
import com.manuel.ecommerce.support.exceptions.UserNotExistException;
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
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getAllUsers(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<User> pageResult = userRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public User getUser(String cf) throws UserNotExistException {
        if(!userRepository.existsById(cf))
            throw new UserNotExistException();
        return userRepository.findById(cf).get();
    }


    @Transactional(readOnly = true)
    public List<User> searchUser(String cf, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<User> pageResult = userRepository.findByCFStartingWith(cf,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<User> searchDeletedUser(String cf, int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<User> pageResult = userRepository.searchDeletedUser(cf,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<User> getAllDeletedUsers(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<User> pageResult = userRepository.findDeletedUsers(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<User> filterUser(String name, String surname, String city, String address, Integer cap, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<User> pageResult = userRepository.filterUser(name,surname,city,address,cap,paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User updateUser(User user) throws UserNotExistException {
        if(!userRepository.existsById(user.getCF()))
            throw new UserNotExistException();
        User update = userRepository.getReferenceById(user.getCF());
        update.setCap(user.getCap());
        update.setCity(user.getCity());
        update.setSurname(user.getSurname());
        update.setName(user.getName());
        update.setAddress(user.getAddress());
        update.setCity(user.getCity());
        update.setRegion(user.getRegion());
        update.setCellularNumber(user.getCellularNumber());
        return update;
    }

    @Transactional(readOnly = true)
    public boolean isVisible(String username) throws UserNotExistException {
        if(!userRepository.existsByUsername(username))
            throw new UserNotExistException();
        return userRepository.findByUsernameIsVisibile(username);
    }
}

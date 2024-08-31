package com.manuel.ecommerce.services;

import com.manuel.ecommerce.entities.Associated;
import com.manuel.ecommerce.entities.Order;
import com.manuel.ecommerce.entities.OrderReturn;
import com.manuel.ecommerce.entities.User;
import com.manuel.ecommerce.repositories.*;
import com.manuel.ecommerce.support.exceptions.*;
import jakarta.validation.Valid;
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
public class OrderReturnService {
    @Autowired
    private OrderReturnRepository orderReturnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AssociatedRepository associatedRepository;

    @Autowired
    private ProductService productService;

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderReturn addOrderReturn(@Valid OrderReturn orderReturn) throws OrderReturnAlreadyExistException, OrderNotExistException, IllegalQuantityException, ProductNotExistException {
        Order order = orderRepository.getReferenceById(orderReturn.getOrder().getId());
        User user = userRepository.getReferenceById(orderReturn.getUserOrderReturn().getCF());
        if(orderReturnRepository.existsByOrder(order))
            throw new OrderReturnAlreadyExistException();
        if(!orderRepository.existsByUserOrderAndOrderNumber(user.getCF(), order.getId()) || order.getState().equals("Annullato"))
            throw new OrderNotExistException();
        Date today = new Date();
        orderReturn.setOrder(order);
        orderService.removeOrder(order.getId());
        return orderReturnRepository.save(orderReturn);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOrderReturn(Long id) throws OrderReturnNotExistException, NegativeQuantityException, ProductNotExistException, IllegalQuantityException {
        if(!orderReturnRepository.existsById(id))
            throw new OrderReturnNotExistException();
        OrderReturn orderReturn = orderReturnRepository.getReferenceById(id);
        Order order = orderReturn.getOrder();
        order.setState("Consegnato");
        List<Associated> orderProducts = associatedRepository.findByOrder(order);
        for(Associated a: orderProducts){
            productService.removeProduct(a.getProduct().getBarCode(), a.getQuantity());
        }
        orderReturnRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderReturn> getAllOrderReturns(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<OrderReturn> pageResult = orderReturnRepository.findAll(paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<OrderReturn> getAllUserOrderReturns(String cf, int pageNumber, int pageSize, String sortBy) throws UserNotExistException {
        if(!userRepository.existsById(cf))
            throw new UserNotExistException();
        User user = userRepository.findById(cf).get();
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<OrderReturn> pageResult = orderReturnRepository.findByUserOrderReturn(user, paging);
        if(pageResult.hasContent()){
            return pageResult.getContent();
        }else{
            return new LinkedList<>();
        }
    }
}

package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Order> findById(@PathVariable Long id, Authentication authentication) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        User user = (User) authentication.getPrincipal();
        if (!user.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            if (!order.getClient().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
        }
        
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Order> insert(@RequestBody Order order, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        order.setClient(user);
        order = orderRepository.save(order);
        return ResponseEntity.ok(order);
    }
}

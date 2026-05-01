package com.Customer.management.system.Customer.management.system.controller;

import com.Customer.management.system.Customer.management.system.DTO.CustomerRequestDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerResponseDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerSummaryDTO;
import com.Customer.management.system.Customer.management.system.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO created = customerService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO updated = customerService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        // If search is provided, return filtered list
        if (search != null && !search.trim().isEmpty()) {
            List<CustomerSummaryDTO> results = customerService.search(search);
            return ResponseEntity.ok(results);
        }

        // If pagination is requested
        if (page != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<CustomerSummaryDTO> paged = customerService.getAllPaged(pageable);
            return ResponseEntity.ok(paged);
        }

        // Default: return all as list
        List<CustomerSummaryDTO> all = customerService.getAll();
        return ResponseEntity.ok(all);
    }
}
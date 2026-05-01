package com.Customer.management.system.Customer.management.system.service;


import com.Customer.management.system.Customer.management.system.DTO.CustomerRequestDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerResponseDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO create(CustomerRequestDTO requestDTO);

    CustomerResponseDTO update(Long id, CustomerRequestDTO requestDTO);

    CustomerResponseDTO getById(Long id);

    List<CustomerSummaryDTO> getAll();

    Page<CustomerSummaryDTO> getAllPaged(Pageable pageable);

    List<CustomerSummaryDTO> search(String keyword);
}

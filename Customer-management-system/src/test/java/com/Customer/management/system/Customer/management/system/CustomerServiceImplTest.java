package com.Customer.management.system.Customer.management.system;

import com.Customer.management.system.Customer.management.system.DTO.CustomerRequestDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerResponseDTO;
import com.Customer.management.system.Customer.management.system.entiry.Customer;
import com.Customer.management.system.Customer.management.system.repository.CityRepository;
import com.Customer.management.system.Customer.management.system.repository.CountryRepository;
import com.Customer.management.system.Customer.management.system.repository.CustomerRepository;
import com.Customer.management.system.Customer.management.system.serviceimpl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new CustomerRequestDTO();
        requestDTO.setName("Vishwa");
        requestDTO.setNic("200012345V");
        requestDTO.setDob(LocalDate.of(2000, 1, 1));
    }

    @Test
    void testCreateCustomer_Success() {
        when(customerRepository.existsByNic("200012345V")).thenReturn(false);

        Customer saved = new Customer();
        saved.setId(1L);
        saved.setName("Vishwa");
        saved.setNic("200012345V");

        when(customerRepository.save(any(Customer.class))).thenReturn(saved);
        when(customerRepository.findByIdWithMobiles(1L)).thenReturn(Optional.of(saved));
        when(customerRepository.findByIdWithAddresses(1L)).thenReturn(Optional.of(saved));
        when(customerRepository.findByIdWithFamily(1L)).thenReturn(Optional.of(saved));

        CustomerResponseDTO response = customerService.create(requestDTO);

        assertNotNull(response);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_DuplicateNIC_ShouldThrowException() {
        when(customerRepository.existsByNic("200012345V")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            customerService.create(requestDTO);
        });
    }

    @Test
    void testGetById_CustomerExists() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test");

        when(customerRepository.findByIdWithMobiles(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByIdWithAddresses(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByIdWithFamily(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDTO response = customerService.getById(1L);

        assertNotNull(response);
    }
}
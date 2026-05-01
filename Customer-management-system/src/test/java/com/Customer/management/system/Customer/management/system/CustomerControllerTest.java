package com.Customer.management.system.Customer.management.system;

import com.Customer.management.system.Customer.management.system.DTO.CustomerResponseDTO;
import com.Customer.management.system.Customer.management.system.controller.CustomerController;
import com.Customer.management.system.Customer.management.system.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCustomerById() throws Exception {

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(1L);
        dto.setName("Test User");

        Mockito.when(customerService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}
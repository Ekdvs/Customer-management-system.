package com.Customer.management.system.Customer.management.system.utill;


import com.Customer.management.system.Customer.management.system.DTO.AddressDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerRequestDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerResponseDTO;
import com.Customer.management.system.Customer.management.system.DTO.FamilyMemberDTO;
import com.Customer.management.system.Customer.management.system.entiry.Address;
import com.Customer.management.system.Customer.management.system.entiry.Customer;
import com.Customer.management.system.Customer.management.system.entiry.MobileNumber;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateless utility class for mapping between Customer entities and DTOs.
 */
public class CustomerMapper {

    private CustomerMapper() {}

    /**
     * Maps a fully-loaded Customer entity to a CustomerResponseDTO.
     * Assumes all lazy collections have already been initialized.
     */
    public static CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) return null;

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setDob(customer.getDob());
        dto.setNic(customer.getNic());

        // Mobile numbers
        if (customer.getMobileNumbers() != null) {
            List<String> mobiles = customer.getMobileNumbers().stream()
                    .map(MobileNumber::getNumber)
                    .collect(Collectors.toList());
            dto.setMobileNumbers(mobiles);
        }

        // Addresses
        if (customer.getAddresses() != null) {
            List<AddressDTO> addressDTOs = customer.getAddresses().stream()
                    .map(CustomerMapper::toAddressDTO)
                    .collect(Collectors.toList());
            dto.setAddresses(addressDTOs);
        }

        // Family members — flat, to avoid circular nesting
        if (customer.getFamilyMembers() != null) {
            List<FamilyMemberDTO> familyDTOs = customer.getFamilyMembers().stream()
                    .map(fm -> new FamilyMemberDTO(fm.getId(), fm.getName(), fm.getNic(), fm.getDob()))
                    .collect(Collectors.toList());
            dto.setFamilyMembers(familyDTOs);
        }

        return dto;
    }

    /**
     * Maps an Address entity to AddressDTO.
     */
    public static AddressDTO toAddressDTO(Address address) {
        if (address == null) return null;

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());

        if (address.getCity() != null) {
            dto.setCityId(address.getCity().getId());
            dto.setCityName(address.getCity().getName());
        }
        if (address.getCountry() != null) {
            dto.setCountryId(address.getCountry().getId());
            dto.setCountryName(address.getCountry().getName());
        }

        return dto;
    }

    /**
     * Creates a new Customer entity from a request DTO (no relationships resolved here).
     */
    public static Customer toEntity(CustomerRequestDTO dto) {
        if (dto == null) return null;

        Customer customer = new Customer();
        customer.setName(dto.getName().trim());
        customer.setDob(dto.getDob());
        customer.setNic(dto.getNic().trim());
        return customer;
    }

    /**
     * Updates mutable fields on an existing Customer entity from a request DTO.
     */
    public static void updateEntity(Customer existing, CustomerRequestDTO dto) {
        existing.setName(dto.getName().trim());
        existing.setDob(dto.getDob());
        existing.setNic(dto.getNic().trim());
    }
}

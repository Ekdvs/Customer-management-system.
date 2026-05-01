package com.Customer.management.system.Customer.management.system.serviceimpl;
import com.Customer.management.system.Customer.management.system.DTO.AddressDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerRequestDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerResponseDTO;
import com.Customer.management.system.Customer.management.system.DTO.CustomerSummaryDTO;
import com.Customer.management.system.Customer.management.system.config.DuplicateNicException;
import com.Customer.management.system.Customer.management.system.config.ResourceNotFoundException;
import com.Customer.management.system.Customer.management.system.entiry.*;
import com.Customer.management.system.Customer.management.system.repository.CityRepository;
import com.Customer.management.system.Customer.management.system.repository.CountryRepository;
import com.Customer.management.system.Customer.management.system.repository.CustomerRepository;
import com.Customer.management.system.Customer.management.system.service.CustomerService;
import com.Customer.management.system.Customer.management.system.utill.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CityRepository cityRepository,
                               CountryRepository countryRepository) {
        this.customerRepository = customerRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    @Override
    public CustomerResponseDTO create(CustomerRequestDTO requestDTO) {
        // Validate unique NIC
        if (customerRepository.existsByNic(requestDTO.getNic().trim())) {
            throw new DuplicateNicException(requestDTO.getNic());
        }

        Customer customer = CustomerMapper.toEntity(requestDTO);

        // Mobile numbers
        List<MobileNumber> mobileNumbers = buildMobileNumbers(requestDTO, customer);
        customer.setMobileNumbers(mobileNumbers);

        // Addresses
        List<Address> addresses = buildAddresses(requestDTO, customer);
        customer.setAddresses(addresses);

        // Save customer first so we have an ID for family links
        Customer saved = customerRepository.save(customer);

        // Family members
        linkFamilyMembers(saved, requestDTO.getFamilyMemberIds());

        Customer result = customerRepository.save(saved);
        return buildFullResponse(result);
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO requestDTO) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        // Validate NIC uniqueness (exclude self)
        if (customerRepository.existsByNicAndIdNot(requestDTO.getNic().trim(), id)) {
            throw new DuplicateNicException(requestDTO.getNic());
        }

        CustomerMapper.updateEntity(existing, requestDTO);

        // Replace mobile numbers
        existing.getMobileNumbers().clear();
        List<MobileNumber> mobileNumbers = buildMobileNumbers(requestDTO, existing);
        existing.getMobileNumbers().addAll(mobileNumbers);

        // Replace addresses
        existing.getAddresses().clear();
        List<Address> addresses = buildAddresses(requestDTO, existing);
        existing.getAddresses().addAll(addresses);

        // Replace family members
        existing.getFamilyMembers().clear();
        linkFamilyMembers(existing, requestDTO.getFamilyMemberIds());

        Customer saved = customerRepository.save(existing);
        return buildFullResponse(saved);
    }

    // ----------------------------------------------------------------
    // READ SINGLE
    // ----------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getById(Long id) {
        // Fetch in separate queries to avoid Cartesian product from multiple JOIN FETCHes
        Customer withMobiles = customerRepository.findByIdWithMobiles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        Customer withAddresses = customerRepository.findByIdWithAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        Customer withFamily = customerRepository.findByIdWithFamily(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        // Merge into one object — withMobiles is our base
        withMobiles.setAddresses(withAddresses.getAddresses());
        withMobiles.setFamilyMembers(withFamily.getFamilyMembers());

        return CustomerMapper.toResponseDTO(withMobiles);
    }

    // ----------------------------------------------------------------
    // READ ALL (table view — summary only, minimal DB calls)
    // ----------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CustomerSummaryDTO> getAll() {
        return customerRepository.findAllSummaries();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerSummaryDTO> getAllPaged(Pageable pageable) {
        return customerRepository.findAllSummariesPaged(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSummaryDTO> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerRepository.findAllSummaries();
        }
        return customerRepository.searchByNameOrNic(keyword.trim());
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    private List<MobileNumber> buildMobileNumbers(CustomerRequestDTO dto, Customer customer) {
        List<MobileNumber> list = new ArrayList<>();
        if (dto.getMobileNumbers() != null) {
            for (String num : dto.getMobileNumbers()) {
                if (num != null && !num.trim().isEmpty()) {
                    MobileNumber m = new MobileNumber();
                    m.setNumber(num.trim());
                    m.setCustomer(customer);
                    list.add(m);
                }
            }
        }
        return list;
    }

    private List<Address> buildAddresses(CustomerRequestDTO dto, Customer customer) {
        List<Address> list = new ArrayList<>();
        if (dto.getAddresses() != null) {
            for (AddressDTO addrDTO : dto.getAddresses()) {
                Address address = new Address();
                address.setLine1(addrDTO.getLine1());
                address.setLine2(addrDTO.getLine2());
                address.setCustomer(customer);

                if (addrDTO.getCityId() != null) {
                    City city = cityRepository.findById(addrDTO.getCityId())
                            .orElseThrow(() -> new ResourceNotFoundException("City", addrDTO.getCityId()));
                    address.setCity(city);
                }

                if (addrDTO.getCountryId() != null) {
                    Country country = countryRepository.findById(addrDTO.getCountryId())
                            .orElseThrow(() -> new ResourceNotFoundException("Country", addrDTO.getCountryId()));
                    address.setCountry(country);
                }

                list.add(address);
            }
        }
        return list;
    }

    private void linkFamilyMembers(Customer customer, List<Long> familyMemberIds) {
        if (familyMemberIds == null || familyMemberIds.isEmpty()) return;

        for (Long memberId : familyMemberIds) {
            if (memberId.equals(customer.getId())) continue; // skip self-reference

            Customer member = customerRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer (family member)", memberId));

            if (!customer.getFamilyMembers().contains(member)) {
                customer.getFamilyMembers().add(member);
            }
        }
    }

    /**
     * Build a full CustomerResponseDTO by fetching all associations for the saved entity.
     * Called after save to get fresh data from DB.
     */
    private CustomerResponseDTO buildFullResponse(Customer customer) {
        // Re-fetch with all associations to ensure consistency after save
        Customer withMobiles = customerRepository.findByIdWithMobiles(customer.getId())
                .orElse(customer);
        Customer withAddresses = customerRepository.findByIdWithAddresses(customer.getId())
                .orElse(customer);
        Customer withFamily = customerRepository.findByIdWithFamily(customer.getId())
                .orElse(customer);

        withMobiles.setAddresses(withAddresses.getAddresses());
        withMobiles.setFamilyMembers(withFamily.getFamilyMembers());

        return CustomerMapper.toResponseDTO(withMobiles);
    }
}
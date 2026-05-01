package com.Customer.management.system.Customer.management.system.repository;

import com.Customer.management.system.Customer.management.system.entiry.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByNameIgnoreCase(String name);
}

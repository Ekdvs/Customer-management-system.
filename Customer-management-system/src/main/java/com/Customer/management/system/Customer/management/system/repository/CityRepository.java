package com.Customer.management.system.Customer.management.system.repository;


import com.Customer.management.system.Customer.management.system.entiry.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByNameIgnoreCase(String name);

    /** Eager-load country alongside city to avoid N+1 on lookups */
    @Query("SELECT c FROM City c JOIN FETCH c.country")
    List<City> findAllWithCountry();
}
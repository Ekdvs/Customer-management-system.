package com.Customer.management.system.Customer.management.system.repository;


import com.Customer.management.system.Customer.management.system.DTO.CustomerSummaryDTO;
import com.Customer.management.system.Customer.management.system.entiry.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNic(String nic);

    boolean existsByNic(String nic);

    boolean existsByNicAndIdNot(String nic, Long id);

    @Query("SELECT DISTINCT c FROM Customer c " +
            "LEFT JOIN FETCH c.mobileNumbers " +
            "WHERE c.id = :id")
    Optional<Customer> findByIdWithMobiles(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Customer c " +
            "LEFT JOIN FETCH c.addresses a " +
            "LEFT JOIN FETCH a.city " +
            "LEFT JOIN FETCH a.country " +
            "WHERE c.id = :id")
    Optional<Customer> findByIdWithAddresses(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Customer c " +
            "LEFT JOIN FETCH c.familyMembers " +
            "WHERE c.id = :id")
    Optional<Customer> findByIdWithFamily(@Param("id") Long id);


    @Query("SELECT new  com.Customer.management.system.Customer.management.system.DTO.CustomerSummaryDTO(" +
            "  c.id, c.name, c.dob, c.nic, " +
            "  (SELECT COUNT(m) FROM MobileNumber m WHERE m.customer = c), " +
            "  (SELECT COUNT(a) FROM Address a WHERE a.customer = c), " +
            "  (SELECT COUNT(f) FROM Customer c2 JOIN c2.familyMembers f WHERE c2 = c)" +
            ") FROM Customer c")
    List<CustomerSummaryDTO> findAllSummaries();

    /**
     * Paginated summary for large datasets.
     */
    @Query(value = "SELECT new  com.Customer.management.system.Customer.management.system.DTO.CustomerSummaryDTO(" +
            "  c.id, c.name, c.dob, c.nic, " +
            "  (SELECT COUNT(m) FROM MobileNumber m WHERE m.customer = c), " +
            "  (SELECT COUNT(a) FROM Address a WHERE a.customer = c), " +
            "  (SELECT COUNT(f) FROM Customer c2 JOIN c2.familyMembers f WHERE c2 = c)" +
            ") FROM Customer c",
            countQuery = "SELECT COUNT(c) FROM Customer c")
    Page<CustomerSummaryDTO> findAllSummariesPaged(Pageable pageable);

    /**
     * Search by name or NIC.
     */
    @Query("SELECT new com.Customer.management.system.Customer.management.system.DTO.CustomerSummaryDTO(" +
            "  c.id, c.name, c.dob, c.nic, " +
            "  (SELECT COUNT(m) FROM MobileNumber m WHERE m.customer = c), " +
            "  (SELECT COUNT(a) FROM Address a WHERE a.customer = c), " +
            "  (SELECT COUNT(f) FROM Customer c2 JOIN c2.familyMembers f WHERE c2 = c)" +
            ") FROM Customer c " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(c.nic) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CustomerSummaryDTO> searchByNameOrNic(@Param("keyword") String keyword);
}
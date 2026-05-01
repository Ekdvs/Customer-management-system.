package com.Customer.management.system.Customer.management.system.entiry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;



@Entity
@Table(name = "mobile_number")
public class MobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String number;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public MobileNumber() {}

    public MobileNumber(String number, Customer customer) {
        this.number = number;
        this.customer = customer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
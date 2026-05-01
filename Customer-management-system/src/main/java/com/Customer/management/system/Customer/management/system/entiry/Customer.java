package com.Customer.management.system.Customer.management.system.entiry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull(message = "Date of birth is mandatory")
    @Column(nullable = false)
    private LocalDate dob;

    @NotBlank(message = "NIC number is mandatory")
    @Column(nullable = false, unique = true, length = 50)
    private String nic;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MobileNumber> mobileNumbers = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    /**
     * Self-referencing many-to-many for family members.
     * We only map one direction to avoid circular issues;
     * both sides are managed by the application layer.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "customer_family",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "family_customer_id")
    )
    @JsonIgnore  // Prevent circular serialization – exposed through DTO
    private List<Customer> familyMembers = new ArrayList<>();

    public Customer() {}

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public List<MobileNumber> getMobileNumbers() { return mobileNumbers; }
    public void setMobileNumbers(List<MobileNumber> mobileNumbers) { this.mobileNumbers = mobileNumbers; }

    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

    public List<Customer> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(List<Customer> familyMembers) { this.familyMembers = familyMembers; }
}
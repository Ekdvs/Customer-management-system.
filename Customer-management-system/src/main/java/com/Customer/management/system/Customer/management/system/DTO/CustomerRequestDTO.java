package com.Customer.management.system.Customer.management.system.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerRequestDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Date of birth is mandatory")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotBlank(message = "NIC number is mandatory")
    private String nic;

    private List<String> mobileNumbers = new ArrayList<>();

    private List<AddressDTO> addresses = new ArrayList<>();

    // IDs of existing customers to link as family members
    private List<Long> familyMemberIds = new ArrayList<>();

    public CustomerRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public List<String> getMobileNumbers() { return mobileNumbers; }
    public void setMobileNumbers(List<String> mobileNumbers) { this.mobileNumbers = mobileNumbers; }

    public List<AddressDTO> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDTO> addresses) { this.addresses = addresses; }

    public List<Long> getFamilyMemberIds() { return familyMemberIds; }
    public void setFamilyMemberIds(List<Long> familyMemberIds) { this.familyMemberIds = familyMemberIds; }
}
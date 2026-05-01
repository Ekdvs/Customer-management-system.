package com.Customer.management.system.Customer.management.system.DTO;



import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CustomerResponseDTO {

    private Long id;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String nic;
    private List<String> mobileNumbers = new ArrayList<>();
    private List<AddressDTO> addresses = new ArrayList<>();
    private List<FamilyMemberDTO> familyMembers = new ArrayList<>();

    public CustomerResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<FamilyMemberDTO> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(List<FamilyMemberDTO> familyMembers) { this.familyMembers = familyMembers; }
}
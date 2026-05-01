package com.Customer.management.system.Customer.management.system.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;


public class CustomerSummaryDTO {

    private Long id;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String nic;
    private long mobileCount;
    private long addressCount;
    private long familyCount;

    public CustomerSummaryDTO() {}

    public CustomerSummaryDTO(Long id, String name, LocalDate dob, String nic,
                              long mobileCount, long addressCount, long familyCount) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.nic = nic;
        this.mobileCount = mobileCount;
        this.addressCount = addressCount;
        this.familyCount = familyCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public long getMobileCount() { return mobileCount; }
    public void setMobileCount(long mobileCount) { this.mobileCount = mobileCount; }

    public long getAddressCount() { return addressCount; }
    public void setAddressCount(long addressCount) { this.addressCount = addressCount; }

    public long getFamilyCount() { return familyCount; }
    public void setFamilyCount(long familyCount) { this.familyCount = familyCount; }
}

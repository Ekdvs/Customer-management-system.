package com.Customer.management.system.Customer.management.system.DTO;
import java.time.LocalDate;


public class FamilyMemberDTO {

    private Long id;
    private String name;
    private String nic;
    private LocalDate dob;

    public FamilyMemberDTO() {}

    public FamilyMemberDTO(Long id, String name, String nic, LocalDate dob) {
        this.id = id;
        this.name = name;
        this.nic = nic;
        this.dob = dob;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
}
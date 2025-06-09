package com.example.possupplierservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate; // Import for LocalDate

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDto {
    private int id;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthday;  // Use LocalDate here for the birthday field
    private String address;
    private String mobileNumber;
    private String telephoneNumber;
    private String email;
    private String facebook;
    private String instagram;
    private String image;
    private String motherName;
    private String fatherName;

    // Constructor for imports
    public SupplierDto(String lastName, String firstName, String middleName, LocalDate birthday,
                       String address, String mobileNumber, String telephoneNumber, String email,
                       String facebook, String instagram, String motherName, String fatherName) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthday = birthday;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
        this.facebook = facebook;
        this.instagram = instagram;
        this.motherName = motherName;
        this.fatherName = fatherName;
    }
}

package com.pcrypto.patientservice.dto;

import com.pcrypto.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRequestDTO {
    @NotBlank(message = "First Name is required")
    @Size(max = 100, message = "First Name should not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(max = 100, message = "Last Name should not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "DOB is required")
    private String dateOfBirth;

    @NotBlank(groups = CreatePatientValidationGroup.class, message = "Registered date is required")
    private String registeredDate;
}

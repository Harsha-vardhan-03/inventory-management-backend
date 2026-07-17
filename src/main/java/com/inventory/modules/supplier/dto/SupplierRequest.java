package com.inventory.modules.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequest {
    
    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 200, message = "Supplier name must be between 2 and 200 characters")
    private String supplierName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private String gstNumber;
    
    private String website;
    
    private String contactPerson;
    
    private String contactPersonPhone;
}
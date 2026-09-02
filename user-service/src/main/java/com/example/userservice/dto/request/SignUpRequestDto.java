package com.example.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequestDto (
        @NotBlank(message = "name cannot be blank")
        String fullName,

        @NotBlank(message = "phone number cannot be blank")
        @Pattern(
                regexp = "^\\+994(50|51|55|70|77|99|10|60)\\d{7}$",
                message = "Invalid Azerbaijani phone number"
        )
        String phoneNumber,

        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^.{8,}$",
                message = "Sifre en azi 8 simvol olmalidir"
        )
        String password
){
}

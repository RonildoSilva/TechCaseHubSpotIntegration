package com.codeone.hubspot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;
}

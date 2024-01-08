package com.xpresPayment.Airtimeservice.dto.request;

import com.xpresPayment.Airtimeservice.enums.RegistrationStatus;
import com.xpresPayment.Airtimeservice.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
public class RegRequest {
    @NotEmpty(message = "full_name must not be empty")
    private String full_name;
    @NotEmpty(message = "email must not be empty")
    private String email;
    @NotEmpty(message = "phone_number must not be empty")
    @Pattern(regexp = "\\d+", message = "phone_number must be a digit")
    private String phone_number;
    @NotEmpty(message = "password must not be empty")
    @Size(min = 8, message = "password must be atleast 8 digits")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[-+_!@#$%^&*., ?]).+$",
            message = "password mmust contain atleast one uppercase, one lowercase and  ")
    private String password;
    @NotEmpty(message = "password must not be empty")
    private String confirm_password;
}

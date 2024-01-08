package com.xpresPayment.Airtimeservice.dto.response;

import com.xpresPayment.Airtimeservice.enums.RegistrationStatus;
import com.xpresPayment.Airtimeservice.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long id;
    private String full_name;
    private String email;
    private String phone_number;
    private String role;
    private String reg_status;
}

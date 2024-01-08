package com.xpresPayment.Airtimeservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotEmpty(message = "new password must not be empty")
    private String newPassword;
    @NotEmpty(message = "confirm password must not be empty")
    private String confirmPassword;
    @NotEmpty(message = "identifier must not be empty")
    private String identifier;
    @NotEmpty(message = "action id must not be empty")
    private String actionId;
}

package com.xpresPayment.Airtimeservice.security;

import com.xpresPayment.Airtimeservice.repository.JwtTokenRepository;
import com.xpresPayment.Airtimeservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private  final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;

    //Invalids user's token upon logout
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        var vendor = userRepository.findByEmail(userEmail)
                .orElse(null);
        var storedToken = jwtTokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken == null && vendor == null) {
            return;
        }

        assert storedToken != null;
        assert vendor != null;
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        jwtTokenRepository.save(storedToken);
        vendor.setOnline(false);
        userRepository.save(vendor);
    }
}

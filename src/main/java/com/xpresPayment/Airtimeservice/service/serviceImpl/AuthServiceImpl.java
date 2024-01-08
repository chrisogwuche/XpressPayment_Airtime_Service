package com.xpresPayment.Airtimeservice.service.serviceImpl;

import com.xpresPayment.Airtimeservice.dto.request.LoginRequest;
import com.xpresPayment.Airtimeservice.dto.request.RegRequest;
import com.xpresPayment.Airtimeservice.dto.response.ErrorResponseDto;
import com.xpresPayment.Airtimeservice.dto.response.RegisterResponse;
import com.xpresPayment.Airtimeservice.dto.response.TokenResponse;
import com.xpresPayment.Airtimeservice.enums.RegistrationStatus;
import com.xpresPayment.Airtimeservice.enums.Role;
import com.xpresPayment.Airtimeservice.exceptions.NotFoundException;
import com.xpresPayment.Airtimeservice.model.JwtToken;
import com.xpresPayment.Airtimeservice.model.User;
import com.xpresPayment.Airtimeservice.repository.JwtTokenRepository;
import com.xpresPayment.Airtimeservice.repository.UserRepository;
import com.xpresPayment.Airtimeservice.security.JwtService;
import com.xpresPayment.Airtimeservice.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;
    private final AuthenticationManager authenticationManager;


    @Transactional
    @Override
    public ResponseEntity<RegisterResponse> register(RegRequest request) {
        boolean isPasswordEqual = isEqual(request.getPassword(), request.getConfirm_password());
        if(isPasswordEqual){
            return new ResponseEntity<>(getRegisterResponse(request),HttpStatus.CREATED);
        }else {
            throw new BadCredentialsException("Password and Confirm Password do not match");
        }
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new NotFoundException("User not found with email: "+request.getEmail()));

        if(user.isVerified()){
            return new ResponseEntity<>(authenticateLogin(request,user),HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(ErrorResponseDto.builder()
                    .status("FAILED")
                    .message("Account not verified!")
                    .build(), HttpStatus.UNAUTHORIZED);
        }
    }

    //Handles user validation upon login and generate jwt token if validated
    public TokenResponse authenticateLogin(LoginRequest request,User user){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        user.setOnline(true);
        userRepository.save(user);
        return getToken(user);
    }

    //Handles user registration
    public RegisterResponse getRegisterResponse(RegRequest request) {
        boolean isExit = userRepository.existsByEmail(request.getEmail());
        RegisterResponse registerResponse = new RegisterResponse();

        if(!isExit){
            User user = mapToUser(request);
            User savedUser = userRepository.save(user);
            TokenResponse data = getToken(savedUser);

            registerResponse.setStatus("SUCCESS");
            registerResponse.setMessage("User Created");
            registerResponse.setData(data);
            return registerResponse;
        }
        else{
            throw new AccessDeniedException("user already exist");
        }
    }

    //Creates and return Token objects
    private TokenResponse getToken(User user) {
        TokenResponse tokenData = new TokenResponse();
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Date expiration = jwtService.extractExpiration(token);
        Date issuedAt = jwtService.extractIssuedAt(token);
        saveToken(token, refreshToken, user,expiration,issuedAt); //token saved in the database
        tokenData.setToken(token);
        tokenData.setIssuedAt(issuedAt);
        tokenData.setExpiresAt(expiration);
        tokenData.setRefreshToken(refreshToken);
        return tokenData;
    }

    //token saved in the database
    private void saveToken(String token, String refreshToken, User user,Date expiredAt,Date issuedAt) {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setRefreshToken(refreshToken);
        jwtToken.setExpired(false);
        jwtToken.setUser(user);
        jwtToken.setRevoked(false);
        jwtToken.setExpiresAt(expiredAt);
        jwtToken.setGeneratedAt(issuedAt);
        jwtTokenRepository.save(jwtToken);
    }

    private User mapToUser(RegRequest request){
        log.info("mapToUser");
        User user = new User();
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setVerified(true);
        user.setRegStatus(RegistrationStatus.COMPLETED);
        return user;
    }

    //Checks if password and confirm password are thesame and returns a boolean
    private boolean isEqual(String password, String confirmPassword){
        return Objects.equals(password,confirmPassword);
    }
}

package com.xpresPayment.Airtimeservice.security;

import com.xpresPayment.Airtimeservice.model.User;
import com.xpresPayment.Airtimeservice.repository.JwtTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtTokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7); // extract the jwt token from the "Authorization" header
        userEmail = jwtService.extractUsername(jwt); // get the email associated with that token

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { //Check if the SecurityContextHolder already has a user in it or not
            User user  = (User) this.userDetailsService.loadUserByUsername(userEmail); //Get user with email from database
            if(!user.isEnabled()){
                throw new AccessDeniedException("Account not verified!"); //throw an exception if user has not been verified
            }
            UserDetailsDto userDetailsDto = mapToUserDetailsDto(user);
            log.info("user details: " +userDetailsDto.getUsername());

            boolean isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(jwt, userDetailsDto.getUsername()) && isTokenValid) { //validate the token gotten from the "Authorization" header
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetailsDto, null,userDetailsDto.getGrantedAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);  //Set the user in the SecurityContextHolder
            }
        }
        filterChain.doFilter(request, response);
    }

    //This is to map relevant user info to userDetails in other to avoid errors like StackOverFlow exceptions
    private UserDetailsDto mapToUserDetailsDto(User user){
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setEnabled(user.isEnabled());
        userDetailsDto.setAccountNonExpired(user.isAccountNonExpired());
        userDetailsDto.setUsername(user.getUsername());
        userDetailsDto.setCredentialsNonExpired(user.isCredentialsNonExpired());
        userDetailsDto.setAccountNonLocked(user.isAccountNonLocked());
        userDetailsDto.setGrantedAuthorities(user.getAuthorities());
        userDetailsDto.setFullName(user.getFullName());
        userDetailsDto.setPhoneNumber(user.getPhoneNumber());

        return userDetailsDto;
    }
}

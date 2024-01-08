package com.xpresPayment.Airtimeservice.repository;


import com.xpresPayment.Airtimeservice.model.JwtToken;
import com.xpresPayment.Airtimeservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByToken(String token);
    List<JwtToken> findTokenByUserAndExpiredIsFalseAndRevokedIsFalse(User user);
}

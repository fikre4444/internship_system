package com.system.internship.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.system.internship.domain.Account;
import com.system.internship.repository.AccountRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

  @Autowired
  private AccountRepository accountRepository;

  private String secretKey = "T+vwWyP3VuV7nFWQtup1eWkNWm9863s2d/Atx20gG7o=";

  public JwtService() {
    // this creates a dynamically generated secret key which then changes during
    // restart of server.
    // try {
    // KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
    // SecretKey sk = keyGen.generateKey();
    // secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
    // } catch (NoSuchAlgorithmException e) {
    // e.printStackTrace();
    // }
  }

  public String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();

    //this is to add the roles of the person
    Optional<Account> account = accountRepository.findByUsername(username);
    if (account.isPresent()) {
      claims.put("roles", account.get().getAuthorities().stream()
          .map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList()));
    }

    return Jwts.builder()
        .claims()
        .add(claims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
        .and()
        .signWith(getKey())
        .compact();
  }

  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

}

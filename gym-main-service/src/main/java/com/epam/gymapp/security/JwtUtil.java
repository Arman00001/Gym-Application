package com.epam.gymapp.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility component for generating and validating JWT tokens.
 *
 * <p>
 * This component creates access and refresh tokens using the configured JWT secret.
 * Access tokens include the username, expiration date, issue date, and user authorities.
 * Refresh tokens include the username, expiration date, and issue date.
 * </p>
 *
 * <p>
 * The utility also provides methods for extracting token data after verification,
 * such as username, authorities, and expiration date.
 * </p>
 */
@Component
public class JwtUtil {

    /**
     * Access token validity duration in milliseconds.
     */
    public static final long JWT_ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60;

    /**
     * Refresh token validity duration in milliseconds.
     */
    public static final long JWT_REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30;

    /**
     * Authorization header authentication type prefix.
     */
    public static final String AUTH_TYPE = "Bearer ";
    private static final String ROLES = "roles";

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Generates a JWT access token for the given user.
     *
     * <p>
     * The access token contains the username as the subject and includes the user's
     * authorities in the {@code roles} claim.
     * </p>
     *
     * @param userDetails the authenticated user's details
     * @return a signed JWT access token
     */
    public String generateAccessToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_ACCESS_TOKEN_VALIDITY))
                .withIssuedAt(Instant.now())
                .withClaim(ROLES, userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(this.getAlgorithm());
    }

    /**
     * Generates a JWT refresh token for the given user.
     *
     * <p>
     * The refresh token contains the username as the subject but does not include
     * user authorities.
     * </p>
     *
     * @param userDetails the authenticated user's details
     * @return a signed JWT refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY))
                .withIssuedAt(Instant.now())
                .sign(this.getAlgorithm());
    }

    /**
     * Extracts the username from a verified JWT token.
     *
     * @param token the JWT token to verify and decode
     * @return the username stored as the token subject
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the token is invalid or expired
     */
    public String getUsername(String token) {
        return this.verifyAndDecode(token).getSubject();
    }

    /**
     * Extracts authorities from a verified JWT access token.
     *
     * @param token the JWT token to verify and decode
     * @return the authorities stored in the {@code roles} claim
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the token is invalid or expired
     */
    public String[] getAuthorities(String token) {
        return this.verifyAndDecode(token).getClaim(ROLES).asArray(String.class);
    }

    /**
     * Extracts the expiration date from a verified JWT token.
     *
     * @param token the JWT token to verify and decode
     * @return the token expiration date
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the token is invalid or expired
     */
    public Date getExpirationDate(String token) {
        return this.verifyAndDecode(token).getExpiresAt();
    }

    /**
     * Checks whether the given JWT token is valid.
     *
     * @param token the JWT token to verify
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    public boolean isVerified(String token) {
        try {
            this.verifyAndDecode(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret.getBytes(UTF_8));
    }

    private DecodedJWT verifyAndDecode(String token) {
        return JWT.require(this.getAlgorithm()).build().verify(token);
    }

}

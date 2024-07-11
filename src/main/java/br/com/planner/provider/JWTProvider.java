package br.com.planner.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTProvider {

    @Value("${owner.security.token}")
    private String secretKey;

    public DecodedJWT validateOwnerToken(String token) {
        token = token.replace("Bearer ", "");

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            DecodedJWT tokenValidated = JWT.require(algorithm).build().verify(token);
            return tokenValidated;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

}

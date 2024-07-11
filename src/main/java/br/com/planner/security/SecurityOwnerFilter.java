package br.com.planner.security;

import br.com.planner.exceptions.TokenInvalidException;
import br.com.planner.provider.JWTProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityOwnerFilter extends OncePerRequestFilter {

    private JWTProvider provider;

    public SecurityOwnerFilter(JWTProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        System.out.println(token);

        if (token != null) {
            DecodedJWT decodedJWT = provider.validateOwnerToken(token);

            if (decodedJWT == null) {
                throw new TokenInvalidException("Token invalid.");
            }

            request.setAttribute("owner_id", decodedJWT.getSubject());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(decodedJWT.getSubject(), null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        filterChain.doFilter(request, response);
    }
}

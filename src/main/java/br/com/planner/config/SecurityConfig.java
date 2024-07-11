package br.com.planner.config;

import br.com.planner.security.SecurityOwnerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private SecurityOwnerFilter securityOwnerFilter;

    public SecurityConfig(SecurityOwnerFilter securityOwnerFilter) {
        this.securityOwnerFilter = securityOwnerFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests(http -> {
                    http.requestMatchers("/owners/create").permitAll();
                    http.requestMatchers("/owners/auth").permitAll();
                    http.anyRequest().authenticated();
                })
                .addFilterBefore(securityOwnerFilter, BasicAuthenticationFilter.class);


        return httpSecurity.build();
    }

}

package org.microservice.tenantservice.security;

import org.microservice.tenantservice.repository.AdminUserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminAuthenticationProvider implements AuthenticationProvider {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthenticationProvider(AdminUserRepository adminUserRepository,
                                       PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        return adminUserRepository.findByEmail(email)
                .map(admin -> {
                    if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
                        throw new BadCredentialsException("Invalid password");
                    }
                    return new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );
                })
                .orElse(null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

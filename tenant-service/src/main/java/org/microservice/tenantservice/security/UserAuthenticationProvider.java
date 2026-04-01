package org.microservice.tenantservice.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public UserAuthenticationProvider(UserDetailsServiceImpl userDetailsService,
                                      PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // get username and password
        String email = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        // check if username exist
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(email);

        // match password
        if (!passwordEncoder.matches(rawPassword, userPrincipal.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // return token
        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
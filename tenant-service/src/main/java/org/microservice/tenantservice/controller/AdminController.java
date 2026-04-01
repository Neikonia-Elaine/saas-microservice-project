package org.microservice.tenantservice.controller;

import org.microservice.tenantservice.dto.request.LoginRequest;
import org.microservice.tenantservice.dto.response.LoginResponse;
import org.microservice.tenantservice.entity.AdminUser;
import org.microservice.tenantservice.repository.AdminUserRepository;
import org.microservice.tenantservice.service.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/admin")
public class AdminController {

    private final AuthenticationManager adminAuthManager;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminController(@Qualifier("adminAuthManager") AuthenticationManager adminAuthManager,
                           AdminUserRepository adminUserRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.adminAuthManager = adminAuthManager;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // no JWT required - skip in JwtAuthFilter
    @PostMapping
    public ResponseEntity<Void> register(@RequestBody LoginRequest request) {
        AdminUser admin = new AdminUser();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminUserRepository.save(admin);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = adminAuthManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String email = (String) authentication.getPrincipal();
        String token = jwtService.generateToken(email, null, "ROLE_ADMIN");
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

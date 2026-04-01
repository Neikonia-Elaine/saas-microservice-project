package org.microservice.tenantservice.controller;

import org.microservice.tenantservice.dto.request.LoginRequest;
import org.microservice.tenantservice.dto.response.LoginResponse;
import org.microservice.tenantservice.entity.User;
import org.microservice.tenantservice.security.UserPrincipal;
import org.microservice.tenantservice.service.JwtService;
import org.microservice.tenantservice.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public UserController(@Qualifier("userAuthManager") AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(
                principal.getUsername(),
                principal.getTenantId(),
                principal.getAuthorities().iterator().next().getAuthority()
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestParam String tenantId) {
        List<User> users = userService.getUsersByTenantId(tenantId);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
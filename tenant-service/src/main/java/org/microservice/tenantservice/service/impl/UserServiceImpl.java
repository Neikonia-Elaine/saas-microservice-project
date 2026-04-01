package org.microservice.tenantservice.service.impl;

import org.microservice.tenantservice.entity.User;
import org.microservice.tenantservice.exception.TenantNotFoundException;
import org.microservice.tenantservice.exception.UserNotFoundException;
import org.microservice.tenantservice.repository.TenantRepository;
import org.microservice.tenantservice.repository.UserRepository;
import org.microservice.tenantservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           TenantRepository tenantRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        if (!tenantRepository.existsByTenantId(user.getTenantId())) {
            throw new TenantNotFoundException(user.getTenantId());
        }
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public List<User> getUsersByTenantId(String tenantId) {
        return userRepository.findByTenantId(tenantId);
    }
}
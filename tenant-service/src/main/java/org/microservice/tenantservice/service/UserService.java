package org.microservice.tenantservice.service;

import org.microservice.tenantservice.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    void deleteUser(Long id);

    User getUserByEmail(String email);

    List<User> getUsersByTenantId(String tenantId);
}
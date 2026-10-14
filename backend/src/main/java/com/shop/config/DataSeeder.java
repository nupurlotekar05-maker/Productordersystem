package com.shop.config;

import com.shop.entity.Role;
import com.shop.entity.User;
import com.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Creates one ADMIN (id 1) and one USER (id 2) 
@Component @RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;
        users.save(create("Admin", "admin@shop.com", Role.ADMIN));
        users.save(create("Rahul", "user@shop.com", Role.USER));
    }

    private User create(String name, String email, Role role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setRole(role);
        return u;
    }
}

package com.shop.service;

import com.shop.entity.Role;
import com.shop.entity.User;
import com.shop.exception.ForbiddenException;
import com.shop.exception.ResourceNotFoundException;
import com.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;

    public User getUser(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public void requireAdmin(Long id) {
        if (getUser(id).getRole() != Role.ADMIN)
            throw new ForbiddenException("Only ADMIN can perform this action");
    }
}

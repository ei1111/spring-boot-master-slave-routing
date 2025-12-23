package com.example.db.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(String name) {
        log.info("Creating user: {}", name);
        User user = new User();
        user.setName(name);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Getting all users (should use SLAVE)");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        log.info("Getting user: {} (should use SLAVE)", id);
        return userRepository.findById(id).orElse(null);
    }
}

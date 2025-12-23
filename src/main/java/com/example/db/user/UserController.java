package com.example.db.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@RequestParam String name) {
        return userService.createUser(name);
    }

    @GetMapping
    public List<User> list() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
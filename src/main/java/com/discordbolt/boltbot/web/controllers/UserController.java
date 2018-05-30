package com.discordbolt.boltbot.web.controllers;

import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import com.discordbolt.boltbot.web.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "")
    public @ResponseBody
    Iterable<UserData> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/{user.id}")
    public @ResponseBody
    UserData getUserById(@PathVariable(name = "user.id") long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}

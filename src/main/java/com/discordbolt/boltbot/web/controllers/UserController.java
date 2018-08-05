package com.discordbolt.boltbot.web.controllers;

import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import com.discordbolt.boltbot.web.exceptions.EntityNotFoundException;
import com.discordbolt.boltbot.web.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "")
    public ResponseEntity<UserModel> getAllUsers() {
        return new ResponseEntity<>(new UserModel(userRepository.findAll()), HttpStatus.OK);
    }

    @GetMapping(path = "/{user.id}")
    public ResponseEntity<UserData> getUserById(@PathVariable(name = "user.id") long id) {
        return new ResponseEntity<>(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with ID " + id)), HttpStatus.OK);
    }
}

package com.discordbolt.boltbot.web;

import com.discordbolt.boltbot.data.objects.UserData;
import com.discordbolt.boltbot.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<UserData> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/search")
    public @ResponseBody
    Optional<UserData> getUserById(@RequestParam long id) {
        return userRepository.findById(id);
    }
}

package com.discordbolt.boltbot.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class IndexController {

    @GetMapping(path = "")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}

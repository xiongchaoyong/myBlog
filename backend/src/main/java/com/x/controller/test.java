package com.x.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@CrossOrigin
@RestController
public class test {
    @GetMapping("/test")
    public String test(){

        return "Welcome to X's Website";
    }

}

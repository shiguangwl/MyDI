package com.xxhoz.timeho.service;

import com.xxhoz.spring.annotation.Autowired;
import com.xxhoz.spring.annotation.Component;

@Component("dog")
public class Dog {

    @Autowired
    Student student;
}

package com.xxhoz.timeho.service;

import com.xxhoz.spring.annotation.Autowired;
import com.xxhoz.spring.annotation.Component;

@Component("teacher")
public class Teacher {
    private String name = "我是老师";
    @Autowired
    Dog dog;

    //@Override
    //public String toString() {
    //    return "Teacher{" +
    //            "name='" + name + '\'' +
    //            '}';
    //}
}

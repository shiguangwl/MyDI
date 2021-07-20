package com.xxhoz.timeho.service;


import com.xxhoz.spring.annotation.Autowired;
import com.xxhoz.spring.annotation.Component;
import com.xxhoz.spring.annotation.Scope;


@Component("student")
@Scope("prototype")
public class Student {

    private String name = "我是学生";

    @Autowired
    Teacher teacher;


    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", teacher=" + teacher +
                '}';
    }
}

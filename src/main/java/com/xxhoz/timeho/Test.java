package com.xxhoz.timeho;

import com.xxhoz.spring.TimeHoaplication;
import com.xxhoz.timeho.service.Student;
import com.xxhoz.timeho.service.Teacher;

public class Test {
    public static void main(String[] args) {


        TimeHoaplication timeHoaplication = new TimeHoaplication(AppConfig.class);
        Student student = (Student) timeHoaplication.getBean("Student");
        System.out.println("");
    }
}

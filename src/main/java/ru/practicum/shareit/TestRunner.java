package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Deprecated
@SpringBootApplication
public class TestRunner {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }
}
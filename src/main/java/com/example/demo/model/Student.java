package com.example.demo.model;

import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("student")
@Builder
public class Student {
    public enum Gender {
        MALE, FEMALE
    }

    private String id;
    private String name;
    private Gender gender;
    private int grade;
}

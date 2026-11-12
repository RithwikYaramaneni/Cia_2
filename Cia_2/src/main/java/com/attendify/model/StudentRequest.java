package com.attendify.model;

public record StudentRequest(
        String name,
        String rollNumber,
        String course,
        int marks
) {}

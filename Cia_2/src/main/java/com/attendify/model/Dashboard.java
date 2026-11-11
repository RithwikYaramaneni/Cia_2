package com.attendify.model;

import java.util.List;

public record Dashboard(
        int totalStudents,
        double averageAttendance,
        int averageMarks,
        long atRiskCount,
        List<Student> students
) {}

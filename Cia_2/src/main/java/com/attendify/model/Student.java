package com.attendify.model;

public record Student(
        long id,
        String name,
        String rollNumber,
        String course,
        int present,
        int totalClasses,
        int marks
) {
    public double attendancePercentage() {
        return totalClasses == 0 ? 0 : (present * 100.0) / totalClasses;
    }

    public String grade() {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }

    public Student withAttendance(boolean isPresent) {
        return new Student(id, name, rollNumber, course,
                present + (isPresent ? 1 : 0), totalClasses + 1, marks);
    }
}

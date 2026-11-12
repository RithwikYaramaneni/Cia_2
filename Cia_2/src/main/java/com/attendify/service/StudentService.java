package com.attendify.service;

import com.attendify.model.Dashboard;
import com.attendify.model.Student;
import com.attendify.model.StudentRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StudentService {
    private final List<Student> students = new ArrayList<>(List.of(
            new Student(1, "Aarav Sharma", "CS-2024-001", "Computer Science", 18, 20, 92),
            new Student(2, "Diya Patel", "CS-2024-002", "Computer Science", 16, 20, 84),
            new Student(3, "Kabir Singh", "CS-2024-003", "Computer Science", 13, 20, 68),
            new Student(4, "Meera Nair", "CS-2024-004", "Computer Science", 19, 20, 96),
            new Student(5, "Rohan Verma", "CS-2024-005", "Computer Science", 11, 20, 48)
    ));

    public synchronized Dashboard dashboard() {
        double attendance = students.stream().mapToDouble(Student::attendancePercentage).average().orElse(0);
        int marks = (int) Math.round(students.stream().mapToInt(Student::marks).average().orElse(0));
        long atRisk = students.stream().filter(student -> student.attendancePercentage() < 75 || student.marks() < 50).count();
        return new Dashboard(students.size(), attendance, marks, atRisk, List.copyOf(students));
    }

    public synchronized List<Student> findAll() {
        return List.copyOf(students);
    }

    public synchronized Student findById(long id) {
        return students.stream().filter(student -> student.id() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Student " + id + " was not found"));
    }

    public synchronized Student recordAttendance(long id, boolean present) {
        Student updated = findById(id).withAttendance(present);
        students.replaceAll(student -> student.id() == id ? updated : student);
        return updated;
    }

    public synchronized Student addStudent(StudentRequest request) {
        String name = required(request.name(), "Name");
        String rollNumber = required(request.rollNumber(), "Roll number");
        String course = required(request.course(), "Course");
        if (request.marks() < 0 || request.marks() > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }
        if (students.stream().anyMatch(student -> student.rollNumber().equalsIgnoreCase(rollNumber))) {
            throw new IllegalArgumentException("Roll number already exists");
        }

        long id = students.stream().mapToLong(Student::id).max().orElse(0) + 1;
        Student student = new Student(id, name, rollNumber, course, 0, 0, request.marks());
        students.add(student);
        return student;
    }

    public synchronized void removeStudent(long id) {
        if (students.removeIf(student -> student.id() == id)) {
            return;
        }
        throw new NoSuchElementException("Student " + id + " was not found");
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}

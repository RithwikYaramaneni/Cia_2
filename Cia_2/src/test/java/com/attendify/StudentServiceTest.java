package com.attendify;

import com.attendify.model.Student;
import com.attendify.model.StudentRequest;
import com.attendify.service.StudentService;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    @Test
    void calculatesAttendancePercentage() {
        assertEquals(75.0, student(15, 20, 84).attendancePercentage());
    }

    @Test
    void returnsZeroAttendanceWhenNoClassesExist() {
        assertEquals(0.0, student(0, 0, 84).attendancePercentage());
    }

    @Test
    void calculatesAPlusGrade() {
        assertEquals("A+", student(1, 1, 90).grade());
    }

    @Test
    void calculatesAGrade() {
        assertEquals("A", student(1, 1, 80).grade());
    }

    @Test
    void calculatesBGrade() {
        assertEquals("B", student(1, 1, 70).grade());
    }

    @Test
    void calculatesCGrade() {
        assertEquals("C", student(1, 1, 60).grade());
    }

    @Test
    void calculatesDGrade() {
        assertEquals("D", student(1, 1, 50).grade());
    }

    @Test
    void calculatesFGrade() {
        assertEquals("F", student(1, 1, 49).grade());
    }

    @Test
    void recordsPresentAttendance() {
        Student updated = new StudentService().recordAttendance(1, true);
        assertEquals(19, updated.present());
        assertEquals(21, updated.totalClasses());
    }

    @Test
    void recordsAbsentAttendance() {
        Student updated = new StudentService().recordAttendance(1, false);
        assertEquals(18, updated.present());
        assertEquals(21, updated.totalClasses());
    }

    @Test
    void persistsAttendanceChanges() {
        StudentService service = new StudentService();
        service.recordAttendance(1, true);
        Student updated = service.findById(1);
        assertEquals(19, updated.present());
        assertEquals(21, updated.totalClasses());
    }

    @Test
    void findsAllSeededStudents() {
        assertEquals(5, new StudentService().findAll().size());
    }

    @Test
    void findsStudentById() {
        Student student = new StudentService().findById(3);
        assertEquals("Kabir Singh", student.name());
        assertEquals("CS-2024-003", student.rollNumber());
    }

    @Test
    void rejectsUnknownStudentId() {
        assertThrows(NoSuchElementException.class, () -> new StudentService().findById(999));
    }

    @Test
    void calculatesDashboardSummaryAndAtRiskStudents() {
        var dashboard = new StudentService().dashboard();
        assertAll(
                () -> assertEquals(5, dashboard.totalStudents()),
                () -> assertEquals(77.0, dashboard.averageAttendance()),
                () -> assertEquals(78, dashboard.averageMarks()),
                () -> assertEquals(2, dashboard.atRiskCount()),
                () -> assertEquals(5, dashboard.students().size())
        );
    }

    @Test
    void addsStudentWithNextId() {
        StudentService service = new StudentService();
        Student added = service.addStudent(new StudentRequest("Nisha Rao", "CS-2024-006", "Computer Science", 88));

        assertEquals(6, added.id());
        assertEquals(0, added.totalClasses());
        assertEquals(6, service.findAll().size());
    }

    @Test
    void rejectsDuplicateRollNumber() {
        StudentService service = new StudentService();

        assertThrows(IllegalArgumentException.class,
                () -> service.addStudent(new StudentRequest("New Student", "cs-2024-001", "Computer Science", 70)));
    }

    @Test
    void removesStudent() {
        StudentService service = new StudentService();
        service.removeStudent(2);

        assertEquals(4, service.findAll().size());
        assertThrows(NoSuchElementException.class, () -> service.findById(2));
    }

    private static Student student(int present, int totalClasses, int marks) {
        return new Student(1, "Test Student", "T-1", "Computer Science",
                present, totalClasses, marks);
    }
}

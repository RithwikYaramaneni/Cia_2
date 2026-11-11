package com.attendify.controller;

import com.attendify.model.Dashboard;
import com.attendify.model.Student;
import com.attendify.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public Dashboard dashboard() {
        return service.dashboard();
    }

    @GetMapping("/students")
    public List<Student> students() {
        return service.findAll();
    }

    @PostMapping("/students/{id}/attendance")
    public Student recordAttendance(@PathVariable long id, @RequestBody Map<String, Boolean> body) {
        Boolean present = body.get("present");
        if (present == null) {
            throw new IllegalArgumentException("The present field is required");
        }
        return service.recordAttendance(id, present);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> notFound(NoSuchElementException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
}

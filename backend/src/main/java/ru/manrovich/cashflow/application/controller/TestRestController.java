package ru.manrovich.cashflow.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:5173")
public class TestRestController {
    private final AtomicReference<String> value = new AtomicReference<>();

    @PostMapping("/value")
    public ResponseEntity<String> setValue(@RequestBody String body) {
        value.set(body);
        return new ResponseEntity<>("{\"data\":\"Hello, world\"}", HttpStatus.ACCEPTED);
    }

    @GetMapping("/value")
    public ResponseEntity<String> getValue() {
        return new ResponseEntity<>(value.get(), HttpStatus.CREATED);
    }
}

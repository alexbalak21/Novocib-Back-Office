package com.novocib.backoffice.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.novocib.timetracking.domain.TimeEntry;
import com.novocib.timetracking.dto.CreateTimeEntryInput;
import com.novocib.timetracking.service.TimeEntryService;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody CreateTimeEntryInput input) {
        return ResponseEntity.ok(timeEntryService.create(input));
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> findAll() {
        return ResponseEntity.ok(timeEntryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntry> findById(@PathVariable Long id) {
        return timeEntryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable Long id, @RequestBody CreateTimeEntryInput input) {
        return timeEntryService.update(id, input)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return timeEntryService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
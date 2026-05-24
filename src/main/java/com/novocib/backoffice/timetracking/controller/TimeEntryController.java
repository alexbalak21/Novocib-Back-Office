package com.novocib.backoffice.timetracking.controller;

import com.novocib.backoffice.timetracking.domain.TimeEntry;
import com.novocib.backoffice.timetracking.service.TimeEntryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/timetracking")
public class TimeEntryController {
    private final TimeEntryService service;

    public TimeEntryController(TimeEntryService service) {
        this.service = service;
    }

    @GetMapping("/entries")
    public List<TimeEntry> getAll() {
        return service.getAll();
    }
}
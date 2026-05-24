package com.novocib.backoffice.timetracking.service;

import com.novocib.backoffice.timetracking.domain.TimeEntry;
import com.novocib.backoffice.timetracking.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeEntryService {
    private final TimeEntryRepository repository;

    public TimeEntryService(TimeEntryRepository repository) {
        this.repository = repository;
    }

    public List<TimeEntry> getAll() {
        return repository.findAll();
    }
}
package com.novocib.timetracking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.novocib.timetracking.domain.TimeEntry;
import com.novocib.timetracking.dto.CreateTimeEntryInput;
import com.novocib.timetracking.mapper.TimeEntryMapper;
import com.novocib.timetracking.repository.TimeEntryRepository;

@Service
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TimeEntryMapper timeEntryMapper;

    public TimeEntryService(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.timeEntryMapper = new TimeEntryMapper();
    }

    public TimeEntry create(CreateTimeEntryInput input) {
        TimeEntry timeEntry = timeEntryMapper.toEntity(input);
        return timeEntryRepository.save(timeEntry);
    }

    public List<TimeEntry> findAll() {
        return timeEntryRepository.findAll();
    }

    public Optional<TimeEntry> findById(Long id) {
        return timeEntryRepository.findById(id);
    }

    public Optional<TimeEntry> update(Long id, CreateTimeEntryInput input) {
        return timeEntryRepository.findById(id)
                .map(existing -> {
                    timeEntryMapper.updateEntity(existing, input);
                    return timeEntryRepository.save(existing);
                });
    }

    public boolean delete(Long id) {
        if (!timeEntryRepository.existsById(id)) {
            return false;
        }

        timeEntryRepository.deleteById(id);
        return true;
    }
}

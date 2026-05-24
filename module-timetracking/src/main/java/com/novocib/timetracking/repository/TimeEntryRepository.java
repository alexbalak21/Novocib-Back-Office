package com.novocib.timetracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.novocib.timetracking.domain.TimeEntry;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
}

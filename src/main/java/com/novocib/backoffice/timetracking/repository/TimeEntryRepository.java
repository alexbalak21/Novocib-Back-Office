package com.novocib.backoffice.timetracking.repository;

import com.novocib.backoffice.timetracking.domain.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {}

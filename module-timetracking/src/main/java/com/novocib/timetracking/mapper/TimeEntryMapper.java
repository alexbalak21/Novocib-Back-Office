package com.novocib.timetracking.mapper;

import com.novocib.timetracking.domain.TimeEntry;
import com.novocib.timetracking.dto.CreateTimeEntryInput;

public class TimeEntryMapper {

    public TimeEntry toEntity(CreateTimeEntryInput input) {
        return new TimeEntry(input.getTitle());
    }

    public void updateEntity(TimeEntry timeEntry, CreateTimeEntryInput input) {
        timeEntry.setTitle(input.getTitle());
    }
}

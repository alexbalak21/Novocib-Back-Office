package com.novocib.backoffice.timetracking.graphql;

import com.novocib.backoffice.timetracking.domain.TimeEntry;
import com.novocib.backoffice.timetracking.service.TimeEntryService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TimeEntryGraphQL {
    private final TimeEntryService service;

    public TimeEntryGraphQL(TimeEntryService service) {
        this.service = service;
    }

    @QueryMapping
    public List<TimeEntry> timeEntries() {
        return service.getAll();
    }
}

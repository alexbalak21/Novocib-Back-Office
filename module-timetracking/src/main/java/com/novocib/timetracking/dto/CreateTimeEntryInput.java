package com.novocib.timetracking.dto;

public class CreateTimeEntryInput {

    private String title;

    public CreateTimeEntryInput() {
    }

    public CreateTimeEntryInput(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

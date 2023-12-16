package com.funnyproject.todolistprojectapi.dto;

import java.time.LocalDateTime;

public class ProjectDto {

    private long id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private int creator;

    public ProjectDto() {
    }

    public ProjectDto(long id, String name, String description, LocalDateTime creationDate, int creator) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.creator = creator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }
}

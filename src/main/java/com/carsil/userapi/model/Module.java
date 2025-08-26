package com.carsil.userapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float remainingTime = 0f;

    public Module() {}

    public Module(String name) {
        this.name = name;
    }

    public Module(String description, String name, float remainingTime) {
        this.description = description;
        this.name = name;
        this.remainingTime = remainingTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getRemainingTime() { return remainingTime; }
    public void setRemainingTime(float remainingTime) { this.remainingTime = remainingTime; }
}
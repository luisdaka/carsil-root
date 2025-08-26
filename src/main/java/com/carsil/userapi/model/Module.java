package com.carsil.userapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
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

}
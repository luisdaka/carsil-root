package com.carsil.userapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "carsil_modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 255, message = "The description cannot exceed 255 characters.")
    private String description;

    @NotNull(message = "Module name cannot be null.")
    @Size(min = 2, max = 100, message = "The name must have between 2 and 100 characters.")
    @Column(nullable = false)
    private String name;


    @NotNull(message = "Remaining time cannot be null.")
    @Min(value = 0, message = "loadDays cannot be a negative value.")
    @Column(nullable = false)
    private float loadDays = 0f;

    @Column
    private Integer numPersons;
}
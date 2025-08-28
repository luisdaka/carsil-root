package com.carsil.userapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carsil_modules")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String description;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float remainingTime = 0f;
}

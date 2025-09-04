package com.carsil.userapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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


    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String description;


    @NotNull(message = "El nombre del módulo no puede ser nulo.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    @Column(nullable = false)
    private String name;


    @NotNull(message = "El tiempo restante no puede ser nulo.")
    @Min(value = 0, message = "El tiempo restante no puede ser un valor negativo.")
    @Column(nullable = false)
    private float remainingTime = 0f;
}
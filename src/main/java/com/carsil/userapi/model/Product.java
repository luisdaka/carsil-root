package com.carsil.userapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "carsil_product",
        uniqueConstraints = @UniqueConstraint(name = "uk_carsil_product_op", columnNames = "op")
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(nullable = false)
    private double price;

    @NotNull
    @Column(nullable = false)
    private int quantity;

    @NotNull
    @Column(nullable = false)
    private LocalDate assignedDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate plantEntryDate;

    @NotNull
    @Pattern(regexp = "^[0-9]*$", message = "La referencia debe contener solo números.")
    @Column(nullable = false)
    private String reference;

    @NotNull
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "La marca debe contener solo letras.")
    @Column(nullable = false)
    private String brand;

    @NotNull
    @Pattern(regexp = "^[0-9]*$", message = "El campo OP debe contener solo números.")
    @Column(nullable = false)
    private String op;

    @NotNull
    @Pattern(regexp = "^[0-9]*$", message = "La campaña debe contener solo números.")
    @Column(nullable = false)
    private String campaign;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "El tipo debe contener letras y números.")
    @Column(nullable = false)
    private String type;

    @NotNull
    @Size(min = 1, max = 4, message = "La talla debe tener entre 1 y 4 caracteres.")
    @Column(nullable = false)
    private String size;

    @Column
    private String description;
}
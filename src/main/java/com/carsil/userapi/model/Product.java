package com.carsil.userapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @Column(nullable = false)
    private String reference;

    @NotNull
    @Column(nullable = false)
    private String brand;

    @NotNull
    @Column(nullable = false)
    private String op;

    @NotNull
    @Column(nullable = false)
    private String campaign;

    @NotNull
    @Column(nullable = false)
    private String type;

    @NotNull
    @Column(nullable = false)
    private String size;

    @Column
    private String description;
}

package com.carsil.userapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
    private Double price;

    @NotNull
    @Column(nullable = false)
    private Integer quantity = 0;

    @NotNull
    @Column(nullable = false)
    private LocalDate assignedDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate plantEntryDate;

    @NotNull
    @Pattern(regexp = "^[0-9]*$", message = "The reference must contain only numbers.")
    @Column(nullable = false)
    private String reference;

    @NotNull
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "The brand must contain only letters.")
    @Column(nullable = false)
    private String brand;

    @NotNull
    @Pattern(regexp = "^[0-9]*$", message = "The OP field must contain only numbers.")
    @Column(nullable = false)
    private String op;

    @NotNull
    @Pattern(regexp = "^[0-9]*$", message = "The campaign must contain only numbers.")
    @Column(nullable = false)
    private String campaign;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The type must contain letters and numbers.")
    @Column(nullable = false)
    private String type;

    @Column
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_size_quantities", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "size")
    @Column(name = "units")
    private Map<String, Integer> sizeQuantities = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "module_id", nullable = true)
    private Module module;

    @PrePersist
    @PreUpdate
    private void validateAndSyncQuantity() {
        if (sizeQuantities == null) sizeQuantities = new HashMap<>();
        sizeQuantities.replaceAll((k, v) -> v == null ? 0 : Math.max(0, v));

        int sum = sizeQuantities.values().stream().mapToInt(Integer::intValue).sum();

        if (quantity == null || quantity == 0) {
            quantity = sum;
        } else if (!quantity.equals(sum)) {
            throw new IllegalArgumentException(
                    "The sum of sizes (" + sum + ") does not match the total (quantity=" + quantity + ")"
            );
        }
    }
}
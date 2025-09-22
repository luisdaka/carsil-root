package com.carsil.userapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
feature/error-handling
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

main
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
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    @NotBlank(message = "El nombre del módulo no puede estar vacío.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

 feature/error-handling
    @NotNull(message = "El tiempo restante no puede ser nulo.")
    @Min(value = 0, message = "El tiempo restante no puede ser negativo.")
    @Column(nullable = false)
    private Float remainingTime = 0f;

    @Column
    private Integer numPersons;

    private java.math.BigDecimal loadDays;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"module"})
    private java.util.List<Product> products = new java.util.ArrayList<>();

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("totaLoadDays")
    public java.math.BigDecimal getTotaLoadDays() {
        if (products == null || products.isEmpty()) return java.math.BigDecimal.ZERO;
 main

    public Integer getNumPersons() {
        return 0; // Placeholder por ahora
    }
}
package com.carsil.userapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "carsil_user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "password")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @NotNull(message = "El nombre no puede ser nulo.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    @Column(nullable = false)
    private String name;


    @NotNull(message = "El email no puede ser nulo.")
    @Email(message = "El email debe tener un formato válido.")
    @Column(nullable = false, unique = true)
    private String email;


    @NotNull(message = "La contraseña no puede ser nula.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    @Column(nullable = false)
    private String password;
}
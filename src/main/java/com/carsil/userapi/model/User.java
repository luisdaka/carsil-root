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

    @NotNull(message = "Name cannot be null.")
    @Size(min = 2, max = 50, message = "The name must have between 2 and 50 characters.")
    @Column(nullable = false)
    private String name;


    @NotNull(message = "Email cannot be null.")
    @Email(message = "Email must be in a valid format.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Password cannot be null.")
    @Size(min = 6, message = "The password must have at least 6 characters.")
    @Column(nullable = false)
    private String password;
}
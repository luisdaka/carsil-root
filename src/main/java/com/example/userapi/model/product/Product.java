package com.example.userapi.model.product;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "app_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private int quantity;
    private LocalDate assignedDate;
    private LocalDate plantEntryDate;
    private String reference;
    private String brand;
    private String op;
    private String campaign;
    private String type;
    private String size;
}






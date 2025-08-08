
package com.example.userapi.model;

import com.example.userapi.model.product.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "app_user")
@Data // GETTER Y SETTER
public class User extends Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;

    public void setPrice(double price) {
        
    }

    public void setQuantity(int quantity) {
        
    }

    public void setAssignedDate(LocalDate assignedDate) {
    }

    public void setPlantEntryDate(LocalDate plantEntryDate) {
    }

    public void setReference(String reference) {
    }

    public void setBrand(String brand) {
    }

    public void setOp(String op) {
    }

    public void setCampaign(String campaign) {
    }

    public void setType(String type) {
    }

    public void setSize(String size) {
    }
}

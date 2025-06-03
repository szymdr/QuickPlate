package com.quickplate.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "account_types")
public class AccountType {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

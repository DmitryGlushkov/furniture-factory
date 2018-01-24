package com.factory.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class FurnitureType {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    private String name;

    public FurnitureType() {
    }

    public FurnitureType(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

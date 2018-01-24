package com.factory.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Employee  extends OrderExecutor  {

    public Employee() {
    }

    public Employee(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private String surname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    @JsonIgnore
    public String getExecutorType() {
        return OrderExecutor.TYPE_EMPLOYEE;
    }

}

package com.factory.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Department  extends OrderExecutor  {

    @Column(nullable = false)
    @NotNull
    private String title;

    @Column
    @OneToMany
    @JsonIgnore
    private Set<Employee> employees;

    @Column
    @ManyToMany
    @JsonIgnore
    private Set<FurnitureType> furnitureTypes;

    @JsonIgnore @Transient private Set<Long> typesIdSet;
    @JsonIgnore @Transient private Set<Long> emplIdSet;

    @JsonProperty(value = "employees_id", access = JsonProperty.Access.READ_ONLY)
    public Set<Long> getEmployeeIdSet() {
        if (employees != null) {
            emplIdSet = employees.stream().mapToLong(Employee::getId).boxed().collect(Collectors.toSet());
        }
        return emplIdSet;
    }

    @JsonProperty(value = "employees_id", access = JsonProperty.Access.WRITE_ONLY)
    public void setEmployeeIdSet(Set<Long> idSet) {
        emplIdSet = idSet;
    }

    @JsonProperty(value = "types_id", access = JsonProperty.Access.READ_ONLY)
    public Set<Long> getTypesIdSet() {
        if (furnitureTypes != null) {
            typesIdSet = furnitureTypes.stream().mapToLong(FurnitureType::getId).boxed().collect(Collectors.toSet());
        }
        return typesIdSet;
    }

    @JsonProperty(value = "types_id", access = JsonProperty.Access.WRITE_ONLY)
    public void setTypesIdSet(Set<Long> idSet){
        this.typesIdSet = idSet;
    }

    public Department(){}

    public Department(String title, Set<Employee> employees, Set<FurnitureType> furnitureTypes){
        this.title = title;
        this.employees = employees;
        this.furnitureTypes = furnitureTypes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Set<FurnitureType> getFurnitureTypes() {
        return furnitureTypes;
    }

    public void setFurnitureTypes(Set<FurnitureType> furnitureTypes) {
        this.furnitureTypes = furnitureTypes;
    }

    @Override
    public String getExecutorType() {
        return OrderExecutor.TYPE_DEPARTMENT;
    }
}

package com.factory.models;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class OrderExecutor {

    public static final String TYPE_DEPARTMENT = "DEPARTMENT";
    public static final String TYPE_EMPLOYEE = "EMPLOYEE";


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract String getExecutorType();
}

package com.factory.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    private String name;

    @OneToOne
    @JsonIgnore
    private FurnitureType type;

    @OneToOne
    private OrderExecutor executor;

    @Temporal(TemporalType.DATE)
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date date;

    @JsonIgnore @Transient private ExecutorDescription executorDescription;
    @JsonIgnore @Transient private Long typeId;

    public Order() {
    }

    public Order(String name, OrderExecutor executor, FurnitureType type, Date date) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.executor = executor;
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

    public FurnitureType getType() {
        return type;
    }

    @JsonProperty(value = "type_id", access = JsonProperty.Access.READ_ONLY)
    public Long getTypeId() {
        return type != null ? type.getId() : typeId;
    }

    @JsonProperty(value = "type_id", access = JsonProperty.Access.WRITE_ONLY)
    public void setTypeId(Long id) {
        typeId = id;
    }

    @JsonProperty(value = "executor", access = JsonProperty.Access.READ_ONLY)
    public ExecutorDescription getExecutorDescription() {
        if (executorDescription == null) {
            executorDescription = new ExecutorDescription(executor.getId(), executor.getExecutorType());
        }
        return executorDescription;
    }

    @JsonProperty(value = "executor", access = JsonProperty.Access.WRITE_ONLY)
    public void setExecutorDescription(ExecutorDescription description) {
        executorDescription = description;
    }

    public void setType(FurnitureType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OrderExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(OrderExecutor executor) {
        this.executor = executor;
    }

    public static class ExecutorDescription {
        public ExecutorDescription(){}
        public ExecutorDescription(Long id, String type){
            this.id = id; this.type = type;
        }
        private Long id;
        private String type;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}

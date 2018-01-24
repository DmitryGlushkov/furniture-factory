package com.factory.service;

import com.factory.models.Department;
import com.factory.models.Employee;
import com.factory.models.FurnitureType;
import com.factory.models.Order;
import com.factory.repository.DepartmentRepository;
import com.factory.repository.EmployeeRepository;
import com.factory.repository.OrderRepository;
import com.factory.repository.TypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    TypesRepository typesRepository;

    private final Map<Class, JpaRepository> repoMap = new HashMap<>();

    @PostConstruct
    void init() {
        repoMap.put(Department.class, departmentRepository);
        repoMap.put(Employee.class, employeeRepository);
        repoMap.put(Order.class, orderRepository);
        repoMap.put(FurnitureType.class, typesRepository);
    }

    public <T> T getById(Long id, Class<T> c) {
        if (repoMap.containsKey(c)) {
            return (T) repoMap.get(c).findOne(id);
        }
        return null;
    }

    public boolean removeObject(Object object) {
        Class cls = object.getClass();
        if (repoMap.containsKey(cls)) {
            if (cls == Employee.class) {
                removeEmployee((Employee) object);
            } else if (cls == FurnitureType.class) {
                removeFurnitureType((FurnitureType) object);
            } else {
                repoMap.get(cls).delete(object);
            }
            return true;
        }
        return false;
    }

    private void removeEmployee(Employee employee) {
        departmentRepository.findAll().forEach(department -> department.getEmployees().remove(employee));
        employeeRepository.delete(employee);
    }

    private void removeFurnitureType(FurnitureType type){
        departmentRepository.findAll().forEach(department -> department.getFurnitureTypes().remove(type));
        typesRepository.delete(type);
    }

    public <T> List<T> getAll(Class<T> c) {
        if (repoMap.containsKey(c)) {
            return (List<T>) repoMap.get(c).findAll();
        }
        return null;
    }

    @Transactional
    public <T> T save(T object) {
        Class cls = object.getClass();
        if (repoMap.containsKey(cls)) {
            return (T) repoMap.get(cls).saveAndFlush(object);
        }
        return null;
    }

}

package com.factory.controllers;

import com.factory.models.*;
import com.factory.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.factory.Constants.*;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class ApiController {

    class ApiMessage {
        public String message;
        ApiMessage(String message) {
            this.message = message;
        }
    }

    @Autowired
    DataService dataService;

    private final Map<String, Class> classMap = new HashMap<>();

    @PostConstruct
    void init() {
        classMap.put(EMPLOYEES, Employee.class);
        classMap.put(ORDERS, Order.class);
        classMap.put(TYPES, FurnitureType.class);
        classMap.put(DEPARTMENTS, Department.class);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiMessage> error(HttpServletRequest req, Exception ex) {
        String errorMessage = String.format("Exception: %s", ex.getMessage() != null ? ex.getMessage() : ex.toString());
        return new ResponseEntity<>(new ApiMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    // ------------------- GET

    /*@RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ApiInfo> apiInfo(){
        return new ResponseEntity<>(new ApiInfo("info"), HttpStatus.OK);
    }*/

    @RequestMapping(value = "/{model}", method = RequestMethod.GET)
    public ResponseEntity<List<Object>> getAll(@PathVariable String model) {
        Class modelClass;
        List list;
        if ((modelClass = classMap.get(model)) != null) {
            if ((list = dataService.getAll(modelClass)) != null) {
                return new ResponseEntity<>(list, HttpStatus.OK);
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = {"/{model}/{id}"}, method = RequestMethod.GET)
    public ResponseEntity<Object> getById(@PathVariable String model, @PathVariable Long id) {
        Class modelClass;
        Object object;
        if ((modelClass = classMap.get(model)) != null) {
            if ((object = dataService.getById(id, modelClass)) != null) {
                return new ResponseEntity<>(object, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------- DELETE

    @RequestMapping(value = {"/{model}/{id}"}, method = RequestMethod.DELETE)
    public ResponseEntity<ApiMessage> delete(@PathVariable String model, @PathVariable Long id) {
        Class modelClass;
        Object object;
        if ((modelClass = classMap.get(model)) != null) {
            if ((object = dataService.getById(id, modelClass)) != null) {
                boolean success = dataService.removeObject(object);
                return success ?
                        new ResponseEntity<>(new ApiMessage("Data deleted successfully"), HttpStatus.OK) :
                        new ResponseEntity<>(new ApiMessage("Fail to delete data"), HttpStatus.BAD_REQUEST) ;
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------- POST

    @RequestMapping(value = "/employees", method = RequestMethod.POST)
    public ResponseEntity<Object> createEmployee(@Validated @RequestBody Employee employee) {
        return processPost(employee, employee.getId());
    }

    @RequestMapping(value = "/types", method = RequestMethod.POST)
    public ResponseEntity<FurnitureType> createType(@Validated @RequestBody FurnitureType type) {
        return processPost(type, type.getId());
    }

    @RequestMapping(value = "/departments", method = RequestMethod.POST)
    public ResponseEntity<Department> createDepartment(@Validated @RequestBody Department department) {
        return processPost(department, department.getId());
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public ResponseEntity<Object> createOrder(@Validated @RequestBody Order order) {
        setupOrderFields(order);
        if (order.getExecutor() == null)
            return new ResponseEntity<>(new ApiMessage("Wrong order executor"), HttpStatus.BAD_REQUEST);
        if (order.getType() == null)
            return new ResponseEntity<>(new ApiMessage("Wrong furniture type"), HttpStatus.BAD_REQUEST);
        return processPost(order, order.getId());
    }

    private <MODEL> ResponseEntity<MODEL> processPost(MODEL object, Long id) {
        if (id != null && dataService.getById(id, object.getClass()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        MODEL created;
        if ((created = dataService.save(object)) != null) {
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // ------------------- PUT

    @RequestMapping(value = "/employees/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateEmployee(@PathVariable Long id, @RequestBody(required = false) Employee employee) {
        return processUpdate(employee, id, Employee.class, (employee1, employeeSaved) -> {
            if (employee1.getName() != null) employeeSaved.setName(employee1.getName());
            if (employee1.getSurname() != null) employeeSaved.setSurname(employee1.getSurname());
        });
    }

    @RequestMapping(value = "/types/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateType(@PathVariable Long id, @RequestBody(required = false) FurnitureType type) {
        return processUpdate(type, id, FurnitureType.class, (type1, typeSaved) -> {
            if (type1.getName() != null) typeSaved.setName(type1.getName());
        });
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateOrder(@PathVariable Long id, @RequestBody(required = false) Order order) {
        setupOrderFields(order);
        return processUpdate(order, id, Order.class, (order1, orderSaved) -> {
            if (order1.getName() != null) orderSaved.setName(order1.getName());
            if (order1.getType() != null) orderSaved.setType(order1.getType());
            if (order1.getExecutor() != null) orderSaved.setExecutor(order1.getExecutor());
            if (order1.getDate() != null) orderSaved.setDate(order1.getDate());
        });
    }

    @RequestMapping(value = "/departments/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateDepartment(@PathVariable Long id, @RequestBody(required = false) Department dept) {
        return processUpdate(dept, id, Department.class, (dept1, deptSaved) -> {
            if (dept1.getTitle() != null) deptSaved.setTitle(dept1.getTitle());
            if (dept1.getTypesIdSet() != null) {
                final List<FurnitureType> allTypes = dataService.getAll(FurnitureType.class);
                final Set<Long> availableIds = dept1.getTypesIdSet();
                deptSaved.setFurnitureTypes(allTypes.stream().filter(t -> availableIds.contains(t.getId())).collect(Collectors.toSet()));
            }
            if (dept1.getEmployeeIdSet() != null) {
                final Set<Long> availableIds = dept1.getEmployeeIdSet();
                if(availableIds.isEmpty()){
                    deptSaved.setEmployees(Collections.emptySet());
                } else {
                    dataService.getAll(Department.class)
                            .stream()
                            .filter(d -> !d.getId().equals(id))
                            .forEach(d -> {
                                Set<Employee> emplToRemove = d.getEmployees().stream().filter(e -> availableIds.contains(e.getId())).collect(Collectors.toSet());
                                if(!emplToRemove.isEmpty()) {
                                    d.getEmployees().removeAll(emplToRemove);
                                    dataService.save(d);
                                }
                            });
                    final List<Employee> allEmployees = dataService.getAll(Employee.class);
                    deptSaved.setEmployees(allEmployees.stream().filter(e -> availableIds.contains(e.getId())).collect(Collectors.toSet()));
                }
            }
        });
    }

    private <MODEL> ResponseEntity<Object> processUpdate(MODEL object, Long id, Class<MODEL> cls, BiConsumer<MODEL, MODEL> updateFieldsFunction){
        if (object == null) {
            return new ResponseEntity<>(new ApiMessage("Request body expected"), HttpStatus.OK);
        }
        final MODEL objectSaved = dataService.getById(id, cls);
        if (objectSaved == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        updateFieldsFunction.accept(object, objectSaved);
        dataService.save(objectSaved);
        return new ResponseEntity<>(objectSaved, HttpStatus.OK);
    }

    private void setupOrderFields(Order order){
        OrderExecutor orderExecutor = order.getExecutorDescription().getType().equals(OrderExecutor.TYPE_DEPARTMENT)?
                dataService.getById(order.getExecutorDescription().getId(), Department.class) :
                dataService.getById(order.getExecutorDescription().getId(), Employee.class);
        FurnitureType type = dataService.getById(order.getTypeId(), FurnitureType.class);
        order.setExecutor(orderExecutor);
        order.setType(type);
    }

}

package com.factory;

import com.factory.models.*;
import com.factory.repository.DepartmentRepository;
import com.factory.repository.EmployeeRepository;
import com.factory.repository.OrderRepository;
import com.factory.repository.TypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Stream;

@Component
public class TestDataLoader implements ApplicationRunner {

    private final static String DEPT_SOFT = "Soft furniture";
    private final static String DEPT_STORE = "Furniture storage";
    private final static String DEPT_OFFICE = "Office furniture";

    @Autowired ResourceLoader resourceLoader;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired TypesRepository typesRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        /*employee*/
        List<Employee>[] employees = mockedEmployee();  // length:3
        Stream.of(employees).forEach(list -> employeeRepository.save(list));
        System.out.println("employee... loaded");

        /*furniture-types*/
        Map<String, List<FurnitureType>> typesMap = mockTypes();    // length:9
        typesMap.values().forEach(list ->  typesRepository.save(list));
        System.out.println("furniture-types... loaded");

        /*departments*/
        List<Department> departments = Arrays.asList(
                new Department(DEPT_SOFT, new HashSet<>(employees[0]), new HashSet<>(typesMap.get(DEPT_SOFT))),
                new Department(DEPT_STORE, new HashSet<>(employees[1]), new HashSet<>(typesMap.get(DEPT_STORE))),
                new Department(DEPT_OFFICE, new HashSet<>(employees[2]), new HashSet<>(typesMap.get(DEPT_OFFICE)))
        );
        departmentRepository.save(departments);
        System.out.println("departments... loaded");

        /*orders*/
        Department department1 = departments.get(0);
        FurnitureType[] types_1 = department1.getFurnitureTypes().toArray(new FurnitureType[0]);
        long now = Calendar.getInstance().getTimeInMillis();
        OrderExecutor executor1 = department1;
        OrderExecutor executor2 = employees[0].get(0);
        List<Order> orders = Arrays.asList(
                new Order("Order-1", executor1, types_1[0], new Date(now)),
                new Order("Order-2", executor2, types_1[1], new Date(now))
        );
        orderRepository.save(orders);
        System.out.println("orders... loaded");

    }

    private List<Employee>[] mockedEmployee() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(resourceLoader.getResource("classpath:/MOCK_DATA.txt").getFile()));
        String line;String[] split;
        List<Employee> list = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            split = line.split("\t");
            list.add(new Employee(split[0], split[1]));
        }
        int _size = list.size()/3;
        List<Employee>[] array = (List<Employee>[]) new ArrayList[3];
        array[0] = new ArrayList<>(list.subList(0, _size));
        array[1] = new ArrayList<>(list.subList(_size, _size*2));
        array[2] = new ArrayList<>(list.subList(_size*2,  list.size()));
        return array;
    }

    private Map<String, List<FurnitureType>> mockTypes() {
        final Map<String, List<FurnitureType>> map = new HashMap<>(3);
        map.put(DEPT_SOFT, Arrays.asList(new FurnitureType("Bed"), new FurnitureType("Couch"), new FurnitureType("Armchair")));
        map.put(DEPT_STORE, Arrays.asList(new FurnitureType("Wardrobe"), new FurnitureType("Nightstand"), new FurnitureType("Rack")));
        map.put(DEPT_OFFICE, Arrays.asList(new FurnitureType("Table"), new FurnitureType("Chair"), new FurnitureType("Rocking chair")));
        return map;
    }
}

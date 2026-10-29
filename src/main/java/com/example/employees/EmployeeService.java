package com.example.employees;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    public EmployeeResponse findById(Long id) {
        return EmployeeResponse.from(findEmployee(id));
    }

    public EmployeeResponse create(EmployeeRequest request) {
        Employee employee = new Employee(request.name(), request.email(), request.designation());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        employee.update(request.name(), request.email(), request.designation());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        employeeRepository.delete(findEmployee(id));
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }
}

package com.example.employees;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void findAllMapsEmployeesToResponses() {
        when(employeeRepository.findAll()).thenReturn(List.of(
                new Employee("Avery Lee", "avery@example.com", Designation.TEAM_LEAD)));

        List<EmployeeResponse> responses = employeeService.findAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).salary()).isEqualByComparingTo(new BigDecimal("130000"));
    }

    @Test
    void findByIdReturnsEmployeeResponse() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(
                new Employee("Avery Lee", "avery@example.com", Designation.INTERN)));

        EmployeeResponse response = employeeService.findById(7L);

        assertThat(response.name()).isEqualTo("Avery Lee");
        assertThat(response.salary()).isEqualByComparingTo(new BigDecimal("30000"));
    }

    @Test
    void createSavesEmployeeAndReturnsDerivedSalary() {
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeService.create(
                new EmployeeRequest("Avery Lee", "avery@example.com", Designation.MANAGER));

        assertThat(response.designation()).isEqualTo(Designation.MANAGER);
        assertThat(response.salary()).isEqualByComparingTo(new BigDecimal("150000"));
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateChangesEmployeeAndReturnsNewSalary() {
        Employee employee = new Employee("Avery Lee", "avery@example.com", Designation.INTERN);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        EmployeeResponse response = employeeService.update(
                7L, new EmployeeRequest("Jordan Kim", "jordan@example.com", Designation.MANAGER));

        assertThat(response.name()).isEqualTo("Jordan Kim");
        assertThat(response.salary()).isEqualByComparingTo(new BigDecimal("150000"));
    }

    @Test
    void findByIdThrowsNotFoundForMissingEmployee() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findById(999L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void deleteRemovesExistingEmployee() {
        Employee employee = new Employee("Avery Lee", "avery@example.com", Designation.INTERN);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        employeeService.delete(7L);

        verify(employeeRepository).delete(employee);
    }
}
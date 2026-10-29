package com.example.employees;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Test
    void getEmployeesReturnsEmployeeList() throws Exception {
        when(employeeService.findAll()).thenReturn(List.of(response(7L, Designation.TEAM_LEAD)));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Avery Lee"))
                .andExpect(jsonPath("$[0].salary").value(130000));
    }

    @Test
    void getEmployeeReturnsEmployeeById() throws Exception {
        when(employeeService.findById(7L)).thenReturn(response(7L, Designation.INTERN));

        mockMvc.perform(get("/api/employees/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.salary").value(30000));
    }

    @Test
    void getDesignationsReturnsConfiguredSalaries() throws Exception {
        mockMvc.perform(get("/api/designations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].designation").value("INTERN"))
                .andExpect(jsonPath("$[0].salary").value(30000));
    }

    @Test
    void createEmployeeReturnsCreatedResponse() throws Exception {
        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(response(7L, Designation.SENIOR_DEVELOPER));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Avery Lee","email":"avery@example.com","designation":"SENIOR_DEVELOPER"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.salary").value(100000));

        verify(employeeService).create(any(EmployeeRequest.class));
    }

    @Test
    void createEmployeeRejectsInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","email":"not-an-email","designation":null}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    @Test
    void updateEmployeeReturnsUpdatedResponse() throws Exception {
        when(employeeService.update(any(Long.class), any(EmployeeRequest.class)))
                .thenReturn(response(7L, Designation.MANAGER));

        mockMvc.perform(put("/api/employees/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Avery Lee","email":"avery@example.com","designation":"MANAGER"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.designation").value("MANAGER"))
                .andExpect(jsonPath("$.salary").value(150000));

        verify(employeeService).update(any(Long.class), any(EmployeeRequest.class));
    }

    @Test
    void getMissingEmployeeReturnsNotFound() throws Exception {
        when(employeeService.findById(999L)).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEmployeeReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/employees/7"))
                .andExpect(status().isNoContent());

        verify(employeeService).delete(7L);
    }

    private EmployeeResponse response(Long id, Designation designation) {
        return new EmployeeResponse(id, "Avery Lee", "avery@example.com", designation, designation.getSalary());
    }
}
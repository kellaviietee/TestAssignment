package niilo.investment.validators;

import niilo.investment.employee.Employee;
import niilo.investment.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class EmployeeValidatorTest {

    @Mock
    EmployeeRepository employeeRepository;
    @InjectMocks
    EmployeeValidator employeeValidator;
    @BeforeEach
    public void setUp(){
        openMocks(this);
    }

    @Test
    void testValidEmployee() {
        Employee employee = new Employee(1L);
        when(employeeRepository.existsById(employee.getId())).thenReturn(true);
        employeeValidator.validateEmployee(employee);
    }

    @Test
    void testInvalidEmployee() {
        Employee employee = new Employee(1L);
        when(employeeRepository.existsById(employee.getId())).thenReturn(false);
        assertThatThrownBy(() -> {
            employeeValidator.validateEmployee(employee);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Employee not in the database");
    }
    @Test
    void testEmployeeIdIsNull() {
        Employee employee = new Employee(null);
        when(employeeRepository.existsById(employee.getId())).thenReturn(false);
        assertThatThrownBy(() -> {
            employeeValidator.validateEmployee(employee);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Employee ID is missing");
    }

}
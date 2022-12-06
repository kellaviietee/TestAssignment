package niilo.investment.validators;

import niilo.investment.employee.Employee;
import niilo.investment.employee.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EmployeeValidator {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Validates the employee
     * @param employee employee to be validated.
     */
    public void validateEmployee(Employee employee) {
        if (employee.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee ID is missing");
        } else if (!employeeRepository.existsById(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not in the database");
        }
    }
}

package niilo.investment.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import niilo.investment.employee.Employee;

import java.util.List;

@AllArgsConstructor
@Data
public class EmployeeMonthlyExpenditure {

    private final Employee employee;
    private final List<MonthlyExpenditureDTO> monthlyExpenses;
}

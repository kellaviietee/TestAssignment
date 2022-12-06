package niilo.investment.service;

import niilo.investment.employee.Employee;
import niilo.investment.employee.EmployeeRepository;
import niilo.investment.sharedata.ShareDataRepository;
import niilo.investment.statistics.EmployeeMonthlyExpenditure;
import niilo.investment.statistics.MonthlyAcquiringRecords;
import niilo.investment.statistics.MonthlyExpenditureDTO;
import niilo.investment.statistics.MonthlyStockDTO;
import niilo.investment.stock.Stock;
import niilo.investment.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ShareDataRepository shareDataRepository;

    /**
     * Add a single stock to the database.
     */
    public Stock addStockToRepository(Stock stock) {
        return stockRepository.save(stock);
    }
    /**
     * Adds multiple stocks to the database.
     */
    public List<Stock> addStocksToRepository(List<Stock> stocks) {
        return stockRepository.saveAll(stocks);
    }

    /**
     * Return all the stocks in the database.
     */
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    /**
     * Total amount spent on stocks in each month.
     * @return Monthly expenses across all employees.
     */
    public List<MonthlyExpenditureDTO> getMonthlyCost() {
        return calculateTotalCostForAllMonths();
    }

    /**
     * Total amount spent on stocks by a specific employee.
     * @param employeeId Id of the employee.
     * @return Monthly report of expenses of the employee.
     */
    public EmployeeMonthlyExpenditure getEmployeeMonthlyExpenses(Long employeeId) {
        return calculateEmployeeTotalExpensesForAllMonths(employeeId);
    }

    /**
     * What stocks were bought in a specific month.
     * @param month Month that the stocks were bought on.
     * @return Stocks in a given month.
     */
    public MonthlyAcquiringRecords AcquiredStocksInAGivenMonth(Integer month) {
        return getStockDataInAMonth(Month.of(month));
    }

    /**
     * All the stocks that an employee bought sorted by Month.
     * @param employeeId id of the employee.
     * @return Stocks categorized by months.
     */
    public List<MonthlyAcquiringRecords> getAnEmployeeRecordsSortedByMonth(Long employeeId) {
        return Arrays.stream(Month.values())
                .map(month -> getAllAcquiredRecordsInAMonthOfAnEmployee(employeeId, month)).toList();

    }

    /**
     * All the stocks an employee bought in a given month.
     * @param employeeId id of the employee.
     * @param month Month the stocks were acquired in.
     * @return Stocks bought in that month by the specified employee.
     */
    public MonthlyAcquiringRecords getAllAcquiredRecordsInAMonthOfAnEmployee(Long employeeId,Month month) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee Id not found"));
        List<Stock> allStocks = stockRepository.findAll();
        List<MonthlyStockDTO> monthlyStockDTOList  = allStocks.stream()
                .filter(stock -> stock.getDate().getMonth().equals(month))
                .filter(stock -> stock.getEmployee().equals(employee))
                .map(MonthlyStockDTO::new).toList();
        return new MonthlyAcquiringRecords(month, monthlyStockDTOList);
    }

    /**
     * All stocks that have been bought.
     * @return Stocks that have been bought sorted by Months.
     */
    public List<MonthlyAcquiringRecords> getAllAcquiredRecordsByMonths() {
        return Arrays.stream(Month.values())
                .map(this::getStockDataInAMonth).toList();
    }

    /**
     * All stocks bought in a given month.
     * @param month Month that has been specified.
     * @return Stocks bought in that month.
     */
    private MonthlyAcquiringRecords getStockDataInAMonth(Month month) {
        List<Stock> allStocks = stockRepository.findAll();
        List<MonthlyStockDTO> monthlyStockDTOList  = allStocks.stream()
                .filter(stock -> stock.getDate().getMonth().equals(month))
                .map(MonthlyStockDTO::new).toList();
        return new MonthlyAcquiringRecords(month, monthlyStockDTOList);
    }

    /**
     * Calculates the total expenses of all the stocks.
     * @return total expenses of all the stocks sorted by month.
     */
    private List<MonthlyExpenditureDTO> calculateTotalCostForAllMonths() {
        List<Stock> allStocks = stockRepository.findAll();
        List<MonthlyExpenditureDTO> allMonthlyCosts = new ArrayList<>();
        for (Month month : Month.values()) {
            List<Stock> monthlyStocks = allStocks.stream()
                    .filter(stock -> stock.getDate().getMonth().equals(month))
                    .toList();
            allMonthlyCosts.add(new MonthlyExpenditureDTO(monthlyStocks,month));
        }
        return allMonthlyCosts;
    }

    /**
     * Shows how much did the employee spent on all the stocks.
     * @param employeeId id of the employee.
     * @return In each month how much did the employee spent.
     */
    private EmployeeMonthlyExpenditure calculateEmployeeTotalExpensesForAllMonths(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee Id not found"));
        List<Stock> allStocks = stockRepository.findAll();
        List<MonthlyExpenditureDTO> allMonthlyCosts = new ArrayList<>();
        for (Month month : Month.values()) {
            List<Stock> monthlyStocks = allStocks.stream()
                    .filter(stock -> stock.getDate().getMonth().equals(month))
                    .filter(stock -> stock.getEmployee().equals(employee))
                    .toList();
            allMonthlyCosts.add(new MonthlyExpenditureDTO(monthlyStocks,month));
        }
        return new EmployeeMonthlyExpenditure(employee,allMonthlyCosts);
    }
}

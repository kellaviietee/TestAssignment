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

    public Stock addStockToRepository(Stock stock) {
        return stockRepository.save(stock);
    }

    public List<Stock> addStocksToRepository(List<Stock> stocks) {
        return stockRepository.saveAll(stocks);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public List<MonthlyExpenditureDTO> getMonthlyCost() {
        return calculateTotalCostForAllMonths();
    }

    public EmployeeMonthlyExpenditure getEmployeeMonthlyExpenses(Long employeeId) {
        return calculateEmployeeTotalExpensesForAllMonths(employeeId);
    }

    public MonthlyAcquiringRecords AcquiredStocksInAGivenMonth(Integer month) {
        return getStockDataInAMonth(Month.of(month));
    }

    public List<MonthlyAcquiringRecords> getAnEmployeeRecordsSortedByMonth(Long employeeId) {
        return Arrays.stream(Month.values())
                .map(month -> getAllAcquiredRecordsInAMonthOfAnEmployee(employeeId, month)).toList();

    }

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

    public List<MonthlyAcquiringRecords> getAllAcquiredRecordsByMonths() {
        return Arrays.stream(Month.values())
                .map(this::getStockDataInAMonth).toList();
    }

    private MonthlyAcquiringRecords getStockDataInAMonth(Month month) {
        List<Stock> allStocks = stockRepository.findAll();
        List<MonthlyStockDTO> monthlyStockDTOList  = allStocks.stream()
                .filter(stock -> stock.getDate().getMonth().equals(month))
                .map(MonthlyStockDTO::new).toList();
        return new MonthlyAcquiringRecords(month, monthlyStockDTOList);
    }

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

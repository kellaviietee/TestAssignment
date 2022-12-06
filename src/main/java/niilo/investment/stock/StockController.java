package niilo.investment.stock;

import niilo.investment.service.Service;
import niilo.investment.statistics.EmployeeMonthlyExpenditure;
import niilo.investment.statistics.MonthlyAcquiringRecords;
import niilo.investment.statistics.MonthlyExpenditureDTO;
import niilo.investment.validators.StockValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api")
public class StockController {

    @Autowired
    Service service;

    @Autowired
    StockValidator stockValidator;

    @GetMapping(path = "/stocks")
    public List<Stock> getAllStocks() {
        return service.getAllStocks();
    }

    @PostMapping(path = "/stocks/add")
    public Stock addStock(@RequestBody Stock addedStock) {
        Stock validStock = stockValidator.isStockValid(addedStock);
        return service.addStockToRepository(validStock);
    }

    @PostMapping(path = "/stocks/addall")
    public List<Stock> addStock(@RequestBody List<Stock> addedStocks) {
        List<Stock> validStocks = stockValidator.validateAllStocks(addedStocks);
        return service.addStocksToRepository(validStocks);
    }

    @GetMapping(path = "/monthly")
    public List<MonthlyExpenditureDTO> getMonthlyCost() {
        return service.getMonthlyCost();
    }

    @GetMapping(path = "/monthly/{id}")
    public EmployeeMonthlyExpenditure getEmployeeMonthlyExpenses(@PathVariable long id) {
        return service.getEmployeeMonthlyExpenses(id);
    }

    @GetMapping(path = "/monthly/stocks/{month}")
    public MonthlyAcquiringRecords getAcquiredStocksStatisticsInAGivenMonth(@PathVariable Integer month) {
        return service.AcquiredStocksInAGivenMonth(month);
    }

    @GetMapping(path = "/monthly/stocks")
    public List<MonthlyAcquiringRecords> getAcquiredStocksStatistics() {
        return service.getAllAcquiredRecordsByMonths();
    }

    @GetMapping(path = "/monthly/stocks/{month}/{id}")
    public MonthlyAcquiringRecords getAcquiredStocksStatisticsInAGivenMonth(@PathVariable Integer month, @PathVariable long id) {
        return service.getAllAcquiredRecordsInAMonthOfAnEmployee(id, Month.of(month));
    }

    @GetMapping(path = "/stocks/{id}")
    public List<MonthlyAcquiringRecords> getEmployeeAcquiredStocksSortedByMonths(@PathVariable long id) {
        return service.getAnEmployeeRecordsSortedByMonth(id);
    }
}

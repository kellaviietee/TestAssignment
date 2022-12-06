package niilo.investment.service;

import niilo.investment.employee.Employee;
import niilo.investment.employee.EmployeeRepository;
import niilo.investment.sharedata.ShareData;
import niilo.investment.statistics.EmployeeMonthlyExpenditure;
import niilo.investment.statistics.MonthlyAcquiringRecords;
import niilo.investment.statistics.MonthlyExpenditureDTO;
import niilo.investment.statistics.MonthlyStockDTO;
import niilo.investment.stock.Stock;
import niilo.investment.stock.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ServiceTest {

    private final Stock stock1 = new Stock(1L,
            new ShareData(1L,"First Company","FCPY",
                    "EE-FE4592FA96","Estonia","Financial"),
            12.5,5L, LocalDate.now(),new Employee(1L));
    private final Stock stock2 = new Stock(2L,
            new ShareData(2L,"Another Company","ACPY",
                    "LV-FE4592FA96","Latvia","Timber"),
            9.0,23L, LocalDate.of(2020, Month.APRIL,12),new Employee(2L));
    private final Stock stock3 = new Stock(3L,
            new ShareData(3L,"Third Firm","TFRM",
                    "LT-FE4872FA26","Lithuania","Education"),
            3.0,23000L, LocalDate.of(2018, Month.FEBRUARY,10),new Employee(3L));
    private final Stock stock4 = new Stock(4L,
            new ShareData(4L,"Fourth Business","FBSN",
                    "FI-GE48H2FA16","Finland","Agriculture"),
            6.45,1500L, LocalDate.of(2019, Month.MARCH,15),new Employee(1L));

    @Mock
    StockRepository stockRepository;
    @Mock
    EmployeeRepository employeeRepository;
    @InjectMocks
    Service service;

    @BeforeEach
    public void setUp(){
        openMocks(this);
    }

    @Test
    void ServiceAddingStockShouldReturnStock() {
        Stock stock = new Stock();
        when(stockRepository.save(stock)).thenReturn(stock);
        Stock result = service.addStockToRepository(stock);
        assertThat(result).isEqualTo(stock);
    }

    @Test
    void ServiceAddingMultipleStockSShouldReturnListOfStocks() {
        List<Stock> stockList = new ArrayList<>();
        when(stockRepository.saveAll(stockList)).thenReturn(stockList);
        List<Stock> result = service.addStocksToRepository(stockList);
        assertThat(result).isEqualTo(stockList);
    }

    @Test
    void ServiceReturnsAllStocks() {
        List<Stock> stockList = new ArrayList<>();
        when(stockRepository.findAll()).thenReturn(stockList);
        List<Stock> result = service.getAllStocks();
        assertThat(result).isEqualTo(stockList);
    }
    @Test
    void ServiceReturnsMonthlyCostsOfAllStocks() {
        List<Stock> stockList = List.of(stock1, stock2, stock3, stock4);
        when(stockRepository.findAll()).thenReturn(stockList);
        List<MonthlyExpenditureDTO> monthlyExpenditureDTOList = new ArrayList<>();
        for (Month month : Month.values()) {
            List<Stock> monthlyStocks = new ArrayList<>();
            for (Stock stock : stockList) {
                if (stock.getDate().getMonth().equals(month)) {
                    monthlyStocks.add(stock);
                }
            }
            monthlyExpenditureDTOList.add(new MonthlyExpenditureDTO(monthlyStocks, month));
        }
        List<MonthlyExpenditureDTO> serviceMonthlyExpenditure = service.getMonthlyCost();
        assertThat(serviceMonthlyExpenditure).isEqualTo(monthlyExpenditureDTOList);
    }

    @Test
    void serviceReturnsEmployeesAllMonthlyCosts() {
        Employee dummyEmployee = new Employee(1L);
        when(employeeRepository.findById(dummyEmployee.getId())).thenReturn(Optional.of(dummyEmployee));
        List<Stock> stockList = List.of(stock1, stock2, stock3, stock4);
        when(stockRepository.findAll()).thenReturn(stockList);
        EmployeeMonthlyExpenditure expenditure;
        List<MonthlyExpenditureDTO> monthlyExpenditureDTOList = new ArrayList<>();
        for (Month month : Month.values()) {
            List<Stock> monthlyStocksOfEmployee = new ArrayList<>();
            for (Stock stock : stockList) {
                if (stock.getDate().getMonth().equals(month) && stock.getEmployee().equals(dummyEmployee)) {
                    monthlyStocksOfEmployee.add(stock);
                }
            }
            monthlyExpenditureDTOList.add(new MonthlyExpenditureDTO(monthlyStocksOfEmployee, month));
        }
        expenditure = new EmployeeMonthlyExpenditure(dummyEmployee, monthlyExpenditureDTOList);
        assertThat(service.getEmployeeMonthlyExpenses(dummyEmployee.getId())).isEqualTo(expenditure);
    }

    @Test
    void gettingEmployeesMonthlyCostThrowsErrorIfIdNull() {
        when(employeeRepository.findById(null)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Employee Id not found"));
        assertThatThrownBy(() -> {
            service.getEmployeeMonthlyExpenses(null);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Employee Id not found");
    }
    @Test
    void gettingEmployeesMonthlyCostThrowsErrorIfIdWrong() {
        when(employeeRepository.findById(1L)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Employee Id not found"));
        assertThatThrownBy(() -> {
            service.getEmployeeMonthlyExpenses(1L);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Employee Id not found");
    }

    @Test
    void gettingStockDataOfAMonth() {
        List<Stock> stockList = List.of(stock1, stock2, stock3, stock4);
        when(stockRepository.findAll()).thenReturn(stockList);
        Month month = Month.APRIL;
        List<MonthlyStockDTO> monthlyStockDTOList = new ArrayList<>();
        for (Stock stock : stockRepository.findAll()) {
            if (stock.getDate().getMonth().equals(month)) {
                monthlyStockDTOList.add(new MonthlyStockDTO(stock));
            }
        }
        MonthlyAcquiringRecords monthlyAcquiringRecords = new MonthlyAcquiringRecords(month,monthlyStockDTOList);
        assertThat(service.AcquiredStocksInAGivenMonth(month.getValue())).isEqualTo(monthlyAcquiringRecords);
    }

    @Test
    void getAllRecordsByMonth() {
        List<Stock> stockList = List.of(stock1, stock2, stock3, stock4);
        when(stockRepository.findAll()).thenReturn(stockList);
        List<MonthlyAcquiringRecords> monthlyAcquiringRecords = new ArrayList<>();
        for(Month month : Month.values()) {
            List<MonthlyStockDTO> monthlyStockDTOList = new ArrayList<>();
            for (Stock stock : stockList) {
                if (stock.getDate().getMonth().equals(month)) {
                    monthlyStockDTOList.add(new MonthlyStockDTO(stock));
                }
            }
            monthlyAcquiringRecords.add(new MonthlyAcquiringRecords(month,monthlyStockDTOList));
        }
        assertThat(service.getAllAcquiredRecordsByMonths()).isEqualTo(monthlyAcquiringRecords);
    }

    @Test
    void getAcquiredRecordsOfAnEmployeeInAMonth() {
        Employee dummyEmployee = new Employee(1L);
        when(employeeRepository.findById(dummyEmployee.getId())).thenReturn(Optional.of(dummyEmployee));
        List<Stock> stockList = List.of(stock1, stock2, stock3, stock4);
        when(stockRepository.findAll()).thenReturn(stockList);
        Month month = Month.MARCH;
        MonthlyAcquiringRecords monthlyAcquiringRecords = calculateMonthlyAcquiringRecords(month,dummyEmployee,stockList);
        assertThat(service.getAllAcquiredRecordsInAMonthOfAnEmployee(dummyEmployee.getId(), month)).
                isEqualTo(monthlyAcquiringRecords);

    }

    @Test
    void getAllAcquiredRecordsOfAnEmployee() {
        Employee dummyEmployee = new Employee(1L);
        when(employeeRepository.findById(dummyEmployee.getId())).thenReturn(Optional.of(dummyEmployee));
        List<Stock> stockList = List.of(stock1, stock2, stock3, stock4);
        when(stockRepository.findAll()).thenReturn(stockList);
        List<MonthlyAcquiringRecords> monthlyAcquiringRecordsList = new ArrayList<>();
        for(Month month : Month.values()) {
            monthlyAcquiringRecordsList.add(calculateMonthlyAcquiringRecords(month,dummyEmployee,stockList));
        }
        assertThat(service.getAnEmployeeRecordsSortedByMonth(dummyEmployee.getId())).isEqualTo(monthlyAcquiringRecordsList);
    }

    private MonthlyAcquiringRecords calculateMonthlyAcquiringRecords(Month month, Employee employee, List<Stock> stockList) {
        List<MonthlyStockDTO> monthlyStockDTOList = new ArrayList<>();
        for(Stock stock : stockList) {
            if(stock.getDate().getMonth().equals(month) && stock.getEmployee().equals(employee)) {
                monthlyStockDTOList.add(new MonthlyStockDTO(stock));
            }
        }
        return new MonthlyAcquiringRecords(month,monthlyStockDTOList);
    }
}
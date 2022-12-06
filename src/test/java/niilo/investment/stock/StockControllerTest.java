package niilo.investment.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import niilo.investment.employee.Employee;
import niilo.investment.service.Service;
import niilo.investment.sharedata.ShareData;
import niilo.investment.statistics.EmployeeMonthlyExpenditure;
import niilo.investment.statistics.MonthlyAcquiringRecords;
import niilo.investment.statistics.MonthlyExpenditureDTO;
import niilo.investment.statistics.MonthlyStockDTO;
import niilo.investment.validators.StockValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StockControllerTest {
    private final Stock validStock = new Stock(1L,
            new ShareData(1L, "First Company", "FCPY",
                    "EEFE4592FA96", "Estonia", "Financial"),
            12.5, 5L, LocalDate.of(2022,4,11), new Employee(1L));
    private final Stock validStock1 = new Stock(2L,
            new ShareData(2L, "Another Company", "ACPY",
                    "LVFE2562FA96", "Latvia", "Timber"),
            10.5, 2L, LocalDate.of(2022,12,8), new Employee(2L));
    private final Stock validStock2 = new Stock(1L,
            new ShareData(3L, "Third Company", "THPY",
                    "LTFE4592FA96", "Lithuania", "Education"),
            7.5, 50L, LocalDate.of(2022,4,22), new Employee(1L));
    private final Stock validStock3 = new Stock(2L,
            new ShareData(2L, "Farming Ltd", "FLTD",
                    "FIFE27652FA96", "Finland", "Agriculture"),
            3.0, 12L, LocalDate.of(2022,12,7), new Employee(3L));
    private final List<Stock> stocks = List.of(validStock, validStock2);
    private final List<Stock> stocks1 = List.of(validStock1, validStock3);
    private final MonthlyExpenditureDTO AprilExpenditureDTO = new MonthlyExpenditureDTO(stocks, Month.APRIL);
    private final MonthlyExpenditureDTO DecemberExpenditureDTO = new MonthlyExpenditureDTO(stocks1, Month.DECEMBER);
    private final List<MonthlyExpenditureDTO> monthlyExpenditureDTOList = List.of(AprilExpenditureDTO, DecemberExpenditureDTO);
    private final Employee employee = new Employee(1L);
    private final EmployeeMonthlyExpenditure expenditure = new EmployeeMonthlyExpenditure(employee,monthlyExpenditureDTOList);
    private final MonthlyStockDTO monthlyStockDTO = new MonthlyStockDTO(validStock);
    private final MonthlyStockDTO monthlyStockDTO1 = new MonthlyStockDTO(validStock2);
    private final MonthlyStockDTO monthlyStockDTO2 = new MonthlyStockDTO(validStock1);
    private final MonthlyStockDTO monthlyStockDTO3 = new MonthlyStockDTO(validStock3);
    private final List<MonthlyStockDTO> monthlyStockDTOList = List.of(monthlyStockDTO, monthlyStockDTO1);
    private final List<MonthlyStockDTO> monthlyStockDTOList1 = List.of( monthlyStockDTO2, monthlyStockDTO3);
    private  final MonthlyAcquiringRecords monthlyAcquiringRecords = new MonthlyAcquiringRecords(Month.APRIL, monthlyStockDTOList);
    private  final MonthlyAcquiringRecords monthlyAcquiringRecords1 = new MonthlyAcquiringRecords(Month.DECEMBER, monthlyStockDTOList1);
    private final List<MonthlyAcquiringRecords> monthlyAcquiringRecordsList = List.of(monthlyAcquiringRecords, monthlyAcquiringRecords1);


    @Mock
    Service service;
    @Mock
    StockValidator stockValidator;
    @InjectMocks
    StockController stockController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).build();
    }
    @Test
    void testList() throws Exception {
        List<Stock> validStocks = List.of(validStock, validStock1);
        when(service.getAllStocks()).thenReturn(validStocks);
        MvcResult result = mockMvc.perform(get("/api/stocks")).andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(validStocks));

    }

    @Test
    void addOneStock() throws Exception {
        when(stockValidator.isStockValid(validStock)).thenReturn(validStock);
        when(service.addStockToRepository(validStock)).thenReturn(validStock);
        MvcResult result = mockMvc.perform(post("/api/stocks/add")
                .content(asJsonString(validStock))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(validStock));
    }

    @Test
    void addMultipleStocks() throws Exception {
        when(stockValidator.validateAllStocks(stocks)).thenReturn(stocks);
        when(service.addStocksToRepository(stocks)).thenReturn(stocks);
        MvcResult result = mockMvc.perform(post("/api/stocks/addall")
                .content(asJsonString(stocks))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(stocks));

    }

    @Test
    void monthlyCosts() throws Exception {
        when(service.getMonthlyCost()).thenReturn(monthlyExpenditureDTOList);
        MvcResult result = mockMvc.perform(get("/api/monthly"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(monthlyExpenditureDTOList));

    }

    @Test
    void employeeMonthlyExpenses() throws Exception {
        when(service.getEmployeeMonthlyExpenses(1L)).thenReturn(expenditure);
        MvcResult result = mockMvc.perform(get("/api/monthly/1"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(expenditure));

    }

    @Test
    void stocksAcquiredInAMonth() throws Exception {
        when(service.AcquiredStocksInAGivenMonth(4)).thenReturn(monthlyAcquiringRecords);
        MvcResult result = mockMvc.perform(get("/api/monthly/stocks/4"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(monthlyAcquiringRecords));

    }

    @Test
    void allAcquiredStocksByMonths() throws Exception {
        when(service.getAllAcquiredRecordsByMonths()).thenReturn(monthlyAcquiringRecordsList);
        MvcResult result = mockMvc.perform(get("/api/monthly/stocks"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(monthlyAcquiringRecordsList));
    }

    @Test
    void employeeAcquiredStocksOfTheMonth() throws Exception {
        when(service.getAllAcquiredRecordsInAMonthOfAnEmployee(1L,Month.APRIL))
                .thenReturn(monthlyAcquiringRecords);
        MvcResult result = mockMvc.perform(get("/api/monthly/stocks/4/1"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(monthlyAcquiringRecords));
    }

    @Test
    void allEmployeeAcquiredStocksByMonths() throws Exception {
        when(service.getAnEmployeeRecordsSortedByMonth(1L)).thenReturn(monthlyAcquiringRecordsList);
        MvcResult result = mockMvc.perform(get("/api/stocks/1"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(asJsonString(monthlyAcquiringRecordsList));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().findAndRegisterModules().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
package niilo.investment.validators;

import niilo.investment.employee.Employee;
import niilo.investment.sharedata.ShareData;
import niilo.investment.stock.Stock;
import niilo.investment.stock.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class StockValidatorTest {


    @Mock
    StockRepository stockRepository;
    @Mock
    EmployeeValidator employeeValidator;
    @Mock
    ShareDataValidator shareDataValidator;
    @InjectMocks
    StockValidator stockValidator;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    void validStock() {
        Stock validStock = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 5L, LocalDate.now(), new Employee(1L));
        assertThat(stockValidator.isStockValid(validStock)).isEqualTo(validStock);
    }

    @Test
    void stockMissingDateThrowsError() {
        Stock stockWithoutDate = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 5L, null, new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockWithoutDate);
        }).isInstanceOf(ResponseStatusException.class).hasMessageContaining("Stock date is missing");
    }

    @Test
    void stockMissingIdThrowsError() {
        Stock stockWithoutId = new Stock(null,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 5L, LocalDate.now(), new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockWithoutId);
        }).isInstanceOf(ResponseStatusException.class).hasMessageContaining("Stock ID is missing");
    }

    @Test
    void stockIdLessThan1ThrowsError() {
        Stock stockIdLessThan1 = new Stock(0L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 5L, LocalDate.now(), new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockIdLessThan1);
        }).isInstanceOf(ResponseStatusException.class).hasMessageContaining("Stock ID can not be smaller than 1");
    }

    @Test
    void stockIdAlreadyInDatabaseThrowsError() {
        when(stockRepository.existsById(1L)).thenReturn(true);
        Stock stockIdExists = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 5L, LocalDate.now(), new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockIdExists);
        }).isInstanceOf(ResponseStatusException.class).hasMessageContaining("Purchase with this ID has been done already");
    }

    @Test
    void stockMissingPricePerShare() {
        Stock stockWithoutPricePerShare = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                null, 5L, LocalDate.now(), new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockWithoutPricePerShare);
        }).isInstanceOf(ResponseStatusException.class).hasMessageContaining("Stock price per share is missing");
    }

    @Test
    void stockPricePerShareIsZero() {
        Stock stockWithZeroPricePerShare = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                0.0, 5L, LocalDate.now(), new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockWithZeroPricePerShare);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Stock price per share has to be bigger than zero");
    }

    @Test
    void stockMissingVolumeAcquired() {
        Stock stockWithoutVolume = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, null, LocalDate.now(), new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockWithoutVolume);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Stock volume acquired is missing");
    }
    @Test
    void stockVolumeAcquiredLessOrEqualToZero() {
        Stock stockWithZeroVolume = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 0L, LocalDate.now(), new Employee(1L));
        assertThatThrownBy(() -> {
            stockValidator.isStockValid(stockWithZeroVolume);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Stock volume acquired has to be bigger than zero");
    }

    @Test
    void validListOfStocks() {
        Stock validStock = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 5L, LocalDate.now(), new Employee(1L));
        Stock validStock1 = new Stock(2L,
                new ShareData(2L, "Another Company", "ACPY",
                        "LVFE2562FA96", "Latvia", "Timber"),
                10.5, 2L, LocalDate.of(2022,10,14), new Employee(1L));
        List<Stock> validStocks = List.of(validStock, validStock1);
        assertThat(stockValidator.validateAllStocks(validStocks)).isEqualTo(validStocks);
    }

    @Test
    void invalidListOfStocks() {
        Stock validStock = new Stock(1L,
                new ShareData(1L, "First Company", "FCPY",
                        "EEFE4592FA96", "Estonia", "Financial"),
                12.5, 5L, LocalDate.now(), new Employee(1L));
        Stock invalidStock = new Stock(2L,
                new ShareData(2L, "Another Company", "ACPY",
                        "LVFE2562FA96", "Latvia", "Timber"),
                10.5, 2L, null, new Employee(1L));
        List<Stock> invalidStocks = List.of(validStock, invalidStock);
        assertThatThrownBy(() -> {
            stockValidator.validateAllStocks(invalidStocks);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Problem with Stock nr 2 Stock date is missing");

    }
}
package niilo.investment.validators;

import niilo.investment.stock.Stock;
import niilo.investment.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class StockValidator {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private EmployeeValidator employeeValidator;

    @Autowired
    private ShareDataValidator shareDataValidator;

    public Stock isStockValid(Stock stock) {
        validateStockId(stock);
        validateStockPricePerShare(stock);
        validateStockVolumeAcquired(stock);
        validateStockDate(stock);
        employeeValidator.validateEmployee(stock.getEmployee());
        shareDataValidator.validateShareData(stock.getShareData());
        return stock;
    }

    private void validateStockDate(Stock stock) {
        if (stock.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Stock date is missing");
        }
    }

    private void validateStockId(Stock stock) {
        if (stock.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock ID is missing");
        } else if (stock.getId() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock ID can not be smaller than 1");
        } else if (stockRepository.existsById(stock.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase with this ID has been done already");
        }
    }

    private void validateStockPricePerShare(Stock stock) {
        if (stock.getPricePerShare() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock price per share is missing");
        } else if (!(stock.getPricePerShare() > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock price per share has to be bigger than zero");
        }
    }

    private void validateStockVolumeAcquired(Stock stock) {
        if (stock.getVolumeAcquired() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock volume acquired is missing");
        } else if (!(stock.getVolumeAcquired() > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock volume acquired has to be bigger than zero");
        }
    }

    public List<Stock> validateAllStocks(List<Stock> stocks) {
        int problematicEntryNumber = 0;
        List<Stock> validStocks = new ArrayList<>();
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            problematicEntryNumber = i + 1;
            try {
                Stock validStock = isStockValid(stock);
                validStocks.add(validStock);
            } catch (ResponseStatusException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Problem with Stock nr " + problematicEntryNumber
                        + " " + exception.getReason());
            }
        }
        return validStocks;
    }
}

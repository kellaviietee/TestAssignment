package niilo.investment.statistics;

import lombok.Data;
import niilo.investment.stock.Stock;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@Data
public class MonthlyExpenditureDTO {

    private Month month;
    private BigDecimal totalCost;

    public MonthlyExpenditureDTO(List<Stock> stocks, Month month) {
        totalCost = stocks.stream()
                .map(stock -> BigDecimal.valueOf(stock.getPricePerShare() * stock.getVolumeAcquired()))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        this.month = month;
    }
}

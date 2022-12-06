package niilo.investment.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Month;
import java.util.List;

@Data
@AllArgsConstructor
public class MonthlyAcquiringRecords {

    Month month;
    List<MonthlyStockDTO> acquiredStocks;
}

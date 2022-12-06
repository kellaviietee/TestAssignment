package niilo.investment.statistics;

import lombok.Data;
import niilo.investment.employee.Employee;
import niilo.investment.stock.Stock;

import java.time.LocalDate;

@Data
public class MonthlyStockDTO {

    private String companyName;
    private String shareName;
    private String shareISINCode;
    private String country;
    private String fieldOfEconomicActivity;
    private LocalDate date;
    private Double price;
    private Long volume;
    private Double totalPrice;
    private Employee employee;

    public MonthlyStockDTO(Stock stock) {
        this.companyName = stock.getShareData().getCompanyName();
        this.shareName = stock.getShareData().getShareName();
        this.shareISINCode = stock.getShareData().getShareISINCode();
        this.country = stock.getShareData().getCountry();
        this.fieldOfEconomicActivity = stock.getShareData().getFieldOfEconomicActivity();
        this.date = stock.getDate();
        this.price = stock.getPricePerShare();
        this.volume = stock.getVolumeAcquired();
        this.totalPrice = stock.getPricePerShare() * stock.getVolumeAcquired();
        this.employee = stock.getEmployee();
    }
}

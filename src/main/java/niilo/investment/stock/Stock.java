package niilo.investment.stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import niilo.investment.employee.Employee;
import niilo.investment.sharedata.ShareData;

import java.time.LocalDate;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @Column(name = "id")
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    private ShareData shareData;
    @Column(name = "price_per_share")
    private Double pricePerShare;
    @Column(name = "volume_acquired")
    private Long volumeAcquired;
    @Column(name = "Date")
    private LocalDate date;
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;
}

package niilo.investment.sharedata;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "share_data")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ShareData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "share_name")
    private String shareName;
    @Column(name = "share_ISIN_code")
    private String shareISINCode;
    @Column(name = "country")
    private String country;
    @Column(name = "field_of_economic_activity")
    private String fieldOfEconomicActivity;
}

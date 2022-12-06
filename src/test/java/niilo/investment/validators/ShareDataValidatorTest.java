package niilo.investment.validators;

import niilo.investment.sharedata.ShareData;
import niilo.investment.sharedata.ShareDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ShareDataValidatorTest {

    @Mock
    ShareDataRepository shareDataRepository;
    @InjectMocks
    ShareDataValidator shareDataValidator;
    @BeforeEach
    public void setUp(){
        openMocks(this);
    }

    @Test
    void testValidShareData(){
        ShareData validShareData = new ShareData(1L,"First Company","FCPY",
                "EEFE4592FA96","Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        shareDataValidator.validateShareData(validShareData);
    }

    @Test
    void testInvalidShareDataId(){
        ShareData invalidShareData = new ShareData(1L,"First Company","FCPY",
                "EEFE4592FA96","Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(true);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data ID already exists in database");
    }

    @Test
    void ShareDataCompanyNameIsNull(){
        ShareData invalidShareData = new ShareData(1L,null,"FCPY",
                "EEFE4592FA96","Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data company name is missing");
    }
    @Test
    void ShareDataCompanyNameIsBlank(){
        ShareData invalidShareData = new ShareData(1L," ","FCPY",
                "EEFE4592FA96","Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data company name is missing");
    }

    @Test
    void ShareDataShareNameIsNull(){
        ShareData invalidShareData = new ShareData(1L,"First Company",null,
                "EEFE4592FA96","Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data share name is missing");
    }

    @Test
    void ShareDataShareNameIsBlank(){
        ShareData invalidShareData = new ShareData(1L,"First Company"," ",
                "EEFE4592FA96","Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data share name is missing");
    }

    @Test
    void ShareDataISINCodeIsNull(){
        ShareData invalidShareData = new ShareData(1L,"First Company","FCPY",
                null,"Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data ISIN code is missing");
    }

    @Test
    void ShareDataISINCodeIsInvalid(){
        ShareData invalidShareData = new ShareData(1L,"First Company","FCPY",
                "E4FE4592FA96","Estonia","Financial");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data ISIN code is not matching with standard");
    }

    @Test
    void ShareDataFieldOfEconomicActivityIsNull(){
        ShareData invalidShareData = new ShareData(1L,"First Company","FCPY",
                "EEFE4592FA96","Estonia",null);
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data field of economic activity is missing");
    }

    @Test
    void ShareDataFieldOfEconomicActivityIsBlank(){
        ShareData invalidShareData = new ShareData(1L,"First Company","FCPY",
                "EEFE4592FA96","Estonia"," ");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data field of economic activity is missing");
    }

    @Test
    void ShareDataCountryIsNull(){
        ShareData invalidShareData = new ShareData(1L,"First Company","FCPY",
                "EEFE4592FA96",null,"Timber");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data country is missing");
    }

    @Test
    void ShareDataCountryIsBlank(){
        ShareData invalidShareData = new ShareData(1L,"First Company","FCPY",
                "EEFE4592FA96"," ","Timber");
        when(shareDataRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> {
            shareDataValidator.validateShareData(invalidShareData);
        }).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share data country is missing");
    }

}
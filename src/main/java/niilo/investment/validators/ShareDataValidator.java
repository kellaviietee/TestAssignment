package niilo.investment.validators;

import niilo.investment.sharedata.ShareData;
import niilo.investment.sharedata.ShareDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ShareDataValidator {

    @Autowired
    private ShareDataRepository shareDataRepository;

    public void validateShareData(ShareData shareData) {
        validateShareDataId(shareData);
        validateShareDataCompanyName(shareData);
        validateShareDataShareName(shareData);
        validateShareDataISINCode(shareData);
        validateShareDataCountry(shareData);
        validateShareDataFieldOfEconomicActivity(shareData);

    }

    private void validateShareDataFieldOfEconomicActivity(ShareData shareData) {
        if (shareData.getFieldOfEconomicActivity() == null || shareData.getFieldOfEconomicActivity().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Share data field of economic activity is missing");
        }
    }

    private void validateShareDataCountry(ShareData shareData) {
        if (shareData.getCountry() == null || shareData.getCountry().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Share data country is missing");
        }
    }

    private void validateShareDataISINCode(ShareData shareData) {
        if(shareData.getShareISINCode() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Share data ISIN code is missing");
        } else if (!shareData.getShareISINCode().matches("[A-Z]{2}[A-Z0-9]{9}[0-9]")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Share data ISIN code is not matching with standard");
        }
    }

    private void validateShareDataId(ShareData shareData) {
        if (shareDataRepository.existsById(shareData.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Share data ID already exists in database");
        }
    }

    private void validateShareDataCompanyName(ShareData shareData) {
        if (shareData.getCompanyName() == null || shareData.getCompanyName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Share data company name is missing");
        }
    }

    private void validateShareDataShareName(ShareData shareData) {
        if (shareData.getShareName() == null || shareData.getShareName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Share data share name is missing");
        }
    }
}

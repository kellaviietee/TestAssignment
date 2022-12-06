package niilo.investment.sharedata;

import lombok.AllArgsConstructor;
import niilo.investment.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping(path = "/api")
public class ShareDataController {

    @Autowired
    Service service;

}

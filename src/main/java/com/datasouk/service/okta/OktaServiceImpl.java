package com.datasouk.service.okta;

import com.datasouk.core.dto.okta.UserProfileDto;
import com.datasouk.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author kalaivani
 * Service for okta users
 */
@Service
public class OktaServiceImpl implements OktaService {

    private static Logger logger = LoggerFactory.getLogger(OktaServiceImpl.class);

    @Value("${okta.domainUrl}")
    private String domainUrl;

    @Value("${okta.apiToken}")
    private String apiToken;

    private final RestTemplate restTemplate;

    @Autowired
    public OktaServiceImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserProfileDto getUserProfileById(String userId) throws ServiceException {
        ResponseEntity<UserProfileDto> response = null;

        String URL = this.domainUrl + "api/v1/users/" + userId;
        logger.info("URL to fetch the current user : " + URL);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","SSWS " + this.apiToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            response = restTemplate.exchange(URL, HttpMethod.GET, request, UserProfileDto.class);
            if(response.getStatusCode() == HttpStatus.OK)
                logger.info("Got User profile successfully");

        } catch (Exception ex) {
            logger.error("Error while getting user profile " + " => " + ex.getMessage());

        }

        return response.getBody();
    }
}

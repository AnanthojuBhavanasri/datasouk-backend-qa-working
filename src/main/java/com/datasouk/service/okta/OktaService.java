package com.datasouk.service.okta;

import com.datasouk.core.dto.okta.UserProfileDto;
import com.datasouk.core.exception.ServiceException;

/**
 * @author kalaivani
 * Interface to define Okta service
 */
public interface OktaService {

    /**
     * Get user profile based on the userId
     * @param userId
     * @return userProfile
     * @throws ServiceException
     */
    UserProfileDto getUserProfileById(String userId) throws ServiceException;
}

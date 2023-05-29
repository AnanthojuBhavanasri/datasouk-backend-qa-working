package com.datasouk.service.arango.userProfile;

import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.okta.UserProfile;

/**
 * @author kalaivani
 * Interface to define arango service calls
 */
public interface UserProfileService {

    /**
     *
     * @param userProfile
     * @return userProfile created
     */
    UserProfile save(UserProfile userProfile) throws ServiceException;

    boolean getById(String id) throws ServiceException;
}

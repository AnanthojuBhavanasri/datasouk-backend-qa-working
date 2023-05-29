package com.datasouk.service.arango.userProfile;

import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.okta.UserProfile;
import com.datasouk.core.repository.UserProfileRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author kalaivani Service Implementation for arangodocument operations
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

  private static Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);
  private final UserProfileRepository userProfileRepository;

  public UserProfileServiceImpl(final UserProfileRepository userProfileRepository) {
    this.userProfileRepository = userProfileRepository;
  }

  @Override
  public UserProfile save(final UserProfile userProfile) throws ServiceException {
    UserProfile userProfileCreated = userProfileRepository.save(userProfile);
    return userProfileCreated;
  }

  @Override
  public boolean getById(String id) throws ServiceException {
    boolean userStatus = false;
    Optional<UserProfile> userProfile = userProfileRepository.findById(id);
    if (userProfile != null) {
      userStatus = true;
    }
    return userStatus;
  }
}

package com.datasouk.controller;

import com.datasouk.core.dto.okta.UserProfileDto;
import com.datasouk.core.models.okta.UserProfile;
import com.datasouk.service.arango.userProfile.UserProfileService;
import com.datasouk.service.okta.OktaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kalaivani
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final OktaService oktaService;

  private final UserProfileService userProfileService;

  private final DozerBeanMapper mapper;


  @CrossOrigin(origins = "*")
  @PostMapping("/okta/user/{id}")
  @Tag(name = "User", description = "Api's Related User Profile Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Posting the okta user"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<Void> storeOktaUserProfile(@PathVariable final String id) throws Exception {

    if (id == null) {
      throw new Exception("user id cannot be null");
    }

    UserProfileDto userProfileDto = oktaService.getUserProfileById(id);
    if (userProfileDto.getId() == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    logger.info("Received user profile from okta client");
    if (userProfileService.getById(id)) {
      logger.info("user profile is not available and going to store the okta user");
      userProfileService.save(mapper.map(userProfileDto, UserProfile.class));
    }

    return ResponseEntity.ok().build();
  }

}

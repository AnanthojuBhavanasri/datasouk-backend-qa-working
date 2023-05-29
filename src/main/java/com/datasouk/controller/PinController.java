package com.datasouk.controller;


import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.dto.search.NodeSearchResponse;
import com.datasouk.service.arango.node.PinCollectionServiceImpl;
import com.datasouk.service.arango.node.PinNodeServiceImpl;
import com.datasouk.service.arango.node.TeamsServiceImpl;
import com.datasouk.utils.dto.request.PinPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/pin")
public class PinController {

  private final PinNodeServiceImpl pinNodeService;
  private final PinCollectionServiceImpl pinCollectionServiceImpl;

  private final TeamsServiceImpl teamServiceImpl;

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/getNodeDetails")
  @Operation(summary = "returns search Node response")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Nodes Details Posted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Nodes Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public NodeSearchResponse nodeDetails(@RequestBody PayLoad payLoad) {
    return pinNodeService.node(payLoad);
  }


  @CrossOrigin(origins = "*")
  @PostMapping(value = "/getPinCollectionByKey")
  @Operation(summary = "returns getPinCollection using key")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Returned By Key"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public NodeSearchResponse getPinCollectionByKey(@RequestBody PinPayload payLoad)
      throws IOException {

    return pinCollectionServiceImpl.pinCollection(payLoad);
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/allTeams")
  @Operation(summary = "returns all Teams")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "all Teams"),
          @ApiResponse(responseCode = "400", description = "Invalid supplied"),
          @ApiResponse(responseCode = "404", description = "all Teams data Not Found"),
          @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getAllTeams()
          throws IOException {

    return teamServiceImpl.allTeams();
  }
}

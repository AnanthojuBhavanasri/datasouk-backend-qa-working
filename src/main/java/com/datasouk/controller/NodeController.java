package com.datasouk.controller;


import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.core.dto.search.AutoCompleteResponse;
import com.datasouk.core.models.arango.ActualDataNode;
import com.datasouk.core.models.arango.Node;
import com.datasouk.dto.search.*;
import com.datasouk.service.arango.node.AutoCompleteServiceImpl;
import com.datasouk.service.arango.node.NodeServiceImpl;
import com.datasouk.utils.dto.request.PinPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/nodes")
public class NodeController {


  private final NodeServiceImpl nodeService;
  private final AutoCompleteServiceImpl autoCompleteService;

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/search")
  @Operation(summary = "returns search Node response")
  @Tag(name = "Search", description = "Api's Related to Search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search Results Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Search Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public NodeSearchResponse search(@RequestBody PayLoad payLoad) {
    return nodeService.search(payLoad);
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/search-filters")
  @Operation(summary = "returns search filter count")
  @Tag(name = "Search", description = "Api's Related to Search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search Results with Filters  Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Search Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public SearchResultsCount searchFilter(@RequestBody PayLoad payLoad) {
    return nodeService.searchFilters(payLoad);
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/autoComplete")
  @Operation(summary = "returns autoComplete results")
  @Tag(name = "AutoComplete", description = "Api's to Related to AutoComplete actions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "autoComplete values Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<AutoCompleteResponse> autoComplete(@RequestParam String value) {
    return autoCompleteService.getAutoCompleteNodes(value);
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/viewHistory")
  @Operation(summary = "returns search Node detailed response")
  @Tag(name="viewNode",description = "Api's used to view Node details")
  public NodeDetailSearchResponse nodeViewer(@RequestParam String id){
    return nodeService.nodeInfo(id);
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/moreFromSourceSystem")
  @Operation(summary = "returns nodes from source system")
  @Tag(name="viewNode",description = "Api's used to return source system")
  public NodeSearchResponse sourceSystemNode(@RequestBody PinPayload payLoad){
    return nodeService.nodeSourceSystemInfo(payLoad);
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/examples")
  @Operation(summary = "returns nodes from source system")
  @Tag(name="Node",description = "Api's used to return source system")
  public ActualNodeResponse  exampleNode(@RequestParam String key){
    return nodeService.nodeExamplesInfo(key);
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/inprogressNodes")
  @Operation(summary = "returns nodes from source system")
  @Tag(name="Node",description = "Api's used to return source system")
  public NodeSearchResponse  progressNode(@RequestBody PinPayload payLoad){
    return nodeService.nodeProgressInfo(payLoad);
  }

}

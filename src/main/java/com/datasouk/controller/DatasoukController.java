package com.datasouk.controller;

import com.datasouk.core.exception.NotFound;
import com.datasouk.core.exception.ProcessFailedException;
import com.datasouk.core.exception.ServiceUnavailable;
import com.datasouk.core.models.arango.Node;
import com.datasouk.integration.restclient.alation.AlationRestClient;
import com.datasouk.service.AQL;
import com.datasouk.service.DGC2ArangoDB;
import com.datasouk.service.Dgc2ArangoDBCommunityDomain;
import com.datasouk.service.DocumentsClassification;
import com.datasouk.service.GcpAccess;
import com.datasouk.service.UserRegistration;
import com.datasouk.service.arango.connect.ConnectArango;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class DatasoukController {

  private static Logger logger = LoggerFactory.getLogger(DatasoukController.class);
  @Autowired
  AlationRestClient alationService;
  @Autowired
  GcpAccess gcpAccess;
  @Autowired
  private DGC2ArangoDB dgc2arangodb;
  @Autowired
  private Dgc2ArangoDBCommunityDomain dgc2arangodbcommunitydomain;
  @Autowired
  private AQL aql;
  @Autowired
  private UserRegistration ur;
  @Autowired
  private DocumentsClassification dc;
  @Autowired
  private ConnectArango ca;

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getNodesByViewId")
  @Operation(summary = "used to store the meta data in arangoDB using viewId")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "return nodes By viewid"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  public String nodesByView(@RequestParam String viewId) throws IOException {

    String response = null;
    try {
      response = dgc2arangodb.exportdgc2Arango(viewId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getcommunity")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Return communities"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String nodesByCommunity() throws IOException {

    String response = null;
    try {
      response = dgc2arangodbcommunitydomain.exportdgc2Arangocd();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/autoComplete")
  @Operation(summary = "used to completes the node names")
  @Tag(name = "AutoComplete", description = "Api's to Related to AutoComplete actions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "AutoComplete Values Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Object apiAutoCompleteService(@RequestParam String value) throws IOException {
    Object response = null;
    try {
      response = aql.autoComplete(value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myRecentTags")
  @Operation(summary = "getRecentTags")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "RecentTags Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getRecentTags() throws IOException {
    List<Object> response;
    response = aql.getRecentrTags();
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myViewNodeTags")
  @Operation(summary = "getViewNodeTags")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User View Node Tags Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Tags Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getmyViewNodeTags(@RequestParam String Tag) throws IOException {
    List<Object> response;
    response = aql.getViewNodeTags(Tag);
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/nodes")
  @Operation(summary = "returns search Node response")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search Node Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Nodes Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> postApi2Service(@RequestParam String nodeName,
      @RequestParam String nodeType) {
    List<HashMap> response = null;
    try {
      response = aql.nodes(nodeName, nodeType);
      logger.info(nodeName);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/recentTrend")
  @Operation(summary = "returns recent And Trend results")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Recent Trend Data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Recent Trend data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Object recentAndTrendSearch() throws IOException {

    Object response = null;
    try {
      response = aql.recentTrendNodes1();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myRecommandedNodes")
  @Operation(summary = "returns RecommandedNodes details")
  @Tag(name = "Search", description = "Api's Related to Search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Recommanded Nodes details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getRecommandedNodes() throws IOException {
    List<Object> response;
    response = aql.getMyRecommandedNodesList();
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @Operation(summary = "returns search NodeListView response")
  @GetMapping(value = "/nodesListView")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Node List View Details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getListViewOfNodes(@RequestParam String nodeName,
      @RequestParam String nodeType) {
    List<HashMap> response = null;
    try {
      response = ca.nodesListView(nodeName, nodeType);
      logger.info(nodeName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @Operation(summary = "returns nodetypedropdown")
  @GetMapping(value = "/nodeTypesDropDown")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Node Type dropdown list Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Node Type dropdown Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getnodeTypesDropDown() {
    List<Object> response = null;
    try {
      response = ca.nodeTypesdropDown();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/filters")
  @Operation(summary = "returns filter details of nodes")
  @Tag(name = "Filter", description = "Api's to Related to Filters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List Of Filters Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Filter Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Object nodeFilterPageSearch() throws IOException {

    Object response = null;
    try {
      response = ca.searchPageFilters();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/nodesFilters")
  @Operation(summary = "returns exact,single,multiword matches")
  @Tag(name = "Filter", description = "Api's to Related to Filters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Exact,single,multiword matches Results Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getNodesByNames(@RequestParam String nodeFilter, @RequestParam String name)
      throws IOException, NullPointerException {
    List<Object> response = null;

    if (nodeFilter.equals("OneWord")) {
      try {
        response = aql.nodeFilterSearchOneWord(name);
      } catch (org.apache.http.ParseException e) {
        e.printStackTrace();
      }
    } else if (nodeFilter.equals("AllWords")) {
      try {
        response = aql.nodeFilterSearchAllWord(name);
      } catch (org.apache.http.ParseException e) {
        e.printStackTrace();
      }
    } else if (nodeFilter.equals("ExactMatch")) {
      try {
        response = aql.nodeFilterSearchExactMatch(name);
      } catch (org.apache.http.ParseException e) {
        e.printStackTrace();
      }
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/nodesFiltersList")
  @Operation(summary = "returns exact,single,multiword matches")
  @Tag(name = "Filter", description = "Api's to Related to Filters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Exact,single,multiword matches FiltersList Results Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Filter Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getNodesByNamesList(@RequestParam String nodeFilter,
      @RequestParam String name) throws IOException, NullPointerException {
    List<Object> response = null;

    if (nodeFilter.equals("OneWord")) {
      try {
        response = aql.nodeFilterSearchOneWordList(name);
      } catch (org.apache.http.ParseException e) {
        e.printStackTrace();
      }
    } else if (nodeFilter.equals("AllWords")) {
      try {
        response = aql.nodeFilterSearchAllWordList(name);
      } catch (org.apache.http.ParseException e) {
        e.printStackTrace();
      }
    } else if (nodeFilter.equals("ExactMatch")) {
      try {
        response = aql.nodeFilterSearchExactMatchList(name);
      } catch (org.apache.http.ParseException e) {
        e.printStackTrace();
      }
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/viewHistory")
  @Operation(summary = "returns nodeViewer details")
  @Tag(name = "History", description = "Api's to view Nodes History")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "NodeViewer details& View History Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "View History Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap<String, ArrayList<Object>> viewHistory(@RequestParam String id)
      throws IOException {

    HashMap<String, ArrayList<Object>> response = null;
    try {
      response = aql.viewHistoryNodes(id);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/semantics")
  @Operation(summary = "returns semantis of pinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Semantics of pinCollection Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Semantics data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> semanticSearch(@RequestParam String pinId) {
    List<Object> response = null;
    try {
      response = aql.nodessemantics1(pinId);
      logger.info(pinId);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }

    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/classification")
  @Operation(summary = "returns classification of pinCollection")
  @Tag(name = "Classification", description = "Api's Related to classification/classificationStatusUpdate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Classification of pinCollection Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Classification data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> classificationSearch(@RequestParam String pinId) {
    List<Object> response = null;
    try {
      response = aql.nodesclassification(pinId);
      logger.info(pinId);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/classificationStatusUpdate")
  @Operation(summary = "returns classificationStatusUpdate")
  @Tag(name = "Classification", description = "Api's Related to classification/classificationStatusUpdate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Classification of pinCollection Status Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Classification data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> classificationStatusUpdate(@RequestParam String columnName,
      @RequestParam String recommededName, @RequestParam String status) {
    List<Object> response = null;
    try {
      response = aql.nodesclassificationStatusUpdate(columnName, recommededName, status);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/termsList")
  @Operation(summary = "returns list of terms")
  @Tag(name = "NewTerm Manual", description = "Api's to Related to Manual Term")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List of terms Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "List of terms Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> termList(@RequestParam String name) {
    List<String> response = null;
    try {
      response = aql.recommededTermsList(name);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/classificationTermsList")
  @Operation(summary = "returns list of terms")
  @Tag(name = "NewTerm Manual", description = "Api's to Related to Manual Term")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Classification Terms in List Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Classification Terms Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> classificationTermList() {
    List<String> response = null;
    try {
      response = aql.classificationSuggestedTermsList();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/semanticStatusUpdate")
  @Operation(summary = "returns semanticStatusUpdate")
  @Tag(name = "Semantics", description = "Api's Related to Semantics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "semantic Status Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> semanticStatusUpdate(@RequestParam String columnName,
      @RequestParam String recommededName, @RequestParam String status) {
    List<Object> response = null;
    try {
      response = aql.nodessemanticStatusUpdate(columnName, recommededName, status);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addNewTermManualInSemantics")
  @Operation(summary = "returns adding NewTermManualInSemantics")
  @Tag(name = "NewTerm Manual", description = "Api's to Related to Manual Term")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New TermManual Added In Semantics Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> manualUpdate(@RequestParam String columnName,
      @RequestParam String recommededName, @RequestParam String status) {
    List<String> response = null;
    try {
      response = aql.nodeManualUpdate(columnName, recommededName, status);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addNewTermManualInClasssification")
  @Operation(summary = "returns adding NewTermManualInClassification")
  @Tag(name = "NewTerm Manual", description = "Api's to Related to Manual Term")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New Term Manual Added In Classification Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> manualClassificationUpdate(@RequestParam String columnName,
      @RequestParam String recommededName, @RequestParam String status) {
    List<String> response = null;
    try {
      response = aql.nodeManualUpdateForClassification(columnName, recommededName, status);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/collectionSearch")
  @Operation(summary = "returns collectionSearch response")
  @Tag(name = "Search", description = "Api's Related to Search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search Collection Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Collection Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> collectionSearch(@RequestParam String nodeName) {
    List<String> response = null;
    try {
      response = aql.getcollectionSearch(nodeName);
      logger.info(nodeName);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/graphNodeFilter")
  @Operation(summary = "returns graph Node Filter response")
  @Tag(name = "Graph", description = "Api's Related to Graphs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Filtered Graph Node Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Graph Node Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> graphNodeFilterSearch(@RequestParam String nodeName) {
    List<HashMap> response = null;
    try {
      response = aql.graphFilters(nodeName);
      logger.info(nodeName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/metagraph")
  @Operation(summary = "returns metanodes classification")
  @Tag(name = "Graph", description = "Api's Related to Graphs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Meta Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Meta Nodes Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public void mgclassification() throws IOException {
    dc.groupByNodeTypes();
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/graph")
  @Operation(summary = "returns graph")
  @Tag(name = "Graph", description = "Api's Related to Graphs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Graph Node Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Graph Node Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Object graph(@RequestParam String nodeId, @RequestParam String depth) throws IOException {

    Object response = null;
    try {
      response = aql.graphQuery(nodeId, depth);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/graphQuerySearch")
  @Operation(summary = "returns graphSearch")
  @Tag(name = "Graph", description = "Api's Related to Graphs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Graph Query Search Results Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Graph Node Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Object graphSearchResults(@RequestParam String nodeName) throws IOException {

    Object response = null;
    try {
      response = aql.graphSearchQuery(nodeName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/dataelements")
  @Operation(summary = "returns dataElements response")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "DataElements Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "DataElements Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Object getRelationnByAssetId(@RequestParam String id)
      throws IOException, NullPointerException {
    List<HashMap> response = null;
    try {
      response = aql.dataElements(id);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

//	@CrossOrigin(origins = "*")
//	@GetMapping(value = "/nodesFilter")
//	@Operation(summary="returns exact,single,multiword matches")
//	public List<Object> getNodesByName(@RequestParam String name) throws IOException,NullPointerException{
//		List<Object> response = null;
//		try {
//			response = aql.nodeFilterSearch(name);
//		} catch (org.apache.http.ParseException e) {
//			e.printStackTrace();
//		}
//		return response;
//	}


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/versions")
  @Operation(summary = "returns version details")
  @Tag(name = "Version", description = "Api's to Related to versions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Version details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Version Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap<String, ArrayList<Object>> versions(@RequestParam String name) throws IOException {
    HashMap<String, ArrayList<Object>> response;
    response = ca.getVersions(name);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/moreFromUser")
  @Operation(summary = "returns moreFromUser node details")
  @Tag(name = "More", description = "Api's to Related to moreFromsourceSystem/moreFromUser")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "MoreFromUser Nodes details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "MoreFromUser data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap<String, ArrayList<Object>> moreFromUser(@RequestParam String userName)
      throws IOException {
    HashMap<String, ArrayList<Object>> response;
    response = ca.getmoreFromUser(userName);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/moreFromsourceSystem")
  @Operation(summary = "returns moreFromsourceSystem node details")
  @Tag(name = "More", description = "Api's to Related to moreFromsourceSystem/moreFromUser")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "returns moreFromsourceSystem node details"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap<String, ArrayList<Object>> moreFromsourceSystem(@RequestParam String sourceName)
      throws IOException {
    HashMap<String, ArrayList<Object>> response;
    response = ca.getmoreFromsourceSystem(sourceName);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/privacyAndRisk")
  @Operation(summary = "returns privacyAndRisk details")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "PrivacyAndRisk details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "PrivacyAndRisk Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getprivacyAndRisks(@RequestParam String Name) throws IOException {
    List<HashMap> response;
    response = ca.getprivacyAndRisks(Name);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/facets")
  @Operation(summary = "returns facets details")
  @Tag(name = "Facets", description = "Api's Related to facets")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Facets details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Facets data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> facets(@RequestParam String Name) throws IOException {
    List<Object> response;
    response = aql.getfacets(Name);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myRecentSearches")
  @Operation(summary = "returns myRecentSearches details")
  @Tag(name = "Search", description = "Api's Related to Search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "myRecentSearches Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "myRecentSearches Dataa Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getRecentSearches() throws IOException {
    List<Object> response;
    response = aql.getMyRecentSearchesList();
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myRelatedSearches")
  @Operation(summary = "returns myRelatedSearches details")
  @Tag(name = "Search", description = "Api's Related to Search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "myRelatedSearches Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "myRecentSearches Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getRelatedSearches(@RequestParam String Name) throws IOException {
    List<Object> response;
    response = aql.getMyRelatedSearchesList(Name);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myRelatedNodes")
  @Operation(summary = "returns myRelatedNodes details")
  @Tag(name = "Search", description = "Api's Related to Search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User Related Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "myRelatedNodes data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getRelatedNodes(@RequestParam List<String> Name) throws IOException {
    List<Object> response;
    response = aql.getMyRelatedNodesList(Name);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/similarSeaches")
  @Operation(summary = "returns similarSeaches details")
  @Tag(name = "Similarity", description = "Api's to Related to Similarity/similarSeaches")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Similar Searches Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Similar Searches data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getSimilarSeaches(@RequestParam List<String> Name) throws IOException {
    List<Object> response;
    response = aql.getMySimilarSeachesList(Name);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/similarity")
  @Operation(summary = "returns similarity details")
  @Tag(name = "Similarity", description = "Api's to Related to Similarity/similarSeaches")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Similarity data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Similarity data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getSimilaritySeaches(@RequestParam List<String> Name) throws IOException {
    List<Object> response;
    response = aql.getMySimilaritySeachesList(Name);
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myPopularTagsCount")
  @Operation(summary = "returns myPopularTagsCount")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User Related Popular Tags Count Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Popular Tags Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getPopularTagsCount() throws IOException {
    List<Object> response;
    response = aql.getMyPopularTags();
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myPopularTags")
  @Operation(summary = "returns myPopularTags")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User Related Popular Tags Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Popular Tags Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getPopularTags(@RequestParam String Tag) throws IOException {
    List<Object> response;
    response = aql.getPopularTags(Tag);
    return response;

  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/mytags")
  @Operation(summary = "store all tags in other collection")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "my tags stored in other collection"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "My Tags Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Object getTags() throws IOException {
    Object response;
    response = ca.getTagsInfo();
    return response;

  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/inTheCartForMyNodes")
  @Operation(summary = "returns addtocart details")
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User Cart Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Node Not Found in cart"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addtocart() throws IOException {
    List<Object> response;
    response = aql.getAddtoCart();
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/filterOperations")
  @Operation(summary = "returns filterOperations")
  @Tag(name = "Filter", description = "Api's to Related to Filters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Filter Operations Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getfiltersOperations(@RequestParam List<String> Filters) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.filtersOperations(Filters);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/savePreferences")
  @Operation(summary = "used to save the preferences")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "preferences details Stored in savePreferences"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> savePreferences(@RequestParam String savePreferencesHolder,
      @RequestParam String savePreferencesHolderId, @RequestBody HashMap filterValues) {
    List<Object> response = null;
    try {
      response = ca.getsavePreferences1(savePreferencesHolder, savePreferencesHolderId,
          filterValues);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getPreferences")
  @Operation(summary = "used to get saved the preferences")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Preferences details Returned from savePreferences"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "preferences Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getSavedPreferences(@RequestParam String savePreferencesHolder,
      @RequestParam String savePreferencesHolderId) {
    List<Object> response = null;
    try {
      response = ca.getsavedPreferences(savePreferencesHolder, savePreferencesHolderId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/deleteSavedPreferences")
  @Operation(summary = "used to delete saved the preferences")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "saved preferences details deleted from savePreferences"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "preferences Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> deleteSavedPreferences(@RequestParam String savedFilterId) {
    List<Object> response = null;
    try {
      response = ca.deletesavedPreferences(savedFilterId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/deleteFilterValues")
  @Operation(summary = "used to delete saved the preferences")
  @Tag(name = "Filter", description = "Api's to Related to Filters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Filter Values details deleted from Filters"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Filters Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> deleteSavedPreferencesValue(@RequestParam String savedFilterId,
      @RequestParam List<String> savedFiltervalue) {
    List<Object> response = null;
    try {
      response = ca.deletesavedPreferencesValues(savedFilterId, savedFiltervalue);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/updateNameInSavePreferences")
  @Operation(summary = "used to updateNameInSavePreferences")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Saved Preferences details updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Preferences data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> updateNameSavedPreferences(@RequestParam String savePreferencesHolderId,
      @RequestParam String savedFilterId, @RequestParam String savedFilterReName) {
    List<Object> response = null;
    try {
      response = ca.updatesavedPreferences(savePreferencesHolderId, savedFilterId,
          savedFilterReName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/editNameInSavePreferences")
  @Operation(summary = "used to editNameInSavePreferences")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Saved Preferences details edited"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Preferences data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> editNameSavedPreferences(@RequestParam String savePreferencesHolder,
      @RequestParam String savePreferencesHolderId, @RequestParam String savedFilterId) {
    List<Object> response = null;
    try {
      response = ca.editsavedPreferences(savePreferencesHolder, savePreferencesHolderId,
          savedFilterId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   * UserRegistration APIs
   **/
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/userRegistration")
  @Operation(summary = "Adds new User")
  @Tag(name = "User", description = "Api's Related User Profile Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New user is Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap<String, String> getUserRegister(
      @RequestBody Map<String, String> registerUserDetails) throws IOException {

    HashMap<String, String> response = null;
    try {
      response = ur.usersRegistration1(registerUserDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/registerUserDetails")
  @Operation(summary = "returns registerUserDetails")
  @Tag(name = "User", description = "Api's Related User Profile Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Return register User Details"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Register User Details Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getUserRegister(@RequestParam String identifier) throws IOException {

    List<Object> response = null;
    try {
      response = ur.getRegisterUsers(identifier);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getAllregisterUserDetails")
  @Operation(summary = "returns AllregisterUserDetails")
  @Tag(name = "User", description = "Api's Related User Profile Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All register Users Details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Register Users Details Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getAllUserRegister() throws IOException {

    List<Object> response = null;
    try {
      response = ur.getRegisterUsers();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myCollectionsDropDownList")
  @Operation(summary = "returns myCollectionsDropDownList Nodes")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User Collections DropDownLists Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "user Collections data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> myCollectionsDropDownList() throws IOException {

    List<Object> response = null;
    try {
      response = ca.getMyCollectionsDropDownList();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   * myNodes
   **/
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/createdByMe")
  @Operation(summary = "returns createdByMe Nodes")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created By Me Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Created By Me Nodes data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> createdByMe(@RequestParam String loginUser) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getcreatedByMe(loginUser);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/sharedWithMe")
  @Operation(summary = "returns sharedWithMe Nodes")
  @Tag(name = "Share", description = "Api's Related to Share")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Shared With Me Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Shared With Me data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> sharedWithMe(@RequestParam String loginId) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getsharedWithMe(loginId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/sharedByMe")
  @Operation(summary = "returns sharedByMe Nodes")
  @Tag(name = "Share", description = "Api's Related to Share")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Shared By Me Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Shared By Me data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> sharedByMe(@RequestParam String loginId) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getsharedByMe(loginId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinSharedByMe")
  @Operation(summary = "returns sharedByMe Nodes")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Shared By Me Nodes details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Shared By Me Nodes data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> pinSharedByMe(@RequestParam String loginId) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getPinSharedByMe(loginId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionSharedWithMe")
  @Operation(summary = "returns pinCollectionSharedWithMe")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Shared With Me details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Shared With Me data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> PinSharedWithMe(@RequestParam String loginId) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getPinSharedWithMe(loginId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   * pincollection APIs
   **/

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollections")
  @Operation(summary = "returns AllpinCollections nodes")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All Pin Collections details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collections data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getPinCollection() throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.pinCollection();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollection")
  @Operation(summary = "returns pinCollection nodes")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Nodes data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getPinCollections(@RequestParam String key) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.getPinCollectionsdetails(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removepinCollection")
  @Operation(summary = "removepinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Colection Removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String removePinCollection(@RequestParam String key) throws IOException {

    String response = null;
    try {
      response = ca.getRemovePinCollection(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/editpinCollection")
  @Operation(summary = "editpinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Edited Pin Collection data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> editPinCollection(@RequestBody HashMap pinDetails) throws IOException {

    List<String> response = null;
    try {
      response = ca.geteditPinCollection(pinDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/updatePinCollection")
  @Operation(summary = "editPostCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Data Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> editPostCollection(@RequestParam String key, @RequestBody HashMap pindetails)
      throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.geteditPostPinCollection(key, pindetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/simplePostPinCollections")
  @Operation(summary = "simplepostpinCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Simple Pin Collections Posted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> simplePostPinCollection(@RequestBody HashMap pinDetails) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.simplePostPincollection(pinDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/matrixPostPinCollections")
  @Operation(summary = "matrixpostpinCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Matrix Pin Collections Posted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> matrixPostPinCollection(@RequestBody HashMap pinDetails) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.matrixPostPincollection(pinDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionHeaders")
  @Operation(summary = "returns pinCollectionDetails using createdBy")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Headers Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Headers data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getPinCollectionHeaders(@RequestParam String createdById,
      @RequestParam String order, @RequestParam String pinFilter) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getPinCollectionHeaderskeys(createdById, order, pinFilter);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myCollections")
  @Operation(summary = "returns pinCollectionDetails")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Headers Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Headers data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getMyCollections(@RequestParam String createdById, @RequestParam String order,
      @RequestParam String pinFilter) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getMyCollectionKeys(createdById, order, pinFilter);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/createdByYouCollections")
  @Operation(summary = "returns createdByYouCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Headers Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Headers data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getCreatedByYouCollections(@RequestParam String createdById,
      @RequestParam String order, @RequestParam String pinFilter) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getCreatedByCollections(createdById, order, pinFilter);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getPinCollectionByKey")
  @Operation(summary = "returns getPinCollection using key")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Returned By Key"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap<String, List<Object>> getPinCollectionByKey(@RequestParam String key)
      throws IOException {

    HashMap<String, List<Object>> response = null;
    try {
      response = ca.getPinCollectionNodekeys(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionsHeaders")
  @Operation(summary = "returns AllPinCollectionDetails")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Headers Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Headers data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getAllPinCollectionHeaders() throws IOException {

    List<Object> response = null;
    try {
      response = ca.getPinCollectionHeaders();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionRoles")
  @Operation(summary = "returns pinCollectionRoles")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Roles Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Roles data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getPinCollectionRoles(@RequestParam String key) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.getPinCollectionRoles(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionTypeRoles")
  @Operation(summary = "returns pinCollectionRoles using Type")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Type Roles Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Type Roles data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getNodeTypePinCollectionRoles() throws IOException {

    List<Object> response = null;
    try {
      response = ca.getnodeTypePinCollectionRoles(); // not added to documnet of collection api check once
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionCategories")
  @Operation(summary = "returns pinCollectionCategories")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Categories Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection  Categories data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getPinCollectionCategories(@RequestParam String Type) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getPinCollectionCategories(Type);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionCategoriesType")
  @Operation(summary = "returns pinCollectionCategories using Type")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Categories Type Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection  Categories Type data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getPinCollectionCategoriesType(@RequestParam String nodeType)
      throws IOException {

    List<Object> response = null;
    try {
      response = ca.getPinCollectionCategoriesType(nodeType);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addRoles")//to store all nodes in categorylist
  @Operation(summary = "addRoles")
  @Tag(name = "Roles", description = "Api's Related to User Roles")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Roles Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addRolesToPinCollection(@RequestParam List<String> key) throws IOException {

    List<Object> response = null;
    try {
      response = ca.addPinCollectionRoles(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getpinCollectins")
  @Operation(summary = "get pinCollectins details")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All Pin Collection details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getPinCollectionUsingKey(@RequestParam String key) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.getPinCollections(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getpinNodes")
  @Operation(summary = "returns pinNodes details")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Nodes Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getPinNodes(@RequestParam String key) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.getPinNodes(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/autoCompleteTag")
  @Operation(summary = "returns autoCompleteTag")
  @Tag(name = "AutoComplete", description = "Api's to Related to AutoComplete actions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "AutoComplete Tag Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> apiAutoCompleteTagService(@RequestParam String value) throws IOException {
    List<Object> response = null;
    try {
      response = aql.autoCompleteTag(value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/autoCompleteRoles")
  @Operation(summary = "returns autoCompleteRoles")
  @Tag(name = "AutoComplete", description = "Api's to Related to AutoComplete actions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "AutoComplete Roles Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> apiAutoCompleteRolesService(@RequestParam String value) throws IOException {
    List<Object> response = null;
    try {
      response = aql.autoCompleteRole(value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/sharedBy")
  @Operation(summary = "returns sharedBy nodes")
  @Tag(name = "Share", description = "Api's Related to Share")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SharedBy Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getsharedNodes(@RequestParam String key) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getsharedcollections(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/updatedOn")
  @Operation(summary = "returns updatedOn nodes")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "UpdatedOn Nodes Data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getupdatedNodes() throws IOException {

    List<Object> response = null;
    try {
      response = ca.getupdatedcollections();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getAllDetailsOfPinCollections")
  @Operation(summary = "returns AllDetailsOfPinCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All Details Of PinCollections Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getAllPinCollectionDetails(@RequestParam String key) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getAllPinCollectionsdetails(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removePinNodesFromPinCollection")
  @Operation(summary = "used to removePinNodesFromPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Nodes From Pin Collection Removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String removePinNodesFromPinCollection(@RequestParam String key,
      @RequestBody HashMap nodekeys) throws IOException {

    String response = null;
    try {
      response = ca.removePinNodesFromPinCollections(key, nodekeys);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removePinCollectionFromPinCollection")
  @Operation(summary = "used to removePinCollectionFromPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection From Pin Collections Removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String removePinCollectionFromPinCollection(@RequestParam String key,
      @RequestBody HashMap nodekeys) throws IOException {

    String response = null;
    try {
      response = ca.removePinCollectionFromPinCollections(key, nodekeys);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/getRecentCollections")
  @Operation(summary = "used to getRecentPinCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Recent PinCollections Details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})

  public List<Object> recentPinCollection() throws IOException {

    List<Object> response = null;
    try {
      response = ca.getRecentPinCollection();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addPinCollectionToPinCollection")
  @Operation(summary = "used to addPinCollectionToPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Added to Pin Collection"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String addPinCollectionToPinCollection(@RequestParam String key,
      @RequestBody String pinDetails) throws IOException {

    String response = null;
    try {
      response = ca.addPinCollectionFromPinCollections(key, pinDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addPinNodeToPinCollection")
  @Operation(summary = "used to addPinNodeToPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Node Added to Pin Collection"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String addPinNodesToPinCollection(@RequestParam String key, @RequestBody String pinDetails)
      throws IOException {

    String response = null;
    try {
      response = ca.addPinNodesToPinCollections(key, pinDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/movePinNodeToPinCollection")
  @Operation(summary = "used to movePinNodeToPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Node is Moved to Pin Collection"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String movePinNodesToPinCollection(@RequestParam String to, @RequestParam String from,
      @RequestBody HashMap nodepinkey) throws IOException {

    String response = null;
    try {
      response = ca.movePinNodesToPinCollections(to, from, nodepinkey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/movePinCollectionToPinCollection")
  @Operation(summary = "used to movePinCollectionToPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection is Moved to Pin Collection"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String movePinColectionToPinCollection(@RequestParam String to, @RequestParam String from,
      @RequestBody HashMap nodepinkey) throws IOException {

    String response = null;
    try {
      response = ca.movePinCollectionToPinCollections(to, from, nodepinkey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/copyPinNodeToPinCollection")
  @Operation(summary = "used to copyPinNodeToPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Node is Copyed Pin Collection"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String copyPinNodesToPinCollection(@RequestParam String to, @RequestParam String from,
      @RequestBody HashMap nodepinkey) throws IOException {

    String response = null;
    try {
      response = ca.copyPinNodesToPinCollections(to, from, nodepinkey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/copyPinCollectionToPinCollection")
  @Operation(summary = "used to copyPinCollectionToPinCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection is Copyed Pin Collection"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String copyPinColectionToPinCollection(@RequestParam String to, @RequestParam String from,
      @RequestBody HashMap nodepinkey) throws IOException {

    String response = null;
    try {
      response = ca.copyPinCollectionToPinCollections(to, from, nodepinkey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinCollectionFilters")
  @Operation(summary = "return pinCollectionFilters")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collection Filters Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> pinFilters(@RequestParam List<String> pinFilters) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.pinFilters(pinFilters);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/pinNodes")
  @Operation(summary = "return pinNodes")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Node Returned By nodeName"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin node Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> postPinApi2Service(@RequestParam String nodeName) {
    List<HashMap> response = null;
    try {
      response = aql.pinNodes(nodeName);
      logger.info(nodeName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   * pin Team simple creation && Add section
   **/
  //To get the Teams list we are using this API
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/Teams")
  @Operation(summary = "get teams list")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Teams list details returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Team List Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> teamslist() throws IOException {

    List<Object> response = null;
    try {
      response = ca.getTeams();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getTeamDetails")
  @Operation(summary = "get team details")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Team Details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Teams Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String team(@RequestParam String teamId) throws IOException {

    String response = null;
    try {
      response = ca.getTeamDetails(teamId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/deleteSimpleTeam")
  @Operation(summary = "removeTeamRoles")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Simple Taem Deleted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Teams Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> removeRoles(@RequestParam String teamId) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getRemoveRoles(teamId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/editTeamDetails")
  @Operation(summary = "edit team details")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Edited Tem Details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Teams Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> editteam(@RequestParam String teamId) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getEditTeamDetails1(teamId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/updateSimpleTeam")
  @Operation(summary = "updateTeam")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Simple Team Details Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Teams Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addUsersInRoles(@RequestBody HashMap teamdetails) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.addUpdateTeam(teamdetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/updateMatrixTeam")
  @Operation(summary = "updateMatrixTeam")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Matrix Team Details Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Teams Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> updateMatrixTeam(@RequestBody HashMap teamdetails) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.addUpdateMatrixTeam(teamdetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  //Create New Team
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createNewTeamForSimple")
  @Operation(summary = "createNewTeamForSimple")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New Simple Team is Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Teams Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> createNewTeam(@RequestBody HashMap teamDetails) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getcreateNewTeam1(teamDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  // Add roles for rolesList with users to the roles
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addRole")
  @Operation(summary = "addRole")
  @Tag(name = "Roles", description = "Api's Related to User Roles")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Role Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addRole(@RequestBody HashMap details) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.getAddRoles(details);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/Roles")
  @Operation(summary = "getRoles")
  @Tag(name = "Roles", description = "Api's Related to User Roles")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All Roles Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Roles Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getRoles() throws IOException {

    List<Object> response = null;
    try {
      response = ca.getRole();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createNewCollection")
  @Operation(summary = "createNewCollection")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New Collection Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collection Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addSectionTeamForSimple(@RequestBody HashMap simpleDetails)
      throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.addSectionTeamForSimple(simpleDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createAddSection")
  @Operation(summary = "createAddSection")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "AddSection Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addSectionCollection(@RequestParam String key,
      @RequestBody HashMap simpleDetails) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.addSubCollection(key, simpleDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getContextInfo")
  @Operation(summary = "gettingContextInfo")
  @Tag(name = "Context", description = "Api's Related to Context")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Context Info Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Context Info Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> gettingContextInfo(@RequestParam String contextName, @RequestParam String key)
      throws IOException {

    List<Object> response = null;
    try {
      response = ca.getContextInfo(contextName, key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   * pin Matrix Team creation && Add section
   **/

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addProfileCategories")//to store all nodes in categorylist
  @Operation(summary = "addProfileCategories")
  @Tag(name = "Categories", description = "Api's Related to Categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profile Categories Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Categories Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addPinCollectionProfileCategories(@RequestBody HashMap details)
      throws IOException {

    List<Object> response = null;
    try {
      response = ca.addProfileCategories(details);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addCategories")//to store all nodes in categorylist
  @Operation(summary = "addCategories")
  @Tag(name = "Categories", description = "Api's Related to Categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Categories Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Categories Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addPinCollectionCategories(@RequestBody HashMap details) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.addPinCollectionCategories(details);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/mySharedNodesCommunity")
  @Operation(summary = "mySharedNodesCommunity")
  @Tag(name = "Share", description = "Api's Related to Share")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Nodes Added mySharedNodesCommunity"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "mySharedNodesCommunity Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> mySharedNodesCommunity(@RequestBody HashMap shareDetails)
      throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.mySharedNodesCommunityList1(shareDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createMatrixAddSection")
  @Operation(summary = "createMatrixAddSection")
  @Tag(name = "Add Section", description = "Api's Related to AddSection for simple/Matrix")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "MatrixAddSection Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "MatrixAddSection Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> creatematrixTeam(@RequestBody HashMap pinDetails) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.getcreateMatrixTeam(pinDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getMatrixTeamDetails")
  @Operation(summary = "get Matrix team details")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "MatrixTeamDetails Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "MatrixTeamDetails Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String teamMatrix(@RequestParam String teamId) throws IOException {

    String response = null;
    try {
      response = ca.getMatrixTeamDetails1(teamId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createNewMatrixTeam")
  @Operation(summary = "createNewMatrixTeam")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New Matrix Team Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Matrix Team Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> createNewMatrixTeam(@RequestBody HashMap teamDetails) throws IOException {

    List<Object> response = null;
    try {
      response = ca.addcreateNewMatrixTeam(teamDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addingNewContextToMatrixTeam")
  @Operation(summary = "addingNewContextToMatrixTeam")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New Context Added  To Matrix Team"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> createNewContextTomatrixTeam(@RequestBody HashMap teamDetails)
      throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.getNewContextMatrixTeam(teamDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/removeContext")
  @Operation(summary = "removeContext")
  @Tag(name = "Context", description = "Api's Related to Context")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Context Removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Context Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> removeContext(@RequestParam String teamId, @RequestParam String context)
      throws IOException {

    List<Object> response = null;
    try {
      response = ca.removeContext(teamId, context);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/editContext")
  @Operation(summary = "editContext")
  @Tag(name = "Context", description = "Api's Related to Context")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Edited Context Data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Context Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String editMatrixContext(@RequestParam String teamId, @RequestParam String context)
      throws IOException {

    String response = null;
    try {
      response = ca.getEditMatrixContext(teamId, context);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/removeCategories")
  @Operation(summary = "removeCategories")
  @Tag(name = "Categories", description = "Api's Related to Categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Categories removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Categories Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> removeCategories(@RequestParam String teamId,
      @RequestParam String contextName, @RequestParam List<String> categoryId) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.removeCategories(teamId, contextName, categoryId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addCategoriesToContext")
  @Operation(summary = "addCategoriesToContext")
  @Tag(name = "Context", description = "Api's Related to Context")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Categories Added to Context"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Categories Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addCategories(@RequestParam String teamId, @RequestParam String contextName,
      @RequestParam List<String> categoryId) throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.addCategoriesToContext(teamId, contextName, categoryId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/removeRolesFromMatrixTeam")
  @Operation(summary = "removeRoles")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Roles removed Matrix Team"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Matrix Team Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> removeRolesMatrixContext(@RequestParam String teamId,
      @RequestParam String roleName, @RequestParam String contextName) throws IOException {

    List<String> response = null;
    try {
      response = ca.removeMatrixRoles(teamId, roleName, contextName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/removeRoles")
  @Operation(summary = "removeRoles")
  @Tag(name = "Roles", description = "Api's Related to User Roles")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Roles removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = " Roles Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> editContext(@RequestParam String teamId, @RequestParam String roleName)
      throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.editRoles(teamId, roleName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @PostMapping("/uploadSimpleFile")
  @CrossOrigin(origins = "*")
  @Operation(summary = "To upload Simple File")
  @Tag(name = "FileUpload", description = "Api's Related to File Upload/download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Simple File Uploaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String uploadSimpleFile(@RequestParam("file") MultipartFile file)
      throws IOException, ParseException {

    String extension = file.getOriginalFilename().split("\\.")[1];
    String fileUploadResponse = null;
    if (extension.equals("xlsx") || extension.equals("csv")) {
      fileUploadResponse = aql.uploadExcelFile(file);
    }
    return fileUploadResponse;
  }


  @GetMapping("/downloadSimpleFile")
  @CrossOrigin(origins = "*")
  @Tag(name = "FileUpload", description = "Api's Related to File Upload/download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Simple File Downloaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<InputStreamResource> downloadSimpleFile()
      throws IOException, ParseException {
    //ByteArrayInputStream excelFilePath = null;
    ByteArrayInputStream excelFilePath = aql.downloadSimpleFile();
    HttpHeaders headers = new HttpHeaders();
    // set filename in header
    headers.add("Content-Disposition", "attachment; filename=userRoles.xlsx");
    return ResponseEntity
        .ok()
        .headers(headers)
        .body(new InputStreamResource(excelFilePath));
  }


  @GetMapping("/downloadMatrixFile")
  @CrossOrigin(origins = "*")
  @Tag(name = "FileUpload", description = "Api's Related to File Upload/download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Matrix File Downloaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<InputStreamResource> downloadMatrixFile()
      throws IOException, ParseException {
    //ByteArrayInputStream excelFilePath = null;
    ByteArrayInputStream excelFilePath = aql.downloadFile();
    HttpHeaders headers = new HttpHeaders();
    // set filename in header
    headers.add("Content-Disposition", "attachment; filename=userRoles.xlsx");
    return ResponseEntity
        .ok()
        .headers(headers)
        .body(new InputStreamResource(excelFilePath));
  }


  @PostMapping("/uploadMatrixFile")
  @CrossOrigin(origins = "*")
  @Tag(name = "FileUpload", description = "Api's Related to File Upload/download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Matrix File Uploaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String uploadMatrixFile(@RequestParam("file") MultipartFile file)
      throws IOException, ParseException {

    String extension = file.getOriginalFilename().split("\\.")[1];
    String fileUploadResponse = null;
    if (extension.equals("xlsx") || extension.equals("csv")) {
      fileUploadResponse = aql.uploadMatrixExcelFile(file);
    }
    return fileUploadResponse;
  }


  /**
   * Cart APIs
   **/

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/cartListForCheckOuts")
  @Operation(summary = "return cartListForCheckOuts")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart List for CheckOuts Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Cart List Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  public List<HashMap> getAddtoCart(@RequestParam String userName) {
    List<HashMap> response = null;
    try {
      response = aql.getCartList(userName);
      logger.info(userName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PatchMapping(value = "/saveForLater")
  @Operation(summary = "return saveForLaterListOfNodes")
  @Tag(name = "Save For Later", description = "Api's to Related to saveforlater")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Save For Later details Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getsaveForLater(@RequestParam String cartHolder, @RequestBody HashMap key) {
    List<HashMap> response = null;
    try {
      response = aql.getSaveForLater(cartHolder, key);
      logger.info(String.valueOf(key));
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PatchMapping(value = "/saveForLaterToMovetoCart")
  @Operation(summary = "return saveForLaterNodeToMovetoCartNode")
  @Tag(name = "Save For Later", description = "Api's to Related to saveforlater")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Moved savForLater to CartNode"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getaddtoCart(@RequestParam String cartHolder, @RequestBody HashMap key) {
    List<HashMap> response = null;
    try {
      response = aql.getSaveForLaterToAddToCart(cartHolder, key);
      logger.info(String.valueOf(key));
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/saveForLaterList")
  @Operation(summary = "return saveForLaterList")
  @Tag(name = "Save For Later", description = "Api's to Related to saveforlater")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SaveForLaterList details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "SaveForLaterList data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addToCartForSaveForLater(@RequestParam String cartHolder) {
    List<Object> response = null;
    try {
      response = aql.getaddToCartFromSaveForLater(cartHolder);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removeCartsFromSaveForLater")
  @Operation(summary = "return removeCartsFromSaveForLater")
  @Tag(name = "Save For Later", description = "Api's to Related to saveforlater")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Removed Carts From SaveForLater"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> removeFromSaveForLater(@RequestParam String cartHolder,
      @RequestBody HashMap key) {
    List<HashMap> response = null;
    try {
      response = aql.getRemoveCartsFromSaveForLater(cartHolder, key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addToCartForCheckouts")
  @Operation(summary = "return addToCartForCheckouts details")
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Added to Cart for Checkout"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addToCartFromSaveForLater(@RequestParam String cartHolder,
      @RequestParam String arangoNodeKey) {
    List<HashMap> response = null;
    try {
      response = aql.getaddToCartFromSavedForLaterList(cartHolder, arangoNodeKey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getCartNodes")
  @Operation(summary = "used to get Cart nodes")
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Cart Nodes Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getCartNodes(@RequestParam String cartHolder) {
    List<Object> response = null;
    try {
      response = aql.getcartHolderNodes(cartHolder);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removeCart")
  @Operation(summary = "removeCart")
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart Details Removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> removeFromShoppingCart(@RequestParam String userEmailId,
      @RequestBody HashMap nodekeys) {
    List<HashMap> response = null;
    try {
      response = aql.getremoveFromCheckOut(userEmailId, nodekeys);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/expiredOrdersDropDown")
  @Operation(summary = "expiredOrdersDropDown")
  @Tag(name = "Orders", description = "Api's to Related to  Users Orders")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Expired Orders DropDown Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Expired Orders Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> expiredOrder(String input, String userId) {
    List<HashMap> response = null;
    try {
      response = aql.getexpiredOrdersDropDown(input, userId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/yourOrdersDropDown")
  @Operation(summary = "yourOrdersWithInput")
  @Tag(name = "Orders", description = "Api's to Related to  Users Orders")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Expired Orders DropDown Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Expired Orders Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> yourOrders(String input, String userId) {
    List<HashMap> response = null;
    try {
      response = aql.getyourOrders(input, userId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/searchOrders")
  @Operation(summary = "searchOrders")
  @Tag(name = "Orders", description = "Api's to Related to  Users Orders")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search Orders Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Search Orders Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> searchOrders(String name) {
    List<HashMap> response = null;
    try {
      response = aql.getsearchOrders(name);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/ordersDropDownValues")
  @Operation(summary = "searchOrders")
  @Tag(name = "Orders", description = "Api's to Related to  Users Orders")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Orders DropDown Values Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Orders DropDown Values Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> ordersDropDownValues() {
    List<Object> response = null;
    try {
      response = aql.getOrdersDropDownValues();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/clearDataUsages")
  @Operation(summary = "clearDataUsages")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Data Usages Clared"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Usages Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> clearDataUsages() {
    List<Object> response = null;
    try {
      response = aql.getclearDataUsages();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removeSingleDataUsages")
  @Operation(summary = "removeSingleDataUsages")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Single DataUsages Removed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Usages Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> removeSingleDataUsage(String key) {
    List<HashMap> response = null;
    try {
      response = aql.getclearSingleDataUsages(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/clearCart")
  @Operation(summary = "clearCart")
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart data Cleared"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Cart Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> clearCart(@RequestParam String cartHolder) {
    List<HashMap> response = null;
    try {
      response = aql.getclearCart(cartHolder);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getDeliverypreferences")
  @Operation(summary = "getDeliverypreferences in checkout details")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delivery preferences Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Delivery preferences Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> deliverypreferences() {
    List<Object> response = null;
    try {
      response = aql.getDeliverypreferences();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getDataRefreshFrequency")
  @Operation(summary = "get DataRefreshFrequency in checkout details")
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Data Refresh Frequency Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Refresh Frequency Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> dataRefreshFrequency() {
    List<Object> response = null;
    try {
      response = aql.getDataRefreshFrequency();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getPriority")
  @Operation(summary = "get Priority in checkout details")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Priority Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getPriority() {
    List<Object> response = null;
    try {
      response = aql.getPriorityList();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getDeliveryPlatform")
  @Operation(summary = "get DeliveryPlatform in checkout details")
  @Tag(name = "Platform Availability", description = "Api's Related to platform Availability")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delivery Platform details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Delivery Platform list Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getDeliveryPlatform() {
    List<Object> response = null;
    try {
      response = aql.getDeliveryPlatformList();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/checkoutdetails")
  @Operation(summary = "checkoutdetails")
  @Tag(name = "Checkout-cart", description = "Api's to Related to Checkout-cart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Checkout details Posted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getcheckoutdetails(@RequestBody HashMap details) throws ServiceUnavailable {
    List<Object> response = null;
    try {
      response = aql.getcheckoutdetailsForm3(details);
      // gcpAccess.getAccessOnGcp(details);

    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  /* favourite APIs */
  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addNodeKeyToFavoriteNodes")
  @Operation(summary = "used to addNodeKeyToFavoriteNodes")
  @Tag(name = "Favourites", description = "Api's to Related to Favourite Node/Collection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Favorite Node Key added to FavoriteNodes"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "FavoriteNodes Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addToFavoriteNodes(@RequestParam String favHolder,
      @RequestBody HashMap arangoNodeKey) {
    List<HashMap> response = null;
    try {
      response = aql.getaddToFavories(favHolder, arangoNodeKey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addPinCollectionKeyToFavoriteCollections")
  @Operation(summary = "used to addPinCollectionKeyToFavoriteCollections")
  @Tag(name = "Favourites", description = "Api's to Related to Favourite Node/Collection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "PinCollection Key added to FavoriteCollections"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addToFavoritesCollection(@RequestParam String favHolder,
      @RequestBody HashMap arangoPinCollectionKey) {
    List<HashMap> response = null;
    try {
      response = aql.getaddToFavoritesCollection(favHolder, arangoPinCollectionKey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removePinCollectionKeyFromFavoriteCollections")
  @Operation(summary = "used to removePinCollectionKeyFromFavoriteCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "PinCollection Key Removed From FavoriteCollections"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "FavoriteCollections Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> removeFromFavoritesCollection(@RequestParam String favHolder,
      @RequestParam String arangoPinCollectionKey) {
    List<HashMap> response = null;
    try {
      response = aql.getremoveFromFavoritesCollection(favHolder, arangoPinCollectionKey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/removeArangoNodeKeyFromFavoriteNodes")
  @Operation(summary = "used to removeArangoNodeKeyFromFavoriteNodes")
  @Tag(name = "Favourites", description = "Api's to Related to Favourite Node/Collection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "ArangoNode Key Removed From FavoriteNodes"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "FavoriteNodes Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> removeFromFavoritesNodes(@RequestParam String favHolder,
      @RequestParam String arangoNodeKey) {
    List<HashMap> response = null;
    try {
      response = aql.getremoveFromFavoritesNodes(favHolder, arangoNodeKey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PatchMapping(value = "/updateFavorites")
  @Operation(summary = "used to updateFavorite nodes")
  @Tag(name = "Favourites", description = "Api's to Related to Favourite Node/Collection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Favorites Nodes Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "FavoriteNodes Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> updateFavorites(@RequestParam String favHolder,
      @RequestBody String arangoNodeKey) {
    List<HashMap> response = null;
    try {
      response = aql.getupdateFavorites(favHolder, arangoNodeKey);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getFavoriteNodes")
  @Operation(summary = "used to get Favorite nodes")
  @Tag(name = "Favourites", description = "Api's to Related to Favourite Node/Collection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All Favorites Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "FavoriteNodes Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getFavorites(@RequestParam String favHolder) {
    List<Object> response = null;
    try {
      response = aql.getFavoriteNodes(favHolder);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getFavoritePinCollections")
  @Operation(summary = "used to get Favorite nodes")
  @Tag(name = "Favourites", description = "Api's to Related to Favourite Node/Collection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All Favorites PinCollections Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "FavoritePinCollections Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getFavoriteCollection(@RequestParam String favHolder) {
    List<Object> response = null;
    try {
      response = aql.getFavoriteCollection(favHolder);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myworkUpdates")
  @Operation(summary = "myworkUpdates")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "myworkUpdates Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "myworkUpdates Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})

  public List<Object> myworkUpdates(@RequestParam String userName) {
    List<Object> response = null;
    try {
      response = ca.getmyworkUpdates(userName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/login")
  @Operation(summary = "used to login the user")
  @Tag(name = "User", description = "Api's Related User Profile Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login Details Posted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap<String, String> mylogin(@RequestBody HashMap loginDetails) {
    HashMap<String, String> response = null;
    try {
      response = ur.getmylogin(loginDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/tags")
  @Operation(summary = "used to post Tags")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tags Posted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> myTags(@RequestParam String id, @RequestBody HashMap tags) {
    List<HashMap> response = null;
    try {
      response = ca.getmyTags1(id, tags);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/tagsDropDown")
  @Operation(summary = "used to give Tags dropdown")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tags DropDown Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Tags DropDown Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> myTagsDropDown() {
    List<Object> response = null;
    try {
      response = ca.getmyTagsDropDown();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getTags")
  @Operation(summary = "used to give Tags")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All tags Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Tags Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> myGetTags(@RequestParam String id) {
    List<String> response = null;
    try {
      response = ca.gettingTags(id);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @DeleteMapping(value = "/deleteTags")
  @Operation(summary = "used to give Tags")
  @Tag(name = "Tag", description = "Api's Related to Tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tags Deleted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Tags Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> deleteTags(@RequestParam String id, @RequestParam String tagName) {
    List<Object> response = null;
    try {
      response = ca.delTags(id, tagName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @PostMapping(value = "/ratings")
  @Operation(summary = "used to give ratings")
  @Tag(name = "Ratings", description = "Api's Related to Ratings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ratings Posted"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> myRatings(@RequestParam String key, @RequestParam String userName,
      @RequestParam String userId, @RequestParam String ratings) {
    List<Object> response = null;
    try {
      response = ca.getmyRatings(key, userName, userId, ratings);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/ratingsInfo")
  @Operation(summary = "used to give ratings")
  @Tag(name = "Ratings", description = "Api's Related to Ratings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Rating Info Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Rating Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> myRatingsInfo(@RequestParam String key, @RequestParam String userId) {
    List<Object> response = null;
    try {
      response = ca.getmyRatingsInfo(key, userId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @PatchMapping(value = "/updateRatings")
  @Operation(summary = "used to give ratings")
  @Tag(name = "Ratings", description = "Api's Related to Ratings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ratings Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Ratings Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> UpdateRatingsInfo(@RequestParam String key, @RequestParam String userId,
      @RequestParam String ratings) {
    List<Object> response = null;
    try {
      response = ca.updatemyRatingsInfo(key, userId, ratings);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  //Graph search

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/graphSearch")
  @Tag(name = "Graph", description = "Api's Related to Graphs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get the graph Search  "),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ArrayList<Object> multilevelNodeSearch(@RequestParam String name)
      throws IOException, NullPointerException, ArrayIndexOutOfBoundsException {
    ArrayList<Object> response = null;
    try {
      response = aql.getmultilevelSearch(name);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/graphSearchListView")
  @Tag(name = "Graph", description = "Api's Related to Graphs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get the graph Search List View "),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ArrayList<Object> multilevelGraphSearch(@RequestParam String name)
      throws IOException, NullPointerException, ArrayIndexOutOfBoundsException {
    ArrayList<Object> response = null;
    //Pageable pageable1 name=page
    try {
      response = aql.getmultilevelGraphSearch(name);
      logger.info(String.valueOf(response));
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;

    //return response;
  }

  //preference APIs
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/yourPreferences")
  @Operation(summary = "yourPreferences")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "your Preferences Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getyourPreferences(@RequestParam String userName,
      @RequestBody HashMap preferenceDetails) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getyourPreferences(userName, preferenceDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getyourPreferences")
  @Operation(summary = "getYourPreferences")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User Preferences data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Preferences Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap yourPreferences(@RequestParam String userName) throws IOException {

    HashMap response = null;
    try {
      response = ca.yourPreferences(userName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @Operation(summary = "returns categoriestypedropdown")
  @GetMapping(value = "/categoriestypedropdown")
  @Tag(name = "Categories", description = "Api's Related to Categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "categories type dropdown Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "categories data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getcategoriesDropDown() {
    List<Object> response = null;
    try {
      response = ca.nodeCategoriesdropDown();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @Operation(summary = "returns business")
  @GetMapping(value = "/business")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "business Related Node Details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "business Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String getcategoriesBusiness() {
    String response = null;
    try {
      response = ca.nodeBusiness();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @Operation(summary = "returns Datadomain")
  @GetMapping(value = "/dataDomain")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Datadomain Nodes  details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Datadomain data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String getcategoriesDatadomain() {
    String response = null;
    try {
      response = ca.nodeDatadomain();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @Operation(summary = "returns Geography")
  @GetMapping(value = "/geography")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Geography data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Geography data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String getcategoriesGeography() {
    String response = null;
    try {
      response = ca.nodeGeography();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @Operation(summary = "returns Product")
  @GetMapping(value = "/product")
  @Tag(name = "DataProduct", description = "Api's to Create DataProduct")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "data Product details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Product data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String getcategoriesProduct() {
    String response = null;
    try {
      response = ca.nodeProduct();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/yourPreferencesProfileCategories")
  @Operation(summary = "yourPreferencesProfileCategories")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Add the  Your Preferences Profile Categories"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> profileCategories(@RequestBody HashMap categories) throws IOException {

    List<Object> response = null;
    try {
      response = ca.saveProfileCategories(categories);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getYourPreferencesProfileCategories")
  @Operation(summary = "yourPreferencesProfileCategories")
  @Tag(name = "Preferences", description = "api's related User Preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "It will returns the  Your Preferences Profile Categories"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String getProfileCategories(@RequestParam String userId) throws IOException {

    String response = null;
    try {
      response = ca.retriveProfileCategories(userId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/generalInformation")
  @Operation(summary = "generalInformation")
  @Tag(name = "User", description = "Api's Related User Profile Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User General Information Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getgeneralInformation(@RequestParam String userName,
      @RequestBody HashMap generalInformation) throws IOException {

    List<Object> response = null;
    try {
      response = ca.getgeneralInformation(userName, generalInformation);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getGeneralInformation")
  @Operation(summary = "getGeneralInformation")
  @Tag(name = "User", description = "Api's Related User Profile Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User General Information Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "General Information data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap generalInformation(@RequestParam String userName) throws IOException {

    HashMap response = null;
    try {
      response = ca.generalInformation(userName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/organizationFunctions")
  @Operation(summary = "get organizationFunctions")
  @Tag(name = "Organization Functions", description = "Api's Related to OrganizationFunctions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Organization Functions Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Organization data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getorganizationFunctions() {
    List<Object> response = null;
    try {
      response = aql.getorganizationFunctions();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addOrganizationFunctions")
  @Operation(summary = "Add organizationFunctions")
  @Tag(name = "Organization Functions", description = "Api's Related to OrganizationFunctions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Organization Functions Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Organization data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addOrganizationFunctions(@RequestBody HashMap value) {
    List<Object> response = null;
    try {
      response = aql.addOrganizationFunctions(value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/platformAvailability")
  @Operation(summary = "get platform availability")
  @Tag(name = "Platform Availability", description = "Api's Related to platform Availability")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Platform availability details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Platform availability data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String getPlatformAvailability(@RequestParam String id, @RequestParam String platform) {
    String response = null;
    try {
      response = aql.getplatformAvailability1(id, platform);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/platformAvailabilityDropDown")
  @Operation(summary = "get platform availability dropdown")
  @Tag(name = "Platform Availability", description = "Api's Related to platform Availability")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Platform availability dropdown details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Platform availability data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getPlatformAvailabilityDropDown() {
    List<Object> response = null;
    try {
      response = aql.getplatformAvailabilitydropdown();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   * Iteration 3
   **/
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getDataSources")
  @Operation(summary = "get Datasources")
  @Tag(name = "DataSources", description = "Api's to Get DataSources Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "DataSources Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "DataSources daata Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getDataSources() {
    List<Object> response = null;
    try {
      response = aql.getDatasources();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getDataSetFromDataSources")
  @Operation(summary = "getDataSetFromDataSources")
  @Tag(name = "DataSources", description = "Api's to Get DataSources Info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returned DataSet From DataSources"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "DataSet data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getDataSetsFromDatasource(@RequestParam String key) {
    List<Object> response = null;
    try {
      response = aql.getDataSets(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createDataProduct")
  @Operation(summary = "createDataProduct")
  @Tag(name = "DataProduct", description = "Api's to Create DataProduct")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Data Product Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String createDataProduct(@RequestParam String nodeName, @RequestParam String description) {
    String response = null;
    try {
      response = aql.getDataproducts(nodeName, description);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getNodeType")
  @Operation(summary = "getDataProduct")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "DataProduct Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Data Product Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getDataProduct(@RequestParam String type) {
    List<HashMap> response = null;
    try {
      response = aql.gettingDataproducts(type);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createEdgeBetweenDataProductAndDataSet")
  @Operation(summary = "createEdgeBetweenDataProductAndDataSet")
  @Tag(name = "Edge", description = "Api's to Create Edge Between DataProduct And DataSet")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Edge Created Between DataProduct And DataSet"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> createDataProductDataset(@RequestParam String dataProductId,
      @RequestBody HashMap dataSetDetails) {
    List<String> response = null;
    try {
      response = aql.getDataproductAndDataSetRelation(dataProductId, dataSetDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createAttributes")
  @Operation(summary = "createAttributes")
  @Tag(name = "Attributes", description = "Api's to Create Attributes")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "create the Attributes"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> createAttributes(@RequestParam String key,
      @RequestBody List<HashMap> attributedetails) {
    List<HashMap> response = null;
    try {
      response = aql.addAttributes(key, attributedetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createResponsibilities")
  @Operation(summary = "createResponsibilities")
  @Tag(name = "Responsibility", description = "Api's to Create Responsibility")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Responsibilities Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> createResponsibility(@RequestParam String key,
      @RequestBody HashMap details) {
    List<HashMap> response = null;
    try {
      response = aql.addResponsibility(key, details);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createRelation")
  @Operation(summary = "createRelation")
  @Tag(name = "Relation", description = "Api's to Create Relation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Related Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> createRelations(@RequestParam String key,
      @RequestBody List<HashMap> details) {
    List<String> response = null;
    try {
      response = aql.addRelations(key, details);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createNode")
  @Operation(summary = "createNode")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Node Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String createNode(@RequestParam String nodeName, @RequestParam String type,
      @RequestParam(name = "userId", required = false) String userId,
      @RequestParam(name = "description", required = false) String description) {
    String response = null;
    try {
      response = aql.addNodes(nodeName, type, userId, description);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getControls")
  @Operation(summary = "getControls")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Controls Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Controls Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> controls(@RequestParam List<String> nodes) {
    List<Object> response = null;
    try {
      response = aql.addControls(nodes);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @PostMapping("/uploadFile")
  @CrossOrigin(origins = "*")
  @Operation(summary = "Upload File")
  @Tag(name = "FileUpload", description = "Api's Related to File Upload/download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "File Uploaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String key)
      throws IOException, ParseException {

    String extension = file.getOriginalFilename().split("\\.")[1];
    String fileUploadResponse = null;
    if (extension.equals("xlsx") || extension.equals("csv")) {
      fileUploadResponse = aql.uploadFile(file, key);
    }
    return fileUploadResponse;
  }

  @GetMapping("/downloadFile")
  @CrossOrigin(origins = "*")
  @Operation(summary = "Download File")
  @Tag(name = "FileUpload", description = "Api's Related to File Upload/download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "File Downloaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "File Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<InputStreamResource> downloadJsonFile(@RequestParam String key)
      throws JsonProcessingException {

    ResponseEntity<InputStreamResource> response = null;
    try {
      response = aql.downloadJSONFile(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    System.out.println("response" + response);
    return response;
  }

//	@PostMapping("/jsonToExcelConverter")
//	@CrossOrigin(origins = "*")
//	public File jsonToExcelConverter(File srcFile, String targetFileExtension)
//			throws IOException, ParseException {
//
//			fileUploadResponse = aql.jsonFileToExcelFile(srcFile,targetFileExtension);
//		}
//		return fileUploadResponse;
//	}

  @PostMapping(value = "/uploadPinImage")
  @CrossOrigin(origins = "*")
  @Tag(name = "Image Upload", description = "Api's Related to Upload/Get Images")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Image Uploaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Image Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Path create(@RequestParam("file") MultipartFile file) throws IOException {
    StringBuilder fileNames = new StringBuilder();
    Path fileNameAndPath = Paths.get("pinImages", file.getOriginalFilename());
    fileNames.append(file.getOriginalFilename() + " ");
    try {
      Files.write(fileNameAndPath, file.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileNameAndPath;
  }

  @GetMapping(value = "/getPinImage")
  @CrossOrigin(origins = "*")
  @Operation(summary = "Get Pin Image")
  @Tag(name = "Image Upload", description = "Api's Related to Upload/Get Images")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Image Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Image Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<InputStreamResource> getImage(@RequestParam String file)
      throws IOException, ProcessFailedException {
    try {
      //  String fileContentType=file.getContentType();
      String[] format = file.split("\\.");
      String fileFormat = format[1].trim();
      if (fileFormat.equals("jpg")) {
        BufferedImage bImage = ImageIO.read(new File("pinImages/" + file));
        //  BufferedImage bImage = ImageIO.read(new File(file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        byte[] data = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(data);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(new InputStreamResource(is));
      } else if (fileFormat.equals("png")) {
        BufferedImage bImage = ImageIO.read(new File("pinImages/" + file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos);
        byte[] data = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(data);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(new InputStreamResource(is));
      } else if (fileFormat.equals("jpeg")) {
        BufferedImage bImage = ImageIO.read(new File("pinImages/" + file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpeg", bos);
        byte[] data = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(data);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(new InputStreamResource(is));
      } else {
        throw new ProcessFailedException(
            "File extension should be in .jpg (or) .png (or) jpeg format...");
      }
    } catch (Exception e) {
      throw new NotFound("image not found,please upload the image");
    }
  }

  @PostMapping(value = "/uploadProfileImage")
  @CrossOrigin(origins = "*")
  @Operation(summary = "Upload Profile Image")
  @Tag(name = "Image Upload", description = "Api's Related to Upload/Get Images")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profile Image Uploaded"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Profile Image Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public Path createProfile(@RequestParam("file") MultipartFile file, @RequestParam String userId)
      throws IOException, java.text.ParseException {
    StringBuilder fileNames = new StringBuilder();
    Path fileNameAndPath = Paths.get("profileImages", file.getOriginalFilename());
    fileNames.append(file.getOriginalFilename() + " ");
    try {
      Files.write(fileNameAndPath, file.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    aql.addImageUrl(file, userId);
    return fileNameAndPath;
  }

  @GetMapping(value = "/getProfileImage")
  @CrossOrigin(origins = "*")
  @ResponseBody
  @Operation(summary = "Get Profile Image")
  @Tag(name = "Image Upload", description = "Api's Related to Upload/Get Images")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profile Image Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Image Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<InputStreamResource> getProfileImage(@RequestParam String userId,
      @RequestParam String date) throws IOException, ProcessFailedException {

    //String fileContentType=file.getContentType();
    try {
      String file = "profileImages/" + aql.getPImage(userId);
      logger.info(file);
      String[] format = file.split("\\.");
      String fileFormat = format[1].trim();
      if (fileFormat.equals("jpg")) {
        // BufferedImage bImage = ImageIO.read(new File("profileImages/"+file));
        BufferedImage bImage = ImageIO.read(new File(file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        byte[] data = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(data);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(new InputStreamResource(is));
      } else if (fileFormat.equals("png")) {

        BufferedImage bImage = ImageIO.read(new File(file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos);
        byte[] data = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(data);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(new InputStreamResource(is));
      } else if (fileFormat.equals("jpeg")) {
        BufferedImage bImage = ImageIO.read(new File(file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpeg", bos);
        byte[] data = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(data);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(new InputStreamResource(is));
      } else {
        throw new ProcessFailedException(
            "File extension should be in .jpg (or) .png (or) jpeg format...");
      }
    } catch (Exception e) {
      throw new NotFound("image not found,please upload the image");
    }

  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getNodeDetails")
  @Operation(summary = "get NodeDetails")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Node Details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Node Details Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getNodeDetails() {
    List<HashMap> response = null;
    try {
      response = aql.getNodeNameAndId();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/favoriteNodesCount")
  @Operation(summary = "get favoriteNodesCount")
  @Tag(name = "Favourites", description = "Api's to Related to Favourite Node/Collection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Favorite Nodes Count Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Favorite Nodes Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<String> getfavoriteNodesCount(@RequestParam String key) {
    List<String> response = null;
    try {
      response = aql.favoriteNodesCount(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/displayPinCollectionsDropdown")
  @Operation(summary = "get displayPinCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collections Dropdown Displayed"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collections Dropdown Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> displayPinCollectionsDropDown() {
    List<Object> response = null;
    try {
      response = aql.displayPinCollectionDropdown();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/displayPinCollections")
  @Operation(summary = "get displayPinCollections")
  @Tag(name = "Pin", description = "Api's Related to Pin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pin Collections Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Pin Collections Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getdisplayPinCollections(@RequestParam String Order,
      @RequestParam String pinFilter) {
    List<Object> response = null;
    try {
      response = aql.displayPinCollections(Order, pinFilter);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/nodesSorting")
  @Operation(summary = "get displayPinCollections")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sorted Nodes Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getSortNodes(@RequestParam String order, @RequestParam String nodeFilter) {
    List<Object> response = null;
    try {
      response = aql.displaySortedNodes(order, nodeFilter);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @GetMapping(value = "/alationQueryResult")
  @Operation(summary = "get alation Query Result")
  @Tag(name = "Alation", description = "Api's to Related to Alation Harvest-bi/QueryResult")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful get the Alation Query Results"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<InputStreamResource> getQueryResult(@RequestParam int id)
      throws IOException {
    int latestQueryId = alationService.getLatestQueryId(id);
    logger.info("Query Latest Id : {}", latestQueryId);
    return alationService.getQueryResult(latestQueryId, id);
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/curateContext")
  @Operation(summary = "Add curateContext")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Curate Context Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Curate Context Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addCurateContext(@RequestParam String key, @RequestBody HashMap value) {
    List<Object> response = null;
    try {
      response = aql.addCurateContextInfo(key, value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getCurateContext")
  @Operation(summary = "Add curateContext")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Curate Context details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Curate Context data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap curateContext(@RequestParam String key) {
    HashMap response = null;
    try {
      response = aql.getCurateContextInfo(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/curateSecurity")
  @Operation(summary = "Add curateSecurity")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "curate Security Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addCurateSecurity(@RequestParam String key, @RequestBody HashMap value) {
    List<Object> response = null;
    try {
      response = aql.addCurateSecurityInfo(key, value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getcurateSecurity")
  @Operation(summary = "Add curateSecurity")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Curate Security Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Curate Security Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap addCurateSecurity(@RequestParam String key) {
    HashMap response = null;
    try {
      response = aql.getCurateSecurityInfo(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/curatePrivacy")
  @Operation(summary = "Add curatePrivacy")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Curate Privacy Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addCuratePrivacy(@RequestParam String key, @RequestBody HashMap value) {
    List<Object> response = null;
    try {
      response = aql.addCuratePrivacyInfo(key, value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getCuratePrivacy")
  @Operation(summary = "Add curatePrivacy")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Curate Privacy Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Curate Privacy Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap curatePrivacy(@RequestParam String key) {
    HashMap response = null;
    try {
      response = aql.getCuratePrivacyInfo(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/serviceLevelObjective")
  @Operation(summary = "Add serviceLevelObjective")
  @Tag(name = "Service", description = "Api's Related to service Level Objective")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "service Level Objective Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addServiceLevelObjective(@RequestParam String key,
      @RequestBody List<HashMap> value) {
    List<Object> response = null;
    try {
      response = aql.addServiceLevelObjectiveInfo(key, value);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getServiceLevelObjective")
  @Operation(summary = "Add serviceLevelObjective")
  @Tag(name = "Service", description = "Api's Related to service Level Objective")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "service Level Objective Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "ServiceLevelObjective Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> serviceLevelObjective(@RequestParam String key) {
    List<HashMap> response = null;
    try {
      response = aql.getServiceLevelObjectiveInfo(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getDataEntryDetails")
  @Operation(summary = "getDataEntryDetails")
  @Tag(name = "Curate", description = "Api's Related to Curate Feature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Curate Context details Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Curate Context data Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public HashMap dataEntryDetails(@RequestParam String key) {
    HashMap response = null;
    try {
      response = aql.getDataSetDataEntryRegistration(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/selectFields")
  @Operation(summary = "Add selectFields")
  @Tag(name = "Other api's", description = "Other api's")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SelectFields list returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "SelectFields Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> selectFields(@RequestParam String key) {
    List<Object> response = null;
    try {
      response = aql.getselectFields(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addNodeCategories")//to store all nodes in categorylist
  @Operation(summary = "addNodeCategories")
  @Tag(name = "Categories", description = "Api's Related to Categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Node Categories Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "NodeCategories Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> addNodesCategories(@RequestBody HashMap details) throws IOException {

    List<Object> response = null;
    try {
      response = ca.addNodesCategoriesList(details);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getNodeCategories")
  @Operation(summary = "getNodeCategories")
  @Tag(name = "Categories", description = "Api's Related to Categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "NodeCategories list Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "getNodeCategories not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> nodeCategories(@RequestParam String key) throws IOException {

    List<Object> response = null;
    try {
      response = ca.nodesCategories(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/usage")
  @Operation(summary = "getNodeUsage")
  @Tag(name = "Node", description = "Api's Related to Node")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Node Usage data Returned"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Node Usage data not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> nodeUsage(@RequestParam String key) throws IOException {

    List<Object> response = null;
    try {
      response = ca.nodesUsage(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/dataSetPicker")
  @Operation(summary = "getDatasetPicker")
  @Tag(name = "DataSet", description = "Api's Related to DataSet creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "dataSetPicker Added"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "DatasetPicker not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> nodeDatasetPicker(@RequestParam List<String> databaseName)
      throws IOException {

    List<Object> response = null;
    try {
      response = ca.nodesPicker(databaseName);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/updateTeamDetailsInPinCollection")
  @Operation(summary = "updateTeamDetailsInNode")
  @Tag(name = "Teams", description = "api's related Team creation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Team Details In Node Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "updateTeamDetailsInNode Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})

  public List<String> collectionTeam(@RequestParam String pinkey, @RequestParam String teamId)
      throws IOException {

    List<String> response = null;
    try {
      response = ca.collectionTeamIds(pinkey, teamId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/nodeSemantics")
  @Operation(summary = "nodeSemantics")
  @Tag(name = "Semantics", description = "Api's Related to Semantics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "nodeSemantics Created"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> nodeSemanticsInfo(@RequestBody HashMap columns) throws IOException {

    List<Object> response = null;
    try {
      response = ca.semanticNodes(columns);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/nodeClassification")
  @Operation(summary = "nodeClassification")
  @Tag(name = "Classification", description = "Api's Related to Classification")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "nodeSemantics Created"),
          @ApiResponse(responseCode = "400", description = "Invalid supplied"),
          @ApiResponse(responseCode = "404", description = "Not Found"),
          @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> nodeClassificationInfo(@RequestBody HashMap columns) throws IOException {

    List<Object> response = null;
    try {
      response = ca.classificationNodes(columns);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getDataProductTeamDetails")
  @Operation(summary = "get team details")
  @Tag(name = "Data Product", description = "Api is for getting the team details in Data product Teams page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Team Details In Node Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "updateTeamDetailsInNode Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String dataProductTeam(@RequestParam String nodeId) throws IOException {

    String response = null;
    try {
      response = ca.getDataProductTeamDetails(nodeId);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/updateTeamDetailsInNode")
  @Operation(summary = "updateTeamDetailsInNode")
  @Tag(name = "Data Product", description = "Api is for updating the team details in Data product page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Team Details In Node Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "updateTeamDetailsInNode Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> nodeTeam(@RequestParam String key, @RequestBody List<HashMap> teamDetails)
      throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.nodesTeamResponsibilities(key, teamDetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/privacyRisk")
  @Operation(summary = "privacyRisk")
  @Tag(name = "Data Product", description = "Api is for updating the team details in Data product page")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Team Details In Node Updated"),
          @ApiResponse(responseCode = "400", description = "Invalid supplied"),
          @ApiResponse(responseCode = "404", description = "updateTeamDetailsInNode Not found"),
          @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> nodePrivacyRisk(@RequestParam String key, @RequestParam String type)
          throws IOException {

    List<HashMap> response = null;
    try {
      response = ca.nodePrivacyRisk(key,type);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/removeProfileContext")
  @Operation(summary = "removeProfileContext")
  @Tag(name = "Profile", description = "Api is for removing profile context ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Team Details In Node Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "updateTeamDetailsInNode Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> removeProductContext(@RequestParam String profileId,
      @RequestParam String context) throws IOException {

    List<Object> response = null;
    try {
      response = ca.removeProfileContext(profileId, context);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/createProduct")
  @Operation(summary = "createProduct")
  @Tag(name = "Data Product", description = "Api is for getting Data Product and Data Set details ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Team Details In Node Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "updateTeamDetailsInNode Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public String createNode(@RequestBody HashMap details) {
    String response = null;
    try {
      response = aql.addProductNodes(details);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getProduct")
  @Operation(summary = "getProduct")
  @Tag(name = "Data Product", description = "Api is for getting Data Product and Data Set details ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Team Details In Node Updated"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "updateTeamDetailsInNode Not found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> getProductNode(@RequestParam String key) {
    List<Object> response = null;
    try {
      response = aql.productNodes(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/addingObjectives")
  @Operation(summary = "addingObjectives")
  @Tag(name = "Attributes", description = "Api's to addingObjectives")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "create the Attributes"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> addObjectives(@RequestBody List<HashMap> Objectdetails) {
    List<HashMap> response = null;
    try {
      response = aql.addingObjectives(Objectdetails);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/getObjectives")
  @Operation(summary = "getObjectives")
  @Tag(name = "Attributes", description = "Api's to getObjectives")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "create the Attributes"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> getObjectives() {
    List<HashMap> response = null;
    try {
      response = aql.gettingObjectives();
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }


  @CrossOrigin(origins = "*")
  @GetMapping(value = "/publishNodes")
  @Operation(summary = "to get the publish Nodes")
  @Tag(name = "Attributes", description = "Api's to publish Nodes")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "create the Attributes"),
          @ApiResponse(responseCode = "400", description = "Invalid supplied"),
          @ApiResponse(responseCode = "404", description = "Not Found"),
          @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<HashMap> publishNode(@RequestParam String key) {
    List<HashMap> response = null;
    try {
      response = aql.updatePublishNode(key);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/myteams")
  @Operation(summary = "to get the myTeams")
  @Tag(name = "Attributes", description = "Api's to my teams")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "create the teams"),
          @ApiResponse(responseCode = "400", description = "Invalid supplied"),
          @ApiResponse(responseCode = "404", description = "Not Found"),
          @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public List<Object> myTeams(@RequestParam String loginid) {
    List<Object> response = null;
    try {
      response = aql.getMyTeams(loginid);
    } catch (org.apache.http.ParseException e) {
      e.printStackTrace();
    }
    return response;
  }

}

package com.datasouk.service;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.datasouk.core.exception.FileStorageException;
import com.datasouk.core.exception.NotAcceptableException;
import com.datasouk.core.exception.ProcessFailedException;
import com.datasouk.core.exception.ServiceUnavailable;
import com.datasouk.core.models.arango.Node;
import com.datasouk.service.arango.connect.ArangoRestClient;
import com.datasouk.service.arango.connect.ConnectArango;
import com.datasouk.service.exportExcel.ExportService;
import com.datasouk.service.importExcel.ImportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
public class AQL {

  private static Logger logger = LoggerFactory.getLogger(AQL.class);
  Logger log = LoggerFactory.getLogger(AQL.class);
  List<String> columNames = new ArrayList<>();
  List<String> columNames1 = new ArrayList<>();
  List<String> columNames2 = new ArrayList<>();
  List<String> columNames3 = new ArrayList<>();
  List<String> columNames4 = new ArrayList<>();
  List<String> columNames5 = new ArrayList<>();
  List<Object> pages = new ArrayList<Object>();
  List<Object> listpages = new ArrayList<Object>();
  List<String> columNamesPlatform = new ArrayList<String>();
  @Autowired
  private ArangoRestClient arangorestclient;
  @Autowired
  private ConnectArango connectArango;
  @Autowired
  private Usage us;
  @Autowired
  private ExportService exportService;
  @Autowired
  private ImportService importService;
  @Value("${alation.aplicationurl}")
  private String alationAppicationUrl;
  @Value("${gcp.url}")
  private String gcpUrl;

  @Value("${serviceNow.url}")
  private String serviceNowUrl;

  @Value("${alation.url}")
  private String alationUrl;
  @Value("${arango.database}")
  private String arangodatabase;
  @Value("${arango.search.collection}")
  private String searchCollectionName;
  @Value("${arango.myrecentsearch.collection}")
  private String arangomyrecentsearchCollection;
  @Value("${arango.viewName}")
  private String viewName;
  @Value("${arango.tag.collection}")
  private String ViewTagSearchesCollection;
  @Value("${arango.nodes.collection}")
  private String nodesCollection;
  @Value("${arango.view.collection}")
  private String arangoViewCollection;
  @Value("${arango.category.collection}")
  private String arangocategoryCollection;
  @Value("${arango.tagsHistory.collection}")
  private String arangomySearchTagsCollection;
  @Value("${arango.nodetypes.collection}")
  private String arangoNodeTypesCollection;
  @Value("${arango.cart.collection}")
  private String Cart;
  @Value("${arango.shoppingCart.collection}")
  private String ShoppingCart;
  @Value("${arango.dataUsage.collection}")
  private String DataUsage;
  @Value("${arango.orders.collection}")
  private String Orders;
  @Value("${arango.ordersSearch.collection}")
  private String ordersSearchCollection;
  @Value("${arango.orderview.collection}")
  private String ordersView;
  @Value("${arango.expireOrders.collection}")
  private String expireOrders;
  @Value("${arango.dataUsageArchive.collection}")
  private String DataUsageArchiveCollection;
  @Value("${arango.searchType.collection}")
  private String arangoSearchTypeCollection;
  @Value("${arango.searchfilterHistory.collection}")
  private String arangoFilterSearchCollection;
  @Value("${arango.userRoles.collection}")
  private String userRoles;
  @Value("${arango.gcpDataUsage.collection}")
  private String GcpDataUsage;
  @Value("${arango.alationDataUsage.collection}")
  private String alationDataUsage;
  @Value("${arango.sampleCollection.collection}")
  private String sampleNodesCollection;
  @Value("${arango.userRegistration.collection}")
  private String userRegistration;
  private Path fileStorageLocation = null;
  @Value("${slack.owner}")
  private String owner;
  @Value("${slack.steward}")
  private String steward;

  @Value("${slack.url}")
  private String slackUrl;

  @Value("${teams.url}")
  private String teamsUrl;

  @Autowired
  private com.datasouk.core.utils.ArangoDB arangoDB;

  @Value("${arango.profileCategories.collection}")
  private String profileCategories;

  @Value("${arango.tagsCollection.collection}")
  private String tagsCollection;

  @Value("${arango.tagEdge.collection}")
  private String tagsEdges;


  public AQL() throws IOException {
    fileStorageLocation = Files.createTempDirectory("");
    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException(
          "Could not create the directory where the uploaded files will be stored.",
          ex);
    }
  }

  public Object recentTrendNodes1() {

    List<HashMap> response = new ArrayList<>();
    JSONObject recentTrendresponse = new JSONObject();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "Let recentSearches = (FOR doc IN ViewHistory\n"
        + "SORT doc.searchedOn DESC\n"
        + "LIMIT 10\n"
        + "RETURN doc.nodekey)\n"
        + "LET trendingSearches = (For doc in ViewHistory\n"
        + "FILTER doc.searchedOn <= DATE_SUBTRACT(DATE_NOW(), \"P10DT1H\")\n"
        + "COLLECT label = doc.nodekey WITH COUNT into nodesCount\n"
        + "SORT nodesCount DESC\n"
        + "LIMIT 10\n"
        + "RETURN {label,value: nodesCount})\n"
        + "Return {RecentSearchValues:recentSearches,TrendingSearchValues:trendingSearches}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while recentTrendNodes1 : " + e.getMessage().toString());
    }
    response.forEach(a -> {
      JSONObject nodeInfo = new JSONObject(a);
      List<HashMap> response1 = new ArrayList<>();
      JSONArray rs = nodeInfo.getJSONArray("RecentSearchValues");
      JSONArray ts = nodeInfo.getJSONArray("TrendingSearchValues");

      String queryToBeExecuted1 = "for a in Nodes\n"
          + "filter a.id in " + rs + "\n"
          + "return a";

      logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

      ArangoCursor<HashMap> cursor2 = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted1, HashMap.class);
        response1 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while recentTrendNodes1_2 : " + e.getMessage().toString());
      }

      JSONObject recentSearches = new JSONObject();
      //recentSearches.put("RecentSearchValues", connectArango.tailView(response1));
      recentTrendresponse.put("RecentSearchValues", connectArango.tailView(response1));
      JSONObject trendingSearches = new JSONObject();
      List<Object> tsearches = new ArrayList<>();
      for (int i = 0; i < ts.length(); i++) {
        JSONObject trnd = new JSONObject();
        JSONObject trendingObj = new JSONObject(ts.get(i).toString());
        String label = trendingObj.getString("label");
        int value = trendingObj.getInt("value");
        List<HashMap> response2 = new ArrayList<>();
        String query2 = "for a in Nodes\n"
            + "filter a.id == '" + label + "'\n"
            + "return a";

        logger.info("query2----->" + query2);

        ArangoCursor<HashMap> cursor3 = null;
        try {

          cursor3 = arangoDB.query(query2, HashMap.class);
          response2 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while recentTrendNodes1_3 : " + e.getMessage().toString());
        }

        ArrayList<Object> lab = connectArango.tailView(response2);
        //logger.info("lab"+lab.get(0));
        //trnd.put("label",connectArango.tailView(response2));
        if (!(lab.isEmpty())) {
          //trnd.put("label",lab.get(0));

          //trnd.put("value", value);
          //tsearches.add(trnd.toMap());
          tsearches.add(lab.get(0));
        }


      }

      recentTrendresponse.put("TrendingSearchValues", tsearches);

      //trendingSearches.put("TrendingSearchValues", trnd);

      //recentTrendresponse.add(trendingSearches.toMap());
    });

    return recentTrendresponse.toMap();
  }

  public Object autoComplete(String nodeName) {

    return connectArango.autoCompleteConnection(nodeName);
  }


  public List<Object> nodessemantics(String pinId) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<Object> cursor2 = null;
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor3 = null;
    String queryToBeExecuted2 = "for a in PinCollection\r\n"
        + "filter a._key == '" + pinId + "' AND a.pinNodes !=null\r\n"
        + "for b in a.pinNodes\r\n"
        + "filter b.type == \"Column\" AND b.arangokey !=null\r\n"
        + "return b.arangokey";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted2);

    try {

      cursor2 = arangoDB.query(queryToBeExecuted2, Object.class);
      response = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodessemantics : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<>();

    for (int i = 0; i < response.size(); i++) {
      String[] node = ((String) response.get(i)).split("/");
      columns.add("a._key == '" + node[1] + "'");
    }

    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted3 = "for a in PhysicalDataDictionary\r\n"
        + "filter " + columnIds + "\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response1 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodessemantics_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      JSONObject semanticsColumnName = new JSONObject();
      JSONObject semanticsColumnName1 = new JSONObject();
      //JSONObject semantics=new JSONObject();
      JSONObject semantics = new JSONObject();
      JSONObject semantics1 = new JSONObject();
      JSONArray semanticsList = new JSONArray();
      JSONObject s1 = new JSONObject(response1.get(i));
      if (s1.get("source").equals("Collibra")) {
        String name = s1.get("name").toString();
        semanticsColumnName.put("columnName", name);
        //semanticsColumnName1.put("columnName", name);

        semantics.put("columnName", name);
        String Id = s1.get("_id").toString();

        String queryToBeExecute = "for a in buslnkphy\r\n"
            + "filter a._to=='" + Id + "'\r\n"
            + "return a";
        logger.info("queryToBeExecuted----->" + queryToBeExecute);
        try {
          cursor3 = arangoDB.query(queryToBeExecute, HashMap.class);
          response3 = cursor3.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while nodessemantics_3 : " + e.getMessage().toString());
        }
        for (int j = 0; j < response3.size(); j++) {

          JSONObject s = new JSONObject(response3.get(j));
          if (s.has("confidenceScore")) {
            String confidenceScore = s.get("confidenceScore").toString();
            semantics.put("confidenceScore", confidenceScore);
            semantics.put("Status", "Sourced");
          }
          String id = s.getString("_from");

          String query = "for a in Business\r\n"
              + "filter a._id=='" + id + "'\r\n"
              + "return a";

          logger.info("queryToBeExecuted----->" + query);

          try {

            cursor3 = arangoDB.query(query, HashMap.class);
            response2 = cursor3.asListRemaining();

          } catch (Exception e) {
            log.error("Exception while nodessemantics_4 : " + e.getMessage().toString());
          }

          for (int k = 0; k < response2.size(); k++) {
            JSONObject l = new JSONObject(response2.get(k));
            if (l.has("name")) {
              String recommendedName = l.get("name").toString();
              semantics.put("recommendedName", recommendedName);
            } else if (l.has("suggestedNames")) {
              JSONArray suggestedName = l.getJSONArray("suggestedNames");
              semantics.put("SuggestedNames", suggestedName);
            }
          }
          responseList.add(semantics);

        }

        if (response3.isEmpty()) {

          String queryToBeExecuted = "for a in mllinkphy\r\n"
              + "filter a._to=='" + Id + "'\r\n"
              + "return a";

          logger.info("queryToBeExecuted----->" + queryToBeExecuted);

          try {

            cursor3 = arangoDB.query(queryToBeExecuted, HashMap.class);
            response3 = cursor3.asListRemaining();

          } catch (Exception e) {
            log.error("Exception while nodessemantics_5: " + e.getMessage().toString());
          }

          for (int j = 0; j < response3.size(); j++) {
            JSONObject s = new JSONObject(response3.get(j));
            if (s.has("confidenceScore")) {
              String confidenceScore = s.get("confidenceScore").toString();
              semantics.put("confidenceScore", confidenceScore);
              //semanticsList.add(semantics);
            }
            if (s.has("Status")) {
              String Status = s.get("Status").toString();
              semantics.put("Status", Status);
              //semanticsList.add(semantics);
            }
            if (s.has("Source")) {
              String Source = s.get("Source").toString();
              semantics.put("Source", Source);
              //semanticsList.add(semantics);
            }
            String id = s.getString("_from");

            String query = "for a in ML_collection\r\n"
                + "filter a._id=='" + id + "'\r\n"
                + "return a";

            logger.info("queryToBeExecuted----->" + query);

            try {

              cursor3 = arangoDB.query(query, HashMap.class);
              response2 = cursor3.asListRemaining();

            } catch (Exception e) {
              log.error("Exception while nodessemantics_6 : " + e.getMessage().toString());
            }

            //JSONObject l=new JSONObject();
            JSONArray snames = new JSONArray();
            for (int k = 0; k < response2.size(); k++) {
              JSONObject l = new JSONObject(response2.get(k));

              if (l.has("name")) {
                String recommendedName = l.get("name").toString();
                semantics.put("recommendedName", recommendedName);
                //semanticsList.add(semantics);

              } else if (l.has("suggestedNames") && l.has("Status")) {
                logger.info("no values to insert");
              } else {

                JSONArray suggestedName = l.getJSONArray("suggestedNames");
                //semantics.put("SuggestedName", suggestedName);
                snames.put(suggestedName);
              }

            }
            semanticsList.put(semantics.toMap());
          }
        }
        //semanticsColumnName.put("SuggestedNames",semanticsList);
        responseList.add(semanticsColumnName.toMap());
      }
    }
    return responseList;
  }

  public List<Object> nodessemantics1(String pinId) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> businessResponse = new ArrayList<>();
    List<HashMap> businessResponse1 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    String query = "for a in PinCollection\r\n"
        + "filter a._key == '" + pinId + "' AND a.pinNodes !=null\r\n"
        + "for b in a.pinNodes\r\n"
        + "filter b.type == \"Column\" AND b.arangoNodeKey !=null\r\n"
        + "return b.arangoNodeKey";
    logger.info("queryToBeExecuted----->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodessemantics1 : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<>();
    for (int i = 0; i < response.size(); i++) {
      String[] node = ((String) response.get(i)).split("/");
      columns.add("b.id == '" + node[1] + "'");
    }
    String columnIds = String.join(" OR ", columns);


//    String query2 = "for a in PhysicalDataDictionary\r\n"
//            + "filter " + columnIds + "\r\n"
//            + "return a";
    String query2 = "for a in PhysicalDataDictionary\n"
        + "for b in a.nodes\n"
        + "filter " + columnIds + "\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + query2);
    try {
      cursor1 = arangoDB.query(query2, HashMap.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodessemantics1_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      JSONObject semantics = new JSONObject();
      JSONObject semanticJsonSuggested = new JSONObject();
      JSONArray suggestedName = new JSONArray();
      JSONObject physicalJson = new JSONObject(response1.get(i));
          if(!physicalJson.has("source")){
      String name = physicalJson.getString("displayName");
      String Id = physicalJson.getString("_id");

      semantics.put("columnName", name);

      String queryToBeExecute = "for a in buslnkphy\r\n"
          + "filter a._to=='" + Id + "'\r\n"
          + "return a";
      logger.info("queryToBeExecuted----->" + queryToBeExecute);
      try {
        cursor1 = arangoDB.query(queryToBeExecute, HashMap.class);
        businessResponse = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while nodessemantics1_3 : " + e.getMessage().toString());
      }
      for (int j = 0; j < businessResponse.size(); j++) {

        JSONObject s = new JSONObject(businessResponse.get(j));
        if (s.has("confidence_score")) {
          String confidenceScore = s.get("confidence_score").toString();
          semantics.put("confidence_score", confidenceScore);
          semantics.put("Status", "Sourced");
        }
        String id = s.getString("_from");

        String querytoexecute = "for a in Business\r\n"
            + "filter a._id=='" + id + "'\r\n"
            + "return a";

        logger.info("queryToBeExecuted----->" + query);

        try {

          cursor1 = arangoDB.query(querytoexecute, HashMap.class);
          businessResponse1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while nodessemantics1_4 : " + e.getMessage().toString());
        }

        for (int k = 0; k < businessResponse1.size(); k++) {
          JSONObject l = new JSONObject(response2.get(k));
          if (l.has("name")) {
            String recommendedName = l.get("name").toString();
            semanticJsonSuggested.put("name", recommendedName);
            suggestedName.put(semanticJsonSuggested.toMap());
          } else if (l.has("Suggestednames")) {
            //JSONArray suggestedName=l.getJSONArray("suggestedNames");
            //semantics.put("SuggestedNames", suggestedName);

            JSONArray suggetName = l.getJSONArray("Suggestednames");
            for (int sb = 0; sb < suggetName.length(); sb++) {
              JSONObject g = new JSONObject(suggetName.get(sb).toString());
              logger.info(String.valueOf(g));
									/*String sname=g.get("name").toString();
								String confidenceScore=g.get("confidenceScore").toString();
								semanticJsonSuggested.put("recommendedName", sname);
								semanticJsonSuggested.put("confidenceScore", confidenceScore);
								suggestedName.put(semanticJsonSuggested.toMap());*/
              suggestedName.put(g.toMap());

            }

          }
          semantics.put("suggestedNames", suggestedName);
        }
        responseList.add(semantics.toMap());
      }

      if (businessResponse.isEmpty()) {

        String query3 = "for a in mllnkphy\r\n"
            + "filter a._to == '" + Id + "'\r\n"
            + "return a";
        logger.info("queryToBeExecuted----->" + query3);
        try {
          cursor1 = arangoDB.query(query3, HashMap.class);
          response2 = cursor1.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while nodessemantics1_5 : " + e.getMessage().toString());
        }
        for (int j = 0; j < response2.size(); j++) {
          JSONObject mlJson = new JSONObject(response2.get(j));
          String Id1 = mlJson.getString("_from");
          if (mlJson.has("confidence")) {
            String confidenceScore = mlJson.get("confidence").toString();
            semanticJsonSuggested.put("confidence_score", confidenceScore);
            semantics.put("confidence_score", confidenceScore);
          }
          if (mlJson.has("Status")) {
            String status = mlJson.get("Status").toString();
            if (status.contains("Accepted") || status.contains("accepted")) {

              if (mlJson.has("confidence_score")) {
                String confidenceScore = mlJson.get("confidence_score").toString();
                semanticJsonSuggested.put("confidence_score", confidenceScore);
                semantics.put("confidence_score", confidenceScore);
              }
              if (mlJson.has("Status")) {
                String Status = mlJson.get("Status").toString();
                semanticJsonSuggested.put("Status", Status);
                semantics.put("Status", Status);
              }
              if (mlJson.has("Source")) {
                String Source = mlJson.get("Source").toString();
                semanticJsonSuggested.put("Source", Source);
                semantics.put("Source", Source);
              }

              String query4 = "for a in ML_collection\r\n"
                  + "filter a._id =='" + Id1 + "'\r\n"
                  + "return a";

              logger.info("queryToBeExecuted----->" + query4);
              try {
                cursor1 = arangoDB.query(query4, HashMap.class);
                response3 = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while nodessemantics1_6 : " + e.getMessage().toString());
              }

              for (int k = 0; k < response3.size(); k++) {
                JSONObject mlcJson = new JSONObject(response3.get(k));
                if (mlcJson.has("name")) {
                  String recommendedName = mlcJson.get("name").toString();
                  semantics.put("name", recommendedName);
                }
              }

            }
          }
          if (mlJson.has("confidence_score")) {
            String confidenceScore = mlJson.get("confidence_score").toString();
            semanticJsonSuggested.put("confidence_score", confidenceScore);
          }
          if (mlJson.has("Status")) {
            String Status = mlJson.get("Status").toString();
            semanticJsonSuggested.put("Status", Status);
          }
          if (mlJson.has("Source")) {
            String Source = mlJson.get("Source").toString();
            semanticJsonSuggested.put("Source", Source);
          }

          String query4 = "for a in ML_collection\r\n"
              + "filter a._id =='" + Id1 + "'\r\n"
              + "return a";

          logger.info("queryToBeExecuted----->" + query4);
          try {
            cursor1 = arangoDB.query(query4, HashMap.class);
            response3 = cursor1.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while nodessemantics1_7 : " + e.getMessage().toString());
          }

          for (int k = 0; k < response3.size(); k++) {
            JSONObject mlcJson = new JSONObject(response3.get(k));
            if (mlcJson.has("name") && !mlcJson.has("Suggestednames")) {
              String recommendedName = mlcJson.get("name").toString();
              semanticJsonSuggested.put("name", recommendedName);
              suggestedName.put(semanticJsonSuggested.toMap());
            } else if (mlcJson.has("Suggestednames") && mlcJson.has("Status")) {
              logger.info("no values to insert");
            } else if (mlcJson.has("Suggestednames") && mlcJson.has("name")) {
              JSONArray suggestName = mlcJson.getJSONArray("Suggestednames");
              for (int s = 0; s < suggestName.length(); s++) {
                JSONObject l = new JSONObject(suggestName.get(s).toString());
                logger.info(String.valueOf(l));
										/*String sname=l.get("name").toString();
									String confidenceScore=l.get("confidenceScore").toString();

									semanticJsonSuggested.put("recommendedName", sname);
									semanticJsonSuggested.put("confidenceScore", confidenceScore);
									suggestedName.put(semanticJsonSuggested.toMap());*/
                suggestedName.put(l.toMap());

              }
            }
            semantics.put("suggestedNames", suggestedName);
          }
        }
        responseList.add(semantics.toMap());
      }
          }
    }
    return responseList;
  }

  public List<String> recommededTermsList(String name) {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<String> cursor2 = null;
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor3 = null;

    String queryToBeExecuted1 = "FOR doc IN mlView\r\n"
        + "SEARCH ANALYZER(doc.suggestedNames.name IN TOKENS('" + name
        + "','text_en'),'text_en')\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 1\r\n"
        + "RETURN {names:doc.suggestedNames[*].name}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    try {

      cursor = arangoDB.query(queryToBeExecuted1, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while recommededTermsList : " + e.getMessage().toString());
    }
    logger.info("response" + response);
    for (int a = 0; a < response.size(); a++) {
      JSONObject names = new JSONObject(response.get(a));
      logger.info("names" + names);
      JSONArray namesList = names.getJSONArray("names");
      logger.info("namesList" + namesList);
      for (int j = 0; j < namesList.length(); j++) {
        if (j == 11) {
          break;
        }
        columns.add(namesList.get(j).toString());
        logger.info("columns" + columns);

      }

    }

    String query = "FOR doc IN mlView\r\n"
        + "SEARCH ANALYZER(doc.name IN TOKENS('" + name + "','text_en'),'text_en')\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 10\r\n"
        + "RETURN doc.name";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    try {

      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while recommededTermsList_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response.size(); i++) {
      columns.add(response.get(i));
    }
    //
    //
    //			String query ="for a in ML_collection\r\n"
    //					+ "filter a.suggestedNames !=null AND a.source == \"Collibra\"\r\n"
    //					+ "for b in a.suggestedNames\r\n"
    //					+ "return b.name";
    //
    //			logger.info("queryToBeExecuted----->" + query);
    //
    //			try {
    //
    //				cursor = arangoDB.query(query, String.class);
    //				response1 = cursor.asListRemaining();
    //
    //			} catch (Exception e) {
    //				log.error("Exception while executing  Query: " + e.getMessage().toString());
    //			}
    //
    //			for(int i=0;i<response1.size();i++) {
    //				columns.add(response1.get(i));
    //			}

    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return columns1;

  }

  public Object graphQuery(String nodeId, String depth) {

    List<Object> response = new ArrayList<>();
    List<Object> res1 = new ArrayList<>();
    List<Object> res2 = new ArrayList<>();
    List<Object> res3 = new ArrayList<>();
    List<Object> res4 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    JSONObject graphResponse = new JSONObject();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String qry = "for a in Business\n"
        + "for b in a.nodes\n"
        + "filter b.id == '" + nodeId + "'\n"
        + "return a._id";
    logger.info("queryToBeExecuted----->" + qry);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(qry, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphQuery : " + e.getMessage().toString());
    }

    String query1 = "for a in Governance\n"
        + "for b in a.nodes\n"
        + "filter b.id == '" + nodeId + "'\n"
        + "return a._id";
    logger.info("queryToBeExecuted----->" + query1);

    try {

      cursor = arangoDB.query(query1, Object.class);
      res1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphQuery_2 : " + e.getMessage().toString());
    }

    String qry2 = "for a in LogicalData\n"
        + "for b in a.nodes\n"
        + "filter b.id == '" + nodeId + "'\n"
        + "return a._id";
    logger.info("queryToBeExecuted----->" + qry2);

    try {

      cursor = arangoDB.query(qry2, Object.class);
      res2 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphQuery_3 : " + e.getMessage().toString());
    }

    String query3 = "for a in PhysicalDataDictionary\n"
        + "for b in a.nodes\n"
        + "filter b.id == '" + nodeId + "'\n"
        + "return a._id";
    logger.info("queryToBeExecuted----->" + query3);

    try {

      cursor = arangoDB.query(query3, Object.class);
      res3 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphQuery_4 : " + e.getMessage().toString());
    }
    logger.info("response" + res3);
    String query4 = "for a in Technology\n"
        + "for b in a.nodes\n"
        + "filter b.id == '" + nodeId + "'\n"
        + "return a._id";
    logger.info("queryToBeExecuted----->" + query4);

    try {

      cursor = arangoDB.query(query4, Object.class);
      res4 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphQuery_5 : " + e.getMessage().toString());
    }
    logger.info("response" + res4);
    String id = null;
    if (!(response.isEmpty())) {
      for (int i = 0; i < response.size(); i++) {
        id = (String) response.get(0);
      }
    } else if (!(res1.isEmpty())) {
      for (int i = 0; i < res1.size(); i++) {
        id = (String) res1.get(0);
      }
    } else if (!(res2.isEmpty())) {
      for (int i = 0; i < res2.size(); i++) {
        id = (String) res2.get(0);
      }
    } else if (!(res3.isEmpty())) {
      for (int i = 0; i < res3.size(); i++) {
        id = (String) res3.get(0);
      }
    } else if (!(res4.isEmpty())) {
      for (int i = 0; i < res4.size(); i++) {
        id = (String) res4.get(0);
      }
    }
    String queryToBeExecuted = "For v,e,p in 1.." + depth + " ANY\r\n" + "\"" + id
        + "\"\r\n" + "Graph metaversgph1\r\n" + "return e";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphQuery_6 : " + e.getMessage().toString());
    }
    List<Object> nodes = new ArrayList<>();
    List<Object> links = new ArrayList<>();
    response1.forEach(g -> {
      JSONObject graph = new JSONObject(g);
      logger.info(String.valueOf(graph));
      String from = graph.getString("_from");
      String to = graph.getString("_to");
      String coRole = graph.getString("coRole");
      String[] fromSplit = from.split("/");
      String fromCollection = fromSplit[0];
      String fromKey = fromSplit[1];
      String[] toSplit = to.split("/");
      String toCollection = toSplit[0];
      String toKey = toSplit[1];

      List<HashMap> qresponse1 = new ArrayList<>();
      List<HashMap> response2 = new ArrayList<>();

      String query = "for a in " + fromCollection + "\r\n"
          + "filter a._key == '" + fromKey + "'\r\n"
          + "return {name:a.displayName,type:a.typeName}";
      logger.info("queryToBeExecuted----->" + query);

      ArangoCursor<HashMap> qcursor1 = null;
      try {

        qcursor1 = arangoDB.query(query, HashMap.class);
        qresponse1 = qcursor1.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while graphQuery_7 : " + e.getMessage().toString());
      }

      String query2 = "for a in " + toCollection + "\r\n"
          + "filter a._key == '" + toKey + "'\r\n"
          + "return {name:a.displayName,type:a.typeName}";
      logger.info("queryToBeExecuted----->" + query2);

      ArangoCursor<HashMap> cursor2 = null;
      try {

        cursor2 = arangoDB.query(query2, HashMap.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while graphQuery_8 : " + e.getMessage().toString());
      }
      JSONObject linksObject = new JSONObject();
      String sname = null;
      String tname = null;
      for (int i = 0; i < qresponse1.size(); i++) {
        JSONObject res = new JSONObject(qresponse1.get(i));
        JSONObject resname = new JSONObject();
        sname = res.getString("name");
        resname.put("path", sname);
        String type = res.getString("type");
        resname.put("type", type);
        nodes.add(resname.toMap());
      }

      for (int i = 0; i < response2.size(); i++) {
        JSONObject res = new JSONObject(response2.get(i));
        JSONObject resname = new JSONObject();
        tname = res.getString("name");
        resname.put("path", tname);
        String type = res.getString("type");
        resname.put("type", type);
        nodes.add(resname.toMap());
      }
      linksObject.put("source", sname);
      linksObject.put("target", tname);
      //linksObject.put("coRole", coRole);
      links.add(linksObject.toMap());

    });

    List<Object> UniqueNumbers
        = nodes.stream().distinct().collect(
        Collectors.toList());

    graphResponse.put("nodes", UniqueNumbers);
    graphResponse.put("links", links);

    return graphResponse.toMap();
  }

  @SuppressWarnings("rawtypes")
  public List<HashMap> nodes(String nodeName, String nodeType) {
    logger.info(nodeName);
    return connectArango.getNodesInfo(nodeName, nodeType);
  }

  @SuppressWarnings("rawtypes")
  public HashMap<String, ArrayList<Object>> viewHistoryNodes(String id) {
    logger.info(id);
    return connectArango.viewnodesinfo(id);
  }

  @SuppressWarnings("rawtypes")
  public List<HashMap> dataElements(String nodeId) {

    ArangoDB arangoConn = arangorestclient.getArangoConnection();

    List<String> response = new ArrayList<>();

    List<HashMap> response1 = new ArrayList<>();

    List<HashMap> response2 = new ArrayList<>();

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "For node in " + nodesCollection + "\r\n"
        + "filter node._key == \"" + nodeId + "\"\r\n" + "return node.relations";
    logger.info(queryToBeExecuted);

    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Relations are empty in Data Elements: " + "dataElements(String nodeId)" + e.getMessage()
              .toString());
    }

    List<String> columns = new ArrayList<String>();

    response.forEach(datasettargetsObject -> {

      JSONObject dataset = new JSONObject(datasettargetsObject.toString());
      JSONArray sources = dataset.getJSONArray("sources");
      JSONArray targets = dataset.getJSONArray("targets");

      sources.forEach(eachRelationObject -> {

        JSONObject eachRelation = new JSONObject(eachRelationObject.toString());

        JSONObject source = (JSONObject) eachRelation.get("source");

//        if (eachRelation.get("role").toString().equals("Exposes") || eachRelation.get("role").toString().equals("exposed by")
//            || eachRelation.get("role").toString().equals("is sourced from")
//            || eachRelation.get("role").toString().equals("utilizes") || eachRelation.get(
//            "role").toString().equals("applies to") || eachRelation.get("role").toString()
//            .equals("has control") || eachRelation.get("role").toString().equals("complies to")
//            || eachRelation.get("role").toString().equals("Has Query") || eachRelation.get(
//            "role").toString().equals("grouped by") || eachRelation.get("role").toString()
//            .equals("has") || eachRelation.get("role").toString().equals("represents")
//            || eachRelation.get("role").toString().equals("is essential for")
//            || eachRelation.get("role").toString().equals("contains") || eachRelation.get(
//            "role").toString().equals("is part of") || eachRelation.get("role").toString()
//            .equals("produce") || eachRelation.get("role").toString().equals("is source for ")
//            || eachRelation.get("role").toString().equals("Has Suggested Name")
//            || eachRelation.get("role").toString().equals("maintain")
//            || eachRelation.get("role").toString().equals("allowed methods")
//            || eachRelation.get("role").toString().equals("uses")) {

         // columns.add("node._key == '" + source.get("id").toString() + "'");

  //      }
        columns.add("node._key == '" + source.get("id").toString() + "'");

      });

      targets.forEach(eachRelationObject -> {

        JSONObject eachRelation = new JSONObject(eachRelationObject.toString());

        JSONObject target = (JSONObject) eachRelation.get("target");
        String targetCoRole = null;
        if (eachRelation.has("CoRole")) {
          targetCoRole = eachRelation.getString("CoRole");
        } else {
          targetCoRole = eachRelation.getString("coRole");
        }

//        if (targetCoRole.contains("Exposed by") || targetCoRole.contains("is the source of") || targetCoRole.contains("exposes")
//            || targetCoRole.contains("is utilized by") || targetCoRole.contains("complies to")
//            || targetCoRole.contains("is control for") || targetCoRole.contains("applies to")
//            || targetCoRole.contains("Is Query For") || targetCoRole.contains("is part of")
//            || targetCoRole.contains("containedBy") || targetCoRole.contains(
//            "Is Suggested Name") || targetCoRole.contains("contains") || targetCoRole.contains(
//            "belongs to") || targetCoRole.contains("used by") || targetCoRole.contains(
//            "represented by") || targetCoRole.contains("representedBy")
//            || targetCoRole.contains("groups") || targetCoRole.contains("maintained by")
//            || targetCoRole.contains("allowed by")) {
//
//          columns.add("node._key == '" + target.get("id").toString() + "'");
//
//        }

        columns.add("node._key == '" + target.get("id").toString() + "'");

      });
    });

    String columnIds = String.join(" OR ", columns);
    if (!columnIds.isEmpty()) {
      String query = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds
          + "\r\n" + "return node";
      logger.info("query----->" + query);

      try {

        cursor1 = arangoDB.query(query, HashMap.class);

        response1 = cursor1.asListRemaining();
        logger.info("response1" + response1);
      } catch (Exception e) {
        log.error(
            "Exception while dataElements_2 : " + e.getMessage().toString());
      }
    }
    ArrayList<Object> nodesList = new ArrayList<>();
    ArrayList<Object> nodesList1 = new ArrayList<>();
    HashMap<String, Integer> ElementsCount = new HashMap<>();

    HashMap<String, ArrayList<Object>> DataElementInfo = new HashMap<>();
    HashMap<String, ArrayList<Object>> DataElementInfo1 = new HashMap<>();
    HashMap<String, Object> DataElementInfo3 = new HashMap<>();
    HashMap<String, Set<String>> DataElementInfo2 = new HashMap<>();

    List<String> keycolumns = new ArrayList<String>();

    Set<String> keycolumns1 = new HashSet<String>();

    response1.forEach(nodesinfo -> {

      JSONObject nodesinfo1 = new JSONObject();
      JSONObject nodesinfo2 = new JSONObject();
      JSONObject nodes = new JSONObject(nodesinfo);
      logger.info("nodes" + nodes);

      //getting asset fullName
      String fullName = nodes.getString("identifier");
      nodesinfo2.put("FullName", fullName);
      nodesinfo1.put("FullName", fullName);

      //getting asset displayName
      String nodeDisplayname = nodes.getString("displayName");
      nodesinfo1.put("DisplayName", nodeDisplayname);
      nodesinfo1.put("Title", nodeDisplayname);
      nodesinfo2.put("DisplayName", nodeDisplayname);

      //getting id
      String Id = nodes.getString("id");
      nodesinfo1.put("id", Id);

      String key = nodes.getString("_key");
      nodesinfo1.put("key", key);

      if (nodes.has("createdOn")) {
        String createdOn = nodes.getString("createdOn");
//            long l = Long.parseLong(createdOn);
//            String Date = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(l)),
//                ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            nodesinfo1.put("CreatedOn", Date);
        nodesinfo1.put("CreatedOn", createdOn);
      }
      if (nodes.has("subType")) {
        String subType = nodes.getString("subType");
//              long l = Long.parseLong(createdOn);
//              String Date = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(l)),
//                  ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//              nodesinfo1.put("CreatedOn", Date);
        nodesinfo1.put("subType", subType);
      }
      //String value = attributes.getString("value");

      if (nodes.has("createdByUserName")) {
        String createdBy = nodes.getString("createdByUserName");
        nodesinfo1.put("CreatedBy", createdBy);
      }
      if (nodes.has("responsibilities")) {
        JSONArray responsibilities = nodes.getJSONArray("responsibilities");
        responsibilities.forEach(eachsource -> {
          JSONObject responsibility = new JSONObject(eachsource.toString());
          String roleName = responsibility.getString("roleName");
          String responsibilityName = responsibility.getString("name");
          nodesinfo1.put(roleName, responsibilityName);

        });
      }

      if (nodes.has("community")) {
        JSONObject community = nodes.getJSONObject("community");
        if (community.has("name")) {
          String communityName = community.getString("name");
          nodesinfo1.put("CommunityName", communityName);
        }
      }

      if (nodes.has("domain")) {
        JSONObject domain = nodes.getJSONObject("domain");
        if (domain.has("name")) {
          String domainName = domain.getString("name");
          nodesinfo1.put("DomainName", domainName);
        }
      }

      JSONObject nodeType = nodes.getJSONObject("type");
      if (nodeType.has("metaCollectionName")) {
        String name = nodeType.getString("metaCollectionName");
        nodesinfo1.put("MetaCollectionName", name);
      } else {
        nodesinfo1.put("MetaCollectionName", "null");
      }

      String name = null;
      if (nodeType.has("name")) {
        name = nodeType.getString("name");
        nodesinfo1.put("MetaCollectionTypeName", name);
      } else {
        nodesinfo1.put("MetaCollectionTypeName", "null");
      }
      if (nodes.has("status")) {
        JSONObject nodestatus = nodes.getJSONObject("status");
        String statusName = nodestatus.getString("name");
        nodesinfo1.put("Status", statusName);
      }
      JSONObject relations = (JSONObject) nodes.get("relations");
      JSONArray sourcerelations = relations.getJSONArray("sources");
      if (!sourcerelations.isEmpty()) {
        ArrayList<String> sourceList = new ArrayList<>();
        String sourcetype = null;
        String sourcesrole = null;
        for (int j = 0; j < sourcerelations.length(); j++) {
          JSONObject sources = new JSONObject(sourcerelations.get(j).toString());
          sourcesrole = sources.getString("role");
          JSONObject source1 = sources.getJSONObject("source");
          String sourcename = source1.getString("displayName");
          sourcetype = source1.getString("type");
          sourceList.add(sourcename);
        }
        nodesinfo1.put(sourcetype.concat(" " + sourcesrole + " " + name), sourceList);
        logger.info("nodesinfo1" + nodesinfo1);
      }
      //					sourcerelations.forEach(eachsource -> {
      //						JSONObject sources=new JSONObject(eachsource.toString());
      //						String sourcesrole=sources.getString("role");
      //						JSONObject source1=sources.getJSONObject("source");
      //						String sourcename=source1.getString("displayName");
      //						String sourcetype=source1.getString("type");
      //						ArrayList<String> sourceList= new ArrayList<>();
      //						sourceList.add(sourcename);
      //						nodesinfo1.put(sourcetype.concat(" "+sourcesrole),sourceList);
      //						logger.info("nodesinfo1"+nodesinfo1);
      //
      //					});

      JSONArray targetrelations = relations.getJSONArray("targets");
      if (!targetrelations.isEmpty()) {

        ArrayList<String> targetList = new ArrayList<>();
        ArrayList<String> targetList1 = new ArrayList<>();
        ArrayList<String> targettype1 = new ArrayList<>();
        ArrayList<String> targettype2 = new ArrayList<>();
        ArrayList<String> targetCoRole1 = new ArrayList<>();
        ArrayList<String> targetCoRole2 = new ArrayList<>();
        for (int i = 0; i < targetrelations.length(); i++) {
          JSONObject targets = new JSONObject(targetrelations.get(i).toString());
          String targetname = null;
          String TargetCoRole = null;
          String targettype = null;
          if (targets.has("CoRole")) {
            TargetCoRole = targets.getString("CoRole");
          } else {
            TargetCoRole = targets.getString("coRole");
          }
          JSONObject target1 = targets.getJSONObject("target");
          targetname = target1.getString("displayName");
          targettype = target1.getString("type");
          //targetList.add(targetname);
          if (targettype.contains("Column")) {
            //targetname=target1.getString("displayName");
            targetList.add(targetname);
            targettype1.add(targettype);
            targetCoRole1.add(TargetCoRole);

          } else {
            //targetname=target1.getString("displayName");
            targetList1.add(targetname);
            targettype2.add(targettype);
            targetCoRole2.add(TargetCoRole);
          }

        }

        //nodesinfo1.put(targettype.concat(" "+TargetCoRole+" "+name),targetList);
        if (!targettype1.isEmpty() && !targetCoRole1.isEmpty()) {
          nodesinfo1.put(targettype1.get(0).concat(" " + targetCoRole1.get(0) + " " + name),
              targetList);
        }
        if (!targettype2.isEmpty() && !targetCoRole2.isEmpty()) {
          nodesinfo1.put(targettype2.get(0).concat(" " + targetCoRole2.get(0) + " " + name),
              targetList1);
        }
        logger.info("nodesinfo1" + nodesinfo1);
      }
      //					targetrelations.forEach(eachsource -> {
      //						JSONObject targets=new JSONObject(eachsource.toString());
      //						String TargetCoRole=null;
      //						if(targets.has("CoRole")) {
      //						TargetCoRole=targets.getString("CoRole");
      //						}else {
      //							TargetCoRole=targets.getString("coRole");
      //						}
      //						JSONObject target1=targets.getJSONObject("target");
      //						String targetname=target1.getString("displayName");
      //						String targettype=target1.getString("type");
      //						ArrayList<String> targetList= new ArrayList<>();
      //						targetList.add(targetname);
      //						nodesinfo1.put(targettype.concat(" "+TargetCoRole),targetList);
      //						logger.info("nodesinfo1"+nodesinfo1);
      //					});

      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      attributeInfo.forEach(eachAttribute -> {
        JSONObject attributes = new JSONObject(eachAttribute.toString());
        logger.info("attributes" + attributes);
        if (!attributes.isEmpty()) {

          String attributesname = attributes.get("name").toString();
          String attributesvalue = null;
          if (attributes.has("value")) {
            attributesvalue = attributes.get("value").toString();

          } else {
            attributesvalue = "null";
          }
          //							if(attributesname.equals("Shoppable") && attributesname.equals("Searchable") || attributesname.equals("searchable")) {
          //							logger.info("avoid to add attribute");
          //							}else
          //							{
          //								nodesinfo1.put(attributesname, attributesvalue);
          //							}
          if (attributes.get("name").toString().equals("Shoppable") || attributes.get("name")
              .toString().equals("Searchable") || attributes.get("name").toString()
              .equals("searchable")) {
            logger.info("avoid to add attribute");
          } else {
            nodesinfo1.put(attributesname, attributesvalue);
          }
          if (attributes.get("name").toString().equals("Description") || attributes.get("name")
              .toString().equals("description")) {
            String value = attributes.getString("value");
            nodesinfo2.put("Description", value);
            nodesinfo1.put("Description", value);
          }

        }

      });
      Set<String> k = nodesinfo1.keySet();
      keycolumns.addAll(k);
      keycolumns1.addAll(k);
      logger.info("keys" + k);
      nodesList.add(nodesinfo1.toMap());
      nodesList1.add(nodesinfo2.toMap());

    });

    int counter = 0;
    for (int i = 1; i <= nodesList.size(); i++) {
      counter = counter + 1;
      logger.info(String.valueOf(counter));
    }
    //logger.info("keycolumns"+keycolumns);
    logger.info("keycolumns1" + keycolumns1);
    //logger.info("newList"+newList);
    Set<String> sortedKeycolumns1 = new TreeSet<>(keycolumns1);
    DataElementInfo2.put("Nodekeys", sortedKeycolumns1);
    DataElementInfo.put("DataelementInfo", nodesList);
    ElementsCount.put("count", counter);
    logger.info("nodesList" + nodesList);
    DataElementInfo1.put("Info", nodesList1);
    //DataElementInfo3.put("Count", ElementsCount);

    //response2.add(DataElementInfo3);
    response2.add(DataElementInfo2);
    response2.add(DataElementInfo);
    response2.add(ElementsCount);
    response2.add(DataElementInfo1);

    return response2;
  }

  public List<Object> nodeFilterSearch(String name) {
    List<Object> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //			String queryToBeExecuted = "let AllWords=(\r\n"
    //					+ "let AllWordsMatch = (FOR a in "+ viewName +"\r\n"
    //					+ "SEARCH ANALYZER(a.name IN TOKENS('"+ name +"','text_en'),'text_en')\r\n"
    //					+ "filter a.name != '"+ name +"'\r\n"
    //					+ "SORT BM25(a) DESC\r\n"
    //					+ "RETURN a.name)\r\n"
    //					+ "let AllWordsNgram=(FOR doc IN " + viewName +"\r\n"
    //					+ "SEARCH NGRAM_MATCH(doc.name,'"+ name +"',0.3,'fuzzy_search_bigram')\r\n"
    //					+ "sort BM25(doc) DESC\r\n"
    //					+ "return doc.name)\r\n"
    //					+ "let AllWordsLevenshtein=(FOR doc IN "+ viewName +"\r\n"
    //					+ "  SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('"+ name +"', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
    //					+ "  SORT BM25(doc) DESC\r\n"
    //					+ "  return doc.name)\r\n"
    //					+ "  return intersection(AllWordsMatch,AllWordsNgram,AllWordsLevenshtein)\r\n"
    //					+ ")\r\n"
    //					+ " let exactMatch=(FOR a in "+ viewName +"\r\n"
    //					+ "SEARCH ANALYZER (PHRASE(a.name,'"+ name +"'),\"text_en\")\r\n"
    //					+ "SORT BM25(a) DESC\r\n"
    //					+ "RETURN a.name)\r\n"
    //					+ "let oneWord=(\r\n"
    //					+ "let oneWordMatch = (FOR a in " + viewName +"\r\n"
    //					+ "SEARCH ANALYZER(PHRASE(a.name, '"+ name +"'),'text_en')\r\n"
    //					+ "SORT BM25(a) ASC\r\n"
    //					+ "RETURN a.name)\r\n"
    //					+ "let OneWordFuzzy=(FOR doc IN "+ viewName +"\r\n"
    //					+ "SEARCH NGRAM_MATCH(\r\n"
    //					+ "doc.name,'"+ name +"',0.3,'fuzzy_search_bigram')\r\n"
    //					+ "sort BM25(doc) DESC\r\n"
    //					+ "return doc.name)\r\n"
    //					+ "let oneWordLevenshtein=(FOR doc IN "+ viewName +"\r\n"
    //					+ " SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('"+ name +"', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
    //					+ " filter doc.name != '"+ name +"'\r\n"
    //					+ " SORT BM25(doc) DESC\r\n"
    //					+ " return doc.name)\r\n"
    //					+ " return intersection(oneWordMatch,OneWordFuzzy,oneWordLevenshtein))\r\n"
    //					+ "return {AllWords:AllWords,ExactMatch:exactMatch,OneWord:oneWord}\r\n"
    //					+ "";
    String queryToBeExecuted = "let AllWords=(\r\n"
        + "LET tokens = TOKENS(['" + name + "'], \"text_en\") \r\n"
        + "LET tokens_flat = FLATTEN(tokens, 2)\r\n"
        + "FOR doc IN nodesView SEARCH ANALYZER(tokens_flat ALL IN doc.name, \"text_en\") RETURN doc.name)\r\n"
        + "let exactMatch=(FOR a in nodesView\r\n"
        + "SEARCH ANALYZER (PHRASE(a.name,'" + name + "'),\"text_en\")\r\n"
        + "SORT BM25(a) DESC\r\n"
        + "RETURN a.name)\r\n"
        + "let oneWord=(\r\n"
        + "let OneWordFuzzy=(FOR doc IN nodesView\r\n"
        + "SEARCH NGRAM_MATCH(\r\n"
        + "doc.name,'" + name + "',0.3,'fuzzy_search_bigram')\r\n"
        + "filter doc.name != '" + name + "'\r\n"
        + "sort BM25(doc) DESC\r\n"
        + "LIMIT 10"
        + "return doc.name)\r\n"
        + "let oneWordLevenshtein=(FOR doc IN nodesView\r\n"
        + "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
        + "filter doc.name != '" + name + "'\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 10"
        + "return doc.name)\r\n"
        + "return UNION_DISTINCT(OneWordFuzzy,oneWordLevenshtein))\r\n"
        + "return {AllWords:AllWords,ExactMatch:exactMatch,OneWord:oneWord}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeFilterSearch: " + e.getMessage().toString());
    }

    return response;
  }

  public ArrayList<Object> getfacets(String name) throws JSONException, IOException {

    InputStream initialStream = null;
    BufferedReader r = null;
    String l = null;
    String content = null;
    List<String> response = new ArrayList<>();
    //List<String> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    HashMap<String, ArrayList<Object>> NodeInfo = new HashMap<>();
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo2 = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "For node in " + viewName + "\r\n"
        + "filter node.name == '" + name + "'\r\n" + "return node";

    ArangoCursor<String> cursor = null;
    logger.info("query...  " + query);
    try {

      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getfacets : " + e.getMessage().toString());
    }
    JSONObject nodesinfo1 = new JSONObject();

    response.forEach(nodesinfo -> {
      JSONObject nodes = new JSONObject(nodesinfo);

      String str = nodes.getString("displayName");
      String str1 = "<b>" + str + "</b>";
      //nodesinfo1.put("DisplayName",str1);
      //logger.info(nodesinfo1);
      JSONArray targetsObj = new JSONArray();
      JSONObject edges = (JSONObject) nodes.get("relations");
      JSONArray targetedges = edges.getJSONArray("targets");

      if (!targetedges.isEmpty()) {
        targetedges.forEach(eachsource -> {
          JSONObject targets = new JSONObject(eachsource.toString());
          String targetname = null;
          if (targets.has("CoRole")) {
            targetname = targets.getString("CoRole");
          } else {
            targetname = targets.getString("coRole");
          }
          //	String targetname=targets.getString("CoRole");
          if (targetname.contains("contains")) {
            targetsObj.put(targets);
          }
        });
        if (!targetsObj.isEmpty()) {
          JSONObject targetedges1 = targetsObj.getJSONObject(0);
          String result = null;
          JSONObject target1 = targetedges1.getJSONObject("target");
          //String targetname=targetedges1.getString("CoRole");
          String targetname = null;
          if (targetedges1.has("CoRole")) {
            targetname = targetedges1.getString("CoRole");
          } else {
            targetname = targetedges1.getString("coRole");
          }
          String targetid = target1.getString("id");
          logger.info("targetid" + targetid);
          String query1 = connectArango.getQueryResult(targetid);

          if (targetname.contains("is part of")) {
            result = connectArango.getNodesResponse(query1);
            logger.info(result);
            columns1.add("node.name == '" + result.toString() + "'");
            logger.info(String.valueOf(columns1));

          } else {
            columns1.add("node.name == '" + target1.getString("name").toString() + "'");
            logger.info(String.valueOf(columns1));
          }
        } else {
          columns1.add("node.name == '" + str.toString() + "'");
          logger.info(String.valueOf(columns1));
        }

      } else {
        columns1.add("node.name == '" + str.toString() + "'");
        logger.info(String.valueOf(columns1));
      }

      String columnNames = String.join(" OR ", columns1);
      logger.info("columnNames" + columnNames);

      String query1 = "For node in " + viewName + "\r\n"
          + "filter " + columnNames + "\r\n" + "return node";
      logger.info(query1);
      List<String> response1 = null;

      ArangoCursor<String> cursor1 = null;
      try {

        cursor1 = arangoDB.query(query1, String.class);
        response1 = cursor1.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getfacets_2 : " + e.getMessage().toString());
      }
      response1.forEach(nodesInfo ->
      {
        JSONObject nodes1 = new JSONObject(nodesInfo);
        JSONArray attributeInfo = nodes1.getJSONArray("attributes");
        if (!attributeInfo.isEmpty()) {
          attributeInfo.forEach(eachAttribute -> {
            JSONObject attributes = new JSONObject(eachAttribute.toString());
            logger.info(String.valueOf(attributes));
            if (!attributes.isEmpty()) {
              if (attributes.getString("name").equals("Categorical Data")) {
                String value = attributes.getString("value");
                columns2.add("node.name == '" + nodes1.getString("name") + "'");
              } else {
                columns1.add("node.name == '" + str.toString() + "'");
              }
            }
          });

        } else {
          columns1.add("node.name == '" + str.toString() + "'");
        }

      });

    });

    String columnNames1 = String.join(" OR ", columns2);
    logger.info("columnNames1" + columnNames1);
    String query2 = "For node in " + arangocategoryCollection + "\r\n"
        + "filter " + columnNames1 + "\r\n" + "return node";
    logger.info(query2);

    ArangoCursor<HashMap> cursor2 = null;
    try {

      cursor2 = arangoDB.query(query2, HashMap.class);
      response2 = cursor2.asListRemaining();
      logger.info(String.valueOf(response2));
    } catch (Exception e) {
      log.error("Exception while getfacets_3 : " + e.getMessage().toString());
    }

    initialStream = getClass().getResourceAsStream("/cate.json");
    r = new BufferedReader(new InputStreamReader(initialStream));
    content = "";
    // reads each line
    while ((l = r.readLine()) != null) {
      content = content + l;
    }
    initialStream.close();
    logger.info(content);
    JSONArray response3 = new JSONArray(content);
    logger.info(String.valueOf(response3));

    response2.forEach(categoriacalData ->
    {
      JSONObject cData = new JSONObject(categoriacalData);
      String Cname = cData.getString("name");
      logger.info(Cname);
      JSONArray category = cData.getJSONArray("categories");
      //logger.info(category);
      if (Cname.contains("netflix_titles.csv > country")) {
        nodesinfo2.put(Cname, response3);
      } else {
        nodesinfo2.put(Cname, category);
      }

    });
    nodesList.add(nodesinfo2.toMap());

    return nodesList;
  }

  public List<Object> getMyRecentSearchesList() {
    String response1 = null;
    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "FOR doc IN " + arangomyrecentsearchCollection + "\r\n"
        + "COLLECT nodename = doc.nodename INTO searches\r\n"
        + "LET names = (FOR document IN searches[*].doc SORT document.searchedOn DESC RETURN document)\r\n"
        + "SORT names[0].searchedOn DESC\r\n"
        + "LIMIT 10\r\n"
        + "RETURN names[0].nodename";
    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getMyRecentSearchesList : " + e.getMessage().toString());
    }

    return response;
  }

  public List<Object> getMyPopularTags() {
    String response1 = null;
    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For doc in " + arangomySearchTagsCollection + "\r\n"
        + "COLLECT labelTags=doc.tag WITH COUNT into nodesCount\r\n"
        + "FILTER labelTags != null\r\n"
        + "RETURN {labelTags,value:nodesCount}";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getMyPopularTags : " + e.getMessage().toString());
    }
    return response;
  }

  public List<Object> getPopularTags(String tag) {
    String response1 = null;
    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in " + arangomySearchTagsCollection + "\r\n"
        + "filter doc.tag == '" + tag + "'\r\n"
        + "RETURN doc";
    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getPopularTags : " + e.getMessage().toString());
    }
    return response;
  }

  public List<Object> getViewNodeTags(String tag) {

    String response1 = null;
    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for doc in " + ViewTagSearchesCollection + "\r\n"
        + "Collect tags = doc.map\r\n"
        + "filter tags.tag == '" + tag + "'\r\n"
        + "RETURN tags";
    logger.info("query--->" + query);
    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getViewNodeTags : " + e.getMessage().toString());
    }
    return response;

  }

  public List<Object> getMyRelatedSearchesList(String name) {

    List<Object> response = new ArrayList<>();
    ;
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "FOR doc IN " + viewName + "\r\n"
        + "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 10\r\n"
        + "collect  AGGREGATE names = UNIQUE(doc.name)\r\n"
        + "return names";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getMyRelatedSearchesList : " + e.getMessage().toString());
    }
    return response;

  }

  public List<Object> getMySimilarSeachesList(List<String> name) {
    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < name.size(); i++) {
      columns.add("ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name.get(i)
          + "', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")");
    }
    String columnIds1 = String.join(" OR ", columns);
    List<HashMap> response = new ArrayList<>();
    ;
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    //				String query = "FOR doc IN "+ viewName +"\r\n"
    //						+ "SEARCH "+ columnIds1 +"\r\n"
    //						+ "SORT BM25(doc) DESC\r\n"
    //						+ "LIMIT 10\r\n"
    //						//+ "collect  AGGREGATE names = UNIQUE(doc)\r\n"
    //						+ "return doc";
    String query = "FOR doc IN " + viewName + "\r\n"
        + "SEARCH " + columnIds1 + "\r\n"
        + "let score = BM25(doc)\r\n"
        + "filter score > 2\r\n"
        + "LIMIT 10\r\n"
        + "return doc";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getMySimilarSeachesList : " + e.getMessage().toString());
    }
    return connectArango.tailView(response);

  }

  public List<Object> getRecentrTags() {

    List<Object> response = new ArrayList<>();
    ;
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For doc in " + ViewTagSearchesCollection + "\r\n"
        + "COLLECT docs = doc.map \r\n"
        + "COLLECT labelTags=docs.tag AGGREGATE names = UNIQUE(docs)\r\n"
        + "FILTER labelTags != null\r\n"
        + "RETURN {labelTags, names: names, value:COUNT(names)}";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getRecentrTags " + e.getMessage().toString());
    }
    return response;
  }

  //	public List<Object> getmultilevelSearch(String name, int pageNo, int pageSize) {
  //
  //		columNames.clear();
  //		columNames1.clear();
  //		columNames2.clear();
  //		columNames3.clear();
  //		columNames4.clear();
  //		columNames5.clear();
  //		ArangoDB arangoConn = arangorestclient.getArangoConnection();
  //		List<String> response = new ArrayList<>();
  //		List<String> response2 = new ArrayList<>();
  //		ArangoCursor<String> cursor = null;
  //		List<String> result = new ArrayList<String>();
  //
  //		if (arangoConn != null) {
  //			ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn,arangodatabase);
  //			if (arangodb != null) {
  //				String queryToBeExecuted="let AllWordsMatch = (FOR a in nodesView\r\n"
  //						+ "SEARCH ANALYZER(a.name IN TOKENS('"+ name +"','text_en'),'text_en')\r\n"
  //						+ "SORT BM25(a) ASC\r\n"
  //						+ "LIMIT 10"
  //						+ "RETURN a.relations)\r\n"
  //						+ "let AllWordsNgram=(FOR doc IN nodesView\r\n"
  //						+ "SEARCH NGRAM_MATCH(doc.name,'"+ name +"',0.3,'fuzzy_search_bigram')\r\n"
  //						+ "sort BM25(doc) DESC\r\n"
  //						+ "LIMIT 10"
  //						+ "return doc.relations)\r\n"
  //						+ "let AllWordsLevenshtein=(FOR doc IN nodesView\r\n"
  //						+ "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('"+ name +"', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
  //						+ "SORT BM25(doc) DESC\r\n"
  //						+ "LIMIT 10"
  //						+ "return doc.relations)\r\n"
  //						+ "return {resultMatches:UNION_DISTINCT(AllWordsMatch,AllWordsNgram,AllWordsLevenshtein)}";
  //
  //				logger.info("queryToBeExecuted----->" + queryToBeExecuted);
  //				try {
  //
  //					cursor = arangoDB.query(queryToBeExecuted, String.class);
  //					response = cursor.asListRemaining();
  //
  //				} catch (Exception e) {
  //					log.error("Exception while retrieving search query: " + e.getMessage().toString());
  //				}
  //				List<String> columns = new ArrayList<String>();
  //				response.forEach(res1->{
  //					JSONObject resultRelation = new JSONObject(res1);
  //					JSONArray resultMatches=resultRelation.getJSONArray("resultMatches");
  //					resultMatches.forEach(datasettargetsObject -> {
  //						JSONObject eachRelation = new JSONObject(datasettargetsObject.toString());
  //						logger.info(eachRelation);
  //						JSONArray targetedges=eachRelation.getJSONArray("targets");
  //						if(!targetedges.isEmpty()) {
  //							targetedges.forEach(eachsource -> {
  //								JSONObject targets=new JSONObject(eachsource.toString());
  //								logger.info("targets"+targets);
  //								JSONObject target=targets.getJSONObject("target");
  //								logger.info("target"+target);
  //								String id=target.getString("id");
  //								columNames.add(id);
  //							});
  //						}
  //					});
  //				});
  //			}
  //
  //		}
  //		ArrayList<Object> resultForPage=targetList1();
  //
  //		return getPages(resultForPage,pageSize,pageNo);
  //	}
  //static String id;
  public List<String> sourceSearch(String columnIds1) {

    List<HashMap> response = new ArrayList<>();

    //List<Object> columNames1 = new ArrayList<Object>();

    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query1 = "For node in Nodes\r\n"
        + "FILTER node.id == '" + columnIds1 + "'\r\n" + "return node";
    logger.info("query----->" + query1);
    try {
      cursor = arangoDB.query(query1, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception sourceSearch : " + e.getMessage().toString());
    }

    //JSONObject nodeString = new JSONObject();
    String name = null;
    for (int j = 0; j < response.size(); j++) {
      HashMap eachRelation1 = response.get(j);
      //		logger.info(eachRelation1);
      JSONObject nodes1 = new JSONObject(eachRelation1);
      JSONObject nodes = new JSONObject(nodes1.toString());
      JSONObject relations = (JSONObject) nodes.get("relations");
      JSONArray sourceedges1 = relations.getJSONArray("sources");
      if (!sourceedges1.isEmpty()) {
        for (int i = 0; i < sourceedges1.length(); i++) {
          JSONObject targetlists = (JSONObject) sourceedges1.get(i);
          JSONObject targets1 = new JSONObject(targetlists.toString());
          JSONObject target = targets1.getJSONObject("source");
          name = target.getString("name");
          columNames1.add(target.getString("id"));
          //id=target.getString("id");
          sourceSearch(target.getString("id"));
          //sourceSearch(id);
        }
      }
    }

    return columNames1;

  }

  public List<Object> uniqueList() {

    List<Object> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<Object> columNames1 = new ArrayList<Object>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();

    for (int i = 0; i < columNames.size(); i++) {
      columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.name == '" + columNames.get(i) + "'");
      logger.info("columns-->" + columns);
    }
    String columnIds1 = String.join(" OR ", columns1);
    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted1 = "return UNIQUE(" + columns + ")";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    try {

      cursor = arangoDB.query(queryToBeExecuted1, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while uniqueList : " + e.getMessage().toString());
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while uniqueList_2 : " + e.getMessage().toString());
    }

    response2.add(connectArango.tailView(response1));

    return response2;

  }

  public ArrayList<Object> getmultilevelGraphSearch(String name) {

    columNames.clear();
    columNames1.clear();
    columNames2.clear();
    columNames3.clear();
    columNames4.clear();
    columNames5.clear();

    List<String> response = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    ArangoCursor<String> cursor = null;
    List<String> result = new ArrayList<String>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "let AllWordsMatch = (FOR a in nodesView\r\n"
        + "SEARCH ANALYZER(a.name IN TOKENS('" + name + "','text_en'),'text_en')\r\n"
        + "SORT BM25(a) ASC\r\n"
        + "LIMIT 10"
        + "RETURN a.relations)\r\n"
        + "let AllWordsNgram=(FOR doc IN nodesView\r\n"
        + "SEARCH NGRAM_MATCH(doc.name,'" + name + "',0.3,'fuzzy_search_bigram')\r\n"
        + "sort BM25(doc) DESC\r\n"
        + "LIMIT 10"
        + "return doc.relations)\r\n"
        + "let AllWordsLevenshtein=(FOR doc IN nodesView\r\n"
        + "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 10"
        + "return doc.relations)\r\n"
        + "return {resultMatches:UNION_DISTINCT(AllWordsMatch,AllWordsNgram,AllWordsLevenshtein)}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getmultilevelGraphSearch : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    response.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      JSONArray resultMatches = resultRelation.getJSONArray("resultMatches");
      resultMatches.forEach(datasettargetsObject -> {
        JSONObject eachRelation = new JSONObject(datasettargetsObject.toString());
        logger.info(String.valueOf(eachRelation));
        JSONArray targetedges = eachRelation.getJSONArray("targets");
        if (!targetedges.isEmpty()) {
          targetedges.forEach(eachsource -> {
            JSONObject targets = new JSONObject(eachsource.toString());
            logger.info("targets" + targets);
            JSONObject target = targets.getJSONObject("target");
            logger.info("target" + target);
            String id = target.getString("id");
            columNames.add(id);
          });
        }
      });
    });
    ArrayList<Object> resultForPage = targetList1();

    return resultForPage;
  }

  public String getPages(ArrayList<Object> c, Integer pageSize, Integer pageNo)
      throws IndexOutOfBoundsException, ProcessFailedException {
    JSONObject retunPageJson = new JSONObject();

    pages.clear();
    listpages.clear();
    List<Object> result = new ArrayList<Object>();
    //List<Object> list = new ArrayList<Object>(c);
    retunPageJson.put("totalNumberofNodes", c.size());
    retunPageJson.put("currentPageNumber", pageNo);

    int numPages = (int) Math.ceil((double) c.size() / (double) pageSize);

    int pageNum;
    for (pageNum = 0; pageNum < numPages; ) {
      pages.add(c.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, c.size())));

    }
    int count = 0;

    for (int i = 0; i < pages.size(); i++) {
      count++;
      if (pageNo == count) {
        listpages.add(pages.get(i));

      }
    }
    if (listpages.isEmpty()) {
      throw new ProcessFailedException("Page Not Found ..Please check the Page number..");

    } else {
      retunPageJson.put("nodeInfo", listpages);

      return retunPageJson.toString();
    }

  }

  public ArrayList<Object> targetList1() {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < columNames.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == '" + columNames.get(i) + "'");
      //logger.info("columns-->"+columns);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while targetList1 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);

      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames1.add(id);
        });
      }
    });
    return uniqueListOfSources();

  }

  private ArrayList<Object> uniqueListOfSources() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames1.size(); i++) {
      columns.add("'" + columNames1.get(i) + "'");
      columNames.add(columNames1.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //logger.info("columns1"+columns1);

    }
    logger.info(String.valueOf(columns1));

    return sourceList1(columns1);
  }

  public ArrayList<Object> sourceList1(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceList1 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames2.add(id);


        });
      }


    });

    return uniqueListOfSources1();

  }

  private ArrayList<Object> uniqueListOfSources1() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames2.size(); i++) {
      columns.add("'" + columNames2.get(i) + "'");
      columNames.add(columNames2.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceList2(columns1);
  }

  public ArrayList<Object> sourceList2(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceList2 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames3.add(id);
          //sourceSearch(id);

        });
      }
    });

    //return connectArango.listView(columNames);
    return uniqueListOfSources2();

  }

  private ArrayList<Object> uniqueListOfSources2() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames3.size(); i++) {
      columns.add("'" + columNames3.get(i) + "'");
      columNames.add(columNames3.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceList3(columns1);
  }

  public ArrayList<Object> sourceList3(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceList3 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames4.add(id);
          //sourceSearch(id);

        });
      }
    });

    //return connectArango.listView(columNames);
    return uniqueListOfSources3();

  }

  private ArrayList<Object> uniqueListOfSources3() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames4.size(); i++) {
      columns.add("'" + columNames4.get(i) + "'");
      columNames.add(columNames4.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceList4(columns1);
  }

  public ArrayList<Object> sourceList4(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceList4 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames5.add(id);
          //sourceSearch(id);

        });
      }
    });

    //return connectArango.listView(columNames);
    return uniqueListOfSources5();

  }

  private ArrayList<Object> uniqueListOfSources5() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames5.size(); i++) {
      columns.add("'" + columNames5.get(i) + "'");
      columNames.add(columNames5.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceList5(columns1);
  }

  public ArrayList<Object> sourceList5(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceList5 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames.add(id);
          //sourceSearch(id);

        });
      }
    });
    return connectArango.listView(columNames);
    //return uniqueListOfSources4();

  }

  public String getnodeResponse(String id) {
    List<String> columns = new ArrayList<String>();
    String Id = null;

    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    final String query = us.getQueryResultId(id);
    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getnodeResponse : " + e.getMessage().toString());
    }

    response.forEach(nodesinfo -> {
      JSONObject nodes = new JSONObject(nodesinfo);
      String str = nodes.getString("displayName");

      JSONObject edges = (JSONObject) nodes.get("relations");
      JSONArray targetedges = edges.getJSONArray("targets");

      if (!targetedges.isEmpty()) {
        targetedges.forEach(eachsource -> {
          JSONObject targets = new JSONObject(eachsource.toString());
          String targetname = null;
          if (targets.has("CoRole")) {
            targetname = targets.getString("CoRole");
          } else {
            targetname = targets.getString("coRole");
          }
          JSONObject target = targets.getJSONObject("target");

          String type = target.getString("type");
          if (type.contains("Data Set") || type.contains("Schema") || type.contains("Product")) {
            columns.add(target.getString("id"));
          } else {
            getnodeResponse(target.getString("id"));
          }
        });
      }
    });
    if (!columns.isEmpty()) {
      Id = columns.get(0);
    }
    return Id;
  }

  public List<Object> getAddtoCart() {
    //String response1 = null;
    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for node in nodesView\r\n"
        + "filter node.attributes[*].value ANY == \"AddToCart\"\r\n"
        + "return node";
    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getAddtoCart : " + e.getMessage().toString());
    }
    //connectArango.importDocuments2Arango3(response,shoppingCart);
    response1.add(connectArango.tailView(response));
    return response1;
  }

  public List<Object> autoCompleteTag(String value) {
    //String response1 = null;
    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //				String query="FOR node in "+ viewName +"\r\n"
    //						+ "SEARCH ANALYZER(STARTS_WITH(node.Tag,TOKENS('"+ value +"','text_en')),'text_en')\r\n"
    //						+ "LIMIT 10\r\n"
    //						+ "RETURN {name:node.Tag}";
    String query = "return UNIQUE((\r\n"
        + "FOR node in " + viewName + "\r\n"
        + "SEARCH ANALYZER(STARTS_WITH(node.Tag,TOKENS('" + value
        + "','text_en')),'text_en')\r\n"
        + "LIMIT 10\r\n"
        + "RETURN node.Tag))[**]";
    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while autoCompleteTag : " + e.getMessage().toString());
    }

    return response;

  }

  public List<Object> autoCompleteRole(String value) {

    //String response1 = null;
    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //				String query="FOR node in "+ viewName +"\r\n"
    //						+ "SEARCH ANALYZER(STARTS_WITH(node.Tag,TOKENS('"+ value +"','text_en')),'text_en')\r\n"
    //						+ "LIMIT 10\r\n"
    //						+ "RETURN {name:node.Tag}";
    //				String query="return UNIQUE((\r\n"
    //						+ "FOR node in "+ viewName +"\r\n"
    //						+ "SEARCH ANALYZER(STARTS_WITH(node.FirstName,TOKENS('"+ value +"','text_en')),'text_en')\r\n"
    //						+ "LIMIT 10\r\n"
    //						+ "RETURN node.FirstName))[**]";
    String query = "let AllRoles=(\r\n"
        + "let First = (FOR node in " + viewName + "\r\n"
        + "SEARCH ANALYZER(STARTS_WITH(node.FirstName,TOKENS('" + value
        + "','text_en')),'text_en')\r\n"
        + "LIMIT 10\r\n"
        + "RETURN node)\r\n"
        + "let Last=(FOR node in " + viewName + "\r\n"
        + "SEARCH ANALYZER(STARTS_WITH(node.LastName,TOKENS('" + value
        + "','text_en')),'text_en')\r\n"
        + "LIMIT 10\r\n"
        + "RETURN node)\r\n"
        + "let metaRoles =(FOR node in " + viewName + "\r\n"
        + "SEARCH ANALYZER(STARTS_WITH(node.roleName,TOKENS('" + value
        + "','text_en')),'text_en')\r\n"
        + "LIMIT 10\r\n"
        + "RETURN node)\r\n"
        + "return union(First,Last,metaRoles))\r\n"
        + "return unique([AllRoles][**][**])";
    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();

      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while autoCompleteRole : " + e.getMessage().toString());
    }
    return response;

  }

  @SuppressWarnings("rawtypes")
  public List<HashMap> pinNodes(String nodeName) {
    logger.info(nodeName);
    return connectArango.getPinNodesInfo(nodeName);
  }

  public List<HashMap> getCartList(String userName) {

    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //			String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    //					+ "return node.responsibilities";
    String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
        + "filter doc.cartHolder == '" + userName + "'\r\n"
        + "for d in doc.cartNodes\r\n"
        + "return d";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getCartList : " + e.getMessage().toString());
    }

    return response;

  }

  public List<HashMap> getSaveForLater(String cartHolder, HashMap key) {

    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    List<String> nodekeysList = (List<String>) key.get("nodekeys");
    for (int i = 0; i < nodekeysList.size(); i++) {

      String query = "for a in ShoppingCart\n"
          + "filter a.favoriteHolder == '" + cartHolder + "'\n"
          + "return a";
      logger.info("queryToBeExecuted----->" + query);

      ArangoCursor<Object> cursor1 = null;
      try {

        cursor1 = arangoDB.query(query, Object.class);
        response1 = cursor1.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getSaveForLater : " + e.getMessage().toString());
      }
      ArangoCursor<HashMap> cursor = null;
      if (response1.isEmpty()) {
        String queryToBeExecuted1 =
            "INSERT { type: \"Favorites\", createdby: \"Admin\", createdon: \"1599817247388\", lastmodifiedby: \"Admin\",lastmodifiedon: \"1599817247388\",favoriteHolder: '"
                + cartHolder
                + "', favoriteNodes: [], favoritepinCollections: [],cartNodes: []} INTO "
                + ShoppingCart + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

        ArangoCursor<HashMap> cursor3 = null;
        try {

          cursor3 = arangoDB.query(queryToBeExecuted1, HashMap.class);
          response2 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getSaveForLater_2 : " + e.getMessage().toString());
        }

        String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
            + "filter doc.cartNodes != null AND doc.favoriteHolder =='" + cartHolder + "'\r\n"
            + "UPDATE doc WITH { cartNodes: push(doc.cartNodes,{'arangoNodeKey':'"
            + nodekeysList.get(i) + "',savedForLater: \"true\"},true) } IN ShoppingCart";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted);

        //ArangoCursor<HashMap> cursor = null;
        try {

          cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
          response = cursor.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getSaveForLater_3 : " + e.getMessage().toString());
        }
      } else {

        String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
            + "filter doc.cartNodes != null AND doc.favoriteHolder =='" + cartHolder + "'\r\n"
            + "UPDATE doc WITH { cartNodes: push(doc.cartNodes,{'arangoNodeKey':'"
            + nodekeysList.get(i) + "',savedForLater: \"true\"},true) } IN ShoppingCart";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted);

        //ArangoCursor<HashMap> cursor = null;
        try {

          cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
          response = cursor.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getSaveForLater_4 : " + e.getMessage().toString());
        }
      }

    }
    return response;


  }

  public List<Object> getaddToCartFromSaveForLater(String cartHolder) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
        + "filter doc.favoriteHolder == '" + cartHolder + "'\r\n"
        + "for d in doc.cartNodes\r\n"
        + "filter d.savedForLater == \"true\"\r\n"
        + "return d.arangoNodeKey";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getaddToCartFromSaveForLater : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < response.size(); i++) {
      columns.add("node._key == '" + response.get(i) + "'");
      logger.info("columns-->" + columns);
    }
    String columnIds1 = String.join(" OR ", columns);
    String queryToBeExecuted1 = "for node in Nodes\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    ArangoCursor<HashMap> cursor1 = null;

    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getaddToCartFromSaveForLater_2 : " + e.getMessage().toString());
    }
    return connectArango.tailView(response1);
  }

  public List<HashMap> getRemoveCartsFromSaveForLater(String cartHolder, HashMap key) {

    List<HashMap> response = new ArrayList<>();
    List<String> column = new ArrayList<>();
    List<String> nodeKeys = (List<String>) key.get("nodekeys");
    for (int i = 0; i < nodeKeys.size(); i++) {
      column.add("d.arangoNodeKey=='" + nodeKeys.get(i) + "'");
    }
    String columnIds = String.join(" OR ", column);
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in ShoppingCart\r\n"
        + "filter doc.cartNodes != null && doc.favoriteHolder == '" + cartHolder + "'\r\n"
        + "let b=(\r\n"
        + "for d in doc.cartNodes\r\n"
        + "filter " + columnIds + "\r\n"
        + "return d\r\n"
        + ")\r\n"
        + "UPDATE doc WITH { cartNodes:REMOVE_VALUES(doc.cartNodes,b) } IN ShoppingCart";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getRemoveCartsFromSaveForLater : " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> getaddToCartFromSavedForLaterList(String cartHolder, String arangoNodeKey) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        Cart);

    String queryToBeExecuted = "for doc in " + Cart + "\r\n"
        + "filter doc.cartHolder == '" + cartHolder + "'\r\n"
        + "return doc\r\n";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getaddToCartFromSavedForLaterList : " + e.getMessage().toString());
    }

    if (response.isEmpty()) {
      String queryToBeExecuted1 =
          "INSERT {createdby: \"Admin\", createdon: \"1599817247388\", lastmodifiedby: \"Admin\",lastmodifiedon: \"1599817247388\",cartHolder: '"
              + cartHolder + "', cartNodes: []} INTO " + Cart + "";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      ArangoCursor<HashMap> cursor1 = null;
      try {

        cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
        response1 = cursor1.asListRemaining();

      } catch (Exception e) {
        log.error(
            "Exception while getaddToCartFromSavedForLaterList_2 : " + e.getMessage().toString());
      }

      String queryToBeExecuted2 = "for doc in " + Cart + "\r\n"
          + "filter doc.cartNodes != null AND doc.cartHolder == '" + cartHolder + "'\r\n"
          + "UPDATE doc WITH { cartNodes: push(doc.cartNodes,{'arangoNodeKey':'" + arangoNodeKey
          + "'},true) } IN " + Cart + "";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
      try {

        cursor = arangoDB.query(queryToBeExecuted2, HashMap.class);
        response2 = cursor.asListRemaining();

      } catch (Exception e) {
        log.error(
            "Exception while getaddToCartFromSavedForLaterList_3 : " + e.getMessage().toString());
      }

    } else {
      ArangoCursor<HashMap> cursor3 = null;
      String queryToBeExecuted3 = "for doc in " + Cart + "\r\n"
          + "filter doc.cartNodes != null AND doc.cartHolder == '" + cartHolder + "'\r\n"
          + "UPDATE doc WITH { cartNodes: push(doc.cartNodes,{'arangoNodeKey':'" + arangoNodeKey
          + "'},true) } IN " + Cart + "";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
        response3 = cursor3.asListRemaining();

      } catch (Exception e) {
        log.error(
            "Exception while getaddToCartFromSavedForLaterList_4 : " + e.getMessage().toString());
      }
    }
    return response1;

  }

  public List<HashMap> getremoveFromCheckOut(String userEmailId, HashMap nodekeys) {

    List<HashMap> response = new ArrayList<>();
    List<String> column = new ArrayList<>();
    List<String> nodeKeys = (List<String>) nodekeys.get("nodekeys");
    for (int i = 0; i < nodeKeys.size(); i++) {
      column.add("d.arangoNodeKey=='" + nodeKeys.get(i) + "'");
    }
    String columnIds = String.join(" OR ", column);
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //			String queryToBeExecuted="for d in "+ Cart +"\r\n"
    //					+ "filter d.arangoNodeKey == '"+ key +"'\r\n"
    //					+ "Remove d IN "+ Cart +"";
    String queryToBeExecuted = "for doc in " + Cart + "\r\n"
        + "filter doc.cartNodes != null && doc.cartHolder == '" + userEmailId + "'\r\n"
        + "let b=(\r\n"
        + "for d in doc.cartNodes\r\n"
        + "filter " + columnIds + "\r\n"
        + "return d\r\n"
        + ")\r\n"
        + "UPDATE doc WITH { cartNodes:REMOVE_VALUES(doc.cartNodes,b) } IN " + Cart + "";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getremoveFromCheckOut : " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> getclearCart(String cartHolder) {

    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + Cart + "\r\n"
        + "filter doc.cartNodes != null\r\n"
        + "filter doc.cartHolder == '" + cartHolder + "'\r\n"
        + "let b=(\r\n"
        + "for d in doc.cartNodes\r\n"
        + "return d\r\n"
        + ")\r\n"
        + "UPDATE doc WITH { cartNodes:REMOVE_VALUES(doc.cartNodes,b) } IN " + Cart + "";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getclearCart : " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> getSaveForLaterToAddToCart(String cartHolder, HashMap key) {

    List<String> columns = new ArrayList<String>();
    List<String> nodekeysList = (List<String>) key.get("keys");
    for (int i = 0; i < nodekeysList.size(); i++) {
      columns.add("d.arangoNodeKey == '" + nodekeysList.get(i) + "'");
      logger.info("columns-->" + columns);
    }
    String columnIds1 = String.join(" OR ", columns);
    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
        + "filter doc.cartNodes != null AND doc.favoriteHolder == '" + cartHolder + "'\r\n"
        + "update doc with {cartNodes:(for d in doc.cartNodes \r\n"
        + "return  " + columnIds1 + " ? MERGE(d,{savedForLater: \"false\"}):d) } IN ShoppingCart";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<HashMap> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getSaveForLaterToAddToCart : " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> getaddToFavories(String favHolder, HashMap arangoNodekey) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    List<String> nodekeysList = (List<String>) arangoNodekey.get("keys");
    for (int i = 0; i < nodekeysList.size(); i++) {
      String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
          + "filter doc.favoriteHolder == '" + favHolder + "'\r\n"
          + "return doc\r\n";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      ArangoCursor<HashMap> cursor = null;
      try {

        cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
        response = cursor.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getaddToFavories : " + e.getMessage().toString());
      }

      if (response.isEmpty()) {
        String queryToBeExecuted1 =
            "INSERT { type: \"Favorites\", createdby: \"Admin\", createdon: \"1599817247388\", lastmodifiedby: \"Admin\",lastmodifiedon: \"1599817247388\",favoriteHolder: '"
                + favHolder
                + "', favoriteNodes: [], favoritepinCollections: [],cartNodes: []} INTO "
                + ShoppingCart + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

        ArangoCursor<HashMap> cursor1 = null;
        try {

          cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
          response1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getaddToFavories_2 : " + e.getMessage().toString());
        }

        String queryToBeExecuted2 = "for doc in " + ShoppingCart + "\r\n"
            + "filter doc.favoriteNodes != null AND doc.favoriteHolder == '" + favHolder + "'\r\n"
            + "UPDATE doc WITH { favoriteNodes: push(doc.favoriteNodes,{'arangoNodeKey':'"
            + nodekeysList.get(i) + "'},true) } IN ShoppingCart";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
        try {

          cursor = arangoDB.query(queryToBeExecuted2, HashMap.class);
          response2 = cursor.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getaddToFavories_3 : " + e.getMessage().toString());
        }

      } else {
        ArangoCursor<HashMap> cursor3 = null;
        String queryToBeExecuted3 = "for doc in " + ShoppingCart + "\r\n"
            + "filter doc.favoriteNodes != null AND doc.favoriteHolder == '" + favHolder + "'\r\n"
            + "UPDATE doc WITH { favoriteNodes: push(doc.favoriteNodes,{'arangoNodeKey':'"
            + nodekeysList.get(i) + "'},true) } IN ShoppingCart";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

        try {

          cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
          response3 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getaddToFavories_4: " + e.getMessage().toString());
        }
      }
    }
    return response1;

  }

  public List<HashMap> getaddToFavoritesCollection(String favHolder,
      HashMap arangoPinCollectionKey) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    List<String> nodekeysList = (List<String>) arangoPinCollectionKey.get("keys");
    for (int i = 0; i < nodekeysList.size(); i++) {
      String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
          + "filter doc.favoriteHolder == '" + favHolder + "'\r\n"
          + "return doc\r\n";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      ArangoCursor<HashMap> cursor = null;
      try {

        cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
        response = cursor.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getaddToFavoritesCollection : " + e.getMessage().toString());
      }

      if (response.isEmpty()) {
        String queryToBeExecuted1 =
            "INSERT { type: \"Favorites\", createdby: \"Admin\", createdon: \"1599817247388\", lastmodifiedby: \"Admin\",lastmodifiedon: \"1599817247388\",favoriteHolder: '"
                + favHolder + "', favoriteNodes: [], favoritepinCollections: []} INTO "
                + ShoppingCart + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

        ArangoCursor<HashMap> cursor1 = null;
        try {

          cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
          response1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getaddToFavoritesCollection_2 : " + e.getMessage().toString());
        }

        String queryToBeExecuted2 = "for doc in " + ShoppingCart + "\r\n"
            + "filter doc.favoritepinCollections != null AND doc.favoriteHolder == '" + favHolder
            + "'\r\n"
            + "UPDATE doc WITH { favoritepinCollections: push(doc.favoritepinCollections,{'arangoPinCollectionKey':'"
            + nodekeysList.get(i) + "'},true) } IN ShoppingCart";
        //+ "UPDATE doc WITH { favoritepinCollections: push(doc.favoritepinCollections,"+ arangoPinCollectionKey +",true) } IN ShoppingCart";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
        try {

          cursor = arangoDB.query(queryToBeExecuted2, HashMap.class);
          response2 = cursor.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getaddToFavoritesCollection_3 : " + e.getMessage().toString());
        }

      } else {
        ArangoCursor<HashMap> cursor3 = null;
        String queryToBeExecuted3 = "for doc in " + ShoppingCart + "\r\n"
            + "filter doc.favoritepinCollections != null AND doc.favoriteHolder == '" + favHolder
            + "'\r\n"
            + "UPDATE doc WITH { favoritepinCollections: push(doc.favoritepinCollections,{'arangoPinCollectionKey':'"
            + nodekeysList.get(i) + "'},true) } IN ShoppingCart";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

        try {

          cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
          response3 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while getaddToFavoritesCollection_4 : " + e.getMessage().toString());
        }
      }
    }
    return response1;

  }

  public List<HashMap> getremoveFromFavoritesCollection(String favHolder,
      String arangoPinCollectionKey) {

    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //			String queryToBeExecuted="for doc in "+ ShoppingCart +"\r\n"
    //					+ "filter doc.favoritepinCollections != null AND doc.favoriteHolder == '"+ favHolder +"'\r\n"
    //					+ "UPDATE doc WITH { favoritepinCollections: push(doc.favoritepinCollections,"+ arangoPinCollectionKey +",true)} IN ShoppingCart";

    String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
        + "filter doc.favoritepinCollections != null && doc.favoriteHolder == '" + favHolder
        + "'\r\n"
        + "let b=(\r\n"
        + "for d in doc.favoritepinCollections\r\n"
        + "filter d.arangoPinCollectionKey == '" + arangoPinCollectionKey + "'\r\n"
        + "return d\r\n"
        + ")\r\n"
        + "UPDATE doc WITH { favoritepinCollections:REMOVE_VALUES(doc.favoritepinCollections,b) } IN ShoppingCart";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getremoveFromFavoritesCollection : " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> getremoveFromFavoritesNodes(String favHolder, String arangoNodeKey) {
    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //			String queryToBeExecuted="for doc in "+ ShoppingCart +"\r\n"
    //					+ "filter doc.favoritepinCollections != null AND doc.favoriteHolder == '"+ favHolder +"'\r\n"
    //					+ "UPDATE doc WITH { favoritepinCollections: push(doc.favoritepinCollections,"+ arangoPinCollectionKey +",true)} IN ShoppingCart";

    String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
        + "filter doc.favoriteNodes != null && doc.favoriteHolder == '" + favHolder + "'\r\n"
        + "let b=(\r\n"
        + "for d in doc.favoriteNodes\r\n"
        + "filter d.arangoNodeKey == '" + arangoNodeKey + "'\r\n"
        + "return d\r\n"
        + ")\r\n"
        + "UPDATE doc WITH { favoriteNodes:REMOVE_VALUES(doc.favoriteNodes,b) } IN ShoppingCart";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getremoveFromFavoritesNodes : " + e.getMessage().toString());
    }

    return response;

  }

  public List<HashMap> getupdateFavorites(String favHolder, String arangoNodekey) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + ShoppingCart + "\r\n"
        + "filter doc.favoriteHolder == '" + favHolder + "'\r\n"
        + "return doc\r\n";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getupdateFavorites : " + e.getMessage().toString());
    }

    if (response.isEmpty()) {
      String queryToBeExecuted1 =
          "INSERT { type: \"Favorites\", createdby: \"Admin\", createdon: \"1599817247388\", lastmodifiedby: \"Admin\",lastmodifiedon: \"1599817247388\",favoriteHolder: '"
              + favHolder + "', favoriteNodes: [], favoritepinCollections: []} INTO "
              + ShoppingCart + "";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      ArangoCursor<HashMap> cursor1 = null;
      try {

        cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
        response1 = cursor1.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getupdateFavorites_2 : " + e.getMessage().toString());
      }

      String queryToBeExecuted2 = "for doc in " + ShoppingCart + "\r\n"
          + "filter doc.favoriteNodes != null AND doc.favoriteHolder == '" + favHolder + "'\r\n"
          + "UPDATE doc WITH { favoriteNodes: push(doc.favoriteNodes,{'arangoNodeKey':'"
          + arangoNodekey + "'},true) } IN ShoppingCart";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
      try {

        cursor = arangoDB.query(queryToBeExecuted2, HashMap.class);
        response2 = cursor.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getupdateFavorites_3 : " + e.getMessage().toString());
      }

    } else {
      ArangoCursor<HashMap> cursor3 = null;
      String queryToBeExecuted3 = "for doc in " + ShoppingCart + "\r\n"
          + "filter doc.favoriteNodes != null AND doc.favoriteHolder == '" + favHolder + "'\r\n"
          + "UPDATE doc WITH { favoriteNodes: push(doc.favoriteNodes,{'arangoNodeKey':'"
          + arangoNodekey + "'},true) } IN ShoppingCart";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
        response3 = cursor3.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getupdateFavorites_4 : " + e.getMessage().toString());
      }
    }
    return response1;

  }

  public List<Object> getFavoriteNodes(String favHolder) {
    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in ShoppingCart\r\n"
        + "filter a.favoriteNodes !=null && a.favoriteHolder == '" + favHolder + "'\r\n"
        + "for b in a.favoriteNodes\r\n"
        + "return b.arangoNodeKey";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getFavoriteNodes : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < response.size(); i++) {
      columns.add("node._key =='" + response.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    String queryToBeExecuted1 = "for node in Nodes\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getFavoriteNodes_2 : " + e.getMessage().toString());
    }

    return connectArango.tailView(response1);

  }

  public List<Object> getFavoriteCollection(String favHolder) {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<Object> pinResponse = new ArrayList<>();
    final List<String>[] response2 = new List[]{new ArrayList<String>()};
    JSONObject pins = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for a in ShoppingCart\r\n"
        + "filter a.favoritepinCollections !=null && a.favoriteHolder == '" + favHolder + "'\r\n"
        + "for b in a.favoritepinCollections\r\n"
        + "return b.arangoPinCollectionKey";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    ArangoCursor<String> cursor1 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getFavoriteCollection : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < response.size(); i++) {
      columns.add("node._key =='" + response.get(i) + "'");
      logger.info("columns-->" + columns);
    }
    String columnIds1 = String.join(" OR ", columns);
    String queryToBeExecuted1 = "for node in PinCollection\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, String.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getFavoriteCollection_2 : " + e.getMessage().toString());
    }
    response1.forEach(a -> {
      JSONObject pin = new JSONObject(a);
      logger.info(String.valueOf(pin));
      String pinkey = pin.getString("_key");
      String displayName = pin.getString("displayName");
      String cover = pin.getString("cover");
      String classification = pin.getString("classification");
      //  JSONArray tags = pin.getJSONArray("tags");
      JSONArray pinNodes = pin.getJSONArray("pinNodes");
      JSONArray pinCollection = pin.getJSONArray("pinCollection");
      pins.put("key", pinkey);
      pins.put("displayName", displayName);
      pins.put("cover", cover);
      pins.put("classification", classification);
      // pins.put("tags", tags);
      pins.put("pinNodes", pinNodes);
      pins.put("pinCollection", pinCollection);
      response2[0] = connectArango.getCurateNodes(pinkey);

      if(!response2[0].isEmpty()){
        pins.put("curate", true);
      }else{
        pins.put("curate", false);
      }
      pinResponse.add(pins.toMap());
    });

    return pinResponse;
  }

  private String serviceNowMethod(String response1) throws ServiceUnavailable {
    try {
      System.out.println("response1" + response1);
    //  String url = "http://162.241.69.133:5050/createIncident";
      //String url = "http://localhost:5050/createIncident";
      JSONObject s = new JSONObject(response1);
      logger.info(String.valueOf(s));
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
      HttpEntity<String> entity = new HttpEntity<>(response1, headers);
      ResponseEntity<String> responseString = restTemplate.exchange(serviceNowUrl, HttpMethod.POST, entity,
          String.class);
      String response = responseString.getBody();

      log.info("Status : " + responseString.getStatusCodeValue());
      return response;
    } catch (Exception e) {
      throw new ServiceUnavailable("ServiceNow instance Down");
    }

  }

  public List<HashMap> graphFilters(String name) {

    List<HashMap> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "let AllWords=(\r\n"
        + "let AllWordsMatch = (FOR a in " + viewName + "\r\n"
        + "SEARCH ANALYZER(a.source.displayName IN TOKENS('" + name
        + "','text_en'),'text_en')\r\n"
        + "filter a.source.displayName != '" + name + "'\r\n"
        + "SORT BM25(a) ASC\r\n"
        + "RETURN a.source.displayName)\r\n"
        + "let AllWordsNgram=(FOR doc IN " + viewName + "\r\n"
        + "SEARCH NGRAM_MATCH(doc.source.displayName,'" + name
        + "',0.3,'fuzzy_search_bigram')\r\n"
        + "sort BM25(doc) DESC\r\n"
        + "return doc.source.displayName)\r\n"
        + "let AllWordsLevenshtein=(FOR doc IN " + viewName + "\r\n"
        + "  SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.source.displayName,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
        + "  SORT BM25(doc) DESC\r\n"
        + "  return doc.source.displayName)\r\n"
        + "  return intersection(AllWordsMatch,AllWordsNgram,AllWordsLevenshtein)\r\n"
        + ")\r\n"
        + " let exactMatch=(FOR a in " + viewName + "\r\n"
        + "SEARCH ANALYZER (PHRASE(a.source.displayName,'" + name + "'),\"text_en\")\r\n"
        + "SORT BM25(a) ASC\r\n"
        + "RETURN a.source.displayName)\r\n"
        + "let oneWord=(\r\n"
        + "let oneWordMatch = (FOR a in " + viewName + "\r\n"
        + "SEARCH ANALYZER(a.source.displayName IN TOKENS('" + name
        + "','text_en'),'text_en')\r\n"
        + "filter a.source.displayName != '" + name + "'\r\n"
        + "SORT BM25(a) ASC\r\n"
        + "RETURN a.source.displayName)\r\n"
        + "let OneWordFuzzy=(FOR doc IN " + viewName + "\r\n"
        + "SEARCH NGRAM_MATCH(\r\n"
        + "doc.source.displayName,'" + name + "',0.3,'fuzzy_search_bigram')\r\n"
        + "sort BM25(doc) DESC\r\n"
        + "return doc.source.displayName)\r\n"
        + "let oneWordLevenshtein=(FOR doc IN " + viewName + "\r\n"
        + " SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.source.displayName,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],4,false),\"text_en_no_stem\")\r\n"
        + " filter doc.source.displayName != '" + name + "'\r\n"
        + " SORT BM25(doc) DESC\r\n"
        + " return doc.source.displayName)\r\n"
        + " return intersection(oneWordMatch,OneWordFuzzy,oneWordLevenshtein))\r\n"
        + "return {AllWords:unique(AllWords),ExactMatch:unique(exactMatch),OneWord:unique(oneWord)}\r\n"
        + "";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphFilters : " + e.getMessage().toString());
    }

    return response;

  }

  public List<Object> getorganizationFunctions() {
    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + arangoNodeTypesCollection + "\r\n"
        + "filter doc.organizationFunctions\r\n"
        + "return unique(doc.organizationFunctions)";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getorganizationFunctions : " + e.getMessage().toString());
    }

    return response;

  }

  public List<Object> addOrganizationFunctions(HashMap value) {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String OrganizationFunctions = value.get("OrganizationFunctions").toString();
    String queryToBeExecuted = "for doc in " + arangoNodeTypesCollection + "\r\n"
        + "filter doc._key == \"775320\"\r\n"
        + "update doc With {organizationFunctions:push(doc.organizationFunctions,'"
        + OrganizationFunctions + "')} in NodeTypes";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while addOrganizationFunctions : " + e.getMessage().toString());
    }

    return response;

  }

  public String getplatformAvailability(String name) {
    String res = null;
    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + viewName + "\r\n"
        + "filter doc.source.displayName == '" + name
        + "' &&  doc.source.type == \"Data Set\" && doc.role == \"hosts\"\r\n"
        + "return doc.target.type";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getplatformAvailability : " + e.getMessage().toString());
    }

    if (response.get(0).toString().equals("Microsoft")) {
      res = "is available on microsoft";
    } else {
      res = "is not available on microsoft";
    }

    return res;


  }

  public String getplatformAvailability1(String id, String platform) {
    columNamesPlatform.clear();
    String res = null;

    List<String> response = new ArrayList<>();
    ArangoCursor<String> cursor = null;
    List<String> result = new ArrayList<String>();
    //	List<String> columNamesPlatform = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //				String queryToBeExecuted="FOR doc IN nodesView\r\n"
    //						+ "SEARCH ANALYZER(doc.name IN TOKENS('"+ name +"', \"text_en\"),\"text_en\")\r\n"
    //						+ "filter doc.type.name == \"Data Set\"\r\n"
    //						+ "SORT BM25(doc)\r\n"
    //						+ "return doc.relations";
    String queryToBeExecuted = "for doc in Nodes\r\n"
        + "filter doc.type.name == \"Data Set\" AND doc.id =='" + id + "'\r\n"
        + "return doc.relations";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getplatformAvailability1 : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    response.forEach(datasettargetsObject -> {

      JSONObject eachRelation = new JSONObject(datasettargetsObject);
      logger.info(String.valueOf(eachRelation));
      JSONArray targetedges = eachRelation.getJSONArray("targets");

      if (!targetedges.isEmpty()) {
        targetedges.forEach(eachsource -> {
          JSONObject targets = new JSONObject(eachsource.toString());
          logger.info(String.valueOf(targets));
          JSONObject target = targets.getJSONObject("target");
          logger.info(String.valueOf(target));
          String Id = target.getString("id");
          if (target.getString("type").toString().equals("Platform")) {
            columNamesPlatform.add(target.get("name").toString());
          } else if (target.getString("type").toString().equals("DataProduct")) {
            getnodeResponse1(Id);
            //								columNamesPlatform.add( target.get("name").toString());
          }
        });
      }
    });

    if (columNamesPlatform.get(0).toString().equals(platform)) {
      res = "true";
    } else {
      res = "false";
    }

    return res;

  }

  public List<String> getnodeResponse1(String id) {
    List<String> columns = new ArrayList<String>();
    String Id = null;

    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    final String query = us.getQueryResultId(id);
    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getnodeResponse1 : " + e.getMessage().toString());
    }

    response.forEach(nodesinfo -> {
      JSONObject nodes = new JSONObject(nodesinfo);
      String str = nodes.getString("displayName");

      JSONObject edges = (JSONObject) nodes.get("relations");
      JSONArray targetedges = edges.getJSONArray("targets");

      if (!targetedges.isEmpty()) {
        targetedges.forEach(eachsource -> {
          JSONObject targets = new JSONObject(eachsource.toString());
          //String targetname=targets.getString("CoRole");
          String targetname = null;
          if (targets.has("CoRole")) {
            targetname = targets.getString("CoRole");
          } else {
            targetname = targets.getString("coRole");
          }
          JSONObject target = targets.getJSONObject("target");

          String type = target.getString("type");
          if (type.contains("Platform")) {
            columNamesPlatform.add(target.getString("name"));
          }
        });
      }
    });

    return columNamesPlatform;
  }


  public List<Object> getcheckoutdetailsForm3(HashMap details) throws ServiceUnavailable {

    List<HashMap> response1 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<Object> responseMessageList = new ArrayList<>();
    JSONObject responseMessage = new JSONObject();
    JSONObject link = new JSONObject();
    List<Object> requetForList = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        DataUsage);

    ArangoCollection arangoCollection1 = arangorestclient.getArangoCollection(arangoDB,
        DataUsageArchiveCollection);

    ArangoCollection arangoCollection2 = arangorestclient.getArangoCollection(arangoDB,
        GcpDataUsage);

    ArangoCollection arangoCollection3 = arangorestclient.getArangoCollection(arangoDB,
        alationDataUsage);
    ArangoCollection arangoEdgeCollection = arangorestclient.getArangoEdgeCollection(arangoDB,
        Orders);

    List<HashMap> Requestedfor = (List<HashMap>) details.get("Requestedfor");
    List<String> orderIds = (List<String>) details.get("orderIds");
    List<String> orderUrls = (List<String>) details.get("orderUrls");
    List<String> columns = new ArrayList<String>();
    String columnIds1 = String.join(" OR ", columns);
    Requestedfor.forEach(refor -> {
      JSONObject requestFor = new JSONObject(refor);
      JSONObject requetFor = new JSONObject();
      String name = requestFor.getString("name");
      String id = requestFor.getString("id");
      requetFor.put("name", name);
      requetFor.put("id", id);
      logger.info("requetFor" + requetFor);

      requetForList.add(requetFor);
    });
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    List<String> columns3 = new ArrayList<String>();
    String columnIds2 = String.join(" OR ", columns1);
    for (int i = 0; i < orderIds.size(); i++) {
      columns2.add("'" + orderIds.get(i) + "'");
      columns1.add("Nodes/" + orderIds.get(i) + "");
      logger.info("columns-->" + columns1);
    }

    for (int i = 0; i < orderUrls.size(); i++) {
      columns3.add("'" + orderUrls.get(i) + "'");
    }

    String Purpose = details.get("Purpose").toString();
    String StartDate = details.get("StartDate").toString();
    String[] datesplit = StartDate.split("-");
    String sdate = datesplit[0];
    String smonth = datesplit[1];
    String syear = datesplit[2];
    String SDate = syear + "-" + smonth + "-" + sdate;

    String EndDate = details.get("EndDate").toString();
    String[] datesplit1 = EndDate.split("-");
    String edate = datesplit1[0];
    String emonth = datesplit1[1];
    String eyear = datesplit1[2];
    String EDate = eyear + "-" + emonth + "-" + edate;

    String Type = "Data Usage";
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String DeliveryPreferences = details.get("DeliveryPreferences").toString();
    String DeliveryPlatform = details.get("DeliveryPlatform").toString();
    String DataRefreshFrequency = details.get("DataRefreshFrequency").toString();
    String Priority = details.get("Priority").toString();
    String RequesterMailId = details.get("RequesterMailId").toString();
    String CallerName = details.get("CallerName").toString();
    String createdBy = details.get("createdBy").toString();
    String PrivacyPolicy = details.get("PrivacyPolicy").toString();
    String DSA = details.get("DSA").toString();

    if (DeliveryPlatform.equals("ServiceNow")) {
      dataUsageCheckoutOptions(arangoDB, DeliveryPreferences, DeliveryPlatform,
          DataRefreshFrequency, Priority);
      response3 = dataUsageCreation(arangoDB, requetForList, Purpose, StartDate, EndDate,
          DeliveryPreferences, DeliveryPlatform, DataRefreshFrequency, Priority,
          RequesterMailId, CallerName, Type, datestr, createdBy, PrivacyPolicy, columns2,
          columns3, DSA);
      response1 = getQueryResponse(arangoDB, response3, columns1);
      responseMessageList = serviceNowRequest(response1);
      storeDataUsages(response3.get(0));
    } else if (DeliveryPlatform.equals("GCP BigQuery")) {
      try {
        List<String> tableIdsList = gcpNodeId(orderIds);
        JSONObject reqBody = new JSONObject();
        JSONObject accessRequest = new JSONObject();
        JSONArray tableIds = new JSONArray();
        accessRequest.put("roleId", "roles/bigquery.dataViewer");
        accessRequest.put("accessRequestPrincipal", "user:" + RequesterMailId);
        accessRequest.put("tableIds", tableIdsList);
        reqBody.put("accessRequest", accessRequest);
        reqBody.put("effectiveStartDate", StartDate);
        reqBody.put("effectiveEndDate", EndDate);
        log.info("requestBody for GCP Access :{}", reqBody);
        String project = null;
        String dataset = null;
        String table = null;
        for (int l = 0; l < tableIdsList.size(); l++) {
          String x = tableIdsList.get(l);
          String[] url = x.replaceAll("\\.", " ").split("\\s+");
          logger.info(String.valueOf(url));
          project = url[0];
          dataset = url[1];
          table = url[2];
          link.put("url", "https://console.cloud.google.com");
          link.put("title", "GCP Console URL:");
          link.put("description",
              "Sign In and Select Organisation and Below Table Hierarchy..Project: " + project
                  + ",Dataset:" + dataset + ",Table:" + table);
          responseMessage.put("link", link);
        }
        String urls = "https://console.cloud.google.com";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(reqBody.toString(), headers);
        ResponseEntity<String> responses = restTemplate.exchange(gcpUrl, HttpMethod.POST,
            entity, String.class);
        int statuscode = responses.getStatusCodeValue();
        if (statuscode == 201 || statuscode == 200) {
          log.info("Access Granted successfully");
        } else {
          ResponseEntity.status(statuscode)
              .body("Failed !! ServicenowClientFactory Due to " + responses.getBody());
        }
        response3 = dataGcpUsageCreation(arangoDB, requetForList, Purpose, StartDate, EndDate,
            DeliveryPreferences, DeliveryPlatform, DataRefreshFrequency, Priority,
            RequesterMailId, CallerName, Type, datestr, createdBy, PrivacyPolicy, columns2,
            columns3, DSA, accessRequest, urls);
        response1 = getQueryResponse(arangoDB, response3, columns1);
        responseMessage.put("OrderId", response3.get(0));
        responseMessageList.add(responseMessage.toMap());
      } catch (Exception e) {
        throw new NotAcceptableException("User not register OR Select GCP specific node");
      }
      storeDataUsages(response3.get(0));

    } else if (DeliveryPlatform.equals("Ranger")) {
      try {
        List<Integer> tableIdsList = rangerNodeId(orderIds);
        JSONObject reqBody = new JSONObject();
        JSONObject accessRequest = new JSONObject();
        JSONArray tableIds = new JSONArray();
        accessRequest.put("policyId", tableIdsList.get(0));
        accessRequest.put("accessRequestPrincipal", RequesterMailId);
        accessRequest.put("accessRequestPrivilage", "['READ','SELECT']");
        reqBody.put("rangerPolicyUpdate", accessRequest);
        updatePolicyInRanger(reqBody);
        response3 = dataUsageCreation(arangoDB, requetForList, Purpose, StartDate, EndDate,
            DeliveryPreferences, DeliveryPlatform, DataRefreshFrequency, Priority,
            RequesterMailId, CallerName, Type, datestr, createdBy, PrivacyPolicy, columns2,
            columns3, DSA);
        response1 = getQueryResponse(arangoDB, response3, columns1);
        responseMessage.put("OrderId", response3.get(0));
        responseMessageList.add(responseMessage.toMap());
      } catch (Exception e) {
        throw new NotAcceptableException("User not register OR Select Ranger specific node");
      }
      storeDataUsages(response3.get(0));
    } else if (DeliveryPlatform.equals("Slack")) {
      String status = "Pending";
      response3 = dataUsageCreationOfSlack(arangoDB, requetForList, Purpose, StartDate, EndDate,
          DeliveryPreferences, DeliveryPlatform, DataRefreshFrequency, Priority, RequesterMailId,
          CallerName, Type, datestr, createdBy, PrivacyPolicy, columns2, columns3, DSA, status);
      response1 = getQueryResponse(arangoDB, response3, columns1);
      JSONObject slackDetails = new JSONObject();
      slackDetails.put("datasetUrl", orderUrls.get(0));
      slackDetails.put("requester", RequesterMailId);
      //slackDetails.put("owner", owner);
      slackDetails.put("approver", steward);
      slackRequest(slackDetails.toString());
      responseMessage.put("OrderId", response3.get(0));
      responseMessage.put("nodeViewerUrl", orderUrls.get(0));
      responseMessageList.add(responseMessage.toMap());
      storeDataUsages(response3.get(0));
    } else if (DeliveryPlatform.equals("Teams")) {
      try {
        String status = "Pending";
        response3 = dataUsageCreationOfSlack(arangoDB, requetForList, Purpose, StartDate, EndDate,
            DeliveryPreferences, DeliveryPlatform, DataRefreshFrequency, Priority, RequesterMailId,
            CallerName, Type, datestr, createdBy, PrivacyPolicy, columns2, columns3, DSA, status);
        response1 = getQueryResponse(arangoDB, response3, columns1);
        JSONObject TeamsDetails = new JSONObject();

        //Map<String, Object> map = new HashMap<>();
        //TeamsDetails.put("title", "Data Set Approval");
        //TeamsDetails.put("text", "please approve the request");
        List<Object> potentialAction = new ArrayList<>();
        List<Object> action = new ArrayList<>();
        JSONObject teamDetails = new JSONObject();
        teamDetails.put("title", "Data Set Approval Request");
        teamDetails.put("text", "do you want to approve the Data Set" + orderUrls.get(0) + "");

        JSONObject ptn = new JSONObject();

        ptn.put("@type", "ActionCard");
        ptn.put("name", "Approve");

        JSONObject actn1 = new JSONObject();
        JSONObject actn2 = new JSONObject();

        actn1.put("@type", "HttpPOST");
        actn1.put("name", "Approve");
        actn1.put("isPrimary", true);
        actn1.put("target",
            "https://teams.microsoft.com/_?culture=en-in&country=in#/conversations/General?threadId=19:3wPdUMR5RDPekIrCr38hYEFI6d22--vpWsDcXWiBGGA1@thread.tacv2&ctx=channel");
        action.add(actn1);

        actn2.put("@type", "HttpPOST");
        actn2.put("name", "Reject");
        actn2.put("isPrimary", true);
        actn2.put("target",
            "https://teams.microsoft.com/_?culture=en-in&country=in#/conversations/General?threadId=19:3wPdUMR5RDPekIrCr38hYEFI6d22--vpWsDcXWiBGGA1@thread.tacv2&ctx=channel");
        action.add(actn2);
        ptn.put("actions", action);
        potentialAction.add(ptn);
        teamDetails.put("potentialAction", potentialAction);
        //map.put("Approvers", "siri@lorangtechnologies.com");
        //TeamsDetails.put("Priority", Priority);
        //map.put("datasetUrl", orderUrls.get(0));
        //teamRequest(map);
        teamRequest(teamDetails.toString());
        responseMessage.put("OrderId", response3.get(0));
        responseMessage.put("nodeViewerUrl", orderUrls.get(0));
        responseMessageList.add(responseMessage.toMap());
      } catch (Exception e) {
        throw new NotAcceptableException("Unable to connect Teams");
      }
      storeDataUsages(response3.get(0));
    } else {
      try {
        List<String> urls = new ArrayList<>();
        List<Integer> alationIds = new ArrayList<>();
        JSONObject alationId = new JSONObject();
        for (int l = 0; l < orderIds.size(); l++) {
          String id = orderIds.get(l);
          urls = getalationUrl(id);
          logger.info("urls" + urls);
          alationIds = getAlationIds(id);
          alationId.put("alationId", alationIds.get(0));
        }
        String queryUrl = alationAppicationUrl + alationIds.get(0);
        link.put("url", queryUrl);
        link.put("fileName", "AlationQueryResult" + alationIds.get(0) + ".csv");
        responseMessage.put("link", link);
        response3 = dataUsageAlationCreation(arangoDB, requetForList, Purpose, StartDate,
            EndDate, DeliveryPreferences, DeliveryPlatform, DataRefreshFrequency, Priority,
            RequesterMailId, CallerName, Type, datestr, createdBy, PrivacyPolicy, columns2,
            columns3, DSA, queryUrl);
        response1 = getQueryResponse(arangoDB, response3, columns1);
        responseMessage.put("OrderId", response3.get(0));
        responseMessageList.add(responseMessage.toMap());
      } catch (Exception e) {
        throw new NotAcceptableException(
            "please select one node to checkout OR Select Alation specific node");
      }
      storeDataUsages(response3.get(0));
    }
    return responseMessageList;
  }

  private String slackRequest(String slackResponse) {
    try {
      // String url = "https://hooks.slack.com/workflows/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";
      JSONObject s = new JSONObject(slackResponse);
      logger.info(String.valueOf(s));
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
      HttpEntity<String> entity = new HttpEntity<>(slackResponse, headers);
      ResponseEntity<String> responseString = restTemplate.exchange(slackUrl, HttpMethod.POST,
          entity, String.class);
      String response = responseString.getBody();

      log.info("Status : " + responseString.getStatusCodeValue());
      return response;
    } catch (Exception e) {
      throw new ServiceUnavailable(" unable to connect slack");
    }

  }

  private String teamRequest(String teamsresponse) {
    try {
      System.out.println("teamsresponse" + teamsresponse);
      // String url = "https://datanomist486.webhook.office.com/webhookb2/54ccffdf-3277-4bc1-9a60-9b5b6a5a9dd8@46f06dc7-7485-4a3a-a7d4-356dfa46f447/IncomingWebhook/083088c6beca4e42b30ddcd639b8cb82/5480c1cf-2e0d-4196-bd50-dd19bdb418f1";
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> entity = new HttpEntity<>(teamsresponse, headers);
      ResponseEntity<String> responseString = restTemplate.exchange(teamsUrl, HttpMethod.POST,
          entity, String.class);
      String response = responseString.getBody();
      return response;
    } catch (Exception e) {
      throw new ServiceUnavailable(" unable to connect slack");
    }

  }

  private List<Object> dataUsageCreationOfSlack(ArangoDatabase arangodb, List<Object> requetForList,
      String purpose, String startDate, String endDate,
      String deliveryPreferences, String deliveryPlatform, String dataRefreshFrequency,
      String priority,
      String requesterMailId, String callerName, String type, String datestr, String createdBy,
      String privacyPolicy, List<String> columns2, List<String> columns3, String dsa,
      String status) {

    List<Object> response3 = new ArrayList<>();

    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query =
        "INSERT {requesterNames:" + requetForList + ",Purpose:'" + purpose + "',StartDate:'"
            + startDate + "',EndDate:'" + endDate + "',deliveryPreferences:'" + deliveryPreferences
            + "',deliveryPlatform:'" + deliveryPlatform + "',dataRefreshFrequency:'"
            + dataRefreshFrequency + "',priority:'" + priority + "',requesterMailID:'"
            + requesterMailId + "',callerName:'" + callerName + "',Type:'" + type + "',createdOn:'"
            + datestr + "',createdBy:'" + createdBy + "',PrivacyPolicy:'" + privacyPolicy
            + "',orderIds:" + columns2 + ",orderUrls:" + columns3 + ",DSA:'" + dsa
            + "',IncidentState:'" + status + "'} In " + DataUsage + "\r\n"
            + "return NEW._key";
    System.out.println("queryToBeExecuted----->" + query);
    try {

      cursor = arangoDB.query(query, Object.class);
      response3 = cursor.asListRemaining();
      System.out.println("queryToBeExecuted----->" + response3);

    } catch (Exception e) {
      log.error("Exception while dataUsageCreationOfSlack : " + e.getMessage().toString());
    }

    return response3;
  }

  private List<Object> dataGcpUsageCreation(ArangoDatabase arangodb, List<Object> requetForList,
      String purpose,
      String startDate, String endDate, String deliveryPreferences, String deliveryPlatform,
      String dataRefreshFrequency, String priority, String requesterMailId, String callerName,
      String type,
      String datestr, String createdBy, String privacyPolicy, List<String> columns2,
      List<String> columns3,
      String dSA, JSONObject accessRequest, String urls) {

    List<Object> response3 = new ArrayList<>();

    ArangoCursor<Object> cursor = null;
    String query =
        "INSERT {requesterNames:" + requetForList + ",Purpose:'" + purpose + "',StartDate:'"
            + startDate + "',EndDate:'" + endDate + "',deliveryPreferences:'" + deliveryPreferences
            + "',deliveryPlatform:'" + deliveryPlatform + "',dataRefreshFrequency:'"
            + dataRefreshFrequency + "',priority:'" + priority + "',requesterMailID:'"
            + requesterMailId + "',callerName:'" + callerName + "',Type:'" + type + "',createdOn:'"
            + datestr + "',createdBy:'" + createdBy + "',PrivacyPolicy:'" + privacyPolicy
            + "',itemIds:" + columns2 + ",itemUrls:" + columns3 + ",accessRequest:"
            + accessRequest + ",url:'" + urls + "',DSA:'" + dSA + "'} In " + DataUsage + "\r\n"
            + "return NEW._key";
//		String query="INSERT {requesterNames:"+ requetForList +",Purpose:'"+ Purpose +"',StartDate:'"+ StartDate +"',EndDate:'"+ EndDate +"',deliveryPreferences:'"+ DeliveryPreferences +"',deliveryPlatform:'"+ DeliveryPlatform +"',dataRefreshFrequency:'"+ DataRefreshFrequency +"',priority:'"+ Priority +"',requesterMailID:'"+ RequesterMailId +"',callerName:'"+ CallerName +"',Type:'"+ Type +"',createdOn:'"+ datestr +"',createdBy:'"+ createdBy +"',PrivacyPolicy:'"+ PrivacyPolicy +"',orderIds:"+ columns2 +",orderUrls:"+ columns3 +",accessRequest:"+accessRequest+",url:'"+urls+"',DSA:'"+DSA+"'} In "+ DataUsage +"\r\n"
//				+ "return NEW._key";

    logger.info("queryToBeExecuted----->" + query);
    try {

      cursor = arangodb.query(query, Object.class);
      response3 = cursor.asListRemaining();
      logger.info("queryToBeExecuted----->" + response3);

    } catch (Exception e) {
      log.error("Exception while dataGcpUsageCreation : " + e.getMessage().toString());
    }

    return response3;
  }


  private List<Object> dataUsageAlationCreation(ArangoDatabase arangodb, List<Object> requetForList,
      String purpose,
      String startDate, String endDate, String deliveryPreferences, String deliveryPlatform,
      String dataRefreshFrequency, String priority, String requesterMailId, String callerName,
      String type,
      String datestr, String createdBy, String privacyPolicy, List<String> columns2,
      List<String> columns3,
      String dSA, String queryUrl) {

    List<Object> response3 = new ArrayList<>();

    ArangoCursor<Object> cursor = null;
    String query =
        "INSERT {requesterNames:" + requetForList + ",Purpose:'" + purpose + "',StartDate:'"
            + startDate + "',EndDate:'" + endDate + "',deliveryPreferences:'" + deliveryPreferences
            + "',deliveryPlatform:'" + deliveryPlatform + "',dataRefreshFrequency:'"
            + dataRefreshFrequency + "',priority:'" + priority + "',requesterMailID:'"
            + requesterMailId + "',callerName:'" + callerName + "',Type:'" + type + "',createdOn:'"
            + datestr + "',createdBy:'" + createdBy + "',PrivacyPolicy:'" + privacyPolicy
            + "',itemIds:" + columns2 + ",itemUrls:" + columns3 + ",url:'" + queryUrl + "',DSA:'"
            + dSA + "'} In " + DataUsage + "\r\n"
            + "return NEW._key";

    logger.info("queryToBeExecuted----->" + query);
    try {

      cursor = arangodb.query(query, Object.class);
      response3 = cursor.asListRemaining();
      logger.info("queryToBeExecuted----->" + response3);

    } catch (Exception e) {
      log.error("Exception while dataUsageAlationCreation : " + e.getMessage().toString());
    }

    return response3;

  }


  private List<Object> serviceNowRequest(List<HashMap> response1) {
    List<String> rnames = new ArrayList<String>();
    JSONObject res = new JSONObject();
    JSONObject responseMessage = new JSONObject();
    List<Object> responseMessageList = new ArrayList<>();
    response1.forEach(a -> {
      //return {deliveryPreferences:doc.deliveryPreferences, priority:doc.priority, deliveryPlatform:doc.deliveryPlatform , dataRefreshFrequency: doc.dataRefreshFrequency, requesterMailID:doc.requesterMailID , callerName:doc.callerName ,requesterNames:doc.requesterNames,dataUsageId:doc._id}
      JSONObject s = new JSONObject(a);
      String deliveryPreferences = s.getString("deliveryPreferences");
      res.put("deliveryPreferences", deliveryPreferences);
      String priority = s.getString("priority").toString();
      res.put("priority", priority);
      String deliveryPlatform = s.getString("deliveryPlatform").toString();
      res.put("deliveryPlatform", deliveryPlatform);
      String dataRefreshFrequency = s.getString("dataRefreshFrequency").toString();
      res.put("dataRefreshFrequency", dataRefreshFrequency);
      String requesterMailID = s.getString("requesterMailID");
      res.put("requesterMailID", requesterMailID);
      //responseMessage.put("requesterMailID", requesterMailID);
      String callerName = s.getString("callerName");
      res.put("callerName", callerName);
      //List<Object> requesterNames= (List<Object>) s.getJSONArray("requesterNames");
      JSONArray requesterNames = s.getJSONArray("requesterNames");
      logger.info("requesterNames" + requesterNames);

      requesterNames.forEach(reNames -> {
        JSONObject names = new JSONObject(reNames.toString());
        logger.info("names" + names);
        String name = (String) names.get("name");
        rnames.add(name);
        //rnames.clear();
        logger.info("rnames" + rnames);
      });
      res.put("requesterNames", rnames.toString());
      JSONArray orderUrl = s.getJSONArray("itemUrls");
      //.getString("requesterNames");
      res.put("orderUrls", orderUrl);
      String dataUsageId = s.getString("_id");
      //DatausageID=s.getString("_id");
      res.put("dataUsageId", dataUsageId);
      String dataUsageKey = s.getString("_key");
      responseMessage.put("OrderId", dataUsageKey);
      String startDate = s.getString("StartDate");
      res.put("startDate", startDate);
      String endDate = s.getString("EndDate");
      res.put("endDate", endDate);
      logger.info("res" + res);
      logger.info(res.toString());

    });

    String servicenowResponse = serviceNowMethod(res.toString());
    if (servicenowResponse.equals(
        "successfully created incident and stored Incidentstate and IncidentNo in DataUsage collection")) {
      responseMessageList.add(responseMessage.toMap());

    }
    return responseMessageList;
  }


  private List<HashMap> getQueryResponse(ArangoDatabase arangodb, List<Object> response3,
      List<String> columns1) {

    List<HashMap> response1 = new ArrayList<>();
    String queryToBeExecuted = "for doc in " + DataUsage + "\r\n"
        + "filter doc._key == '" + response3.get(0) + "'\r\n"
        + "return doc";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangodb.query(queryToBeExecuted, HashMap.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getQueryResponse : " + e.getMessage().toString());
    }

    for (int i = 0; i < columns1.size(); i++) {
      String id = columns1.get(i);
      String queryToBeExecuted1 = "for doc in " + DataUsage + "\r\n"
          + "filter doc._key=='" + response3.get(0) + "'\r\n"
          + "INSERT {_from: doc._id, _to: '" + id
          + "',StartDate:doc.StartDate,EndDate:doc.EndDate,lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO "
          + Orders + "";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<HashMap> cursor2 = null;
      try {
        cursor2 = arangodb.query(queryToBeExecuted1, HashMap.class);
      } catch (Exception e) {
        log.error("Exception while getQueryResponse_2 : " + e.getMessage().toString());
      }
    }

    return response1;

  }


  private List<Object> dataUsageCreation(ArangoDatabase arangodb, List<Object> requetForList,
      String purpose, String startDate, String endDate,
      String deliveryPreferences, String deliveryPlatform, String dataRefreshFrequency,
      String priority,
      String requesterMailId, String callerName, String type, String datestr, String createdBy,
      String privacyPolicy, List<String> columns2, List<String> columns3, String dsa) {

    List<Object> response3 = new ArrayList<>();

    ArangoCursor<Object> cursor = null;
    String query =
        "INSERT {requesterNames:" + requetForList + ",Purpose:'" + purpose + "',StartDate:'"
            + startDate + "',EndDate:'" + endDate + "',deliveryPreferences:'" + deliveryPreferences
            + "',deliveryPlatform:'" + deliveryPlatform + "',dataRefreshFrequency:'"
            + dataRefreshFrequency + "',priority:'" + priority + "',requesterMailID:'"
            + requesterMailId + "',callerName:'" + callerName + "',Type:'" + type + "',createdOn:'"
            + datestr + "',createdBy:'" + createdBy + "',PrivacyPolicy:'" + privacyPolicy
            + "',itemIds:" + columns2 + ",itemUrls:" + columns3 + ",DSA:'" + dsa + "'} In "
            + DataUsage + "\r\n"
            + "return NEW._key";
    logger.info("queryToBeExecuted----->" + query);
    try {

      cursor = arangodb.query(query, Object.class);
      response3 = cursor.asListRemaining();
      logger.info("queryToBeExecuted----->" + response3);

    } catch (Exception e) {
      log.error("Exception while dataUsageCreation : " + e.getMessage().toString());
    }

    return response3;
  }


  private void dataUsageCheckoutOptions(ArangoDatabase arangodb, String deliveryPreferences,
      String deliveryPlatform, String dataRefreshFrequency, String priority) {

    ArangoCursor<Object> cursor = null;
    String q = "for doc in NodeTypes\r\n"
        + "filter doc._key == \"433940\"\r\n"
        + "update doc With {deliveryPreferences:push(doc.deliveryPreferences,'"
        + deliveryPreferences + "',true),deliveryPlatforms:push(doc.deliveryPlatforms,'"
        + deliveryPlatform + "',true),dataRefreshFrequency:push(doc.dataRefreshFrequency,'"
        + dataRefreshFrequency + "',true),priority:push(doc.priority,'" + priority
        + "',true)} in NodeTypes";

    logger.info("queryToBeExecuted----->" + q);

    try {
      cursor = arangodb.query(q, Object.class);
    } catch (Exception e) {
      log.error("Exception while dataUsageCheckoutOptions: " + e.getMessage().toString());
    }


  }


  private List<String> getDeliveryPlatform(String id) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    List<String> colors = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<HashMap> cursor = null;

    String query = "for doc in Nodes\r\n"
        + "filter doc._key== '" + id + "'\r\n"
        + "RETURN {attributes:doc.attributes}";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getDeliveryPlatform : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    for (int j = 0; j < response1.size(); j++) {
      JSONObject attributesObject = new JSONObject(response1.get(j));
      logger.info(String.valueOf(attributesObject));
      JSONArray attributes = attributesObject.getJSONArray("attributes");
      String PII = "null";
      String SE = "null";
      String piiName = "null";
      String seName = "null";
      JSONObject attrsObj = new JSONObject();
      for (int k = 0; k < attributes.length(); k++) {
        attrsObj = new JSONObject(attributes.get(k).toString());
        //String idAttribute=attrsObj.getString("ID");

        if (attrsObj.get("name").toString().equals("Personally Identifiable Information")) {
          PII = attrsObj.getString("value");
          piiName = attrsObj.getString("name");
        }

        if (attrsObj.get("name").toString().equals("Security Classification")) {
          SE = attrsObj.getString("value");
          seName = attrsObj.getString("name");
        }

      }
      if (PII.contains("true") && seName.equals("Security Classification")) {
        colors.add("Red");
      } else if (PII.contains("false") && seName.equals("Security Classification")) {
        colors.add("Green");
      } else if (piiName.equals("Personally Identifiable Information") || seName.equals(
          "Security Classification")) {
        colors.add("Orange");
      } else if (PII.contains("null") && SE.contains("null")) {
        logger.info("not available");
      } else if (piiName.contains("null") && seName.contains("null")) {
        logger.info("not available");
      } else {
        colors.add("White");
      }

    }

    return colors;


  }


  private List<Integer> getAlationIds(String id) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    List<Integer> columns2 = new ArrayList<Integer>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<HashMap> cursor = null;

    String query = "for doc in Nodes\r\n"
        + "filter doc._key== '" + id + "'\r\n"
        + "RETURN {attributes:doc.attributes}";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getAlationIds : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    for (int j = 0; j < response1.size(); j++) {
      JSONObject attributesObject = new JSONObject(response1.get(j));
      logger.info(String.valueOf(attributesObject));
      JSONArray attributes = attributesObject.getJSONArray("attributes");
      for (int k = 0; k < attributes.length(); k++) {
        JSONObject attrsObj = new JSONObject(attributes.get(k).toString());
        //String idAttribute=attrsObj.getString("ID");
        if (attrsObj.get("name").toString().equals("alation_id")) {
          int value = attrsObj.getInt("value");
          //nodesinfo1.put("Description", value);
          columns2.add(value);
          logger.info("columns2" + columns2);
        }
      }
    }

    return columns2;

  }


  private void updatePolicyInRanger(JSONObject reqBody) {
    JSONObject rangerPolicyUpdateObj = reqBody.getJSONObject("rangerPolicyUpdate");
    int pId = rangerPolicyUpdateObj.getInt("policyId");
    String userName = rangerPolicyUpdateObj.getString("accessRequestPrincipal");
    String policyResponse = getPolicyInfo(pId);
    JSONObject responseObject = new JSONObject(policyResponse);
    JSONObject addingObject = new JSONObject();
    JSONArray accesses = new JSONArray();
    JSONObject accessesObject1 = new JSONObject();
    accessesObject1.put("type", "select");
    accessesObject1.put("isAllowed", true);
    JSONObject accessesObject2 = new JSONObject();
    accessesObject2.put("type", "read");
    accessesObject2.put("isAllowed", true);
    accesses.put(accessesObject1);
    accesses.put(accessesObject2);
    addingObject.put("accesses", accesses);
    JSONArray users = new JSONArray();
    users.put(userName);
    addingObject.put("users", users);
    JSONArray groups = new JSONArray();
    JSONArray roles = new JSONArray();
    JSONArray conditions = new JSONArray();
    addingObject.put("groups", groups);
    addingObject.put("roles", roles);
    addingObject.put("conditions", conditions);
    addingObject.put("delegateAdmin", false);
    JSONArray policyItems = responseObject.getJSONArray("policyItems");
    policyItems.put(addingObject);
    responseObject.put("policyItems", policyItems);
    updatePolicy(responseObject.toString(), pId);

  }


  public String getPolicyInfo(int pId) {

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    String auth = "Admin" + ":" + "admin";
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
    String authHeader = "Basic " + new String(encodedAuth);
    headers.set("Authorization", authHeader);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    String url = "http://162.241.222.29:6080/service/public/v2/api/policy/" + pId;
    HttpEntity<String> entity = new HttpEntity<String>(headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,
        String.class);

    String stringData = response.getBody();
    int statusCode = response.getStatusCodeValue();

    if (statusCode == 200) {
      log.info("Policy json uploaded to ranger succesfully");
      return stringData;
    } else {
      JSONObject jsonObject = new JSONObject(stringData);
      log.warn("Ranger Failure Message :{}", jsonObject.getString("msgDesc"));
      return jsonObject.getString("msgDesc");
    }

  }

  public void updatePolicy(String reqbody, int pId) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    String auth = "Admin" + ":" + "admin";
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
    String authHeader = "Basic " + new String(encodedAuth);
    headers.set("Authorization", authHeader);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    String url = "http://162.241.222.29:6080/service/public/v2/api/policy/" + pId;
    HttpEntity<String> entity = new HttpEntity<String>(reqbody, headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity,
        String.class);

    String stringData = response.getBody();
    int statusCode = response.getStatusCodeValue();

    if (statusCode == 200) {
      log.info("Policy json Updated to ranger succesfully");
    } else {
      JSONObject jsonObject = new JSONObject(stringData);
      log.warn("Ranger Failure Message :{}", jsonObject.getString("msgDesc"));
    }
  }

  private List<String> getalationUrl(String id) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    List<String> columns2 = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<HashMap> cursor = null;

    String query = "for doc in Nodes\r\n"
        + "filter doc._key== '" + id + "'\r\n"
        + "RETURN {attributes:doc.attributes}";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getalationUrl : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    for (int j = 0; j < response1.size(); j++) {
      JSONObject attributesObject = new JSONObject(response1.get(j));
      logger.info(String.valueOf(attributesObject));
      JSONArray attributes = attributesObject.getJSONArray("attributes");
      for (int k = 0; k < attributes.length(); k++) {
        JSONObject attrsObj = new JSONObject(attributes.get(k).toString());
        //String idAttribute=attrsObj.getString("ID");
        if (attrsObj.get("name").toString().equals("url")) {
          String value = attrsObj.getString("value");
          //nodesinfo1.put("Description", value);
          columns2.add(value);
          logger.info("columns2" + columns2);
        }
      }
    }
    return columns2;
  }

  private List<String> storeDataUsages(Object object) {

    List<String> response1 = new ArrayList<>();
    JSONObject res = new JSONObject();
    List<String> response = new ArrayList<>();
    List<JSONObject> nodesInfo = new ArrayList<JSONObject>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in " + DataUsage + "\r\n"
        + "filter a._key == '" + object + "'\r\n"
        + "return a";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));
    } catch (Exception e) {
      log.error("Exception while storeDataUsages : " + e.getMessage().toString());
    }

    response.forEach(b -> {
      JSONObject s = new JSONObject(b);
      System.out.println(s);
      String deliveryPreferences = s.getString("deliveryPreferences");
      res.put("deliveryPreferences", deliveryPreferences);
      String priority = s.getString("priority").toString();
      res.put("priority", priority);
      String deliveryPlatform = s.getString("deliveryPlatform").toString();
      res.put("deliveryPlatform", deliveryPlatform);
      String dataRefreshFrequency = s.getString("dataRefreshFrequency").toString();
      res.put("dataRefreshFrequency", dataRefreshFrequency);
      String requesterMailID = s.getString("requesterMailID");
      res.put("requesterMailID", requesterMailID);
      String StartDate = s.getString("StartDate");
      res.put("StartDate", StartDate);
      String EndDate = s.getString("EndDate");
      res.put("EndDate", EndDate);
      String createdOn = s.getString("createdOn");
      res.put("createdOn", createdOn);
      String createdBy = s.getString("createdBy");
      res.put("createdBy", createdBy);
      if (s.has("IncidentState") && s.has("IncidentNumber")) {
        String IncidentState = s.getString("IncidentState");
        res.put("IncidentState", IncidentState);
        String IncidentNumber = s.getString("IncidentNumber");
        res.put("IncidentNumber", IncidentNumber);
      }
      String callerName = s.getString("callerName");
      res.put("callerName", callerName);
      JSONArray requesterNames = s.getJSONArray("requesterNames");
      JSONArray orderIds = s.getJSONArray("itemIds");
      //.getString("requesterNames");
      res.put("requesterNames", requesterNames);
      JSONArray orderUrl = s.getJSONArray("itemUrls");
      res.put("orderUrls", orderUrl);
      res.put("orderIds", orderIds);
      String dataUsageId = s.getString("_key");
      //DatausageID=s.getString("_id");
      res.put("_key", dataUsageId);
      String dataSharingId = s.getString("DSA");
      res.put("DSA", dataSharingId);
      logger.info(String.valueOf(res));
      logger.info(res.toString());
      nodesInfo.add(s);
    });
   // nodesInfo.add(res);
    connectArango.importDocuments2Arango(nodesInfo.toString(), DataUsageArchiveCollection);

    return response1;

  }

  public ArrayList<Object> nodeFilterSearchOneWord(String name) {

    List<HashMap> response = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "FOR doc IN nodesView\n"
        + "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],2,false),\"text_en_no_stem\") OR NGRAM_MATCH(doc.name,'"
        + name + "',0.4,'fuzzy_search_bigram')\n"
        + "SORT doc.type.name == \"Data Product\" ? 1 : doc.type.name == \"Data Set\" ? 2 : doc.type.name == \"Reports\" ? 3 : doc.type.name == \"Schema\" ? 4 : doc.type.name == \"File\" ? 5 : 6\n"
        + "RETURN doc";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor1.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchOneWord : " + e.getMessage().toString());
    }

    return connectArango.tailView(response);
  }

  public ArrayList<Object> nodeFilterSearchOneWordList(String name) {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "FOR doc IN " + viewName + "\r\n"
        + "SEARCH NGRAM_MATCH(\r\n"
        + "doc.name,'" + name + "',0.3,'fuzzy_search_bigram')\r\n"
        + "filter doc.name != '" + name + "'\r\n"
        + "sort BM25(doc) DESC\r\n"
        + "LIMIT 10 return doc.name \r\n";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchOneWordList : " + e.getMessage().toString());
    }

    for (int i = 0; i < response.size(); i++) {
      columns.add("'" + response.get(i).toString() + "'");
      //columns1.add("node.name == '"+ response.get(i).toString() +"'");
    }

    String queryToBeExecuted1 = "FOR doc IN " + viewName + "\r\n"
        + "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],2,false),\"text_en_no_stem\")\r\n"
        + "filter doc.name != '" + name + "'\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 10return doc.name \r\n";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    try {

      cursor = arangoDB.query(queryToBeExecuted1, Object.class);
      response1 = cursor.asListRemaining();
      logger.info("response" + response1);
    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchOneWordList_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      columns.add("'" + response.get(i).toString() + "'");
      //columns1.add("node.name == '"+ response1.get(i).toString() +"'");
    }

    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //logger.info("columns1"+columns1);
    }
    logger.info(String.valueOf(columns1));

    for (int i = 0; i < columns1.size(); i++) {
      //columns2.add("'"+ response.get(i).toString() +"'");
      columns2.add("node.name == " + columns1.get(i).toString() + "");
    }

    String columnIds1 = String.join(" OR ", columns2);
    logger.info("columns2" + columns2);

    String queryToBeExecuted3 = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor1 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response2 = cursor1.asListRemaining();
      logger.info("response" + response1);
    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchOneWordList_3 : " + e.getMessage().toString());
    }

    return connectArango.listViewresponse(response2);
  }

  public ArrayList<Object> nodeFilterSearchAllWord(String name) {

    List<HashMap> response = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "LET tokens = TOKENS(['" + name + "'], \"text_en\") \r\n"
        + "LET tokens_flat = FLATTEN(tokens, 2)\r\n"
        + "FOR doc IN " + viewName + " SEARCH ANALYZER(tokens_flat ALL IN doc.name, \"text_en\") "
        + "SORT doc.type.name == \"Data Product\" ? 1 : doc.type.name == \"Data Set\" ? 2 : doc.type.name == \"Reports\" ? 3 : doc.type.name == \"Schema\" ? 4 : doc.type.name == \"File\" ? 5 : 6"
        + "RETURN doc";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchAllWord : " + e.getMessage().toString());
    }

    return connectArango.tailView(response);

  }


  public ArrayList<Object> nodeFilterSearchAllWordList(String name) {

    List<HashMap> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "LET tokens = TOKENS(['" + name + "'], \"text_en\") \r\n"
        + "LET tokens_flat = FLATTEN(tokens, 2)\r\n"
        + "FOR doc IN " + viewName
        + " SEARCH ANALYZER(tokens_flat ALL IN doc.name, \"text_en\") RETURN doc";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchAllWordList : " + e.getMessage().toString());
    }

    return connectArango.listViewresponse(response);

  }

  public ArrayList<Object> nodeFilterSearchExactMatch(String name) {

    List<HashMap> response = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "FOR doc in " + viewName + "\r\n"
        + "SEARCH ANALYZER (PHRASE(doc.name,'" + name + "'),\"text_en\")\r\n"
        + "SORT doc.type.name == \"Data Product\" ? 1 : doc.type.name == \"Data Set\" ? 2 : doc.type.name == \"Reports\" ? 3 : doc.type.name == \"Schema\" ? 4 : doc.type.name == \"File\" ? 5 : 6 "
        + " RETURN doc";

    //			String queryToBeExecuted="For a in Nodes\r\n"
    //					+"filter a.name == '"+ name +"'\r\n"
    //					+"return a";
    logger.info("nodeFilterSearchExactMatch queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchExactMatch : " + e.getMessage().toString());
    }
    return connectArango.tailView(response);

  }

  public ArrayList<Object> nodeFilterSearchExactMatchList(String name) {

    List<HashMap> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "FOR a in " + viewName + "\r\n"
        + "SEARCH ANALYZER (PHRASE(a.name,'" + name + "'),\"text_en\")\r\n"
        + "SORT BM25(a) DESC\r\n"
        + "RETURN a";

    //			String queryToBeExecuted="For a in Nodes"
    //					+"filter a.name == '"+name+"'"
    //					+"return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeFilterSearchExactMatchList: " + e.getMessage().toString());
    }

    return connectArango.listViewresponse(response);


  }

  public List<HashMap> getsearchOrders(String name) {

    List<HashMap> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "FOR a IN " + ordersView + "\r\n"
        + "SEARCH ANALYZER(a.nodeInfo.DisplayName IN TOKENS('" + name
        + "','text_en'),'text_en')\r\n"
        + "SORT BM25(a) DESC \r\n"
        + "RETURN a";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsearchOrders: " + e.getMessage().toString());
    }
    return response;
  }

  public String getDate() {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    String sdate = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "return DATE_ISO8601(DATE_NOW())";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getDate : " + e.getMessage().toString());
    }
    String date = response.get(0).toString();
    logger.info("date" + date);
    String[] datesplit = date.split("T");
    sdate = datesplit[0];
    return sdate;

  }


  public List<Object> getOrdersDropDownValues() {

    List<Object> response = new ArrayList<>();
    List<String> column = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    final String query = "return [\"Past 1 month\",\"Past 3 months\",\"Past 6 months\",\"Past 1 year\",\"All\"]";
    logger.info("query------>" + query);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getOrdersDropDownValues : " + e.getMessage().toString());
    }
    return response;

  }

  public List<Object> getDeliverypreferences() {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + arangoNodeTypesCollection + "\r\n"
        + "filter doc.deliveryPreferences\r\n"
        + "return doc.deliveryPreferences";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getDeliverypreferences : " + e.getMessage().toString());
    }

    return response;
  }

  public List<Object> getDataRefreshFrequency() {
    List<Object> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + arangoNodeTypesCollection + "\r\n"
        + "filter doc.dataRefreshFrequency\r\n"
        + "return doc.dataRefreshFrequency";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getDataRefreshFrequency : " + e.getMessage().toString());
    }
    return response;

  }

  public List<Object> getPriorityList() {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + arangoNodeTypesCollection + "\r\n"
        + "filter doc.priority\r\n"
        + "return doc.priority";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPriorityList : " + e.getMessage().toString());
    }
    return response;
  }

  public List<Object> getDeliveryPlatformList() {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for doc in " + arangoNodeTypesCollection + "\r\n"
        + "filter doc.deliveryPlatforms\r\n"
        + "return doc.deliveryPlatforms";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getDeliveryPlatformList : " + e.getMessage().toString());
    }
    return response;
  }

  public List<Object> getclearDataUsages() {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for doc in " + DataUsage + "\r\n"
        + "remove doc._key in  " + DataUsage + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getclearDataUsages : " + e.getMessage().toString());
    }
    return response;
  }

  public List<HashMap> getclearSingleDataUsages(String key) {

    List<String> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for doc in " + ordersSearchCollection + "\r\n"
        + "filter doc._id == 'ordersSearchCollection/" + key + "'\r\n"
        + "return doc";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getclearSingleDataUsages : " + e.getMessage().toString());
    }
    //logger.info("response"+response);

    String queryToBeExecuted1 = "for doc in " + ordersSearchCollection + "\r\n"
        + "filter doc._id == 'ordersSearchCollection/" + key + "'\r\n"
        + "remove  doc in " + ordersSearchCollection + "";

    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getclearSingleDataUsages_2 : " + e.getMessage().toString());
    }
    response.forEach(a -> {
      List<Object> response3 = new ArrayList<>();
      JSONObject res = new JSONObject(a);
      String requestNumber = res.getString("RequesterNumber");
      JSONObject nodeInfo = res.getJSONObject("nodeInfo");
      String id = nodeInfo.getString("Id");

      String queryToBeExecuted3 = "for doc in " + DataUsage + "\r\n"
          + "filter doc._key == '" + requestNumber + "'\r\n"
          + "UPDATE doc WITH {orderIds:REMOVE_VALUE(doc.orderIds,'" + id + "')} IN " + DataUsage
          + "";
      //				String q="for doc in dataUsageCollection\r\n"
      //						+ "filter doc._key == '26709198' \r\n"
      //						+ "UPDATE doc WITH {orderIds:REMOVE_VALUE(doc.orderIds,\"fa90beb6-8565-4ed7-ba49-26f19bea9bf4\")} IN dataUsageCollection";
      //
      logger.info("queryToBeExecuted3----->" + queryToBeExecuted3);

      ArangoCursor<Object> cursor3 = null;
      try {

        cursor3 = arangoDB.query(queryToBeExecuted3, Object.class);
        response3 = cursor3.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getclearSingleDataUsages_3 : " + e.getMessage().toString());
      }

    });

    return response1;
  }

  public List<HashMap> getexpiredOrdersDropDown(String input, String userId) {

    List<String> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    ArrayList<JSONObject> nodesInfo = new ArrayList<JSONObject>();
    JSONObject yourOrders = new JSONObject();

    Format f = new SimpleDateFormat("yyyy-MM-dd");
    String datestr = f.format(new Date());
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection3 = arangorestclient.getArangoCollection(arangoDB,
        expireOrders);
    ArangoCursor<HashMap> cursor = null;

    String queryToBeExecuted = "for doc in " + expireOrders + "\r\n"
        + "remove doc._key in " + expireOrders + "";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response3 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getexpiredOrdersDropDown : " + e.getMessage().toString());
    }

    ArangoCursor<String> userCursor = null;

    String queryToExecute = "for doc in " + userRegistration + "\r\n"
        + "filter doc._key == '" + userId + "'\r\n"
        + "return doc.Email";

    logger.info("queryToBeExecuted----->" + queryToExecute);

    try {

      userCursor = arangoDB.query(queryToExecute, String.class);
      response = userCursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getexpiredOrdersDropDown_2 : " + e.getMessage().toString());
    }

    String usermailId = response.get(0);

    if (input.equals("Past 1 month")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P1M\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_3 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsageArchiveCollection + "\r\n"
          + "filter node.requesterMailID== '" + usermailId + "' AND node.EndDate >= '" + sdate
          + "' AND node.EndDate < '" + todayDate + "'\r\n"
          + "return node";
      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_4 : " + e.getMessage().toString());
      }

    } else if (input.equals("Past 3 months")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P3M\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_5 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsageArchiveCollection + "\r\n"
          + "filter node.requesterMailID== '" + usermailId + "' AND node.EndDate >= '" + sdate
          + "' AND node.EndDate < '" + todayDate + "'\r\n"
          + "return node";
      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_6 : " + e.getMessage().toString());
      }

    } else if (input.equals("Past 6 months")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P6M\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_7 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsageArchiveCollection + "\r\n"
          + "filter node.requesterMailID== '" + usermailId + "' AND node.EndDate >= '" + sdate
          + "' AND node.EndDate < '" + todayDate + "'\r\n"
          + "return node";
      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_8 : " + e.getMessage().toString());
      }

    } else if (input.equals("Past 1 year")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P1Y\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_9 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsageArchiveCollection + "\r\n"
          + "filter node.requesterMailID == '" + usermailId + "' AND node.EndDate >= '" + sdate
          + "' AND node.EndDate < '" + todayDate + "'\r\n"
          + "return node";
      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_10 : " + e.getMessage().toString());
      }

    } else {

      String queryToBeExecuted3 = "for a in " + DataUsageArchiveCollection + "\r\n"
          + "filter a.requesterMailID == '" + usermailId + "' AND a.EndDate < '" + datestr + "'\r\n"
          + "return a";

      logger.info("queryToBeExecuted3----->" + queryToBeExecuted3);

      ArangoCursor<String> cursor2 = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted3, String.class);
        response = cursor2.asListRemaining();
        logger.info(String.valueOf(response));
      } catch (Exception e) {
        log.error("Exception while getexpiredOrdersDropDown_11 : " + e.getMessage().toString());
      }
    }
    response.forEach(a -> {
      JSONObject ords = new JSONObject(a);
      List<String> columns = new ArrayList<String>();
      if (ords.has("orderIds") && !ords.isEmpty()) {
        JSONArray orderIds = ords.getJSONArray("orderIds");
        logger.info("orderIds" + orderIds);
        List<String> response2 = new ArrayList<>();
        for (int i = 0; i < orderIds.length(); i++) {
          String queryToBeExecuted1 =
              "For node in " + nodesCollection + "\r\n" + "filter node._key == '" + orderIds.get(
                  i)
                  + "'\r\n" + "return node";

          logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

          ArangoCursor<String> cursor1 = null;
          try {

            cursor1 = arangoDB.query(queryToBeExecuted1, String.class);
            response2 = cursor1.asListRemaining();
            logger.info(String.valueOf(response2));
          } catch (Exception e) {
            log.error("Exception while getexpiredOrdersDropDown_12 : " + e.getMessage().toString());
          }

          JSONObject nodesinfo1 = new JSONObject();
          List<Object> nodesList = new ArrayList<>();
          response2.forEach(nodesinfo -> {
            JSONObject nodes = new JSONObject(nodesinfo);
            String str = nodes.getString("displayName");
            //String str1="<b>"+str+"</b>";
            nodesinfo1.put("displayName", str);
            String label = nodes.getString("identifier");
            String[] label1 = label.split(">");
            logger.info(String.valueOf(label1));
            JSONObject nodetype = nodes.getJSONObject("type");
            String nameType = nodetype.getString("name");
            String Id = nodes.getString("id");
            nodesinfo1.put("id", Id);
            nodesinfo1.put("type", nameType);

            JSONArray attributeInfo = nodes.getJSONArray("attributes");
            if (!attributeInfo.isEmpty()) {
              attributeInfo.forEach(eachAttribute -> {
                JSONObject attributes = new JSONObject(eachAttribute.toString());
                logger.info(String.valueOf(attributes));
                if (!attributes.isEmpty()) {
                  if (attributes.get("name").toString().equals("Description")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("description", "<b>" + value + "</b>");
                  } else if (attributes.get("name").toString().equals("Definition")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("description", "<b>" + value + "</b>");
                  }
                  if (attributes.get("name").toString().equals("Certified")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("certified", value);
                  }
                  if (attributes.get("name").toString()
                      .equals("Personally Identifiable Information")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("personallyIdentifiableInformation", value);
                  }
                  if (attributes.get("name").toString().equals("Security Classification")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("securityClassification", value);
                  }
                }
              });
            }

            String RequesterNumber = ords.getString("_key");
            yourOrders.put("requesterNumber", RequesterNumber);
            String StartDate = ords.getString("StartDate");
            yourOrders.put("startDate", StartDate);
            String EndDate = ords.getString("EndDate");
            yourOrders.put("endDate", EndDate);
            if (ords.has("IncidentState")) {
              String Status = ords.getString("IncidentState");
              yourOrders.put("status", Status);
            }
            if (ords.has("IncidentNumber")) {
              String TicketNumber = ords.getString("IncidentNumber");
              yourOrders.put("ticketNumber", TicketNumber);
            }
            //						String Role=ords.getString("requesterMailID");
            //						yourOrders.put("Role", Role);
            yourOrders.put("nodeInfo", nodesinfo1);
          });
          //response1.add(yourOrders.toMap());
          nodesInfo.add(yourOrders);
          connectArango.importDocuments2Arango(nodesInfo.toString(), expireOrders);
          nodesInfo.clear();
        }
      }
    });

    ArangoCursor<HashMap> cursor4 = null;

    String queryToBeExecuted3 = "for doc in " + expireOrders + "\r\n"
        + "return doc";

    logger.info("queryToBeExecuted3----->" + queryToBeExecuted3);

    try {

      cursor4 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response4 = cursor4.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getexpiredOrdersDropDown_13 : " + e.getMessage().toString());
    }

    return response4;

  }

  public List<HashMap> getyourOrders(String input, String userId) {

    List<String> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    ArrayList<JSONObject> nodesInfo = new ArrayList<JSONObject>();

    Format f = new SimpleDateFormat("yyyy-MM-dd");
    String datestr = f.format(new Date());
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection3 = arangorestclient.getArangoCollection(arangoDB,
        ordersSearchCollection);
    ArangoCursor<HashMap> cursor = null;

    String queryToBeExecuted = "for doc in " + ordersSearchCollection + "\r\n"
        + "remove doc._key in " + ordersSearchCollection + "";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response3 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getyourOrders : " + e.getMessage().toString());
    }

    ArangoCursor<String> userCursor = null;

    String queryToExecute = "for doc in " + userRegistration + "\r\n"
        + "filter doc._key == '" + userId + "'\r\n"
        + "return doc.Email";

    logger.info("queryToBeExecuted----->" + queryToExecute);

    try {

      userCursor = arangoDB.query(queryToExecute, String.class);
      response = userCursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getyourOrders_2 : " + e.getMessage().toString());
    }

    String usermailId = response.get(0);
    if (input.equals("Past 1 month")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P1M\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getyourOrders_3 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsage + "\r\n"
          + "filter node.requesterMailID== '" + usermailId + "' AND node.StartDate >= '" + sdate
          + "' AND node.StartDate <= '" + todayDate + "' AND  node.EndDate <= '" + todayDate
          + "'\r\n"
          + "return node";

      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getyourOrders_4 : " + e.getMessage().toString());
      }

    } else if (input.equals("Past 3 months")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P3M\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getyourOrders_5 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsage + "\r\n"
          + "filter node.requesterMailID== '" + usermailId + "' AND node.StartDate >= '" + sdate
          + "' AND node.StartDate <= '" + todayDate + "' AND  node.EndDate <= '" + todayDate
          + "'\r\n"
          + "return node";
      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getyourOrders_6: " + e.getMessage().toString());
      }

    } else if (input.equals("Past 6 months")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P6M\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getyourOrders_7 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsage + "\r\n"
          + "filter node.requesterMailID== '" + usermailId + "' AND node.StartDate >= '" + sdate
          + "' AND node.StartDate <= '" + todayDate + "' AND  node.EndDate <= '" + todayDate
          + "'\r\n"
          + "return node";
      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getyourOrders_8 : " + e.getMessage().toString());
      }

    } else if (input.equals("Past 1 year")) {

      String queryToBeExecuted1 = "return DATE_SUBTRACT(DATE_NOW(), \"P1Y\")";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
      ArangoCursor<String> cursor5 = null;
      try {

        cursor5 = arangoDB.query(queryToBeExecuted1, String.class);
        response = cursor5.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getyourOrders_9 : " + e.getMessage().toString());
      }
      String date = response.get(0).toString();
      logger.info("date" + date);
      String[] datesplit = date.split("T");
      String sdate = datesplit[0];
      String todayDate = getDate();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted2 = "for node in " + DataUsage + "\r\n"
          + "filter node.requesterMailID =='" + usermailId + "' AND node.StartDate >= '" + sdate
          + "' AND node.StartDate <= '" + todayDate + "' AND  node.EndDate <= '" + todayDate
          + "'\r\n"
          + "return node";
      logger.info("queryToBeExecuted2----->" + queryToBeExecuted2);
      try {
        cursor1 = arangoDB.query(queryToBeExecuted2, String.class);
        response = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getyourOrders_10: " + e.getMessage().toString());
      }

    } else {

      String queryToBeExecuted3 = "for a in " + DataUsage + "\r\n"
          + "filter a.EndDate <= '" + datestr + "' AND a.requesterMailID == '" + usermailId
          + "'\r\n"
          + "return a";

      logger.info("queryToBeExecuted3----->" + queryToBeExecuted3);

      ArangoCursor<String> cursor2 = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted3, String.class);
        response = cursor2.asListRemaining();
        logger.info(String.valueOf(response));
      } catch (Exception e) {
        log.error("Exception while getyourOrders_11 : " + e.getMessage().toString());
      }
    }
    logger.info("response" + response);
    response.forEach(a -> {
      JSONObject yourOrders = new JSONObject();
      JSONObject ords = new JSONObject(a);
      List<String> columns = new ArrayList<String>();
      if (ords.has("itemIds") && !ords.isEmpty()) {
        JSONArray orderIds = ords.getJSONArray("itemIds");
        logger.info("itemIds" + orderIds);
        List<String> response2 = new ArrayList<>();
        for (int i = 0; i < orderIds.length(); i++) {
          String queryToBeExecuted1 =
              "For node in " + nodesCollection + "\r\n" + "filter node._key == '" + orderIds.get(
                  i)
                  + "'\r\n" + "return node";

          logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

          ArangoCursor<String> cursor1 = null;
          try {

            cursor1 = arangoDB.query(queryToBeExecuted1, String.class);
            response2 = cursor1.asListRemaining();
            logger.info(String.valueOf(response2));
          } catch (Exception e) {
            log.error("Exception while getyourOrders_12 : " + e.getMessage().toString());
          }

          JSONObject nodesinfo1 = new JSONObject();
          List<Object> nodesList = new ArrayList<>();
          response2.forEach(nodesinfo -> {
            JSONObject nodes = new JSONObject(nodesinfo);
            String str = nodes.getString("displayName");
            //String str1="<b>"+str+"</b>";
            nodesinfo1.put("displayName", str);

            String label = nodes.getString("identifier");
            String[] label1 = label.split(">");
            logger.info(String.valueOf(label1));
            JSONObject nodetype = nodes.getJSONObject("type");
            String nameType = nodetype.getString("name");
            String Id = nodes.getString("id");
            nodesinfo1.put("id", Id);
            nodesinfo1.put("type", nameType);

            JSONArray attributeInfo = nodes.getJSONArray("attributes");
            if (!attributeInfo.isEmpty()) {
              attributeInfo.forEach(eachAttribute -> {
                JSONObject attributes = new JSONObject(eachAttribute.toString());
                logger.info(String.valueOf(attributes));
                if (!attributes.isEmpty()) {
                  if (attributes.get("name").toString().equals("Description")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("description", "<b>" + value + "</b>");
                  } else if (attributes.get("name").toString().equals("Definition")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("description", "<b>" + value + "</b>");
                  }
                  if (attributes.get("name").toString().equals("Certified")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("certified", value);
                  }
                  if (attributes.get("name").toString()
                      .equals("Personally Identifiable Information")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("personallyIdentifiableInformation", value);
                  }
                  if (attributes.get("name").toString().equals("Security Classification")) {
                    String value = attributes.getString("value");
                    nodesinfo1.put("securityClassification", value);
                  }
                }
              });
            }

            String RequesterNumber = ords.getString("_key");
            yourOrders.put("requesterNumber", RequesterNumber);
            String StartDate = ords.getString("StartDate");
            yourOrders.put("startDate", StartDate);
            String EndDate = ords.getString("EndDate");
            yourOrders.put("endDate", EndDate);
            String createdOn = ords.getString("createdOn");
            yourOrders.put("createdOn", createdOn);
            if (ords.has("IncidentState")) {
              String Status = ords.getString("IncidentState");
              yourOrders.put("status", Status);
            }
            if (ords.has("IncidentNumber")) {
              String TicketNumber = ords.getString("IncidentNumber");
              yourOrders.put("ticketNumber", TicketNumber);
            }
            if (ords.has("deliveryPlatform")) {
              String dp = ords.getString("deliveryPlatform");
              if (dp.equals("ServiceNow")) {

              } else {
                if (ords.has("url")) {
                  String url = ords.getString("url");
                  yourOrders.put("url", url);
                }
              }
            }

            //						String Role=ords.getString("requesterMailID");
            //						yourOrders.put("Role", Role);
            yourOrders.put("nodeInfo", nodesinfo1);
          });

          //response1.add(yourOrders.toMap());
          if (yourOrders.has("nodeInfo")) {
            nodesInfo.add(yourOrders);
          }
          connectArango.importDocuments2Arango(nodesInfo.toString(), ordersSearchCollection);
          nodesInfo.clear();
        }
      }
    });

    ArangoCursor<HashMap> cursor4 = null;

    String queryToBeExecuted3 = "for doc in " + ordersSearchCollection + "\r\n"
        + "return doc";

    logger.info("queryToBeExecuted3----->" + queryToBeExecuted3);

    try {

      cursor4 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response4 = cursor4.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getyourOrders_13 : " + e.getMessage().toString());
    }
    return response4;
  }

  public List<Object> getplatformAvailabilitydropdown() {
    List<Object> response = new ArrayList<>();
    List<String> column = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    final String query = "return [\"Microsoft\",\"GCP\",\"AWS\",\"Snowflake\"]";
    logger.info("query------>" + query);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getplatformAvailabilitydropdown : " + e.getMessage().toString());
    }
    return response;


  }

  public List<String> getcollectionSearch(String nodeName) {

    List<String> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "FOR node IN collectionView\r\n"
        + "SEARCH ANALYZER(node.displayname IN TOKENS('" + nodeName
        + "','text_en'),'text_en')\r\n"
        + "RETURN node.displayname";

    ArangoCursor<String> cursor = null;

    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getcollectionSearch : " + e.getMessage().toString());
    }

    return response;

  }

  public ArrayList<Object> getmultilevelSearch(String name) {

    columNames1.clear();
    columNames2.clear();
    columNames3.clear();
    columNames4.clear();
    columNames5.clear();

    List<String> response = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    ArangoCursor<String> cursor = null;
    List<String> result = new ArrayList<String>();

    List<String> qresponse = new ArrayList<>();
    List<Object> qresponse2 = new ArrayList<>();
    List<HashMap> qresponse3 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "let AllWordsMatch = (FOR a in nodesView\r\n"
        + "SEARCH ANALYZER(a.name IN TOKENS('" + name + "','text_en'),'text_en')\r\n"
        + "SORT BM25(a) ASC\r\n"
        + "LIMIT 10"
        + "RETURN a.relations)\r\n"
        + "let AllWordsNgram=(FOR doc IN nodesView\r\n"
        + "SEARCH NGRAM_MATCH(doc.name,'" + name + "',0.3,'fuzzy_search_bigram')\r\n"
        + "sort BM25(doc) DESC\r\n"
        + "LIMIT 10"
        + "return doc.relations)\r\n"
        + "let AllWordsLevenshtein=(FOR doc IN nodesView\r\n"
        + "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],2,false),\"text_en_no_stem\")\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 10"
        + "return doc.relations)\r\n"
        + "return {resultMatches:UNION_DISTINCT(AllWordsMatch,AllWordsNgram,AllWordsLevenshtein)}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getmultilevelSearch : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    response.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      JSONArray resultMatches = resultRelation.getJSONArray("resultMatches");
      resultMatches.forEach(datasettargetsObject -> {
        JSONObject eachRelation = new JSONObject(datasettargetsObject.toString());
        logger.info(String.valueOf(eachRelation));
        JSONArray targetedges = eachRelation.getJSONArray("targets");
        if (!targetedges.isEmpty()) {
          targetedges.forEach(eachsource -> {
            JSONObject targets = new JSONObject(eachsource.toString());
            logger.info("targets" + targets);
            JSONObject target = targets.getJSONObject("target");
            logger.info("target" + target);
            String id = target.getString("id");
            columNames.add(id);
            logger.info("columNames" + columNames);
          });
        }
      });
    });
    ArrayList<Object> resultForPage = targetail1();
    columNames.clear();
    return resultForPage;

  }


  public String getTailPages(ArrayList<Object> c, Integer pageSize, Integer pageNo)
      throws IndexOutOfBoundsException, ProcessFailedException {
    JSONObject retunPageJson = new JSONObject();

    pages.clear();
    listpages.clear();
    List<Object> result = new ArrayList<Object>();
    //List<Object> list = new ArrayList<Object>(c);
    retunPageJson.put("totalNumberofNodes", c.size());
    retunPageJson.put("currentPageNumber", pageNo);

    int numPages = (int) Math.ceil((double) c.size() / (double) pageSize);

    int pageNum;
    for (pageNum = 0; pageNum < numPages; ) {
      pages.add(c.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, c.size())));

    }
    int count = 0;

    for (int i = 0; i < pages.size(); i++) {
      count++;
      if (pageNo == count) {
        listpages.add(pages.get(i));

      }
    }
    if (listpages.isEmpty()) {
      throw new ProcessFailedException("Page Not Found ..Please check the Page number..");

    } else {
      retunPageJson.put("nodeInfo", listpages);

      return retunPageJson.toString();
    }

  }

  public ArrayList<Object> targetail1() {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < columNames.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == '" + columNames.get(i) + "'");
      //logger.info("columns-->"+columns);
      //columNames.add(columNames.get(i));
    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while targetail1 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);

      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames1.add(id);
        });
      }
    });
    return uniquetailOfSources();

  }

  private ArrayList<Object> uniquetailOfSources() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames1.size(); i++) {
      columns.add("'" + columNames1.get(i) + "'");
      columNames.add(columNames1.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //logger.info("columns1"+columns1);

    }
    logger.info(String.valueOf(columns1));

    return sourceListTail1(columns1);
  }


  public ArrayList<Object> sourceListTail1(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceListTail1 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames2.add(id);


        });
      }


    });

    return uniqueListOfSourcesTail1();

  }

  private ArrayList<Object> uniqueListOfSourcesTail1() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames2.size(); i++) {
      columns.add("'" + columNames2.get(i) + "'");
      columNames.add(columNames2.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceListTail2(columns1);
  }


  public ArrayList<Object> sourceListTail2(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceListTail2 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames3.add(id);
          //sourceSearch(id);

        });
      }
    });

    //return connectArango.listView(columNames);
    return uniqueTailListOfSources2();

  }

  private ArrayList<Object> uniqueTailListOfSources2() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames3.size(); i++) {
      columns.add("'" + columNames3.get(i) + "'");
      columNames.add(columNames3.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceTailList3(columns1);
  }

  public ArrayList<Object> sourceTailList3(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceTailList3 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames4.add(id);
          //sourceSearch(id);

        });
      }
    });

    //return connectArango.listView(columNames);
    return uniqueTailListOfSources3();

  }

  private ArrayList<Object> uniqueTailListOfSources3() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames4.size(); i++) {
      columns.add("'" + columNames4.get(i) + "'");
      columNames.add(columNames4.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceTailList4(columns1);
  }

  public ArrayList<Object> sourceTailList4(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceTailList4 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames5.add(id);
          //sourceSearch(id);

        });
      }
    });
    //return connectArango.listView(columNames);
    return uniqueTailListOfSources5();

  }

  private ArrayList<Object> uniqueTailListOfSources5() {

    List<String> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < columNames5.size(); i++) {
      columns.add("'" + columNames5.get(i) + "'");
      columNames.add(columNames5.get(i));

    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
      //columNames.add(strNumber);
    }
    logger.info(String.valueOf(columns1));

    return sourceTailList5(columns1);
  }

  public ArrayList<Object> sourceTailList5(List<String> response3) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response3.size(); i++) {
      //columns.add("'" + columNames.get(i) + "'");
      columns1.add("node.id == " + response3.get(i) + "");
      logger.info("columns-->" + columns1);

    }
    String columnIds1 = String.join(" OR ", columns1);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1
        + "\r\n" + "return node.relations";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while sourceTailList5 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject resultRelation = new JSONObject(res1);
      //	String id = null;
      JSONArray sourceedges = resultRelation.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          logger.info("sources" + sources);
          JSONObject source = sources.getJSONObject("source");
          logger.info("source" + source);
          String id = source.getString("id");
          columNames.add(id);
          //sourceSearch(id);

        });
      }
    });

    return connectArango.graphtailView(columNames);
    //return uniqueListOfSources4();

  }

  public String uploadFileData(MultipartFile file) throws ParseException {

    String importresponse = null;
    String filelocation = storeFile(file);
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/downloadFile/")
        .path(fileName).toUriString();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        userRoles);

    try (FileReader reader = new FileReader(filelocation)) {
      JSONParser jsonParser = new JSONParser(reader);
      Object obj = jsonParser.parse();
      try {
        //arangoCollection.importDocuments((Collection<?>) obj);
        importresponse = connectArango.importDocuments2Arango(obj, userRoles);
        logger.info("Documents created");
      } catch (ArangoDBException e) {
        System.err.println("Failed to create document. " + e.getMessage());
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return importresponse;
    //new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());

  }


  public String storeFile(MultipartFile file) {
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      // Check if the file's name contains invalid characters
      if (fileName.contains("..")) {
        throw new FileStorageException(
            "Sorry! Filename contains invalid path sequence " + fileName);
      }
      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = getTempFileLocation(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      return targetLocation.toString();
    } catch (IOException ex) {
      throw new FileStorageException("Could not store file " + fileName + ". Please try again!",
          ex);
    }
  }

  public Path getTempFileLocation(String fileName) throws IOException {
    File directory = new File(fileStorageLocation.toString());
    File targetFile = File.createTempFile(fileName, "", directory);
    Path targetLocation = targetFile.toPath();
    return targetLocation;
  }


  public String uploadExcelFile(MultipartFile file) throws IOException {
    String resmsg = null;
    String filelocation = storeFile(file);
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/downloadFile/")
        .path(fileName).toUriString();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        userRoles);
    File fileReader = new File(filelocation);
    FileInputStream fis = new FileInputStream(fileReader);
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    XSSFSheet sheet = workbook.getSheetAt(0);
    java.util.Iterator<Row> row = sheet.iterator();
    XSSFRow headerRow = sheet.getRow(0);
    while (row.hasNext()) {
      Row currentRow = row.next();
      JSONObject objectData = new JSONObject();
      if (currentRow.getRowNum() != 0) {
        for (int column = 0; column < currentRow.getPhysicalNumberOfCells(); column++) {
          logger.info("CellType---->" + currentRow.getCell(column).getCellType());
          if (currentRow.getCell(column).getCellType() == CellType.STRING) {
            ArrayList usersList = new ArrayList();
            ArrayList<Object> usersList1 = new ArrayList();
            List<String> teamName = new ArrayList();
            String teamId = null;
            if (column == 0) {
              logger.info("column0" + column);

              String teamname = currentRow.getCell(column).getStringCellValue();
              String query = "for a in Teams\r\n"
                  + "filter a.displayName == '" + teamname + "'\r\n"
                  + "RETURN a._id";
              logger.info("queryToBeExecuted----->" + query);

              ArangoCursor<String> cursor = null;

              try {
                cursor = arangoDB.query(query, String.class);
                teamName = cursor.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while uploadExcelFile : " + e.getMessage().toString());
              }

              teamId = teamName.get(0);
              logger.info("teamId" + teamId);

              //objectData.put(headerRow.getCell(column).getStringCellValue(),teamId);
              objectData.put("_from", teamId);
              logger.info("teamId" + objectData);
            } else if (column == 1) {
              logger.info("column0" + column);

              String rolename = currentRow.getCell(column).getStringCellValue();

              String roleId = "NodeTypes/" + rolename;
              logger.info("teamId" + teamId);

              //objectData.put(headerRow.getCell(column).getStringCellValue(),roleId);
              objectData.put("_to", roleId);
              logger.info("roleId" + objectData);
            } else if (column == 6) {
              String[] userId = currentRow.getCell(column).getStringCellValue().split(",");
              String emailId = null;
              for (int i = 0; i < userId.length; i++) {
                JSONObject usersObje = new JSONObject();
                if (userId[i].startsWith("[")) {
                  String id = userId[i].toString();
                  String[] emailIds = id.split("\\[");
                  String mail = emailIds[0];
                  emailId = emailIds[1];
                  if (emailId.endsWith("]")) {
                    String[] emailend = emailId.split("\\]");
                    emailId = emailend[0];
                    usersList.add("a.Email=='" + emailId + "'");
                  }
                  logger.info("emailId" + emailId);
                  usersList.add("a.Email=='" + emailId + "'");

                } else if (userId[i].endsWith("]")) {
                  String id = userId[i].toString();
                  String[] emailIds = id.split("\\]");
                  String mail = emailIds[0];
                  usersList.add("a.Email=='" + mail + "'");
                } else {
                  usersObje.put("id", userId[i]);
                  usersList.add("a.Email=='" + userId[i] + "'");
                }


              }
              logger.info("usersList" + usersList);

              List<Object> response = new ArrayList();
              String columnIds = String.join(" OR ", usersList);
              String queryToBeExecuted = "for a in registerUsers\r\n"
                  + "filter " + columnIds + "\r\n"
                  + "RETURN {id:a._key}";
              logger.info("queryToBeExecuted----->" + queryToBeExecuted);

              ArangoCursor<Object> cursor1 = null;

              try {
                cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
                response = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while uploadExcelFile_2 : " + e.getMessage().toString());
              }
              logger.info("response" + response);
              //									response.forEach(a->{
              //										JSONObject b=new JSONObject(a);
              //
              //										String id=b.get("id").toString();
              //										JSONObject userIds=new JSONObject();
              //										userIds.put("id",id);
              //										usersList1.add(userIds);
              //									});
              //

              objectData.put(headerRow.getCell(column).getStringCellValue(), response);
            } else {
              objectData.put(headerRow.getCell(column).getStringCellValue(),
                  currentRow.getCell(column).getStringCellValue());
              logger.info("objectData" + objectData);
            }

          } else if (currentRow.getCell(column).getCellType() == CellType.BOOLEAN) {
            objectData.put(headerRow.getCell(column).getStringCellValue(),
                currentRow.getCell(column).getBooleanCellValue());
            logger.info("objectData" + objectData);
          } else if (currentRow.getCell(column).getCellType() == CellType.NUMERIC) {

            List<String> teamName = new ArrayList();
            String teamId = null;
            if (column == 0) {
              logger.info("column0" + column);

              String teamname = currentRow.getCell(column).getStringCellValue();
              String query = "for a in Teams\r\n"
                  + "filter a.displayName == '" + teamname + "'\r\n"
                  + "RETURN a._id";
              logger.info("queryToBeExecuted----->" + query);

              ArangoCursor<String> cursor = null;

              try {
                cursor = arangoDB.query(query, String.class);
                teamName = cursor.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while uploadExcelFile_3: " + e.getMessage().toString());
              }

              teamId = teamName.get(0);

              objectData.put(headerRow.getCell(column).getStringCellValue(), teamId);
            } else {
              objectData.put(headerRow.getCell(column).getStringCellValue(), teamId);
              logger.info("objectData" + objectData);
            }

            if (column == 6) {
              logger.info("column7" + column);
              JSONObject usersObje = new JSONObject();
              ArrayList<Object> usersList = new ArrayList();
              //String[] userids=currentRow.getCell(column).getNumericCellValue().split(",");
              double users = currentRow.getCell(column).getNumericCellValue();
              String[] userId = String.valueOf(users).split(",");

              for (int i = 0; i < userId.length; i++) {
                usersObje.put("id", userId[i]);
                usersList.add(usersObje);
              }
              objectData.put(headerRow.getCell(column).getStringCellValue(), usersList);
            } else {
              objectData.put(headerRow.getCell(column).getStringCellValue(),
                  currentRow.getCell(column).getNumericCellValue());
              logger.info("Numeric type---->" + currentRow.getCell(column).getCellType());
              logger.info("objectData" + objectData);
            }
          } else if (currentRow.getCell(column).getCellType() == CellType.BLANK) {
            objectData.put(headerRow.getCell(column).getStringCellValue(), "");
            logger.info("objectData" + objectData);
          } else if (currentRow.getCell(column).getCellType() == CellType.FORMULA) {
            objectData.put(headerRow.getCell(column).getStringCellValue(),
                currentRow.getCell(column).getArrayFormulaRange());
            logger.info("objectData" + objectData);
          }
        }
        try {
          //arangoDB.insertDocument(objectData.toMap());
          resmsg = connectArango.importDocuments2Arango(objectData.toMap(), userRoles);
          logger.info("Documents created");
        } catch (ArangoDBException e) {
          System.err.println("Failed to create  document. " + e.getMessage());
        }
      }
    }
    return resmsg;
    //new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }


  public List<Object> nodesmlSearchList(String name) {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "FOR doc IN mlView \r\n"
        + "SEARCH ANALYZER(LEVENSHTEIN_MATCH(doc.name,TOKENS('" + name
        + "', \"text_en_no_stem\")[0],2,false),\"text_en_no_stem\")\r\n"
        + "SORT BM25(doc) DESC\r\n"
        + "LIMIT 10"
        + "return doc._id \r\n";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<Object> cursor2 = null;

    try {
      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while nodesmlSearchList : " + e.getMessage().toString());
    }
    if (response.isEmpty()) {

      String query = "FOR doc IN mlView\r\n"
          + "SEARCH NGRAM_MATCH(\r\n"
          + "doc.name,'" + name + "',0.3,'fuzzy_search_bigram')\r\n"
          + "sort BM25(doc) DESC\r\n"
          + "LIMIT 10 \r\n"
          + "return doc._id \r\n";

      logger.info("query----->" + query);

      try {
        cursor = arangoDB.query(query, String.class);
        response = cursor.asListRemaining();
        logger.info(String.valueOf(response));

      } catch (Exception e) {
        log.error("Exception while nodesmlSearchList_2 : " + e.getMessage().toString());
      }

      List<String> columns = new ArrayList<String>();
      List<String> columns1 = new ArrayList<String>();
      List<String> columns2 = new ArrayList<String>();

      for (int i = 0; i < response.size(); i++) {
        columns.add("'" + response.get(i).toString() + "'");
        //columns1.add("node.name == '"+ response.get(i).toString() +"'");
      }

      //String columnIds = String.join(" OR ", columns);

      String queryToBeExecuted1 = "for node in mllinkphy\r\n"
          + "filter node._from IN " + columns + "\r\n"
          + "return node._to";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      ArangoCursor<Object> cursor3 = null;
      try {

        cursor3 = arangoDB.query(queryToBeExecuted1, Object.class);
        response2 = cursor3.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while nodesmlSearchList_3: " + e.getMessage().toString());
      }

      for (int j = 0; j < response2.size(); j++) {
        columns1.add("a._id == '" + response2.get(j) + "'");
      }

      String columnIds1 = String.join(" OR ", columns1);
      String queryToBeExecuted2 = "for a in Physical\r\n"
          + "filter " + columnIds1 + "\r\n"
          + "for b in a.nodes\r\n"
          + "return b.id";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);

      //	ArangoCursor<Object> cursor = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted2, Object.class);
        response2 = cursor2.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while nodesmlSearchList_4 : " + e.getMessage().toString());
      }

      for (int j = 0; j < response2.size(); j++) {
        columns2.add("a._key == '" + response2.get(j) + "'");
      }

      String columnIds2 = String.join(" OR ", columns2);

      String queryToBeExecuted3 = "for a in Nodes\r\n"
          + "filter " + columnIds2 + "\r\n"
          + "return a";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

      //	ArangoCursor<Object> cursor = null;
      try {

        cursor1 = arangoDB.query(queryToBeExecuted3, HashMap.class);
        response3 = cursor1.asListRemaining();
        logger.info(String.valueOf(response3));

      } catch (Exception e) {
        log.error("Exception while nodesmlSearchList_5 : " + e.getMessage().toString());
      }


    } else {
      List<String> columns = new ArrayList<String>();
      List<String> columns1 = new ArrayList<String>();
      List<String> columns2 = new ArrayList<String>();

      for (int i = 0; i < response.size(); i++) {
        columns.add("'" + response.get(i).toString() + "'");
        //columns1.add("node.name == '"+ response.get(i).toString() +"'");
      }

      //String columnIds = String.join(" OR ", columns);

      String queryToBeExecuted1 = "for node in mllinkphy\r\n"
          + "filter node._from IN " + columns + "\r\n"
          + "return node._to";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      ArangoCursor<Object> cursor3 = null;
      try {

        cursor3 = arangoDB.query(queryToBeExecuted1, Object.class);
        response2 = cursor3.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while nodesmlSearchList_6 : " + e.getMessage().toString());
      }

      for (int j = 0; j < response2.size(); j++) {
        columns1.add("a._id == '" + response2.get(j) + "'");
      }

      String columnIds1 = String.join(" OR ", columns1);
      String queryToBeExecuted2 = "for a in Physical\r\n"
          + "filter " + columnIds1 + "\r\n"
          + "for b in a.nodes\r\n"
          + "return b.id";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);

      //	ArangoCursor<Object> cursor = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted2, Object.class);
        response2 = cursor2.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while nodesmlSearchList_7 : " + e.getMessage().toString());
      }

      for (int j = 0; j < response2.size(); j++) {
        columns2.add("a._key == '" + response2.get(j) + "'");
      }

      String columnIds2 = String.join(" OR ", columns2);

      String queryToBeExecuted3 = "for a in Nodes\r\n"
          + "filter " + columnIds2 + "\r\n"
          + "return a";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

      //	ArangoCursor<Object> cursor = null;
      try {

        cursor1 = arangoDB.query(queryToBeExecuted3, HashMap.class);
        response3 = cursor1.asListRemaining();
        logger.info(String.valueOf(response3));

      } catch (Exception e) {
        log.error("Exception while nodesmlSearchList_8 : " + e.getMessage().toString());
      }

    }
    return connectArango.listViewresponse(response3);

  }


  public List<Object> nodessemanticStatusUpdate(String columnName, String recommededName,
      String status) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    List<HashMap> qresponse = new ArrayList<>();
    List<Object> qresponse3 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCursor<Object> cursor2 = null;
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor3 = null;

    String queryToBeExecuted3 = "for a in PhysicalDataDictionary\r\n"
        + "filter a.nodes !=null\r\n"
        + "for b in a.nodes\r\n"
        + "filter b.displayName == '" + columnName + "'\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response1 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodessemanticStatusUpdate : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      //JSONObject semantics=new JSONObject();
      JSONObject s1 = new JSONObject(response1.get(i));
      String name = s1.get("name").toString();
      //semantics.put("columnName", name);
      String Id = s1.get("_id").toString();

      String queryToBeExecuted = "for a in mllnkphy\r\n"
          + "filter a._to=='" + Id + "'\r\n"
          + "return a";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted, HashMap.class);
        response3 = cursor3.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while nodessemanticStatusUpdate_2 : " + e.getMessage().toString());
      }
      String id = null;
      for (int j = 0; j < response3.size(); j++) {
        JSONObject s = new JSONObject(response3.get(j));
        if (s.has("confidence_score")) {
          String confidenceScore = s.get("confidence_score").toString();
          //semantics.put("confidenceScore", confidenceScore);
        }
        id = s.getString("_from");

        String query = "for a in ML_collection\r\n"
            + "filter a._id=='" + id + "'\r\n"
            + "return a";

        logger.info("queryToBeExecuted----->" + query);

        try {

          cursor3 = arangoDB.query(query, HashMap.class);
          response3 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while nodessemanticStatusUpdate_3 : " + e.getMessage().toString());
        }
      }

      for (int k = 0; k < response3.size(); k++) {
        JSONObject l = new JSONObject(response3.get(k));
        logger.info("l" + l);
        //  mlcJson.has("name") && !mlcJson.has("Suggestednames")
        if (l.has("name") && !l.has("Suggestednames")) {
          String recommendedName = l.get("name").toString();

          String queryToBeExecuted2 = "for a in mllnkphy\r\n"
              + "filter a._from== '" + id + "'\r\n"
              + "update a with {\"Status\":'" + status + "'} in mllnkphy";

          logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
          //ArangoCursor<Object> cursor = null;
          try {

            cursor3 = arangoDB.query(queryToBeExecuted2, HashMap.class);
            response = cursor3.asListRemaining();

          } catch (Exception e) {
            log.error("Exception while nodessemanticStatusUpdate_3: " + e.getMessage().toString());
          }

        } else if (l.has("Suggestednames")) {
          JSONArray suggestedName = l.getJSONArray("Suggestednames");

          if (status.contains("Accepted") || status.contains("accepted")) {

            String q1 = "for a in ML_collection\r\n"
                + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                + "return a";

            logger.info("queryToBeExecuted----->" + q1);
            try {

              cursor3 = arangoDB.query(q1, HashMap.class);
              qresponse = cursor3.asListRemaining();

            } catch (Exception e) {
              log.error(
                  "Exception while nodessemanticStatusUpdate_4 : " + e.getMessage().toString());
            }

            if (!qresponse.get(0).containsKey("Status")) {

              String query3 = "for a in ML_collection\r\n"
                  + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                  + "UPDATE a WITH {Status: \"Deleted\" } IN ML_collection";

              logger.info("queryToBeExecuted----->" + query3);
              try {

                cursor3 = arangoDB.query(query3, HashMap.class);
                response = cursor3.asListRemaining();

              } catch (Exception e) {
                log.error(
                    "Exception while nodessemanticStatusUpdate_5 : " + e.getMessage().toString());
              }

              String queryToBeExecuted2 = "for a in ML_collection\r\n"
                  + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                  + "for b in a.Suggestednames\r\n"
                  + "filter b.name=='" + recommededName + "'\r\n"
                  + "return b";

              logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
              try {

                cursor3 = arangoDB.query(queryToBeExecuted2, HashMap.class);
                response = cursor3.asListRemaining();

              } catch (Exception e) {
                log.error(
                    "Exception while nodessemanticStatusUpdate_6 : " + e.getMessage().toString());
              }
              double confidence_score = 0;

              for (int c = 0; c < response.size(); c++) {
                JSONObject s = new JSONObject(response.get(c));

                confidence_score = s.getDouble("confidence_score");
              }

              String query = "INSERT {'name':'" + recommededName
                  + "','type':'BusinessTerm','identifier':'recommendations>" + recommededName
                  + "'} In ML_collection \r\n"
                  + "return NEW._id";

              logger.info("queryToBeExecuted----->" + query);

              try {

                cursor2 = arangoDB.query(query, Object.class);
                response2 = cursor2.asListRemaining();

              } catch (Exception e) {
                log.error(
                    "Exception while nodessemanticStatusUpdate_7 : " + e.getMessage().toString());
              }

              String query1 =
                  "INSERT {'_from':'" + response2.get(0) + "','_to':'" + Id + "','Status':'"
                      + status + "','confidence_score':" + confidence_score
                      + ",'role':'is recomended for','coRole':'recommends'} In mllnkphy \r\n";

              logger.info("queryToBeExecuted----->" + query1);

              try {

                cursor2 = arangoDB.query(query1, Object.class);
                response2 = cursor2.asListRemaining();

              } catch (Exception e) {
                log.error(
                    "Exception while nodessemanticStatusUpdate_8: " + e.getMessage().toString());
              }

              //if(status.contains("Accepted") || status.contains("accepted")) {
              if (l.has("Suggestednames")) {

                //	JSONArray suggestedName=l.getJSONArray("suggestedNames");

                String queryToBeExecute2 = "for a in ML_collection\r\n"
                    + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                    + "for b in a.Suggestednames\r\n"
                    + "filter b.name !='" + recommededName + "'\r\n"
                    + "return b";

                logger.info("queryToBeExecuted----->" + queryToBeExecute2);
                try {

                  cursor3 = arangoDB.query(queryToBeExecute2, HashMap.class);
                  response = cursor3.asListRemaining();

                } catch (Exception e) {
                  log.error(
                      "Exception while nodessemanticStatusUpdate_9 : " + e.getMessage().toString());
                }

                String rejectedRecommededName = null;
                for (int c = 0; c < response.size(); c++) {

                  JSONObject s = new JSONObject(response.get(c));
                  rejectedRecommededName = s.getString("name");
                  double confidence_Score = s.getDouble("confidence_score");

                  String querys = "INSERT {'name':'" + rejectedRecommededName
                      + "','type':'BusinessTerm','identifier':'recommendations>"
                      + rejectedRecommededName + "'} In ML_collection \r\n"
                      + "return NEW._id";

                  logger.info("queryToBeExecuted----->" + querys);

                  try {

                    cursor2 = arangoDB.query(querys, Object.class);
                    response2 = cursor2.asListRemaining();

                  } catch (Exception e) {
                    log.error("Exception while nodessemanticStatusUpdate_10 : " + e.getMessage()
                        .toString());
                  }
                  String Status = "Rejected";
                  String querys1 =
                      "INSERT {'_from':'" + response2.get(0) + "','_to':'" + Id + "','Status':'"
                          + Status + "','confidence_score':" + confidence_Score
                          + ",'role':'is recomended for','coRole':'recommends'} In mllnkphy \r\n";

                  logger.info("queryToBeExecuted----->" + querys1);

                  try {

                    cursor2 = arangoDB.query(querys1, Object.class);
                    response2 = cursor2.asListRemaining();

                  } catch (Exception e) {
                    log.error("Exception while nodessemanticStatusUpdate_11 : " + e.getMessage()
                        .toString());
                  }
                }
              }
            } else {

              for (int r = 0; r < response1.size(); r++) {
                //JSONObject semantics=new JSONObject();
                JSONObject sr = new JSONObject(response1.get(r));
                String sname = sr.get("name").toString();
                //semantics.put("columnName", name);
                String Id1 = sr.get("_id").toString();

                String queryToExceute = "for a in mllnkphy\r\n"
                    + "filter a._to=='" + Id1 + "' AND a.Status ==\"Accepted\"\r\n"
                    + "update a with {\"Status\":\"Rejected\"} in mllnkphy";

                logger.info("queryToBeExecuted----->" + queryToExceute);

                try {

                  cursor3 = arangoDB.query(queryToExceute, HashMap.class);
                  response3 = cursor3.asListRemaining();

                } catch (Exception e) {
                  log.error("Exception while nodessemanticStatusUpdate_12 : " + e.getMessage()
                      .toString());
                }

                String query = "for a in ML_collection\r\n"
                    + "filter a.name=='" + recommededName + "'\r\n"
                    + "return a._id";

                logger.info("queryToBeExecuted----->" + query);

                try {

                  cursor2 = arangoDB.query(query, Object.class);
                  qresponse3 = cursor2.asListRemaining();

                } catch (Exception e) {
                  log.error("Exception while nodessemanticStatusUpdate_13 : " + e.getMessage()
                      .toString());
                }

                String querym = "for a in mllnkphy\r\n"
                    + "filter a._from=='" + qresponse3.get(0) + "'\r\n"
                    + "update a with {\"Status\":\"Accepted\"} in mllnkphy";

                logger.info("queryToBeExecuted----->" + querym);

                try {

                  cursor3 = arangoDB.query(querym, HashMap.class);
                  response3 = cursor3.asListRemaining();

                } catch (Exception e) {
                  log.error("Exception while nodessemanticStatusUpdate_14 : " + e.getMessage()
                      .toString());
                }

              }

            }

          } else {
            String querys1 = "for a in ML_collection\r\n"
                + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                + "UPDATE a WITH {\r\n"
                + "   Suggestednames: a.Suggestednames[*\r\n"
                + "     RETURN CURRENT.name == '" + recommededName + "' ?\r\n"
                + "     MERGE(CURRENT, { Status: \"Rejected\" }) : CURRENT ]\r\n"
                + " } IN ML_collection";

            logger.info("queryToBeExecuted----->" + querys1);

            try {

              cursor2 = arangoDB.query(querys1, Object.class);
              response2 = cursor2.asListRemaining();

            } catch (Exception e) {
              log.error(
                  "Exception while nodessemanticStatusUpdate_15 : " + e.getMessage().toString());
            }

            String query = "for a in ML_collection\r\n"
                + "filter a.name=='" + recommededName + "'\r\n"
                + "return a._id";

            logger.info("queryToBeExecuted----->" + query);

            try {

              cursor2 = arangoDB.query(query, Object.class);
              qresponse3 = cursor2.asListRemaining();

            } catch (Exception e) {
              log.error(
                  "Exception while nodessemanticStatusUpdate_16 : " + e.getMessage().toString());
            }

            String querym = "for a in mllnkphy\r\n"
                + "filter a._from=='" + qresponse3.get(0) + "'\r\n"
                + "update a with {\"Status\":\"Rejected\"} in mllnkphy";

            logger.info("queryToBeExecuted----->" + querym);

            try {

              cursor3 = arangoDB.query(querym, HashMap.class);
              response3 = cursor3.asListRemaining();

            } catch (Exception e) {
              log.error(
                  "Exception while nodessemanticStatusUpdate_17: " + e.getMessage().toString());
            }

          }
        }
      }

    }
    return response2;
  }

  public List<String> nodeManualUpdate(String columnName, String recommededName, String status) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCursor<String> cursor2 = null;
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor3 = null;

    String queryToBeExecuted3 = "for a in PhysicalDataDictionary\r\n"
        + "for b in a.nodes\r\n"
        + "filter b.name == '" + columnName + "'\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response1 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeManualUpdate : " + e.getMessage().toString());
    }

    String query = "INSERT {'name':'" + recommededName
        + "','type':'BusinessTerm','identifier':'recommendations>" + recommededName
        + "'} In ML_collection \r\n"
        + "return NEW._id";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor2 = arangoDB.query(query, String.class);
      response2 = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeManualUpdate_2 : " + e.getMessage().toString());
    }
    //				String Id=null;
    //				for(int i=0;i<response2.size();i++) {
    //					JSONObject s1=new JSONObject(response2.get(i));
    //					logger.info("s1"+s1);
    //					Id=s1.get("_id").toString();
    //				}

    String id = null;
    for (int i = 0; i < response1.size(); i++) {
      JSONObject s1 = new JSONObject(response1.get(i));
      id = s1.get("_id").toString();
    }

    String query1 =
        "INSERT {'_from':'" + response2.get(0) + "','_to':'" + id + "','Status':'" + status
            + "','Source':'manually created','role':'represents','coRole':'represented by'} In mllinkphy \r\n";

    logger.info("queryToBeExecuted----->" + query1);

    try {

      cursor2 = arangoDB.query(query1, String.class);
      response2 = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeManualUpdate_3 : " + e.getMessage().toString());
    }

    return response2;
  }


  public List<Object> getcartHolderNodes(String cartHolder) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in Cart\r\n"
        + "filter a.cartNodes !=null && a.cartHolder == '" + cartHolder + "'\r\n"
        + "for b in a.cartNodes\r\n"
        + "return b.arangoNodeKey";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getcartHolderNodes : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < response.size(); i++) {
      columns.add("node._key == '" + response.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    String queryToBeExecuted1 = "for node in Nodes\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getcartHolderNodes_2 : " + e.getMessage().toString());
    }
    return connectArango.tailView(response1);
  }


  public String uploadMatrixExcelFile(MultipartFile file) throws IOException {
    String resmsg = null;
    String filelocation = storeFile(file);
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/downloadFile/")
        .path(fileName).toUriString();
    List<String> response4 = new ArrayList();
    JSONObject counterObj = new JSONObject();
    List<String> teamName = new ArrayList();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        userRoles);
    File fileReader = new File(filelocation);
    FileInputStream fis = new FileInputStream(fileReader);
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    XSSFSheet sheet = workbook.getSheetAt(0);
    java.util.Iterator<Row> row = sheet.iterator();
    XSSFRow headerRow = sheet.getRow(0);
    while (row.hasNext()) {
      Row currentRow = row.next();
      JSONObject objectData = new JSONObject();
      if (currentRow.getRowNum() != 0) {
        for (int column = 0; column < currentRow.getPhysicalNumberOfCells(); column++) {
          logger.info("CellType---->" + currentRow.getCell(column).getCellType());
          if (currentRow.getCell(column).getCellType() == CellType.STRING) {
            ArrayList usersList = new ArrayList();
            ArrayList categoryList = new ArrayList();
            ArrayList<Object> usersList1 = new ArrayList();

            String teamId = null;
            if (column == 0) {
              String teamname = currentRow.getCell(column).getStringCellValue();
              String query = "for a in Teams\r\n"
                  + "filter a.displayName == '" + teamname + "'\r\n"
                  + "RETURN a._id";
              logger.info("queryToBeExecuted----->" + query);

              ArangoCursor<String> cursor = null;

              try {
                cursor = arangoDB.query(query, String.class);
                teamName = cursor.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while uploadMatrixExcelFile : " + e.getMessage().toString());
              }
              teamId = teamName.get(0);
              logger.info("teamId" + teamId);
              //	objectData.put(headerRow.getCell(column).getStringCellValue(),teamId);
              objectData.put("_from", teamId);
              logger.info("teamId" + objectData);
            } else if (column == 1) {
              String rolename = currentRow.getCell(column).getStringCellValue();
              String roleId = "NodeTypes/" + rolename;
              //objectData.put(headerRow.getCell(column).getStringCellValue(),roleId);
              objectData.put("_to", roleId);
            } else if (column == 6) {
              String[] userId = currentRow.getCell(column).getStringCellValue().split(",");
              String emailId = null;
              for (int i = 0; i < userId.length; i++) {
                JSONObject usersObje = new JSONObject();
                if (userId[i].startsWith("[")) {
                  String id = userId[i].toString();
                  String[] emailIds = id.split("\\[");
                  String mail = emailIds[0];
                  emailId = emailIds[1];
                  if (emailId.endsWith("]")) {
                    String[] emailend = emailId.split("\\]");
                    emailId = emailend[0];
                    usersList.add("a.Email=='" + emailId + "'");
                  }
                  logger.info("emailId" + emailId);
                  usersList.add("a.Email=='" + emailId + "'");

                } else if (userId[i].endsWith("]")) {
                  String id = userId[i].toString();
                  String[] emailIds = id.split("\\]");
                  String mail = emailIds[0];
                  usersList.add("a.Email=='" + mail + "'");
                } else {
                  usersObje.put("id", userId[i]);
                  usersList.add("a.Email=='" + userId[i] + "'");
                }


              }
              logger.info("usersList" + usersList);

              List<Object> response = new ArrayList();
              String columnIds = String.join(" OR ", usersList);
              String queryToBeExecuted = "for a in registerUsers\r\n"
                  + "filter " + columnIds + "\r\n"
                  + "RETURN {id:a._key}";
              logger.info("queryToBeExecuted----->" + queryToBeExecuted);

              ArangoCursor<Object> cursor1 = null;

              try {
                cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
                response = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while uploadMatrixExcelFile_2 : " + e.getMessage().toString());
              }
              logger.info("response" + response);
              //									response.forEach(a->{
              //										JSONObject b=new JSONObject(a);
              //
              //										String id=b.get("id").toString();
              //										JSONObject userIds=new JSONObject();
              //										userIds.put("id",id);
              //										usersList1.add(userIds);
              //									});
              //

              objectData.put(headerRow.getCell(column).getStringCellValue(), response);
            } else if (column == 7) {
              String[] categoryId = currentRow.getCell(column).getStringCellValue().split(",");
              //								for(int i=0;i<categoryId.length;i++) {
              //									logger.info("categoryId"+categoryId[i]);
              //									logger.info("i"+i);
              //									JSONObject usersObje=new JSONObject();
              //									usersObje.put("id",categoryId[i]);
              //									categoryList.add("a.name=='"+categoryId[i]+"'");
              //								}
              //
              String catId = null;
              for (int i = 0; i < categoryId.length; i++) {
                JSONObject usersObje = new JSONObject();
                if (categoryId[i].startsWith("[")) {
                  String id = categoryId[i].toString();
                  String[] categoryIds = id.split("\\[");
                  String mail = categoryIds[0];
                  catId = categoryIds[1];
                  if (catId.endsWith("]")) {
                    String[] catgend = catId.split("\\]");
                    catId = catgend[0];
                    categoryList.add("a.name=='" + catId + "'");
                  }
                  logger.info("catId" + catId);
                  categoryList.add("a.name=='" + catId + "'");

                } else if (categoryId[i].endsWith("]")) {
                  String id = categoryId[i].toString();
                  String[] emailIds = id.split("\\]");
                  String mail = emailIds[0];
                  categoryList.add("a.name=='" + mail + "'");
                } else {
                  usersObje.put("id", categoryId[i]);
                  categoryList.add("a.name=='" + categoryId[i] + "'");
                }


              }

              List<Object> categoriesList = new ArrayList();
              String columnIds = String.join(" OR ", categoryList);
              String queryToBeExecuted = "for a in Business\r\n"
                  + "filter " + columnIds + "\r\n"
                  + "RETURN {id:a._key}";
              logger.info("queryToBeExecuted----->" + queryToBeExecuted);
              ArangoCursor<Object> cursor1 = null;
              try {
                cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
                categoriesList = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while uploadMatrixExcelFile_3 : " + e.getMessage().toString());
              }
              logger.info("response" + categoriesList);

              String query = "for a in " + userRoles + "\r\n"
                  + "filter a._from == '" + teamName.get(0) + "'\r\n"
                  + "return a";
              logger.info("query----->" + query);
              ArangoCursor<String> cursor4 = null;
              try {

                cursor4 = arangoDB.query(query, String.class);
                response4 = cursor4.asListRemaining();
                logger.info(String.valueOf(response4));

              } catch (Exception e) {
                log.error("Exception while uploadMatrixExcelFile_4 : " + e.getMessage().toString());
              }

              List<String> contextName = new ArrayList<>();
              HashSet<String> contextName2 = new HashSet<String>();
              HashSet<Integer> contextName3 = new HashSet<Integer>();
              List<String> contextColumns = new ArrayList<>();
              //response4.clear();
              for (int i = 0; i < response4.size(); i++) {
                JSONObject s = new JSONObject(response4.get(i));
                contextName.clear();
                logger.info("s" + s);
                Set<String> keys = s.keySet();
                Object[] namesArray = keys.toArray();
                for (int j = 0; j < namesArray.length; j++) {
                  logger.info(j + ": " + namesArray[j]);
                  contextName.add(namesArray[j].toString());
                }

                for (int x = 0; x < contextName.size(); x++) {
                  if (contextName.get(x).contains("lastmodifiedon") || contextName.get(x)
                      .contains("_from") || contextName.get(x).contains("createdby")
                      || contextName.get(x).contains("_rev") || contextName.get(x)
                      .contains("lastmodifiedby") || contextName.get(x).contains("_id")
                      || contextName.get(x).contains("_to") || contextName.get(x)
                      .contains("_key") || contextName.get(x).contains("createdon")
                      || contextName.get(x).contains("users") || contextName.get(x)
                      .contains("contextCounter")) {

                  } else {
                    contextName2.add(contextName.get(x));
                  }
                }

                for (int x = 0; x < contextName.size(); x++) {
                  if (contextName.get(x).contains("contextCounter")) {
                    contextName3.add(s.getInt("contextCounter"));
                  }
                }

              }

              Set<String> setWithUniqueValues = new HashSet<>(contextName2);
              logger.info("setWithUniqueValues" + setWithUniqueValues);
              List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
              logger.info("listWithUniqueContextValues" + listWithUniqueContextValues);

              Set<Integer> setWithUniqueValues1 = new HashSet<>(contextName3);
              List<Integer> listWithUniqueContextValues1 = new ArrayList<>(
                  setWithUniqueValues1);

              int largest = Collections.max(listWithUniqueContextValues1);
              logger.info("largest" + largest);
              int largest1 = largest + 1;

              counterObj.put("counter", largest1);

              objectData.put("Context" + counterObj.getInt("counter"), categoriesList);
            } else {
              objectData.put(headerRow.getCell(column).getStringCellValue(),
                  currentRow.getCell(column).getStringCellValue());
              logger.info("objectData" + objectData);
            }

          } else if (currentRow.getCell(column).getCellType() == CellType.BOOLEAN) {
            objectData.put(headerRow.getCell(column).getStringCellValue(),
                currentRow.getCell(column).getBooleanCellValue());
            logger.info("objectData" + objectData);
          } else if (currentRow.getCell(column).getCellType() == CellType.NUMERIC) {

            if (column == 6) {
              JSONObject contextObje = new JSONObject();
              ArrayList<Object> contextList = new ArrayList();
              double users = currentRow.getCell(column).getNumericCellValue();
              String[] contxId = String.valueOf(users).split(",");
              for (int i = 0; i < contxId.length; i++) {
                contextObje.put("id", contxId[i]);
                contextList.add(contextObje);
              }
              objectData.put(headerRow.getCell(column).getStringCellValue(), contextList);
            } else if (column == 7) {
              JSONObject usersObje = new JSONObject();
              ArrayList<Object> usersList = new ArrayList();
              double users = currentRow.getCell(column).getNumericCellValue();
              String[] userId = String.valueOf(users).split(",");
              for (int i = 0; i < userId.length; i++) {
                usersObje.put("id", userId[i]);
                usersList.add(usersObje);
              }
              objectData.put(headerRow.getCell(column).getStringCellValue(), usersList);
              //objectData.put(headerRow.getCell(column).getStringCellValue(),usersList);
            } else {
              objectData.put(headerRow.getCell(column).getStringCellValue(),
                  currentRow.getCell(column).getNumericCellValue());
              logger.info("Numeric type---->" + currentRow.getCell(column).getCellType());
              logger.info("objectData" + objectData);
            }
          } else if (currentRow.getCell(column).getCellType() == CellType.BLANK) {
            objectData.put(headerRow.getCell(column).getStringCellValue(), "");
            logger.info("objectData" + objectData);
          } else if (currentRow.getCell(column).getCellType() == CellType.FORMULA) {
            objectData.put(headerRow.getCell(column).getStringCellValue(),
                currentRow.getCell(column).getArrayFormulaRange());
            logger.info("objectData" + objectData);
          }
        }
        try {
          resmsg = connectArango.importDocuments2Arango(objectData.toMap(), userRoles);
          logger.info("Documents created");
        } catch (ArangoDBException e) {
          System.err.println("Failed to create  document. " + e.getMessage());
        }
      }
    }
    return resmsg;

  }


  public ByteArrayInputStream downloadFile() throws IOException {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    XSSFWorkbook workbook = new XSSFWorkbook();

    //String excelFilePath = "Reviews-export.xlsx";
    //String excelFilePath = "C:/GFGsheet.xlsx";
    ByteArrayOutputStream out = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        userRoles);

    String query = "for doc in Teams\r\n"
        + "filter doc.teamStructure==\"Matrix\"\r\n"
        + "return doc._id";
    logger.info("query----->" + query);

    ArangoCursor<String> cursor = null;

    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while downloadFile : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < response.size(); i++) {
      columns.add("a._from == '" + response.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);
    String queryToBeExecuted = "for a in " + userRoles + "\r\n"
        + "filter " + columnIds + "\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    try {
      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response1 = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while downloadFile_2 : " + e.getMessage().toString());
    }

    XSSFSheet sheet = workbook.createSheet("Reviews");
    logger.info("sheet" + sheet);
    writeHeaderLine(sheet);

    writeDataLines(response1, workbook, sheet);

    // FileOutputStream outputStream = new FileOutputStream(excelFilePath);
    out = new ByteArrayOutputStream();
    workbook.write(out);
    logger.info("workbook" + workbook);
    // out.close();
    // workbook.close();
    // logger.info("outputStream"+outputStream);
    //  workbook.getActiveSheetIndex();

    return new ByteArrayInputStream(out.toByteArray());

  }

  private void writeHeaderLine(XSSFSheet sheet) {

    Row headerRow = sheet.createRow(0);

    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue("teamName");

    headerCell = headerRow.createCell(1);
    headerCell.setCellValue("roles");

    headerCell = headerRow.createCell(2);
    headerCell.setCellValue("createdby");

    headerCell = headerRow.createCell(3);
    headerCell.setCellValue("createdon");

    headerCell = headerRow.createCell(4);
    headerCell.setCellValue("lastmodifiedby");

    headerCell = headerRow.createCell(5);
    headerCell.setCellValue("lastmodifiedon");

    headerCell = headerRow.createCell(6);
    headerCell.setCellValue("users");

    headerCell = headerRow.createCell(7);
    headerCell.setCellValue("Contexts");
  }

  private void writeDataLines(List<String> result, XSSFWorkbook workbook,
      XSSFSheet sheet) {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> teamName = new ArrayList<>();
    List<String> roleName = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        userRoles);

    int rowCount = 1;

    //result.forEach(a->{
    for (int i = 0; i < result.size(); i++) {
      JSONObject x = new JSONObject(result.get(i));
      // while (result.next()) {
      String from = x.getString("_from");

      String query = "for a in Teams\r\n"
          + "filter a._id == '" + from + "'\r\n"
          + "RETURN a.displayName";
      logger.info("queryToBeExecuted----->" + query);

      ArangoCursor<String> cursor = null;

      try {
        cursor = arangoDB.query(query, String.class);
        teamName = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while writeDataLines : " + e.getMessage().toString());
      }

      String teamname = teamName.get(0);

      String to = x.getString("_to");

      String query1 = "for a in NodeTypes\r\n"
          + "filter a._id == '" + to + "'\r\n"
          + "RETURN a.roleName";
      logger.info("queryToBeExecuted----->" + query1);

      try {
        cursor = arangoDB.query(query1, String.class);
        roleName = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while writeDataLines_2 : " + e.getMessage().toString());
      }
      if (!roleName.isEmpty()) {
        String rolename = roleName.get(0);
        String createdby = x.getString("createdby");
        //  Timestamp timestamp = result.getTimestamp("timestamp");
        String createdon = x.getString("createdon");
        String lastmodifiedby = x.getString("lastmodifiedby");
        String lastmodifiedon = x.getString("lastmodifiedon");

        JSONArray users = x.getJSONArray("users");
        List<String> column = new ArrayList<String>();
        for (int u = 0; u < users.length(); u++) {
          JSONObject us = new JSONObject(users.get(u).toString());
          String id = us.get("id").toString();
          column.add("'" + id + "'");
        }

        String columnIds1 = String.join(" OR ", column);

        String queryToBeExecuted = "for a in registerUsers\r\n"
            + "filter a._key IN " + column + "\r\n"
            + "RETURN { Email:a.Email}";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted);

        //ArangoCursor<String> cursor = null;

        try {
          cursor = arangoDB.query(queryToBeExecuted, String.class);
          response = cursor.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while writeDataLines_3 : " + e.getMessage().toString());
        }
        List<String> columns = new ArrayList<String>();
        response.forEach(a -> {
          JSONObject rs = new JSONObject(a);
          logger.info("rs" + rs);
          String email = rs.get("Email").toString();
          //					String lname=rs.get("LastName").toString();
          //					String Name = fname+" "+lname;
          //					logger.info("Name"+Name);
          columns.add(email);
        });

        Row row = sheet.createRow(rowCount++);

        int columnCount = 0;
        Cell cell = row.createCell(columnCount++);
        cell.setCellValue(teamname);

        cell = row.createCell(columnCount++);
        cell.setCellValue(rolename);

        cell = row.createCell(columnCount++);
        cell.setCellValue(createdby);

        cell = row.createCell(columnCount++);
        cell.setCellValue(createdon);

        cell = row.createCell(columnCount++);
        cell.setCellValue(lastmodifiedby);

        cell = row.createCell(columnCount++);
        cell.setCellValue(lastmodifiedon);

        cell = row.createCell(columnCount++);
        cell.setCellValue(columns.toString());

        if (x.has("contextCounter")) {
          int contextCounter = x.getInt("contextCounter");
          String str = "Context";
          String str1 = Integer.toString(contextCounter);
          String str2 = str.concat(str1);
          JSONArray context = x.getJSONArray(str2);
          List<String> column1 = new ArrayList<String>();
          for (int u = 0; u < context.length(); u++) {
            JSONObject us = new JSONObject(context.get(u).toString());
            String id = us.get("id").toString();
            column1.add("'" + id + "'");
          }

          String query3 = "for a in Business\r\n"
              + "filter a._key IN " + column1 + "\r\n"
              + "RETURN { name:a.name }";
          logger.info("queryToBeExecuted----->" + query3);

          ArangoCursor<String> cursor1 = null;

          try {
            cursor1 = arangoDB.query(query3, String.class);
            response1 = cursor1.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while writeDataLines_4 : " + e.getMessage().toString());
          }
          List<String> columns2 = new ArrayList<String>();
          response1.forEach(a -> {
            JSONObject rs = new JSONObject(a);
            logger.info("rs" + rs);
            String Name = rs.get("name").toString();
            columns2.add(Name);
          });

          cell = row.createCell(columnCount++);
          cell.setCellValue(columns2.toString());
        }

      }
      // });
    }
  }


  public List<Object> nodesclassification(String pinId) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> businessResponse = new ArrayList<>();
    List<HashMap> businessResponse1 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    String query = "for a in PinCollection\r\n"
            + "filter a._key == '" + pinId + "' AND a.pinNodes !=null\r\n"
            + "for b in a.pinNodes\r\n"
            + "filter b.type == \"Column\" AND b.arangoNodeKey !=null\r\n"
            + "return b.arangoNodeKey";
    logger.info("queryToBeExecuted----->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodessemantics1 : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<>();
    for (int i = 0; i < response.size(); i++) {
      String[] node = ((String) response.get(i)).split("/");
      columns.add("b.id == '" + node[1] + "'");
    }
    String columnIds = String.join(" OR ", columns);

//    String query2 = "for a in PhysicalDataDictionary\r\n"
//            + "filter " + columnIds + "\r\n"
//            + "return a";
    String query2 = "for a in PhysicalDataDictionary\n"
            + "for b in a.nodes\n"
            + "filter " + columnIds + "\r\n"
            + "return a";
    logger.info("queryToBeExecuted----->" + query2);
    try {
      cursor1 = arangoDB.query(query2, HashMap.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodessemantics1_2 : " + e.getMessage().toString());
    }

    //System.out.println(response1+"response1");
    for (int i = 0; i < response1.size(); i++) {
      JSONObject semantics = new JSONObject();
      JSONObject semanticJsonSuggested = new JSONObject();
      JSONArray suggestedName = new JSONArray();
      JSONObject physicalJson = new JSONObject(response1.get(i));
     // System.out.println(physicalJson+"physicalJson");

      if (physicalJson.has("source")) {
        // String source = physicalJson.getString("source");
      String name = physicalJson.getString("displayName");
      String Id = physicalJson.getString("_id");

      semantics.put("columnName", name);

      String queryToBeExecute = "for a in buslnkphy\r\n"
              + "filter a._to=='" + Id + "'\r\n"
              + "return a";
      logger.info("queryToBeExecuted----->" + queryToBeExecute);
      try {
        cursor1 = arangoDB.query(queryToBeExecute, HashMap.class);
        businessResponse = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while nodessemantics1_3 : " + e.getMessage().toString());
      }
      for (int j = 0; j < businessResponse.size(); j++) {

        JSONObject s = new JSONObject(businessResponse.get(j));
        if (s.has("confidence_score")) {
          String confidenceScore = s.get("confidence_score").toString();
          semantics.put("confidence_score", confidenceScore);
          semantics.put("Status", "Sourced");
        }
        String id = s.getString("_from");

        String querytoexecute = "for a in Business\r\n"
                + "filter a._id=='" + id + "'\r\n"
                + "return a";

        logger.info("queryToBeExecuted----->" + query);

        try {

          cursor1 = arangoDB.query(querytoexecute, HashMap.class);
          businessResponse1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while nodessemantics1_4 : " + e.getMessage().toString());
        }

        for (int k = 0; k < businessResponse1.size(); k++) {
          JSONObject l = new JSONObject(response2.get(k));
          if (l.has("name")) {
            String recommendedName = l.get("name").toString();
            semanticJsonSuggested.put("name", recommendedName);
            suggestedName.put(semanticJsonSuggested.toMap());
          } else if (l.has("Suggestednames")) {
            //JSONArray suggestedName=l.getJSONArray("suggestedNames");
            //semantics.put("SuggestedNames", suggestedName);

            JSONArray suggetName = l.getJSONArray("Suggestednames");
            for (int sb = 0; sb < suggetName.length(); sb++) {
              JSONObject g = new JSONObject(suggetName.get(sb).toString());
              logger.info(String.valueOf(g));
									/*String sname=g.get("name").toString();
								String confidenceScore=g.get("confidenceScore").toString();
								semanticJsonSuggested.put("recommendedName", sname);
								semanticJsonSuggested.put("confidenceScore", confidenceScore);
								suggestedName.put(semanticJsonSuggested.toMap());*/
              suggestedName.put(g.toMap());

            }

          }
          semantics.put("suggestedNames", suggestedName);
        }
        responseList.add(semantics.toMap());
      }

      if (businessResponse.isEmpty()) {

        String query3 = "for a in mllnkphy\r\n"
                + "filter a._to == '" + Id + "'\r\n"
                + "return a";
        logger.info("queryToBeExecuted----->" + query3);
        try {
          cursor1 = arangoDB.query(query3, HashMap.class);
          response2 = cursor1.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while nodessemantics1_5 : " + e.getMessage().toString());
        }
        for (int j = 0; j < response2.size(); j++) {
          JSONObject mlJson = new JSONObject(response2.get(j));
          String Id1 = mlJson.getString("_from");
          if (mlJson.has("confidence")) {
            String confidenceScore = mlJson.get("confidence").toString();
            semanticJsonSuggested.put("confidence_score", confidenceScore);
            semantics.put("confidence_score", confidenceScore);
          }
          if (mlJson.has("Status")) {
            String status = mlJson.get("Status").toString();
            if (status.contains("Accepted") || status.contains("accepted")) {

              if (mlJson.has("confidence_score")) {
                String confidenceScore = mlJson.get("confidence_score").toString();
                semanticJsonSuggested.put("confidence_score", confidenceScore);
                semantics.put("confidence_score", confidenceScore);
              }
              if (mlJson.has("Status")) {
                String Status = mlJson.get("Status").toString();
                semanticJsonSuggested.put("Status", Status);
                semantics.put("Status", Status);
              }
              if (mlJson.has("Source")) {
                String Source = mlJson.get("Source").toString();
                semanticJsonSuggested.put("Source", Source);
                semantics.put("Source", Source);
              }

              String query4 = "for a in ML_collection\r\n"
                      + "filter a._id =='" + Id1 + "'\r\n"
                      + "return a";

              logger.info("queryToBeExecuted----->" + query4);
              try {
                cursor1 = arangoDB.query(query4, HashMap.class);
                response3 = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while nodessemantics1_6 : " + e.getMessage().toString());
              }

              for (int k = 0; k < response3.size(); k++) {
                JSONObject mlcJson = new JSONObject(response3.get(k));
                if (mlcJson.has("name")) {
                  String recommendedName = mlcJson.get("name").toString();
                  semantics.put("name", recommendedName);
                }
              }

            }
          }
          if (mlJson.has("confidence_score")) {
            String confidenceScore = mlJson.get("confidence_score").toString();
            semanticJsonSuggested.put("confidence_score", confidenceScore);
          }
          if (mlJson.has("Status")) {
            String Status = mlJson.get("Status").toString();
            semanticJsonSuggested.put("Status", Status);
          }
          if (mlJson.has("Source")) {
            String Source = mlJson.get("Source").toString();
            semanticJsonSuggested.put("Source", Source);
          }

          String query4 = "for a in ML_collection\r\n"
                  + "filter a._id =='" + Id1 + "'\r\n"
                  + "return a";

          logger.info("queryToBeExecuted----->" + query4);
          try {
            cursor1 = arangoDB.query(query4, HashMap.class);
            response3 = cursor1.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while nodessemantics1_7 : " + e.getMessage().toString());
          }

          for (int k = 0; k < response3.size(); k++) {
            JSONObject mlcJson = new JSONObject(response3.get(k));
            if (mlcJson.has("name") && !mlcJson.has("Suggestednames")) {
              String recommendedName = mlcJson.get("name").toString();
              semanticJsonSuggested.put("name", recommendedName);
              suggestedName.put(semanticJsonSuggested.toMap());
            } else if (mlcJson.has("Suggestednames") && mlcJson.has("Status")) {
              logger.info("no values to insert");
            } else if (mlcJson.has("Suggestednames") && mlcJson.has("name")) {
              JSONArray suggestName = mlcJson.getJSONArray("Suggestednames");
              for (int s = 0; s < suggestName.length(); s++) {
                JSONObject l = new JSONObject(suggestName.get(s).toString());
                logger.info(String.valueOf(l));
										/*String sname=l.get("name").toString();
									String confidenceScore=l.get("confidenceScore").toString();

									semanticJsonSuggested.put("recommendedName", sname);
									semanticJsonSuggested.put("confidenceScore", confidenceScore);
									suggestedName.put(semanticJsonSuggested.toMap());*/
                suggestedName.put(l.toMap());

              }
            }
            semantics.put("suggestedNames", suggestedName);
          }
        }
        responseList.add(semantics.toMap());
      }
       }
    }
    return responseList;
  }


  public List<Object> nodesclassificationStatusUpdate(String columnName, String recommededName,
      String status) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    List<HashMap> qresponse = new ArrayList<>();
    List<Object> qresponse3 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCursor<Object> cursor2 = null;
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor3 = null;

    String queryToBeExecuted3 = "for a in PhysicalDataDictionary\r\n"
            + "filter a.nodes !=null\r\n"
            + "for b in a.nodes\r\n"
            + "filter b.displayName == '" + columnName + "'\r\n"
            + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response1 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodessemanticStatusUpdate : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      //JSONObject semantics=new JSONObject();
      JSONObject s1 = new JSONObject(response1.get(i));
      String name = s1.get("name").toString();
      //semantics.put("columnName", name);
      String Id = s1.get("_id").toString();

      String queryToBeExecuted = "for a in mllnkphy\r\n"
              + "filter a._to=='" + Id + "'\r\n"
              + "return a";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted, HashMap.class);
        response3 = cursor3.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while nodessemanticStatusUpdate_2 : " + e.getMessage().toString());
      }
      String id = null;
      for (int j = 0; j < response3.size(); j++) {
        JSONObject s = new JSONObject(response3.get(j));
        if (s.has("confidence_score")) {
          String confidenceScore = s.get("confidence_score").toString();
          //semantics.put("confidenceScore", confidenceScore);
        }
        id = s.getString("_from");

        String query = "for a in ML_collection\r\n"
                + "filter a._id=='" + id + "'\r\n"
                + "return a";

        logger.info("queryToBeExecuted----->" + query);

        try {

          cursor3 = arangoDB.query(query, HashMap.class);
          response3 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while nodessemanticStatusUpdate_3 : " + e.getMessage().toString());
        }
      }

      for (int k = 0; k < response3.size(); k++) {
        JSONObject l = new JSONObject(response3.get(k));
        logger.info("l" + l);
        //  mlcJson.has("name") && !mlcJson.has("Suggestednames")
        if (l.has("name") && !l.has("Suggestednames")) {
          String recommendedName = l.get("name").toString();

          String queryToBeExecuted2 = "for a in mllnkphy\r\n"
                  + "filter a._from== '" + id + "'\r\n"
                  + "update a with {\"Status\":'" + status + "'} in mllnkphy";

          logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
          //ArangoCursor<Object> cursor = null;
          try {

            cursor3 = arangoDB.query(queryToBeExecuted2, HashMap.class);
            response = cursor3.asListRemaining();

          } catch (Exception e) {
            log.error("Exception while nodessemanticStatusUpdate_3: " + e.getMessage().toString());
          }

        } else if (l.has("Suggestednames")) {
          JSONArray suggestedName = l.getJSONArray("Suggestednames");

          if (status.contains("Accepted") || status.contains("accepted")) {

            String q1 = "for a in ML_collection\r\n"
                    + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                    + "return a";

            logger.info("queryToBeExecuted----->" + q1);
            try {

              cursor3 = arangoDB.query(q1, HashMap.class);
              qresponse = cursor3.asListRemaining();

            } catch (Exception e) {
              log.error(
                      "Exception while nodessemanticStatusUpdate_4 : " + e.getMessage().toString());
            }

            if (!qresponse.get(0).containsKey("Status")) {

              String query3 = "for a in ML_collection\r\n"
                      + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                      + "UPDATE a WITH {Status: \"Deleted\" } IN ML_collection";

              logger.info("queryToBeExecuted----->" + query3);
              try {

                cursor3 = arangoDB.query(query3, HashMap.class);
                response = cursor3.asListRemaining();

              } catch (Exception e) {
                log.error(
                        "Exception while nodessemanticStatusUpdate_5 : " + e.getMessage().toString());
              }

              String queryToBeExecuted2 = "for a in ML_collection\r\n"
                      + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                      + "for b in a.Suggestednames\r\n"
                      + "filter b.name=='" + recommededName + "'\r\n"
                      + "return b";

              logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
              try {

                cursor3 = arangoDB.query(queryToBeExecuted2, HashMap.class);
                response = cursor3.asListRemaining();

              } catch (Exception e) {
                log.error(
                        "Exception while nodessemanticStatusUpdate_6 : " + e.getMessage().toString());
              }
              double confidence_score = 0;

              for (int c = 0; c < response.size(); c++) {
                JSONObject s = new JSONObject(response.get(c));

                confidence_score = s.getDouble("confidence_score");
              }

              String query = "INSERT {'name':'" + recommededName
                      + "','type':'BusinessTerm','identifier':'recommendations>" + recommededName
                      + "'} In ML_collection \r\n"
                      + "return NEW._id";

              logger.info("queryToBeExecuted----->" + query);

              try {

                cursor2 = arangoDB.query(query, Object.class);
                response2 = cursor2.asListRemaining();

              } catch (Exception e) {
                log.error(
                        "Exception while nodessemanticStatusUpdate_7 : " + e.getMessage().toString());
              }

              String query1 =
                      "INSERT {'_from':'" + response2.get(0) + "','_to':'" + Id + "','Status':'"
                              + status + "','confidence_score':" + confidence_score
                              + ",'role':'is recomended for','coRole':'recommends'} In mllnkphy \r\n";

              logger.info("queryToBeExecuted----->" + query1);

              try {

                cursor2 = arangoDB.query(query1, Object.class);
                response2 = cursor2.asListRemaining();

              } catch (Exception e) {
                log.error(
                        "Exception while nodessemanticStatusUpdate_8: " + e.getMessage().toString());
              }

              //if(status.contains("Accepted") || status.contains("accepted")) {
              if (l.has("Suggestednames")) {

                //	JSONArray suggestedName=l.getJSONArray("suggestedNames");

                String queryToBeExecute2 = "for a in ML_collection\r\n"
                        + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                        + "for b in a.Suggestednames\r\n"
                        + "filter b.name !='" + recommededName + "'\r\n"
                        + "return b";

                logger.info("queryToBeExecuted----->" + queryToBeExecute2);
                try {

                  cursor3 = arangoDB.query(queryToBeExecute2, HashMap.class);
                  response = cursor3.asListRemaining();

                } catch (Exception e) {
                  log.error(
                          "Exception while nodessemanticStatusUpdate_9 : " + e.getMessage().toString());
                }

                String rejectedRecommededName = null;
                for (int c = 0; c < response.size(); c++) {

                  JSONObject s = new JSONObject(response.get(c));
                  rejectedRecommededName = s.getString("name");
                  double confidence_Score = s.getDouble("confidence_score");

                  String querys = "INSERT {'name':'" + rejectedRecommededName
                          + "','type':'BusinessTerm','identifier':'recommendations>"
                          + rejectedRecommededName + "'} In ML_collection \r\n"
                          + "return NEW._id";

                  logger.info("queryToBeExecuted----->" + querys);

                  try {

                    cursor2 = arangoDB.query(querys, Object.class);
                    response2 = cursor2.asListRemaining();

                  } catch (Exception e) {
                    log.error("Exception while nodessemanticStatusUpdate_10 : " + e.getMessage()
                            .toString());
                  }
                  String Status = "Rejected";
                  String querys1 =
                          "INSERT {'_from':'" + response2.get(0) + "','_to':'" + Id + "','Status':'"
                                  + Status + "','confidence_score':" + confidence_Score
                                  + ",'role':'is recomended for','coRole':'recommends'} In mllnkphy \r\n";

                  logger.info("queryToBeExecuted----->" + querys1);

                  try {

                    cursor2 = arangoDB.query(querys1, Object.class);
                    response2 = cursor2.asListRemaining();

                  } catch (Exception e) {
                    log.error("Exception while nodessemanticStatusUpdate_11 : " + e.getMessage()
                            .toString());
                  }
                }
              }
            } else {

              for (int r = 0; r < response1.size(); r++) {
                //JSONObject semantics=new JSONObject();
                JSONObject sr = new JSONObject(response1.get(r));
                String sname = sr.get("name").toString();
                //semantics.put("columnName", name);
                String Id1 = sr.get("_id").toString();

                String queryToExceute = "for a in mllnkphy\r\n"
                        + "filter a._to=='" + Id1 + "' AND a.Status ==\"Accepted\"\r\n"
                        + "update a with {\"Status\":\"Rejected\"} in mllnkphy";

                logger.info("queryToBeExecuted----->" + queryToExceute);

                try {

                  cursor3 = arangoDB.query(queryToExceute, HashMap.class);
                  response3 = cursor3.asListRemaining();

                } catch (Exception e) {
                  log.error("Exception while nodessemanticStatusUpdate_12 : " + e.getMessage()
                          .toString());
                }

                String query = "for a in ML_collection\r\n"
                        + "filter a.name=='" + recommededName + "'\r\n"
                        + "return a._id";

                logger.info("queryToBeExecuted----->" + query);

                try {

                  cursor2 = arangoDB.query(query, Object.class);
                  qresponse3 = cursor2.asListRemaining();

                } catch (Exception e) {
                  log.error("Exception while nodessemanticStatusUpdate_13 : " + e.getMessage()
                          .toString());
                }

                String querym = "for a in mllnkphy\r\n"
                        + "filter a._from=='" + qresponse3.get(0) + "'\r\n"
                        + "update a with {\"Status\":\"Accepted\"} in mllnkphy";

                logger.info("queryToBeExecuted----->" + querym);

                try {

                  cursor3 = arangoDB.query(querym, HashMap.class);
                  response3 = cursor3.asListRemaining();

                } catch (Exception e) {
                  log.error("Exception while nodessemanticStatusUpdate_14 : " + e.getMessage()
                          .toString());
                }

              }

            }

          } else {
            String querys1 = "for a in ML_collection\r\n"
                    + "filter a._id == '" + id + "' AND a.Suggestednames !=null\r\n"
                    + "UPDATE a WITH {\r\n"
                    + "   Suggestednames: a.Suggestednames[*\r\n"
                    + "     RETURN CURRENT.name == '" + recommededName + "' ?\r\n"
                    + "     MERGE(CURRENT, { Status: \"Rejected\" }) : CURRENT ]\r\n"
                    + " } IN ML_collection";

            logger.info("queryToBeExecuted----->" + querys1);

            try {

              cursor2 = arangoDB.query(querys1, Object.class);
              response2 = cursor2.asListRemaining();

            } catch (Exception e) {
              log.error(
                      "Exception while nodessemanticStatusUpdate_15 : " + e.getMessage().toString());
            }

            String query = "for a in ML_collection\r\n"
                    + "filter a.name=='" + recommededName + "'\r\n"
                    + "return a._id";

            logger.info("queryToBeExecuted----->" + query);

            try {

              cursor2 = arangoDB.query(query, Object.class);
              qresponse3 = cursor2.asListRemaining();

            } catch (Exception e) {
              log.error(
                      "Exception while nodessemanticStatusUpdate_16 : " + e.getMessage().toString());
            }

            String querym = "for a in mllnkphy\r\n"
                    + "filter a._from=='" + qresponse3.get(0) + "'\r\n"
                    + "update a with {\"Status\":\"Rejected\"} in mllnkphy";

            logger.info("queryToBeExecuted----->" + querym);

            try {

              cursor3 = arangoDB.query(querym, HashMap.class);
              response3 = cursor3.asListRemaining();

            } catch (Exception e) {
              log.error(
                      "Exception while nodessemanticStatusUpdate_17: " + e.getMessage().toString());
            }

          }
        }
      }

    }
    return response2;
  }


  public List<String> classificationSuggestedTermsList() {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<String> cursor2 = null;
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor3 = null;

    String queryToBeExecuted1 = "for a in ML_collection\r\n"
        + "filter a.name !=null \r\n"
        + "return a.name";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    try {

      cursor = arangoDB.query(queryToBeExecuted1, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while classificationSuggestedTermsList : " + e.getMessage().toString());
    }

    for (int i = 0; i < response.size(); i++) {
      columns.add(response.get(i));
    }

    String query = "for a in ML_collection\r\n"
        + "filter a.suggestedNames !=null \r\n"
        + "for b in a.suggestedNames\r\n"
        + "return b.name";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor = arangoDB.query(query, String.class);
      response1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error(
          "Exception while classificationSuggestedTermsList_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      columns.add(response1.get(i));
    }

    return columns;


  }


  public List<String> nodeManualUpdateForClassification(String columnName, String recommededName,
      String status) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<String> cursor2 = null;
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor3 = null;

    String queryToBeExecuted3 = "for a in PhysicalDataDictionary\r\n"
        + "for b in a.nodes\r\n"
        + "filter b.name == '" + columnName + "'\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response1 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeManualUpdateForClassification : " + e.getMessage().toString());
    }

    String query = "INSERT {'name':'" + recommededName
        + "','type':'sensitiveType','identifier':'recommendations>" + recommededName
        + "'} In ML_collection \r\n"
        + "return NEW._id";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor2 = arangoDB.query(query, String.class);
      response2 = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error(
          "Exception while nodeManualUpdateForClassification_2 : " + e.getMessage().toString());
    }

    String id = null;
    for (int i = 0; i < response1.size(); i++) {
      JSONObject s1 = new JSONObject(response1.get(i));
      id = s1.get("_id").toString();
    }

    String query1 =
        "INSERT {'_from':'" + response2.get(0) + "','_to':'" + id + "','Status':'" + status
            + "','Source':'manually created','role':'classifies','coRole':'is classified by'} In mllinkphy \r\n";

    logger.info("queryToBeExecuted----->" + query1);

    try {

      cursor2 = arangoDB.query(query1, String.class);
      response2 = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error(
          "Exception while nodeManualUpdateForClassification_3 : " + e.getMessage().toString());
    }

    return response2;

  }


  public List<Integer> rangerNodeId(List<String> orderIds) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> responseList = new ArrayList<>();
    List<Integer> columns2 = new ArrayList<Integer>();
    int value;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<HashMap> cursor = null;
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    //List<String> columns2=new ArrayList<String>();
    for (int i = 0; i < orderIds.size(); i++) {
      columns.add("doc._key=='" + orderIds.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted3 = "for doc in Nodes\r\n"
        + "filter " + columnIds + "\r\n"
        + "return {sources:doc.relations.sources}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while rangerNodeId : " + e.getMessage().toString());
    }

    for (int j = 0; j < response.size(); j++) {
      JSONObject targetObject = new JSONObject(response.get(j));
      logger.info(String.valueOf(targetObject));
      JSONArray targets = targetObject.getJSONArray("sources");
      for (int k = 0; k < targets.length(); k++) {
        JSONObject targetrelations = new JSONObject(targets.get(k).toString());
        //String coRole=targetrelations.get("CoRole").toString();
        String role = null;
        if (targetrelations.has("role")) {
          role = targetrelations.get("role").toString();
        }

        JSONObject source = targetrelations.getJSONObject("source");
        String type = source.getString("type");
        if (role.equals("applies to") && type.equals("Policy")) {
          columns1.add("doc._key=='" + source.getString("id") + "'");
        }

      }
    }

    String columnIds1 = String.join(" OR ", columns1);
    String query = "for doc in Nodes\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "RETURN {attributes:doc.attributes}";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while rangerNodeId_2 : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);

    for (int j = 0; j < response1.size(); j++) {
      JSONObject attributesObject = new JSONObject(response1.get(j));
      logger.info(String.valueOf(attributesObject));
      JSONArray attributes = attributesObject.getJSONArray("attributes");

      if (!attributes.isEmpty()) {

        for (int k = 0; k < attributes.length(); k++) {
          JSONObject attrsObj = new JSONObject(attributes.get(k).toString());
          //String idAttribute=attrsObj.getString("ID");

          if (attrsObj.get("name").toString().equals("ranger_id")) {
            if (attrsObj.has("value")) {
              value = attrsObj.getInt("value");
              columns2.add(value);
            }
            //HashMap nodesinfo1=new HashMap();
            //nodesinfo1.put("value", value);
            //responseList.add(nodesinfo1);
          }
        }

      }
    }
    return columns2;

  }


  public List<String> gcpNodeId(List<String> orderIds) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> responseList = new ArrayList<>();
    List<String> columns2 = new ArrayList<String>();
    String value = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCursor<HashMap> cursor = null;
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> schemacolumns = new ArrayList<String>();
    for (int i = 0; i < orderIds.size(); i++) {
      columns.add("doc._key=='" + orderIds.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted3 = "for doc in Nodes\r\n"
        + "filter " + columnIds + "\r\n"
        + "return {targets:doc.relations.targets}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    try {

      cursor = arangoDB.query(queryToBeExecuted3, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while gcpNodeId : " + e.getMessage().toString());
    }
    logger.info("response" + response);
    String type = null;
    for (int j = 0; j < response.size(); j++) {
      JSONObject targetObject = new JSONObject(response.get(j));
      logger.info(String.valueOf(targetObject));
      JSONArray targets = targetObject.getJSONArray("targets");
      for (int k = 0; k < targets.length(); k++) {
        JSONObject targetrelations = new JSONObject(targets.get(k).toString());
        //String coRole=targetrelations.get("CoRole").toString();
        String coRole = null;
        if (targetrelations.has("CoRole")) {
          coRole = targetrelations.get("CoRole").toString();
        } else {
          coRole = targetrelations.get("coRole").toString();
        }

        JSONObject target = targetrelations.getJSONObject("target");
        type = target.getString("type");
        if (type.contains("")) {
          if (coRole.equals("used by") || coRole.equals("is part of") && type.equals("Table")) {
            columns1.add("doc._key=='" + target.getString("id") + "'");
          } else if (coRole.equals("used by") || coRole.equals("is part of") && type.equals(
              "Schema")) {
            schemacolumns.add("doc._key=='" + target.getString("id") + "'");
          }
        }

      }
    }
    String columnIds1 = String.join(" OR ", columns1);
    String columnIds2 = String.join(" OR ", schemacolumns);
    if (type.contains("Table")) {
      String query = "for doc in Nodes\r\n"
          + "filter " + columnIds1 + "\r\n"
          + "RETURN {name:doc.name}";

      logger.info("queryToBeExecuted----->" + query);
      try {
        cursor = arangoDB.query(query, HashMap.class);
        response1 = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while gcpNodeId_2 : " + e.getMessage().toString());
      }
    } else if (type.contains("Schema")) {
      String query = "for doc in Nodes\r\n"
          + "filter " + columnIds1 + "\r\n"
          + "RETURN {name:doc.name}";

      logger.info("queryToBeExecuted----->" + query);
      try {
        cursor = arangoDB.query(query, HashMap.class);
        response1 = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while gcpNodeId_2 : " + e.getMessage().toString());
      }
    }

    logger.info("response1" + response1);
    for (int j = 0; j < response1.size(); j++) {
      JSONObject nameObject = new JSONObject(response1.get(j));
      logger.info(String.valueOf(nameObject));
      String name = nameObject.getString("name");
      columns2.add(name);
    }
    return columns2;
  }


  public ByteArrayInputStream downloadSimpleFile() throws IOException {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    XSSFWorkbook workbook = new XSSFWorkbook();

    //String excelFilePath = "Reviews-export.xlsx";
    //String excelFilePath = "C:/GFGsheet.xlsx";
    ByteArrayOutputStream out = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        userRoles);

    String query = "for doc in Teams\r\n"
        + "filter doc.teamStructure==\"simple\"\r\n"
        + "return doc._id";
    logger.info("query----->" + query);

    ArangoCursor<String> cursor = null;

    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while downloadSimpleFile: " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < response.size(); i++) {
      columns.add("a._from == '" + response.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);
    String queryToBeExecuted = "for a in " + userRoles + "\r\n"
        + "filter " + columnIds + "\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    try {
      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response1 = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while downloadSimpleFile_2 : " + e.getMessage().toString());
    }

    XSSFSheet sheet = workbook.createSheet("Reviews");
    logger.info("sheet" + sheet);
    writeHeaderLine1(sheet);

    writeDataLines1(response1, workbook, sheet);

    // FileOutputStream outputStream = new FileOutputStream(excelFilePath);
    out = new ByteArrayOutputStream();
    workbook.write(out);
    logger.info("workbook" + workbook);
    // out.close();
    // workbook.close();
    // logger.info("outputStream"+outputStream);
    //  workbook.getActiveSheetIndex();

    return new ByteArrayInputStream(out.toByteArray());
  }

  private void writeHeaderLine1(XSSFSheet sheet) {

    Row headerRow = sheet.createRow(0);

    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue("teamName");

    headerCell = headerRow.createCell(1);
    headerCell.setCellValue("roles");

    headerCell = headerRow.createCell(2);
    headerCell.setCellValue("createdby");

    headerCell = headerRow.createCell(3);
    headerCell.setCellValue("createdon");

    headerCell = headerRow.createCell(4);
    headerCell.setCellValue("lastmodifiedby");

    headerCell = headerRow.createCell(5);
    headerCell.setCellValue("lastmodifiedon");

    headerCell = headerRow.createCell(6);
    headerCell.setCellValue("users");

  }

  private void writeDataLines1(List<String> result, XSSFWorkbook workbook, XSSFSheet sheet) {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> teamName = new ArrayList<>();
    List<String> roleName = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        userRoles);

    int rowCount = 1;

    //result.forEach(a->{
    for (int i = 0; i < result.size(); i++) {
      JSONObject x = new JSONObject(result.get(i));
      // while (result.next()) {
      String from = x.getString("_from");

      String query = "for a in Teams\r\n"
          + "filter a._id == '" + from + "'\r\n"
          + "RETURN a.displayName";
      logger.info("queryToBeExecuted----->" + query);

      ArangoCursor<String> cursor = null;

      try {
        cursor = arangoDB.query(query, String.class);
        teamName = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while writeDataLines1 : " + e.getMessage().toString());
      }

      String teamname = teamName.get(0);
      String to = x.getString("_to");

      String query1 = "for a in NodeTypes\r\n"
          + "filter a._id == '" + to + "'\r\n"
          + "RETURN a.roleName";
      logger.info("queryToBeExecuted----->" + query1);

      try {
        cursor = arangoDB.query(query1, String.class);
        roleName = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while writeDataLines1_2 : " + e.getMessage().toString());
      }
      String rolename = roleName.get(0);
      String createdby = x.getString("createdby");
      //  Timestamp timestamp = result.getTimestamp("timestamp");
      String createdon = x.getString("createdon");
      String lastmodifiedby = x.getString("lastmodifiedby");
      String lastmodifiedon = x.getString("lastmodifiedon");

      JSONArray users = x.getJSONArray("users");
      List<String> column = new ArrayList<String>();
      for (int u = 0; u < users.length(); u++) {
        JSONObject us = new JSONObject(users.get(u).toString());
        String id = us.get("id").toString();
        column.add("'" + id + "'");
      }

      String columnIds1 = String.join(" OR ", column);

      String queryToBeExecuted = "for a in registerUsers\r\n"
          + "filter a._key IN " + column + "\r\n"
          + "RETURN { Email:a.Email}";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      //ArangoCursor<String> cursor = null;

      try {
        cursor = arangoDB.query(queryToBeExecuted, String.class);
        response = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while writeDataLines1_3 : " + e.getMessage().toString());
      }
      List<String> columns = new ArrayList<String>();
      response.forEach(a -> {
        JSONObject rs = new JSONObject(a);
        logger.info("rs" + rs);
        String email = rs.get("Email").toString();
        //					String lname=rs.get("LastName").toString();
        //					String Name = fname+" "+lname;
        //					logger.info("Name"+Name);
        columns.add(email);
      });

      Row row = sheet.createRow(rowCount++);

      int columnCount = 0;
      Cell cell = row.createCell(columnCount++);
      cell.setCellValue(teamname);

      cell = row.createCell(columnCount++);
      cell.setCellValue(rolename);

      cell = row.createCell(columnCount++);
      cell.setCellValue(createdby);

      cell = row.createCell(columnCount++);
      cell.setCellValue(createdon);

      cell = row.createCell(columnCount++);
      cell.setCellValue(lastmodifiedby);

      cell = row.createCell(columnCount++);
      cell.setCellValue(lastmodifiedon);

      cell = row.createCell(columnCount++);
      cell.setCellValue(columns.toString());

    }

  }


  public List<Object> getMySimilaritySeachesList(List<String> name) {

    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < name.size(); i++) {
      columns.add("doc.name =='" + name.get(i) + "'");
    }
    String columnIds1 = String.join(" OR ", columns);

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    //				String query = "FOR doc IN "+ viewName +"\r\n"
    //						+ "SEARCH "+ columnIds1 +"\r\n"
    //						+ "SORT BM25(doc) DESC\r\n"
    //						+ "LIMIT 10\r\n"
    //						//+ "collect  AGGREGATE names = UNIQUE(doc)\r\n"
    //						+ "return doc";
    String query = "FOR doc IN LogicalData\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "return doc._id";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getMySimilaritySeachesList : " + e.getMessage().toString());
    }

    List<String> columns1 = new ArrayList<String>();

    for (int i = 0; i < response.size(); i++) {
      columns1.add("doc._from =='" + response.get(i) + "'");
    }
    String columnIds2 = String.join(" OR ", columns1);

    if (!columnIds2.isEmpty()) {
      String query1 = "FOR doc IN simdslnk\r\n"
          + "filter " + columnIds2 + "\r\n"
          + "SORT doc.association ASC\r\n"
          + "LIMIT 10\r\n"
          + "return doc._to";

      logger.info("query--->" + query1);

      try {

        cursor = arangoDB.query(query1, Object.class);
        response1 = cursor.asListRemaining();
        logger.info("response" + response1);

      } catch (Exception e) {
        log.error(
            "Exception while getMySimilaritySeachesList_2 : " + e.getMessage().toString());
      }

      List<String> columns2 = new ArrayList<String>();

      for (int i = 0; i < response1.size(); i++) {
        columns2.add("doc._id =='" + response1.get(i) + "'");
      }

      String columnIds3 = String.join(" OR ", columns2);
      String query2 = "FOR doc IN LogicalData\r\n"
          + "filter " + columnIds3 + "\r\n"
          + "return doc.name";

      logger.info("query--->" + query2);

      try {

        cursor = arangoDB.query(query2, Object.class);
        response2 = cursor.asListRemaining();
        logger.info("response" + response2);

      } catch (Exception e) {
        log.error(
            "Exception while getMySimilaritySeachesList_3 : " + e.getMessage().toString());
      }

      List<String> columns3 = new ArrayList<String>();

      for (int i = 0; i < response2.size(); i++) {
        columns3.add("doc.name =='" + response2.get(i) + "'");
      }
      String columnIds4 = String.join(" OR ", columns3);

      String query3 = "FOR doc IN Nodes\r\n"
          + "filter " + columnIds4 + "\r\n"
          + "return doc";

      logger.info("query--->" + query3);

      try {

        cursor1 = arangoDB.query(query3, HashMap.class);
        response3 = cursor1.asListRemaining();
        logger.info("response" + response3);

      } catch (Exception e) {
        log.error(
            "Exception while getMySimilaritySeachesList_4 : " + e.getMessage().toString());
      }
    }
    return connectArango.tailView(response3);


  }


  public List<Object> getMyRelatedNodesList(List<String> name) {

    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < name.size(); i++) {
      columns.add("doc.name =='" + name.get(i) + "'");
    }
    String columnIds1 = String.join(" OR ", columns);

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    //				String query = "FOR doc IN "+ viewName +"\r\n"
    //						+ "SEARCH "+ columnIds1 +"\r\n"
    //						+ "SORT BM25(doc) DESC\r\n"
    //						+ "LIMIT 10\r\n"
    //						//+ "collect  AGGREGATE names = UNIQUE(doc)\r\n"
    //						+ "return doc";
    String query = "FOR doc IN LogicalData\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "SORT doc.ratings ASC\r\n"
        + "LIMIT 10"
        + "return doc._id";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getMyRelatedNodesList : " + e.getMessage().toString());
    }

    List<String> columns1 = new ArrayList<String>();

    for (int i = 0; i < response.size(); i++) {
      columns1.add("doc._from =='" + response.get(i) + "'");
    }
    String columnIds2 = String.join(" OR ", columns1);

    String query1 = "FOR doc IN simdslnk\r\n"
        + "filter " + columnIds2 + "\r\n"
        + "return doc._to";

    logger.info("query--->" + query1);

    try {

      cursor = arangoDB.query(query1, Object.class);
      response1 = cursor.asListRemaining();
      logger.info("response" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while getMyRelatedNodesList_2 : " + e.getMessage().toString());
    }

    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response1.size(); i++) {
      columns2.add("doc._id =='" + response1.get(i) + "'");
    }

    String columnIds3 = String.join(" OR ", columns2);
    String query2 = "FOR doc IN LogicalData\r\n"
        + "filter " + columnIds3 + "\r\n"
        + "return doc.name";

    logger.info("query--->" + query2);

    try {

      cursor = arangoDB.query(query2, Object.class);
      response2 = cursor.asListRemaining();
      logger.info("response" + response2);

    } catch (Exception e) {
      log.error(
          "Exception while getMyRelatedNodesList_3 : " + e.getMessage().toString());
    }

    List<String> columns3 = new ArrayList<String>();

    for (int i = 0; i < response2.size(); i++) {
      columns3.add("doc.name =='" + response2.get(i) + "'");
    }
    String columnIds4 = String.join(" OR ", columns3);

    String query3 = "FOR doc IN Nodes\r\n"
        + "filter " + columnIds4 + "\r\n"
        + "return doc";

    logger.info("query--->" + query3);

    try {

      cursor1 = arangoDB.query(query3, HashMap.class);
      response3 = cursor1.asListRemaining();
      logger.info("response" + response3);

    } catch (Exception e) {
      log.error(
          "Exception while getMyRelatedNodesList_4 : " + e.getMessage().toString());
    }

    return connectArango.tailView(response3);
  }


  public List<Object> getDatasources() {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for a in Nodes\r\n"
        + "filter a.type.name == \"Database\"\r\n"
        + "return a";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getDatasources : " + e.getMessage().toString());
    }

    return connectArango.tailView(response);
  }


  public List<Object> getDataSets(String key) {

    List<HashMap> response = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for a in Nodes\r\n"
        + "filter a._key == '" + key + "'\r\n"
        + "return a.relations";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getDataSets : " + e.getMessage().toString());
    }

    response.forEach(a -> {
      JSONObject targets = new JSONObject(a);
      JSONArray target = targets.getJSONArray("targets");
      target.forEach(t -> {
        JSONObject dataset = new JSONObject(t.toString());
        logger.info("dataset" + dataset);
        JSONObject datasetTarget = dataset.getJSONObject("target");
        logger.info("datasetTarget" + datasetTarget);
        String type = datasetTarget.getString("type");
        if (type.contains("Data Set")) {
          String id = datasetTarget.getString("id");
          columns.add(id);
          logger.info(String.valueOf(columns));
        }

      });

    });
    return connectArango.graphtailView(columns);

  }


  public String getDataproducts(String nodeName, String description) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;

    JSONObject key = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String productUUID = UUID.randomUUID().toString();
    String query =
        "Insert {\"_key\":'" + productUUID + "',\"id\":'" + productUUID + "',\"displayName\":'"
            + nodeName + "',\"name\":'" + nodeName + "',\"identifier\":'" + nodeName
            + "',\"sourceCatalog\":\"Datasouk\",\"type\": {\r\n"
            + "    \"metaCollectionName\": \"PhysicalDataDictionary\",\r\n"
            + "    \"name\": \"Data Product\",\r\n"
            + "    \"id\": \"00000000-0000-0000-0000-000000031008\"\r\n"
            + "  },\"attributes\":[{\"name\":\"Description\",\"value\":'" + description
            + "'},{\"name\":\"Shoppable\",\"value\":\"true\"}],\"relations\":{\"sources\":[],\"targets\":[]},\"responsibilities\": [],\"createdByUserName\": \"Admin\",\"createdByFullName\": \"Admin Istrator\",\"createdOn\": \"1658245248600\",\"articulationScore\": \"0.0\",\"ratingsCount\": \"0\",\r\n"
            + "  \"avgRating\": \"0.0\", } into Nodes";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getDataproducts : " + e.getMessage().toString());
    }
    key.put("key", productUUID);

    return key.toString();
  }


  public List<String> getDataproductAndDataSetRelation(String key, HashMap dataSetDetails) {

    List<String> response = new ArrayList<>();
    ArangoCursor<String> cursor = null;
    JSONObject sources = new JSONObject();
    JSONObject source = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String id = dataSetDetails.get("id").toString();
    String type = dataSetDetails.get("type").toString();
    String displayName = dataSetDetails.get("displayName").toString();
    String role = "is part of";
    String typeId = "9aa1b8e4-c2a6-41ce-ac9e-08e43d5a901a";
    source.put("id", id);
    source.put("type", type);
    source.put("displayName", displayName);
    source.put("name", displayName);

    sources.put("role", role);
    sources.put("typeId", typeId);
    sources.put("source", source);

    String query = "for a in Nodes\r\n"
        + "filter a._key == '" + key + "'\r\n"
        + "update a with {relations:{sources:push(a.relations.sources," + sources
        + ",true)}} IN Nodes";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getDataproductAndDataSetRelation : " + e.getMessage().toString());
    }

    return response;
  }


  public String uploadFile(MultipartFile file, String key) throws IOException {

    List<Node> assets = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        nodesCollection);
    assets = importService.importExcel(file);
    JSONObject pindetails = new JSONObject();
    for (int i = 0; i < assets.size(); i++) {
      JSONObject s = new JSONObject(assets.get(i));
      String nodekey = s.getString("_key");
      String name = s.getString("displayName");
      JSONObject type = s.getJSONObject("type");
      String nodeType = type.getString("name");
      pindetails.put("arangoNodeKey", "Nodes/" + nodekey);
      pindetails.put("displayName", name);
      pindetails.put("type", nodeType);

      connectArango.importDocuments2Arango(assets.get(i), nodesCollection);
      ArangoCursor<Object> cursor = null;
      String query = "for doc in PinCollection\r\n" + "filter doc._key == '" + key
          + "'\r\n" + "UPDATE doc WITH { pinNodes: push(doc.pinNodes," + pindetails
          + ",true) } IN PinCollection";

      log.info("query--->" + query);
      try {
        cursor = arangoDB.query(query, Object.class);
      } catch (Exception e) {
        log.error(
            "Exception while uploadFile : " + e.getMessage()
                .toString());
      }
    }

    return "success";
  }


  public ResponseEntity<InputStreamResource> downloadJSONFile(String key)
      throws JsonProcessingException {
    List<String> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    JSONArray fileResponse = new JSONArray();
    ByteArrayInputStream excelFilePath1 = null;

    ByteArrayOutputStream out = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        sampleNodesCollection);

    String query = "for doc in PinCollection\n"
        + "filter doc._key=='" + key + "' AND doc.pinNodes !=null\n"
        + "for c in doc.pinNodes\n"
        + "return c.arangoNodeKey";
    log.info("query----->" + query);

    ArangoCursor<String> cursor = null;

    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while downloadJSONFile : " + e.getMessage().toString());
    }
    for (int i = 0; i < response.size(); i++) {
      columns.add("'" + response.get(i) + "'");
    }
    String query1 = "for doc in Nodes\n"
        + "filter doc._id in" + columns + "\r\n"
        + "return doc";
    log.info("query----->" + query1);

    ArangoCursor<HashMap> cursor1 = null;

    try {
      cursor1 = arangoDB.query(query1, HashMap.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while downloadJSONFile_2 : " + e.getMessage().toString());
    }

    //ByteArrayInputStream excelFilePath = null;
    ObjectMapper mapper = new ObjectMapper();
    byte[] buf = mapper.writeValueAsBytes(response1);
    //ByteArrayOutputStream out = new ByteArrayOutputStream();
    //ByteArrayInputStream excelFilePath1 = new ByteArrayInputStream(buf);
    excelFilePath1 = new ByteArrayInputStream(buf);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=manualImport.xlsx");
    return exportService.exportJsonAsExcel(excelFilePath1);

  }

  private void writeHeaderNodes(XSSFSheet sheet) {

    Row headerRow = sheet.createRow(0);

    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue("name");

    headerCell = headerRow.createCell(1);
    headerCell.setCellValue("type");

    headerCell = headerRow.createCell(2);
    headerCell.setCellValue("Description");

    headerCell = headerRow.createCell(3);
    headerCell.setCellValue("_key");

  }

  private void writeDataNodes(List<String> result, XSSFWorkbook workbook, XSSFSheet sheet) {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> teamName = new ArrayList<>();
    List<String> roleName = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        sampleNodesCollection);

    int rowCount = 1;

    //result.forEach(a->{
    for (int i = 0; i < result.size(); i++) {
      JSONObject x = new JSONObject(result.get(i));
      // while (result.next()) {
      String name = x.getString("name");
      JSONObject typeObject = x.getJSONObject("type");
      String type = typeObject.getString("name");
      String identifier = x.getString("identifier");
      String key = x.getString("_key");
      JSONArray attributes = x.getJSONArray("attributes");
      String value = null;
      for (int j = 0; j < attributes.length(); j++) {
        JSONObject y = new JSONObject(attributes.get(j).toString());
        logger.info("y" + y);

        if (y.get("name").toString().equals("Description")) {
          value = y.getString("value");
        }
      }

      Row row = sheet.createRow(rowCount++);
      int columnCount = 0;
      Cell cell = row.createCell(columnCount++);
      cell.setCellValue(name);

      cell = row.createCell(columnCount++);
      cell.setCellValue(type);

      //					cell = row.createCell(columnCount++);
      //					cell.setCellValue(identifier);

      cell = row.createCell(columnCount++);
      cell.setCellValue(value);

      cell = row.createCell(columnCount++);
      cell.setCellValue(key);


    }
  }


  public List<String> addRelations(String key, List<HashMap> details) {

    List<String> response = new ArrayList<>();
    ArangoCursor<String> cursor = null;
    JSONObject sources = new JSONObject();
    JSONObject source = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    for (int i = 0; i < details.size(); i++) {
      JSONObject relDetails = new JSONObject(details.get(i));
      String id = relDetails.get("id").toString();
      String type = relDetails.get("type").toString();
      String displayName = relDetails.get("displayName").toString();
      String name = relDetails.get("name").toString();
      //String coRole=details.get("coRole").toString();
      String roletype = relDetails.get("roletype").toString();
      String rolevalue = relDetails.get("rolevalue").toString();
      //"is part of";
      String typeId = "D0000000-0000-0000-0000-A00000000006";
      //"9aa1b8e4-c2a6-41ce-ac9e-08e43d5a901a";
      //String sourceOrtargetType=details.get("sourceOrtarget").toString();
      if (roletype.equals("role")) {
        source.put("id", id);
        source.put("type", type);
        source.put("displayName", displayName);
        source.put("name", name);

        sources.put("role", rolevalue);
        sources.put("typeId", typeId);
        sources.put("source", source);

        //				String query="for a in Nodes\r\n"
        //						+ "filter a._key == '"+key+"'\r\n"
        //						+ "update a with {relations:{"+sourceOrtargetType+":push(a.relations."+sourceOrtargetType+","+sources+")}} IN Nodes";

        String query = "for a in Nodes\r\n"
            + "filter a._key == '" + key + "'\r\n"
            + "update a with {relations:{sources:push(a.relations.sources," + sources
            + ",true)}} IN Nodes";
        logger.info("query--->" + query);
        try {
          cursor = arangoDB.query(query, String.class);
          response = cursor.asListRemaining();
          logger.info("response" + response);
        } catch (Exception e) {
          log.error(
              "Exception while addRelations : " + e.getMessage().toString());
        }
      } else {

        source.put("id", id);
        source.put("type", type);
        source.put("displayName", displayName);
        source.put("name", displayName);

        sources.put("coRole", rolevalue);
        sources.put("typeId", typeId);
        sources.put("target", source);

        //					String query="for a in Nodes\r\n"
        //							+ "filter a._key == '"+key+"'\r\n"
        //							+ "update a with {relations:{"+sourceOrtargetType+":push(a.relations."+sourceOrtargetType+","+sources+")}} IN Nodes";

        String query = "for a in Nodes\r\n"
            + "filter a._key == '" + key + "'\r\n"
            + "update a with {relations:{targets:push(a.relations.targets," + sources
            + ",true)}} IN Nodes";
        logger.info("query--->" + query);
        try {
          cursor = arangoDB.query(query, String.class);
          response = cursor.asListRemaining();
          logger.info("response" + response);
        } catch (Exception e) {
          log.error(
              "Exception while addRelations : " + e.getMessage().toString());
        }
      }
    }
    return response;

  }


  public List<HashMap> addResponsibility(String key, HashMap details) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String name = details.get("fullName").toString();
    String roleName = details.get("roleName").toString();
    String userName = details.get("firstName").toString();
    String grpName = details.get("groupName").toString();
    JSONObject responsibilities = new JSONObject();

    responsibilities.put("name", name);
    responsibilities.put("roleName", roleName);
    responsibilities.put("userName", userName);
    responsibilities.put("groupName", grpName);

    String query = "for doc in Nodes\r\n"
        + "filter doc._key=='" + key + "'\r\n"
        + "UPDATE doc WITH { responsibilities: push(doc.responsibilities," + responsibilities
        + ",true) } IN Nodes";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addResponsibility : " + e.getMessage().toString());
    }

    return response;


  }


  public String addNodes(String nodeName, String type, String userId, String description) {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> userDetails = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    String productUUID = UUID.randomUUID().toString();
    JSONObject key = new JSONObject();
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);
      if (arangodb != null) {

        String userQuery = "for a in registerUsers\r\n"
            + "filter a._key == '" + userId + "'\r\n"
            + "return {FirstName:a.FirstName,LastName:a.LastName}";
        logger.info("query--->" + userQuery);
        try {
          cursor = arangodb.query(userQuery, HashMap.class);
          response = cursor.asListRemaining();
          logger.info("response" + response);
        } catch (Exception e) {
          log.error(
              "Exception while Creating Data Product Results: "
                  + "addNodes(String nodeName, String type, String userId,String description)"
                  + e.getMessage().toString());
        }
        String firstName = null;
        String lastName = null;
        String fullName = null;
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

        //System.out.println("DATETIME = " + utc.toInstant());
        //Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String datestr = f.format(new Date());
        //source.setCreatedOn(utc.toInstant().toString());
        for (int i = 0; i < response.size(); i++) {
          JSONObject user = new JSONObject(response.get(i));
          firstName = user.getString("FirstName");
          lastName = user.getString("LastName");
          fullName = firstName.concat(" " + lastName);
        }
        String query = null;
        if (description == null) {
          query = "Insert {\"_key\":'" + productUUID + "',\"id\":'" + productUUID
              + "',\"displayName\":'" + nodeName + "',\"name\":'" + nodeName + "',\"identifier\":'"
              + nodeName + "',\"sourceCatalog\":\"Datasouk\",\"type\": {\r\n"
              + "    \"metaCollectionName\": \"PhysicalDataDictionary\",\r\n"
              + "    \"name\": '" + type + "',\r\n"
              + "    \"id\": \"00000000-0000-0000-0000-000000031008\"\r\n"
              + "  },\"attributes\":[{\"name\":\"LastModifiedOn\",\"value\":'" + utc.toInstant()
              .toString()
              + "'}],\"relations\":{\"sources\":[],\"targets\":[]},\"responsibilities\": [],\"createdByUserName\": '"
              + firstName + "',\"createdByFullName\": '" + fullName + "',\"createdOn\": '"
              + utc.toInstant().toString()
              + "',\"articulationScore\": \"0.0\",\"ratingsCount\": \"0\",\"nodesClassification\":\"private\",\r\n"
              + "  \"avgRating\": \"0.0\", } into Nodes";
        } else {
          query = "Insert {\"_key\":'" + productUUID + "',\"id\":'" + productUUID
              + "',\"displayName\":'" + nodeName + "',\"name\":'" + nodeName + "',\"identifier\":'"
              + nodeName + "',\"sourceCatalog\":\"Datasouk\",\"type\": {\r\n"
              + "    \"metaCollectionName\": \"PhysicalDataDictionary\",\r\n"
              + "    \"name\": '" + type + "',\r\n"
              + "    \"id\": \"00000000-0000-0000-0000-000000031008\"\r\n"
              + "  },\"attributes\":[{\"name\":\"Description\",\"value\":'" + description
              + "'},{\"name\":\"LastModifiedOn\",\"value\":'" + utc.toInstant().toString()
              + "'}],\"relations\":{\"sources\":[],\"targets\":[]},\"responsibilities\": [],\"createdByUserName\": '"
              + firstName + "',\"createdByFullName\": '" + fullName + "',\"createdOn\": '"
              + utc.toInstant().toString()
              + "',\"articulationScore\": \"0.0\",\"ratingsCount\": \"0\",\"nodesClassification\":\"private\",\r\n"
              + "  \"avgRating\": \"0.0\", } into Nodes";
        }

        logger.info("query--->" + query);
        try {
          cursor = arangodb.query(query, HashMap.class);
          response = cursor.asListRemaining();
          logger.info("response" + response);
        } catch (Exception e) {
          log.error(
              "Exception while retrieving Data in Search Results: " + e.getMessage().toString());
        }
        key.put("key", productUUID);
      }
      arangoConn.shutdown();
    }
    return key.toString();

  }


  public List<Object> nodessemantics2(String pinId) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> businessResponse = new ArrayList<>();
    List<HashMap> businessResponse1 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    String query = "for a in PinCollection\r\n"
        + "filter a._key == '" + pinId + "' AND a.pinNodes !=null\r\n"
        + "for b in a.pinNodes\r\n"
        + "filter b.type == \"Column\" \r\n"
        + "return b.arangoNodeKey";
    logger.info("queryToBeExecuted----->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodessemantics2 : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<>();
    for (int i = 0; i < response.size(); i++) {
      String[] node = ((String) response.get(i)).split("/");
      //columns.add("a._key == '"+node[1]+"'");
      columns.add("b.id == '" + node[1] + "'");
    }
    String columnIds = String.join(" OR ", columns);

    //			String query2="for a in PhysicalDataDictionary\r\n"
    //					+ "filter "+columnIds+"\r\n"
    //					+ "return a";

    String query2 = "for a in PhysicalDataDictionary\r\n"
        + "filter a.nodes !=null \r\n"
        + "for b in a.nodes\r\n"
        + "filter " + columnIds + "\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + query2);
    try {
      cursor1 = arangoDB.query(query2, HashMap.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodessemantics2_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      JSONObject semantics = new JSONObject();
      JSONObject semanticJsonSuggested = new JSONObject();
      JSONArray suggestedName = new JSONArray();
      JSONObject physicalJson = new JSONObject(response1.get(i));
      String source = physicalJson.getString("source");
      //String Id=null;
      //String name=null;
      //	if(source.equals("Collibra")) {
      String name = physicalJson.getString("name");
      String Id = physicalJson.getString("_id");

      semantics.put("columnName", name);

      String queryToBeExecute = "for a in buslnkphy\r\n"
          + "filter a._to=='" + Id + "'\r\n"
          + "return a";
      logger.info("queryToBeExecuted----->" + queryToBeExecute);
      try {
        cursor1 = arangoDB.query(queryToBeExecute, HashMap.class);
        businessResponse = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while nodessemantics2_3 : " + e.getMessage().toString());
      }
      for (int j = 0; j < businessResponse.size(); j++) {

        JSONObject s = new JSONObject(businessResponse.get(j));
        if (s.has("confidenceScore")) {
          String confidenceScore = s.get("confidenceScore").toString();
          semantics.put("confidenceScore", confidenceScore);
          semantics.put("Status", "Sourced");
        }
        String id = s.getString("_from");

        String querytoexecute = "for a in Business\r\n"
            + "filter a._id=='" + id + "'\r\n"
            + "return a";

        logger.info("queryToBeExecuted----->" + query);

        try {

          cursor1 = arangoDB.query(querytoexecute, HashMap.class);
          businessResponse1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while nodessemantics2_4 : " + e.getMessage().toString());
        }

        for (int k = 0; k < businessResponse1.size(); k++) {
          JSONObject l = new JSONObject(response2.get(k));
          if (l.has("name")) {
            String recommendedName = l.get("name").toString();
            semanticJsonSuggested.put("name", recommendedName);
            suggestedName.put(semanticJsonSuggested.toMap());
          } else if (l.has("suggestedNames")) {
            //JSONArray suggestedName=l.getJSONArray("suggestedNames");
            //semantics.put("SuggestedNames", suggestedName);

            JSONArray suggetName = l.getJSONArray("suggestedNames");
            for (int sb = 0; sb < suggetName.length(); sb++) {
              JSONObject g = new JSONObject(suggetName.get(sb).toString());
              logger.info(String.valueOf(g));
								/*String sname=g.get("name").toString();
								String confidenceScore=g.get("confidenceScore").toString();
								semanticJsonSuggested.put("recommendedName", sname);
								semanticJsonSuggested.put("confidenceScore", confidenceScore);
								suggestedName.put(semanticJsonSuggested.toMap());*/
              suggestedName.put(g.toMap());

            }

          }
          semantics.put("suggestedNames", suggestedName);
        }
        responseList.add(semantics.toMap());
      }

      if (businessResponse.isEmpty()) {

        String query3 = "for a in mllinkphy\r\n"
            + "filter a._to == '" + Id + "'\r\n"
            + "return a";
        logger.info("queryToBeExecuted----->" + query3);
        try {
          cursor1 = arangoDB.query(query3, HashMap.class);
          response2 = cursor1.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while nodessemantics2_5 : " + e.getMessage().toString());
        }
        for (int j = 0; j < response2.size(); j++) {
          JSONObject mlJson = new JSONObject(response2.get(j));
          String Id1 = mlJson.getString("_from");
          if (mlJson.has("Status")) {
            String status = mlJson.get("Status").toString();
            if (status.contains("Accepted") || status.contains("accepted")) {

              if (mlJson.has("confidenceScore")) {
                String confidenceScore = mlJson.get("confidenceScore").toString();
                semanticJsonSuggested.put("confidenceScore", confidenceScore);
                semantics.put("confidenceScore", confidenceScore);
              }
              if (mlJson.has("Status")) {
                String Status = mlJson.get("Status").toString();
                semanticJsonSuggested.put("Status", Status);
                semantics.put("Status", Status);
              }
              if (mlJson.has("Source")) {
                String Source = mlJson.get("Source").toString();
                semanticJsonSuggested.put("Source", Source);
                semantics.put("Source", Source);
              }

              String query4 = "for a in ML_collection\r\n"
                  + "filter a._id =='" + Id1 + "'\r\n"
                  + "return a";

              logger.info("queryToBeExecuted----->" + query4);
              try {
                cursor1 = arangoDB.query(query4, HashMap.class);
                response3 = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while nodessemantics2_6 : " + e.getMessage().toString());
              }

              for (int k = 0; k < response3.size(); k++) {
                JSONObject mlcJson = new JSONObject(response3.get(k));
                if (mlcJson.has("name")) {
                  String recommendedName = mlcJson.get("name").toString();
                  semantics.put("name", recommendedName);
                }
              }

            }
          }
          if (mlJson.has("confidenceScore")) {
            String confidenceScore = mlJson.get("confidenceScore").toString();
            semanticJsonSuggested.put("confidenceScore", confidenceScore);
          }
          if (mlJson.has("Status")) {
            String Status = mlJson.get("Status").toString();
            semanticJsonSuggested.put("Status", Status);
          }
          if (mlJson.has("Source")) {
            String Source = mlJson.get("Source").toString();
            semanticJsonSuggested.put("Source", Source);
          }

          String query4 = "for a in ML_collection\r\n"
              + "filter a._id =='" + Id1 + "'\r\n"
              + "return a";

          logger.info("queryToBeExecuted----->" + query4);
          try {
            cursor1 = arangoDB.query(query4, HashMap.class);
            response3 = cursor1.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while nodessemantics2_7 : " + e.getMessage().toString());
          }

          for (int k = 0; k < response3.size(); k++) {
            JSONObject mlcJson = new JSONObject(response3.get(k));
            if (mlcJson.has("name")) {
              String recommendedName = mlcJson.get("name").toString();
              semanticJsonSuggested.put("name", recommendedName);
              suggestedName.put(semanticJsonSuggested.toMap());
            } else if (mlcJson.has("suggestedNames") && mlcJson.has("Status")) {
              logger.info("no values to insert");
            } else {
              JSONArray suggestName = mlcJson.getJSONArray("suggestedNames");
              for (int s = 0; s < suggestName.length(); s++) {
                JSONObject l = new JSONObject(suggestName.get(s).toString());
                logger.info(String.valueOf(l));
									/*String sname=l.get("name").toString();
									String confidenceScore=l.get("confidenceScore").toString();

									semanticJsonSuggested.put("recommendedName", sname);
									semanticJsonSuggested.put("confidenceScore", confidenceScore);
									suggestedName.put(semanticJsonSuggested.toMap());*/
                suggestedName.put(l.toMap());

              }
            }
            semantics.put("suggestedNames", suggestedName);
          }
        }
        responseList.add(semantics.toMap());
      }
      //}
    }
    return responseList;

  }


  public List<Object> nodesclassification1(String pinId) {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> businessResponse = new ArrayList<>();
    List<HashMap> businessResponse1 = new ArrayList<>();
    List<Object> responseList = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCursor<Object> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    String query = "for a in PinCollection\r\n"
        + "filter a._key == '" + pinId + "' AND a.pinNodes !=null\r\n"
        + "for b in a.pinNodes\r\n"
        + "filter b.type == \"Column\" \r\n"
        + "return b.arangoNodeKey";
    logger.info("queryToBeExecuted----->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodesclassification1 : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<>();
    for (int i = 0; i < response.size(); i++) {
      String[] node = ((String) response.get(i)).split("/");
      //columns.add("a._key == '"+node[1]+"'");
      columns.add("b.id == '" + node[1] + "'");
    }
    String columnIds = String.join(" OR ", columns);

    //			String query2="for a in PhysicalDataDictionary\r\n"
    //					+ "filter "+columnIds+"\r\n"
    //					+ "return a";

    String query2 = "for a in PhysicalDataDictionary\r\n"
        + "for b in a.nodes\r\n"
        + "filter " + columnIds + "\r\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + query2);
    try {
      cursor1 = arangoDB.query(query2, HashMap.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while nodesclassification1_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      JSONObject semantics = new JSONObject();
      JSONObject semanticJsonSuggested = new JSONObject();
      JSONArray suggestedName = new JSONArray();
      JSONObject physicalJson = new JSONObject(response1.get(i));
      String source = physicalJson.getString("source");
      //String Id=null;
      //String name=null;
      //	if(source.equals("PKWARE")) {
      String name = physicalJson.getString("name");
      String Id = physicalJson.getString("_id");

      semantics.put("columnName", name);

      String queryToBeExecute = "for a in buslnkphy\r\n"
          + "filter a._to=='" + Id + "'\r\n"
          + "return a";
      logger.info("queryToBeExecuted----->" + queryToBeExecute);
      try {
        cursor1 = arangoDB.query(queryToBeExecute, HashMap.class);
        businessResponse = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while nodesclassification1_3 : " + e.getMessage().toString());
      }
      for (int j = 0; j < businessResponse.size(); j++) {

        JSONObject s = new JSONObject(businessResponse.get(j));
        if (s.has("confidenceScore")) {
          String confidenceScore = s.get("confidenceScore").toString();
          semantics.put("confidenceScore", confidenceScore);
          semantics.put("Status", "Sourced");
        }
        String id = s.getString("_from");

        String querytoexecute = "for a in Business\r\n"
            + "filter a._id=='" + id + "'\r\n"
            + "return a";

        logger.info("queryToBeExecuted----->" + query);

        try {

          cursor1 = arangoDB.query(querytoexecute, HashMap.class);
          businessResponse1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while nodesclassification1_4: " + e.getMessage().toString());
        }

        for (int k = 0; k < businessResponse1.size(); k++) {
          JSONObject l = new JSONObject(response2.get(k));
          if (l.has("name")) {
            String recommendedName = l.get("name").toString();
            semanticJsonSuggested.put("name", recommendedName);
            suggestedName.put(semanticJsonSuggested.toMap());
          } else if (l.has("suggestedNames")) {
            //JSONArray suggestedName=l.getJSONArray("suggestedNames");
            //semantics.put("SuggestedNames", suggestedName);

            JSONArray suggetName = l.getJSONArray("suggestedNames");
            for (int sb = 0; sb < suggetName.length(); sb++) {
              JSONObject g = new JSONObject(suggetName.get(sb).toString());
              logger.info(String.valueOf(g));
              g.remove("columnGraphId");
              g.remove("updateDate");
              g.remove("hitCount");
              g.remove("attribMap");
              g.remove("valuesScanned");
              g.remove("graphId");
              g.remove("createDate");
								/*String sname=g.get("name").toString();
								String confidenceScore=g.get("confidenceScore").toString();
								semanticJsonSuggested.put("recommendedName", sname);
								semanticJsonSuggested.put("confidenceScore", confidenceScore);
								suggestedName.put(semanticJsonSuggested.toMap());*/
              suggestedName.put(g.toMap());
            }

          }
          semantics.put("suggestedNames", suggestedName);
        }
        responseList.add(semantics.toMap());
      }

      if (businessResponse.isEmpty()) {

        String query3 = "for a in mllinkphy\r\n"
            + "filter a._to == '" + Id + "'\r\n"
            + "return a";
        logger.info("queryToBeExecuted----->" + query3);
        try {
          cursor1 = arangoDB.query(query3, HashMap.class);
          response2 = cursor1.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while nodesclassification1_5 : " + e.getMessage().toString());
        }
        for (int j = 0; j < response2.size(); j++) {
          JSONObject mlJson = new JSONObject(response2.get(j));
          String Id1 = mlJson.getString("_from");

          if (mlJson.has("Status")) {
            String status = mlJson.get("Status").toString();
            if (status.contains("Accepted") || status.contains("accepted")) {

              if (mlJson.has("confidenceScore")) {
                String confidenceScore = mlJson.get("confidenceScore").toString();
                semanticJsonSuggested.put("confidenceScore", confidenceScore);
                semantics.put("confidenceScore", confidenceScore);
              }
              if (mlJson.has("Status")) {
                String Status = mlJson.get("Status").toString();
                semanticJsonSuggested.put("Status", Status);
                semantics.put("Status", Status);
              }
              if (mlJson.has("Source")) {
                String Source = mlJson.get("Source").toString();
                semanticJsonSuggested.put("Source", Source);
                semantics.put("Source", Source);
              }

              String query4 = "for a in ML_collection\r\n"
                  + "filter a._id =='" + Id1 + "'\r\n"
                  + "return a";

              logger.info("queryToBeExecuted----->" + query4);
              try {
                cursor1 = arangoDB.query(query4, HashMap.class);
                response3 = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while nodesclassification1_6 : " + e.getMessage().toString());
              }

              for (int k = 0; k < response3.size(); k++) {
                JSONObject mlcJson = new JSONObject(response3.get(k));
                if (mlcJson.has("name")) {
                  String recommendedName = mlcJson.get("name").toString();
                  semantics.put("name", recommendedName);
                }
              }

            }
          }

          if (mlJson.has("confidenceScore")) {
            String confidenceScore = mlJson.get("confidenceScore").toString();
            semanticJsonSuggested.put("confidenceScore", confidenceScore);
          }
          if (mlJson.has("Status")) {
            String Status = mlJson.get("Status").toString();
            semanticJsonSuggested.put("Status", Status);
          }
          if (mlJson.has("Source")) {
            String Source = mlJson.get("Source").toString();
            semanticJsonSuggested.put("Source", Source);
          }

          String query4 = "for a in ML_collection\r\n"
              + "filter a._id =='" + Id1 + "'\r\n"
              + "return a";

          logger.info("queryToBeExecuted----->" + query4);
          try {
            cursor1 = arangoDB.query(query4, HashMap.class);
            response3 = cursor1.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while nodesclassification1_7 : " + e.getMessage().toString());
          }

          for (int k = 0; k < response3.size(); k++) {
            JSONObject mlcJson = new JSONObject(response3.get(k));
            if (mlcJson.has("name")) {
              String recommendedName = mlcJson.get("name").toString();
              semanticJsonSuggested.put("name", recommendedName);
              suggestedName.put(semanticJsonSuggested.toMap());
            } else if (mlcJson.has("suggestedNames") && mlcJson.has("Status")) {
              logger.info("no values to insert");
            } else {
              JSONArray suggestName = mlcJson.getJSONArray("suggestedNames");
              for (int s = 0; s < suggestName.length(); s++) {
                JSONObject l = new JSONObject(suggestName.get(s).toString());
                logger.info(String.valueOf(l));
                l.remove("columnGraphId");
                l.remove("updateDate");
                l.remove("hitCount");
                l.remove("attribMap");
                l.remove("valuesScanned");
                l.remove("graphId");
                l.remove("createDate");
									/*String sname=l.get("name").toString();
									String confidenceScore=l.get("confidenceScore").toString();
									semanticJsonSuggested.put("recommendedName", sname);
									semanticJsonSuggested.put("confidenceScore", confidenceScore);
									suggestedName.put(semanticJsonSuggested.toMap());*/
                suggestedName.put(l.toMap());
              }
            }
            semantics.put("suggestedNames", suggestedName);
          }
        }
        responseList.add(semantics.toMap());
      }
      //}
    }
    return responseList;

  }


  public Object jsonFileToExcelFile(File srcFile, String targetFileExtension) {
    // TODO Auto-generated method stub
    return null;
  }


  public ArrayList<Object> addControls(List<String> nodes) {

    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < nodes.size(); i++) {
      String s = nodes.get(i);
      columns.add("node.id =='" + s + "'");
    }
    String columnIds = String.join(" OR ", columns);
    List<String> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in Nodes\r\n"
        + "filter " + columnIds + "\r\n"
        + "return node.relations.sources";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addControls : " + e.getMessage().toString());
    }

    response.forEach(a -> {
      JSONArray sources = new JSONArray(a);
      sources.forEach(b -> {
        JSONObject l = new JSONObject(b.toString());
        JSONObject source = l.getJSONObject("source");
        String type = source.getString("type");
        String id = source.getString("id");
        if (type.contains("Policy")) {
          columns1.add("node.id=='" + id + "'");
        }
      });

    });

    String columnIds1 = String.join(" OR ", columns1);

    String query1 = "For node in Nodes\r\n"
        + "filter " + columnIds1 + "\r\n"
        + "return node";

    logger.info("query--->" + query1);
    try {
      cursor1 = arangoDB.query(query1, HashMap.class);
      response1 = cursor1.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addControls_2 : " + e.getMessage().toString());
    }

    return connectArango.tailView(response1);


  }


  public ArrayList<Object> getMyRecommandedNodesList() {

    List<HashMap> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in Nodes\r\n"
        + "filter a.type.name == 'Data set' OR a.type.name == 'Data Product' OR a.type.name == 'Product'\r\n"
        + "LIMIT 10\r\n"
        + "return a";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getMyRecommandedNodesList : " + e.getMessage().toString());
    }
    return connectArango.tailView(response);


  }


  public List<HashMap> getNodeNameAndId() {

    List<HashMap> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in Nodes\r\n"
        + "return {displayName:a.displayName,id:a.id,type:a.type.name}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getNodeNameAndId : " + e.getMessage().toString());
    }

    return response;


  }


  public void addImageUrl(MultipartFile file, String userId) throws java.text.ParseException {

    List<HashMap> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    logger.info("fileNameAndPath" + file);
    // ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

    //source.setCreatedOn(utc.toInstant().toString());

    // Date date = format.parse(utc.toInstant());
    // long millis = utc.toEpochSecond()*1000;
    //toEpochSecond();

    String queryToBeExecuted = "for a in  registerUsers\r\n"
        + "filter a._key == '" + userId + "'\r\n"
        + "update a with {cover:'" + file.getOriginalFilename() + "'} in registerUsers";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while addImageUrl : " + e.getMessage().toString());
    }

  }


  public String getPImage(String userId) {

    List<String> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    //logger.info("fileNameAndPath"+file);
    String queryToBeExecuted = "for a in  registerUsers\r\n"
        + "filter a._key == '" + userId + "'\r\n"
        + "return a.cover";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPImage : " + e.getMessage().toString());
    }
    return response.get(0);

  }


  public List<String> favoriteNodesCount(String id) {

    List<String> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for a in ShoppingCart\r\n"
        + "filter a.favoriteNodes !=null\r\n"
        + "for b in a.favoriteNodes\r\n"
        + "filter b.arangoNodeKey == '" + id + "'\r\n"
        + "COLLECT WITH COUNT INTO count\r\n"
        + "return count";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<String> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while favoriteNodesCount : " + e.getMessage().toString());
    }

    return response;
  }


  public List<HashMap> gettingDataproducts(String type) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for a in Nodes\r\n"
        + "filter a.type.name == '" + type + "'\r\n"
        + "return {name:a.name,type:a.type.name,description:FIRST(a.attributes[* FILTER CURRENT.name == 'Description']).value,key:a._key}";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while gettingDataproducts : " + e.getMessage().toString());
    }

    return response;

  }


  public List<Object> displayPinCollections(String order, String pinFilter) {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = null;
    if (pinFilter.contains("lastmodifiedon")) {
      queryToBeExecuted = "for a in PinCollection\r\n"
          + "SORT a." + pinFilter + " " + order + "\r\n"  //ASC/DESC
          + "return a";
    } else if (pinFilter.contains("createdon")) {
      queryToBeExecuted = "for a in PinCollection\r\n"
          + "SORT a." + pinFilter + " " + order + "\r\n"  //ASC/DESC
          + "return a";
    } else {
      queryToBeExecuted = "for a in PinCollection\r\n"
          + "SORT a." + pinFilter + " " + order + "\r\n"  //ASC/DESC
          + "return a";
    }
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while displayPinCollections : " + e.getMessage().toString());
    }
    return response;
  }


  public List<Object> displayPinCollectionDropdown() {
    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "return [\"createdon\",\"lastmodifiedon\",\"displayName\"]";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while displayPinCollectionDropdown : " + e.getMessage().toString());
    }
    return response;
  }


  public List<Object> displaySortedNodes(String order, String nodeFilter) {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = null;
    if (nodeFilter.contains("displayName")) {
      queryToBeExecuted = "for a in sortingCollection\r\n"
          + "filter a.DisplayName\r\n"
          + "SORT a.DisplayName " + order + "\r\n"
          + "return a";
    } else if (nodeFilter.contains("createdOn")) {
      queryToBeExecuted = "for a in sortingCollection\r\n"
          + "filter a.createdOn\r\n"
          + "SORT a.createdOn " + order + "\r\n"
          + "return a";
    } else if (nodeFilter.contains("lastModifiedOn")) {
      queryToBeExecuted = "for a in sortingCollection\r\n"
          + "filter a.LastModifiedOn\r\n"
          + "SORT a.LastModifiedOn " + order + "\r\n"
          + "return a";
    } else {
      queryToBeExecuted = "for a in sortingCollection\r\n"
          + "filter a.AvgRating\r\n"
          + "SORT a.AvgRating " + order + "\r\n"
          + "return a";
    }
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while displaySortedNodes : " + e.getMessage().toString());
    }
    return response;
  }

  public List<Object> addCurateContextInfo(String key, HashMap value) {

    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String contextCategory = (String) value.get("contextCategory");
    String productImportance = (String) value.get("productImportance");
    String estimatedDataVolume = (String) value.get("dataVolume");
    String dataType = (String) value.get("dataType");
    String refreshFrequency = (String) value.get("refreshFrequency");
    String query = "for doc in Nodes\r\n"
        + "filter doc._key=='" + key + "'\r\n"
        + "UPDATE doc WITH { attributes: APPEND(doc.attributes,[{'name':'What context category best describes your product','value':'"
        + contextCategory + "'},{'name':'How this product important to your business','value':'"
        + productImportance + "'},{'name':'Estimated data volume','value':'"
        + estimatedDataVolume + "'},{'name':'Data type','value':'" + dataType
        + "'},{'name':'Refresh frequency','value':'" + refreshFrequency
        + "'}],true) } IN Nodes";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addCurateContextInfo : " + e.getMessage().toString());
    }
    return response;
  }


  public List<Object> addCurateSecurityInfo(String key, HashMap value) {

    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String accessRules = (String) value.get("accessRules");
    String retentionPeriod = (String) value.get("retentionPeriod");
    String encryption = (String) value.get("encryption");
    String dataBackupFrequency = (String) value.get("dataBackupFrequency");
    String dataAccessRestrictions = (String) value.get("dataAccessRestrictions");
    String query = "for doc in Nodes\r\n"
        + "filter doc._key=='" + key + "'\r\n"
        + "UPDATE doc WITH { attributes: APPEND(doc.attributes,[{'name':'Access rules','value':'"
        + accessRules + "'},{'name':'Retention period','value':'" + retentionPeriod
        + "'},{'name':'Encryption','value':'" + encryption
        + "'},{'name':'Data backup frequency','value':'" + dataBackupFrequency
        + "'},{'name':'Data access restrictions','value':'" + dataAccessRestrictions
        + "'}],true) } IN Nodes";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addCurateSecurityInfo : " + e.getMessage().toString());
    }

    return response;

  }


  public List<Object> addCuratePrivacyInfo(String key, HashMap value) {

    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String securityClassification = (String) value.get("securityClassification");
    String retentionPeriod = (String) value.get("privacyRetentionPeriod");
    String inforedToxicCombination = (String) value.get("inforedToxicCombination");
    String pII = (String) value.get("pII");

    String query = "for doc in Nodes\r\n"
        + "filter doc._key=='" + key + "'\r\n"
        + "UPDATE doc WITH { attributes: APPEND(doc.attributes,[{'name':'Security classification','value':'"
        + securityClassification + "'},{'name':'Infored toxic combination','value':'"
        + inforedToxicCombination + "'},{'name':'Privacy Retention period','value':'"
        + retentionPeriod + "'},{'name':'PII','value':'" + pII + "'}],true) } IN Nodes";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addCuratePrivacyInfo : " + e.getMessage().toString());
    }

    return response;
  }


  public List<Object> addServiceLevelObjectiveInfo(String key, List<HashMap> objValue) {

    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    /*String objective = (String) value.get("chooseObjective");
    String confirmity = (String) value.get("confirmity");
    String consistency = (String) value.get("consistency");
    String completeness = (String) value.get("completeness");
    String riskLevel = (String) value.get("riskLevel");
    String integrity = (String) value.get("integrity");
    String timelyness = (String) value.get("timelyness");
    String validity = (String) value.get("validity");*/
    for (int i = 0; i < objValue.size(); i++) {
      JSONObject obj = new JSONObject(objValue.get(i));
      System.out.println(obj);

      String objective = (String) obj.get("name");
      String objTypeValue = (String) obj.get("valueType");
      //String objVal = (String) obj.get("value");
      int objVal = obj.getInt("value");

      String query = "for doc in Nodes\r\n"
          + "filter doc._key=='" + key + "'\r\n"
          + "UPDATE doc WITH { attributes: APPEND(doc.attributes,[{'name':'" + objective
          + "','value':'"
          + objVal + "','valueType':'" + objTypeValue + "'}],true) } IN Nodes";
      logger.info("query--->" + query);
      try {
        cursor = arangoDB.query(query, Object.class);
        response = cursor.asListRemaining();
        logger.info("response" + response);
      } catch (Exception e) {
        log.error(
            "Exception while addServiceLevelObjectiveInfo : " + e.getMessage().toString());
      }
    }
    return response;
  }

  public HashMap getCurateContextInfo(String key) {
    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    HashMap curate = new HashMap();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in Nodes\n"
        + "filter doc._key == '" + key + "'\n"
        + "for attr in doc.attributes\n"
        + "filter attr.name == \"What content category best describes your product\" || attr.name ==\"How this product is important to your business\" || attr.name == \"Estimated data volume\" || attr.name == \"Data type\" || attr.name == \"Refresh frequency\"\n"
        + "return attr";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getCurateContextInfo : " + e.getMessage().toString());
    }
    for (int i = 0; i < response.size(); i++) {
      JSONObject privacy = new JSONObject(response.get(i));
      System.out.println("privacy" + privacy);

      if (privacy.get("name").equals("What content category best describes your product")) {
        String value = privacy.getString("value");
        curate.put("whatContentCategoryBestDescribesYourProduct", value);
      } else if (privacy.get("name").equals("How this product is important to your business")) {
        String value = privacy.getString("value");
        curate.put("howThisProductIsImportantToYourBusiness", value);
      } else if (privacy.get("name").equals("Estimated data volume")) {
        String value = privacy.getString("value");
        curate.put("estimatedDataVolume", value);
      } else if (privacy.get("name").equals("Data type")) {
        String value = privacy.getString("value");
        curate.put("dataType", value);
      } else if (privacy.get("name").equals("Refresh frequency")) {
        String value = privacy.getString("value");
        curate.put("refreshFrequency", value);
      }
//    	if(privacy.has("name")) {
//    		String name=privacy.getString("name");
//    		String value=privacy.getString("value");
//    		curate.put(name,value);
//    	}
    }

    return curate;
  }

  public HashMap getCurateSecurityInfo(String key) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    HashMap security = new HashMap();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for doc in Nodes\n"
        + "filter doc._key == '" + key + "'\n"
        + "for attr in doc.attributes\n"
        + "filter attr.name == \"Access rules\" || attr.name ==\"Retention period\" || attr.name == \"Encryption\" || attr.name == \"Data backup frequency\" || attr.name == \"Data access restrictions\"\n"
        + "return attr";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getCurateSecurityInfo : " + e.getMessage().toString());
    }

    for (int i = 0; i < response.size(); i++) {
      JSONObject privacy = new JSONObject(response.get(i));
      System.out.println("privacy" + privacy);

      if (privacy.get("name").equals("Access rules")) {
        String value = privacy.getString("value");
        security.put("accessRules", value);
      } else if (privacy.get("name").equals("Retention period")) {
        String value = privacy.getString("value");
        security.put("retentionPeriod", value);
      } else if (privacy.get("name").equals("Encryption")) {
        String value = privacy.getString("value");
        security.put("encryption", value);
      } else if (privacy.get("name").equals("Data backup frequency")) {
        String value = privacy.getString("value");
        security.put("dataBackupFrequency", value);
      } else if (privacy.get("name").equals("Data access restrictions")) {
        String value = privacy.getString("value");
        security.put("dataAccessRestrictions", value);
      }
//    	if(privacy.has("name")) {
//    		String name=privacy.getString("name");
//    		String value=privacy.getString("value");
//    		security.put(name,value);
//    	}
    }

    return security;
  }


  public HashMap getCuratePrivacyInfo(String key) {

    List<HashMap> response = new ArrayList<>();

    HashMap curatePrivacy = new HashMap();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in Nodes\n"
        + "filter doc._key == '" + key + "'\n"
        + "for attr in doc.attributes\n"
        + "filter attr.name == \"Security Classification\" || attr.name ==\"Infored toxic Combination\" || attr.name == \"Privacy retention period\" || attr.name == \"PII\"\n"
        + "return attr";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getCuratePrivacyInfo : " + e.getMessage().toString());
    }

    for (int i = 0; i < response.size(); i++) {
      JSONObject privacy = new JSONObject(response.get(i));
      System.out.println("privacy" + privacy);

      if (privacy.get("name").equals("Security Classification")) {
        String value = privacy.getString("value");
        curatePrivacy.put("securityClassification", value);
      } else if (privacy.get("name").equals("Infored toxic Combination")) {
        String value = privacy.getString("value");
        curatePrivacy.put("inforedToxicCombination", value);
      } else if (privacy.get("name").equals("Privacy retention period")) {
        String value = privacy.getString("value");
        curatePrivacy.put("privacyRetentionPeriod", value);
      } else if (privacy.get("name").equals("PII")) {
        String value = privacy.getString("value");
        curatePrivacy.put("pII", value);
      }
//    	if(privacy.has("name")) {
//    		String name=privacy.getString("name");
//    		String value=privacy.getString("value");
//    		curatePrivacy.put(name,value);
//    	}
    }

    return curatePrivacy;
  }


  public List<HashMap> getServiceLevelObjectiveInfo(String key) {
    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();
    HashMap serviceLevelObjective = new HashMap();
    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in Nodes\n"
        + "filter doc._key == '" + key + "'\n"
        + "for attr in doc.attributes\n"
        + "return attr";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getServiceLevelObjectiveInfo : " + e.getMessage().toString());
    }
    System.out.println(response);
    for (int i = 0; i < response.size(); i++) {
      HashMap sr = new HashMap(response.get(i));
      System.out.println(sr);
      if (sr.containsKey("valueType")) {
        response1.add(sr);
    	/*String name=sr.getString("name");
        String value = sr.getString("value");
        String valueType=sr.getString("valueType");
        serviceLevelObjective.put("name", name);
        serviceLevelObjective.put("value", value);
        serviceLevelObjective.put("valueType", valueType);*/

      }
      //response1.add(serviceLevelObjective);
      //serviceLevelObjective.clear();
    }
    return response1;
  }


  public List<Object> getselectFields(String key) {
    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in " + arangoNodeTypesCollection + "\r\n"
        + "filter doc." + key + "\r\n"
        + "return unique(doc." + key + ")";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getselectFields : " + e.getMessage().toString());
    }

    return response;
  }


  public List<Object> addDataSetEntry(String key, HashMap value) {

    List<Object> response = new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String dataEntryForm = (String) value.get("dataEntryForm");
    String type = (String) value.get("type");
    String frequency = (String) value.get("frequency");
    String offeredAs = (String) value.get("offeredAs");

    String query = "for doc in Nodes\r\n"
        + "filter doc._key=='" + key + "'\r\n"
        + "UPDATE doc WITH { attributes: APPEND(doc.attributes,[{'name':'Data entry form','value':'"
        + dataEntryForm + "'},{'name':'Type','value':'" + type
        + "'},{'name':'Frequency','value':'" + frequency + "'},{'name':'Offered as','value':'"
        + offeredAs + "'}],true) } IN Nodes";
    System.out.println("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      System.out.println("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addDataSetEntry : " + e.getMessage().toString());
    }

    return response;

  }


  public HashMap getDataSetDataEntryRegistration(String key) {

    List<HashMap> response = new ArrayList<>();

    HashMap dataEntry = new HashMap();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in Nodes\n"
        + "filter doc._key == '" + key + "'\n"
        + "for attr in doc.attributes\n"
        + "filter attr.name == \"Encryption\" || attr.name ==\"Type\" || attr.name == \"Offered as\" || attr.name == \"Frequency\"\n"
        + "return attr";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getCuratePrivacyInfo : " + e.getMessage().toString());
    }

    for (int i = 0; i < response.size(); i++) {
      JSONObject privacy = new JSONObject(response.get(i));
      System.out.println("privacy" + privacy);

      if (privacy.get("name").equals("Encryption")) {
        String value = privacy.getString("value");
        dataEntry.put("encryption", value);
      } else if (privacy.get("name").equals("Type")) {
        String value = privacy.getString("value");
        dataEntry.put("type", value);
      } else if (privacy.get("name").equals("Offered as")) {
        String value = privacy.getString("value");
        dataEntry.put("offeredAs", value);
      } else if (privacy.get("name").equals("Frequency")) {
        String value = privacy.getString("value");
        dataEntry.put("frequency", value);
      }
//	    	if(privacy.has("name")) {
//	    		String name=privacy.getString("name");
//	    		String value=privacy.getString("value");
//	    		curatePrivacy.put(name,value);
//	    	}
    }

    return dataEntry;
  }


  public List<HashMap> addAttributes(String key, List<HashMap> attributedetails) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    for (int i = 0; i < attributedetails.size(); i++) {
      JSONObject attr = new JSONObject(attributedetails.get(i));
      System.out.println(attr);
      String attributeName = attr.getString("attributeName");
      String attributeValue = attr.getString("attributeValue");

      String query = "for doc in Nodes\r\n"
          + "filter doc._key=='" + key + "'\r\n"
          + "UPDATE doc WITH { attributes: push(doc.attributes,{name:'" + attributeName
          + "',value:'" + attributeValue + "'},true) } IN Nodes";
      System.out.println("query--->" + query);
      try {
        cursor = arangoDB.query(query, HashMap.class);
        response = cursor.asListRemaining();
        System.out.println("response" + response);
      } catch (Exception e) {
        log.error(
            "Exception while addAttributes : " + e.getMessage().toString());
      }

    }

    return response;


  }

  public Object graphSearchQuery(String nodeName) {
    List<Object> response = new ArrayList<>();
    List<Object> res1 = new ArrayList<>();
    List<Object> res2 = new ArrayList<>();
    List<Object> res3 = new ArrayList<>();
    List<Object> res4 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    JSONObject graphResponse = new JSONObject();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String qry = "for doc in graphView\r\n"
        + "SEARCH ANALYZER(STARTS_WITH(doc.name,TOKENS('" + nodeName
        + "',\"text_en\")), \"text_en\") OR NGRAM_MATCH(doc.name,'" + nodeName
        + "',0.4,'fuzzy_search_bigram') OR LEVENSHTEIN_MATCH(doc.name,TOKENS('" + nodeName
        + "', \"text_en_no_stem\")[0],2,false)\r\n"
        + "let meta1=(For v,e,p in 1..2 ANY doc._id\r\n"
        + "Graph metaversgph1 \r\n"
        + "return v._id\r\n"
        + ")\r\n"
        + "let metaversgrp2=(\r\n"
        + "for m in meta1\r\n"
        + "For v,e,p in 1..2 ANY m\r\n"
        + "Graph metaversgph1\r\n"
        + "filter v.typeName == \"Data Set\" || v.typeName == \"Data Product\"\r\n"
        + "return v)\r\n"
        + "for m in metaversgrp2\r\n"
        + "return DISTINCT m";

    logger.info("queryToBeExecuted----->" + qry);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(qry, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphQuery : " + e.getMessage().toString());
    }

    return response;
  }

  public String addProductNodes(HashMap details) {
    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<String> presponse = new ArrayList<>();
    List<Object> presponse1 = new ArrayList<>();
    List<HashMap> userDetails = new ArrayList<>();
    JSONObject counterObj = new JSONObject();
    ArangoCursor<HashMap> cursor = null;
    String productUUID = UUID.randomUUID().toString();
    JSONObject key = new JSONObject();
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);
      if (arangodb != null) {
        System.out.println("details" + details);
        String userId = details.get("userId").toString();
        String nodeName = details.get("nodeName").toString();
        String type = details.get("type").toString();
        String description = details.get("description").toString();
        String productId = details.get("nodeId").toString();
        List<String> tags = (List<String>) details.get("tags");

        String Id = "Nodes/" + productId;
        for (int i = 0; i < tags.size(); i++) {
          String Tag = tags.get(i);
          String queryToExecute = "INSERT {Tag:'" + Tag + "',_key:'" + Tag + "'} in "
              + tagsCollection + "\r\n" + "return NEW";
          logger.info("queryToBeExecuted----->" + queryToExecute);
          ArangoCursor<HashMap> tagcursor = null;
          try {
            tagcursor = arangodb.query(queryToExecute, HashMap.class);
            response = cursor.asListRemaining();
          } catch (Exception e) {
            log.info("Exception while getTags : " + e.getMessage().toString());
          }
          logger.info("response" + response);
          if (response.isEmpty()) {

            ArangoCursor<HashMap> cursor1 = null;
            // List<HashMap> response2 = new ArrayList<>();
            String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n"
                + "filter doc._key=='" + Tag + "'\r\n" + "INSERT {_from: doc._id, _to: '" + Id
                + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges + "";
            logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
            try {
              cursor1 = arangodb.query(queryToBeExecuted1, HashMap.class);
              response2 = cursor1.asListRemaining();
            } catch (Exception e) {
              log.error("Exception while getTags_2" + e.getMessage().toString());
            }

          } else {
            response.forEach(a -> {
              JSONObject s = new JSONObject(a);
              String keys = s.getString("_key");
              String tagsl = s.getString("Tag");
              String ids = s.getString("_id");

              ArangoCursor<HashMap> cursor1 = null;
              List<HashMap> responset = new ArrayList<>();

              String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n"
                  + "filter doc._key=='" + keys + "'\r\n" + "INSERT {_from:'" + ids + "', _to: '"
                  + Id + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges
                  + "";
              logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
              try {
                cursor1 = arangodb.query(queryToBeExecuted1, HashMap.class);
                responset = cursor1.asListRemaining();
              } catch (Exception e) {
                log.error("Exception while getTags_3 : " + e.getMessage().toString());
              }
            });
          }

        }

        String userQuery = "for a in registerUsers\r\n"
            + "filter a._key == '" + userId + "'\r\n"
            + "return {FirstName:a.FirstName,LastName:a.LastName}";
        logger.info("query--->" + userQuery);
        try {
          cursor = arangodb.query(userQuery, HashMap.class);
          response = cursor.asListRemaining();
          logger.info("response" + response);
        } catch (Exception e) {
          log.error(
              "Exception while Creating Data Product Results: "
                  + "addNodes(String nodeName, String type, String userId,String description)"
                  + e.getMessage().toString());
        }
        String firstName = null;
        String lastName = null;
        String fullName = null;
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

        //System.out.println("DATETIME = " + utc.toInstant());
        //Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String datestr = f.format(new Date());
        //source.setCreatedOn(utc.toInstant().toString());
        for (int i = 0; i < response.size(); i++) {
          JSONObject user = new JSONObject(response.get(i));
          firstName = user.getString("FirstName");
          lastName = user.getString("LastName");
          fullName = firstName.concat(" " + lastName);
        }
        String query = null;
        if (description == null) {
          query = "for doc in Nodes\r\n"
              + "filter doc._key == '" + productId + "'\r\n"
              + "update doc._key with {\"_key\":'" + productId + "',\"id\":'" + productId
              + "',\"displayName\":'" + nodeName + "',\"name\":'" + nodeName + "',\"identifier\":'"
              + nodeName + "',\"sourceCatalog\":\"Datasouk\",\"type\": {\r\n"
              + "    \"metaCollectionName\": \"PhysicalDataDictionary\",\r\n"
              + "    \"name\": '" + type + "',\r\n"
              + "    \"id\": \"00000000-0000-0000-0000-000000031008\"\r\n"
              + "  },\"attributes\":[{\"name\":\"LastModifiedOn\",\"value\":'" + utc.toInstant()
              .toString()
              + "'}],\"relations\":{\"sources\":[],\"targets\":[]},\"responsibilities\": [],\"createdByUserName\": '"
              + firstName + "',\"createdByFullName\": '" + fullName + "',\"createdOn\": '"
              + utc.toInstant().toString()
              + "',\"articulationScore\": \"0.0\",\"ratingsCount\": \"0\",\r\n"
              + "  \"avgRating\": \"0.0\", } into Nodes";
        } else {
          query = "for doc in Nodes\r\n"
              + "filter doc._key == '" + productId + "'\r\n"
              + "update doc._key with {\"_key\":'" + productId + "',\"id\":'" + productId
              + "',\"displayName\":'" + nodeName + "',\"name\":'" + nodeName + "',\"identifier\":'"
              + nodeName + "',\"sourceCatalog\":\"Datasouk\",\"type\": {\r\n"
              + "    \"metaCollectionName\": \"PhysicalDataDictionary\",\r\n"
              + "    \"name\": '" + type + "',\r\n"
              + "    \"id\": \"00000000-0000-0000-0000-000000031008\"\r\n"
              + "  },\"attributes\":[{\"name\":\"Description\",\"value\":'" + description
              + "'},{\"name\":\"LastModifiedOn\",\"value\":'" + utc.toInstant().toString()
              + "'}],\"relations\":{\"sources\":[],\"targets\":[]},\"responsibilities\": [],\"createdByUserName\": '"
              + firstName + "',\"createdByFullName\": '" + fullName + "',\"createdOn\": '"
              + utc.toInstant().toString()
              + "',\"articulationScore\": \"0.0\",\"ratingsCount\": \"0\",\r\n"
              + "  \"avgRating\": \"0.0\", } into Nodes";
        }

        logger.info("query--->" + query);
        try {
          cursor = arangodb.query(query, HashMap.class);
          response = cursor.asListRemaining();
          logger.info("response" + response);
        } catch (Exception e) {
          log.error(
              "Exception while retrieving Data in Search Results: " + e.getMessage().toString());
        }
        key.put("key", productId);

        // String node = details.get("nodeId").toString();
        String nodeId = "Nodes/" + productId;
        ArangoCursor<Object> cursorp = null;
        String queryToBeExecuted = "for a in " + profileCategories + "\n"
            + "filter a._from == '" + nodeId + "'\n"
            + "return a";
        System.out.println(queryToBeExecuted);

        try {
          cursorp = arangodb.query(queryToBeExecuted, Object.class);
          response = cursor.asListRemaining();
          System.out.println(response);
        } catch (Exception e) {
          log.error("Exception while addNodesCategoriesList : " + e.getMessage().toString());
        }
        if (response.isEmpty()) {

          List<HashMap> categories = (List<HashMap>) details.get("categories");

          for (int i = 0; i < categories.size(); i++) {
            JSONObject catgories = new JSONObject(categories.get(i));
            String context = catgories.getString("context");
            JSONArray business = catgories.getJSONArray("business");
            JSONArray dataDomain = catgories.getJSONArray("dataDomain");
            // teamdetails.get("dataDomain").toString();
            // .getJSONArray("dataDomain");
            JSONArray geography = catgories.getJSONArray("geography");
            JSONArray product = catgories.getJSONArray("products");
            List<Object> contexts = new ArrayList<Object>();

            business.forEach(b -> {
              JSONObject busines = new JSONObject(b.toString());
              System.out.println("business" + busines);
              String id = busines.get("key").toString();
              JSONObject busiObject = new JSONObject();
              busiObject.put("id", id);
              contexts.add(busiObject);
            });

            dataDomain.forEach(d -> {
              JSONObject domain = new JSONObject(d.toString());
              String id = domain.getString("key");
              JSONObject dataDomainObject = new JSONObject();
              dataDomainObject.put("id", id);
              contexts.add(dataDomainObject);
            });

            // String datadomainId=dataDomain.getString("key");
            // dataDomainObject.put("id", datadomainId);

            geography.forEach(g -> {
              JSONObject geograph = new JSONObject(g.toString());
              String id = geograph.getString("key");
              JSONObject geographyObject = new JSONObject();
              geographyObject.put("id", id);
              contexts.add(geographyObject);
            });

            product.forEach(p -> {
              JSONObject prodct = new JSONObject(p.toString());
              String id = prodct.getString("key");
              JSONObject productObject = new JSONObject();
              productObject.put("id", id);
              contexts.add(productObject);
            });

            int contextCounter = 0;
            int contextCounter1 = contextCounter + 1;
            ArangoCursor<Object> cursor1 = null;
            String queryToBeExecuted1 =
                "insert {_from:'" + nodeId + "',_to:'" + nodeId + "'," + context + ":" + contexts
                    + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
                    + ",contextCounter:" + contextCounter1 + "} In " + profileCategories + "\r\n";
            System.out.println(queryToBeExecuted1);

            try {
              cursor1 = arangodb.query(queryToBeExecuted1, Object.class);
              response1 = cursor1.asListRemaining();
              System.out.println(response1);
            } catch (Exception e) {
              log.error("Exception while addNodesCategoriesList_2 : " + e.getMessage().toString());
            }

          }
        } else {
        }
        arangoConn.shutdown();
      }
    }
    return key.toString();
  }

  public List<Object> productNodes(String key) {

    List<String> response1 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<Object> tresponse = new ArrayList<>();
    List<Object> presponse2 = new ArrayList<>();
    JSONObject preferences = new JSONObject();
    List<Object> usr = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRegistration);

    List<Object> response = new ArrayList<>();
    List<String> response2 = new ArrayList<>();

    ArangoCursor<String> cursor2 = null;
    String id = "Nodes/" + key;
    String queryToBeExecuted1 = "for a in " + profileCategories + "\r\n" + "filter a._from == '"
        + id + "'\r\n" + "return a";
    System.out.println("queryToBeExecuted1----->" + queryToBeExecuted1);

    try {

      cursor2 = arangoDB.query(queryToBeExecuted1, String.class);
      response2 = cursor2.asListRemaining();
      System.out.println(response2);

    } catch (Exception e) {
      log.error("Exception while nodesCategories : " + e.getMessage().toString());
    }

    List<String> contextName = new ArrayList<>();
    HashSet<String> contextName2 = new HashSet<String>();
    List<String> contextColumns = new ArrayList<>();
    // response4.clear();
    for (int i = 0; i < response2.size(); i++) {

      JSONObject s = new JSONObject(response2.get(i));
      contextName.clear();
      System.out.println("s" + s);
      Set<String> keys = s.keySet();
      Object[] namesArray = keys.toArray();
      for (int j = 0; j < namesArray.length; j++) {
        System.out.println(j + ": " + namesArray[j]);
        contextName.add(namesArray[j].toString());
      }

      for (int x = 0; x < contextName.size(); x++) {
        if (contextName.get(x).contains("lastmodifiedon") || contextName.get(x).contains("_from")
            || contextName.get(x).contains("createdby") || contextName.get(x).contains("_rev")
            || contextName.get(x).contains("lastmodifiedby") || contextName.get(x).contains("_id")
            || contextName.get(x).contains("_to") || contextName.get(x).contains("_key")
            || contextName.get(x).contains("createdon") || contextName.get(x).contains("users")
            || contextName.get(x).contains("contextCounter")) {

        } else {
          contextName2.add(contextName.get(x));
        }
      }
    }
    Set<String> setWithUniqueValues = new HashSet<>(contextName2);
    System.out.println("setWithUniqueValues" + setWithUniqueValues);
    List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
    System.out.println("listWithUniqueContextValues" + listWithUniqueContextValues);

    for (int k = 0; k < listWithUniqueContextValues.size(); k++) {
      String contName = listWithUniqueContextValues.get(k);

      ArangoCursor<String> cursor3 = null;
      String query =
          "for a in " + profileCategories + "\r\n" + "filter a._from == '" + id + "' AND  a."
              + listWithUniqueContextValues.get(k) + " !=null \r\n" + "return a";
      System.out.println("query----->" + query);

      try {

        cursor3 = arangoDB.query(query, String.class);
        response2 = cursor3.asListRemaining();
        System.out.println(response2);

      } catch (Exception e) {
        log.error("Exception while nodesCategories_2 : " + e.getMessage().toString());
      }
      JSONObject teamDetails = new JSONObject();
      ArrayList<Object> response4 = new ArrayList<>();
      JSONObject teamDetail1 = new JSONObject();
      JSONObject teamDetail3 = new JSONObject();
      response2.forEach(a -> {
        // response4.clear();
        JSONObject contxt = new JSONObject(a);
        System.out.println("contxt" + contxt);
        String to = contxt.getString("_to");
        List<String> columns3 = new ArrayList<String>();

        // JSONArray users=contxt.getJSONArray("users");
        String[] Id = to.split("/");
        String nodetypes = Id[0];
        String name = Id[1];
        System.out.println("name" + name);

        JSONArray context = contxt.getJSONArray(contName);
        teamDetails.put("context", contName);
        context.forEach(z -> {
          JSONObject categoryId = new JSONObject(z.toString());
          String cids = categoryId.getString("id");
          columns3.add("a._key == '" + cids + "'");
        });
        List<Object> Business = new ArrayList<>();
        List<Object> DataDomain = new ArrayList<>();
        List<Object> Geography = new ArrayList<>();
        List<Object> Products = new ArrayList<>();
        List<HashMap> response5 = new ArrayList<>();
        String columnIds3 = String.join(" OR ", columns3);
        ArangoCursor<HashMap> cursor4 = null;
        String queryToBeExecuted4 = "for a in Business\r\n" + "filter " + columnIds3
            + "\r\n" + "return {name:a.name,type:a.typeName,id:a._key}";

        System.out.println("queryToBeExecuted----->" + queryToBeExecuted4);

        try {

          cursor4 = arangoDB.query(queryToBeExecuted4, HashMap.class);
          response5 = cursor4.asListRemaining();
          System.out.println(response5);

        } catch (Exception e) {
          log.error("Exception while nodesCategories_3 : " + e.getMessage().toString());
        }
        columns3.clear();
        DataDomain.clear();
        Products.clear();
        Geography.clear();
        Business.clear();
        response5.forEach(ca -> {
          JSONObject categoryObject = new JSONObject();
          JSONObject cate = new JSONObject(ca);
          String type = cate.getString("type");
          String cname = cate.getString("name");
          String ids = cate.getString("id");
          if (type.equals("Data Domain")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", ids);
            DataDomain.add(categoryObject);
          } else if (type.equals("Product")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", ids);
            Products.add(categoryObject);
          } else if (type.equals("Region")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", ids);
            Geography.add(categoryObject);
          } else if (type.equals("Line of Business")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", ids);
            Business.add(categoryObject);
          }
        });
        if (!DataDomain.isEmpty()) {
          teamDetails.put("dataDomain", DataDomain);
        }
        if (!Products.isEmpty()) {
          teamDetails.put("products", Products);
        }
        if (!Geography.isEmpty()) {
          teamDetails.put("geography", Geography);
        }
        if (!Business.isEmpty()) {
          teamDetails.put("business", Business);
        }
      });
      usr.add(teamDetails);
    }

    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    String query = "for node in " + tagsEdges + "\r\n" + "filter node._to=='Nodes/" + key
        + "'\r\n" + "return node._from";

    logger.info("queryToBeExecuted----->" + query);

    ArangoCursor<Object> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query, Object.class);
      tresponse = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionsdetails : " + e.getMessage().toString());
    }

    for (int i = 0; i < tresponse.size(); i++) {
      columns.add("node._id == '" + tresponse.get(i) + "'");
    }

    String columnIds = String.join(" OR ", columns);

    String query1 = "for node in " + tagsCollection + "\r\n" + "filter " + columnIds + "\r\n"
        + "return node.Tag";
    logger.info("queryToBeExecuted----->" + query1);

    ArangoCursor<Object> pcursor = null;
    try {

      pcursor = arangoDB.query(query1, Object.class);
      presponse2 = pcursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionsdetails_2 : " + e.getMessage().toString());
    }

    for (int j = 0; j < presponse2.size(); j++) {
      columns1.add("" + presponse2.get(j) + "");
    }

    String queryT = "for node in Nodes\r\n"
        + "filter node.id == '" + key + "'\r\n"
        + "for c in node.attributes\r\n"
        + "filter c.name == \"Description\" \r\n"
        + "return {name:node.name,description:c.value}";
    System.out.println("queryT----->" + queryT);

    try {

      cursor2 = arangoDB.query(queryT, String.class);
      response2 = cursor2.asListRemaining();
      System.out.println(response2);

    } catch (Exception e) {
      log.error("Exception while nodesCategories : " + e.getMessage().toString());
    }

    String name = null;
    String description = null;
    for (int i = 0; i < response2.size(); i++) {
      JSONObject s = new JSONObject(response2.get(i));
      System.out.println(s);
      name = s.getString("name");
      description = s.getString("description");
    }

    preferences.put("categories", usr);
    usr.clear();
    preferences.put("tags", columns1);
    preferences.put("name", name);
    preferences.put("description", description);
    response3.add(preferences.toMap());
    // HashMap s = new HashMap();

    // response3.add(s);
    return response3;

  }

  public List<HashMap> addingObjectives(List<HashMap> objectDetails) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    for (int i = 0; i < objectDetails.size(); i++) {
      JSONObject obj = new JSONObject(objectDetails.get(i));
      System.out.println(obj);
      String objName = obj.getString("name");
      String objValue = obj.getString("valueType");

//	      String query = "for doc in Nodes\r\n"
//	          + "filter doc._key=='" + key + "'\r\n"
//	          + "UPDATE doc WITH { attributes: push(doc.attributes,{name:'" + objName
//	          + "',value:'" + objValue + "'},true) } IN Nodes";

      String query = "for doc in NodeTypes\r\n"
          + "filter doc._key=='5523359'\r\n"
          + "UPDATE doc WITH {chooseObjective:push(doc.chooseObjective,{name:'" + objName
          + "',valueType:'" + objValue + "'},true)} IN NodeTypes";
      System.out.println("query--->" + query);
      try {
        cursor = arangoDB.query(query, HashMap.class);
        response = cursor.asListRemaining();
        System.out.println("response" + response);
      } catch (Exception e) {
        log.error(
            "Exception while addAttributes : " + e.getMessage().toString());
      }

    }

    return response;
  }

  public List<HashMap> gettingObjectives() {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();
    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for doc in NodeTypes\r\n"
        + "filter doc._key=='5523359'\r\n"
        + "for obj in doc.chooseObjective\r\n"
        + "return obj";
    System.out.println("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      System.out.println("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while addAttributes : " + e.getMessage().toString());
    }
    return response;
  }

  public List<HashMap> updatePublishNode(String key) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();
    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for doc in Nodes\r\n"
            + "filter doc._key=='"+key+"'\r\n"
            + "update doc with {\"nodesClassification\":\"public\"} in Nodes";
    System.out.println("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      System.out.println("response" + response);
    } catch (Exception e) {
      log.error(
              "Exception while addAttributes : " + e.getMessage().toString());
    }
    return response;
  }

  public List<Object> getMyTeams(String loginid) {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> myteams=new ArrayList<>();
    ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();
    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "for usr in userRoles\n" +
            "for usrId in usr.users\n" +
            "filter usrId.id == '"+loginid+"'\n" +
            "return usr._from";
    System.out.println("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      System.out.println("response" + response);
    } catch (Exception e) {
      log.error(
              "Exception while teamdetails_1 : " + e.getMessage().toString());
    }
    System.out.println("response"+response);

    for(int i=0;i<response.size();i++) {
      String query1 = "for a in Teams\n" +
              "filter a._id == '" + response.get(i) + "'\n" +
              "let l=(\n" +
              "for usr in userRoles\n" +
              "filter usr._from =='" + response.get(i) + "'\n" +
              "for usrId in usr.users\n" +
              " return usrId.id)\n" +
              " let d=(\n" +
              "for b in userRoles\n" +
              "filter b._from == '" + response.get(i) + "'\n" +
              "return {created:b.createdon,modified:b.lastmodifiedon})\n" +
              "let r=(\n" +
              "for u in registerUsers\n" +
              "filter u._key in l\n" +
              "return u.FirstName)\n" +
              "return {participants:r,createdOn:(for e in d collect c=e.created return c)[0],lastModifiedOn:(for e in d collect c=e.modified return c)[0],teamName:a.displayName,type:a.teamStructure,key:a._key}";
      System.out.println("query1--->" + query1);
      try {
        cursor = arangoDB.query(query1, Object.class);
        response1 = cursor.asListRemaining();
        System.out.println("response" + response1);
      } catch (Exception e) {
        log.error(
                "Exception while teamdetails_1 : " + e.getMessage().toString());
      }
      myteams.add(response1.get(0));
    }
    return myteams;
  }
}

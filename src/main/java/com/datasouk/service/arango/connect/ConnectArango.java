package com.datasouk.service.arango.connect;


import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.DocumentImportOptions;
import com.datasouk.core.exception.NotFound;
import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.exception.UnAuthorizedException;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.repository.MlQuery;
import com.datasouk.core.repository.NodeRepository;
import com.datasouk.mapper.search.NodeDetailMapperImpl;
import com.datasouk.mapper.search.NodeSearchMapperImpl;
import com.datasouk.service.AQL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datasouk.utils.arango.BuildRelation;
import com.datasouk.utils.arango.QueryBuilder;
import com.datasouk.utils.pagination.PageBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


// @SuppressWarnings({ "deprecation", "serial" })
@Service
public class ConnectArango {

  private static final Logger logger = LoggerFactory.getLogger(ConnectArango.class);
  Logger log = LoggerFactory.getLogger(ConnectArango.class);
  List<String> preferenceName = new ArrayList<>();
  ArrayList<JSONObject> savedPreferencesList = new ArrayList<>();
  @Value("${arango.nodes.collection}")
  private String arangonodesCollection;
  @Value("${arango.search.collection}")
  private String arangoSearchCollection;
  @Value("${arango.pinsearch.collection}")
  private String arangopinSearchCollection;
  @Value("${arango.myrecentsearch.collection}")
  private String arangomyrecentsearchCollection;
  @Value("${arango.searchType.collection}")
  private String arangoSearchTypeCollection;
  @Value("${arango.searchfilterHistory.collection}")
  private String arangoFilterSearchCollection;
  @Value("${arango.pinSearchType.collection}")
  private String arangoPinSearchTypeCollection;
  @Value("${arango.tagsHistory.collection}")
  private String arangomySearchTagsCollection;
  @Value("${arango.tag.collection}")
  private String ViewTagSearchesCollection;
  @Value("${arango.view.collection}")
  private String arangoViewCollection;
  @Value("${arango.edges.collection}")
  private String arangoRelCollection;
  @Value("${arango.viewName}")
  private String viewName;
  @Value("${arango.pinviewName}")
  private String pinviewName;
  @Value("${arango.nodes.collection}")
  private String nodesCollection;
  @Value("${arango.search.collection}")
  private String searchCollectionName;
  @Value("${arango.nodetypes.collection}")
  private String arangoNodeTypesCollection;
  @Value("${arango.metaedges.collection}")
  private String arangometaRelCollection;
  @Value("${arango.pincollection.collection}")
  private String pincollection;
  @Value("${arango.pincollectionedges.collection}")
  private String pincollectionedges;
  @Value("${arango.categoryList.collection}")
  private String categoryList;
  @Value("${arango.roleList.collection}")
  private String roleList;
  @Value("${arango.teams.collection}")
  private String Teams;
  @Value("${arango.userRegistration.collection}")
  private String userRegistration;
  @Value("${arango.savedPreferences.collection}")
  private String savedPreferences;
  @Value("${arango.savedPreferenceEdge.collection}")
  private String savedPreferenceEdge;
  @Value("${arango.myShares.collection}")
  private String mySharedCollection;
  @Value("${arango.tagsCollection.collection}")
  private String tagsCollection;
  @Value("${arango.tagEdge.collection}")
  private String tagsEdges;
  @Value("${arango.userRoles.collection}")
  private String userRoles;
  @Value("${arango.recentCollections.collection}")
  private String recentCollections;
  @Value("${arango.sort.collection}")
  private String arangoSortCollection;
  @Value("${arango.profileCategories.collection}")
  private String profileCategories;
  // @Autowired
  // private ArangoRestClient arangorestclient;
  @Autowired
  private ArangoRestClient arangorestclient;
  @Autowired
  private AQL aql;
  @Autowired
  private com.datasouk.core.utils.ArangoDB arangoDB;

  @Autowired
  private NodeRepository repository;
  @Autowired
  private NodeSearchMapperImpl nodeSearchMapperImpl;
  @Autowired
  private BuildRelation buildRelation;

  public String importDocuments2Arango(String documents, String collectionName) {

    String importResponse;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB,
        collectionName);

    if (arangoCollection != null) {
      arangoCollection.importDocuments(documents);
      log.info("Documents Inserted Successfully");
      importResponse = "Documents Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Documents";
    }
    return importResponse;
  }

  public String importDocuments2Arangofile(JSONObject obj, String collectionName) {

    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB != null) {
      ArangoCollection arangoCollection =
          arangorestclient.getArangoCollection(arangoDB, collectionName);

      if (arangoCollection != null) {

        arangoCollection.importDocuments((Collection<?>) obj);
        log.info("Documents Inserted Successfully");
        importResponse = "Documents Inserted Successfully";
      } else {
        importResponse = "Failed to Insert Documents";
      }
    } else {
      importResponse = "Failed to Connect to Database";
    }
    return importResponse;
  }

  public String importDocuments2Arango(Object documents, String collectionName) {

    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, collectionName);

    if (arangoCollection != null) {

      arangoCollection.insertDocument(documents);
      log.info("Documents Inserted Successfully");
      importResponse = "Documents Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Documents";
    }
    return importResponse;
  }

  @SuppressWarnings("rawtypes")
  public String importDocuments2Arango3(List<HashMap> documents, String collectionName)
      throws ServiceException {

    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, collectionName);

    if (arangoCollection != null) {

      arangoCollection.insertDocument(documents);
      log.info("Documents Inserted Successfully");
      importResponse = "Documents Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Documents";
    }
    return importResponse;
  }

  public String importDocuments2Arango1(List<Object> response, String collectionName)
      throws ServiceException {

    String importResponse = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, collectionName);

    if (arangoCollection != null) {

      arangoCollection.importDocuments(response);
      log.info("Documents Inserted Successfully");
      importResponse = "Documents Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Documents";
    }
    return importResponse;
  }

  public String importEdges2Arango(String documents) throws ServiceException {

    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, arangoRelCollection);

    if (arangoCollection != null) {

      DocumentImportOptions importOptions = new DocumentImportOptions();
      importOptions.waitForSync(Boolean.TRUE);
      importOptions.fromPrefix(arangonodesCollection);
      importOptions.toPrefix(arangonodesCollection);

      arangoCollection.importDocuments(documents, importOptions);
      log.info("Documents Inserted Successfully");
      importResponse = "Edges Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Edges";
    }
    return importResponse;

  }

  public String importDocuments2Arango2(List<String> response, String collectionName)
      throws ServiceException {

    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, collectionName);

    if (arangoCollection != null) {

      arangoCollection.importDocuments(response);
      log.info("Documents Inserted Successfully");
      importResponse = "Documents Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Documents";
    }
    return importResponse;
  }

  public List<Object> autoCompleteConnection(String nodeName) throws ServiceException {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    final String query = "FOR node in " + viewName + "\r\n"
        + "SEARCH ANALYZER(STARTS_WITH(node.name,TOKENS('" + nodeName
        + "','text_en')),'text_en')\r\n" + "SORT TFIDF(node) ASC\r\n" + "LIMIT 10\r\n"
        + "COLLECT name= node.name, type = node.type.name  \r\n" + "RETURN {name,type}";

    logger.info("autoCompleteConnection query------>" + query);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing Autocomplete Query: " + e.getMessage().toString());
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  public List<HashMap> getNodesInfo(String nodeName, String nodeType) throws ServiceException {
    try {
      preferenceName.clear();
      List<JSONObject> nodetypes = new ArrayList<JSONObject>();
      List<HashMap> response = new ArrayList<>();
      List<HashMap> response1 = new ArrayList<>();
      List<Object> response4 = new ArrayList<>();
      HashMap<String, ArrayList<Object>> NodeInfo = new HashMap<>();
      HashMap<String, Object> NodeInfo1 = new HashMap<>();
      ArrayList<Object> nodesList = new ArrayList<>();
      List<String> columns1 = new ArrayList<String>();
      List<String> columns2 = new ArrayList<String>();
      List<String> columns3 = new ArrayList<String>();
      List<String> columns4 = new ArrayList<String>();
      List<String> columns5 = new ArrayList<String>();
      List<String> columns6 = new ArrayList<String>();
      List<String> columns8 = new ArrayList<String>();
      List<String> columns9 = new ArrayList<String>();
      List<String> columns10 = new ArrayList<String>();
      // List<String> columns11= new ArrayList<String>();
      ArrayList<String> columns7 = new ArrayList<String>();
      List<String> qresponse = new ArrayList<>();
      List<Object> qresponse2 = new ArrayList<>();
      // List<HashMap> qresponse3 = new ArrayList<>();

      JSONObject searchString = new JSONObject();
      searchString.put("Name", nodeName);
      preferenceName.add(nodeName);

      JSONArray allWords = new JSONArray();

      ArangoDatabase arangoDB = this.arangoDB.getConnection();

      if (arangoDB == null) {
        throw new RuntimeException("Database connection is unavailable");
      }

      ArangoCursor<Object> cursor4 = null;
      removeFilterData(arangoDB);

      if (nodeType.equals("Data Set") || nodeType.equals("Schema")
          || nodeType.equals("Product")) {

        final String query = "let basicSearch=(FOR node IN nodesView\r\n"
            + "SEARCH ANALYZER(node.name IN TOKENS('" + nodeName + "','text_en'),'text_en')\r\n"
            + "SORT node.type.name == \"Data Set\" ? 1 : node.type.name == \"Data Product\" ? 2 : node.type.name == \"Schema\" ? 3 : 4\r\n"
            + "filter node.type.name =='" + nodeType + "'\r\n" + "RETURN node)\r\n"
            + "let soundexSearch=(for a in nodesView\r\n" + "filter SOUNDEX(a.name) == SOUNDEX('"
            + nodeName + "')\r\n"
            + "SORT a.type.name == \"Data Set\" ? 1 : a.type.name == \"Data Product\" ? 2 : a.type.name == \"Schema\" ? 3 : 4\r\n"
            + "FILTER  a.type.name == '" + nodeType + "' \r\n" + "return a)\r\n"
            + "return {resultMatches:UNIQUE(UNION(basicSearch,soundexSearch))}";

        ArangoCursor<HashMap> cursor = null;
        log.info("query...  " + query);

        try {
          cursor = arangoDB.query(query, HashMap.class);
          response = cursor.asListRemaining();
          log.info("response" + response);
        } catch (Exception e) {
          log.error("Exception while executing Asset Query: " + e.getMessage().toString());
        }

        if (response.size() != 0) {
          JSONObject x = new JSONObject(response.get(0));
          JSONArray temp = x.getJSONArray("resultMatches");
          for (int i = 0; i < temp.length(); i++) {
            allWords.put(temp.getJSONObject(i));
          }

        }
      } else {

        ArangoCursor<HashMap> cursor = null;
        ArangoCursor<HashMap> countCursor = null;
        ArangoCursor<Object> countCursor1 = null;
        log.info(nodeType);
        int offset = 1;
        long totalCount = 0;
        int pageLimit = 100;
        boolean moreToProcess = true;
        int responseCount = 100;

        final String query = "let c=(FOR doc IN nodesView\n"
            + "SEARCH ANALYZER(STARTS_WITH(doc.name, TOKENS('" + nodeName
            + "', \"text_en\")), \"text_en\") OR NGRAM_MATCH(doc.name,'" + nodeName
            + "',0.4,'fuzzy_search_bigram')\n"
            + "SORT doc.type.name == \"Data Set\" ? 1 : doc.type.name == \"Data Product\" ? 2 : doc.type.name == \"Schema\" ? 3 : 4\n"
            + "RETURN doc)\n"
            + "let b=(for a in nodesView\n"
            + "filter SOUNDEX(a.name) == SOUNDEX('" + nodeName + "')\n"
            + "SORT a.type.name == \"Data Set\" ? 1 : a.type.name == \"Data Product\" ? 2 : a.type.name == \"Schema\" ? 3 : 4\n"
            + "return a)\n"
            + "return COUNT_DISTINCT(unique([UNION(c,b)][**]))";

        countCursor1 = arangoDB.query(query, Object.class);

        totalCount = (long) countCursor1.asListRemaining().get(0);

        double numberOfPages = Math.ceil((double) totalCount / pageLimit);
        int currentPage = 0;

        while (moreToProcess) {
          if (numberOfPages >= currentPage) {
            logger.info(currentPage + "======currentPage");
            final String query1 = "let c=(FOR doc IN nodesView\n"
                + "  SEARCH ANALYZER(STARTS_WITH(doc.name, TOKENS('" + nodeName
                + "', \"text_en\")), \"text_en\") OR NGRAM_MATCH(doc.name,'" + nodeName
                + "',0.4,'fuzzy_search_bigram')\n"
                + "  SORT doc.type.name == \"Data Set\" ? 1 : doc.type.name == \"Data Product\" ? 2 : doc.type.name == \"Schema\" ? 3 : 4\n"
                + "LIMIT " + currentPage + ", " + pageLimit + " RETURN doc)\r\n"
                + "  let b=(for a in nodesView\n"
                + "filter SOUNDEX(a.name) == SOUNDEX('" + nodeName + "')\n"
                + " SORT a.type.name == \"Data Set\" ? 1 : a.type.name == \"Data Product\" ? 2 : a.type.name == \"Schema\" ? 3 : 4\n"
                + " LIMIT " + currentPage + ", " + pageLimit + " return a)\r\n"
                + "return {resultMatches:unique([UNION(c,b)][**][**])}";
            cursor = arangoDB.query(query1, HashMap.class);
            response = cursor.asListRemaining();
            responseCount = response.size();
            if (response.size() != 0) {
              JSONObject x = new JSONObject(response.get(0));
              JSONArray temp = x.getJSONArray("resultMatches");
              for (int i = 0; i < temp.length(); i++) {
                allWords.put(temp.getJSONObject(i));
              }

            }
            currentPage++;
          } else {
            moreToProcess = false;
          }

        }


      }

      JSONObject nodesinfo2 = new JSONObject();
      JSONObject assettype = new JSONObject();

      allWords.forEach(nodesinfo -> {
        JSONObject nodesinfo1 = new JSONObject();
        JSONObject nodes = new JSONObject(nodesinfo.toString());
        log.info("nodes" + nodes);
        String str = nodes.getString("displayName");
        nodesinfo1.put("DisplayName", str);
        String label = nodes.getString("identifier");
        String[] label1 = label.split(">");
        log.info(label1.toString());
        JSONObject nodetype = nodes.getJSONObject("type");
        String nameType = nodetype.getString("name");
        nodesinfo1.put("Type", nameType);
        assettype.put("Type", nameType);
        nodetypes.add(assettype);
        columns1.add(nameType);
        if (nameType.trim().contains("Data Set") || nameType.trim().contains("Data Product")
            || nameType.trim().contains("API")) {
          nodesinfo1.put("Action", "AddToCart");
        } else if (nameType.contains("Column") || nameType.contains("Schema")
            || nameType.contains("Table") || nameType.contains("Field")
            || nameType.contains("File")) {
          nodesinfo1.put("Action", "AddToDataSet");
        } else {
          nodesinfo1.put("Action", "Empty");
        }

        if (label.contains("curated")) {
          String curated = "true";
          nodesinfo1.put("Curated", curated);
          if (nameType.contains("Data Set") || nameType.contains("Schema")) {
            columns10.add(curated);
          }
        } else {

          String curated = "false";
          nodesinfo1.put("Curated", curated);
          if (nameType.contains("Data Set") || nameType.contains("Schema")) {
            columns10.add(curated);
          }
        }
        String nodeRatingsCount = nodes.get("ratingsCount").toString();
        nodesinfo1.put("RatingsCount", nodeRatingsCount);
        String avgRating = nodes.get("avgRating").toString();
        nodesinfo1.put("AvgRating", avgRating);
        columns3.add(avgRating);
        String identifier = nodes.getString("identifier");
        nodesinfo1.put("Identifier", identifier);
        String uuid = nodes.getString("id");
        nodesinfo1.put("Id", uuid);
        String key = nodes.getString("_key");
        nodesinfo1.put("key", key);
        if (nodes.has("createdByFullName")) {
          String createdByFullName = nodes.get("createdByFullName").toString();
          nodesinfo1.put("CreatedByFullName", createdByFullName);
        }
        if (nodes.has("createdOn")) {
          String createdOn = nodes.get("createdOn").toString();
          long l = Long.parseLong(createdOn);
          String createdOnDate = LocalDateTime
              .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
          nodesinfo1.put("createdOn", createdOnDate);
        }
        if (nodes.has("responsibilities")) {
          JSONArray responsibilities = nodes.getJSONArray("responsibilities");
          if (!responsibilities.isEmpty()) {
            responsibilities.forEach(eachsource -> {
              JSONObject responsibility = new JSONObject(eachsource.toString());
              String roleName = responsibility.getString("roleName");
              String responsibilityName = responsibility.getString("name");
              nodesinfo2.put(roleName.trim(), responsibilityName);
            });
          }
        }
        if (nodes.has("status")) {
          JSONObject nodestatus = nodes.getJSONObject("status");
          String statusName = nodestatus.getString("name");
          nodesinfo1.put("Status", statusName);
          columns2.add(statusName);
        }
        JSONArray attributeInfo = nodes.getJSONArray("attributes");
        // Build attribute object
        buildAttributes(attributeInfo, nodesinfo1, nameType, columns9, columns5, columns6,
            columns4);
        JSONArray targetsObj = new JSONArray();
        JSONArray sourceObj = new JSONArray();
        JSONObject edges = (JSONObject) nodes.get("relations");
        log.info("edges" + edges);
        JSONArray targetedges = edges.getJSONArray("targets");
        log.info("targets" + targetedges);
        JSONArray sourceedges = edges.getJSONArray("sources");
        log.info("sourceedges" + sourceedges);
        if (nodes.has("sourceProduct")) {
          String sourceProduct = nodes.getString("sourceProduct");
          nodesinfo1.put("SourceSystem", sourceProduct);
        } else if (nodes.has("sourceCatalog")) {
          String sourceCatalog = nodes.getString("sourceCatalog");
          nodesinfo1.put("SourceSystem", sourceCatalog);
        } else {
          // build the relation objects
          buildRelations(targetedges, sourceedges, nodesinfo1, targetsObj, sourceObj);
        }
        nodesinfo1.put("roles", nodesinfo2);
        nodesinfo1.put("QualityScore", getScore(str));
        nodesinfo1.put("metaQualityScore", metaQualityScore(str));
        nodesinfo1.put("Count", aql.favoriteNodesCount(key).get(0));
        ArrayList<String> LineOfBusiness = getTypes(str);
        ArrayList<String> DataDomain = getTypes1(str);
        nodesinfo1.put("LineOfBusiness", getTypes(str));
        columns7.addAll(LineOfBusiness);
        nodesinfo1.put("DataDomain", getTypes1(str));
        columns8.addAll(DataDomain);
        nodesList.add(nodesinfo1.toMap());
        storeSortValue(nodesinfo1.toMap());
      });

      NodeInfo.put("nodeInfo", nodesList);
      // nodesList.clear();
      NodeInfo1.put("searchString", searchString.toMap());
      response1.add(NodeInfo);
      storeSearchValue(response1);
      storeFilterSearchValue(response1);
      storeSearchType(columns1, columns2, columns3, columns4, columns5, columns6, columns7,
          columns8, columns9, columns10);
      // storeSearchType(nodetypes);
      response1.add(NodeInfo1);
      myRecentSearchHistory(searchString);

      return response1;
    } catch (Exception e) {
      throw new NotFound("unable to get the Nodeinfo");
    }

  }

  private Map<String, Object> storeSortValue(Map<String, Object> map) throws ServiceException {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangoSortCollection);
    try {
      arangoCollection.insertDocument(map);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocument(response, document);
      log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }
    return map;

  }

  public List<HashMap> nodesListView(String nodeName, String nodeType) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    HashMap<String, ArrayList<Object>> NodeInfo = new HashMap<>();
    HashMap<String, Object> NodeInfo1 = new HashMap<>();
    ArrayList<Object> nodesList = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    List<String> columns3 = new ArrayList<String>();
    List<String> columns4 = new ArrayList<String>();
    List<String> columns5 = new ArrayList<String>();
    List<String> columns6 = new ArrayList<String>();
    ArrayList<String> columns7 = new ArrayList<String>();
    List<String> columns8 = new ArrayList<String>();
    List<String> columns9 = new ArrayList<String>();
    List<String> columns10 = new ArrayList<String>();
    JSONObject searchString = new JSONObject();
    searchString.put("Name", nodeName);
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCursor<Object> cursor4 = null;
    String queryToBeExecuted2 = "for doc in " + arangoFilterSearchCollection + "\r\n"
        + "remove doc._key in " + arangoFilterSearchCollection + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
    // ArangoCursor<Object> cursor = null;
    try {

      cursor4 = arangoDB.query(queryToBeExecuted2, Object.class);
      response2 = cursor4.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while executing  Query: " + e.getMessage().toString());
    }

    // ArangoCursor<Object> cursor4 = null;
    String queryToBeExecuted3 = "for doc in " + arangoSearchTypeCollection + "\r\n"
        + "remove doc._key in " + arangoSearchTypeCollection + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
    // ArangoCursor<Object> cursor = null;
    try {

      cursor4 = arangoDB.query(queryToBeExecuted3, Object.class);
      response3 = cursor4.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while executing  Query: " + e.getMessage().toString());
    }

    if (nodeType.equals("Data Set") || nodeType.equals("Schema") || nodeType.equals("Product")) {
      final String query = "FOR node IN " + viewName + "\r\n"
          + "SEARCH ANALYZER(node.name IN TOKENS('" + nodeName + "','text_en'),'text_en')\r\n"
          + "FILTER  node.type.name == '" + nodeType + "' \r\n" + "RETURN node";
      ArangoCursor<HashMap> cursor = null;
      logger.info("query...  " + query);
      try {
        cursor = arangoDB.query(query, HashMap.class);
        response = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while executing Asset Query: " + e.getMessage().toString());
      }
    } else {
      final String query = "FOR node IN " + viewName + "\r\n"
          + "SEARCH ANALYZER(node.name IN TOKENS('" + nodeName + "','text_en'),'text_en')\r\n"
          + "SORT node.type.name == \"Data Set\" ? 1 : node.type.name == \"Data Product\" ? 2 : node.type.name == \"Schema\" ? 3 : 4\r\n"
          + "RETURN node";

      ArangoCursor<HashMap> cursor = null;
      logger.info("query...  " + query);
      try {
        cursor = arangoDB.query(query, HashMap.class);
        response = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while executing Asset Query: " + e.getMessage().toString());
      }
    }
    JSONObject nodesinfo1 = new JSONObject();
    JSONObject assettype = new JSONObject();
    response.forEach(nodesinfo -> {
      JSONObject nodes = new JSONObject(nodesinfo);
      String str = nodes.getString("displayName");
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("displayName", str);
      String label = nodes.getString("identifier");
      String[] label1 = label.split(">");
      logger.info(String.valueOf(label1));
      JSONObject nodetype = nodes.getJSONObject("type");
      String nameType = nodetype.getString("name");
      columns1.add(nameType);
      String Id = nodes.getString("id");
      nodesinfo1.put("id", Id);
      nodesinfo1.put("type", nameType);
      assettype.put("type", nameType);
      assettype.put("id", Id);

      if (label.contains("curated")) {
        String curated = "true";
        nodesinfo1.put("curated", curated);
        if (nameType.contains("Data Set") || nameType.contains("Schema")) {
          columns10.add(curated);
        }
      } else {

        String curated = "false";
        nodesinfo1.put("curated", curated);
        if (nameType.contains("Data Set") || nameType.contains("Schema")) {
          columns10.add(curated);
        }
      }

      JSONObject nodestatus = nodes.getJSONObject("status");
      String statusName = nodestatus.getString("name");
      nodesinfo1.put("status", statusName);
      columns2.add(statusName);

      String avgRating = nodes.get("avgRating").toString();
      nodesinfo1.put("avgRating", avgRating);
      columns3.add(avgRating);

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
            if (attributes.get("name").toString().equals("Certified")) {
              String value = attributes.getString("value");
              nodesinfo1.put("certified", value);
              if (nameType.contains("Data Set") || nameType.contains("Schema")) {
                columns9.add(value);
              }
            }
            if (attributes.get("name").toString().equals("Frequency")) {
              String frequency = attributes.getString("value");
              nodesinfo1.put("frequency", frequency);
              if (nameType.contains("Data Set") || nameType.contains("Schema")) {
                columns5.add(frequency);
              }
            }
            if (attributes.get("name").toString().equals("LastModifiedOn")) {
              String value = attributes.getString("value");
//                long l = Long.parseLong(value);
//                String Date = LocalDateTime
//                    .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
              nodesinfo1.put("lastModifiedOn", value);
              if (nameType.contains("Data Set") || nameType.contains("Schema")) {
                columns6.add(value);
              }
            }
            if (attributes.get("name").toString().equals("tag")) {
              String tag = attributes.getString("value");
              nodesinfo1.put("tag", tag);
              columns4.add(tag);
            }
          }
        });
      }
      // else if(attributeInfo.isEmpty())
      // {
      // nodesinfo1.put("No Attributes", " Attributes not available ");
      // }
      nodesList.add(nodesinfo1.toMap());
      ArrayList<String> LineOfBusiness = getTypes(str);
      ArrayList<String> DataDomain = getTypes1(str);
      nodesinfo1.put("lineOfBusiness", getTypes(str));
      columns7.addAll(LineOfBusiness);
      nodesinfo1.put("dataDomain", getTypes1(str));
      columns8.addAll(DataDomain);
    });

    NodeInfo.put("nodeInfo", nodesList);
    NodeInfo1.put("searchString", searchString.toMap());
    response1.add(NodeInfo);
    storeSearchValue(response1);
    storeFilterSearchValue(response1);
    storeSearchType(columns1, columns2, columns3, columns4, columns5, columns6, columns7, columns8,
        columns9, columns10);
    // storeSearchType(nodetypes);
    response1.add(NodeInfo1);
    myRecentSearchHistory(searchString);
    return response1;
  }

  public List<Object> nodeTypesdropDown() throws ServiceException {
    List<Object> response = new ArrayList<>();
    List<String> column = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    //final String query = "return [\"Data Set\",\"Data Product\",\"Schema\",\"All\"]";
    final String query = "FOR doc IN nodesView\n" +
        " return distinct doc.type.name";
    logger.info("query------>" + query);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing Autocomplete Query: " + e.getMessage().toString());
    }

    return response;
  }

  public List<Object> nodeCategoriesdropDown() throws ServiceException {
    List<Object> response = new ArrayList<>();
    List<String> column = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    final String query = "return [\"Business\",\"Product\",\"Data Domain\",\"Geography\"]";
    logger.info("query------>" + query);
    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing Autocomplete Query: " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> storeSearchValue(List<HashMap> response) throws ServiceException {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangoSearchCollection);
    HashMap<String, Object> document = new HashMap<>();
    //  document.put("nodename", response);
    Date date = new Date();
    logger.info(String.valueOf(date));
    document.put("searchedOn", date);
    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocument(response, document);
      log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }
    return response;
  }

  public List<HashMap> storeFilterSearchValue(List<HashMap> response) {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangoFilterSearchCollection);
    HashMap<String, Object> document = new HashMap<>();
    document.put("nodename", response);
    Date date = new Date();
    logger.info(String.valueOf(date));
    document.put("searchedOn", date);
    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocument(response, document);
      log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }

    return response;
  }

  public List<HashMap> storepinSearchValue(List<HashMap> response) throws ServiceException {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangopinSearchCollection);
    HashMap<String, Object> document = new HashMap<>();
    document.put("nodename", response);
    Date date = new Date();
    logger.info(String.valueOf(date));
    document.put("searchedOn", date);
    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocument(response, document);
      log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }
    return response;
  }

  public HashMap<String, List<String>> storeSearchType(List<String> columns1, List<String> columns2,
      List<String> columns3, List<String> columns4, List<String> columns5, List<String> columns6,
      ArrayList<String> columns7, List<String> columns8, List<String> columns9,
      List<String> columns10) throws ServiceException {
    HashMap<String, List<String>> document = new HashMap<>();
    HashMap<String, String[]> document1 = new HashMap<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangoSearchTypeCollection);
    document.put("nodename", columns1);
    document.put("status", columns2);
    document.put("AvgRating", columns3);
    document.put("tag", columns4);
    document.put("Frequency", columns5);
    document.put("Freshness", columns6);
    document.put("LineOfBusiness", columns7);
    document.put("DataDomain", columns8);
    document.put("certified", columns9);
    document.put("curated", columns10);
    // document.put("nodeType",columns11);
    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Type Document Created");
    } catch (ArangoDBException e) {
      log.error(
          "Exception while executing StoreSearchesType  Query: " + e.getMessage().toString());

    }

    return document;
  }

  public HashMap<String, List<String>> storePinSearchType(List<String> columns1,
      List<String> columns2, List<String> columns3, List<String> columns4, List<String> columns5,
      List<String> columns6, ArrayList<String> columns7, List<String> columns8,
      List<String> columns9, List<String> columns10) throws ServiceException {
    HashMap<String, List<String>> document = new HashMap<>();
    HashMap<String, String[]> document1 = new HashMap<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangoPinSearchTypeCollection);
    document.put("nodename", columns1);
    document.put("status", columns2);
    document.put("AvgRating", columns3);
    document.put("tag", columns4);
    document.put("Frequency", columns5);
    document.put("Freshness", columns6);
    document.put("LineOfBusiness", columns7);
    document.put("DataDomain", columns8);
    document.put("certified", columns9);
    document.put("curated", columns10);
    // document.put("nodeType",columns11);
    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Type Document Created");
    } catch (ArangoDBException e) {
      log.error(
          "Exception while executing StoreSearchesType  Query: " + e.getMessage().toString());

    }

    return document;
  }

  public List<JSONObject> storeSearchType(List<JSONObject> columns1) throws ServiceException {
    List<JSONObject> document = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangoSearchTypeCollection);
    document.addAll(columns1);

    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Type Document Created");
    } catch (ArangoDBException e) {
      log.error(
          "Exception while executing StoreSearchesType  Query: " + e.getMessage().toString());

    }

    return document;
  }

  public JSONObject myRecentSearchHistory(JSONObject nodesinfo1) throws ServiceException {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangomyrecentsearchCollection);
    HashMap<String, Object> document = new HashMap<>();
    document.put("nodename", nodesinfo1.get("Name"));
    Date date = new Date();
    logger.info(String.valueOf(date));
    document.put("searchedOn", date);
    logger.info(String.valueOf(document));
    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocuments(response, document);
      log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }
    return nodesinfo1;
  }

  public String importEdges2Arango1(String documents) throws ServiceException {

    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, arangometaRelCollection);
    if (arangoCollection != null) {
      DocumentImportOptions importOptions = new DocumentImportOptions();
      importOptions.waitForSync(Boolean.TRUE);
      importOptions.fromPrefix(arangoNodeTypesCollection);
      importOptions.toPrefix(arangoNodeTypesCollection);

      arangoCollection.importDocuments(documents, importOptions);
      log.info("Documents Inserted Successfully");
      importResponse = "Edges Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Edges";
    }
    return importResponse;
  }

  public List<String> getAllDocs(String collectionName) throws ServiceException {

    List<String> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCursor<String> cursor = null;
    String query = "for document in " + collectionName + "\r\n" + "return document";
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing Asset Query: " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> groupBynodetype(String collectionName) throws ServiceException {
    ArangoDatabase arangoDB = this.arangoDB.getConnection();
    List<HashMap> response = null;

    String query = "for doc in " + collectionName + "\r\n"
        + "collect metaCollection = doc.type.metaCollectionName into docClassification\r\n"
        + "return {\r\n" + "    metaCollection,\r\n" + "    docClassification\r\n" + "}";
    logger.info("query--->" + query);
    try {

      response = arangoDB.query(query, HashMap.class).asListRemaining();
      logger.info("query--->" + arangoDB.query(query, HashMap.class).asListRemaining());
    } catch (Exception e) {
      log.error("Exception while executing Query: " + e.getMessage().toString() + "\n" + query);
    }
    return response;
  }

  public String getQueryResult(String id) {
    final String query =
        "For node in " + viewName + "\r\n" + "filter node.id == '" + id + "'\r\n" + "return node";
    return query;
  }

  public String getQueryIdTargetResult(String id) {
    final String query = "For node in " + viewName + "\r\n" + "filter node.id == '" + id + "'\r\n"
        + "return node.relations.targets";
    return query;
  }

  public List<HashMap> getQuerysResult(String id) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    final String query =
        "For node in " + viewName + "\r\n" + "filter node.id == '" + id + "'\r\n" + "return node";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while getQuerysResult : " + e.getMessage().toString());
    }
    return response;

  }

  public String getQueryNodeResult(String name) {
    final String query = "For node in " + arangonodesCollection + "\r\n" + "filter node.name == '"
        + name + "'\r\n" + "return node";
    return query;
  }

  public String getQueryTargetResult(String name) {
    final String query = "For node in " + viewName + "\r\n" + "filter node.name == '" + name
        + "'\r\n" + "return node.relations.targets";
    return query;
  }

  @SuppressWarnings("rawtypes")
  public HashMap<String, ArrayList<Object>> viewnodesinfo(String id) throws JSONException {

    List<HashMap> response = new ArrayList<>();
    HashMap<String, ArrayList<Object>> nodesearchInfo = new HashMap<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = getQueryResult(id);
    ArangoCursor<HashMap> cursor = null;
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing Asset Query: " + e.getMessage().toString());
    }
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();
    JSONObject recentInfo = new JSONObject();
    JSONObject nodesinfo2 = new JSONObject();
    JSONObject nodesinfo3 = new JSONObject();
    JSONObject nodesinfo4 = new JSONObject();
    JSONObject nodesinfo5 = new JSONObject();
    JSONObject versions = new JSONObject();
    JSONObject privacyRisk = new JSONObject();
    JSONObject elements = new JSONObject();
    JSONObject tagsAndNames = new JSONObject();
    ArrayList<Object> responsibilityList = new ArrayList<>();
    response.forEach(nodesinfo -> {
      String result = null;
      JSONObject nodes = new JSONObject(nodesinfo);
      String str = nodes.getString("displayName");
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("displayName", str);
      tagsAndNames.put("displayName", str);
      String label = nodes.getString("identifier");
      String[] label1 = label.split(">");
      logger.info(String.valueOf(label1));
      if (label.contains("curated")) {
        String curated = "true";
        nodesinfo1.put("curated", curated);
      } else {
        String curated = "false";
        nodesinfo1.put("curated", curated);
      }

      String nodeRatingsCount = nodes.get("ratingsCount").toString();
      nodesinfo1.put("ratingsCount", nodeRatingsCount);
      String avgRating = nodes.get("avgRating").toString();
      nodesinfo1.put("avgRating", avgRating);
      String identifier = nodes.getString("identifier");
      nodesinfo1.put("identifier", identifier);
      String Id = nodes.getString("id");
      nodesinfo1.put("id", Id);
      String key = nodes.getString("_key");
      nodesinfo1.put("key", key);
      viewHistory(key);
      if (nodes.has("createdOn")) {
        String createdOn = nodes.getString("createdOn");
        // long m = Long.parseLong(createdOn);
//          String Date1 =
//              LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(m)), ZoneId.systemDefault())
//                  .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
        nodesinfo2.put("dateCreated", createdOn);
        nodesinfo1.put("createdOn", createdOn);
      }

      if (nodes.has("createdByFullName")) {
        String createdByFullName = nodes.getString("createdByFullName");
        nodesinfo1.put("createdByFullName", createdByFullName);
      }
      if (nodes.has("createdByUserName")) {
        String createdByUserName = nodes.getString("createdByUserName");
      }
      JSONObject nodetype = nodes.getJSONObject("type");
      String name = nodetype.getString("name");
      nodesinfo1.put("type", name);
      if (name.contains("Data Set") || name.contains("Data Product") || name.contains("API")) {
        nodesinfo1.put("Action", "AddToCart");
      } else if (name.contains("Column") || name.contains("Schema") || name.contains("Table")
          || name.contains("Field") || name.contains("File")) {
        nodesinfo1.put("Action", "AddToDataSet");
      }
      JSONObject roles = new JSONObject();
      String roleName = null;
      String responsibilityName = null;
      if (nodes.has("responsibilities")) {
        JSONArray responsibilities = nodes.getJSONArray("responsibilities");
        if (!responsibilities.isEmpty()) {
          // responsibilities.forEach(eachsource -> {
          for (int i = 0; i < responsibilities.length(); i++) {
            JSONObject responsibility = new JSONObject(responsibilities.get(i).toString());
            roleName = responsibility.getString("roleName");
            responsibilityName = responsibility.getString("name");
            //roles.put(roleName.trim(), responsibilityName);
            roles.put("roleName", roleName);
            roles.put("name", responsibilityName);
            responsibilityList.add(roles.toMap());
          }
          //});
        }

      }
      if (nodes.has("status")) {
        JSONObject nodestatus = nodes.getJSONObject("status");
        String statusName = nodestatus.getString("name");
        nodesinfo1.put("status", statusName);
      }
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
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
            if (attributes.get("name").toString().equals("Frequency")) {
              String value = attributes.getString("value");
              nodesinfo1.put("frequency", value);
              nodesinfo2.put("frequency", value);

            }
            if (attributes.get("name").toString().equals("LastModifiedOn")) {
              String value = attributes.getString("value");
//                long l = Long.parseLong(value);
//                String Date = LocalDateTime
//                    .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
//                    .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
              nodesinfo1.put("lastModifiedOn", value);
              nodesinfo2.put("lastModifiedOn", value);
            }
            if (attributes.get("name").toString().equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              nodesinfo1.put("personallyIdentifiableInformation", value);
            }
            if (attributes.get("name").toString().equals("Security Classification")) {
              String value = attributes.getString("value");
              nodesinfo1.put("securityClassification", value);
            }
            if (attributes.get("name").toString().equals("tag")) {
              String value = attributes.getString("value");
              nodesinfo1.put("tag", value);
              // nodesinfo2.put("Tag", value);
              tagsAndNames.put("tag", value);
            }
            if (attributes.get("name").toString().equals("Passing Fraction")) {
              String value = attributes.getString("value");
              nodesinfo1.put("passingFraction", value);
            }
            if (attributes.get("name").toString().equals("Shoppable")) {
              String value = attributes.getString("value");
              nodesinfo1.put("shopable", value);
            }
            if (attributes.get("name").toString().equals("searchable")) {
              String value = attributes.getString("value");
              nodesinfo1.put("searchable", value);
            }
            if (attributes.get("name").toString().equals("url")) {
              String value = attributes.getString("value");
              nodesinfo1.put("url", value);
            }

          }
        });
      } else if (attributeInfo.isEmpty()) {
        nodesinfo1.put("no Attributes", " Attributes not available ");
      }
      nodesinfo2.put("currentVersion", "1.0");
      JSONArray targetsObj = new JSONArray();
      JSONObject edges = (JSONObject) nodes.get("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      if (nodes.has("sourceProduct")) {
        String sourceProduct = nodes.getString("sourceProduct");
        nodesinfo1.put("sourceSystem", sourceProduct);
      } else if (nodes.has("sourceCatalog")) {
        String sourceCatalog = nodes.getString("sourceCatalog");
        nodesinfo1.put("sourceSystem", sourceCatalog);
      } else {
        if (!targetedges.isEmpty()) {
          targetedges.forEach(eachsource -> {
            JSONObject targets = new JSONObject(eachsource.toString());
            logger.info("targets" + targets);
            String targetname = null;
            if (targets.has("CoRole")) {
              targetname = targets.getString("CoRole");
            } else {
              targetname = targets.getString("coRole");
            }
            logger.info("targetname" + targetname);
            if (targetname.contains("contains") || targetname.contains("represents")) {
              targetsObj.put(targets);
            } else {
              logger.info("no targets");
            }
          });
          if (!targetsObj.isEmpty()) {
            JSONObject targetedges1 = targetsObj.getJSONObject(0);
            JSONObject target1 = targetedges1.getJSONObject("target");
            String targetname = null;
            if (targetedges1.has("CoRole")) {
              targetname = targetedges1.getString("CoRole");
            }
            if (targetedges1.has("coRole")) {
              targetname = targetedges1.getString("coRole");
            }
            String targetid = target1.getString("id");
            String query1 = getQueryResult(targetid);
            if (targetname.contains("contains") || targetname.contains("represents")) {
              result = getNodesResponse(query1);
              nodesinfo1.put("sourceSystem", result);
            } else {
              nodesinfo1.put("sourceSystem", "null");
            }

          } else {
            nodesinfo1.put("sourceSystem", "null");
          }
        } else {
          nodesinfo1.put("sourceSystem", "null");
        }
      }
      nodesinfo1.put("roles", responsibilityList);
      nodesinfo1.put("qualityScore", getScore(str));
      nodesinfo1.put("lineOfBusiness", getTypes(str));
      nodesinfo1.put("dataDomain", getTypes1(str));
      nodesinfo1.put("metaQualityScore", metaQualityScore(id));
      System.out.println(metaQualityScore(id));
      nodesinfo1.put("count", aql.favoriteNodesCount(key).get(0));
      nodesinfo4.put("operationalMetaData", operationalMetaData(str));
      nodesinfo3.put("metaCollection", MetaCollection(Id));
      nodesinfo5.put("frequencyAndFreshness", nodesinfo2);
      ArrayList<Object> versionscounts = getVersions(str).get("nodesearchInfo");
      HashMap pr = getprivacyAndRisks(str).get(2);
      HashMap dataelements = aql.dataElements(id).get(2);
      Object pr1 = pr.get("count");
      Object dataelements1 = dataelements.get("count");
      int counter = 0;
      for (int i = 1; i <= versionscounts.size(); i++) {
        counter = counter + 1;
        logger.info(String.valueOf(counter));
      }
      versions.put("versionsCount", counter);
      privacyRisk.put("privacyandRiskCount", pr1);
      elements.put("elementsCount", dataelements1);
      nodesList.add(nodesinfo1.toMap());
      nodesList.add(nodesinfo3.toMap());
      nodesList.add(nodesinfo4.toMap());
      nodesList.add(nodesinfo5.toMap());
      nodesList.add(versions.toMap());
      nodesList.add(privacyRisk.toMap());
      nodesList.add(elements.toMap());
    });
    nodesearchInfo.put("nodesearchInfo", nodesList);
    //viewHistory(recentInfo);
    tagsCollection(nodesinfo1);
    return nodesearchInfo;
  }

  public String getNodesResponse(String query) throws ServiceException {
    String targetname = null;
    String targetname1 = null;
    String targetName = null;

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing Asset Query: " + e.getMessage().toString());
    }
    for (int j = 0; j < response.size(); j++) {
      JSONArray targetsObj = new JSONArray();
      HashMap targetlist = response.get(j);
      JSONObject nodes1 = new JSONObject(targetlist);
      JSONObject nodes = new JSONObject(nodes1.toString());
      targetName = nodes.getString("displayName");
      JSONObject relations = (JSONObject) nodes.get("relations");
      logger.info("relations" + relations);
      JSONArray targetrelations = relations.getJSONArray("targets");
      if (!targetrelations.isEmpty()) {
        for (int i = 0; i < targetrelations.length(); i++) {
          JSONObject targetlists = (JSONObject) targetrelations.get(i);
          JSONObject targets = new JSONObject(targetlists.toString());
          if (targets.has("CoRole")) {
            targetname1 = targets.getString("CoRole");
          } else {
            targetname1 = targets.getString("coRole");
          }

          if (targetname1.contains("is part of") || targetname1.contains("implemented in")
              || targetname1.contains("contains")) {
            targetsObj.put(targets);
          }
          if (!targetsObj.isEmpty()) {
            JSONObject targets1 = targetsObj.getJSONObject(0);

            if (targets.has("CoRole")) {
              targetname = targets1.getString("CoRole");
            } else {
              targetname = targets1.getString("coRole");
            }
            JSONObject target = targets1.getJSONObject("target");
            targetName = target.getString("name");
            String id = target.getString("id");
            String type = target.getString("type");

            if (targetname.contains("contains")) {
              String query1 = getQueryResult(id);
              getNodesResponse(query1);
            } else if (targetname.contains("is part of") && type.contains("Table")) {
              String query1 = getQueryResult(id);
              getNodesResponse(query1);
            } else if (targetname.contains("is part of") && type.contains("Schema")) {
              String query1 = getQueryResult(id);
              getNodesResponse(query1);
            } else if (targetname.contains("implemented in")) {
              String query1 = getQueryResult(id);
              getNodesResponse(query1);
            }
          }
        }
      }
    }
    return targetName;
  }

  public JSONObject tagsCollection(JSONObject nodesinfo1) throws ServiceException {
    ArrayList<Object> storingTagsAndNames = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, ViewTagSearchesCollection);
    storingTagsAndNames.add(nodesinfo1);
    try {
      arangoCollection.insertDocument(nodesinfo1);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocuments(response, document);
      log.error("Exception while executing StoreTagSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }
    return nodesinfo1;
  }

  public String viewHistory(String key) throws ServiceException {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangoViewCollection);

    HashMap<String, Object> document = new HashMap<>();
    document.put("nodekey", key);

    Date date = new Date();
    document.put("searchedOn", date);

    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocuments(response, document);
      log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }
    return key;
  }

  public String importDocuments2Arango1(ArrayList<JSONObject> collectionnodes1,
      String collectionName, String collectionType) throws ServiceException {
    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    if (arangoDB != null) {
      ArangoCollection arangoCollection =
          arangorestclient.getArangoCollection(arangoDB, collectionName);

      if (arangoCollection != null) {
        for (int i = 0; i < collectionnodes1.size(); i++) {

          arangoCollection.insertDocument(collectionnodes1.get(i));
        }

        log.info("Documents Inserted Successfully");
        importResponse = "Documents Inserted Successfully";
      } else {
        importResponse = "Failed to Insert Documents";
      }
    } else {
      importResponse = "Failed to Connect to Database";
    }
    return importResponse;
  }

  public String importDocuments2Arango(String documents, String collectionName,
      String collectionType) throws ServiceException {

    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    if (arangoDB != null) {
      ArangoCollection arangoCollection = null;
      if (collectionType == "Document") {
        arangoCollection = arangorestclient.getArangoCollection(arangoDB, collectionName);
      } else if (collectionType == "Edge") {
        arangoCollection = arangorestclient.getArangoEdgeCollection(arangoDB, collectionName);
      } else {
        log.error("Please check the Collection Type");
      }
      if (arangoCollection != null) {
        arangoCollection.importDocuments(documents);
        log.info("Documents Inserted Successfully");
        importResponse = "Documents Inserted Successfully";
      } else {
        importResponse = "Failed to Insert Documents";
      }
    } else {
      importResponse = "Failed to Connect to Database";
    }
    return importResponse;
  }

  public int getScore(String name) throws ServiceException {

    List<String> response = new ArrayList<>();
    int qualityScore = 0;

    List<String> response1 = new ArrayList<>();

    List<String> response2 = new ArrayList<>();

    List<String> response3 = new ArrayList<>();

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "For node in " + nodesCollection + "\r\n"
        + "filter node.name == \"" + name + "\"\r\n" + "return node.relations.targets";

    logger.info("queryToBeExecuted" + queryToBeExecuted);
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.info(
          "Exception while getScore : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();

    response.forEach(coumnrelations -> {
      JSONArray eachRelation = new JSONArray(coumnrelations);
      logger.info("eachRelation" + eachRelation);
      if (!eachRelation.isEmpty()) {
        eachRelation.forEach(coumnrelations1 -> {
          JSONObject relations = new JSONObject(coumnrelations1.toString());
          JSONObject target1 = relations.getJSONObject("target");

          String targetname = null;
          if (relations.has("CoRole")) {
            targetname = relations.getString("CoRole");
          }
          if (relations.has("coRole")) {
            targetname = relations.getString("coRole");
          }


          if (targetname.toString().equals("is part of")) {
            columns.add("node.name == '" + target1.get("name").toString() + "'");
          }

        });
      }
    });

    String columnIds = String.join(" OR ", columns);
    if (!columnIds.isEmpty()) {

      String query = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds + "\r\n"
          + "return node.relations.sources";

      try {

        cursor = arangoDB.query(query, String.class);
        response1 = cursor.asListRemaining();


      } catch (Exception e) {
        log.error(
            "Exception while getScore_2: " + e.getMessage().toString());
      }

      List<String> columns1 = new ArrayList<String>();
      response1.forEach(coumnrelations -> {
        JSONArray eachRelation = new JSONArray(coumnrelations);
        eachRelation.forEach(coumnrelations1 -> {
          JSONObject relations = new JSONObject(coumnrelations1.toString());

          JSONObject target1 = relations.getJSONObject("source");
          if (relations.get("role").toString().equals("complies to")) {
            columns1.add("node.name == '" + target1.get("name").toString() + "'");

          }


        });

      });
      String columnIds1 = String.join(" OR ", columns1);

      if (!columnIds1.isEmpty()) {
        String query1 = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds1 + "\r\n"
            + "return node.relations.targets";
        try {

          cursor = arangoDB.query(query1, String.class);
          response2 = cursor.asListRemaining();

        } catch (Exception e) {
          log.error(
              "Exception while getScore_3: " + e.getMessage().toString());
        }

        List<String> columns2 = new ArrayList<String>();
        response2.forEach(coumnrelations -> {
          JSONArray eachRelation = new JSONArray(coumnrelations);
          eachRelation.forEach(coumnrelations1 -> {
            JSONObject relations = new JSONObject(coumnrelations1.toString());
            JSONObject target1 = relations.getJSONObject("target");
            String targetname = null;
            if (relations.has("CoRole")) {
              targetname = relations.getString("CoRole");
            }
            if (relations.has("coRole")) {
              targetname = relations.getString("coRole");
            }
            if (targetname.contains("executes")) {
              columns2.add("node.name == '" + target1.get("name").toString() + "'");
            }
          });
        });

        int columnCount2 = columns2.size();
        String columnIds2 = String.join(" OR ", columns2);
        if (!columnIds2.isEmpty()) {
          String query2 =
              "For node in " + nodesCollection + "\r\n" + "filter " + columnIds2 + "\r\n"
                  + "return node";
          try {
            cursor = arangoDB.query(query2, String.class);
            response3 = cursor.asListRemaining();
          } catch (Exception e) {
            log.error(
                "Exception while getScore_4: " + e.getMessage().toString());
          }
          List<Integer> columns3 = new ArrayList<Integer>();
          response3.forEach(coumnattributes -> {
            JSONObject attr = new JSONObject(coumnattributes.toString());
            JSONArray attributeInfo = attr.getJSONArray("attributes");
            attributeInfo.forEach(eachAttribute -> {
              JSONObject attributes = new JSONObject(eachAttribute.toString());
              if (attributes.get("name").toString().equals("Passing Fraction")) {
                String value = attributes.getString("value");
                int i = Integer.parseInt(value);
                columns3.add(i);
              }
            });
          });
          int sum = 0;
          for (int i : columns3) {
            sum += i;
          }
          try {
            qualityScore = sum / columnCount2;

          } catch (ArithmeticException e) {
            // Exception handler
            logger.info(String.valueOf(qualityScore));
            logger.info("Divided by zero operation cannot possible");
          }
        }
      }
    }
    return qualityScore;
  }

  public ArrayList<String> getTypes(String name) throws ServiceException {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    ArangoCursor<String> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    ArrayList<String> columns2 = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "For node in " + nodesCollection + "\r\n"
        + "filter node.name == \"" + name + "\"\r\n" + "return node.relations.targets";
    ArangoCursor<String> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing types Query: " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    response.forEach(coumnrelations -> {
      JSONArray eachRelation = new JSONArray(coumnrelations);
      eachRelation.forEach(coumnrelations1 -> {
        JSONObject relations = new JSONObject(coumnrelations1.toString());
        JSONObject target1 = relations.getJSONObject("target");
        String targetname = null;
        if (relations.has("CoRole")) {
          targetname = relations.getString("CoRole");
        }
        if (relations.has("coRole")) {
          targetname = relations.getString("coRole");
        }
        if (targetname.contains("is part of")) {
          columns.add("node.name == '" + target1.get("name").toString() + "'");
        }

      });
    });

    String columnIds = String.join(" OR ", columns);
    if (!columnIds.isEmpty()) {
      String query = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds + "\r\n"
          + "return node.relations.sources";
      try {
        cursor1 = arangoDB.query(query, String.class);
        response1 = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error(
            "Exception while getTypes : " + e.getMessage().toString());
      }
      List<String> columns1 = new ArrayList<String>();
      response1.forEach(coumnrelations -> {
        JSONArray eachRelation = new JSONArray(coumnrelations);
        eachRelation.forEach(coumnrelations1 -> {
          JSONObject relations = new JSONObject(coumnrelations1.toString());
          JSONObject target1 = relations.getJSONObject("source");
          if (relations.get("role").toString().equals("represents")) {
            columns1.add("node.name == '" + target1.get("name").toString() + "'");
          }
        });
      });
      String columnIds2 = String.join(" OR ", columns1);
      if (!columnIds2.isEmpty()) {
        String query1 = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds2 + "\r\n"
            + "return node.relations.sources";
        try {
          cursor2 = arangoDB.query(query1, String.class);
          response2 = cursor2.asListRemaining();
        } catch (Exception e) {
          log.error(
              "Exception while getTypes_2 : " + e.getMessage().toString());
        }
        response2.forEach(coumnrelations -> {
          JSONArray eachRelation = new JSONArray(coumnrelations);
          eachRelation.forEach(coumnrelations1 -> {
            JSONObject relations = new JSONObject(coumnrelations1.toString());
            JSONObject target1 = relations.getJSONObject("source");
            if (relations.get("role").toString().equals("associates")) {
              columns2.add(target1.get("name").toString());
            }
          });
        });
      }
    }
    return columns2;
  }

  public ArrayList<String> getTypes1(String name) throws ServiceException {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    ArangoCursor<String> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    ArrayList<String> columns2 = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "For node in " + nodesCollection + "\r\n"
        + "filter node.name == \"" + name + "\"\r\n" + "return node.relations.targets";
    ArangoCursor<String> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getTypes1: " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    response.forEach(coumnrelations -> {
      JSONArray eachRelation = new JSONArray(coumnrelations);
      eachRelation.forEach(coumnrelations1 -> {
        JSONObject relations = new JSONObject(coumnrelations1.toString());
        JSONObject target1 = relations.getJSONObject("target");
        String targetname = null;
        if (relations.has("CoRole")) {
          targetname = relations.getString("CoRole");
        }
        if (relations.has("coRole")) {
          targetname = relations.getString("coRole");
        }
        if (targetname.contains("is part of")) {
          columns.add("node.name == '" + target1.get("name").toString() + "'");
        }
      });
    });

    String columnIds = String.join(" OR ", columns);
    if (!columnIds.isEmpty()) {
      String query = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds + "\r\n"
          + "return node.relations.sources";
      try {
        cursor1 = arangoDB.query(query, String.class);
        response1 = cursor1.asListRemaining();
      } catch (Exception e) {
        log.error(
            "Exception while getTypes1_2 : " + e.getMessage().toString());
      }
      List<String> columns1 = new ArrayList<String>();
      response1.forEach(coumnrelations -> {
        JSONArray eachRelation = new JSONArray(coumnrelations);
        eachRelation.forEach(coumnrelations1 -> {
          JSONObject relations = new JSONObject(coumnrelations1.toString());
          JSONObject target1 = relations.getJSONObject("source");
          if (relations.get("role").toString().equals("represents")) {
            columns1.add("node.name == '" + target1.get("name").toString() + "'");
          }
        });
      });
      String columnIds2 = String.join(" OR ", columns1);
      if (columnIds2.isEmpty()) {
        String query1 = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds2 + "\r\n"
            + "return node.relations.sources";
        try {
          cursor2 = arangoDB.query(query1, String.class);
          response2 = cursor2.asListRemaining();
        } catch (Exception e) {
          log.error(
              "Exception while getTypes1_3: " + e.getMessage().toString());
        }
        response2.forEach(coumnrelations -> {
          JSONArray eachRelation = new JSONArray(coumnrelations);
          eachRelation.forEach(coumnrelations1 -> {
            JSONObject relations = new JSONObject(coumnrelations1.toString());
            JSONObject target1 = relations.getJSONObject("source");
            if (relations.get("role").toString().equals("classifies")) {
              columns2.add(target1.get("name").toString());
            }
          });
        });
      }
    }
    return columns2;
  }

  public Object searchPageFilters() throws ServiceException {
    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "LET nodeType =(For doc in " + arangoSearchTypeCollection + "\r\n"
        + "for a in doc.nodename\r\n" + "COLLECT label = a WITH COUNT into nodesCount\r\n"
        + "SORT nodesCount DESC\r\n" + "LIMIT 10\r\n" + "RETURN {label,value: nodesCount})\r\n"
        + "LET status =(For doc in " + arangoSearchTypeCollection + "\r\n"
        + "for a in doc.status\r\n" + "COLLECT label = a WITH COUNT into nodesCount\r\n"
        + "SORT nodesCount DESC\r\n" + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n"
        + "LET avgRating =(For doc in " + arangoSearchTypeCollection + "\r\n"
        + "for a in doc.AvgRating\r\n" + "COLLECT label = a WITH COUNT into nodesCount\r\n"
        + "SORT nodesCount DESC\r\n" + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n"
        + "LET tag =(For doc in " + arangoSearchTypeCollection + "\r\n" + "for a in doc.tag\r\n"
        + "COLLECT label = a WITH COUNT into nodesCount\r\n" + "SORT nodesCount DESC\r\n"
        + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n" + "LET frequency =(For doc in "
        + arangoSearchTypeCollection + "\r\n" + "for a in doc.Frequency\r\n"
        + "COLLECT label = a WITH COUNT into nodesCount\r\n" + "SORT nodesCount DESC\r\n"
        + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n" + "LET datadomain=(For doc in "
        + arangoSearchTypeCollection + "\r\n" + "for a in doc.DataDomain\r\n"
        + "COLLECT label = a WITH COUNT into nodesCount\r\n" + "SORT nodesCount DESC\r\n"
        + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n"
        + "LET lineOfBusiness=(For doc in " + arangoSearchTypeCollection + "\r\n"
        + "for a in doc.LineOfBusiness\r\n" + "COLLECT label = a WITH COUNT into nodesCount\r\n"
        + "SORT nodesCount DESC\r\n" + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n"
        + "LET freshness=(For doc in " + arangoSearchTypeCollection + "\r\n"
        + "for a in doc.Freshness\r\n" + "COLLECT label = a WITH COUNT into nodesCount\r\n"
        + "COLLECT AGGREGATE min = MIN(label), max = MAX(label)\r\n" + "LIMIT 10\r\n"
        + "RETURN {label:DATE_DIFF(min, max, \"days\")})\r\n" + "LET certified=(For doc in "
        + arangoSearchTypeCollection + "\r\n" + "for a in doc.certified\r\n"
        + "COLLECT label = a WITH COUNT into nodesCount\r\n" + "SORT nodesCount DESC\r\n"
        + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n" + "LET curated=(For doc in "
        + arangoSearchTypeCollection + "\r\n" + "for a in doc.curated\r\n"
        + "COLLECT label = a WITH COUNT into nodesCount\r\n" + "SORT nodesCount DESC\r\n"
        + "LIMIT 10\r\n" + "RETURN {label,value:nodesCount})\r\n"
        + "Return {NodeType:nodeType,Status:status,AvgRating:avgRating,Tag:tag,Frequency:frequency,Datadomain:datadomain,LineOfBusiness:lineOfBusiness,Freshness:freshness,Certified:certified,Curated:curated}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while executing filters Query: " + e.getMessage().toString());
    }

    return response;
  }

  public int metaQualityScore(String nodeid) throws ServiceException {
    List<String> response = new ArrayList<>();
    int k = 100;
    int metaQualityScore = 0;
    int metaQualityScore1 = 0;
    int columnCount;
    int columnCount1;
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "For node in " + nodesCollection + "\r\n"
        + "filter node.id == \"" + nodeid + "\"\r\n" + "return node";
    ArangoCursor<String> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing  Query: " + e.getMessage().toString());
    }
    response.forEach(node -> {
      JSONObject nodeinfo = new JSONObject(node);
      JSONObject edges = nodeinfo.getJSONObject("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      targetedges.forEach(rel -> {
        JSONObject targets = new JSONObject(rel.toString());
        String targetname = null;
        if (targets.has("CoRole")) {
          targetname = targets.getString("CoRole");
        } else {
          targetname = targets.getString("coRole");
        }
        JSONObject target1 = targets.getJSONObject("target");
        List<HashMap> response1;
        if (targetname.contains("is part of")) {
          String targetid = target1.getString("id");
          String query1 = getQueryResult(targetid);
          response1 = getNodesResponseForMetaQuality(query1);
          if (response1.isEmpty()) {
            for (int j = 0; j < response1.size(); j++) {

              HashMap targetlist = response1.get(j);
              JSONObject nodes1 = new JSONObject(targetlist);
              JSONObject nodes = new JSONObject(nodes1.toString());

              JSONArray attributeInfo = nodes.getJSONArray("attributes");
              if (!attributeInfo.isEmpty()) {
                attributeInfo.forEach(eachAttribute -> {
                  JSONObject attributes = new JSONObject(eachAttribute.toString());
                  if (!attributes.isEmpty()) {
                    if (attributes.getString("name").equals("Description")) {
                      String value = attributes.getString("value");
                      columns1.add(value);
                    }
                  }
                });

              }
              JSONObject relations = (JSONObject) nodes.get("relations");
              JSONArray targetrelations = relations.getJSONArray("targets");
              if (!targetrelations.isEmpty()) {
                for (int i = 0; i < targetrelations.length(); i++) {
                  JSONObject targetlists = (JSONObject) targetrelations.get(i);
                  JSONObject targets1 = new JSONObject(targetlists.toString());
                  if (targets.has("CoRole")) {
                    targetname = targets.getString("CoRole");
                  } else {
                    targetname = targets.getString("coRole");
                  }
                  JSONObject target = targets1.getJSONObject("target");
                  String id = target.getString("id");
                  if (targetname.contains("is part of")) {
                    columns2.add(targetname);
                    String query2 = getQueryResult(id);
                    getNodesResponseForMetaQuality(query2);
                  }
                }
              }
            }
          }
        }
      });

    });

    if (!columns2.isEmpty()) {
      columnCount = columns1.size();
      columnCount1 = columns2.size();
      try {
        // metaQualityScore= (columnCount*k/columnCount1);
        metaQualityScore = (columnCount / columnCount1);
        // metaQualityScore=(columnCount/k)*columnCount1;
        // metaQualityScore=metaQualityScore1*k;
      } catch (ArithmeticException e) {
        // logger.info(metaQualityScore);
        // logger.info("Divided by zero operation cannot possible");
        log.info("Exception while metaQualityScore : " + e.getMessage().toString());
      }
    }
    return metaQualityScore;
  }

  public List<HashMap> getNodesResponseForMetaQuality(String query) throws ServiceException {
    List<HashMap> response = new ArrayList<>();

    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getNodesResponseForMetaQuality : " + e.getMessage().toString());
    }
    return response;
  }

  public List<String> getNodesResponseForMetaQuality1(String query) throws ServiceException {
    List<String> response = new ArrayList<>();

    ArangoCursor<String> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getNodesResponseForMetaQuality1 : " + e.getMessage().toString());
    }
    return response;
  }

  public List<String> Metagraph(String id) throws ServiceException {
    List<String> response = new ArrayList<>();

    ArangoCursor<String> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "filter node.id == '" + id
        + "'\r\n" + "return node.relations.targets";
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while Metagraph : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    response.forEach(coumnrelations -> {
      JSONArray eachRelation = new JSONArray(coumnrelations);
      eachRelation.forEach(coumnrelations1 -> {
        JSONObject relations = new JSONObject(coumnrelations1.toString());
        JSONObject target1 = relations.getJSONObject("target");
        columns.add(target1.getString("type"));
      });
    });
    return response;
  }

  public ArrayList<Object> operationalMetaData(String name) throws ServiceException {
    List<String> response = new ArrayList<>();
    JSONArray targetsObj = new JSONArray();
    int columnCount1 = 0;
    String targetName = null;
    List<String> columns1 = new ArrayList<String>();
    ArrayList<Object> columns2 = new ArrayList<>();
    JSONObject targetNames = new JSONObject();
    JSONArray targetsObj1 = new JSONArray();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = getQueryTargetResult(name);
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error(
          "Exception while executing operational MetaData Query: " + e.getMessage().toString());
    }

    response.forEach(rel -> {
      JSONArray targetRelationsGropup = new JSONArray(rel);
      targetRelationsGropup.forEach(targetRelations -> {
        JSONObject targetObjects = new JSONObject(targetRelations.toString());
        String CoRole = null;
        if (targetObjects.has("CoRole")) {
          CoRole = targetObjects.getString("CoRole");
        } else {
          CoRole = targetObjects.getString("coRole");
        }

        if (CoRole.contains("contains")) {
          targetsObj.put(targetObjects);
        }
      });
    });
    List<String> response1 = new ArrayList<>();
    if (!targetsObj.isEmpty()) {
      JSONObject targetObject = targetsObj.getJSONObject(0);
      // String corole=targetObject.getString("CoRole");
      String corole = null;
      if (targetObject.has("CoRole")) {
        corole = targetObject.getString("CoRole");
      } else {
        corole = targetObject.getString("coRole");
      }
      JSONObject target = targetObject.getJSONObject("target");
      String id = target.getString("id");
      String type = target.getString("type");
      targetName = target.getString("name");
      if (corole.contains("contains")) {
        columns1.add(type);
        String query = getQueryIdTargetResult(id);
        response1 = getNodesResponseForMetaQuality1(query);
      }
    }

    response1.forEach(rel -> {
      JSONArray targetRelationsGropup = new JSONArray(rel);
      targetRelationsGropup.forEach(targetRelations -> {
        JSONObject targetObjects = new JSONObject(targetRelations.toString());
        String CoRole = null;
        if (targetObjects.has("CoRole")) {
          CoRole = targetObjects.getString("CoRole");
        } else {
          CoRole = targetObjects.getString("coRole");
        }

        if (CoRole.contains("contains")) {
          targetsObj1.put(targetObjects);
        }
      });
    });

    if (!targetsObj1.isEmpty()) {
      JSONObject targetObject1 = targetsObj1.getJSONObject(0);
      String corole1 = null;
      if (targetObject1.has("CoRole")) {
        corole1 = targetObject1.getString("CoRole");
      } else {
        corole1 = targetObject1.getString("coRole");
      }
      JSONObject target1 = targetObject1.getJSONObject("target");
      String id1 = target1.getString("id");
      String type1 = target1.getString("type");
      targetName = target1.getString("name");
      if (corole1.contains("contains")) {
        columns1.add(type1);
        String query1 = getQueryIdTargetResult(id1);
        response1 = getNodesResponseForMetaQuality1(query1);
      }
    }
    targetNames.put("ContentType", targetName);
    if (targetNames.isEmpty()) {
      targetNames.put("ContentType", "null");
    }
    columnCount1 = columns1.size();
    String s = Integer.toString(columnCount1);
    targetNames.put("NumberOfElements", s);
    columns2.add(targetNames.toMap());
    return columns2;
  }

  public HashMap<String, Object> MetaCollection(String id) throws ServiceException {
    List<String> response = new ArrayList<>();
    HashMap<String, Object> metaCollectionInfo = new HashMap<>();
    List<Object> BusinessMetadata = new ArrayList<>();
    List<Object> GovernanceMetadata = new ArrayList<>();
    JSONObject governanceMetadata = new JSONObject();
    JSONObject businessMetadata = new JSONObject();

    ArangoCursor<String> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = getQueryResult(id);
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while MetaCollection : " + e.getMessage().toString());
    }
    response.forEach(nodes -> {
      JSONObject nodesInfo = new JSONObject(nodes);

      JSONArray attributeInfo = nodesInfo.getJSONArray("attributes");
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
          // if(attributesname.equals("Shoppable") && attributesname.equals("Searchable") ||
          // attributesname.equals("searchable")) {
          // logger.info("avoid to add attribute");
          // }else
          // {
          // nodesinfo1.put(attributesname, attributesvalue);
          // }
          if (attributes.get("name").toString().equals("Shoppable")
              || attributes.get("name").toString().equals("Searchable")
              || attributes.get("name").toString().equals("searchable")) {
            logger.info("avoid to add attribute");
          } else {
            businessMetadata.put(attributesname, attributesvalue);
          }

        }

      });

      JSONObject edges = nodesInfo.getJSONObject("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      JSONArray sourceedges = edges.getJSONArray("sources");
      targetedges.forEach(targetObject -> {
        JSONObject targetInfo = new JSONObject(targetObject.toString());
        JSONObject target1 = targetInfo.getJSONObject("target");
        String Id = target1.getString("id");
        String tname = target1.getString("name");
        String query1 = getQueryResult(Id);
        List<String> response1 = getNodesResponseForMetaQuality1(query1);
        response1.forEach(nodes1 -> {
          JSONObject nodesInfo1 = new JSONObject(nodes1);
          logger.info("nodesInfo1" + nodesInfo1);
          JSONObject metaCollection1 = nodesInfo1.getJSONObject("type");
          if (metaCollection1.has("metaCollectionName")) {
            String metaCollectionName1 = metaCollection1.getString("metaCollectionName");
            String name1 = metaCollection1.getString("name");
            if (metaCollectionName1.contains("Business")
                || metaCollectionName1.contains("LogicalData")
                || metaCollectionName1.contains("Governance")) {
              businessMetadata.put(name1, tname);

            } else if (metaCollectionName1.contains("Governance")) {
              governanceMetadata.put(name1, tname);
            }
          }
        });

      });

      sourceedges.forEach(sourceObject -> {
        JSONObject sourceInfo = new JSONObject(sourceObject.toString());
        JSONObject source1 = sourceInfo.getJSONObject("source");
        String Id = source1.getString("id");
        String sname = source1.getString("name");
        String query1 = getQueryResult(Id);
        List<String> response1 = getNodesResponseForMetaQuality1(query1);
        response1.forEach(nodes1 -> {
          JSONObject nodesInfo1 = new JSONObject(nodes1);
          logger.info("nodesInfo1" + nodesInfo1);
          JSONObject metaCollection1 = nodesInfo1.getJSONObject("type");
          if (metaCollection1.has("metaCollectionName")) {
            String metaCollectionName1 = metaCollection1.getString("metaCollectionName");
            String name1 = metaCollection1.getString("name");
            if (metaCollectionName1.contains("Business")
                || metaCollectionName1.contains("LogicalData")
                || metaCollectionName1.contains("Governance")) {
              businessMetadata.put(name1, sname);

            } else if (metaCollectionName1.contains("Governance")) {
              governanceMetadata.put(name1, sname);
            }
          }
        });
      });

    });
    ArrayList<Object> nodesList1 = new ArrayList<>();
    ArrayList<Object> nodesList2 = new ArrayList<>();
    Set<String> k = businessMetadata.keySet();
    nodesList1.addAll(k);
    Set<String> l = governanceMetadata.keySet();
    nodesList2.addAll(l);
    BusinessMetadata.add(businessMetadata);
    GovernanceMetadata.add(governanceMetadata);
    metaCollectionInfo.put("BusinessMetadata", BusinessMetadata);
    metaCollectionInfo.put("GovernanceMetadata", GovernanceMetadata);
    metaCollectionInfo.put("BusinessNodeKeys", nodesList1);
    metaCollectionInfo.put("GovernanceNodeKeys", nodesList2);

    return metaCollectionInfo;
  }

  public HashMap<String, ArrayList<Object>> getVersions(String name) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    JSONObject VersionsCount = new JSONObject();
    ArrayList<Object> nodesList = new ArrayList<>();
    HashMap<String, ArrayList<Object>> nodesearchInfo = new HashMap<>();

    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query =
        "FOR m IN " + viewName + "\r\n" + "FILTER m.name == '" + name + "'\r\n" + "RETURN m";
    logger.info("query----->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getVersions : " + e.getMessage().toString());
    }
    nodesearchInfo.put("nodesearchInfo", tailView(response));

    int counters = 0;
    for (int i = 1; i <= tailView(response).size(); i++) {
      counters = counters + 1;
    }
    VersionsCount.put("count", counters);
    nodesList.add(VersionsCount.toMap());
    nodesearchInfo.put("Count", nodesList);
    return nodesearchInfo;
  }

  public HashMap<String, ArrayList<Object>> getmoreFromUser(String userName)
      throws ServiceException {
    List<HashMap> response = new ArrayList<>();
    JSONObject VersionsCount = new JSONObject();
    ArrayList<Object> nodesList = new ArrayList<>();
    HashMap<String, ArrayList<Object>> nodesearchInfo = new HashMap<>();

    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in " + viewName + "\r\n" + "filter doc.createdByFullName == '"
        + userName + "'\r\n" + "return doc";

    // logger.info("query----->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getmoreFromUser : " + e.getMessage().toString());
    }
    logger.info("response" + response);
    nodesearchInfo.put("nodesearchInfo", tailView(response));
    // int counters = 0;
    // for(int i=1;i<=tailView(response).size();i++)
    // {
    // counters = counters + 1;
    // }
    //
    // VersionsCount.put("count", counters);
    // nodesList.add(VersionsCount.toMap());
    // nodesearchInfo.put("Count", nodesList);

    return nodesearchInfo;
  }

  public ArrayList<Object> tailView(List<HashMap> response) throws JSONException {
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();
    JSONObject nodesinfo2 = new JSONObject();
    HashMap<String, ArrayList<Object>> NodeInfo = new HashMap<>();
    HashMap<String, Object> NodeInfo1 = new HashMap<>();
    // ArrayList<Object> nodesList= new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    List<String> columns3 = new ArrayList<String>();
    List<String> columns4 = new ArrayList<String>();
    List<String> columns5 = new ArrayList<String>();
    List<String> columns6 = new ArrayList<String>();
    List<String> columns7 = new ArrayList<String>();
    List<String> columns8 = new ArrayList<String>();
    List<String> columns9 = new ArrayList<String>();
    List<String> columns10 = new ArrayList<String>();
    List<HashMap> response1 = new ArrayList<>();
    response.forEach(nodesinfo -> {
      JSONObject nodes = new JSONObject(nodesinfo);
      String str = nodes.getString("displayName");
      // String str=nodes.get("displayName").toString();
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("displayName", str);
      String label = nodes.getString("identifier");
      String[] label1 = label.split(">");
      if (label.contains("curated")) {
        String curated = "true";
        nodesinfo1.put("curated", curated);
        columns10.add(curated);
      } else {
        String curated = "false";
        nodesinfo1.put("curated", curated);
        columns10.add(curated);
      }
      String nodeRatingsCount = nodes.get("ratingsCount").toString();
      nodesinfo1.put("ratingsCount", nodeRatingsCount);
      String avgRating = nodes.get("avgRating").toString();
      nodesinfo1.put("avgRating", avgRating);
      columns3.add(avgRating);
      String identifier = nodes.getString("identifier");
      nodesinfo1.put("identifier", identifier);
      String Id = nodes.getString("id");
      nodesinfo1.put("id", Id);
      String key = nodes.getString("_key");
      nodesinfo1.put("key", key);
      if (nodes.has("createdOn")) {
        String createdOn = nodes.getString("createdOn");
        // long m = Long.parseLong(createdOn);
      }
      // String Date1=LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(m)),
      // ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
      if (nodes.has("createdByFullName")) {
        String createdByFullName = nodes.getString("createdByFullName");
        nodesinfo1.put("createdByFullName", createdByFullName);
      }
      JSONObject nodetype = nodes.getJSONObject("type");
      String typeName = nodetype.getString("name");
      nodesinfo1.put("type", typeName);
      columns1.add(typeName);
      if (nodes.has("responsibilities")) {
        JSONArray responsibilities = nodes.getJSONArray("responsibilities");
        if (!responsibilities.isEmpty()) {
          responsibilities.forEach(eachsource -> {
            JSONObject responsibility = new JSONObject(eachsource.toString());
            String roleName = responsibility.getString("roleName");
            String responsibilityName = responsibility.getString("name");
            nodesinfo1.put(roleName.trim(), responsibilityName);

          });
        } else {
          nodesinfo1.put("Owner", "No Owner");
        }
      }
      if (nodes.has("status")) {
        JSONObject nodestatus = nodes.getJSONObject("status");
        String statusName = nodestatus.getString("name");
        nodesinfo1.put("status", statusName);
        columns2.add(statusName);
      }
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
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
              if (typeName.contains("Data Set") || typeName.contains("Schema")) {
                columns9.add(value);
              }

            }
            if (attributes.get("name").toString().equals("Frequency")) {
              String value = attributes.getString("value");
              nodesinfo1.put("frequency", value);
              // nodesinfo2.put("Frequency",value);
              if (typeName.contains("Data Set") || typeName.contains("Schema")) {
                columns5.add(value);
              }

            }
            if (attributes.get("name").toString().equals("LastModifiedOn")) {
              String value = attributes.getString("value");
//              long l = Long.parseLong(value);
//              String Date = LocalDateTime
//                  .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
//                  .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
              nodesinfo1.put("lastModifiedOn", value);
              nodesinfo2.put("lastModifiedOn", value);
              if (typeName.contains("Data Set") || typeName.contains("Schema")) {
                columns6.add(value);
              }

            }
            if (attributes.get("name").toString().equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              nodesinfo1.put("personallyIdentifiableInformation", value);

            }
            if (attributes.get("name").toString().equals("Security Classification")) {
              String value = attributes.getString("value");
              nodesinfo1.put("securityClassification", value);

            }
            if (attributes.get("name").toString().equals("tag")) {
              String value = attributes.getString("value");
              nodesinfo1.put("tag", value);
              columns4.add(value);

            }
            if (attributes.get("name").toString().equals("Passing Fraction")) {
              String value = attributes.getString("value");
              nodesinfo1.put("passingFraction", value);
            }
            if (attributes.get("name").toString().equals("Shoppable")) {
              String value = attributes.getString("value");
              nodesinfo1.put("shopable", value);
            }
            if (attributes.get("name").toString().equals("searchable")) {
              String value = attributes.getString("value");
              nodesinfo1.put("searchable", value);
            }
            if (attributes.get("name").toString().equals("url")) {
              String value = attributes.getString("value");
              nodesinfo1.put("url", value);
            }
          }
        });
      } else if (attributeInfo.isEmpty()) {
        nodesinfo1.put("No Attributes", " Attributes not available ");
      }
      JSONArray targetsObj = new JSONArray();
      JSONArray sourceObj = new JSONArray();
      JSONObject edges = (JSONObject) nodes.get("relations");
      logger.info("edges" + edges);
      JSONArray targetedges = edges.getJSONArray("targets");
      logger.info("targets" + targetedges);
      JSONArray sourceedges = edges.getJSONArray("sources");
      logger.info("sourceedges" + sourceedges);
      if (nodes.has("sourceProduct")) {
        String sourceProduct = nodes.getString("sourceProduct");
        nodesinfo1.put("sourceSystem", sourceProduct);
      } else if (nodes.has("sourceCatalog")) {
        String sourceCatalog = nodes.getString("sourceCatalog");
        nodesinfo1.put("sourceSystem", sourceCatalog);
      } else {
        if (!targetedges.isEmpty()) {
          targetedges.forEach(eachsource -> {
            JSONObject targets = new JSONObject(eachsource.toString());
            String targetname = null;
            if (targets.has("CoRole")) {
              targetname = targets.getString("CoRole");
            } else {
              targetname = targets.getString("coRole");
            }
            if (targetname.contains("contains") || targetname.contains("represents")) {
              targetsObj.put(targets);
            }
          });

          if (!sourceedges.isEmpty()) {
            sourceedges.forEach(eachsource -> {
              JSONObject sources = new JSONObject(eachsource.toString());
              String source = sources.getString("role");
              if (source.contains("produce")) {
                sourceObj.put(sources);
              }
            });
          }
          if (!targetsObj.isEmpty()) {
            JSONObject targetedges1 = targetsObj.getJSONObject(0);
            JSONObject target1 = targetedges1.getJSONObject("target");
            // String targetname=targetedges1.getString("CoRole");
            String targetname = null;
            if (targetedges1.has("CoRole")) {
              targetname = targetedges1.getString("CoRole");
            } else {
              targetname = targetedges1.getString("coRole");
            }
            String targetid = target1.getString("id");
            String query1 = getQueryResult(targetid);
            String result = null;
            if (targetname.contains("contains") || targetname.contains("represents")) {
              result = getNodesResponse(query1);
              nodesinfo1.put("sourceSystem", result);

            } else {
              nodesinfo1.put("sourceSystem", "null");
            }
          } else if (!sourceObj.isEmpty()) {

            JSONObject sourceedges1 = sourceObj.getJSONObject(0);
            String result = null;

            JSONObject source1 = sourceedges1.getJSONObject("source");
            String sourcename = sourceedges1.getString("role");
            String sourceid = source1.getString("id");
            String query1 = getQueryResult(sourceid);

            if (sourcename.contains("produce")) {
              result = getNodesResponse(query1);
              nodesinfo1.put("sourceSystem", result);

            } else {
              nodesinfo1.put("sourceSystem", "null");
            }
          } else {
            nodesinfo1.put("sourceSystem", "null");
          }
        } else {
          nodesinfo1.put("sourceSystem", "null");
        }
      }
      nodesinfo1.put("qualityScore", getScore(str));
      nodesinfo1.put("metaQualityScore", metaQualityScore(str));
      nodesinfo1.put("count", aql.favoriteNodesCount(key).get(0));
      ArrayList<String> LineOfBusiness = getTypes(str);
      ArrayList<String> DataDomain = getTypes1(str);
      nodesinfo1.put("lineOfBusiness", getTypes(str));
      columns7.addAll(LineOfBusiness);
      nodesinfo1.put("dataDomain", getTypes1(str));
      columns8.addAll(DataDomain);
      nodesList.add(nodesinfo1.toMap());
    });
    NodeInfo.put("nodeInfo", nodesList);

    response1.add(NodeInfo);

    // storeFilterSearchValue(response1);
    // storeFilterSearchValue(nodesinfo1);
   /* storeSearchType(columns1, columns2, columns3, columns4, columns5, columns6,
        (ArrayList<String>) columns7, columns8, columns9, columns10);*/
    return nodesList;
  }

  public ArrayList<Object> listViewresponse(List<HashMap> response) throws JSONException {

    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();
    JSONObject nodesinfo2 = new JSONObject();
    response.forEach(nodesinfo -> {
      JSONObject nodes = new JSONObject(nodesinfo);
      String str = nodes.getString("displayName");
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("displayName", str);
      String Id = nodes.getString("id");
      nodesinfo1.put("id", Id);
      JSONObject nodetype = nodes.getJSONObject("type");
      String typeName = nodetype.getString("name");
      nodesinfo1.put("type", typeName);
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
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
          }
        });
      }

      nodesList.add(nodesinfo1.toMap());
    });
    return nodesList;
  }

  public ArrayList<Object> listView(List<String> columIds) throws JSONException {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();

    for (int i = 0; i < columIds.size(); i++) {
      columns.add(columIds.get(i));
    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
    }

    for (int i = 0; i < columns1.size(); i++) {
      // columns.add("'" + columNames.get(i) + "'");
      columns2.add("node.id == '" + columns1.get(i) + "'");
      // logger.info("columns-->"+columns);

    }

    String columnIds1 = String.join(" OR ", columns2);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1 + "\r\n"
        + "return node";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while listView : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject nodes = new JSONObject(res1);
      String str = nodes.getString("displayName");
      // String str1="<b>"+str+"</b>";
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
          }
        });
      }
      nodesList.add(nodesinfo1.toMap());
    });
    return nodesList;

  }

  public ArrayList<Object> graphtailView(List<String> columIds) throws JSONException {

    List<String> response = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();

    for (int i = 0; i < columIds.size(); i++) {
      columns.add(columIds.get(i));
    }
    HashSet<String> hSetNumbers = new HashSet(columns);

    for (String strNumber : hSetNumbers) {
      columns1.add(strNumber);
    }

    for (int i = 0; i < columns1.size(); i++) {
      // columns.add("'" + columNames.get(i) + "'");
      columns2.add("node.id == '" + columns1.get(i) + "'");
      // logger.info("columns-->"+columns);

    }

    String columnIds1 = String.join(" OR ", columns2);
    logger.info("columnIds1" + columnIds1);
    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "For node in " + nodesCollection + "\r\n" + "FILTER " + columnIds1 + "\r\n"
        + "return node";
    logger.info("query----->" + query);
    try {

      cursor1 = arangoDB.query(query, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while graphtailView : " + e.getMessage().toString());
    }
    logger.info("response1" + response1);
    response1.forEach(res1 -> {
      JSONObject nodes = new JSONObject(res1);
      String str = nodes.getString("displayName");
      String strName = nodes.getString("name");
      // String str=nodes.get("displayName").toString();
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("displayName", str);
      nodesinfo1.put("name", strName);
      String label = nodes.getString("identifier");
      String[] label1 = label.split(">");
      if (label.contains("curated")) {
        String curated = "true";
        nodesinfo1.put("curated", curated);

      } else {
        String curated = "false";
        nodesinfo1.put("curated", curated);

      }
      String nodeRatingsCount = nodes.get("ratingsCount").toString();
      nodesinfo1.put("ratingsCount", nodeRatingsCount);
      String avgRating = nodes.get("avgRating").toString();
      nodesinfo1.put("avgRating", avgRating);

      String identifier = nodes.getString("identifier");
      nodesinfo1.put("identifier", identifier);
      String Id = nodes.getString("id");
      nodesinfo1.put("id", Id);
      if (nodes.has("createdOn")) {
        String createdOn = nodes.getString("createdOn");
        // long m = Long.parseLong(createdOn);
      }
      // String Date1=LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(m)),
      // ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
      if (nodes.has("createdByFullName")) {
        String createdByFullName = nodes.getString("createdByFullName");
        nodesinfo1.put("createdByFullName", createdByFullName);
      }
      JSONObject nodetype = nodes.getJSONObject("type");
      String typeName = nodetype.getString("name");
      nodesinfo1.put("type", typeName);
      columns1.add(typeName);
      if (nodes.has("responsibilities")) {
        JSONArray responsibilities = nodes.getJSONArray("responsibilities");
        if (!responsibilities.isEmpty()) {
          responsibilities.forEach(eachsource -> {
            JSONObject responsibility = new JSONObject(eachsource.toString());
            String roleName = responsibility.getString("roleName");
            String responsibilityName = responsibility.getString("name");
            nodesinfo1.put(roleName.trim(), responsibilityName);

          });
        }
      }
      if (nodes.has("status")) {
        JSONObject nodestatus = nodes.getJSONObject("status");
        String statusName = nodestatus.getString("name");
        nodesinfo1.put("status", statusName);
        columns2.add(statusName);
      }
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
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
            if (attributes.get("name").toString().equals("Frequency")) {
              String value = attributes.getString("value");
              nodesinfo1.put("frequency", value);
              // nodesinfo2.put("Frequency",value);

            }
            if (attributes.get("name").toString().equals("LastModifiedOn")) {
              String value = attributes.getString("value");
//                  long l = Long.parseLong(value);
//                  String Date = LocalDateTime
//                      .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
//                      .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
              nodesinfo1.put("lastModifiedOn", value);


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
            if (attributes.get("name").toString().equals("tag")) {
              String value = attributes.getString("value");
              nodesinfo1.put("tag", value);


            }
            if (attributes.get("name").toString().equals("Passing Fraction")) {
              String value = attributes.getString("value");
              nodesinfo1.put("passingFraction", value);
            }
          }
        });
      } else if (attributeInfo.isEmpty()) {
        nodesinfo1.put("no Attributes", " Attributes not available ");
      }
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

          if (targetname.contains("contains") || targetname.contains("represents")) {
            targetsObj.put(targets);
          }
        });
        if (!targetsObj.isEmpty()) {
          JSONObject targetedges1 = targetsObj.getJSONObject(0);
          String result = null;
          JSONObject target1 = targetedges1.getJSONObject("target");
          String targetname = null;
          if (targetedges1.has("CoRole")) {
            targetname = targetedges1.getString("CoRole");
          } else {
            targetname = targetedges1.getString("coRole");
          }

          String targetid = target1.getString("id");
          String query1 = getQueryResult(targetid);

          if (targetname.contains("contains") || targetname.contains("represents")) {
            result = getNodesResponse(query1);
            nodesinfo1.put("sourceSystem", result);

          } else {
            nodesinfo1.put("sourceSystem", "null");
          }
        }
      } else {
        nodesinfo1.put("sourceSystem", "null");
      }
      nodesinfo1.put("qualityScore", getScore(str));
      nodesinfo1.put("metaQualityScore", metaQualityScore(str));

      ArrayList<String> LineOfBusiness = getTypes(str);
      ArrayList<String> DataDomain = getTypes1(str);
      nodesinfo1.put("lineOfBusiness", getTypes(str));

      nodesinfo1.put("dataDomain", getTypes1(str));

      nodesList.add(nodesinfo1.toMap());
    });
    return nodesList;

  }

  public List<HashMap> getTagsInfo() throws ServiceException {
    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, arangomySearchTagsCollection);
    String query = "for doc in " + viewName + "\r\n"
        + "filter FIRST(doc.attributes[* FILTER CURRENT.name == 'tag'])\r\n" + "return doc";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error(
          "Exception while retrieving Data in tags Results: " + e.getMessage().toString());
    }
    tagsTailView(response);
    return response;
  }

  public ArrayList<Object> tagsTailView(List<HashMap> response) throws JSONException {
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();
    JSONObject nodesinfo2 = new JSONObject();
    response.forEach(nodeinfo -> {
      JSONObject nodes = new JSONObject(nodeinfo);
      String label = nodes.getString("identifier");
      String str = nodes.getString("displayName");
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("displayName", str);
      String[] label1 = label.split(">");
      logger.info(String.valueOf(label1));
      if (label.contains("curated")) {
        String curated = "true";
        nodesinfo1.put("curated", curated);
      } else {
        String curated = "false";
        nodesinfo1.put("curated", curated);
      }
      String nodeRatingsCount = nodes.get("ratingsCount").toString();
      nodesinfo1.put("ratingsCount", nodeRatingsCount);
      String avgRating = nodes.get("avgRating").toString();
      nodesinfo1.put("avgRating", avgRating);
      String identifier = nodes.getString("identifier");
      nodesinfo1.put("identifier", identifier);
      String Id = nodes.getString("id");
      nodesinfo1.put("id", Id);
      if (nodes.has("createdOn")) {
        String createdOn = nodes.getString("createdOn");
        // long m = Long.parseLong(createdOn);
      }
      // String Date1=LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(m)),
      // ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
      if (nodes.has("createdByFullName")) {
        String createdByFullName = nodes.getString("createdByFullName");
        nodesinfo1.put("createdByFullName", createdByFullName);
      }
      JSONObject nodetype = nodes.getJSONObject("type");
      String typeName = nodetype.getString("name");
      nodesinfo1.put("type", typeName);
      if (nodes.has("responsibilities")) {
        JSONArray responsibilities = nodes.getJSONArray("responsibilities");
        if (!responsibilities.isEmpty()) {
          responsibilities.forEach(eachsource -> {
            JSONObject responsibility = new JSONObject(eachsource.toString());
            String roleName = responsibility.getString("roleName");
            String responsibilityName = responsibility.getString("name");
            nodesinfo1.put(roleName.trim(), responsibilityName);
          });
        }
      }
      if (nodes.has("status")) {
        JSONObject nodestatus = nodes.getJSONObject("status");
        String statusName = nodestatus.getString("name");
        nodesinfo1.put("status", statusName);
      }
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
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
            if (attributes.get("name").toString().equals("Frequency")) {
              String value = attributes.getString("value");
              nodesinfo1.put("frequency", value);
            }
            if (attributes.get("name").toString().equals("LastModifiedOn")) {
              String value = attributes.getString("value");
//              long l = Long.parseLong(value);
//              String Date = LocalDateTime
//                  .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
//                  .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
              nodesinfo1.put("lastModifiedOn", value);
              nodesinfo2.put("lastModifiedOn", value);
            }
            if (attributes.get("name").toString().equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              nodesinfo1.put("personallyIdentifiableInformation", value);
            }
            if (attributes.get("name").toString().equals("Security Classification")) {
              String value = attributes.getString("value");
              nodesinfo1.put("securityClassification", value);
            }
            if (attributes.get("name").toString().equals("tag")) {
              String value = attributes.getString("value");
              nodesinfo1.put("tag", value);
            }
            if (attributes.get("name").toString().equals("Passing Fraction")) {
              String value = attributes.getString("value");
              nodesinfo1.put("passingFraction", value);
            }
          }
        });
      } else if (attributeInfo.isEmpty()) {
        nodesinfo1.put("no Attributes", " Attributes not available ");
      }
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
          if (targetname.contains("contains") || targetname.contains("represents")) {
            targetsObj.put(targets);
          }
        });
        if (!targetsObj.isEmpty()) {
          JSONObject targetedges1 = targetsObj.getJSONObject(0);
          String result = null;
          JSONObject target1 = targetedges1.getJSONObject("target");
          String targetname = null;
          if (targetedges1.has("CoRole")) {
            targetname = targetedges1.getString("CoRole");
          } else {
            targetname = targetedges1.getString("coRole");
          }
          String targetid = target1.getString("id");
          String query1 = getQueryResult(targetid);

          if (targetname.contains("contains") || targetname.contains("represents")) {
            result = getNodesResponse(query1);
            nodesinfo1.put("sourceSystem", result);
          } else {
            nodesinfo1.put("sourceSystem", "null");
          }
        }
      } else {
        nodesinfo1.put("sourceSystem", "null");
      }
      nodesinfo1.put("qualityScore", getScore(str));
      nodesinfo1.put("metaQualityScore", metaQualityScore(str));
      nodesList.add(nodesinfo1.toMap());
    });
    importDocuments2Arango1(nodesList, arangomySearchTagsCollection);
    return nodesList;

  }

  @SuppressWarnings("rawtypes")
  public HashMap<String, ArrayList<Object>> getmoreFromsourceSystem(String name)
      throws ServiceException {
    List<HashMap> response = new ArrayList<>();
    JSONObject VersionsCount = new JSONObject();
    ArrayList<Object> nodesList = new ArrayList<>();
    HashMap<String, ArrayList<Object>> nodesearchInfo = new HashMap<>();

    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String query = getQueryNodeResult(sourceName);
    final String query = "For node in " + viewName + "\r\n" + "filter node.name == '" + name
        + "'\r\n" + "return node";
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getmoreFromsourceSystem : " + e.getMessage().toString());
    }
    response.forEach(nodes -> {
      JSONObject nodesInfo = new JSONObject(nodes);
      JSONObject edges = nodesInfo.getJSONObject("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      targetedges.forEach(targetObject -> {
        JSONObject targetInfo = new JSONObject(targetObject.toString());
        JSONObject target1 = targetInfo.getJSONObject("target");
        String tname = target1.getString("name");
        String coRole = null;
        if (targetInfo.has("CoRole")) {
          coRole = targetInfo.getString("CoRole");
        } else {
          coRole = targetInfo.getString("coRole");
        }

        if (coRole.contains("hosted in") || coRole.contains("contains")
            || coRole.contains("is part of")) {
          String res = getQueryNodeResult(tname);
          logger.info("res" + res);
          List<HashMap> response1 = getNodesResponseForMetaQuality(res);
          logger.info("response1" + response1);
          nodesearchInfo.put("nodesearchInfo", tailView(response1));
          // int counters = 0;
          // for(int i=1;i<=tailView(response1).size();i++)
          // {
          // counters = counters + 1;
          // }
          // VersionsCount.put("count", counters);
        } else {
          nodesearchInfo.put("nodesearchInfo", null);
        }

      });
    });

    // nodesList.add(VersionsCount.toMap());
    // nodesearchInfo.put("Count", nodesList);
    return nodesearchInfo;
  }

  public List<HashMap> getprivacyAndRisks(String name) throws ServiceException {
    ArrayList<Object> nodesList1 = new ArrayList<>();
    List<String> response = new ArrayList<>();
    HashMap<String, Set<String>> PrivacyAndRiskInfo2 = new HashMap<>();
    HashMap<String, ArrayList<Object>> PrivacyAndRiskInfo = new HashMap<>();
    List<HashMap> response2 = new ArrayList<>();
    HashMap<String, Integer> count = new HashMap<String, Integer>();
    Set<String> keycolumns1 = new HashSet<String>();
    ArangoCursor<String> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query = "For node in " + nodesCollection + "\r\n" + "filter node.name == '" + name
        + "'\r\n" + "return node";
    logger.info("query----->" + query);
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getprivacyAndRisks : " + e.getMessage().toString());
    }
    response.forEach(node -> {
      JSONObject nodeInfo = new JSONObject(node.toString());
      JSONObject nodeInfo1 = nodeInfo.getJSONObject("type");
      if (nodeInfo.has("metaCollectionName")) {
        String typename = nodeInfo1.getString("metaCollectionName");

        Set<String> k;
        if (typename.contains("Data Set") || typename.contains("Table")) {
          nodesList1.add(getprivacyAndRisk(name).toMap());
          k = getprivacyAndRisk(name).keySet();
        } else {
          nodesList1.add(getprivacyAndRisk1(name).toMap());
          k = getprivacyAndRisk1(name).keySet();
        }
        keycolumns1.addAll(k);
      }
    });
    PrivacyAndRiskInfo2.put("Nodekeys", keycolumns1);
    PrivacyAndRiskInfo.put("PrivacyAndRiskInfo", nodesList1);
    int counter = 0;
    for (int i = 1; i <= nodesList1.size(); i++) {
      counter = counter + 1;
    }
    count.put("count", counter);
    response2.add(PrivacyAndRiskInfo);
    response2.add(PrivacyAndRiskInfo2);
    response2.add(count);

    return response2;

  }

  public JSONObject getprivacyAndRisk(String name) throws ServiceException {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();
    int i = 1;

    ArangoCursor<String> cursor = null;
    ArangoCursor<String> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "filter node.name == '" + name
        + "'\r\n" + "return node";
    logger.info("query----->" + query);
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getprivacyAndRisks  " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    response.forEach(node -> {
      JSONObject nodeInfo = new JSONObject(node.toString());
      String str = nodeInfo.getString("displayName");
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("DisplayName", str);
      JSONArray attributeInfo = nodeInfo.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
          if (!attributes.isEmpty()) {
            if (attributes.get("name").toString().equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              nodesinfo1.put("PII", value);
            }
            if (attributes.get("name").toString().equals("Security Classification")) {
              String value = attributes.getString("value");
              nodesinfo1.put("SecurityClassification", value);
            }
          }
        });
      } else if (attributeInfo.isEmpty()) {
        nodesinfo1.put("No Attributes", " Attributes not available ");
      }
      JSONObject edges = nodeInfo.getJSONObject("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      targetedges.forEach(coumnrelations1 -> {
        JSONObject relations = new JSONObject(coumnrelations1.toString());
        JSONObject target1 = relations.getJSONObject("target");

        String corole = null;
        if (relations.has("CoRole")) {
          corole = relations.getString("CoRole");
        } else {
          corole = relations.getString("coRole");
        }
        if (corole.contains("contains")) {
          columns.add("node.id == '" + target1.getString("id").toString() + "'");
        }
      });
    });
    String columnIds = String.join(" OR ", columns);
    String query1 = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds + "\r\n"
        + "return node";
    try {
      cursor1 = arangoDB.query(query1, String.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error(
          "Exception while getprivacyAndRisks_2 : " + e.getMessage().toString());
    }
    response1.forEach(asstInfo -> {
      JSONObject eachObject = new JSONObject(asstInfo);
      JSONArray attributeInfo = eachObject.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
          if (!attributes.isEmpty()) {
            if (attributes.getString("name").equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              if (value.contains("true")) {
                columns1.add(value);
              }
            }
          }
        });

      }
    });
    int columnCount1 = columns1.size();
    if (columnCount1 > i) {
      nodesinfo1.put("toxic", "true");
    } else {
      nodesinfo1.put("toxic", "false");
    }
    nodesinfo1.put("PCI", "NotAvailable");
    nodesinfo1.put("HIPAA", "NotAvailable");
    nodesinfo1.put("SensitiveDatatype", "NotAvailable");
    nodesList.add(nodesinfo1.toMap());

    return nodesinfo1;
  }

  public JSONObject getprivacyAndRisk1(String name) throws ServiceException {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    ArrayList<Object> nodesList = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();
    int i = 1;

    ArangoCursor<String> cursor = null;
    ArangoCursor<String> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "For node in " + nodesCollection + "\r\n" + "filter node.name == '" + name
        + "'\r\n" + "return node";
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));
    } catch (Exception e) {
      log.error("Exception while getprivacyAndRisk1 : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    List<String> columns3 = new ArrayList<String>();
    response.forEach(node -> {
      JSONObject nodeInfo = new JSONObject(node.toString());
      String str = nodeInfo.getString("displayName");
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("DisplayName", str);
      JSONArray attributeInfo = nodeInfo.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
          if (!attributes.isEmpty()) {
            if (attributes.get("name").toString().equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              nodesinfo1.put("PII", value);
            }
            if (attributes.get("name").toString().equals("Security Classification")) {
              String value = attributes.getString("value");
              nodesinfo1.put("SecurityClassification", value);
            }
          }
        });
      } else if (attributeInfo.isEmpty()) {
        nodesinfo1.put("No Attributes", " Attributes not available ");
      }
      JSONObject edges = nodeInfo.getJSONObject("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      targetedges.forEach(coumnrelations1 -> {
        JSONObject relations = new JSONObject(coumnrelations1.toString());
        JSONObject target1 = relations.getJSONObject("target");

        String corole = null;
        if (relations.has("CoRole")) {
          corole = relations.getString("CoRole");
        } else {
          corole = relations.getString("coRole");
        }

        if (corole.contains("is part of")) {
          columns.add("node.id == '" + target1.getString("id").toString() + "'");
        }
      });
    });

    String columnIds = String.join(" OR ", columns);
    String query1 = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds + "\r\n"
        + "return node";
    try {
      cursor1 = arangoDB.query(query1, String.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error(
          "Exception while getprivacyAndRisk1_2 : " + e.getMessage().toString());
    }
    response1.forEach(nodeInfo -> {
      JSONObject eachObject = new JSONObject(nodeInfo.toString());
      JSONObject edges = eachObject.getJSONObject("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      targetedges.forEach(coumnrelations1 -> {
        JSONObject relations = new JSONObject(coumnrelations1.toString());
        JSONObject target1 = relations.getJSONObject("target");
        String corole = null;
        if (relations.has("CoRole")) {
          corole = relations.getString("CoRole");
        } else {
          corole = relations.getString("coRole");
        }
        if (corole.contains("is part of")) {
          columns1.add("node.id == '" + target1.getString("id").toString() + "'");
        }
      });
    });
    String columnIds1 = String.join(" OR ", columns1);
    String query2 = "For node in " + nodesCollection + "\r\n" + "filter " + columnIds1 + "\r\n"
        + "return node";
    try {
      cursor2 = arangoDB.query(query2, String.class);
      response2 = cursor2.asListRemaining();
    } catch (Exception e) {
      log.error(
          "Exception while getprivacyAndRisk1_3 : " + e.getMessage().toString());
    }
    response2.forEach(asstInfo -> {
      JSONObject eachObject = new JSONObject(asstInfo);
      JSONArray attributeInfo = eachObject.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
          if (!attributes.isEmpty()) {
            if (attributes.getString("name").equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              if (value.contains("true")) {
                columns2.add(value);
              }
            }
          }
        });

      }
    });
    columns3.addAll(columns2);
    int columnCount1 = columns3.size();
    if (columnCount1 > i) {
      nodesinfo1.put("toxic", "true");
    } else {
      nodesinfo1.put("toxic", "false");
    }
    nodesinfo1.put("PCI", "NotAvailable");
    nodesinfo1.put("HIPAA", "NotAvailable");
    nodesinfo1.put("SecurityDatatype", "NotAvailable");
    nodesList.add(nodesinfo1.toMap());

    return nodesinfo1;
  }

  public String importDocuments2Arango2(ArrayList<String> nodeTypes, String collectionName,
      String collectionType) throws ServiceException {
    String importResponse = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, collectionName);
    if (arangoCollection != null) {
      arangoCollection.importDocuments(nodeTypes);
      log.info("Documents Inserted Successfully");
      importResponse = "Documents Inserted Successfully";
    } else {
      importResponse = "Failed to Insert Documents";
    }

    return importResponse;
  }

  public List<HashMap> filtersOperations(List<String> name) throws ServiceException {

    savedPreferencesList.clear();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    // columns.add("CONTAINS(data, '" + name.toString() + "')");
    for (int i = 0; i < name.size(); i++) {
      columns1.add("'" + name.get(i) + "'");
      JSONObject savedPreference = new JSONObject();
      savedPreference.put("value", name.get(i));
      savedPreferencesList.add(savedPreference);
      // logger.info("columns-->"+columns1);
    }

    String columnIds1 = String.join(" OR ", columns1);
    logger.info(columnIds1);
    ArangoCursor<HashMap> cursor1 = null;
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query2 = "let status=(FOR d IN " + arangoSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.status)\r\n" + "FOR data IN d.status\r\n" + "FILTER data IN "
        + columns1 + "\r\n" + "RETURN data)\r\n" + "let rating=(\r\n" + "FOR d IN "
        + arangoSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.AvgRating)\r\n"
        + "FOR data IN d.AvgRating\r\n" + "FILTER data IN " + columns1 + "\r\n"
        + "RETURN data\r\n" + ")\r\n" + "return {status:UNIQUE(status),rating:UNIQUE(rating)}";
    logger.info(query2);
    String query = "let status=(FOR d IN " + arangoSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.status)\r\n" + "FOR data IN d.status\r\n" + "FILTER data IN "
        + columns1 + "\r\n" + "RETURN data)\r\n" + "let rating=(\r\n" + "FOR d IN "
        + arangoSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.AvgRating)\r\n"
        + "FOR data IN d.AvgRating\r\n" + "FILTER data IN " + columns1 + "\r\n"
        + "RETURN data\r\n" + ")\r\n" + "let tag=(\r\n" + "FOR d IN "
        + arangoSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.tag)\r\n"
        + "FOR data IN d.tag\r\n" + "FILTER data IN " + columns1 + "\r\n" + "RETURN data\r\n"
        + ")\r\n" + "let curated=(\r\n" + "FOR d IN " + arangoSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.curated)\r\n" + "FOR data IN d.curated\r\n" + "FILTER data IN "
        + columns1 + "\r\n" + "RETURN data\r\n" + ")\r\n" + "let freshness=(\r\n" + "FOR d IN "
        + arangoSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.Freshness)\r\n"
        + "FOR data IN d.Freshness\r\n" + "FILTER data IN " + columns1 + "\r\n"
        + "RETURN data\r\n" + ")\r\n" + "let frequency=(\r\n" + "FOR d IN "
        + arangoSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.Frequency)\r\n"
        + "FOR data IN d.Frequency\r\n" + "FILTER data IN " + columns1 + "\r\n"
        + "RETURN data\r\n" + ")\r\n" + "let nodename=(\r\n" + "FOR d IN "
        + arangoSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.nodename)\r\n"
        + "FOR data IN d.nodename\r\n" + "FILTER data IN " + columns1 + "\r\n"
        + "RETURN data\r\n" + ")\r\n" + "let certified=(\r\n" + "FOR d IN "
        + arangoSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.certified)\r\n"
        + "FOR data IN d.certified\r\n" + "FILTER data IN " + columns1 + "\r\n"
        + "RETURN data\r\n" + ")\r\n"
        + "return {status:UNIQUE(status),rating:UNIQUE(rating),certified:UNIQUE(certified),nodename:UNIQUE(nodename),frequency:UNIQUE(frequency),freshness:UNIQUE(freshness),curated:UNIQUE(curated),tag:UNIQUE(tag)}";
    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while filtersOperations : " + e.getMessage().toString());
    }

    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray ratings = new JSONArray();
      JSONArray status = new JSONArray();
      JSONArray certified = new JSONArray();
      JSONArray nodename = new JSONArray();
      JSONArray frequency = new JSONArray();
      JSONArray freshness = new JSONArray();
      JSONArray curated = new JSONArray();
      JSONArray tag = new JSONArray();
      ratings = nodes.getJSONArray("rating");
      status = nodes.getJSONArray("status");
      certified = nodes.getJSONArray("certified");
      nodename = nodes.getJSONArray("nodename");
      frequency = nodes.getJSONArray("frequency");
      freshness = nodes.getJSONArray("freshness");
      curated = nodes.getJSONArray("curated");
      tag = nodes.getJSONArray("tag");

      if (!ratings.isEmpty()) {
        columns.add("node.AvgRating IN " + ratings + "");
        // savedPreferencesList.add("AvgRating:"+ ratings +"");
        // logger.info("columns"+columns);
        // savedPreference.put("value", ratings);
      }
      if (!status.isEmpty()) {
        columns.add("node.Status IN " + status + "");
        // savedPreferencesList.add("Status:"+ status +"");
        // savedPreference.put("value", status);
      }
      if (!certified.isEmpty()) {
        columns.add("node.Certified IN " + certified + "");
        // savedPreferencesList.add("Certified:"+ certified +"");
        // savedPreference.put("value", certified);
      }
      if (!curated.isEmpty()) {
        columns.add("node.Curated IN " + curated + "");
        // savedPreferencesList.add("Curated:"+ curated +"");
        // savedPreference.put("value", curated);
      }
      if (!tag.isEmpty()) {
        columns.add("node.Tag IN " + tag + "");
        // savedPreferencesList.add("Tag:"+ tag +"");
        // savedPreference.put("value", tag);
      }
      if (!freshness.isEmpty()) {
        columns.add("node.LastModifiedOn IN " + freshness + "");
        // savedPreferencesList.add("LastModifiedOn:"+ freshness +"");
        // savedPreference.put("value", freshness);
      }
      if (!frequency.isEmpty()) {
        columns.add("node.Frequency IN " + frequency + "");
        // savedPreferencesList.add("Frequency:"+ frequency +"");
        // savedPreference.put("value", frequency);
      }
      if (!nodename.isEmpty()) {
        columns.add("node.Type IN " + nodename + "");
        // savedPreferencesList.add("NodeType:"+ nodename +"");
        // savedPreference.put("value", nodename);
      }
    });

    // Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // String datestr = f.format(new Date());
    // savedPreference.put("searchedOn", datestr);
    // savedPreferencesList.add(savedPreference);

    String columnIds = String.join(" AND ", columns);
    logger.info(columnIds);
    // logger.info(columnIds);
    // logger.info(columns);
    // logger.info(filterObject);
    // Object ratingfilter=filterObject.get("rating");
    // logger.info(ratingfilter);
    String query1 = "FOR m IN " + arangoFilterSearchCollection + "\r\n"
        + "for a in m.nodename\r\n" + "filter a.nodeInfo\r\n" + "for node in a.nodeInfo\r\n"
        + "filter " + columnIds + "\r\n" + "return node";
    logger.info("query----->" + query1);
    try {

      cursor1 = arangoDB.query(query1, HashMap.class);

      response1 = cursor1.asListRemaining();


    } catch (Exception e) {
      log.error("Exception while filtersOperations_2: " + e.getMessage().toString());
    }

    return response1;

  }

  public List<HashMap> getsavePreferences(String savePreferencesHolder,
      String savePreferencesHolderId) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    JSONObject savedFilters = new JSONObject();
    ArrayList<JSONObject> savedFiltersList = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, savedPreferences);
    ArangoCollection arangoEdgeCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, savedPreferenceEdge);
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());

    String queryToBeExecuted =
        "for doc in " + savedPreferences + "\r\n" + "filter doc.savePreferencesHolder == '"
            + savePreferencesHolder + "' AND doc.savePreferencesHolderId == '"
            + savePreferencesHolderId + "'\r\n" + "return doc";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsavePreferences : " + e.getMessage().toString());
    }

    if (response.isEmpty()) {
      String queryToBeExecuted1 = "INSERT { savePreferencesHolder: '" + savePreferencesHolder
          + "',savePreferencesHolderId: '" + savePreferencesHolderId + "', Filters:[]} INTO "
          + savedPreferences + "\r\n" + "return NEW._id";
      logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);
      // String q="INSERT { savePreferencesHolder: 'ajita@gmail.com',savePreferencesHolderId:
      // '543210', Filters:[],date:'date'} INTO savedPreferences";
      ArangoCursor<Object> cursor1 = null;
      try {

        cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
        response1 = cursor1.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences_2: " + e.getMessage().toString());
      }
      // savedPreferencesList.add(savedPreference);

      String query2 = "INSERT { _from: '" + response1.get(0) + "',_to: '" + response1.get(0)
          + "', NodeFilters:" + savedPreferencesList + "} INTO savedPreferenceEdge\r\n"
          + "return NEW._key";
      ArangoCursor<Object> cursor2 = null;
      try {

        cursor2 = arangoDB.query(query2, Object.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences_3: " + e.getMessage().toString());
      }

      // int savedFilterId=1;

      savedFilters.put("name", preferenceName.get(0));
      savedFilters.put("displayName", preferenceName.get(0));
      savedFilters.put("id", response2.get(0));
      savedFilters.put("searchedOn", datestr);
      // savedPreference.put("savedFilterId",savedFilterId);
      // logger.info("savedPreference"+savedPreference);
      // savedPreferencesList.add(savedPreference);
      savedFiltersList.add(savedFilters);
      // logger.info("savedPreferencesList"+savedPreferencesList);

      String queryToBeExecuted2 = "for doc in " + savedPreferences + "\r\n"
          + "filter doc.Filters != null AND doc.savePreferencesHolder == '"
          + savePreferencesHolder + "' AND doc.savePreferencesHolderId == '"
          + savePreferencesHolderId + "'\r\n" + "UPDATE doc WITH { Filters: push(doc.Filters,"
          + savedFiltersList.get(0) + ",true) } IN " + savedPreferences + "";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
      try {

        cursor2 = arangoDB.query(queryToBeExecuted2, Object.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences_4 : " + e.getMessage().toString());
      }


    } else {

      String query =
          "for doc in " + savedPreferences + "\r\n" + "filter doc.savePreferencesHolder == '"
              + savePreferencesHolder + "' AND doc.savePreferencesHolderId == '"
              + savePreferencesHolderId + "'\r\n" + "return doc";
      logger.info("queryToBeExecuted----->" + query);

      ArangoCursor<HashMap> qcursor = null;
      try {

        qcursor = arangoDB.query(query, HashMap.class);
        response4 = qcursor.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences_5: " + e.getMessage().toString());
      }
      logger.info("response" + response4);

      int counter = 0;

      String id = null;
      for (int i = 0; i < response4.size(); i++) {

        HashMap list = response.get(i);
        JSONObject nodes1 = new JSONObject(list);
        logger.info("nodes1" + nodes1);
        id = nodes1.getString("_id");
        JSONArray filters = nodes1.getJSONArray("Filters");
        for (int j = 0; j < filters.length(); j++) {
          JSONObject lists = (JSONObject) filters.get(j);
          JSONObject ts = new JSONObject(lists.toString());
          logger.info("ts" + ts);
          String name = ts.get("name").toString();
          logger.info("preferenceName" + preferenceName);
          if (name.equals(preferenceName.get(0))) {
            counter = counter + 1;
          }
        }
      }

      String query2 = "INSERT { _from: '" + id + "',_to: '" + id + "', NodeFilters:"
          + savedPreferencesList + "} INTO savedPreferenceEdge\r\n" + "return NEW._key";
      ArangoCursor<Object> cursor2 = null;
      try {

        cursor2 = arangoDB.query(query2, Object.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences_6: " + e.getMessage().toString());
      }

      String fname = preferenceName.get(0);
      savedFilters.put("name", fname);
      String pname = preferenceName.get(0) + counter;
      savedFilters.put("displayName", pname);
      savedFilters.put("id", response2.get(0));
      savedFilters.put("searchedOn", datestr);
      logger.info("savedFilters" + savedFilters);
      savedFiltersList.add(savedFilters);

      ArangoCursor<HashMap> cursor3 = null;
      String queryToBeExecuted3 = "for doc in " + savedPreferences + "\r\n"
          + "filter doc.Filters != null AND doc.savePreferencesHolder == '"
          + savePreferencesHolder + "' AND doc.savePreferencesHolderId == '"
          + savePreferencesHolderId + "'\r\n" + "UPDATE doc WITH { Filters: push(doc.Filters,"
          + savedFiltersList.get(0) + ",true) } IN " + savedPreferences + "";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
        response3 = cursor3.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences_7: " + e.getMessage().toString());
      }
    }
    return response;

  }

  public List<Object> getsavedPreferences(String savePreferencesHolder,
      String savePreferencesHolderId) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<String> response3 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, savedPreferences);

    String queryToBeExecuted = "for doc in " + userRegistration + "\r\n"
        + "filter doc.Email == '" + savePreferencesHolder + "' AND doc._key == '"
        + savePreferencesHolderId + "'\r\n" + "return doc._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsavedPreferences : " + e.getMessage().toString());
    }

    String query = "for doc in " + savedPreferenceEdge + "\r\n" + "filter doc._from == '"
        + response.get(0) + "'\r\n" + "return doc._to";
    logger.info("queryToBeExecuted----->" + query);

    // ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(query, Object.class);
      response1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsavedPreferences_2 : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < response1.size(); i++) {
      columns.add("doc._id=='" + response1.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);
    String query3 = "for doc in " + savedPreferences + "\r\n" + "filter " + columnIds + "\r\n"
        + "return doc";
    logger.info("queryToBeExecuted----->" + query3);

    ArangoCursor<String> cursor3 = null;
    try {

      cursor3 = arangoDB.query(query3, String.class);
      response3 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsavedPreferences_3 : " + e.getMessage().toString());
    }

    String id = null;
    for (int i = 0; i < response3.size(); i++) {
      JSONObject nodes1 = new JSONObject(response3.get(i));
      logger.info("nodes1" + nodes1);
      id = nodes1.get("_id").toString();
      String query1 = "for doc in " + savedPreferenceEdge + "\r\n" + "filter doc._to == '" + id
          + "'\r\n" + "return doc.NodeFilters";

      logger.info("query----->" + query1);
      ArangoCursor<Object> cursor1 = null;
      try {

        cursor1 = arangoDB.query(query1, Object.class);
        response2 = cursor1.asListRemaining();
        logger.info("query----->" + response1);

      } catch (Exception e) {
        log.error("Exception while getsavedPreferences_4 : " + e.getMessage().toString());
      }
      nodes1.put("NodeFilters", response2.get(0));
      logger.info("nodes1" + nodes1);
      response4.add(nodes1.toMap());
      logger.info("response3" + response3);
    }
    return response4;
  }

  public ArrayList<Object> getcreatedByMe(String loginUser) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in " + arangonodesCollection + "\r\n"
        + "filter a.createdByUserName == '" + loginUser + "'\r\n" + "return a";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsavedPreferences_5 : " + e.getMessage().toString());
    }
    return tailView(response);
  }

  public List<Object> getsharedWithMe(String loginId) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in  " + mySharedCollection + "\r\n"
        + "for b in a.shareNodes\r\n" + "for c in b.users\r\n" + "return {users:c,url:b.url}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsharedWithMe: " + e.getMessage().toString());
    }
    logger.info(String.valueOf(response));

    response.forEach(a -> {
      JSONObject b = new JSONObject(a);
      String id = b.getString("users");
      String Url = b.getString("url");
      String[] urlSplit = Url.split("/");
      String url = urlSplit[4];
      if (id.equals(loginId)) {
        columns.add("node._key == '" + url + "'");
      }
    });

    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted1 =
        "for node in Nodes\r\n" + "filter " + columnIds + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsharedWithMe_2 : " + e.getMessage().toString());
    }
    return tailView(response1);
    // return response1;
  }

  public List<Object> getsharedByMe(String loginId) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in  " + mySharedCollection + "\r\n"
        + "for b in a.shareNodes\r\n" + "return {shareHolderIds:a.shareHolderId,url:b.url}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsharedByMe : " + e.getMessage().toString());
    }
    logger.info(String.valueOf(response));

    response.forEach(a -> {
      JSONObject b = new JSONObject(a);
      String id = b.getString("shareHolderIds");
      String Url = b.getString("url");
      String[] urlSplit = Url.split("/");
      String url = urlSplit[4];
      if (id.equals(loginId)) {
        columns.add("node._key == '" + url + "'");
      }
    });

    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted1 =
        "for node in Nodes\r\n" + "filter " + columnIds + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsharedByMe_2 : " + e.getMessage().toString());
    }

    return tailView(response1);
    // return response1;
  }


  public List<HashMap> pinCollection() throws ServiceException {
    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for node in " + pincollection + "\r\n" + "return node";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while pinCollection : " + e.getMessage().toString());
    }
    return response;
  }

  public List<Object> getPinCollectionHeaderskeys(String createdbyId, String order,
      String pinFilter) throws ServiceException {

    List<String> response = new ArrayList<String>();
    List<String> governed = new ArrayList<String>();
    List<String> userRoles = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    List<String> columns = new ArrayList<>();
    String query = "for node in PinCollection\n"
        + "filter node.classification==\"governed\" AND node.teamId \n"
        + "for n in node.teamId\n"
        + "filter n.id\n"
        + "let usrRoles=(for b in userRoles\n"
        + "filter b._from == n.id\n"
        + "for c in b.users\n"
        + "return c.id\n"
        + ")\n"
        + "for s in usrRoles\n"
        + "filter s == '" + createdbyId + "'\n"
        + "return node._key";
    ArangoCursor<String> cursor2 = null;
    try {

      cursor2 = arangoDB.query(query, String.class);
      governed = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionHeaderskeys : " + e.getMessage().toString());
    }
    System.out.println("query" + query);
    for (int i = 0; i < governed.size(); i++) {
      //JSONObject s=new JSONObject(governed.get(i));
      columns.add("a._key =='" + governed.get(i) + "'");
    }

    String userColumnIds = String.join(" OR ", columns);

    String queryToBeExecuted = null;


    /*
     * String queryToBeExecuted="for node in "+ pincollection +"\r\n" +"filter node.createdBy=='"+
     * createdby +"'\r\n" + "return node";
     */
    System.out.println("userColumnIds-->" + userColumnIds);
    if (pinFilter.contains("lastmodifiedon")) {
      if (!userColumnIds.isEmpty()) {
        queryToBeExecuted =
            "for a in PinCollection\r\n" + "filter " + userColumnIds + " OR a.createdById=='"
                + createdbyId
                + "' AND a.classification == \"private\"  OR a.classification == \"public\" OR a.classification == \"System\"\r\n"
                + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
                + "return a";
      } else {
        queryToBeExecuted = "for a in PinCollection\r\n" + "filter a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\"  OR a.classification == \"public\" OR a.classification == \"System\"\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      }

    } else if (pinFilter.contains("createdon")) {
      if (!userColumnIds.isEmpty()) {
        queryToBeExecuted =
            "for a in PinCollection\r\n" + "filter " + userColumnIds + " OR a.createdById=='"
                + createdbyId
                + "' AND a.classification == \"private\"  OR a.classification == \"public\" OR a.classification == \"System\"\r\n"
                + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
                + "return a";
      } else {
        queryToBeExecuted = "for a in PinCollection\r\n" + "filter a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\"  OR a.classification == \"public\" OR a.classification == \"System\"\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      }
    } else {
      if (!userColumnIds.isEmpty()) {
        queryToBeExecuted =
            "for a in PinCollection\r\n" + "filter " + userColumnIds + " OR a.createdById=='"
                + createdbyId
                + "'  AND a.classification == \"private\"  OR a.classification == \"public\" OR a.classification == \"System\"\r\n"
                + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
                + "return a";
      } else {
        queryToBeExecuted = "for a in PinCollection\r\n" + "filter a.createdById=='" + createdbyId
            + "'  AND a.classification == \"private\"  OR a.classification == \"public\" OR a.classification == \"System\"\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      }
    }
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionHeaderskeys : " + e.getMessage().toString());
    }

    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayName"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      if (nodes.has("cover")) {
        pinHeaders.put("cover", nodes.getString("cover"));
      }
      pinHeaders.put("key", nodes.getString("_key"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("createdon", nodes.getString("createdon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      pinHeaderInfo.add(pinHeaders.toMap());
    });

    return pinHeaderInfo;
  }

  public HashMap<String, List<Object>> getPinCollectionNodekeys(String key)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<Object> pinresponse = new ArrayList<>();
    List<String> pinresponse1 = new ArrayList<>();
    List<String> curateresponse1 = new ArrayList<>();
    List<Object> pinresponse2 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    ArrayList<Object> response3 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> pincolumns = new ArrayList<String>();
    HashMap<String, List<Object>> DataInfo = new HashMap<>();
    JSONObject pins = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, recentCollections);

    String queryToBeExecuted =
        "for a in PinCollection\r\n" + "filter a.pinNodes !=null AND a._key =='" + key + "'\r\n"
            + "for b in a.pinNodes\r\n" + "return b.arangoNodeKey";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionNodekeys : " + e.getMessage().toString());
    }
    logger.info(String.valueOf(response));

    for (int i = 0; i < response.size(); i++) {
      columns.add("node._id == '" + response.get(i) + "'");
    }

    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted1 =
        "for node in Nodes\r\n" + "filter " + columnIds + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionNodekeys_2 : " + e.getMessage().toString());
    }

    String query = "for a in PinCollection\r\n" + "filter a.pinCollection !=null AND a._key =='"
        + key + "'\r\n" + "for b in a.pinCollection\r\n" + "return b.arangokey";

    logger.info("queryToBeExecuted----->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      pinresponse = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionNodekeys_3 : " + e.getMessage().toString());
    }
    logger.info(String.valueOf(response));

    for (int i = 0; i < pinresponse.size(); i++) {
      pincolumns.add("node._id == '" + pinresponse.get(i) + "'");
    }

    String pincolumnIds = String.join(" OR ", pincolumns);

    String query1 =
        "for node in PinCollection\r\n" + "filter " + pincolumnIds + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + query1);
    // ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    try {

      cursor2 = arangoDB.query(query1, String.class);
      pinresponse1 = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionNodekeys_4 : " + e.getMessage().toString());
    }


//    String querycurate =
//            "for p in PinCollection\n" +
//                    "filter p.pinNodes !=null AND p._key =='"+key+"'\n" +
//                    "for c in p.pinNodes\n" +
//                    "for a in PhysicalDataDictionary\n" +
//                    "for b in a.nodes\n" +
//                    "filter b.displayName == c.displayName \n" +
//                    "for x in mllnkphy\n" +
//                    "filter x._to == a._id\n" +
//                    "return x";
//    logger.info("queryToBeExecuted----->" + query1);
//    // ArangoCursor<HashMap> cursor1 = null;
//   // ArangoCursor<String> cursor2 = null;
//    try {
//
//      cursor2 = arangoDB.query(querycurate, String.class);
//      curateresponse1 = cursor2.asListRemaining();
//
//    } catch (Exception e) {
//      log.error("Exception while getPinCollectionNodekeys_4 : " + e.getMessage().toString());
//    }

    if (!pinresponse1.isEmpty()) {
     // List<String> finalCurateresponse = curateresponse1;
      pinresponse1.forEach(a -> {
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
        //pins.put("tags", tags);
        pins.put("pinNodes", pinNodes);
        pins.put("pinCollection", pinCollection);
//        if(finalCurateresponse.isEmpty()){
//          pins.put("curate", "false");
//        }else{
//          pins.put("curate", "true");
//        }
        pinresponse2.add(pins.toMap());
      });

    }

    pinHistory(key);

    // response3.add(pinresponse1);
    // response2.add(tailView(response1));
    DataInfo.put("pinCollection", pinresponse2);
    DataInfo.put("pinNodes", tailView(response1));
    return DataInfo;
    // return response1;
  }

  private String pinHistory(String key) {
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, recentCollections);

    HashMap<String, Object> document = new HashMap<>();
    document.put("nodepinkey", key);

    Date date = new Date();
    document.put("searchedOn", date);

    try {
      arangoCollection.insertDocument(document);
      logger.info("Search Value Document Created");
    } catch (ArangoDBException e) {
      // arangoCollection.replaceDocuments(response, document);
      log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());
      // logger.info("Search Value Updated");
    }
    return key;

  }

  public List<Object> getPinCollectionHeaders() throws ServiceException {

    List<String> response = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for node in " + pincollection + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionHeaders : " + e.getMessage().toString());
    }

    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayname"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      pinHeaders.put("cover", nodes.getString("cover"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      pinHeaderInfo.add(pinHeaders.toMap());
    });

    return pinHeaderInfo;
  }


  public List<HashMap> getPinCollectionRoles(String key) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    // + "return node.responsibilities";
    String queryToBeExecuted = "for pin in " + pincollectionedges + "\r\n" + "filter pin._key=='"
        + key + "'\r\n" + "return {Owner:pin.Owner,Steward:pin.Steward}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionRoles : " + e.getMessage().toString());
    }
    return response;

  }

  public List<Object> getPinCollectionCategories(String Type) throws ServiceException {
    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    // + "return node.categories";
    // String queryToBeExecuted="for pin in "+ pincollectionedges +"\r\n"
    // + " return {DataDomain:pin.DataDomain,System:pin.System,Product:pin.Product}";

    String queryToBeExecuted = "Return ( For u IN pinCollectionEdges return u." + Type + ")[**]";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionCategories : " + e.getMessage().toString());
    }

    return response;

  }

  public List<HashMap> addPinCollectionCategories(HashMap teamdetails) throws ServiceException {

    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response6 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, userRoles);
    String team = teamdetails.get("teamId").toString();
    String teamId = "Teams/" + team;
    // List<HashMap> context=(List<HashMap>) teamdetails.get("context");

    List<HashMap> business = (List<HashMap>) teamdetails.get("business");
    List<HashMap> dataDomain = (List<HashMap>) teamdetails.get("dataDomain");
    // teamdetails.get("dataDomain").toString();
    // .getJSONArray("dataDomain");
    List<HashMap> geography = (List<HashMap>) teamdetails.get("geography");
    List<HashMap> product = (List<HashMap>) teamdetails.get("product");
    List<Object> contexts = new ArrayList<Object>();

    business.forEach(b -> {
      JSONObject busines = new JSONObject(b);
      logger.info("business" + busines);
      String id = busines.get("key").toString();
      JSONObject busiObject = new JSONObject();
      busiObject.put("id", id);
      contexts.add(busiObject);
    });

    dataDomain.forEach(d -> {
      JSONObject domain = new JSONObject(d);
      String id = domain.getString("key");
      JSONObject dataDomainObject = new JSONObject();
      dataDomainObject.put("id", id);
      contexts.add(dataDomainObject);
    });

    // String datadomainId=dataDomain.getString("key");
    // dataDomainObject.put("id", datadomainId);

    geography.forEach(g -> {
      JSONObject geograph = new JSONObject(g);
      String id = geograph.getString("key");
      JSONObject geographyObject = new JSONObject();
      geographyObject.put("id", id);
      contexts.add(geographyObject);
    });

    product.forEach(p -> {
      JSONObject prodct = new JSONObject(p);
      String id = prodct.getString("key");
      JSONObject productObject = new JSONObject();
      productObject.put("id", id);
      contexts.add(productObject);
    });
    // contexts.add(busiObject);
    // contexts.add(dataDomainObject);
    // contexts.add(geographyObject);
    // contexts.add(productObject);
    List<HashMap> roles = (List<HashMap>) teamdetails.get("roles");
    if (!roles.isEmpty()) {
      roles.forEach(z -> {
        logger.info("z" + z);
        JSONObject y = new JSONObject(z);
        logger.info("y" + y);
        String role = y.get("role").toString();
        JSONArray users = y.getJSONArray("users");
        String roleid = "NodeTypes/" + role;
        List<Object> usersList = new ArrayList<>();
        users.forEach(u -> {
          JSONObject userObjects = new JSONObject(u.toString());
          logger.info(String.valueOf(userObjects));
          String id = userObjects.get("id").toString();
          JSONObject newUserObject = new JSONObject();
          newUserObject.put("id", id);
          usersList.add(newUserObject);
        });
        List<Object> response1 = new ArrayList<>();

        int contextCounter = 1;
        ArangoCursor<Object> cursor1 = null;
        String queryToBeExecuted1 = "insert {_from:'" + teamId + "',_to:'" + roleid + "',users:"
            + usersList + "," + "Context" + contextCounter + ":" + contexts
            + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
            + ",contextCounter:" + contextCounter + "} In " + userRoles + "\r\n";
        logger.info(queryToBeExecuted1);

        try {
          cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
          response1 = cursor1.asListRemaining();
          logger.info(String.valueOf(response1));
        } catch (Exception e) {
          log.error("Exception while addPinCollectionCategories : " + e.getMessage().toString());
        }

      });
    }

    return response2;
  }

  public List<Object> addPinCollectionRoles(List<String> key) throws ServiceException {
    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < key.size(); i++) {
      columns.add("d.roleName == '" + key.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, categoryList);

    String queryToBeExecuted = "for d in " + arangoNodeTypesCollection + "\r\n" + "filter "
        + columnIds1 + "\r\n" + "insert {name:d.roleName,type:d.type,id:d._id} IN "
        + categoryList + "\r\n" + "return d";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while addPinCollectionRoles : " + e.getMessage().toString());
    }
    return response;

  }

  public List<HashMap> getPinCollections(String key) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    // + "return node.categories";

    // String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    // + "filter node._key=='"+ key +"'\r\n"
    // + "return node.pinCollection";
    String queryToBeExecuted = "for node in " + pincollection + "\r\n" + "filter node._key=='"
        + key + "'\r\n" + "for n in node.pinCollection\r\n" + "return n";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollections : " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> getPinNodes(String key) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for node in " + pincollection + "\r\n" + "filter node._key=='"
        + key + "'\r\n" + "for n in node.pinNodes\r\n" + "return n";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinNodes : " + e.getMessage().toString());
    }

    return response;
  }

  public List<Object> getPinCollectionCategoriesType(String nodeType) throws ServiceException {
    List<String> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for pin in Business\r\n" + "filter pin.type=='" + nodeType
        + "'\r\n" + "return {key:pin._id,name:pin.name}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionCategoriesType : " + e.getMessage().toString());
    }
    JSONObject nodekeyvalue = new JSONObject();
    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action.toString());
      logger.info(String.valueOf(nodes));
      String key = nodes.getString("key");
      String name = nodes.getString("name");
      nodekeyvalue.put(key, name);

    });
    response1.add(nodekeyvalue.toMap());
    return response1;
  }

  public List<Object> getnodeTypePinCollectionRoles() throws ServiceException {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted =
        "for node in " + pincollection + "\r\n" + "return node.responsibilities";
    // String queryToBeExecuted="for doc in ShoppingCart\r\n"
    // + "filter doc.cratHolder == 'ajitha@gmail.com/12345'\r\n"
    // + "for d in doc.cartNodes\r\n"
    // + "return d";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getnodeTypePinCollectionRoles : " + e.getMessage().toString());
    }

    return response;
  }

  public List<Object> getsharedcollections(String key) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    // + "return node.responsibilities";
    String queryToBeExecuted = "for node in " + pincollectionedges + "\r\n" + "filter node._to=='"
        + key + "'\r\n" + "return node._from";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));
    } catch (Exception e) {
      log.error("Exception while getsharedcollections : " + e.getMessage().toString());
    }
    String id = response.get(0).toString();
    String query = "for node in " + pincollection + "\r\n" + "filter node._id=='" + id + "'\r\n"
        + "return node";
    logger.info("queryToBeExecuted----->" + query);

    ArangoCursor<String> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query, String.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while getsharedcollections_2 : " + e.getMessage().toString());
    }
    response1.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayname"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      pinHeaders.put("cover", nodes.getString("cover"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      pinHeaderInfo.add(pinHeaders.toMap());
    });
    return pinHeaderInfo;
  }

  public List<Object> getAllPinCollectionsdetails(String key) throws ServiceException {

    List<String> response = new ArrayList<String>();
    // List<String> response1 = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    // + "return node";
    String queryToBeExecuted = "for node in " + pincollection + "\r\n" + "FILTER node._key == '"
        + key + "'\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getAllPinCollectionsdetails : " + e.getMessage().toString());
    }

    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayname"));
      pinHeaders.put("Description", nodes.getString("Description"));
      pinHeaders.put("cover", nodes.getString("cover"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("tags", nodes.getJSONArray("tags"));
      pinHeaders.put("numberofpins", (counters + counters1));
      // pinHeaderInfo.add(pinHeaders.toMap());
      List<String> response1 = new ArrayList<String>();
      ArangoCursor<String> cursor1 = null;
      String queryToBeExecuted1 = "for node in " + pincollectionedges + "\r\n" + "return node";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      // ArangoCursor<String> cursor = null;
      try {

        cursor1 = arangoDB.query(queryToBeExecuted1, String.class);
        response1 = cursor1.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getAllPinCollectionsdetails_2 : " + e.getMessage().toString());
      }
      response1.forEach(action1 -> {
        JSONObject nodes1 = new JSONObject(action1);
        logger.info(String.valueOf(nodes1));
        JSONArray categories = new JSONArray();
        JSONArray ownerresponsibilities = new JSONArray();
        JSONArray stewardresponsibilities = new JSONArray();
        JSONObject categories1 = new JSONObject();
        JSONObject responsibility1 = new JSONObject();
        if (nodes.getString("_id").equals(nodes1.getString("_from"))) {
          // categories=nodes1.getJSONArray("DataDomain");
          categories1.put("DataDomain", nodes1.getJSONArray("DataDomain"));
          categories1.put("System", nodes1.getJSONArray("System"));
          // categories1.put("Product", nodes1.getJSONArray("Product"));
          pinHeaders.put("Categories", categories1);
          responsibility1.put("Owner", ownerresponsibilities.put(nodes1.getString("Owner")));
          responsibility1.put("Steward",
              stewardresponsibilities.put(nodes1.getString("Steward")));
          pinHeaders.put("responsibilities", responsibility1);
          pinHeaderInfo.add(pinHeaders.toMap());
        }
      });
    });

    return pinHeaderInfo;
  }

  public List<Object> getupdatedcollections() throws ServiceException {

    List<String> response = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted="for node in "+ pincollection +"\r\n"
    // +"filter node.lastmodifiedon=='"+ lastmodified +"'\r\n"
    // + "return node";
    String queryToBeExecuted = "for node in " + pincollection + "\r\n"
        + "FILTER node.lastmodifiedon >= DATE_SUBTRACT(DATE_NOW(), \"P1M0DT0H\") || node.pinNodes != null\r\n"
        + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getupdatedcollections : " + e.getMessage().toString());
    }

    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      int counters = 0;
      if (!nodes.getJSONArray("pinNodes").isEmpty()) {
        pinNodes = nodes.getJSONArray("pinNodes");

        for (int i = 1; i <= pinNodes.length(); i++) {
          counters = counters + 1;
        }
        logger.info(String.valueOf(counters));
      }
      int counters1 = 0;
      if (!nodes.getJSONArray("pinCollection").isEmpty()) {
        pinCollection = nodes.getJSONArray("pinCollection");

        for (int i = 1; i <= pinCollection.length(); i++) {
          counters1 = counters1 + 1;
        }
        logger.info(String.valueOf(counters1));
      }
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayname"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      pinHeaders.put("cover", nodes.getString("cover"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));

      pinHeaderInfo.add(pinHeaders.toMap());
    });

    return pinHeaderInfo;

  }


  public String getRemovePinCollection(String key) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    ArangoCursor<Object> cursor = null;
    ArangoCursor<String> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query1 = "for node in " + pincollection + "\r\n" + "filter node._key == '" + key
        + "'\r\n" + "return node";

    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, String.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while getRemovePinCollection: " + e.getMessage().toString());
    }
    response1.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      String displayname = nodes.getString("displayName");
      // String keys=nodes.getString("_key");
      columns1.add("Pin " + key + " " + displayname
          + " have been Successfully removed from Pincollection " + nodes.getString("_key"));
    });
    String query = "for node in " + pincollection + "\r\n" + "filter node._key == '" + key
        + "'\r\n" + "remove node._key in " + pincollection + "";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while getRemovePinCollection_2 : " + e.getMessage().toString());
    }

    return columns1.get(0);
  }

  public List<String> geteditPinCollection(HashMap pinDetails) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    HashMap<String, Set<String>> teamDetails = new HashMap<>();
    JSONObject pinInfo = new JSONObject();
    ArangoCursor<String> cursor = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String key = pinDetails.get("key").toString();
    String displayname = pinDetails.get("displayname").toString();
    String Description = pinDetails.get("Description").toString();
    String cover = pinDetails.get("cover").toString();
    String classification = pinDetails.get("classification").toString();
    String type = pinDetails.get("type").toString();
    String createdby = pinDetails.get("createdby").toString();
    String createdon = pinDetails.get("createdon").toString();
    String lastmodifiedby = pinDetails.get("lastmodifiedby").toString();
    String lastmodifiedon = pinDetails.get("lastmodifiedon").toString();
    List<String> tags = (List<String>) pinDetails.get("tags");
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < tags.size(); i++) {
      columns1.add("'" + tags.get(i) + "'");
    }

    String query1 = "for doc in  PinCollection\r\n" + "filter doc._key == '" + key + "'\r\n"
        + "update doc with {\"cover\":'" + cover + "',\"Description\":'" + Description
        + "',\"displayName\":'" + displayname + "',\"classification\":'" + classification
        + "',\"Tags\":" + columns1 + "} in PinCollection";
    logger.info("query--->" + query1);
    try {

      cursor = arangoDB.query(query1, String.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while geteditPinCollection : " + e.getMessage().toString());
    }

    return response;

  }


  public List<HashMap> geteditPostPinCollection(String key, HashMap pinDetails)
      throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    HashMap<String, Set<String>> teamDetails = new HashMap<>();
    JSONObject pinInfo = new JSONObject();
    ArangoCursor<HashMap> cursor = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String displayname = pinDetails.get("displayname").toString();
    String Description = pinDetails.get("Description").toString();
    String cover = pinDetails.get("cover").toString();
    String classification = pinDetails.get("classification").toString();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    List<String> tags = (List<String>) pinDetails.get("tags");

    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < tags.size(); i++) {
      columns1.add("'" + tags.get(i) + "'");
    }

    List<String> teamId = (List<String>) pinDetails.get("teamId");

    List<Object> team = new ArrayList<Object>();
    JSONObject teamInfo = new JSONObject();
    for (int i = 0; i < teamId.size(); i++) {
      teamInfo.put("id", "Teams/" + teamId.get(i));
      team.add(teamInfo);
    }

    String query1 = "for doc in  PinCollection\r\n" + "filter doc._key == '" + key + "'\r\n"
        + "update doc with {\"cover\":'" + cover + "',\"Description\":'" + Description
        + "',\"displayName\":'" + displayname + "',\"classification\":'" + classification
        + "',\"lastmodifiedon\":'" + datestr + "',teamId:" + teamId + "} in PinCollection";
    logger.info("query--->" + query1);
    try {

      cursor = arangoDB.query(query1, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while geteditPostPinCollection : " + e.getMessage().toString());
    }

    String query = "for doc in " + tagsEdges + "\r\n" + "filter doc._to == 'PinCollection/"
        + key + "'\r\n" + "remove doc._key in tagsEdges";
    logger.info("query--->" + query);
    try {

      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while geteditPostPinCollection_2 : " + e.getMessage().toString());
    }

    String Id = "PinCollection/" + key;
    for (int i = 0; i < tags.size(); i++) {
      String Tag = tags.get(i);
      String queryToExecute = "INSERT {Tag:'" + Tag + "',_key:'" + Tag + "'} in "
          + tagsCollection + "\r\n" + "return NEW";
      logger.info("queryToBeExecuted----->" + queryToExecute);
      ArangoCursor<HashMap> tagcursor = null;
      try {
        tagcursor = arangoDB.query(queryToExecute, HashMap.class);
        response = cursor.asListRemaining();
      } catch (Exception e) {
        log.info("Exception while geteditPostPinCollection_3 : " + e.getMessage().toString());
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
          cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
          response2 = cursor1.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while geteditPostPinCollection_4 " + e.getMessage().toString());
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
            cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
            responset = cursor1.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while geteditPostPinCollection_4 : " + e.getMessage().toString());
          }
        });
      }

    }

    return response;


  }

  public String removePinNodesFromPinCollections(String key, HashMap nodepindetails)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    List<String> columns = new ArrayList<String>();
    List<String> nodekeysList = (List<String>) nodepindetails.get("nodepinkey");
    for (int i = 0; i < nodekeysList.size(); i++) {
      columns.add("d.arangoNodeKey == '" + nodekeysList.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    ArangoCursor<Object> cursor = null;
    ArangoCursor<String> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query1 = "for node in " + pincollection + "\r\n" + "filter node._key == '" + key
        + "'\r\n" + "return node";
    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, String.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while removePinNodesFromPinCollections : " + e.getMessage().toString());
    }
    response1.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      JSONArray PinNodes = new JSONArray();
      PinNodes = nodes.getJSONArray("pinNodes");
      if (!PinNodes.isEmpty()) {
        JSONObject pinNodes = new JSONObject();

        pinNodes = (JSONObject) PinNodes.get(0);
        String nodekeys = pinNodes.get("arangoNodeKey").toString();
        String displayname = pinNodes.get("displayName").toString();
        columns1.add("Pin " + nodekeys + " " + displayname
            + " have been Successfully removed from Pincollection " + key);
      } else {
        columns1.add("Pin Collection is Empty");
      }
      String displayname = nodes.getString("displayName");
    });

    String query = "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + key
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinNodes\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc WITH { pinNodes:REMOVE_VALUES(doc.pinNodes,b) } IN PinCollection";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while removePinNodesFromPinCollections_2 : " + e.getMessage().toString());
    }

    return columns1.get(0);
  }

  public String removePinCollectionFromPinCollections(String key, HashMap nodepindetails)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    List<String> columns = new ArrayList<String>();
    List<String> nodekeysList = (List<String>) nodepindetails.get("nodepinkey");
    for (int i = 0; i < nodekeysList.size(); i++) {
      columns.add("d.arangokey == '" + nodekeysList.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    ArangoCursor<Object> cursor = null;
    ArangoCursor<String> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query1 = "for node in " + pincollection + "\r\n" + "filter node._key == '" + key
        + "'\r\n" + "return node";

    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, String.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while removePinCollectionFromPinCollections : " + e.getMessage().toString());
    }
    response1.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      JSONArray PinCollection = new JSONArray();
      PinCollection = nodes.getJSONArray("pinCollection");
      if (!PinCollection.isEmpty()) {
        JSONObject pinNodes = new JSONObject();

        pinNodes = (JSONObject) PinCollection.get(0);
        String nodekeys = pinNodes.get("arangokey").toString();
        String displayname = pinNodes.get("displayName").toString();
        columns1.add("Pin " + nodekeys + " " + displayname
            + " have been Successfully removed from Pincollection " + key);
      } else {
        columns1.add("Pin Collection is Empty");
      }
      String displayname = nodes.getString("displayName");
    });
    String query = "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + key
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinCollection\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc WITH { pinCollection:REMOVE_VALUES(doc.pinCollection,b) } IN PinCollection";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while removePinCollectionFromPinCollections_2: " + e.getMessage().toString());
    }
    return columns1.get(0);
  }

  public String addPinCollectionFromPinCollections(String key, String pinDetails)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    ArangoCursor<Object> cursor = null;
    ArangoCursor<String> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query1 = "for node in " + pincollection + "\r\n" + "filter node._key == '" + key
        + "'\r\n" + "return node";

    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, String.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while addPinCollectionFromPinCollections : " + e.getMessage().toString());
    }
    response1.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      String displayname = nodes.getString("displayname");
      // String keys=nodes.getString("_key");
      columns1.add("Pin " + key + " " + displayname
          + " have been Successfully added to Pincollection " + nodes.getString("_key"));
    });

    String query = "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + key
        + "'\r\n" + "UPDATE doc WITH { pinCollection: push(doc.pinCollection ," + pinDetails
        + ") } IN PinCollection";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while addPinCollectionFromPinCollections_2 : " + e.getMessage().toString());
    }

    return columns1.get(0);

  }

  public String addPinNodesToPinCollections(String key, String pinDetails) throws ServiceException {
    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    ArangoCursor<Object> cursor = null;
    ArangoCursor<String> cursor1 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String query1 = "for node in " + pincollection + "\r\n" + "filter node._key == '" + key
        + "'\r\n" + "return node";

    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, String.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while addPinNodesToPinCollections : " + e.getMessage().toString());
    }
    response1.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      String displayname = nodes.getString("displayName");
      // String keys=nodes.getString("_key");
      columns1.add("Pin " + key + " " + displayname
          + " have been Successfully added To Pincollection " + nodes.getString("_key"));
    });

    String query = "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + key
        + "'\r\n" + "UPDATE doc WITH { pinNodes: push(doc.pinNodes," + pinDetails
        + ",true) } IN PinCollection";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while addPinNodesToPinCollection_2 : " + e.getMessage().toString());
    }

    return columns1.get(0);


  }

  public String movePinNodesToPinCollections(String to, String from, HashMap nodepindetails)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    List<String> columns = new ArrayList<String>();
    List<String> nodepinkey = (List<String>) nodepindetails.get("nodepinkey");
    for (int i = 0; i < nodepinkey.size(); i++) {
      columns.add("d.arangoNodeKey == 'Nodes/" + nodepinkey.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    ArangoCursor<Object> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String querytoexecute = "for node in " + pincollection + "\r\n" + "filter node._key == '"
        + to + "'\r\n" + "return node";
    try {

      cursor2 = arangoDB.query(querytoexecute, String.class);
      response2 = cursor2.asListRemaining();
      logger.info("response1" + response2);

    } catch (Exception e) {
      log.error(
          "Exception while movePinNodesToPinCollections: " + e.getMessage().toString());
    }
    response2.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      if (!nodes.isEmpty()) {
        logger.info(String.valueOf(nodes));
        String displayname = nodes.getString("displayName");
        // String keys=nodes.getString("_key");

        columns1.add("Pin " + "" + nodepinkey + " of " + from + " " + displayname
            + " have been Successfully moved to Pincollection " + to);
      } else {
        columns1.add("Empty collection");
      }
    });
    String query1 = "for doc1 in " + pincollection + "\r\n" + "filter doc1._key == '" + to
        + "'\r\n" + "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + from
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinNodes\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc1 WITH { pinNodes:APPEND(doc1.pinNodes,b,true) } IN PinCollection";

    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while movePinNodesToPinCollections_2 : " + e.getMessage().toString());
    }

    String query = "for doc1 in " + pincollection + "\r\n" + "filter doc1._key == '" + to
        + "'\r\n" + "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + from
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinNodes\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc WITH { pinNodes:REMOVE_VALUES(doc.pinNodes,b) } IN PinCollection";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while movePinNodesToPinCollections_3 : " + e.getMessage().toString());
    }

    return columns1.get(0);


  }

  public String movePinCollectionToPinCollections(String to, String from, HashMap nodepindetails)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    List<String> columns = new ArrayList<String>();
    List<String> nodepinkey = (List<String>) nodepindetails.get("nodepinkey");
    for (int i = 0; i < nodepinkey.size(); i++) {
      columns.add("d.arangokey == 'PinCollection/" + nodepinkey.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    ArangoCursor<Object> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String querytoexecute = "for node in " + pincollection + "\r\n" + "filter node._key == '"
        + to + "'\r\n" + "return node";
    try {

      cursor2 = arangoDB.query(querytoexecute, String.class);
      response2 = cursor2.asListRemaining();
      logger.info("response1" + response2);

    } catch (Exception e) {
      log.error(
          "Exception while movePinCollectionToPinCollections : " + e.getMessage().toString());
    }
    response2.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      if (!nodes.isEmpty()) {
        String displayname = nodes.getString("displayName");
        // String keys=nodes.getString("_key");
        columns1.add("Pin " + "" + nodepinkey + " of " + from + " " + displayname
            + " have been Successfully moved to Pincollection " + to);
      } else {
        columns1.add("Collection empty");
      }
    });
    String query1 = "for doc1 in " + pincollection + "\r\n" + "filter doc1._key == '" + to
        + "'\r\n" + "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + from
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinCollection\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc1 WITH { pinCollection:APPEND(doc1.pinCollection,b,true) } IN PinCollection";

    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while movePinCollectionToPinCollections_2 : " + e.getMessage().toString());
    }

    String query = "for doc1 in " + pincollection + "\r\n" + "filter doc1._key == '" + to
        + "'\r\n" + "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + from
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinCollection\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc WITH { pinCollection:REMOVE_VALUES(doc.pinCollection,b) } IN PinCollection";

    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while movePinCollectionToPinCollections_3 : " + e.getMessage().toString());
    }

    return columns1.get(0);


  }

  public String copyPinNodesToPinCollections(String to, String from, HashMap nodepindetails)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns = new ArrayList<String>();
    List<String> nodepinkey = (List<String>) nodepindetails.get("nodepinkey");
    for (int i = 0; i < nodepinkey.size(); i++) {
      columns.add("d.arangoNodeKey == 'Nodes/" + nodepinkey.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    String Remove = "Successful";
    ArangoCursor<Object> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String querytoexecute = "for node in " + pincollection + "\r\n" + "filter node._key == '"
        + to + "'\r\n" + "return node";
    try {

      cursor2 = arangoDB.query(querytoexecute, String.class);
      response2 = cursor2.asListRemaining();
      logger.info("response1" + response2);

    } catch (Exception e) {
      log.error(
          "Exception while copyPinNodesToPinCollections : " + e.getMessage().toString());
    }
    response2.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      if (!nodes.isEmpty()) {
        String displayname = nodes.getString("displayName");
        // String keys=nodes.getString("_key");
        columns1.add("Pin " + "" + nodepinkey + " of " + from + " " + displayname
            + " have been Successfully moved to Pincollection " + to);
      } else {
        columns1.add("Empty collection");
      }
    });
    // String query1="for doc1 in "+ pincollection +"\r\n"
    // + "filter doc1._key == '"+ to +"'\r\n"
    // + "for doc in "+ pincollection +"\r\n"
    // + "filter doc._key == '"+ from +"'\r\n"
    // + "for d in doc.pinNodes\r\n"
    // + "filter "+ columnIds +"\r\n"
    // + "UPDATE doc1 WITH { pinNodes:push(doc1.pinNodes,d) } IN PinCollection";
    String query1 = "for doc1 in " + pincollection + "\r\n" + "filter doc1._key == '" + to
        + "'\r\n" + "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + from
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinNodes\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc1 WITH { pinNodes:APPEND(doc1.pinNodes,b,true) } IN PinCollection";
    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while copyPinNodesToPinCollections_2 : " + e.getMessage().toString());
    }

    return columns1.get(0);

  }

  public String copyPinCollectionToPinCollections(String to, String from, HashMap nodepindetails)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    String Remove = "Successful";
    List<String> columns = new ArrayList<String>();
    List<String> nodepinkey = (List<String>) nodepindetails.get("nodepinkey");
    for (int i = 0; i < nodepinkey.size(); i++) {
      columns.add("d.arangokey == 'PinCollection/" + nodepinkey.get(i) + "'");
      logger.info("columns-->" + columns);
    }

    String columnIds1 = String.join(" OR ", columns);
    ArangoCursor<Object> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<String> cursor2 = null;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String querytoexecute = "for node in " + pincollection + "\r\n" + "filter node._key == '"
        + to + "'\r\n" + "return node";
    try {

      cursor2 = arangoDB.query(querytoexecute, String.class);
      response2 = cursor2.asListRemaining();
      logger.info("response1" + response2);

    } catch (Exception e) {
      log.error(
          "Exception while copyPinCollectionToPinCollections: " + e.getMessage().toString());
    }
    response2.forEach(node -> {
      JSONObject nodes = new JSONObject(node);
      logger.info(String.valueOf(nodes));
      if (!nodes.isEmpty()) {
        String displayname = nodes.getString("displayName");
        // String keys=nodes.getString("_key");
        columns1.add("Pin " + "" + nodepinkey + " of " + from + " " + displayname
            + " have been Successfully moved to Pincollection " + to);
      } else {
        columns1.add("collection empty");
      }
    });
    String query1 = "for doc1 in " + pincollection + "\r\n" + "filter doc1._key == '" + to
        + "'\r\n" + "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + from
        + "'\r\n" + "let b=(\r\n" + "for d in doc.pinCollection\r\n" + "filter " + columnIds1
        + "\r\n" + "return d)\r\n"
        + "UPDATE doc1 WITH { pinCollection:APPEND(doc1.pinCollection,b,true) } IN PinCollection";

    logger.info("query--->" + query1);

    try {

      cursor1 = arangoDB.query(query1, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info("response1" + response1);

    } catch (Exception e) {
      log.error(
          "Exception while copyPinCollectionToPinCollections_2 : " + e.getMessage().toString());
    }
    return columns1.get(0);
  }

  public List<HashMap> pinFilters(List<String> name) throws ServiceException {

    List<String> columns = new ArrayList<String>();
    // columns.add("CONTAINS(data, '" + name.toString() + "')");
    // String columnIds = String.join(" OR ", columns);
    ArangoCursor<HashMap> cursor1 = null;
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query2 = "let status=(FOR d IN " + arangoPinSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.status)\r\n" + "FOR data IN d.status\r\n" + "FILTER data IN " + name
        + "\r\n" + "RETURN data)\r\n" + "let rating=(\r\n" + "FOR d IN "
        + arangoPinSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.AvgRating)\r\n"
        + "FOR data IN d.AvgRating\r\n" + "FILTER data IN " + name + "\r\n" + "RETURN data\r\n"
        + ")\r\n" + "return {status:UNIQUE(status),rating:UNIQUE(rating)}";
    String query = "let status=(FOR d IN " + arangoPinSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.status)\r\n" + "FOR data IN d.status\r\n" + "FILTER data IN " + name
        + "\r\n" + "RETURN data)\r\n" + "let rating=(\r\n" + "FOR d IN "
        + arangoPinSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.AvgRating)\r\n"
        + "FOR data IN d.AvgRating\r\n" + "FILTER data IN " + name + "\r\n" + "RETURN data\r\n"
        + ")\r\n" + "let tag=(\r\n" + "FOR d IN " + arangoPinSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.tag)\r\n" + "FOR data IN d.tag\r\n" + "FILTER data IN " + name
        + "\r\n" + "RETURN data\r\n" + ")\r\n" + "let curated=(\r\n" + "FOR d IN "
        + arangoPinSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.curated)\r\n"
        + "FOR data IN d.curated\r\n" + "FILTER data IN " + name + "\r\n" + "RETURN data\r\n"
        + ")\r\n" + "let freshness=(\r\n" + "FOR d IN " + arangoPinSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.Freshness)\r\n" + "FOR data IN d.Freshness\r\n" + "FILTER data IN "
        + name + "\r\n" + "RETURN data\r\n" + ")\r\n" + "let frequency=(\r\n" + "FOR d IN "
        + arangoPinSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.Frequency)\r\n"
        + "FOR data IN d.Frequency\r\n" + "FILTER data IN " + name + "\r\n" + "RETURN data\r\n"
        + ")\r\n" + "let nodename=(\r\n" + "FOR d IN " + arangoPinSearchTypeCollection + "\r\n"
        + "FILTER IS_LIST(d.nodename)\r\n" + "FOR data IN d.nodename\r\n" + "FILTER data IN "
        + name + "\r\n" + "RETURN data\r\n" + ")\r\n" + "let certified=(\r\n" + "FOR d IN "
        + arangoPinSearchTypeCollection + "\r\n" + "FILTER IS_LIST(d.certified)\r\n"
        + "FOR data IN d.certified\r\n" + "FILTER data IN " + name + "\r\n" + "RETURN data\r\n"
        + ")\r\n"
        + "return {status:UNIQUE(status),rating:UNIQUE(rating),certified:UNIQUE(certified),nodename:UNIQUE(nodename),frequency:UNIQUE(frequency),freshness:UNIQUE(freshness),curated:UNIQUE(curated),tag:UNIQUE(tag)}";
    logger.info("query--->" + query);

    try {

      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);

    } catch (Exception e) {
      log.error(
          "Exception while pinFilters : " + e.getMessage().toString());
    }

    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray ratings = new JSONArray();
      JSONArray status = new JSONArray();
      JSONArray certified = new JSONArray();
      JSONArray nodename = new JSONArray();
      JSONArray frequency = new JSONArray();
      JSONArray freshness = new JSONArray();
      JSONArray curated = new JSONArray();
      JSONArray tag = new JSONArray();
      ratings = nodes.getJSONArray("rating");
      status = nodes.getJSONArray("status");
      certified = nodes.getJSONArray("certified");
      nodename = nodes.getJSONArray("nodename");
      frequency = nodes.getJSONArray("frequency");
      freshness = nodes.getJSONArray("freshness");
      curated = nodes.getJSONArray("curated");
      tag = nodes.getJSONArray("tag");

      if (!ratings.isEmpty()) {
        columns.add("node.AvgRating IN " + ratings + "");
      }
      if (!status.isEmpty()) {
        columns.add("node.Status IN " + status + "");
      }
      if (!certified.isEmpty()) {
        columns.add("node.Certified IN " + certified + "");
      }
      if (!curated.isEmpty()) {
        columns.add("node.Curated IN " + curated + "");
      }
      if (!tag.isEmpty()) {
        columns.add("node.Tag IN " + tag + "");
      }
      if (!freshness.isEmpty()) {
        columns.add("node.LastModifiedOn IN " + freshness + "");
      }
      if (!frequency.isEmpty()) {
        columns.add("node.Frequency IN " + frequency + "");
      }
      if (!nodename.isEmpty()) {
        columns.add("node.Type IN " + nodename + "");
      }
    });
    String columnIds = String.join(" AND ", columns);
    logger.info(columnIds);
    // logger.info(columnIds);
    // logger.info(columns);
    // logger.info(filterObject);
    // Object ratingfilter=filterObject.get("rating");
    // logger.info(ratingfilter);
    String query1 = "FOR m IN " + arangopinSearchCollection + "\r\n" + "for a in m.nodename\r\n"
        + "filter a.nodeInfo\r\n" + "for node in a.nodeInfo\r\n" + "filter " + columnIds
        + "\r\n" + "return node";
    logger.info("query----->" + query1);
    try {

      cursor1 = arangoDB.query(query1, HashMap.class);

      response1 = cursor1.asListRemaining();


    } catch (Exception e) {
      log.error("Exception while pinFilters_2 : " + e.getMessage().toString());
    }

    return response1;
  }

  public List<HashMap> getPinNodesInfo(String nodeName) throws ServiceException {

    List<JSONObject> nodetypes = new ArrayList<JSONObject>();
    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    HashMap<String, ArrayList<Object>> NodeInfo = new HashMap<>();
    HashMap<String, Object> NodeInfo1 = new HashMap<>();
    ArrayList<Object> nodesList = new ArrayList<>();
    List<String> columns1 = new ArrayList<String>();
    List<String> columns2 = new ArrayList<String>();
    List<String> columns3 = new ArrayList<String>();
    List<String> columns4 = new ArrayList<String>();
    List<String> columns5 = new ArrayList<String>();
    List<String> columns6 = new ArrayList<String>();
    List<String> columns9 = new ArrayList<String>();
    List<String> columns10 = new ArrayList<String>();
    List<String> columns11 = new ArrayList<String>();
    ArrayList<String> columns7 = new ArrayList<String>();
    List<String> columns8 = new ArrayList<String>();
    JSONObject searchString = new JSONObject();
    searchString.put("Name", nodeName);
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    final String query =
        "FOR node IN " + pinviewName + "\r\n" + "SEARCH ANALYZER(node.name IN TOKENS('" + nodeName
            + "','text_en'),'text_en')\r\n" + "SORT BM25(node) DESC\r\n" + "RETURN node";

    ArangoCursor<HashMap> cursor = null;
    logger.info("query...  " + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getPinNodesInfo : " + e.getMessage().toString());
    }
    JSONObject nodesinfo1 = new JSONObject();
    JSONObject nodesinfo2 = new JSONObject();
    JSONObject assettype = new JSONObject();
    response.forEach(nodesinfo -> {
      JSONObject nodes = new JSONObject(nodesinfo);
      String str = nodes.getString("displayName");
      // String str1="<b>"+str+"</b>";
      nodesinfo1.put("DisplayName", str);
      String label = nodes.getString("identifier");
      String[] label1 = label.split(">");
      logger.info(String.valueOf(label1));
      JSONObject nodetype = nodes.getJSONObject("type");
      String nameType = nodetype.getString("name");
      nodesinfo1.put("Type", nameType);
      assettype.put("Type", nameType);
      nodetypes.add(assettype);
      columns1.add(nameType);
      if (nameType.trim().contains("Data Set") || nameType.trim().contains("Data Product")
          || nameType.trim().contains("API")) {
        nodesinfo1.put("Action", "AddToCart");
      } else if (nameType.contains("Column") || nameType.contains("Schema")
          || nameType.contains("Table") || nameType.contains("Field")
          || nameType.contains("File")) {
        nodesinfo1.put("Action", "AddToDataSet");
      } else {
        nodesinfo1.put("Action", "Empty");
      }

      if (label.contains("curated")) {
        String curated = "true";
        nodesinfo1.put("Curated", curated);
        if (nameType.contains("Data Set") || nameType.contains("Schema")) {
          columns10.add(curated);
        }
      } else {

        String curated = "false";
        nodesinfo1.put("Curated", curated);
        if (nameType.contains("Data Set") || nameType.contains("Schema")) {
          columns10.add(curated);
        }
      }
      String nodeRatingsCount = nodes.get("ratingsCount").toString();
      nodesinfo1.put("RatingsCount", nodeRatingsCount);
      String avgRating = nodes.get("avgRating").toString();
      nodesinfo1.put("AvgRating", avgRating);
      columns3.add(avgRating);
      String identifier = nodes.getString("identifier");
      nodesinfo1.put("Identifier", identifier);
      String uuid = nodes.getString("id");
      nodesinfo1.put("Id", uuid);
      if (nodes.has("createdByFullName")) {
        String createdByFullName = nodes.getString("createdByFullName");
        nodesinfo1.put("CreatedByFullName", createdByFullName);
      }
      if (nodes.has("responsibilities")) {
        JSONArray responsibilities = nodes.getJSONArray("responsibilities");
        if (!responsibilities.isEmpty()) {
          responsibilities.forEach(eachsource -> {
            JSONObject responsibility = new JSONObject(eachsource.toString());
            String roleName = responsibility.getString("roleName");
            String responsibilityName = responsibility.getString("name");
            nodesinfo2.put(roleName.trim(), responsibilityName);
          });
        }
      }
      JSONObject nodestatus = nodes.getJSONObject("status");
      String statusName = nodestatus.getString("name");
      nodesinfo1.put("Status", statusName);
      columns2.add(statusName);
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
          logger.info(String.valueOf(attributes));
          if (!attributes.isEmpty()) {
            if (attributes.get("name").toString().equals("Description")) {
              String value = attributes.getString("value");
              nodesinfo1.put("Description", "<b>" + value + "</b>");
            } else if (attributes.get("name").toString().equals("Definition")) {
              String value = attributes.getString("value");
              nodesinfo1.put("Description", "<b>" + value + "</b>");
            }
            if (attributes.get("name").toString().equals("Certified")) {
              String value = attributes.getString("value");
              nodesinfo1.put("Certified", value);
              if (nameType.contains("Data Set") || nameType.contains("Schema")) {
                columns9.add(value);
              }
            }
            if (attributes.get("name").toString().equals("Frequency")) {
              String frequency = attributes.getString("value");
              nodesinfo1.put("Frequency", frequency);
              if (nameType.contains("Data Set") || nameType.contains("Schema")) {
                columns5.add(frequency);
              }
            }
            if (attributes.get("name").toString().equals("LastModifiedOn")) {
              String value = attributes.getString("value");
//                long l = Long.parseLong(value);
//                String Date = LocalDateTime
//                    .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
              nodesinfo1.put("LastModifiedOn", value);
              if (nameType.contains("Data Set") || nameType.contains("Schema")) {
                columns6.add(value);
              }
            }
            if (attributes.get("name").toString().equals("Personally Identifiable Information")) {
              String value = attributes.getString("value");
              nodesinfo1.put("PersonallyIdentifiableInformation", value);
            }
            if (attributes.get("name").toString().equals("Security Classification")) {
              String value = attributes.getString("value");
              nodesinfo1.put("SecurityClassification", value);

            }
            if (attributes.get("name").toString().equals("tag")) {
              String tag = attributes.getString("value");
              nodesinfo1.put("Tag", tag);
              columns4.add(tag);
            }
            if (attributes.get("name").toString().equals("Passing Fraction")) {
              String value = attributes.getString("value");
              nodesinfo1.put("PassingFraction", value);
            }
          }
        });
      } else if (attributeInfo.isEmpty()) {
        nodesinfo1.put("No Attributes", " Attributes not available ");
      }
      JSONArray targetsObj = new JSONArray();
      JSONObject edges = (JSONObject) nodes.get("relations");
      JSONArray targetedges = edges.getJSONArray("targets");
      if (!targetedges.isEmpty()) {
        targetedges.forEach(eachsource -> {
          JSONObject targets = new JSONObject(eachsource.toString());
          // String targetname=targets.getString("CoRole");
          String targetname = null;
          if (targets.has("CoRole")) {
            targetname = targets.getString("CoRole");
          } else {
            targetname = targets.getString("coRole");
          }
          if (targetname.contains("contains") || targetname.contains("represents")) {
            targetsObj.put(targets);
          }
        });
        if (!targetsObj.isEmpty()) {
          JSONObject targetedges1 = targetsObj.getJSONObject(0);
          String result = null;
          JSONObject target1 = targetedges1.getJSONObject("target");
          // String targetname=targetedges1.getString("CoRole");
          String targetname = null;
          if (targetedges1.has("CoRole")) {
            targetname = targetedges1.getString("CoRole");
          } else {
            targetname = targetedges1.getString("coRole");
          }
          String targetid = target1.getString("id");
          String query1 = getQueryResult(targetid);
          if (targetname.contains("contains") || targetname.contains("represents")) {
            result = getNodesResponse(query1);
            nodesinfo1.put("SourceSystem", result);

          } else {
            nodesinfo1.put("SourceSystem", "null");
          }
        } else {
          nodesinfo1.put("SourceSystem", "null");
        }
      } else {
        nodesinfo1.put("SourceSystem", "null");
      }
      nodesinfo1.put("roles", nodesinfo2);
      nodesinfo1.put("QualityScore", getScore(str));
      nodesinfo1.put("metaQualityScore", metaQualityScore(str));
      ArrayList<String> LineOfBusiness = getTypes(str);
      ArrayList<String> DataDomain = getTypes1(str);
      nodesinfo1.put("LineOfBusiness", getTypes(str));
      columns7.addAll(LineOfBusiness);
      nodesinfo1.put("DataDomain", getTypes1(str));
      columns8.addAll(DataDomain);
      nodesList.add(nodesinfo1.toMap());
    });

    NodeInfo.put("nodeInfo", nodesList);
    NodeInfo1.put("searchString", searchString.toMap());
    response1.add(NodeInfo);
    storepinSearchValue(response1);
    storePinSearchType(columns1, columns2, columns3, columns4, columns5, columns6, columns7,
        columns8, columns9, columns10);
    // storeSearchType(nodetypes);
    response1.add(NodeInfo1);
    // myRecentSearchHistory(searchString);
    return response1;

  }

  public List<Object> getmyworkUpdates(String userName) throws ServiceException {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for node1 in PinCollection\r\n" + "filter node1.createdby == '"
        + userName + "' && node1.pinNodes !=null \r\n" + "return node1";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getmyworkUpdates : " + e.getMessage().toString());
    }

    return response;
  }

  public List<HashMap> getPinCollectionsdetails(String key) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    List<String> columns1 = new ArrayList<String>();
    List<HashMap> responseList = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for node in " + pincollection + "\r\n" + "filter node._key=='"
        + key + "'\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionsdetails : " + e.getMessage().toString());
    }

    String query = "for node in " + tagsEdges + "\r\n" + "filter node._to=='PinCollection/" + key
        + "'\r\n" + "return node._from";

    logger.info("queryToBeExecuted----->" + query);

    ArangoCursor<Object> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query, Object.class);
      response3 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionsdetails : " + e.getMessage().toString());
    }

    for (int i = 0; i < response3.size(); i++) {
      columns.add("node._id == '" + response3.get(i) + "'");
    }

    String columnIds = String.join(" OR ", columns);

    String query1 = "for node in " + tagsCollection + "\r\n" + "filter " + columnIds + "\r\n"
        + "return node.Tag";
    logger.info("queryToBeExecuted----->" + query1);

    // ArangoCursor<HashMap> cursor = null;
    try {

      cursor1 = arangoDB.query(query1, Object.class);
      response2 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionsdetails_2 : " + e.getMessage().toString());
    }

    for (int j = 0; j < response2.size(); j++) {
      columns1.add("" + response2.get(j) + "");
    }
    response.forEach(a -> {
      HashMap s = new HashMap(a);
      s.put("tags", columns1);
      responseList.add(s);
    });

    return responseList;

  }


  public List<HashMap> addSectionTeamForSimple(HashMap pinDetails) throws ServiceException {

    List<String> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, pincollection);
    ArangoCollection arangoCollection1 =
        arangorestclient.getArangoEdgeCollection(arangoDB, userRoles);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String displayname = pinDetails.get("displayname").toString();
    String Description = null;
    if (pinDetails.containsKey("Description")) {
      Description = pinDetails.get("Description").toString();
    } else {
      Description = "";
    }
    String cover = null;
    if (pinDetails.containsKey("cover")) {
      cover = pinDetails.get("cover").toString();
    } else {
      cover = "";
    }
    String classification = pinDetails.get("classification").toString();
    String type = pinDetails.get("type").toString();
    String createdby = pinDetails.get("createdby").toString();
    String createdbyId = pinDetails.get("createdbyId").toString();
    // String createdon=pinDetails.get("createdon").toString();
    String lastmodifiedby = pinDetails.get("lastmodifiedby").toString();
    List<String> teamId = (List<String>) pinDetails.get("teamId");

    List<Object> team = new ArrayList<Object>();
    JSONObject teamInfo = new JSONObject();
    for (int i = 0; i < teamId.size(); i++) {
      teamInfo.put("id", "Teams/" + teamId.get(i));
      team.add(teamInfo);
    }
    ArangoCursor<HashMap> cursor = null;
    ArangoCursor<String> cursor2 = null;

    String queryToBeExecuted = "INSERT {displayName:'" + displayname + "',Description:'"
        + Description + "',classification:'" + classification + "',type:'" + type
        + "',createdBy:'" + createdby + "',createdById:'" + createdbyId + "',createdon:'" + datestr
        + "',lastmodifiedby:'"
        + lastmodifiedby + "',lastmodifiedon:'" + datestr + "',pinNodes:[],pinCollection:[]"
        + ",cover:'" + cover + "',teamId:" + team + "} In " + pincollection + "\r\n"
        + "return NEW._id";
    logger.info(queryToBeExecuted);

    try {
      cursor2 = arangoDB.query(queryToBeExecuted, String.class);
      response1 = cursor2.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while addSectionTeamForSimple : " + e.getMessage().toString());
    }
    if (pinDetails.containsKey("tags")) {
      List<String> tags = (List<String>) pinDetails.get("tags");
      String Id = response1.get(0);
      for (int i = 0; i < tags.size(); i++) {
        String Tag = tags.get(i);
        String queryToExecute = "INSERT {Tag:'" + Tag + "',_key:'" + Tag + "'} in "
            + tagsCollection + "\r\n" + "return NEW";
        logger.info("queryToBeExecuted----->" + queryToExecute);
        ArangoCursor<HashMap> tagcursor = null;
        try {
          tagcursor = arangoDB.query(queryToExecute, HashMap.class);
          response = cursor.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while addSectionTeamForSimple_2: " + e.getMessage().toString());
        }
        logger.info("response" + response);
        if (response.isEmpty()) {

          // ArangoCursor<HashMap> cursor1 = null;
          // List<HashMap> response2 = new ArrayList<>();
          String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n"
              + "filter doc._key=='" + Tag + "'\r\n" + "INSERT {_from: doc._id, _to: '" + Id
              + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges + "";
          logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
          try {
            cursor = arangoDB.query(queryToBeExecuted1, HashMap.class);
            response2 = cursor.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while addSectionTeamForSimple_3 : " + e.getMessage().toString());
          }

        } else {
          response.forEach(a -> {
            JSONObject s = new JSONObject(a);
            String key = s.getString("_key");
            String tagsl = s.getString("Tag");
            String ids = s.getString("_id");

            ArangoCursor<HashMap> cursor1 = null;
            List<HashMap> responset = new ArrayList<>();

            String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n"
                + "filter doc._key=='" + key + "'\r\n" + "INSERT {_from:'" + ids + "', _to: '"
                + Id + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges
                + "";
            logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
            try {
              cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
              responset = cursor1.asListRemaining();
            } catch (Exception e) {
              log.error("Exception while addSectionTeamForSimple_4 : " + e.getMessage().toString());
            }
          });
        }

      }
    }
    return response2;

  }

  public List<Object> getTeams() throws ServiceException {
    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted="for node in "+ Teams +"\r\n"
    // + "return {displayName:node.displayName,teamStructure:node.teamStructure,key:node._key}";

    String queryToBeExecuted = "for node in " + Teams + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getTeams : " + e.getMessage().toString());
    }

    return response;
  }


  public String getTeamDetails(String teamId) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    // HashMap<String, List<Object>> teamDetails = new HashMap<>();
    JSONObject teamDetail1 = new JSONObject();
    JSONObject teamDetails = new JSONObject();
    JSONObject teamdetails = new JSONObject();
    // HashMap teamDetail1=new HashMap();
    HashMap teamDetail = new HashMap();
    // JSONObject roleuserObject=new JSONObject();
    List<Object> keycolumns = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getTeamDetails : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < response.size(); i++) {
      columns.add("a._from == '" + response.get(i) + "'");
    }

    String columnIds = String.join(" OR ", columns);
    String queryToBeExecuted1 =
        "for a in " + userRoles + "\r\n" + "filter " + columnIds + "\r\n" + "return a";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, String.class);
      response1 = cursor3.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while getTeamDetails : " + e.getMessage().toString());
    }

    List<String> columns2 = new ArrayList<String>();
    JSONObject team = new JSONObject();
    team.put("teamId", teamId);
    for (int i = 0; i < response1.size(); i++) {
      JSONObject s = new JSONObject(response1.get(i));
      String id = s.getString("_to");
      String[] Id = id.split("/");
      String nodetypes = Id[0];
      String name = Id[1];
      logger.info("name" + name);
      // JSONArray s1=new JSONArray();

      // s1=s.getJSONArray("users");
      // logger.info("s1"+s1);
      // s1.forEach(y->{
      // JSONObject usrId=new JSONObject(y.toString());
      // logger.info("usrId"+usrId);
      // String ids=usrId.getString("id");
      // columns2.add("a._key == '"+ ids +"'");
      // logger.info("columns2"+columns2);
      // });

      if (s.has("users")) {
        JSONArray s1 = new JSONArray();
        s1 = s.getJSONArray("users");
        s1.forEach(y -> {
          JSONObject usrId = new JSONObject(y.toString());
          logger.info("usrId" + usrId);
          String ids = usrId.getString("id");
          columns2.add("a._key == '" + ids + "'");
          logger.info("columns" + columns2);
        });
      }

      List<String> response3 = new ArrayList<>();
      ArangoCursor<String> cursor2 = null;
      String columnIds2 = String.join(" OR ", columns2);
      String queryToBeExecuted3 = "for a in " + userRegistration + "\r\n" + "filter " + columnIds2
          + "\r\n" + "return {" + name + ":a.FirstName,id:a._key}";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
      // ArangoCursor<Object> cursor = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted3, String.class);
        response3 = cursor2.asListRemaining();
        logger.info(String.valueOf(response3));

      } catch (Exception e) {
        log.error("Exception while getTeamDetails_2: " + e.getMessage().toString());
      }
      columns2.clear();

      // teamDetail1.put("id", id);
      // teamDetail1.put("role", name);

      for (int l = 0; l < response3.size(); l++) {

        JSONObject roleuserObject = new JSONObject();
        JSONObject tuser = new JSONObject(response3.get(l));
        logger.info("tuser" + tuser);
        roleuserObject.put("name", tuser.getString(name));
        roleuserObject.put("id", tuser.getString("id"));
        // Collection k = teamDetail.values();
        keycolumns.add(roleuserObject);


      }
      teamDetails.put(name, keycolumns);
      keycolumns.clear();

      // response4.add(teamDetail1.toMap());
      response1.forEach(b -> {
        JSONObject x = new JSONObject(b);
        if (x.has("users")) {
          // JSONArray users=x.getJSONArray("users");
          // response4.add(teamDetail3.toMap());

          teamDetail1.put("roles", teamDetails);
        } else {
          teamDetail1.put("roles", teamdetails);
        }
      });
      teamDetail1.put("teamId", teamId);

      // teamDetail1.put("roles",teamDetails);
    }
    return teamDetail1.toString();
  }

  public List<Object> getRemoveRoles(String teamId) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<Object> cursor3 = null;
    ArangoCursor<Object> cursor1 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getRemoveRoles : " + e.getMessage().toString());
    }

    String queryToBeExecuted1 = "for a in " + userRoles + "\r\n" + "filter a._from == '"
        + response.get(0) + "'\r\n" + "return a._to";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, Object.class);
      response1 = cursor3.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while getRemoveRoles_2 : " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {

      String queryToBeExecuted2 = "for a in " + userRoles + "\r\n" + " filter a._from == '"
          + response.get(0) + "' && a._to == '" + response1.get(i) + "'\r\n" + "remove a._key IN "
          + userRoles + "";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
      try {

        cursor1 = arangoDB.query(queryToBeExecuted2, Object.class);
        response2 = cursor1.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while getRemoveRoles_3 : " + e.getMessage().toString());
      }
    }

    String query = "for a in " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n"
        + "remove a._key IN " + Teams + "";
    logger.info("queryToBeExecuted1----->" + query);

    try {

      cursor3 = arangoDB.query(query, Object.class);
      response1 = cursor3.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while getRemoveRoles_4 : " + e.getMessage().toString());
    }

    return response1;

  }

  public List<Object> getEditTeamDetails1(String teamId) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    HashMap<String, Set<String>> teamDetails = new HashMap<>();
    // HashMap teamDetail1=new HashMap();
    HashMap teamDetail = new HashMap();
    // JSONObject roleuserObject=new JSONObject();
    List<Object> keycolumns = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getEditTeamDetails1 : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < response.size(); i++) {
      columns.add("a._from == '" + response.get(i) + "'");
    }

    String columnIds = String.join(" OR ", columns);
    String queryToBeExecuted1 =
        "for a in " + userRoles + "\r\n" + "filter " + columnIds + "\r\n" + "return a";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, String.class);
      response1 = cursor3.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while getEditTeamDetails1_2 : " + e.getMessage().toString());
    }

    List<String> columns2 = new ArrayList<String>();

    for (int i = 0; i < response1.size(); i++) {
      JSONObject s = new JSONObject(response1.get(i));
      String id = s.getString("_to");
      String[] Id = id.split("/");
      String nodetypes = Id[0];
      String name = Id[1];
      logger.info("name" + name);
      JSONArray s1 = new JSONArray();
      s1 = s.getJSONArray("users");
      logger.info("s1" + s1);
      s1.forEach(y -> {
        JSONObject usrId = new JSONObject(y.toString());
        logger.info("usrId" + usrId);
        String ids = usrId.getString("id");
        columns2.add("a._key == '" + ids + "'");
        logger.info("columns2" + columns2);
      });

      List<String> response3 = new ArrayList<>();
      ArangoCursor<String> cursor2 = null;
      String columnIds2 = String.join(" OR ", columns2);
      String queryToBeExecuted3 = "for a in " + userRegistration + "\r\n" + "filter " + columnIds2
          + "\r\n" + "return {" + name + ":a.FirstName,id:a._key}";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
      // ArangoCursor<Object> cursor = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted3, String.class);
        response3 = cursor2.asListRemaining();
        logger.info(String.valueOf(response3));

      } catch (Exception e) {
        log.error("Exception while getEditTeamDetails1_3 : " + e.getMessage().toString());
      }
      columns2.clear();
      JSONObject teamDetail1 = new JSONObject();
      // teamDetail1.put("id", id);
      teamDetail1.put("role", name);
      for (int l = 0; l < response3.size(); l++) {
        JSONObject roleuserObject = new JSONObject();
        JSONObject tuser = new JSONObject(response3.get(l));
        logger.info("tuser" + tuser);
        roleuserObject.put("name", tuser.getString(name));
        roleuserObject.put("id", tuser.getString("id"));
        // Collection k = teamDetail.values();
        keycolumns.add(roleuserObject);
      }
      // JSONObject team=new JSONObject();
      // team.put("teamId", teamId);
      teamDetail1.put("users", keycolumns);
      teamDetail1.put("teamId", teamId);
      keycolumns.clear();
      response4.add(teamDetail1.toMap());
      // response4.add(team.toString());

    }

    return response4;
  }


  public List<HashMap> addUpdateTeam(HashMap teamdetails) throws ServiceException {

    List<HashMap> response2 = new ArrayList<>();
    // List<Object> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response6 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRoles);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String team = teamdetails.get("teamId").toString();
    String teamId = "Teams/" + team;
    List<HashMap> roles = (List<HashMap>) teamdetails.get("roles");

    // List<Object> userslist=new ArrayList<>();

    List<Object> response1 = new ArrayList<>();

    ArangoCursor<Object> cursor2 = null;
    String queryToBeExecuted = "for doc in userRoles\r\n" + "filter doc._from=='" + teamId
        + "'\r\n" + "remove doc in userRoles";
    // String queryToBeExecuted="for doc in userRoles\r\n"
    // + "filter doc._from=='"+teamId+"' AND doc._to=='"+roleid+"'\r\n"
    // + "remove doc in userRoles";
    logger.info(queryToBeExecuted);

    try {
      cursor2 = arangoDB.query(queryToBeExecuted, Object.class);
      response1 = cursor2.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while addUpdateTeam : " + e.getMessage().toString());
    }

    roles.forEach(z -> {

      JSONObject x = new JSONObject(z);
      logger.info("x" + x);
      String role = x.get("role").toString();
      JSONArray users = x.getJSONArray("users");
      String roleid = "NodeTypes/" + role;

      List<Object> usersList = new ArrayList<>();
      users.forEach(u -> {
        JSONObject userObjects = new JSONObject(u.toString());
        logger.info(String.valueOf(userObjects));
        String id = userObjects.get("id").toString();
        JSONObject newUserObject = new JSONObject();
        newUserObject.put("id", id);
        usersList.add(newUserObject);
      });

      List<Object> response3 = new ArrayList<>();

      ArangoCursor<Object> cursor1 = null;
      String queryToBeExecuted1 = "insert {_from:'" + teamId + "',_to:'" + roleid + "',users:"
          + usersList + ",createdby:'Admin',createdon:'" + datestr
          + "',lastmodifiedby:'Admin',lastmodifiedon:'" + datestr + "'} In " + userRoles
          + "\r\n";

      logger.info(queryToBeExecuted1);

      try {
        cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
        response3 = cursor1.asListRemaining();
        logger.info(String.valueOf(response3));
      } catch (Exception e) {
        log.error("Exception while addUpdateTeam_2: " + e.getMessage().toString());
      }

    });

    return response2;


  }


  public List<HashMap> addUpdateMatrixTeam(HashMap teamdetails) throws ServiceException {

    List<HashMap> response2 = new ArrayList<>();
    // List<Object> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response6 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRoles);
    String team = teamdetails.get("teamId").toString();
    String teamId = "Teams/" + team;
    List<HashMap> context = (List<HashMap>) teamdetails.get("context");

    context.forEach(a -> {
      JSONObject x = new JSONObject(a);
      String contextname = x.getString("contextName");

      List<Object> response1 = new ArrayList<>();

      ArangoCursor<Object> cursor2 = null;

      String queryToBeExecuted = "for doc in userRoles\r\n" + "filter doc._from=='" + teamId
          + "'AND doc." + contextname + "\r\n" + "remove doc in userRoles";

      // String queryToBeExecuted="for doc in userRoles\r\n"
      // + "filter doc._from=='"+teamId+"' AND doc._to=='"+roleid+"'AND doc."+contextname+"\r\n"
      // + "remove doc in userRoles";
      logger.info(queryToBeExecuted);

      try {
        cursor2 = arangoDB.query(queryToBeExecuted, Object.class);
        response1 = cursor2.asListRemaining();
        logger.info(String.valueOf(response1));
      } catch (Exception e) {
        log.error("Exception while addUpdateMatrixTeam : " + e.getMessage().toString());
      }

      char[] chrs = contextname.toCharArray();
      char lastChar = chrs[7];
      int contextCounter = Character.getNumericValue(lastChar);
      JSONArray business = x.getJSONArray("business");
      JSONArray dataDomain = x.getJSONArray("dataDomain");
      JSONArray geography = x.getJSONArray("geography");
      JSONArray product = x.getJSONArray("product");
      List<Object> contexts = new ArrayList<>();

      business.forEach(b -> {
        JSONObject busines = new JSONObject(b.toString());
        logger.info("business" + busines);
        String id = busines.get("key").toString();
        JSONObject busiObject = new JSONObject();
        busiObject.put("id", id);
        logger.info("busiObject" + busiObject);
        contexts.add(busiObject);
      });
      dataDomain.forEach(d -> {
        JSONObject domain = new JSONObject(d.toString());
        String id = domain.getString("key");
        JSONObject dataDomainObject = new JSONObject();
        dataDomainObject.put("id", id);
        logger.info("dataDomainObject" + dataDomainObject);
        contexts.add(dataDomainObject);
      });
      logger.info("dataDomain" + dataDomain);
      geography.forEach(g -> {
        JSONObject geograph = new JSONObject(g.toString());
        String id = geograph.getString("key");
        JSONObject geographyObject = new JSONObject();
        geographyObject.put("id", id);
        logger.info("geographyObject" + geographyObject);
        contexts.add(geographyObject);
      });
      product.forEach(p -> {
        JSONObject prodct = new JSONObject(p.toString());
        String id = prodct.getString("key");
        JSONObject productObject = new JSONObject();
        productObject.put("id", id);
        logger.info("productObject" + productObject);
        contexts.add(productObject);
      });
      // contexts.add(busiObject);
      // contexts.add(dataDomainObject);
      // contexts.add(geographyObject);
      // contexts.add(productObject);
      // logger.info("contexts"+contexts);
      JSONArray roles = x.getJSONArray("roles");
      if (!roles.isEmpty()) {
        roles.forEach(z -> {
          logger.info("z" + z);
          JSONObject y = new JSONObject(z.toString());
          logger.info("y" + y);
          String role = y.get("role").toString();
          JSONArray users = y.getJSONArray("users");
          String roleid = "NodeTypes/" + role;

          List<Object> usersList = new ArrayList<>();
          users.forEach(u -> {
            JSONObject userObjects = new JSONObject(u.toString());
            logger.info(String.valueOf(userObjects));
            String id = userObjects.get("id").toString();
            JSONObject newUserObject = new JSONObject();
            newUserObject.put("id", id);
            usersList.add(newUserObject);
          });

          List<Object> response3 = new ArrayList<>();
          ArangoCursor<Object> cursor1 = null;
          String queryToBeExecuted1 = "insert {_from:'" + teamId + "',_to:'" + roleid
              + "',users:" + usersList + "," + contextname + ":" + contexts
              + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
              + ",'contextCounter':" + contextCounter + "} In " + userRoles + "\r\n";

          logger.info(queryToBeExecuted1);

          try {
            cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
            response3 = cursor1.asListRemaining();
            logger.info(String.valueOf(response3));
          } catch (Exception e) {
            log.error("Exception while addUpdateMatrixTeam_2: " + e.getMessage().toString());
          }

        });
      }

    });

    return response2;
  }

  public List<HashMap> getcreateMatrixTeam(HashMap pinDetails) throws ServiceException {
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response6 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, pincollection);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String displayname = pinDetails.get("displayname").toString();
    String Description = pinDetails.get("Description").toString();
    String cover = pinDetails.get("cover").toString();
    String classification = pinDetails.get("classification").toString();
    String type = pinDetails.get("type").toString();
    String createdby = pinDetails.get("createdby").toString();
    String createdon = pinDetails.get("createdon").toString();
    String lastmodifiedby = pinDetails.get("lastmodifiedby").toString();
    String lastmodifiedon = pinDetails.get("lastmodifiedon").toString();
    List<String> tags = (List<String>) pinDetails.get("tags");
    List<String> columns1 = new ArrayList<String>();
    for (int i = 0; i < tags.size(); i++) {
      columns1.add("'" + tags.get(i) + "'");
    }
    // String tags=pinDetails.get("tags").toString();
    String team = pinDetails.get("teamName").toString();

    ArangoCursor<HashMap> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    String queryToBeExecuted = "INSERT {displayName:'" + displayname + "',Description:'"
        + Description + "',classification:'" + classification + "',type:'" + type
        + "',createdby:'" + createdby + "',createdon:'" + createdon + "',lastmodifiedby:'"
        + lastmodifiedby + "',lastmodifiedon:'" + lastmodifiedon + "',tags:" + columns1
        + ",pinNodes:[],pinCollection:[]" + "} In " + pincollection + "\r\n" + "return NEW._id";
    logger.info(queryToBeExecuted);

    try {
      cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while getcreateMatrixTeam_2 : " + e.getMessage().toString());
    }

    String query = "FOR c IN " + Teams + "\r\n" + "filter c.displayName == '" + team + "'\r\n"
        + "return c";

    logger.info(query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response2 = cursor.asListRemaining();
      logger.info(String.valueOf(response2));
    } catch (Exception e) {
      log.error("Exception while getcreateMatrixTeam_3: " + e.getMessage().toString());
    }
    JSONObject responseInfo = new JSONObject();
    response2.forEach(t -> {
      JSONObject teamDetails = new JSONObject(t);
      logger.info("teamDetails" + teamDetails);
      String teamId = teamDetails.get("_id").toString();
      responseInfo.put("teamId", teamId);
    });
    String queryToBeExecuted3 = "INSERT {_from:'" + response1.get(0) + "', _to:'"
        + responseInfo.getString("teamId") + "',createdby:'" + createdby + "',createdon:'"
        + createdon + "',lastmodifiedby:'" + lastmodifiedby + "',lastmodifiedon:'"
        + lastmodifiedon + "'} IN " + pincollectionedges + "";
    logger.info(queryToBeExecuted3);
    try {
      cursor1 = arangoDB.query(queryToBeExecuted3, Object.class);
      response3 = cursor1.asListRemaining();
      logger.info(String.valueOf(response3));
    } catch (Exception e) {
      log.error("Exception while executing  getcreateMatrixTeam_4 : " + e.getMessage().toString());
    }

    return response2;

  }

  public List<HashMap> getNewContextMatrixTeam(HashMap teamdetails) throws ServiceException {
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response6 = new ArrayList<>();
    List<Object> qresponse2 = new ArrayList<>();
    List<String> qresponse4 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    JSONObject counterObj = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRoles);
    String team = teamdetails.get("teamId").toString();
    String teamId = "Teams/" + team;
    // List<HashMap> context=(List<HashMap>) teamdetails.get("context");

    String queryToBeExecuted1 = "for doc in " + Teams + "\r\n" + "filter doc._key == '" + team
        + "'\r\n" + "return doc._id";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    ArangoCursor<Object> cursor3 = null;
    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, Object.class);
      qresponse2 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getNewContextMatrixTeam : " + e.getMessage().toString());
    }

    String query = "for a in " + userRoles + "\r\n" + "filter a._from == '" + qresponse2.get(0)
        + "'\r\n" + "return a";
    logger.info("query----->" + query);
    ArangoCursor<String> cursor4 = null;
    try {

      cursor4 = arangoDB.query(query, String.class);
      qresponse4 = cursor4.asListRemaining();
      logger.info(String.valueOf(qresponse4));

    } catch (Exception e) {
      log.error("Exception while getNewContextMatrixTeam_2 : " + e.getMessage().toString());
    }

    List<String> contextName = new ArrayList<>();
    HashSet<String> contextName2 = new HashSet<String>();
    HashSet<Integer> contextName3 = new HashSet<Integer>();
    List<String> contextColumns = new ArrayList<>();
    // response4.clear();
    for (int i = 0; i < qresponse4.size(); i++) {
      JSONObject s = new JSONObject(qresponse4.get(i));
      contextName.clear();
      logger.info("s" + s);
      Set<String> keys = s.keySet();
      Object[] namesArray = keys.toArray();
      for (int j = 0; j < namesArray.length; j++) {
        logger.info(j + ": " + namesArray[j]);
        contextName.add(namesArray[j].toString());
      }

      for (int x = 0; x < contextName.size(); x++) {
        if (contextName.get(x).contains("lastmodifiedon")
            || contextName.get(x).contains("_from") || contextName.get(x).contains("createdby")
            || contextName.get(x).contains("_rev")
            || contextName.get(x).contains("lastmodifiedby")
            || contextName.get(x).contains("_id") || contextName.get(x).contains("_to")
            || contextName.get(x).contains("_key") || contextName.get(x).contains("createdon")
            || contextName.get(x).contains("users")
            || contextName.get(x).contains("contextCounter")) {

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
    List<Integer> listWithUniqueContextValues1 = new ArrayList<>(setWithUniqueValues1);

    if (!listWithUniqueContextValues1.isEmpty()) {
      int largest = Collections.max(listWithUniqueContextValues1);
      logger.info("largest" + largest);
      int largest1 = largest + 1;

      counterObj.put("counter", largest1);
    } else {
      counterObj.put("counter", 1);
    }

    List<HashMap> business = (List<HashMap>) teamdetails.get("business");
    List<HashMap> dataDomain = (List<HashMap>) teamdetails.get("dataDomain");
    // teamdetails.get("dataDomain").toString();
    // .getJSONArray("dataDomain");
    List<HashMap> geography = (List<HashMap>) teamdetails.get("geography");
    List<HashMap> product = (List<HashMap>) teamdetails.get("product");
    List<Object> contexts = new ArrayList<Object>();
    business.forEach(b -> {
      JSONObject busines = new JSONObject(b);
      logger.info("business" + busines);
      String id = busines.get("key").toString();
      JSONObject busiObject = new JSONObject();
      busiObject.put("id", id);
      contexts.add(busiObject);
    });

    dataDomain.forEach(d -> {
      JSONObject domain = new JSONObject(d);
      String id = domain.getString("key");
      JSONObject dataDomainObject = new JSONObject();
      dataDomainObject.put("id", id);
      contexts.add(dataDomainObject);
    });

    // String datadomainId=dataDomain.getString("key");
    // dataDomainObject.put("id", datadomainId);

    geography.forEach(g -> {
      JSONObject geograph = new JSONObject(g);
      String id = geograph.getString("key");
      JSONObject geographyObject = new JSONObject();
      geographyObject.put("id", id);
      contexts.add(geographyObject);
    });

    product.forEach(p -> {
      JSONObject prodct = new JSONObject(p);
      String id = prodct.getString("key");
      JSONObject productObject = new JSONObject();
      productObject.put("id", id);
      contexts.add(productObject);
    });
    // contexts.add(busiObject);
    // contexts.add(dataDomainObject);
    // contexts.add(geographyObject);
    // contexts.add(productObject);
    List<HashMap> roles = (List<HashMap>) teamdetails.get("roles");
    if (!roles.isEmpty()) {
      roles.forEach(z -> {
        logger.info("z" + z);
        JSONObject y = new JSONObject(z);
        logger.info("y" + y);
        String role = y.get("role").toString();
        JSONArray users = y.getJSONArray("users");
        String roleid = "NodeTypes/" + role;

        List<Object> usersList = new ArrayList<>();
        users.forEach(u -> {
          JSONObject userObjects = new JSONObject(u.toString());
          logger.info(String.valueOf(userObjects));
          String id = userObjects.get("id").toString();
          JSONObject newUserObject = new JSONObject();
          newUserObject.put("id", id);
          usersList.add(newUserObject);
        });
        List<Object> response1 = new ArrayList<>();

        ArangoCursor<Object> cursor1 = null;
        String queryToBeExecuted = "insert {_from:'" + teamId + "',_to:'" + roleid + "',users:"
            + usersList + "," + "Context" + counterObj.getInt("counter") + ":" + contexts
            + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
            + ",contextCounter:" + counterObj.getInt("counter") + "} In " + userRoles + "\r\n";
        // String queryToBeExecuted7="INSERT {_from:'"+ from +"', _to:'"+ s.getString("id")
        // +"',createdby:'Admin',createdon:'65423452',lastmodifiedby:'Admin',lastmodifiedon:'65423452',users:"+
        // s.getJSONArray(name) +",'"+"Context"+counterObj.getInt("counter")+"':"+ catgslist
        // +",contextCounter:"+counterObj.getInt("counter")+"} IN "+ userRoles +"";

        logger.info(queryToBeExecuted1);

        try {
          cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
          response1 = cursor1.asListRemaining();
          logger.info(String.valueOf(response1));
        } catch (Exception e) {
          log.error("Exception while getNewContextMatrixTeam_3: " + e.getMessage().toString());
        }

      });
    }
    return response2;
  }

  public List<Object> getContextInfo(String contextName, String key) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for n in Teams\r\n" + "filter n." + contextName
        + "!= null && n._key == '" + key + "'\r\n" + "for s in n." + contextName + "\r\n"
        + "for m in s.categories\r\n" + "return m.id";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while executing  Query: " + e.getMessage().toString());
    }

    List<String> column = new ArrayList<>();
    for (int i = 0; i < response.size(); i++) {
      column.add("'" + response.get(i) + "'");
    }

    String query =
        "for n in Business\r\n" + "filter n._id in " + column + "\r\n" + "return n";

    logger.info("queryToBeExecuted----->" + query);

    ArangoCursor<Object> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while executing  Query: " + e.getMessage().toString());
    }

    return response1;

  }

  public List<Object> removeContext(String teamId, String context) throws ServiceException {
    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    String from = "Teams/" + teamId;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query =
        "for a in " + userRoles + "\r\n" + "filter a._from == '" + from + "'\r\n" + "return a";
    logger.info("query----->" + query);
    ArangoCursor<String> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query, String.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while removeContext : " + e.getMessage().toString());
    }

    List<String> contextName = new ArrayList<>();
    HashSet<String> contextName2 = new HashSet<String>();
    HashSet<Integer> contextName3 = new HashSet<Integer>();
    List<String> contextColumns = new ArrayList<>();
    // response4.clear();
    for (int i = 0; i < response1.size(); i++) {
      JSONObject s = new JSONObject(response1.get(i));
      contextName.clear();
      logger.info("s" + s);
      Set<String> keys = s.keySet();
      Object[] namesArray = keys.toArray();
      for (int j = 0; j < namesArray.length; j++) {
        logger.info(j + ": " + namesArray[j]);
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
    logger.info("setWithUniqueValues" + setWithUniqueValues);
    List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
    logger.info("listWithUniqueContextValues" + listWithUniqueContextValues);

    int count = listWithUniqueContextValues.size();
    if (count == 1) {
      String queryToBeExecuted = "for a in " + userRoles + "\r\n" + "filter a._from == '" + from
          + "' AND a." + context + "!=null\r\n" + "remove a._key in " + userRoles + "";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      ArangoCursor<Object> cursor = null;
      try {

        cursor = arangoDB.query(queryToBeExecuted, Object.class);
        response = cursor.asListRemaining();
        logger.info(String.valueOf(response));

      } catch (Exception e) {
        log.error("Exception while removeContext_2 : " + e.getMessage().toString());
      }

      String queryToBeExecuted1 = "for a in " + Teams + "\r\n" + "filter a._key == '" + teamId
          + "'\r\n" + "remove a._key in " + Teams + "";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      ArangoCursor<String> cursor2 = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted1, String.class);
        response2 = cursor2.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while removeContext_3 : " + e.getMessage().toString());
      }
    } else {

      String queryToBeExecuted = "for a in " + userRoles + "\r\n" + "filter a._from == '" + from
          + "' AND a." + context + "!=null\r\n" + "remove a._key in " + userRoles + "";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      ArangoCursor<Object> cursor = null;
      try {

        cursor = arangoDB.query(queryToBeExecuted, Object.class);
        response = cursor.asListRemaining();
        logger.info(String.valueOf(response));

      } catch (Exception e) {
        log.error("Exception while removeContext_4 : " + e.getMessage().toString());
      }
    }

    return response;
  }


  public String getEditMatrixContext(String teamId, String contextName) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns = new ArrayList<>();

    JSONObject teamDetails = new JSONObject();

    List<Object> keycolumns = new ArrayList<>();
    String from = "Teams/" + teamId;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for a in " + userRoles + "\r\n" + "filter a._from == '" + from
        + "' AND a." + contextName + "!=null\r\n" + "return a";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response2 = cursor.asListRemaining();
      logger.info(String.valueOf(response2));

    } catch (Exception e) {
      log.error("Exception while getEditMatrixContext : " + e.getMessage().toString());
    }

    ArrayList<Object> response4 = new ArrayList<>();
    JSONObject teamDetail1 = new JSONObject();

    response2.forEach(a -> {
      // response4.clear();
      JSONObject contxt = new JSONObject(a);
      logger.info("contxt" + contxt);
      String to = contxt.getString("_to");
      List<String> columns3 = new ArrayList<String>();
      if (contxt.has("users")) {
        JSONArray users = contxt.getJSONArray("users");
        users.forEach(y -> {
          JSONObject usrId = new JSONObject(y.toString());
          logger.info("usrId" + usrId);
          String ids = usrId.getString("id");
          columns.add("a._key == '" + ids + "'");
          logger.info("columns" + columns);
        });
      }
      String[] Id = to.split("/");
      String nodetypes = Id[0];
      String name = Id[1];
      logger.info("name" + name);

      JSONArray context = contxt.getJSONArray(contextName);
      teamDetails.put("context", contextName);
      context.forEach(z -> {
        JSONObject categoryId = new JSONObject(z.toString());
        String cids = categoryId.getString("id");
        columns3.add("a._key == '" + cids + "'");
      });

      ArangoCursor<HashMap> cursor2 = null;
      List<HashMap> response3 = new ArrayList<>();
      String columnIds = String.join(" OR ", columns);
      String queryToBeExecuted3 = "for a in " + userRegistration + "\r\n" + "filter " + columnIds
          + "\r\n" + "return {" + name + ":a.FirstName,id:a._key}";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
      try {

        cursor2 = arangoDB.query(queryToBeExecuted3, HashMap.class);
        response3 = cursor2.asListRemaining();
        logger.info(String.valueOf(response3));

      } catch (Exception e) {
        log.error("Exception while getEditMatrixContext_2 : " + e.getMessage().toString());
      }

      columns.clear();

      JSONObject teamDetail2 = new JSONObject();
      // teamDetail1.put("id", to);
      // teamDetail1.put("name", name);
      JSONObject teamDetail3 = new JSONObject();
      for (int l = 0; l < response3.size(); l++) {

        JSONObject roleuserObject = new JSONObject();
        JSONObject tuser = new JSONObject(response3.get(l));
        logger.info("tuser" + tuser);
        roleuserObject.put("name", tuser.getString(name));
        roleuserObject.put("id", tuser.getString("id"));
        // Collection k = teamDetail.values();
        keycolumns.add(roleuserObject);


      }
      teamDetail3.put(name, keycolumns);
      keycolumns.clear();
      response4.add(teamDetail3.toMap());
      logger.info("teamDetail3" + teamDetail3);

      List<Object> Business = new ArrayList<>();
      List<Object> DataDomain = new ArrayList<>();
      List<Object> Geography = new ArrayList<>();
      List<Object> Products = new ArrayList<>();
      List<HashMap> response5 = new ArrayList<>();
      ArangoCursor<HashMap> cursor4 = null;
      String columnIds3 = String.join(" OR ", columns3);
      String queryToBeExecuted4 = "for a in Business\r\n" + "filter " + columnIds3
          + "\r\n" + "return {name:a.name,type:a.typeName,id:a._key}";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted4);

      try {

        cursor4 = arangoDB.query(queryToBeExecuted4, HashMap.class);
        response5 = cursor4.asListRemaining();
        logger.info(String.valueOf(response5));

      } catch (Exception e) {
        log.error("Exception while getEditMatrixContext_3 : " + e.getMessage().toString());
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
        String id = cate.getString("id");
        if (type.equals("Data Domain")) {
          categoryObject.put("name", cname);
          categoryObject.put("key", id);
          DataDomain.add(categoryObject);
        } else if (type.equals("Product")) {
          categoryObject.put("name", cname);
          categoryObject.put("key", id);
          Products.add(categoryObject);
        } else if (type.equals("Region")) {
          categoryObject.put("name", cname);
          categoryObject.put("key", id);
          Geography.add(categoryObject);
        } else if (type.equals("Line of Business")) {
          categoryObject.put("name", cname);
          categoryObject.put("key", id);
          Business.add(categoryObject);
        }
      });
      if (!DataDomain.isEmpty()) {
        teamDetails.put("Data Domain", DataDomain);
      }
      if (!Products.isEmpty()) {
        teamDetails.put("Products", Products);
      }
      if (!Geography.isEmpty()) {
        teamDetails.put("Geography", Geography);
      }
      if (!Business.isEmpty()) {
        teamDetails.put("Business", Business);
      }

    });

    response2.forEach(b -> {
      JSONObject x = new JSONObject(b);
      if (x.has("users")) {
        // JSONArray users=x.getJSONArray("users");

        teamDetails.put("roles", response4);
      } else {
        teamDetails.put("roles", response4);
      }
    });

    teamDetails.put("teamId", teamId);
    logger.info("response4" + response4);
    logger.info("teamDetails" + teamDetails);

    return teamDetails.toString();
  }

  public List<HashMap> getmyTags(List<String> tag, String id) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    ArrayList<JSONObject> nodesInfo = new ArrayList<JSONObject>();
    JSONObject taginfo = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    for (int i = 0; i < tag.size(); i++) {
      String Tag = tag.get(i);
      String queryToBeExecuted = "for doc in " + arangonodesCollection + "\r\n"
          + "filter doc.id =='" + id + "'\r\n" + "UPDATE MERGE(doc,{ Tags: APPEND(doc.Tags,['"
          + Tag + "'],true)}) IN " + arangonodesCollection + "\r\n" + "RETURN doc";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      ArangoCursor<HashMap> cursor = null;
      try {
        cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
        response = cursor.asListRemaining();
      } catch (Exception e) {
        log.error("Exception while getmyTags : " + e.getMessage().toString());
      }
      logger.info("response" + response);
      response.forEach(a -> {
        JSONObject res = new JSONObject(a);
        String name = res.getString("name");
        String Id = res.getString("id");
        taginfo.put("DisplayName", name);
        taginfo.put("Id", Id);
        taginfo.put("Tag", Tag);
        nodesInfo.add(taginfo);
        importDocuments2Arango(nodesInfo.toString(), tagsCollection);
        nodesInfo.clear();
      });

    }

    return response;

  }

  public List<HashMap> getmyTags1(String id, HashMap tagsList) throws ServiceException {

    List<String> tag = (List<String>) tagsList.get("tags");
    List<HashMap> response = new ArrayList<>();
    // List<HashMap> response1 = new ArrayList<>();
    ArrayList<JSONObject> nodesInfo = new ArrayList<JSONObject>();
    JSONObject taginfo = new JSONObject();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoEdgeCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, tagsEdges);
    ArangoCollection arangoCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, tagsCollection);
    String Id = "Nodes/" + id + "";
    for (int i = 0; i < tag.size(); i++) {
      String Tag = tag.get(i);
      String queryToBeExecuted = "INSERT {Tag:'" + Tag + "',_key:'" + Tag + "'} in "
          + tagsCollection + "\r\n" + "return NEW";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted);
      ArangoCursor<HashMap> cursor = null;
      try {
        cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
        response = cursor.asListRemaining();
      } catch (Exception e) {
        // log.info("Exception while getmyTags1: " + e.getMessage().toString());
        log.info("tag already existed in collection");
      }
      logger.info("response" + response);
      if (response.isEmpty()) {

        ArangoCursor<HashMap> cursor1 = null;
        List<HashMap> response1 = new ArrayList<>();
        String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n" + "filter doc._key=='"
            + Tag + "'\r\n" + "INSERT {_from: doc._id, _to: '" + Id
            + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
        try {
          cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
          response1 = cursor1.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while getmyTags1_2 : " + e.getMessage().toString());
        }

      } else {
        response.forEach(a -> {
          JSONObject s = new JSONObject(a);
          String key = s.getString("_key");
          String tags = s.getString("Tag");
          String ids = s.getString("_id");

          ArangoCursor<HashMap> cursor1 = null;
          List<HashMap> response1 = new ArrayList<>();
          String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n"
              + "filter doc._key=='" + key + "'\r\n" + "INSERT {_from:'" + ids + "', _to: '" + Id
              + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges + "";
          logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
          try {
            cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
            response1 = cursor1.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while getmyTags1_3  : " + e.getMessage().toString());
          }
        });
      }
    }
    return response;
  }


  public List<Object> getmyTagsDropDown() throws ServiceException {

    List<Object> response = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "let tags=(\r\n" + "for a in TagsCollection\r\n"
        + "return a.Tag\r\n" + ")\r\n" + "let tagsHistory=(\r\n" + "for b in TagsHistory\r\n"
        + "return b.tag\r\n" + ")\r\n" + "return UNION_DISTINCT(tags,tagsHistory)";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getmyTagsDropDown : " + e.getMessage().toString());
    }

    return response;

  }

  public List<String> gettingTags(String id) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<String> tags = new ArrayList<>();
    String Id = "Nodes/" + id + "";
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // String queryToBeExecuted ="for doc in Nodes\r\n"
    // + "filter doc.id =='"+ id +"' AND doc.Tags !=null \r\n"
    // + "return doc.Tags";
    String queryToBeExecuted =
        "for a in " + tagsEdges + "\r\n" + "filter a._to =='" + Id + "'\r\n" + "return a._from";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while gettingTags : " + e.getMessage().toString());
    }
    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < response.size(); i++) {
      columns.add("a._id =='" + response.get(i) + "'");
      logger.info("columns-->" + columns);
    }
    String columnIds1 = String.join(" OR ", columns);

    if (!columnIds1.isEmpty()) {
      ArangoCursor<HashMap> cursor1 = null;
      String queryToBeExecuted1 =
          "for a in " + tagsCollection + "\r\n" + "filter " + columnIds1 + "\r\n" + "return a";

      logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

      try {

        cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
        response1 = cursor1.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while gettingTags_2 : " + e.getMessage().toString());
      }
    }

    for (int j = 0; j < response1.size(); j++) {
      JSONObject s = new JSONObject(response1.get(j));
      String tag = s.getString("Tag");
      tags.add(tag);
    }

    String queryToBeExecuted2 = "for a in " + arangonodesCollection + "\r\n"
        + "filter a._key == '" + id + "'\r\n" + "return a";

    logger.info("queryToBeExecuted1----->" + queryToBeExecuted2);

    ArangoCursor<HashMap> cursor2 = null;

    try {

      cursor2 = arangoDB.query(queryToBeExecuted2, HashMap.class);
      response2 = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while gettingTags_3 : " + e.getMessage().toString());
    }

    for (int k = 0; k < response2.size(); k++) {
      JSONObject nodes = new JSONObject(response2.get(k));
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
          logger.info(String.valueOf(attributes));
          if (!attributes.isEmpty()) {
            if (attributes.get("name").toString().equals("tag")) {
              String value = attributes.getString("value");
              tags.add(value);
            }
          }
        });
      }
    }

    return tags;

  }


  public List<Object> getmyRatings(String key, String userName, String userId, String ratings)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<Object> ratingsList = new ArrayList<>();
    JSONObject nodeRatingInfo = new JSONObject();
    int k = 100;
    int avgRating = 0;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "FOR t IN " + arangonodesCollection + "\r\n" + "filter t._key == '"
        + key + "'\r\n" + "UPDATE MERGE(t, { Ratings: APPEND(t.Ratings,[{\"userName\": '"
        + userName + "', \"userId\": '" + userId + "', \"ratings\":'" + ratings
        + "'}],true)}) IN " + arangonodesCollection + "\r\n" + "RETURN NEW";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {
      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getmyRatings : " + e.getMessage().toString());
    }
    List<String> columnstot = new ArrayList<>();
    String queryToBeExecuted1 = "FOR t IN " + arangonodesCollection + "\r\n"
        + "filter t._key == '" + key + "'\r\n" + "for s in t.Ratings\r\n"
        + "collect r=s.Rating WITH COUNT into nodesCount\r\n" + "return nodesCount";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    ArangoCursor<Object> cursor1 = null;
    try {
      cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getmyRatings_2 : " + e.getMessage().toString());
    }
    columnstot.add(response1.get(0).toString());
    String queryToBeExecuted2 = "FOR t IN " + arangonodesCollection + "\r\n"
        + "filter t._key == '" + key + "'\r\n" + "return t";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted2);

    ArangoCursor<HashMap> cursor2 = null;
    try {

      cursor2 = arangoDB.query(queryToBeExecuted2, HashMap.class);
      response2 = cursor2.asListRemaining();
      logger.info(String.valueOf(response2));

    } catch (Exception e) {
      log.error("Exception while getmyRatings_3: " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    List<String> rating1 = new ArrayList<String>();
    List<String> rating2 = new ArrayList<String>();
    List<String> rating3 = new ArrayList<String>();
    List<String> rating4 = new ArrayList<String>();
    List<String> rating5 = new ArrayList<String>();

    response2.forEach(x -> {
      logger.info(String.valueOf(x));
      JSONObject s = new JSONObject(x);
      logger.info(String.valueOf(s));
      JSONArray rating = new JSONArray();
      rating = s.getJSONArray("Ratings");
      logger.info(String.valueOf(rating));
      rating.forEach(z -> {
        JSONObject obj = new JSONObject(z.toString());
        logger.info(String.valueOf(obj));
        String ratingcount = obj.getString("ratings");
        logger.info(ratingcount);
        int i = 4;
        if (obj.get("ratings").equals("1")) {
          rating1.add(ratingcount);
        } else if (obj.get("ratings").equals("2")) {
          rating2.add(ratingcount);
        } else if (obj.get("ratings").equals("3")) {
          rating3.add(ratingcount);
        } else if (obj.get("ratings").equals("4")) {
          rating4.add(ratingcount);
        } else if (obj.get("ratings").equals("5")) {
          rating5.add(ratingcount);
        }
      });

    });
    int r1 = rating1.size();
    int r2 = rating2.size();
    int r3 = rating3.size();
    int r4 = rating4.size();
    int r5 = rating5.size();
    int total = Integer.parseInt(columnstot.get(0));
    int ratingtot = 1 * r1 + 2 * r2 + 3 * r3 + 4 * r4 + 5 * r5;
    // avgRating=1*r1+2*r2+3*r3+4*r4+5*r5/total;
    avgRating = ratingtot / total;
    logger.info("avgRating" + avgRating);
    nodeRatingInfo.put("avgRating", avgRating);
    nodeRatingInfo.put("ratingsCount", total);
    ratingsList.add(nodeRatingInfo.toMap());
    int percentage5 = (r5 * k / total);
    logger.info(String.valueOf(percentage5));
    int percentage4 = (r4 * k / total);
    logger.info(String.valueOf(percentage4));
    int percentage3 = (r3 * k / total);
    logger.info(String.valueOf(percentage3));
    int percentage2 = (r2 * k / total);
    logger.info(String.valueOf(percentage2));
    int percentage1 = (r1 * k / total);
    logger.info(String.valueOf(percentage1));

    // String queryToBeExecuted3="FOR t IN "+ arangonodesCollection +"\r\n"
    // + "filter t._key == '"+ key +"'\r\n"
    // + "for s in t.Ratings\r\n"
    // + "collect r=s.Rating WITH COUNT into nodesCount\r\n"
    // + "return nodesCount";
    String queryToBeExecuted3 = "FOR c IN " + arangonodesCollection + "\r\n"
        + "filter c._key == '" + key + "'\r\n" + "UPDATE c WITH { avgRating:'" + avgRating
        + "',ratingsCount:'" + total + "' } IN " + arangonodesCollection + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

    // ArangoCursor<Object> cursor1 = null;
    try {
      cursor1 = arangoDB.query(queryToBeExecuted3, Object.class);
      response3 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getmyRatings_4: " + e.getMessage().toString());
    }
    return ratingsList;
  }

  public List<Object> getmyRatingsInfo(String key, String userId) throws ServiceException {
    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> ratingsList = new ArrayList<>();
    List<Object> ratingsList1 = new ArrayList<>();
    JSONObject nodesinfo1 = new JSONObject();
    JSONObject nodeRatingInfo = new JSONObject();
    int k = 100;
    double avgRating;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    List<String> columnstot = new ArrayList<>();
    String queryToBeExecuted1 = "FOR t IN " + arangonodesCollection + "\r\n"
        + "filter t._key == '" + key + "'AND t.Ratings !=null\r\n" + "for s in t.Ratings\r\n"
        + "collect r=s.Rating WITH COUNT into nodesCount\r\n" + "return nodesCount";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    ArangoCursor<Object> cursor1 = null;
    try {
      cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while getmyRatingsInfo : " + e.getMessage().toString());
    }

    String queryToBeExecuted2 = "FOR t IN " + arangonodesCollection + "\r\n"
        + "filter t._key == '" + key + "'\r\n" + "return t";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted2);

    ArangoCursor<HashMap> cursor2 = null;
    try {

      cursor2 = arangoDB.query(queryToBeExecuted2, HashMap.class);
      response2 = cursor2.asListRemaining();
      logger.info(String.valueOf(response2));

    } catch (Exception e) {
      log.error("Exception while getmyRatingsInfo_2 : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    List<String> rating1 = new ArrayList<String>();
    List<String> rating2 = new ArrayList<String>();
    List<String> rating3 = new ArrayList<String>();
    List<String> rating4 = new ArrayList<String>();
    List<String> rating5 = new ArrayList<String>();

    if (!(response1.isEmpty())) {
      columnstot.add(response1.get(0).toString());
      response2.forEach(x -> {
        logger.info(String.valueOf(x));
        JSONObject s = new JSONObject(x);
        logger.info(String.valueOf(s));
        JSONArray rating = new JSONArray();
        rating = s.getJSONArray("Ratings");
        logger.info(String.valueOf(rating));
        rating.forEach(z -> {
          JSONObject obj = new JSONObject(z.toString());
          logger.info(String.valueOf(obj));
          String ratingcount = obj.getString("ratings");
          logger.info(ratingcount);
          int i = 4;
          if (obj.get("ratings").equals("1")) {
            rating1.add(ratingcount);
          } else if (obj.get("ratings").equals("2")) {
            rating2.add(ratingcount);
          } else if (obj.get("ratings").equals("3")) {
            rating3.add(ratingcount);
          } else if (obj.get("ratings").equals("4")) {
            rating4.add(ratingcount);
          } else if (obj.get("ratings").equals("5")) {
            rating5.add(ratingcount);
          }
        });

        rating.forEach(b -> {
          JSONObject c = new JSONObject(b.toString());
          if (c.getString("userId").equals(userId)) {
            nodesinfo1.put("userName", c.get("userName"));
            nodesinfo1.put("userId", c.getString("userId"));
            nodesinfo1.put("userRatings", c.getString("ratings"));
          }
        });

      });
      int r1 = rating1.size();
      int r2 = rating2.size();
      int r3 = rating3.size();
      int r4 = rating4.size();
      int r5 = rating5.size();
      int total = Integer.parseInt(columnstot.get(0));

      int avgsubratings = (1 * r1) + (2 * r2) + (3 * r3) + (4 * r4) + (5 * r5);

      avgRating = avgsubratings / total;
      logger.info("avgRating" + avgRating);
      // nodeRatingInfo.put("avgRating", avgRating);
      // nodeRatingInfo.put("ratingsCount", total);
      nodesinfo1.put("avgRating", avgRating);
      nodesinfo1.put("ratingsCount", total);
      ratingsList.add(nodeRatingInfo.toMap());
      int percentage5 = (r5 * k / total);
      logger.info(String.valueOf(percentage5));
      int percentage4 = (r4 * k / total);
      logger.info(String.valueOf(percentage4));
      int percentage3 = (r3 * k / total);
      logger.info(String.valueOf(percentage3));
      int percentage2 = (r2 * k / total);
      logger.info(String.valueOf(percentage2));
      int percentage1 = (r1 * k / total);
      logger.info(String.valueOf(percentage1));
      nodesinfo1.put("oneStarRatingPercentage", percentage1);
      nodesinfo1.put("twoStarRatingPercentage", percentage2);
      nodesinfo1.put("threeStarRatingPercentage", percentage3);
      nodesinfo1.put("fourStarRatingPercentage", percentage4);
      nodesinfo1.put("fiveStarRatingPercentage", percentage5);
      ratingsList1.add(nodesinfo1.toMap());
    } else {
      response2.forEach(ab -> {
        JSONObject s = new JSONObject(ab);
        nodesinfo1.put("ratingsCount", s.get("ratingsCount").toString());
        nodesinfo1.put("avgRating", s.get("avgRating").toString());
        ratingsList1.add(nodesinfo1.toMap());

      });
    }

    return ratingsList1;

  }

  public List<Object> getyourPreferences(String userName, HashMap preferenceDetails)
      throws ServiceException {

    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRegistration);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String Background = preferenceDetails.get("Background").toString();
    String DataGovernanceFocus = preferenceDetails.get("DataGovernanceFocus").toString();
    // String cover=preferenceDetails.get("Cover").toString();
    String MetaDataManagementExperience =
        preferenceDetails.get("MetaDataManagementExperience").toString();
    String FrequencyOfDataConsumption =
        preferenceDetails.get("FrequencyOfDataConsumption").toString();

    ArangoCursor<Object> cursor1 = null;
    String queryToBeExecuted = "for a in " + userRegistration + "\r\n" + "filter a.Email == '"
        + userName + "'\r\n" + "update a with {Background:'" + Background
        + "',DataGovernanceFocus:'" + DataGovernanceFocus + "',MetaDataManagementExperience:'"
        + MetaDataManagementExperience + "',FrequencyOfDataConsumption:'"
        + FrequencyOfDataConsumption + "'} In " + userRegistration + "\r\n" + "return NEW._key";

    logger.info(queryToBeExecuted);

    try {
      cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while getyourPreferences : " + e.getMessage().toString());
    }

    return response1;
  }

  public List<Object> updatemyRatingsInfo(String key, String userId, String ratings)
      throws ServiceException {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "LET Nodes = DOCUMENT('Nodes/" + key + "')\r\n"
        + "UPDATE Nodes WITH {Ratings:(FOR rating IN Nodes.Ratings RETURN rating.userId == '"
        + userId + "' ?\r\n" + "MERGE(rating, {ratings: '" + ratings + "'}) : rating)\r\n"
        + "} IN Nodes";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while updatemyRatingsInfo : " + e.getMessage().toString());
    }

    return response;
  }

  public List<Object> getgeneralInformation(String userName, HashMap generalInformation)
      throws ServiceException {

    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRegistration);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String OrganizationFunctions = generalInformation.get("OrganizationFunctions").toString();
    String WorkActivityType = generalInformation.get("WorkActivityType").toString();
    String Goals = generalInformation.get("Goals").toString();
    String Responsibility = generalInformation.get("Responsibility").toString();
    String Need = generalInformation.get("Need").toString();
    String Frustration = generalInformation.get("Frustration").toString();

    ArangoCursor<Object> cursor1 = null;
    String queryToBeExecuted = "for a in " + userRegistration + "\r\n" + "filter a.Email == '"
        + userName + "'\r\n" + "update a with {WorkActivityType:'" + WorkActivityType
        + "',Goals:'" + Goals + "',Responsibility:'" + Responsibility + "',Need:'" + Need
        + "',Frustration:'" + Frustration + "',OrganizationFunctions:'" + OrganizationFunctions
        + "'} In " + userRegistration + "\r\n" + "return NEW._key";

    logger.info(queryToBeExecuted);

    try {
      cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while getgeneralInformation : " + e.getMessage().toString());
    }
    return response1;

  }


  @SuppressWarnings("unchecked")
  public HashMap generalInformation(String userName) throws ServiceException {

    List<HashMap> response1 = new ArrayList<>();
    HashMap generalInfo = new HashMap();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRegistration);
    ArangoCursor<HashMap> cursor1 = null;

    // String queryToBeExecuted="for a in "+ userRegistration +"\r\n"
    // +"filter a.Email == '"+ userName +"'\r\n"
    // + "update a with {WorkActivityType:'"+ WorkActivityType +"',Goals:'"+ Goals
    // +"',Responsibility:'"+ Responsibility +"',Need:'"+ Need +"',Frustration:'"+ Frustration
    // +"',OrganizationFunctions:'"+ OrganizationFunctions +"'} In "+ userRegistration +"\r\n"
    // + "return NEW._key";
    String queryToBeExecuted = "for a in " + userRegistration + "\r\n" + "filter a.Email == '"
        + userName + "'\r\n"
        + "return a";
    // + "return {OrganizationFunctions:a.OrganizationFunctions,WorkActivityType:a.WorkActivityType,Goals:a.Goals,Responsibility:a.Responsibility,Need:a.Need,Frustration:a.Frustration,cover:a.cover}";
    logger.info(queryToBeExecuted);

    try {
      cursor1 = arangoDB.query(queryToBeExecuted, HashMap.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while executing  Query: " + e.getMessage().toString());
    }
    response1.forEach(pref -> {
      JSONObject uprf = new JSONObject(pref);
      logger.info("uprf" + uprf);
      if (uprf.has("OrganizationFunctions") && uprf.has("WorkActivityType") && uprf.has("Goals")
          && uprf.has("Responsibility") && uprf.has("Need") && uprf.has("Frustration") && uprf.has(
          "cover")) {
        //int background = uprf.getInt("Background");
        //String background = uprf.getString("Background");
        String OrganizationFunctions = uprf.getString("OrganizationFunctions");
        String WorkActivityType = uprf.getString("WorkActivityType");
        String Goals = uprf.getString("Goals");
        String Responsibility = uprf.getString("Responsibility");
        String Need = uprf.getString("Need");
        String Frustration = uprf.getString("Frustration");
        String cover = uprf.getString("cover");
        generalInfo.put("OrganizationFunctions", OrganizationFunctions);
        generalInfo.put("WorkActivityType", WorkActivityType);
        generalInfo.put("Goals", Goals);
        generalInfo.put("Responsibility", Responsibility);
        generalInfo.put("Need", Need);
        generalInfo.put("Frustration", Frustration);
        generalInfo.put("cover", cover);
      } else {

        generalInfo.put("OrganizationFunctions", "");
        generalInfo.put("WorkActivityType", "");
        generalInfo.put("Goals", "");
        generalInfo.put("Responsibility", "");
        generalInfo.put("Need", "");
        generalInfo.put("Frustration", "");
        generalInfo.put("cover", "");
      }
    });
    return generalInfo;

  }


  public List<HashMap> simplePostPincollection(HashMap pinDetails) throws ServiceException {

    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    List<Object> response5 = new ArrayList<>();
    List<Object> response6 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, pincollection);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String displayname = pinDetails.get("displayname").toString();
    String Description = pinDetails.get("Description").toString();
    String cover = pinDetails.get("cover").toString();
    String classification = pinDetails.get("classification").toString();
    String type = pinDetails.get("type").toString();
    String createdby = pinDetails.get("createdby").toString();
    String createdon = pinDetails.get("createdon").toString();
    String lastmodifiedby = pinDetails.get("lastmodifiedby").toString();
    String lastmodifiedon = pinDetails.get("lastmodifiedon").toString();
    String tags = pinDetails.get("tags").toString();
    String team = pinDetails.get("teamName").toString();
    // String categories=pinDetails.get("categories").toString();
    String pinNodes = pinDetails.get("pinNodes").toString();
    String pinCollection = pinDetails.get("pinCollection").toString();
    ArangoCursor<HashMap> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    String queryToBeExecuted = "INSERT {displayName:'" + displayname + "',Description:'"
        + Description + "',classification:'" + classification + "',type:'" + type
        + "',createdby:'" + createdby + "',createdon:'" + createdon + "',lastmodifiedby:'"
        + lastmodifiedby + "',lastmodifiedon:'" + lastmodifiedon + "',tags:'" + tags
        + "',pinNodes:" + pinNodes + ",pinCollection:" + pinCollection + "} In " + pincollection
        + "\r\n" + "return NEW._id";

    logger.info(queryToBeExecuted);

    try {
      cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while simplePostPincollection : " + e.getMessage().toString());
    }

    String query = "FOR c IN " + Teams + "\r\n" + "filter c.displayName == '" + team + "'\r\n"
        + "return c";

    logger.info(query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response2 = cursor.asListRemaining();
      logger.info(String.valueOf(response2));
    } catch (Exception e) {
      log.error("Exception while simplePostPincollection_2 : " + e.getMessage().toString());
    }
    JSONObject responseInfo = new JSONObject();
    response2.forEach(t -> {
      JSONObject teamDetails = new JSONObject(t);
      logger.info("teamDetails" + teamDetails);
      String teamId = teamDetails.get("_id").toString();
      responseInfo.put("teamId", teamId);
    });
    String queryToBeExecuted3 = "INSERT {_from:'" + response1.get(0) + "', _to:'"
        + responseInfo.getString("teamId") + "',createdby:'" + createdby + "',createdon:'"
        + createdon + "',lastmodifiedby:'" + lastmodifiedby + "',lastmodifiedon:'"
        + lastmodifiedon + "'} IN " + pincollectionedges + "";
    logger.info(queryToBeExecuted3);
    try {
      cursor1 = arangoDB.query(queryToBeExecuted3, Object.class);
      response3 = cursor1.asListRemaining();
      logger.info(String.valueOf(response3));
    } catch (Exception e) {
      log.error("Exception while simplePostPincollection_3 : " + e.getMessage().toString());
    }
    return response2;

  }

  public List<HashMap> matrixPostPincollection(HashMap pinDetails) throws ServiceException {

    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, pincollection);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String displayname = pinDetails.get("displayname").toString();
    String Description = pinDetails.get("Description").toString();
    String cover = pinDetails.get("cover").toString();
    String classification = pinDetails.get("classification").toString();
    String type = pinDetails.get("type").toString();
    String createdby = pinDetails.get("createdby").toString();
    String createdon = pinDetails.get("createdon").toString();
    String lastmodifiedby = pinDetails.get("lastmodifiedby").toString();
    String lastmodifiedon = pinDetails.get("lastmodifiedon").toString();
    String team = pinDetails.get("teamName").toString();
    String tags = pinDetails.get("tags").toString();
    String pinNodes = pinDetails.get("pinNodes").toString();
    String pinCollection = pinDetails.get("pinCollection").toString();
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<HashMap> cursor = null;

    String queryToBeExecuted = "INSERT {displayName:'" + displayname + "',Description:'"
        + Description + "',classification:'" + classification + "',type:'" + type
        + "',createdby:'" + createdby + "',createdon:'" + createdon + "',lastmodifiedby:'"
        + lastmodifiedby + "',lastmodifiedon:'" + lastmodifiedon + "',tags:'" + tags
        + "',pinNodes:" + pinNodes + ",pinCollection:" + pinCollection + "} In " + pincollection
        + "\r\n" + "return NEW._id";

    logger.info(queryToBeExecuted);

    try {
      cursor1 = arangoDB.query(queryToBeExecuted, Object.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while matrixPostPincollection : " + e.getMessage().toString());
    }

    String query = "FOR c IN " + Teams + "\r\n" + "filter c.displayName == '" + team + "'\r\n"
        + "return c";

    logger.info(query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response2 = cursor.asListRemaining();
      logger.info(String.valueOf(response2));
    } catch (Exception e) {
      log.error("Exception while matrixPostPincollection_2 : " + e.getMessage().toString());
    }
    JSONObject responseInfo = new JSONObject();
    response2.forEach(t -> {
      JSONObject teamDetails = new JSONObject(t);
      logger.info("teamDetails" + teamDetails);
      String teamId = teamDetails.get("_id").toString();
      responseInfo.put("teamId", teamId);
    });
    String queryToBeExecuted3 = "INSERT {_from:'" + response1.get(0) + "', _to:'"
        + responseInfo.getString("teamId") + "',createdby:'" + createdby + "',createdon:'"
        + createdon + "',lastmodifiedby:'" + lastmodifiedby + "',lastmodifiedon:'"
        + lastmodifiedon + "'} IN " + pincollectionedges + "";
    logger.info(queryToBeExecuted3);
    try {
      cursor1 = arangoDB.query(queryToBeExecuted3, Object.class);
      response3 = cursor1.asListRemaining();
      logger.info(String.valueOf(response3));
    } catch (Exception e) {
      log.error("Exception while matrixPostPincollection_3: " + e.getMessage().toString());
    }
    return response2;
  }

  public List<HashMap> getAddRoles(HashMap teamdetails) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response6 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, userRoles);
    HashMap document = new HashMap<>();

    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String team = teamdetails.get("teamId").toString();
    String teamId = "Teams/" + team;

    // ArangoCursor<Object> cursor4 = null;
    // String queryToBeExecuted2="for doc in "+ userRoles +"\r\n"
    // + "filter a._from == '"+teamId+"' \r\n"
    // + "remove doc._key in "+ userRoles +"";
    //
    // logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
    // //ArangoCursor<Object> cursor = null;
    // try {
    //
    // cursor4 = arangoDB.query(queryToBeExecuted2, Object.class);
    // response = cursor4.asListRemaining();
    //
    // } catch (Exception e) {
    // log.error("Exception while executing Query: " + e.getMessage().toString());
    // }

    List<HashMap> roles = (List<HashMap>) teamdetails.get("roles");

    roles.forEach(z -> {

      JSONObject x = new JSONObject(z);
      logger.info("x" + x);
      String role = x.get("role").toString();
      JSONArray users = x.getJSONArray("users");
      String roleid = "NodeTypes/" + role;
      List<Object> usersList = new ArrayList<>();
      users.forEach(u -> {
        JSONObject userObjects = new JSONObject(u.toString());
        logger.info(String.valueOf(userObjects));
        String id = userObjects.get("id").toString();
        JSONObject newUserObject = new JSONObject();
        newUserObject.put("id", id);
        usersList.add(newUserObject);
      });

      List<Object> rolesResponse = new ArrayList<>();

      ArangoCursor<Object> rcursor1 = null;
      String rqueryToBeExecuted1 = "for a in userRoles\r\n" + "filter a._from == '" + teamId
          + "' AND a._to == '" + roleid + "'\r\n" + "return a";

      logger.info(rqueryToBeExecuted1);

      try {
        rcursor1 = arangoDB.query(rqueryToBeExecuted1, Object.class);
        rolesResponse = rcursor1.asListRemaining();
        logger.info(String.valueOf(rolesResponse));
      } catch (Exception e) {
        log.error("Exception while getAddRoles : " + e.getMessage().toString());
      }

      if (rolesResponse.isEmpty()) {

        List<Object> response1 = new ArrayList<>();
        ArangoCursor<Object> cursor1 = null;
        String queryToBeExecuted1 = "insert {_from:'" + teamId + "',_to:'" + roleid + "',users:"
            + usersList + ",createdby:'Admin',createdon:'" + datestr
            + "',lastmodifiedby:'Admin',lastmodifiedon:'" + datestr + "'} In " + userRoles
            + "\r\n";

        logger.info(queryToBeExecuted1);

        try {
          cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
          response1 = cursor1.asListRemaining();
          logger.info(String.valueOf(response1));
        } catch (Exception e) {
          log.error("Exception while getAddRoles_2 : " + e.getMessage().toString());
        }

      }
    });

    return response2;

  }

  public List<HashMap> getcreateNewTeam(HashMap simpleDetails) throws ServiceException {

    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB, Teams);
    ArangoCollection arangoCollection1 =
        arangorestclient.getArangoEdgeCollection(arangoDB, userRoles);

    String nameYourTeam = simpleDetails.get("nameYourTeam").toString();
    String selectAnOption = simpleDetails.get("selectAnOption").toString();
    String teamId = simpleDetails.get("teamId").toString();

    ArangoCursor<String> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<HashMap> cursor2 = null;

    String queryToBeExecuted1 = "for doc in " + Teams + "\r\n" + "filter doc.displayName == '"
        + nameYourTeam + "'\r\n" + "return doc";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    ArangoCursor<HashMap> cursor3 = null;
    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response2 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getcreateNewTeam : " + e.getMessage().toString());
    }

    if (response2.isEmpty()) {
      List<String> response = new ArrayList<>();
      String query = "INSERT {displayName:'" + nameYourTeam + "'," + "type:'" + "team"
          + "',teamStructure:'" + selectAnOption + "'} In " + Teams + "\r\n" + "return NEW._id";

      // String query="update {displayName:'"+ nameYourTeam
      // +"',"+"type:'"+"team"+"',teamStructure:'"+ selectAnOption +"'} In "+ Teams +"\r\n"
      // + "return NEW._id";

      logger.info("queryToBeExecuted----->" + query);

      try {
        cursor = arangoDB.query(query, String.class);
        response = cursor.asListRemaining();
        logger.info("queryToBeExecuted----->" + response);
      } catch (Exception e) {
        log.error("Exception while getcreateNewTeam_2 : " + e.getMessage().toString());
      }

      List<String> responseInfo = new ArrayList<>();
      for (int i = 0; i < response.size(); i++) {
        responseInfo.add(response.get(i));
      }
      String queryToBeExecuted5 =
          "for t in " + roleList + "\r\n" + " RETURN {id:t.id,name:t.name,[t.name]:t.users}";

      logger.info(queryToBeExecuted5);
      try {
        cursor2 = arangoDB.query(queryToBeExecuted5, HashMap.class);
        response3 = cursor2.asListRemaining();
        logger.info("queryToBeExecuted----->" + response3);
      } catch (Exception e) {
        log.error("Exception while getcreateNewTeam_3 : " + e.getMessage().toString());
      }

      response3.forEach(a -> {
        JSONObject s = new JSONObject(a);
        String name = s.getString("name");

        List<Object> response7 = new ArrayList<>();
        ArangoCursor<Object> cursor5 = null;

        String queryToBeExecuted7 = "INSERT {_from:'" + responseInfo.get(0) + "', _to:'"
            + s.getString("id")
            + "',createdby:'Admin',createdon:'65423452',lastmodifiedby:'Admin',lastmodifiedon:'65423452',users:"
            + s.getJSONArray(name) + "} IN " + userRoles + "";

        logger.info(queryToBeExecuted7);
        try {
          cursor5 = arangoDB.query(queryToBeExecuted7, Object.class);
          response7 = cursor5.asListRemaining();
          logger.info("queryToBeExecuted----->" + response7);
        } catch (Exception e) {
          log.error("Exception while getcreateNewTeam_4 : " + e.getMessage().toString());
        }

      });

    } else {
      throw new UnAuthorizedException(queryToBeExecuted1, null);
    }

    ArangoCursor<Object> cursor4 = null;
    String queryToBeExecuted2 =
        "for doc in " + roleList + "\r\n" + "remove doc._key in " + roleList + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
    // ArangoCursor<Object> cursor = null;
    try {

      cursor4 = arangoDB.query(queryToBeExecuted2, Object.class);
      response4 = cursor4.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getcreateNewTeam_5 : " + e.getMessage().toString());
    }
    return response2;
  }


  public List<Object> getcreateNewTeam1(HashMap simpleDetails) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB, Teams);
    ArangoCollection arangoCollection1 =
        arangorestclient.getArangoEdgeCollection(arangoDB, userRoles);

    String nameYourTeam = simpleDetails.get("nameYourTeam").toString();
    String selectAnOption = simpleDetails.get("selectAnOption").toString();

    ArangoCursor<String> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<HashMap> cursor2 = null;

    String queryToBeExecuted1 = "for doc in " + Teams + "\r\n" + "filter doc.displayName == '"
        + nameYourTeam + "'\r\n" + "return doc";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    ArangoCursor<HashMap> cursor3 = null;
    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response2 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getcreateNewTeam1: " + e.getMessage().toString());
    }

    if (response2.isEmpty()) {
      // List<String> response = new ArrayList<>();
      String query = "INSERT {displayName:'" + nameYourTeam + "'," + "type:'" + "team"
          + "',teamStructure:'" + selectAnOption + "'} In " + Teams + "\r\n" + "return NEW";

      logger.info("queryToBeExecuted----->" + query);

      try {
        cursor1 = arangoDB.query(query, Object.class);
        response = cursor1.asListRemaining();
        logger.info("queryToBeExecuted----->" + response);
      } catch (Exception e) {
        log.error("Exception while getcreateNewTeam1_2 : " + e.getMessage().toString());
      }
    } else {
      throw new UnAuthorizedException(queryToBeExecuted1, null);
    }

    return response;

  }

  public List<Object> addcreateNewMatrixTeam(HashMap teamDetails) throws ServiceException {

    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response6 = new ArrayList<>();
    List<Object> response8 = new ArrayList<>();
    List<Object> response = new ArrayList<>();
    List<String> columns = new ArrayList<String>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangoDB, Teams);
    ArangoCollection arangoCollection1 =
        arangorestclient.getArangoEdgeCollection(arangoDB, userRoles);
    String nameYourTeam = teamDetails.get("nameYourTeam").toString();
    // .get("nameYourTeam");
    String selectAnOption = teamDetails.get("selectAnOption").toString();

    ArangoCursor<HashMap> cursor = null;
    ArangoCursor<Object> cursor1 = null;
    ArangoCursor<HashMap> cursor2 = null;
    ArangoCursor<String> cursor4 = null;

    String queryToBeExecuted1 = "for doc in " + Teams + "\r\n" + "filter doc.displayName == '"
        + nameYourTeam + "'\r\n" + "return doc";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    ArangoCursor<HashMap> cursor3 = null;
    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response2 = cursor3.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while addcreateNewMatrixTeam : " + e.getMessage().toString());
    }

    if (response2.isEmpty()) {

      String query = "INSERT {displayName:'" + nameYourTeam + "'," + "type:'" + "team"
          + "',teamStructure:'" + selectAnOption + "'} In " + Teams + "\r\n" + "return NEW";

      // String query="update {displayName:'"+ nameYourTeam
      // +"',"+"type:'"+"team"+"',teamStructure:'"+ selectAnOption +"'} In "+ Teams +"\r\n"
      // + "return NEW._id";

      logger.info("queryToBeExecuted----->" + query);

      try {
        cursor1 = arangoDB.query(query, Object.class);
        response = cursor1.asListRemaining();
        logger.info("queryToBeExecuted----->" + response);
      } catch (Exception e) {
        log.error("Exception while addcreateNewMatrixTeam_2 : " + e.getMessage().toString());
      }

    } else {
      throw new UnAuthorizedException(queryToBeExecuted1, null);
    }
    return response;
  }

  public List<Object> getRole() throws ServiceException {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    // ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangodb,
    // roleList);

    String queryToBeExecuted =
        "for a in NodeTypes\r\n" + "filter a.roleName !=null\r\n" + "return a.roleName";
    logger.info("queryToBeExecuted" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getRole_2: " + e.getMessage().toString());
    }
    return response;
  }


  public List<HashMap> mySharedNodesCommunityList1(HashMap shareDetails) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    String url = shareDetails.get("url").toString();
    String shareHolder = shareDetails.get("shareHolder").toString();
    String shareHolderId = shareDetails.get("shareHolderId").toString();
    String shareCommunity = shareDetails.get("shareType").toString();
    String shareUsing = shareDetails.get("shareUsing").toString();
    List<String> users = (List<String>) shareDetails.get("users");
    for (int i = 0; i < users.size(); i++) {
      columns.add("'" + users.get(i) + "'");
    }
    JSONObject s = new JSONObject();
    s.put("url", url);
    s.put("shareCommunity", shareCommunity);
    s.put("users", users);
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, mySharedCollection);
    String queryToBeExecuted =
        "for doc in " + mySharedCollection + "\r\n" + "filter doc.shareHolder == '" + shareHolder
            + "' AND doc.shareHolderId == '" + shareHolderId + "'\r\n" + "return doc._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while mySharedNodesCommunityList1 : " + e.getMessage().toString());
    }
    if (shareUsing.contains("Nodes")) {

      if (response.isEmpty()) {
        String queryToBeExecuted1 = "INSERT { shareHolder: '" + shareHolder + "',shareHolderId: '"
            + shareHolderId + "', shareNodes: [],shareCollection: []} INTO " + mySharedCollection
            + "";
        logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

        ArangoCursor<HashMap> cursor1 = null;
        try {

          cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
          response1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while mySharedNodesCommunityList1_2 : " + e.getMessage().toString());
        }

        String queryToBeExecuted2 = "for doc in " + mySharedCollection + "\r\n"
            + "filter doc.shareNodes != null AND doc.shareHolder == '" + shareHolder
            + "' AND doc.shareHolderId == '" + shareHolderId + "'\r\n"
            + "UPDATE doc WITH { shareNodes: push(doc.shareNodes," + s + ",true) } IN "
            + mySharedCollection + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
        try {

          cursor1 = arangoDB.query(queryToBeExecuted2, HashMap.class);
          response2 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while mySharedNodesCommunityList1_3 : " + e.getMessage().toString());
        }

      } else {

        ArangoCursor<HashMap> cursor3 = null;
        String queryToBeExecuted3 = "for doc in " + mySharedCollection + "\r\n"
            + "filter doc.shareNodes != null AND doc.shareHolder == '" + shareHolder
            + "' AND doc.shareHolderId == '" + shareHolderId + "'\r\n"
            + "UPDATE doc WITH { shareNodes: push(doc.shareNodes," + s + ",true) } IN "
            + mySharedCollection + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

        try {

          cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
          response3 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while mySharedNodesCommunityList1_4 : " + e.getMessage().toString());
        }
      }

    } else if (shareUsing.contains("PinCollection")) {

      if (response.isEmpty()) {
        String queryToBeExecuted1 = "INSERT { shareHolder: '" + shareHolder + "',shareHolderId: '"
            + shareHolderId + "', shareCollection: [],shareNodes: []} INTO " + mySharedCollection
            + "";
        logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

        ArangoCursor<HashMap> cursor1 = null;
        try {

          cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
          response1 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while mySharedNodesCommunityList1_5 : " + e.getMessage().toString());
        }

        String queryToBeExecuted2 = "for doc in " + mySharedCollection + "\r\n"
            + "filter doc.shareCollection != null AND doc.shareHolder == '" + shareHolder
            + "' AND doc.shareHolderId == '" + shareHolderId + "'\r\n"
            + "UPDATE doc WITH { shareCollection: push(doc.shareCollection," + s + ",true) } IN "
            + mySharedCollection + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
        try {

          cursor1 = arangoDB.query(queryToBeExecuted2, HashMap.class);
          response2 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while mySharedNodesCommunityList1_6 : " + e.getMessage().toString());
        }

      } else {

        ArangoCursor<HashMap> cursor3 = null;
        String queryToBeExecuted3 = "for doc in " + mySharedCollection + "\r\n"
            + "filter doc.shareCollection != null AND doc.shareHolder == '" + shareHolder
            + "' AND doc.shareHolderId == '" + shareHolderId + "'\r\n"
            + "UPDATE doc WITH { shareCollection: push(doc.shareCollection," + s + ") } IN "
            + mySharedCollection + "";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

        try {

          cursor3 = arangoDB.query(queryToBeExecuted3, HashMap.class);
          response3 = cursor3.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while mySharedNodesCommunityList1_7: " + e.getMessage().toString());
        }
      }
    }
    return response1;
  }

  public List<Object> getMyCollectionsDropDownList() throws ServiceException {

    List<Object> response = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR doc IN PinCollection\r\n"
            + "filter doc.createdBy=='Admin'\r\n"
            + "return {name:doc.displayName,id:doc._key}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getMyCollectionsDropDownList : " + e.getMessage().toString());
    }
    return response;

  }

  public String nodeBusiness() throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    HashMap<String, String> count = new HashMap<String, String>();
    JSONObject s = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for pin in Business\r\n"
        + "filter pin.typeName=='Line of Business'\r\n" + "return {key:pin._key,name:pin.name}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeBusiness : " + e.getMessage().toString());
    }

    int counter = 0;
    for (int i = 0; i < response.size(); i++) {
      counter = counter + 1;
    }
    s.put("counter", counter);
    s.put("response", response);
    // response.add(count);
    return s.toString();

  }

  public String nodeDatadomain() throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    JSONObject s = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for pin in Business\r\n"
        + "filter pin.typeName=='Data Domain'\r\n" + "return {key:pin._key,name:pin.name}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeDatadomain : " + e.getMessage().toString());
    }

    int counter = 0;
    for (int i = 0; i < response.size(); i++) {
      counter = counter + 1;
    }
    s.put("counter", counter);
    s.put("response", response);
    // response.add(count);
    return s.toString();

  }

  public String nodeGeography() throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    JSONObject s = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for pin in Business\r\n"
        + "filter pin.typeName== 'Region'\r\n" + "return {key:pin._key,name:pin.name}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeGeography : " + e.getMessage().toString());
    }
    int counter = 0;
    for (int i = 0; i < response.size(); i++) {
      counter = counter + 1;
    }
    s.put("counter", counter);
    s.put("response", response);
    // response.add(count);
    return s.toString();

  }

  public String nodeProduct() throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    JSONObject s = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for pin in Business\r\n"
        + "filter pin.typeName== 'Product'\r\n" + "return {key:pin._key,name:pin.name}";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while nodeProduct : " + e.getMessage().toString());
    }
    int counter = 0;
    for (int i = 0; i < response.size(); i++) {
      counter = counter + 1;
    }
    s.put("counter", counter);
    s.put("response", response);
    // response.add(count);
    return s.toString();

  }

  public List<Object> saveProfileCategories(HashMap categoriesInfo) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRoles);
    String userId = categoriesInfo.get("userId").toString();
    String userEmailId = categoriesInfo.get("userEmailId").toString();

    List<HashMap> business = (List<HashMap>) categoriesInfo.get("business");
    List<HashMap> dataDomain = (List<HashMap>) categoriesInfo.get("dataDomain");
    // teamdetails.get("dataDomain").toString();
    // .getJSONArray("dataDomain");
    List<HashMap> geography = (List<HashMap>) categoriesInfo.get("geography");
    List<HashMap> product = (List<HashMap>) categoriesInfo.get("product");
    List<Object> contexts = new ArrayList<Object>();

    business.forEach(b -> {
      JSONObject busiObject = new JSONObject();
      JSONObject busines = new JSONObject(b);
      logger.info("business" + busines);
      String id = busines.get("key").toString();
      busiObject.put("id", id);
      contexts.add(busiObject);
    });
    dataDomain.forEach(d -> {
      JSONObject dataDomainObject = new JSONObject();
      JSONObject domain = new JSONObject(d);
      String id = domain.getString("key");
      dataDomainObject.put("id", id);
      contexts.add(dataDomainObject);
    });

    // String datadomainId=dataDomain.getString("key");
    // dataDomainObject.put("id", datadomainId);

    geography.forEach(g -> {
      JSONObject geographyObject = new JSONObject();
      JSONObject geograph = new JSONObject(g);
      String id = geograph.getString("key");
      geographyObject.put("id", id);
      contexts.add(geographyObject);
    });
    product.forEach(p -> {
      JSONObject productObject = new JSONObject();
      JSONObject prodct = new JSONObject(p);
      String id = prodct.getString("key");
      productObject.put("id", id);
      contexts.add(productObject);
    });

    ArangoCursor<HashMap> cursor = null;
    String queryToExecute = "for a in registerUsers\r\n" + "filter a._key == '" + userId
        + "' AND a.Email=='" + userEmailId + "'\r\n" + "return a";
    logger.info(queryToExecute);

    try {
      cursor = arangoDB.query(queryToExecute, HashMap.class);
      response1 = cursor.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while saveProfileCategories : " + e.getMessage().toString());
    }
    JSONObject y = new JSONObject();
    for (int a = 0; a < response1.size(); a++) {
      y = new JSONObject(response1.get(a));
    }

    if (y.has("contextCounter")) {

      List<String> contextName = new ArrayList<>();
      HashSet<String> contextName2 = new HashSet<String>();
      HashSet<Integer> contextName3 = new HashSet<Integer>();
      List<String> contextColumns = new ArrayList<>();
      // response4.clear();
      for (int i = 0; i < response1.size(); i++) {
        JSONObject s = new JSONObject(response1.get(i));
        contextName.clear();
        logger.info("s" + s);
        Set<String> keys = s.keySet();
        Object[] namesArray = keys.toArray();
        for (int j = 0; j < namesArray.length; j++) {
          logger.info(j + ": " + namesArray[j]);
          contextName.add(namesArray[j].toString());
        }

        for (int x = 0; x < contextName.size(); x++) {
          if (contextName.get(x).contains("Email") || contextName.get(x).contains("FirstName")
              || contextName.get(x).contains("LastModifiedOn")
              || contextName.get(x).contains("LastName")
              || contextName.get(x).contains("CreatedOn")
              || contextName.get(x).contains("Password")
              || contextName.get(x).contains("Identifier")
              || contextName.get(x).contains("Background")
              || contextName.get(x).contains("createdon")
              || contextName.get(x).contains("FrequencyOfDataConsumption")
              || contextName.get(x).contains("contextCounter")
              || contextName.get(x).contains("DataGovernanceFocus")
              || contextName.get(x).contains("MetaDataManagementExperience")
              || contextName.get(x).contains("Frustration")
              || contextName.get(x).contains("Need") || contextName.get(x).contains("Goals")
              || contextName.get(x).contains("WorkActivityType")
              || contextName.get(x).contains("_key") || contextName.get(x).contains("_id")
              || contextName.get(x).contains("_rev")
              || contextName.get(x).contains("OrganizationFunctions")
              || contextName.get(x).contains("Responsibility")) {

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
      List<Integer> listWithUniqueContextValues1 = new ArrayList<>(setWithUniqueValues1);

      int largest = Collections.max(listWithUniqueContextValues1);
      logger.info("largest" + largest);
      int contextCounter = largest + 1;

      ArangoCursor<Object> cursor1 = null;
      String query =
          "for a in registerUsers\r\n" + "filter a._key == '" + userId + "' AND a.Email=='"
              + userEmailId + "'\r\n" + "update a with  {Context" + contextCounter + ":"
              + contexts + ",contextCounter:" + contextCounter + "} in registerUsers";
      logger.info(query);

      try {
        cursor1 = arangoDB.query(query, Object.class);
        response = cursor1.asListRemaining();
        logger.info(String.valueOf(response));
      } catch (Exception e) {
        log.error("Exception while saveProfileCategories_2 : " + e.getMessage().toString());
      }


    } else {
      int contextCounter = 1;
      ArangoCursor<Object> cursor1 = null;
      String query =
          "for a in registerUsers\r\n" + "filter a._key == '" + userId + "' AND a.Email=='"
              + userEmailId + "'\r\n" + "update a with  {Context" + contextCounter + ":"
              + contexts + ",contextCounter:" + contextCounter + "} in registerUsers";
      logger.info(query);

      try {
        cursor1 = arangoDB.query(query, Object.class);
        response = cursor1.asListRemaining();
        logger.info(String.valueOf(response));
      } catch (Exception e) {
        log.error("Exception while saveProfileCategories_3 : " + e.getMessage().toString());
      }

    }
    return response;
  }

  public String retriveProfileCategories(String userId) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    // HashMap<String, ArrayList<Object>> teamDetails = new HashMap<>();
    JSONObject getTeamDetails = new JSONObject();

    List<Object> contextteamDetails = new ArrayList<>();
    List<Object> keycolumns = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "for a in registerUsers\r\n" + "filter a._key == '" + userId + "'\r\n" + "return a";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while retriveProfileCategories : " + e.getMessage().toString());
    }

    List<String> contextName = new ArrayList<>();
    HashSet<String> contextName2 = new HashSet<String>();
    List<String> contextColumns = new ArrayList<>();
    // response4.clear();
    for (int i = 0; i < response.size(); i++) {

      JSONObject s = new JSONObject(response.get(i));
      contextName.clear();
      logger.info("s" + s);
      Set<String> keys = s.keySet();
      Object[] namesArray = keys.toArray();
      for (int j = 0; j < namesArray.length; j++) {
        logger.info(j + ": " + namesArray[j]);
        contextName.add(namesArray[j].toString());
      }

      for (int x = 0; x < contextName.size(); x++) {
        if (contextName.get(x).contains("Email") || contextName.get(x).contains("FirstName")
            || contextName.get(x).contains("LastModifiedOn")
            || contextName.get(x).contains("LastName") || contextName.get(x).contains("CreatedOn")
            || contextName.get(x).contains("Password")
            || contextName.get(x).contains("Identifier")
            || contextName.get(x).contains("Background")
            || contextName.get(x).contains("createdon")
            || contextName.get(x).contains("FrequencyOfDataConsumption")
            || contextName.get(x).contains("contextCounter")
            || contextName.get(x).contains("DataGovernanceFocus")
            || contextName.get(x).contains("MetaDataManagementExperience")
            || contextName.get(x).contains("Frustration") || contextName.get(x).contains("Need")
            || contextName.get(x).contains("Goals")
            || contextName.get(x).contains("WorkActivityType")
            || contextName.get(x).contains("_key") || contextName.get(x).contains("_id")
            || contextName.get(x).contains("_rev")
            || contextName.get(x).contains("OrganizationFunctions")
            || contextName.get(x).contains("Responsibility")) {

        } else {
          contextName2.add(contextName.get(x));
        }
      }
    }
    Set<String> setWithUniqueValues = new HashSet<>(contextName2);
    logger.info("setWithUniqueValues" + setWithUniqueValues);
    List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
    logger.info("listWithUniqueContextValues" + listWithUniqueContextValues);

    for (int k = 0; k < listWithUniqueContextValues.size(); k++) {
      String contName = listWithUniqueContextValues.get(k);
      String query = "for a in registerUsers\r\n" + "filter a._key == '" + userId + "' AND  a."
          + listWithUniqueContextValues.get(k) + " !=null \r\n" + "return a";
      logger.info("query----->" + query);

      try {

        cursor3 = arangoDB.query(query, String.class);
        response2 = cursor3.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while retriveProfileCategories_2 : " + e.getMessage().toString());
      }
      JSONObject teamDetails = new JSONObject();
      ArrayList<Object> response4 = new ArrayList<>();
      JSONObject teamDetail1 = new JSONObject();
      JSONObject teamDetail3 = new JSONObject();
      response2.forEach(a -> {
        // response4.clear();
        JSONObject contxt = new JSONObject(a);
        logger.info("contxt" + contxt);
        List<String> columns3 = new ArrayList<String>();

        JSONArray context = contxt.getJSONArray(contName);
        teamDetails.put("context", contName);
        context.forEach(z -> {
          JSONObject categoryId = new JSONObject(z.toString());
          String cids = categoryId.getString("id");
          columns3.add("a._key == '" + cids + "'");
        });
        JSONObject teamDetail2 = new JSONObject();

        logger.info("teamDetail1" + teamDetail1);

        List<Object> Business = new ArrayList<>();
        List<Object> DataDomain = new ArrayList<>();
        List<Object> Geography = new ArrayList<>();
        List<Object> Products = new ArrayList<>();
        List<HashMap> response5 = new ArrayList<>();
        ArangoCursor<HashMap> cursor4 = null;
        String columnIds3 = String.join(" OR ", columns3);
        String queryToBeExecuted4 = "for a in Business\r\n" + "filter " + columnIds3
            + "\r\n" + "return {name:a.name,type:a.typeName,id:a._key}";

        logger.info("queryToBeExecuted----->" + queryToBeExecuted4);

        try {

          cursor4 = arangoDB.query(queryToBeExecuted4, HashMap.class);
          response5 = cursor4.asListRemaining();
          logger.info(String.valueOf(response5));

        } catch (Exception e) {
          log.error("Exception while retriveProfileCategories_3 : " + e.getMessage().toString());
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
          String id = cate.getString("id");
          if (type.equals("Data Domain")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
            DataDomain.add(categoryObject);
          } else if (type.equals("Product")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
            Products.add(categoryObject);
          } else if (type.equals("Region")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
            Geography.add(categoryObject);
          } else if (type.equals("Line of Business")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
            Business.add(categoryObject);
          }
        });
        if (!DataDomain.isEmpty()) {
          teamDetails.put("Data Domain", DataDomain);
        }
        if (!Products.isEmpty()) {
          teamDetails.put("Products", Products);
        }
        if (!Geography.isEmpty()) {
          teamDetails.put("Geography", Geography);
        }
        if (!Business.isEmpty()) {
          teamDetails.put("Business", Business);
        }

      });
      teamDetails.put("userId", userId);
      contextteamDetails.add(teamDetails);
    }
    return contextteamDetails.toString();


  }


  public HashMap yourPreferences(String userName) throws ServiceException {

    List<String> response1 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();

    HashMap preferences = new HashMap();
    List<Object> usr = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRegistration);
    ArangoCursor<String> cursor1 = null;
    String queryToBeExecuted = "for a in " + userRegistration + "\r\n" + "filter a.Email == '"
        + userName + "'\r\n"
        + "return a \r\n";
    // + "return {Background:a.Background,DataGovernanceFocus:a.DataGovernanceFocus,MetaDataManagementExperience:a.MetaDataManagementExperience,FrequencyOfDataConsumption:a.FrequencyOfDataConsumption,id:a._id}";
    logger.info(queryToBeExecuted);

    try {
      cursor1 = arangoDB.query(queryToBeExecuted, String.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error(
          "Exception while executing  Query: " + "yourPreferences(String userName)" + e.getMessage()
              .toString());
    }

    response1.forEach(pref -> {
      JSONObject uprf = new JSONObject(pref);
      logger.info("uprf" + uprf);
      if (uprf.has("Background") && uprf.has("DataGovernanceFocus") && uprf.has(
          "MetaDataManagementExperience") && uprf.has("FrequencyOfDataConsumption") && uprf.has(
          "_id")) {
        int background = uprf.getInt("Background");
        //String background = uprf.getString("Background");
        String dataGovernanceFocus = uprf.getString("DataGovernanceFocus");
        String metaDataManagementExperience = uprf.getString("MetaDataManagementExperience");
        String frequencyOfDataConsumption = uprf.getString("FrequencyOfDataConsumption");
        String id = uprf.getString("_id");
        preferences.put("Background", background);
        preferences.put("DataGovernanceFocus", dataGovernanceFocus);
        preferences.put("MetaDataManagementExperience", metaDataManagementExperience);
        preferences.put("FrequencyOfDataConsumption", frequencyOfDataConsumption);

        List<Object> response = new ArrayList<>();
        List<String> response2 = new ArrayList<>();

        ArangoCursor<String> cursor2 = null;
        String queryToBeExecuted1 = "for a in " + profileCategories + "\r\n" + "filter a._from == '"
            + id + "'\r\n" + "return a";
        logger.info(
            "queryToBeExecuted1----->" + "yourPreferences(String userName)" + queryToBeExecuted1);

        try {

          cursor2 = arangoDB.query(queryToBeExecuted1, String.class);
          response2 = cursor2.asListRemaining();
          logger.info(String.valueOf(response2));

        } catch (Exception e) {
          log.error("Exception while executing  Query: " + "yourPreferences(String userName)"
              + e.getMessage().toString());
        }

        List<String> contextName = new ArrayList<>();
        HashSet<String> contextName2 = new HashSet<String>();
        List<String> contextColumns = new ArrayList<>();
        // response4.clear();
        for (int i = 0; i < response2.size(); i++) {

          JSONObject s = new JSONObject(response2.get(i));
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
                .contains("_from")
                || contextName.get(x).contains("createdby") || contextName.get(x).contains("_rev")
                || contextName.get(x).contains("lastmodifiedby") || contextName.get(x)
                .contains("_id")
                || contextName.get(x).contains("_to") || contextName.get(x).contains("_key")
                || contextName.get(x).contains("createdon") || contextName.get(x).contains("users")
                || contextName.get(x).contains("contextCounter")) {

            } else {
              contextName2.add(contextName.get(x));
            }
          }
        }
        Set<String> setWithUniqueValues = new HashSet<>(contextName2);
        logger.info("setWithUniqueValues" + setWithUniqueValues);
        List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
        logger.info("listWithUniqueContextValues" + listWithUniqueContextValues);

        for (int k = 0; k < listWithUniqueContextValues.size(); k++) {
          String contName = listWithUniqueContextValues.get(k);

          ArangoCursor<String> cursor3 = null;
          String query =
              "for a in " + profileCategories + "\r\n" + "filter a._from == '" + id + "' AND  a."
                  + listWithUniqueContextValues.get(k) + " !=null \r\n" + "return a";
          logger.info("query----->" + query);

          try {

            cursor3 = arangoDB.query(query, String.class);
            response2 = cursor3.asListRemaining();
            logger.info(String.valueOf(response2));

          } catch (Exception e) {
            log.error("Exception while executing  Query: " + "yourPreferences(String userName)"
                + e.getMessage().toString());
          }
          JSONObject teamDetails = new JSONObject();
          ArrayList<Object> response4 = new ArrayList<>();
          JSONObject teamDetail1 = new JSONObject();
          JSONObject teamDetail3 = new JSONObject();
          response2.forEach(a -> {
            // response4.clear();
            JSONObject contxt = new JSONObject(a);
            logger.info("contxt" + contxt);
            String to = contxt.getString("_to");
            List<String> columns3 = new ArrayList<String>();

            // JSONArray users=contxt.getJSONArray("users");
            String[] Id = to.split("/");
            String nodetypes = Id[0];
            String name = Id[1];
            logger.info("name" + name);

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

            logger.info("queryToBeExecuted----->" + "yourPreferences(String userName)"
                + queryToBeExecuted4);

            try {

              cursor4 = arangoDB.query(queryToBeExecuted4, HashMap.class);
              response5 = cursor4.asListRemaining();
              logger.info(String.valueOf(response5));

            } catch (Exception e) {
              log.error("Exception while executing  Query: " + e.getMessage().toString());
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

        preferences.put("categories", usr);
        usr.clear();
        // response3.add(preferences.toMap());
      } else {
        preferences.put("Background", "");
        preferences.put("DataGovernanceFocus", "");
        preferences.put("MetaDataManagementExperience", "");
        preferences.put("FrequencyOfDataConsumption", "");
        preferences.put("categories", usr);
        // response3.add(preferences.toMap());

      }
    });
    return preferences;

  }


  public HashMap<String, ArrayList<Object>> getMatrixTeamDetails(String teamId)
      throws ServiceException {

    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    ArrayList<Object> response4 = new ArrayList<>();
    HashMap<String, ArrayList<Object>> teamDetails = new HashMap<>();
    // HashMap teamDetail1=new HashMap();
    HashMap teamDetail = new HashMap();
    Set<String> keycolumns = new HashSet<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getMatrixTeamDetails : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < response.size(); i++) {
      columns.add("a._from == '" + response.get(i) + "'");
    }

    String columnIds = String.join(" OR ", columns);
    String queryToBeExecuted1 =
        "for a in " + userRoles + "\r\n" + "filter " + columnIds + "\r\n" + "return a";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, String.class);
      response1 = cursor3.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while getMatrixTeamDetails_2 : " + e.getMessage().toString());
    }

    List<String> columns2 = new ArrayList<String>();

    List<String> contextName = new ArrayList<>();
    List<String> contextName2 = new ArrayList<>();
    // response4.clear();
    for (int i = 0; i < response1.size(); i++) {

      JSONObject s = new JSONObject(response1.get(i));

      logger.info("s" + s);
      Set<String> keys = s.keySet();
      Object[] namesArray = keys.toArray();
      contextName2.clear();
      contextName.clear();
      for (int j = 0; j < namesArray.length; j++) {
        logger.info(j + ": " + namesArray[j]);
        contextName.add(namesArray[j].toString());
      }
      logger.info("contextName" + contextName);
      for (int x = 0; x < contextName.size(); x++) {
        // logger.info("contextName"+contextName);

        if (contextName.get(x).contains("lastmodifiedon") || contextName.get(x).contains("_from")
            || contextName.get(x).contains("createdby") || contextName.get(x).contains("_rev")
            || contextName.get(x).contains("lastmodifiedby") || contextName.get(x).contains("_id")
            || contextName.get(x).contains("_to") || contextName.get(x).contains("_key")
            || contextName.get(x).contains("createdon") || contextName.get(x).contains("users")) {

        } else {
          contextName2.add(contextName.get(x));
        }

      }
      logger.info("contextName2" + contextName2);
      logger.info("Keys" + keys);
      String id = s.getString("_to");
      String[] Id = id.split("/");
      String nodetypes = Id[0];
      String name = Id[1];
      logger.info("name" + name);
      JSONArray s1 = new JSONArray();
      s1 = s.getJSONArray("users");
      logger.info("s1" + s1);
      s1.forEach(y -> {
        JSONObject usrId = new JSONObject(y.toString());
        logger.info("usrId" + usrId);
        String ids = usrId.getString("id");
        columns2.add("a._key == '" + ids + "'");
        logger.info("columns2" + columns2);
      });
      JSONArray categories = new JSONArray();
      List<String> columns3 = new ArrayList<String>();
      if (!contextName2.isEmpty()) {
        categories = s.getJSONArray(contextName2.get(0));
        categories.forEach(z -> {
          JSONObject categoryId = new JSONObject(z.toString());
          String cids = categoryId.getString("id");
          columns3.add("a._id == '" + cids + "'");
        });
      }
      List<HashMap> response3 = new ArrayList<>();
      ArangoCursor<HashMap> cursor2 = null;
      String columnIds2 = String.join(" OR ", columns2);
      String queryToBeExecuted3 = "for a in " + userRegistration + "\r\n" + "filter " + columnIds2
          + "\r\n" + "return {" + name + ":a.FirstName}";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted3);

      try {

        cursor2 = arangoDB.query(queryToBeExecuted3, HashMap.class);
        response3 = cursor2.asListRemaining();
        logger.info(String.valueOf(response3));

      } catch (Exception e) {
        log.error("Exception while getMatrixTeamDetails_3 : " + e.getMessage().toString());
      }
      columns2.clear();
      // response4.clear();
      JSONObject teamDetail1 = new JSONObject();
      teamDetail1.put("id", id);
      teamDetail1.put("name", name);
      for (int l = 0; l < response3.size(); l++) {

        teamDetail = new HashMap(response3.get(l));
        logger.info("teamDetail" + teamDetail);
        Collection k = teamDetail.values();
        keycolumns.addAll(k);
        k.clear();
        logger.info("k" + k);

      }
      teamDetail1.put(name, keycolumns);
      keycolumns.clear();
      logger.info("keycolumns" + keycolumns);
      logger.info("teamDetail1" + teamDetail1);

      List<String> Business = new ArrayList<>();
      List<String> DataDomain = new ArrayList<>();
      List<String> Geography = new ArrayList<>();
      List<String> Products = new ArrayList<>();
      List<HashMap> response5 = new ArrayList<>();
      ArangoCursor<HashMap> cursor4 = null;
      String columnIds3 = String.join(" OR ", columns3);
      String queryToBeExecuted4 = "for a in Business\r\n" + "filter " + columnIds3
          + "\r\n" + "return {name:a.name,type:a.typeName}";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted4);

      try {

        cursor4 = arangoDB.query(queryToBeExecuted4, HashMap.class);
        response5 = cursor4.asListRemaining();
        logger.info(String.valueOf(response5));

      } catch (Exception e) {
        log.error("Exception while getMatrixTeamDetails_4 : " + e.getMessage().toString());
      }
      columns3.clear();
      DataDomain.clear();
      response5.forEach(ca -> {
        JSONObject cate = new JSONObject(ca);
        String type = cate.getString("type");
        String cname = cate.getString("name");
        if (type.equals("Data Domain")) {
          DataDomain.add(cname);
        } else if (type.equals("Product")) {
          Products.add(cname);
        } else if (type.equals("Region")) {
          Geography.add(cname);
        } else if (type.equals("Line of Business")) {
          Business.add(cname);
        }
      });
      if (!DataDomain.isEmpty()) {
        teamDetail1.put("Data Domain", DataDomain);
      }
      if (!Products.isEmpty()) {
        teamDetail1.put("Products", Products);
      }
      if (!Geography.isEmpty()) {
        teamDetail1.put("Geography", Geography);
      }
      if (!Business.isEmpty()) {
        teamDetail1.put("Business", Business);
      }
      // response4.add(response5);
      response4.add(teamDetail1.toMap());
      teamDetails.put(contextName2.get(0), response4);

      // response4.clear();
      logger.info("teamDetails" + teamDetails);

    }
    return teamDetails;
  }

  public String getMatrixTeamDetails1(String teamId) throws ServiceException {
    List<String> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    // HashMap<String, ArrayList<Object>> teamDetails = new HashMap<>();
    JSONObject getTeamDetails = new JSONObject();

    List<Object> contextteamDetails = new ArrayList<>();
    List<Object> keycolumns = new ArrayList<>();
    String tId = "Teams/" + teamId;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getMatrixTeamDetails1 : " + e.getMessage().toString());
    }

    String queryToBeExecuted1 = "for a in " + userRoles + "\r\n" + "filter a._from == '"
        + response.get(0) + "'\r\n" + "return a";
    logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, String.class);
      response1 = cursor3.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while getMatrixTeamDetails1_2 : " + e.getMessage().toString());
    }

    List<String> contextName = new ArrayList<>();
    HashSet<String> contextName2 = new HashSet<String>();
    List<String> contextColumns = new ArrayList<>();
    // response4.clear();
    for (int i = 0; i < response1.size(); i++) {

      JSONObject s = new JSONObject(response1.get(i));
      contextName.clear();
      logger.info("s" + s);
      Set<String> keys = s.keySet();
      Object[] namesArray = keys.toArray();
      for (int j = 0; j < namesArray.length; j++) {
        logger.info(j + ": " + namesArray[j]);
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
    logger.info("setWithUniqueValues" + setWithUniqueValues);
    List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
    logger.info("listWithUniqueContextValues" + listWithUniqueContextValues);

    for (int k = 0; k < listWithUniqueContextValues.size(); k++) {
      String contName = listWithUniqueContextValues.get(k);
      String query = "for a in " + userRoles + "\r\n" + "filter a._from == '" + tId + "' AND  a."
          + listWithUniqueContextValues.get(k) + " !=null \r\n" + "return a";
      logger.info("query----->" + query);

      try {

        cursor3 = arangoDB.query(query, String.class);
        response2 = cursor3.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while getMatrixTeamDetails1_3 : " + e.getMessage().toString());
      }
      JSONObject teamDetails = new JSONObject();
      ArrayList<Object> response4 = new ArrayList<>();
      JSONObject teamDetail1 = new JSONObject();
      JSONObject teamDetail3 = new JSONObject();
      response2.forEach(a -> {
        // response4.clear();
        JSONObject contxt = new JSONObject(a);
        logger.info("contxt" + contxt);
        String to = contxt.getString("_to");
        List<String> columns3 = new ArrayList<String>();

        // JSONArray users=contxt.getJSONArray("users");
        String[] Id = to.split("/");
        String nodetypes = Id[0];
        String name = Id[1];
        logger.info("name" + name);
        if (contxt.has("users")) {
          JSONArray users = contxt.getJSONArray("users");
          users.forEach(y -> {
            JSONObject usrId = new JSONObject(y.toString());
            logger.info("usrId" + usrId);
            String ids = usrId.getString("id");
            columns.add("a._key == '" + ids + "'");
            logger.info("columns" + columns);
          });
        }

        JSONArray context = contxt.getJSONArray(contName);
        teamDetails.put("context", contName);
        context.forEach(z -> {
          JSONObject categoryId = new JSONObject(z.toString());
          String cids = categoryId.getString("id");
          columns3.add("a._key == '" + cids + "'");
        });

        // users.forEach(y->{
        // JSONObject usrId=new JSONObject(y.toString());
        // logger.info("usrId"+usrId);
        // String ids=usrId.getString("id");
        // columns.add("a._key == '"+ ids +"'");
        // logger.info("columns"+columns);
        // });

        ArangoCursor<HashMap> cursor2 = null;
        List<HashMap> response3 = new ArrayList<>();
        String columnIds = String.join(" OR ", columns);
        String queryToBeExecuted3 = "for a in " + userRegistration + "\r\n" + "filter "
            + columnIds + "\r\n" + "return {" + name + ":a.FirstName,id:a._key}";

        logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
        try {

          cursor2 = arangoDB.query(queryToBeExecuted3, HashMap.class);
          response3 = cursor2.asListRemaining();
          logger.info(String.valueOf(response3));

        } catch (Exception e) {
          log.error("Exception while getMatrixTeamDetails1_4 : " + e.getMessage().toString());
        }

        columns.clear();

        JSONObject teamDetail2 = new JSONObject();
        // teamDetail1.put("id", to);
        // teamDetail1.put("name", name);
        for (int l = 0; l < response3.size(); l++) {

          JSONObject roleuserObject = new JSONObject();
          JSONObject tuser = new JSONObject(response3.get(l));
          logger.info("tuser" + tuser);
          roleuserObject.put("name", tuser.getString(name));
          roleuserObject.put("id", tuser.getString("id"));
          // Collection k = teamDetail.values();
          keycolumns.add(roleuserObject);


        }

        teamDetail3.put(name, keycolumns);
        keycolumns.clear();
        logger.info("teamDetail1" + teamDetail1);
        response4.add(teamDetail3.toMap());
        teamDetails.put("roles", response4);
        response4.clear();
        List<Object> Business = new ArrayList<>();
        List<Object> DataDomain = new ArrayList<>();
        List<Object> Geography = new ArrayList<>();
        List<Object> Products = new ArrayList<>();
        List<HashMap> response5 = new ArrayList<>();
        ArangoCursor<HashMap> cursor4 = null;
        String columnIds3 = String.join(" OR ", columns3);
        String queryToBeExecuted4 = "for a in Business\r\n" + "filter " + columnIds3
            + "\r\n" + "return {name:a.name,type:a.typeName,id:a._key}";

        logger.info("queryToBeExecuted----->" + queryToBeExecuted4);

        try {

          cursor4 = arangoDB.query(queryToBeExecuted4, HashMap.class);
          response5 = cursor4.asListRemaining();
          logger.info(String.valueOf(response5));

        } catch (Exception e) {
          log.error("Exception while getMatrixTeamDetails1_5: " + e.getMessage().toString());
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
          String id = cate.getString("id");
          if (type.equals("Data Domain")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
            DataDomain.add(categoryObject);
          } else if (type.equals("Product")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
            Products.add(categoryObject);
          } else if (type.equals("Region")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
            Geography.add(categoryObject);
          } else if (type.equals("Line of Business")) {
            categoryObject.put("name", cname);
            categoryObject.put("key", id);
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
      // response2.forEach(b->{
      // JSONObject x=new JSONObject(b);
      // if(x.has("users")) {
      // //JSONArray users=x.getJSONArray("users");
      //
      // teamDetails.put("roles", response4);
      // }else {
      // teamDetails.put("roles", response4);
      // }
      // });
      // teamDetails.put("teamId", teamId);
      // contextteamDetails1.put("teamId", teamId);
      teamDetails.put("teamId", teamId);
      contextteamDetails.add(teamDetails);
    }

    // logger.info("response4"+response4);
    // logger.info("teamDetails"+teamDetails);

    // getTeamDetails.put("teamId", teamId);
    // getTeamDetails.put("context", contextteamDetails);
    // contextteamDetails.clear();
    return contextteamDetails.toString();

  }

  public List<HashMap> removeCategories(String teamId, String contextName,
      List<String> categoryId) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<Object> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while removeCategories : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < categoryId.size(); i++) {
      columns.add("b.id == 'Business/" + categoryId.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted1 =
        "for a in " + userRoles + "\r\n" + "filter a._from == '" + response.get(0) + "' AND a."
            + contextName + "!=null \r\n" + "let x=(\r\n" + "for b in a." + contextName + "\r\n"
            + "filter " + columnIds + "\r\n" + "return b\r\n" + ")\r\n" + "UPDATE a WITH {"
            + contextName + ":REMOVE_VALUES(a." + contextName + ",x)} IN " + userRoles + "";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

    try {

      cursor3 = arangoDB.query(queryToBeExecuted1, Object.class);
      response1 = cursor3.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while removeCategories_2 : " + e.getMessage().toString());
    }

    return response2;
  }

  public List<HashMap> addCategoriesToContext(String teamId, String contextName,
      List<String> categoryId) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<Object> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while addCategoriesToContext : " + e.getMessage().toString());
    }

    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < categoryId.size(); i++) {
      // columns.clear();
      // columns.add("id:'"+ nameId.get(i)+"'");
      JSONObject addCategory = new JSONObject();
      String cateId = "Business/" + categoryId.get(i);
      addCategory.put("id", cateId);

      String queryToBeExecuted1 = "for a in " + userRoles + "\r\n" + "filter a._from == '"
          + response.get(0) + "'\r\n" + "UPDATE a WITH {" + contextName + ": push(a."
          + contextName + "," + addCategory + ",true) } IN userRoles";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted1, Object.class);
        response1 = cursor3.asListRemaining();
        logger.info(String.valueOf(response1));

      } catch (Exception e) {
        log.error("Exception while addCategoriesToContext_2 : " + e.getMessage().toString());
      }

    }

    return response2;

  }


  public List<String> removeMatrixRoles(String teamId, String roleName, String contextName)
      throws ServiceException {

    List<String> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    String from = "Teams/" + teamId;
    String to = "NodeTypes/" + roleName;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for a in " + userRoles + "\r\n" + "filter a._from == '" + from
        + "' AND a._to == '" + to + "' AND a." + contextName + "!=null \r\n"
        + "replace a with UNSET(a, 'users') IN " + userRoles + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<Object> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while removeMatrixRoles : " + e.getMessage().toString());
    }

    return response;

  }

  public List<Object> deletesavedPreferences(String savedFilterId) throws ServiceException {
    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, savedPreferences);

    String query = "for doc in " + savedPreferences + "\r\n" + "filter doc._key == '"
        + savedFilterId + "'\r\n" + "remove doc._key in " + savedPreferences + "";

    logger.info("query----->" + query);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(query, Object.class);
      response1 = cursor.asListRemaining();
      logger.info("query----->" + response1);

    } catch (Exception e) {
      log.error("Exception while deletesavedPreferences : " + e.getMessage().toString());
    }

    String query1 =
        "for doc in " + savedPreferenceEdge + "\r\n" + "filter doc._to == 'savedPreferences/"
            + savedFilterId + "'\r\n" + "remove doc._key in " + savedPreferenceEdge + "";

    logger.info("query----->" + query1);
    ArangoCursor<Object> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query1, Object.class);
      response2 = cursor1.asListRemaining();
      logger.info("query----->" + response2);

    } catch (Exception e) {
      log.error("Exception while deletesavedPreferences_2 : " + e.getMessage().toString());
    }

    return response1;
  }

  public List<Object> updatesavedPreferences(String savePreferencesHolderId, String savedFilterId,
      String savedFilterReName) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, savedPreferences);

    String query2 = "for doc in " + savedPreferences + "\r\n" + "filter doc.holderId =='"
        + savePreferencesHolderId + "' && doc.name == '" + savedFilterReName + "'\r\n"
        + "return doc";

    logger.info("query1----->" + query2);
    ArangoCursor<Object> cursor2 = null;
    try {

      cursor2 = arangoDB.query(query2, Object.class);
      response2 = cursor2.asListRemaining();
      logger.info("query----->" + response2);

    } catch (Exception e) {
      log.error("Exception while updatesavedPreferences : " + e.getMessage().toString());
    }

    if (response2.isEmpty()) {

      String query = "for doc in " + savedPreferences + "\r\n" + "filter doc._key =='"
          + savedFilterId + "'\r\n" + "update doc with {name:'" + savedFilterReName
          + "',displayName:'" + savedFilterReName + "'}in " + savedPreferences + "";
      ;

      logger.info("query----->" + query);
      ArangoCursor<String> cursor = null;
      try {

        cursor = arangoDB.query(query, String.class);
        response1 = cursor.asListRemaining();
        logger.info("query----->" + response1);

      } catch (Exception e) {
        log.error("Exception while updatesavedPreferences_2 : " + e.getMessage().toString());
      }

    } else {
      throw new UnAuthorizedException(query2, null);
    }
    return response;

  }


  public List<Object> editsavedPreferences(String savePreferencesHolder,
      String savePreferencesHolderId, String savedFilterId) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, savedPreferences);

    String query = "for doc in " + savedPreferences + "\r\n"
        + "filter doc.savePreferencesHolder == '" + savePreferencesHolder
        + "' AND doc.savePreferencesHolderId == '" + savePreferencesHolderId + "'\r\n"
        + "for b in doc.Filters\r\n" + "filter b.id == " + savedFilterId + "\r\n" + "return b";

    logger.info("query----->" + query);
    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();
      logger.info("query----->" + response1);

    } catch (Exception e) {
      log.error("Exception while editsavedPreferences : " + e.getMessage().toString());
    }

    return response;

  }


  public List<HashMap> editRoles(String teamId, String roleId) throws ServiceException {

    List<String> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    String roleId1 = "NodeTypes/" + roleId;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted =
        "FOR a IN " + Teams + "\r\n" + "filter a._key == '" + teamId + "'\r\n" + "return a._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<Object> cursor3 = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while editRoles : " + e.getMessage().toString());
    }

    String queryToBeExecuted2 =
        "for a in " + userRoles + "\r\n" + "filter a._from == '" + response.get(0)
            + "' AND a._to == '" + roleId1 + "'\r\n" + "remove a._key IN " + userRoles + "";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted2);

    try {

      cursor1 = arangoDB.query(queryToBeExecuted2, HashMap.class);
      response2 = cursor1.asListRemaining();
      logger.info(String.valueOf(response2));

    } catch (Exception e) {
      log.error("Exception while editRoles_2 : " + e.getMessage().toString());
    }

    return response2;
  }

  public List<Object> deletesavedPreferencesValues(String savedFilterId,
      List<String> savedFiltervalue) {

    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < savedFiltervalue.size(); i++) {
      columns.add("c.value == '" + savedFiltervalue.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, savedPreferences);

    String query1 = "for a in savedPreferenceEdge\r\n" + "filter a._to == 'savedPreferences/"
        + savedFilterId + "' \r\n" + "let b=(\r\n" + "for c in a.NodeFilters\r\n" + "filter "
        + columnIds + "\r\n" + "return c)\r\n"
        + "UPDATE a WITH { NodeFilters:REMOVE_VALUES(a.NodeFilters,b) } IN savedPreferenceEdge ";
    logger.info("query----->" + query1);
    ArangoCursor<Object> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query1, Object.class);
      response2 = cursor1.asListRemaining();
      logger.info("query----->" + response2);

    } catch (Exception e) {
      log.error("Exception while deletesavedPreferencesValues : " + e.getMessage().toString());
    }

    return response1;

  }

  public List<Object> getsavePreferences1(String savePreferencesHolder,
      String savePreferencesHolderId, HashMap filterValues) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response2 = new ArrayList<>();
    List<HashMap> response3 = new ArrayList<>();
    List<HashMap> response4 = new ArrayList<>();
    JSONObject savedFilters = new JSONObject();
    ArrayList<JSONObject> savedFiltersList = new ArrayList<>();
    ArrayList<JSONObject> nodeFiltersList = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, userRegistration);
    ArangoCollection arangoEdgeCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, savedPreferenceEdge);
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    List<HashMap> nodeFilters = (List<HashMap>) filterValues.get("nodeFilters");
    for (int i = 0; i < nodeFilters.size(); i++) {
      JSONObject filter = new JSONObject(nodeFilters.get(i));
      nodeFiltersList.add(filter);
    }
    String nodeName = filterValues.get("nodeName").toString();

    String queryToBeExecuted = "for doc in " + userRegistration + "\r\n"
        + "filter doc.Email == '" + savePreferencesHolder + "' AND doc._key == '"
        + savePreferencesHolderId + "'\r\n" + "return doc._id";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsavePreferences1 : " + e.getMessage().toString());
    }

    String usrId = response.get(0).toString();
    String[] usrSplit = usrId.split("/");
    String cname = usrSplit[0];
    String cId = usrSplit[1];

    String query = "for doc in " + savedPreferenceEdge + "\r\n" + "filter doc._from == '"
        + response.get(0) + "'\r\n" + "return doc._to";
    logger.info("queryToBeExecuted----->" + query);

    ArangoCursor<Object> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query, Object.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getsavePreferences1_2 : " + e.getMessage().toString());
    }
    if (response1.isEmpty()) {

      savedFilters.put("name", nodeName);
      // String pname = preferenceName.get(0);
      savedFilters.put("displayName", nodeName);
      savedFilters.put("holderId", cId);
      // savedFilters.put("id", response2.get(0));
      savedFilters.put("searchedOn", datestr);
      savedFiltersList.add(savedFilters);

      ArangoCursor<Object> cursor2 = null;

      String queryToBeExecuted2 = "INSERT " + savedFiltersList.get(0) + "IN " + savedPreferences
          + " \r\n" + "return NEW._id";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
      try {

        cursor2 = arangoDB.query(queryToBeExecuted2, Object.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences1_3 : " + e.getMessage().toString());
      }

      String query2 = "INSERT { _from: '" + response.get(0) + "',_to: '" + response2.get(0)
          + "', NodeFilters:" + nodeFiltersList + "} INTO savedPreferenceEdge\r\n"
          + "return NEW._key";
      logger.info("queryToBeExecuted----->" + query2);
      try {

        cursor2 = arangoDB.query(query2, Object.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences1_4 : " + e.getMessage().toString());
      }


    } else {

      String query3 = "for doc in " + savedPreferences + "\r\n" + "filter doc.holderId == '"
          + savePreferencesHolderId + "'\r\n" + "return doc";
      logger.info("queryToBeExecuted----->" + query3);

      ArangoCursor<HashMap> cursor3 = null;
      try {

        cursor3 = arangoDB.query(query3, HashMap.class);
        response3 = cursor3.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences1_5 : " + e.getMessage().toString());
      }

      int counter = 1;

      String id = null;
      for (int i = 0; i < response3.size(); i++) {

        HashMap list = response3.get(i);
        JSONObject nodes1 = new JSONObject(list);
        logger.info("nodes1" + nodes1);
        id = nodes1.getString("_id");
        String name = nodes1.get("name").toString();
        // logger.info("preferenceName" + preferenceName);
        if (name.equals(nodeName)) {
          counter = counter + 1;
        }
      }

      savedFilters.put("displayName", nodeName);
      String pname = nodeName + counter;
      savedFilters.put("name", pname);
      savedFilters.put("holderId", cId);
      // savedFilters.put("id", response2.get(0));
      savedFilters.put("searchedOn", datestr);
      savedFiltersList.add(savedFilters);

      ArangoCursor<Object> cursor2 = null;

      String queryToBeExecuted2 = "INSERT " + savedFiltersList.get(0) + "IN " + savedPreferences
          + " \r\n" + "return NEW._id";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
      try {

        cursor2 = arangoDB.query(queryToBeExecuted2, Object.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences1_6 : " + e.getMessage().toString());
      }

      String query2 = "INSERT { _from: '" + response.get(0) + "',_to: '" + response2.get(0)
          + "', NodeFilters:" + nodeFiltersList + "} INTO savedPreferenceEdge\r\n"
          + "return NEW._key";
      logger.info("queryToBeExecuted----->" + query2);
      try {

        cursor2 = arangoDB.query(query2, Object.class);
        response2 = cursor2.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while getsavePreferences1_7 : " + e.getMessage().toString());
      }
    }

    return response;
  }

  public List<Object> getRecentPinCollection() throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<String> pinresponse1 = new ArrayList<>();
    final List<String>[] response1 = new List[]{new ArrayList<String>()};
    List<Object> pinresponse2 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, recentCollections);

    String queryToBeExecuted = "FOR doc IN recentPinCollection\r\n"
        + "SORT doc.searchedOn DESC\r\n"
        + "LIMIT 10\r\n"
        + "RETURN doc.nodepinkey";
           /* "for a in " + recentCollections + "\r\n" + "filter a.recentCollectionList !=null \r\n"
                    + "for b in a.recentCollectionList\r\n" + "return b.collectionkey";*/

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getRecentPinCollection : " + e.getMessage().toString());
    }
    logger.info(String.valueOf(response));

    for (int i = 0; i < response.size(); i++) {
      columns.add("node._key == '" + response.get(i) + "'");
    }

    String columnIds = String.join(" OR ", columns);

    String query1 =
        "for node in PinCollection\r\n" + "filter " + columnIds + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + query1);
    // ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor2 = null;
    try {

      cursor2 = arangoDB.query(query1, String.class);
      pinresponse1 = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getRecentPinCollection_2 : " + e.getMessage().toString());
    }

    pinresponse1.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayName"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      if (nodes.has("cover")) {
        pinHeaders.put("cover", nodes.getString("cover"));
      }
      pinHeaders.put("key", nodes.getString("_key"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("createdon", nodes.getString("createdon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      response1[0] = getCurateNodes(nodes.getString("_key"));

      if(!response1[0].isEmpty()){
        pinHeaders.put("curate", true);
      }else{
        pinHeaders.put("curate", false);
      }
      pinHeaderInfo.add(pinHeaders.toMap());
    });
    return pinHeaderInfo;
    // return response1;
  }

  public List<Object> addProfileCategories(HashMap profiledetails) throws ServiceException {

    List<HashMap> response2 = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response = new ArrayList<>();
    List<String> presponse = new ArrayList<>();
    List<Object> presponse1 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    JSONObject counterObj = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, profileCategories);

    String user = profiledetails.get("userId").toString();
    String userId = "registerUsers/" + user;
    ArangoCursor<Object> cursor = null;
    String queryToBeExecuted = "for a in " + profileCategories + "\n"
        + "filter a._from == '" + userId + "'\n"
        + "return a";
    logger.info(queryToBeExecuted);

    try {
      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
      logger.info(String.valueOf(response));
    } catch (Exception e) {
      log.error("Exception while addProfileCategories: " + e.getMessage().toString());
    }
    if (response.isEmpty()) {
      List<HashMap> business = (List<HashMap>) profiledetails.get("business");
      List<HashMap> dataDomain = (List<HashMap>) profiledetails.get("dataDomain");
      // teamdetails.get("dataDomain").toString();
      // .getJSONArray("dataDomain");
      List<HashMap> geography = (List<HashMap>) profiledetails.get("geography");
      List<HashMap> product = (List<HashMap>) profiledetails.get("products");
      List<Object> contexts = new ArrayList<Object>();

      business.forEach(b -> {
        JSONObject busines = new JSONObject(b);
        logger.info("business" + busines);
        String id = busines.get("key").toString();
        JSONObject busiObject = new JSONObject();
        busiObject.put("id", id);
        contexts.add(busiObject);
      });

      dataDomain.forEach(d -> {
        JSONObject domain = new JSONObject(d);
        String id = domain.getString("key");
        JSONObject dataDomainObject = new JSONObject();
        dataDomainObject.put("id", id);
        contexts.add(dataDomainObject);
      });

      // String datadomainId=dataDomain.getString("key");
      // dataDomainObject.put("id", datadomainId);

      geography.forEach(g -> {
        JSONObject geograph = new JSONObject(g);
        String id = geograph.getString("key");
        JSONObject geographyObject = new JSONObject();
        geographyObject.put("id", id);
        contexts.add(geographyObject);
      });

      product.forEach(p -> {
        JSONObject prodct = new JSONObject(p);
        String id = prodct.getString("key");
        JSONObject productObject = new JSONObject();
        productObject.put("id", id);
        contexts.add(productObject);
      });

      int contextCounter = 1;
      ArangoCursor<Object> cursor1 = null;
      String queryToBeExecuted1 =
          "insert {_from:'" + userId + "',_to:'" + userId + "'," + "Context" + contextCounter + ":"
              + contexts
              + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
              + ",contextCounter:" + contextCounter + "} In " + profileCategories + "\r\n";
      logger.info(queryToBeExecuted1);

      try {
        cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
        response1 = cursor1.asListRemaining();
        logger.info(String.valueOf(response1));
      } catch (Exception e) {
        log.error("Exception while addProfileCategories_2 : " + e.getMessage().toString());
      }
    } else {

      String query = "for a in " + profileCategories + "\r\n" + "filter a._from == '" + userId
          + "'\r\n" + "return a";
      logger.info("query----->" + query);
      ArangoCursor<String> cursor1 = null;
      try {

        cursor1 = arangoDB.query(query, String.class);
        presponse = cursor1.asListRemaining();
        logger.info(String.valueOf(presponse));

      } catch (Exception e) {
        log.error("Exception while addProfileCategories_3 : " + e.getMessage().toString());
      }

      List<String> contextName = new ArrayList<>();
      HashSet<String> contextName2 = new HashSet<String>();
      HashSet<Integer> contextName3 = new HashSet<Integer>();
      List<String> contextColumns = new ArrayList<>();
      // response4.clear();
      for (int i = 0; i < presponse.size(); i++) {
        JSONObject s = new JSONObject(presponse.get(i));
        contextName.clear();
        logger.info("s" + s);
        Set<String> keys = s.keySet();
        Object[] namesArray = keys.toArray();
        for (int j = 0; j < namesArray.length; j++) {
          logger.info(j + ": " + namesArray[j]);
          contextName.add(namesArray[j].toString());
        }

        for (int x = 0; x < contextName.size(); x++) {
          if (contextName.get(x).contains("lastmodifiedon")
              || contextName.get(x).contains("_from") || contextName.get(x).contains("createdby")
              || contextName.get(x).contains("_rev")
              || contextName.get(x).contains("lastmodifiedby")
              || contextName.get(x).contains("_id") || contextName.get(x).contains("_to")
              || contextName.get(x).contains("_key") || contextName.get(x).contains("createdon")
              || contextName.get(x).contains("contextCounter")) {

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
      List<Integer> listWithUniqueContextValues1 = new ArrayList<>(setWithUniqueValues1);

      int largest = Collections.max(listWithUniqueContextValues1);
      logger.info("largest" + largest);
      int largest1 = largest + 1;

      counterObj.put("counter", largest1);

      List<HashMap> business = (List<HashMap>) profiledetails.get("business");
      List<HashMap> dataDomain = (List<HashMap>) profiledetails.get("dataDomain");
      // teamdetails.get("dataDomain").toString();
      // .getJSONArray("dataDomain");
      List<HashMap> geography = (List<HashMap>) profiledetails.get("geography");
      List<HashMap> product = (List<HashMap>) profiledetails.get("products");
      List<Object> contexts = new ArrayList<Object>();
      business.forEach(b -> {
        JSONObject busines = new JSONObject(b);
        logger.info("business" + busines);
        String id = busines.get("key").toString();
        JSONObject busiObject = new JSONObject();
        busiObject.put("id", id);
        contexts.add(busiObject);
      });

      dataDomain.forEach(d -> {
        JSONObject domain = new JSONObject(d);
        String id = domain.getString("key");
        JSONObject dataDomainObject = new JSONObject();
        dataDomainObject.put("id", id);
        contexts.add(dataDomainObject);
      });

      // String datadomainId=dataDomain.getString("key");
      // dataDomainObject.put("id", datadomainId);

      geography.forEach(g -> {
        JSONObject geograph = new JSONObject(g);
        String id = geograph.getString("key");
        JSONObject geographyObject = new JSONObject();
        geographyObject.put("id", id);
        contexts.add(geographyObject);
      });

      product.forEach(p -> {
        JSONObject prodct = new JSONObject(p);
        String id = prodct.getString("key");
        JSONObject productObject = new JSONObject();
        productObject.put("id", id);
        contexts.add(productObject);
      });

      ArangoCursor<Object> qcursor1 = null;
      String query1 =
          "insert {_from:'" + userId + "',_to:'" + userId + "'," + "Context" + counterObj.getInt(
              "counter") + ":" + contexts
              + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
              + ",contextCounter:" + counterObj.getInt("counter") + "} In " + profileCategories
              + "\r\n";

      logger.info(query1);

      try {
        qcursor1 = arangoDB.query(query1, Object.class);
        presponse1 = qcursor1.asListRemaining();
        logger.info(String.valueOf(presponse1));
      } catch (Exception e) {
        log.error("Exception while addProfileCategories : " + e.getMessage().toString());
      }
    }
    return response1;
  }

  public void removeFilterData(ArangoDatabase arangodb) throws ServiceException {
    String queryToBeExecuted2 = "for doc in " + arangoFilterSearchCollection + "\r\n"
        + "remove doc._key in " + arangoFilterSearchCollection + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted2);
    // ArangoCursor<Object> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    try {

      arangoDB.query(queryToBeExecuted2, Object.class);

    } catch (Exception e) {
      log.error("Exception while removeFilterData : " + e.getMessage().toString());
    }

    // ArangoCursor<Object> cursor4 = null;
    String queryToBeExecuted3 = "for doc in " + arangoSearchTypeCollection + "\r\n"
        + "remove doc._key in " + arangoSearchTypeCollection + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
    // ArangoCursor<Object> cursor = null;
    try {

      arangoDB.query(queryToBeExecuted3, Object.class);

    } catch (Exception e) {
      log.error("Exception while removeFilterData_2 : " + e.getMessage().toString());
    }

    String queryToBeExecuted4 = "for doc in " + arangoSortCollection + "\r\n"
        + "remove doc._key in " + arangoSortCollection + "";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted4);
    try {
      arangoDB.query(queryToBeExecuted4, Object.class);
    } catch (Exception e) {
      log.error("Exception while removeFilterData_3 : " + e.getMessage().toString());
    }
  }

  public void buildAttributes(JSONArray attributeInfo, JSONObject nodesinfo1, String nameType,
      List<String> columns9, List<String> columns5, List<String> columns6, List<String> columns4)
      throws ServiceException {
    if (!attributeInfo.isEmpty()) {
      attributeInfo.forEach(eachAttribute -> {
        JSONObject attributes = new JSONObject(eachAttribute.toString());
        logger.info(String.valueOf(attributes));
        if (!attributes.isEmpty()) {
          if (attributes.get("name").toString().equals("Description")) {
            String value = attributes.getString("value");
            nodesinfo1.put("Description", "<b>" + value + "</b>");
          } else if (attributes.get("name").toString().equals("Definition")) {
            String value = attributes.getString("value");
            nodesinfo1.put("Description", "<b>" + value + "</b>");
          }
          if (attributes.get("name").toString().equals("Certified")) {
            String value = attributes.getString("value");
            nodesinfo1.put("Certified", value);
            if (nameType.contains("Data Set") || nameType.contains("Schema")) {
              columns9.add(value);
            }
          }
          if (attributes.get("name").toString().equals("Frequency")) {
            String frequency = attributes.getString("value");
            nodesinfo1.put("Frequency", frequency);
            if (nameType.contains("Data Set") || nameType.contains("Schema")) {
              columns5.add(frequency);
            }
          }
          if (attributes.get("name").toString().equals("LastModifiedOn")) {
            String value = attributes.getString("value");
//            long l = Long.parseLong(value);
//            String Date = LocalDateTime
//                .ofInstant(Instant.ofEpochMilli(Long.valueOf(l)), ZoneId.systemDefault())
//                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //          nodesinfo1.put("LastModifiedOn", Date);
            nodesinfo1.put("LastModifiedOn", value);
            if (nameType.contains("Data Set") || nameType.contains("Schema")) {
              columns6.add(value);
            }
          }
          if (attributes.get("name").toString().equals("Personally Identifiable Information")) {
            String value = attributes.getString("value");
            nodesinfo1.put("PersonallyIdentifiableInformation", value);
          }
          if (attributes.get("name").toString().equals("Security Classification")) {
            String value = attributes.getString("value");
            nodesinfo1.put("SecurityClassification", value);

          }
          if (attributes.get("name").toString().equals("tag")) {
            String tag = attributes.getString("value");
            nodesinfo1.put("Tag", tag);
            columns4.add(tag);
          }
          if (attributes.get("name").toString().equals("Passing Fraction")) {
            String value = attributes.getString("value");
            nodesinfo1.put("PassingFraction", value);
          }
          if (attributes.get("name").toString().equals("Shoppable")) {
            String value = attributes.getString("value");
            nodesinfo1.put("Shopable", value);
          }
          if (attributes.get("name").toString().equals("searchable")) {
            String value = attributes.getString("value");
            nodesinfo1.put("searchable", value);
          }
          if (attributes.get("name").toString().equals("url")) {
            String value = attributes.getString("value");
            nodesinfo1.put("url", value);
          }
        }
      });
    } else if (attributeInfo.isEmpty()) {
      nodesinfo1.put("No Attributes", " Attributes not available ");
    }
  }

  public void buildRelations(JSONArray targetedges, JSONArray sourceedges, JSONObject nodesinfo1,
      JSONArray targetsObj, JSONArray sourceObj) throws ServiceException {
    if (!targetedges.isEmpty()) {
      targetedges.forEach(eachsource -> {
        JSONObject targets = new JSONObject(eachsource.toString());
        String targetname = null;
        if (targets.has("CoRole")) {
          targetname = targets.getString("CoRole");
        } else {
          targetname = targets.getString("coRole");
        }
        if (targetname.contains("contains") || targetname.contains("represents")) {
          targetsObj.put(targets);
        }
      });

      if (!sourceedges.isEmpty()) {
        sourceedges.forEach(eachsource -> {
          JSONObject sources = new JSONObject(eachsource.toString());
          String source = sources.getString("role");
          if (source.contains("produce")) {
            sourceObj.put(sources);
          }
        });
      }
      if (!targetsObj.isEmpty()) {
        JSONObject targetedges1 = targetsObj.getJSONObject(0);
        JSONObject target1 = targetedges1.getJSONObject("target");
        // String targetname=targetedges1.getString("CoRole");
        String targetname = null;
        if (targetedges1.has("CoRole")) {
          targetname = targetedges1.getString("CoRole");
        } else {
          targetname = targetedges1.getString("coRole");
        }
        String targetid = target1.getString("id");
        String query1 = getQueryResult(targetid);
        String result = null;
        if (targetname.contains("contains") || targetname.contains("represents")) {
          result = getNodesResponse(query1);
          nodesinfo1.put("SourceSystem", result);

        } else {
          nodesinfo1.put("SourceSystem", "null");
        }
      } else if (!sourceObj.isEmpty()) {

        JSONObject sourceedges1 = sourceObj.getJSONObject(0);
        String result = null;

        JSONObject source1 = sourceedges1.getJSONObject("source");
        String sourcename = sourceedges1.getString("role");
        String sourceid = source1.getString("id");
        String query1 = getQueryResult(sourceid);

        if (sourcename.contains("produce")) {
          result = getNodesResponse(query1);
          nodesinfo1.put("SourceSystem", result);

        } else {
          nodesinfo1.put("SourceSystem", "null");
        }
      } else {
        nodesinfo1.put("SourceSystem", "null");
      }
    } else {
      nodesinfo1.put("SourceSystem", "null");
    }
  }

  public List<Object> delTags(String id, String tagName) throws ServiceException {

    List<Object> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<String> tags = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    String to = "Nodes/" + id + "";
    String from = "TagsCollection/" + tagName + "";

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query =
        "for a in Nodes\n"
            + "filter a.id == '" + id + "'\n"
            + "let b=(for c in a.attributes\n"
            + "filter c.name == \"tag\" AND c.value == '" + tagName + "'\n"
            + "return c)\n"
            + "UPDATE a WITH { attributes:REMOVE_VALUES(a.attributes,b) } IN Nodes";

    logger.info("queryToBeExecuted----->" + query);

    ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(query, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while delTags : " + e.getMessage().toString());
    }

    String queryToBeExecuted =
        "for a in TagsEdges\n"
            + "filter a._from == '" + from + "' AND a._to == '" + to + "'\r\n"
            + "remove a._key in TagsEdges";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    // ArangoCursor<Object> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while delTags_2 : " + e.getMessage().toString());
    }

    return response;
  }


  public List<Object> getPinSharedWithMe(String loginId) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();
    final List<String>[] response2 = new List[]{new ArrayList<String>()};
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for a in  " + mySharedCollection + "\r\n"
        + "filter a.shareCollection !=null\r\n"
        + "for b in a.shareCollection\r\n" + "for c in b.users\r\n" + "return {users:c,url:b.url}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinSharedWithMe : " + e.getMessage().toString());
    }
    logger.info(String.valueOf(response));

    response.forEach(a -> {
      JSONObject b = new JSONObject(a);
      String id = b.getString("users");
      String Url = b.getString("url");
      String[] urlSplit = Url.split("/");
      String url = urlSplit[5];
      if (id.equals(loginId)) {
        columns.add("node._key == '" + url + "'");
      }
    });

    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted1 =
        "for node in PinCollection\r\n" + "filter " + columnIds + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinSharedWithMe_2 : " + e.getMessage().toString());
    }
    response1.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayName"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      if (nodes.has("cover")) {
        pinHeaders.put("cover", nodes.getString("cover"));
      }
      String key = nodes.getString("_key");
      pinHeaders.put("key", nodes.getString("_key"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("createdon", nodes.getString("createdon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      response2[0] = getCurateNodes(key);
      System.out.println("response1[0]"+response2[0]);
      if(!response2[0].isEmpty()){
        pinHeaders.put("curate", true);
      }else{
        pinHeaders.put("curate", false);
      }
      pinHeaderInfo.add(pinHeaders.toMap());
    });
    return pinHeaderInfo;
  }

  public List<Object> getPinSharedByMe(String loginId) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();
    final List<String>[] response2 = new List[]{new ArrayList<String>()};
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    String queryToBeExecuted = "for a in  " + mySharedCollection + "\r\n"
        + "filter a.shareCollection !=null\r\n"
        + "for b in a.shareCollection\r\n" + "return {shareHolderIds:a.shareHolderId,url:b.url}";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<HashMap> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinSharedByMe : " + e.getMessage().toString());
    }
    logger.info(String.valueOf(response));

    response.forEach(a -> {
      JSONObject b = new JSONObject(a);
      String id = b.getString("shareHolderIds");
      String Url = b.getString("url");
      String[] urlSplit = Url.split("/");
      String url = urlSplit[5];
      if (id.equals(loginId)) {
        columns.add("node._key == '" + url + "'");
      }
    });

    String columnIds = String.join(" OR ", columns);

    String queryToBeExecuted1 =
        "for node in PinCollection\r\n" + "filter " + columnIds + "\r\n" + "return node";
    logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
    ArangoCursor<HashMap> cursor1 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
      response1 = cursor1.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinSharedByMe_2: " + e.getMessage().toString());
    }

    response1.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayName"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      if (nodes.has("cover")) {
        pinHeaders.put("cover", nodes.getString("cover"));
      }
      String key = nodes.getString("_key");
      pinHeaders.put("key", nodes.getString("_key"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("createdon", nodes.getString("createdon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      response2[0] = getCurateNodes(key);
    //  System.out.println("response1[0]"+response2[0]);
      if(!response2[0].isEmpty()){
        pinHeaders.put("curate", true);
      }else{
        pinHeaders.put("curate", false);
      }
      pinHeaderInfo.add(pinHeaders.toMap());
    });
    return pinHeaderInfo;

  }

  public List<Object> addNodesCategoriesList(HashMap details) {

    List<HashMap> response2 = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    List<Object> response = new ArrayList<>();
    List<String> presponse = new ArrayList<>();
    List<Object> presponse1 = new ArrayList<>();

    List<String> columns = new ArrayList<String>();
    JSONObject counterObj = new JSONObject();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoEdgeCollection(arangoDB, profileCategories);

    String node = details.get("nodeId").toString();
    String nodeId = "Nodes/" + node;
    ArangoCursor<Object> cursor = null;
    String queryToBeExecuted = "for a in " + profileCategories + "\n"
        + "filter a._from == '" + nodeId + "'\n"
        + "return a";
    System.out.println(queryToBeExecuted);

    try {
      cursor = arangoDB.query(queryToBeExecuted, Object.class);
      response = cursor.asListRemaining();
      System.out.println(response);
    } catch (Exception e) {
      log.error("Exception while addNodesCategoriesList : " + e.getMessage().toString());
    }
    if (response.isEmpty()) {
      List<HashMap> business = (List<HashMap>) details.get("business");
      List<HashMap> dataDomain = (List<HashMap>) details.get("dataDomain");
      // teamdetails.get("dataDomain").toString();
      // .getJSONArray("dataDomain");
      List<HashMap> geography = (List<HashMap>) details.get("geography");
      List<HashMap> product = (List<HashMap>) details.get("products");
      List<Object> contexts = new ArrayList<Object>();

      business.forEach(b -> {
        JSONObject busines = new JSONObject(b);
        System.out.println("business" + busines);
        String id = busines.get("key").toString();
        JSONObject busiObject = new JSONObject();
        busiObject.put("id", id);
        contexts.add(busiObject);
      });

      dataDomain.forEach(d -> {
        JSONObject domain = new JSONObject(d);
        String id = domain.getString("key");
        JSONObject dataDomainObject = new JSONObject();
        dataDomainObject.put("id", id);
        contexts.add(dataDomainObject);
      });

      // String datadomainId=dataDomain.getString("key");
      // dataDomainObject.put("id", datadomainId);

      geography.forEach(g -> {
        JSONObject geograph = new JSONObject(g);
        String id = geograph.getString("key");
        JSONObject geographyObject = new JSONObject();
        geographyObject.put("id", id);
        contexts.add(geographyObject);
      });

      product.forEach(p -> {
        JSONObject prodct = new JSONObject(p);
        String id = prodct.getString("key");
        JSONObject productObject = new JSONObject();
        productObject.put("id", id);
        contexts.add(productObject);
      });

      int contextCounter = 1;
      ArangoCursor<Object> cursor1 = null;
      String queryToBeExecuted1 =
          "insert {_from:'" + nodeId + "',_to:'" + nodeId + "'," + "Context" + contextCounter + ":"
              + contexts
              + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
              + ",contextCounter:" + contextCounter + "} In " + profileCategories + "\r\n";
      System.out.println(queryToBeExecuted1);

      try {
        cursor1 = arangoDB.query(queryToBeExecuted1, Object.class);
        response1 = cursor1.asListRemaining();
        System.out.println(response1);
      } catch (Exception e) {
        log.error("Exception while addNodesCategoriesList_2 : " + e.getMessage().toString());
      }
    } else {
      String query = "for a in " + profileCategories + "\r\n" + "filter a._from == '" + nodeId
          + "'\r\n" + "return a";
      System.out.println("query----->" + query);
      ArangoCursor<String> cursor1 = null;
      try {

        cursor1 = arangoDB.query(query, String.class);
        presponse = cursor1.asListRemaining();
        System.out.println(presponse);

      } catch (Exception e) {
        log.error("Exception while addNodesCategoriesList_3 : " + e.getMessage().toString());
      }

      List<String> contextName = new ArrayList<>();
      HashSet<String> contextName2 = new HashSet<String>();
      HashSet<Integer> contextName3 = new HashSet<Integer>();
      List<String> contextColumns = new ArrayList<>();
      // response4.clear();
      for (int i = 0; i < presponse.size(); i++) {
        JSONObject s = new JSONObject(presponse.get(i));
        contextName.clear();
        System.out.println("s" + s);
        Set<String> keys = s.keySet();
        Object[] namesArray = keys.toArray();
        for (int j = 0; j < namesArray.length; j++) {
          System.out.println(j + ": " + namesArray[j]);
          contextName.add(namesArray[j].toString());
        }

        for (int x = 0; x < contextName.size(); x++) {
          if (contextName.get(x).contains("lastmodifiedon")
              || contextName.get(x).contains("_from") || contextName.get(x).contains("createdby")
              || contextName.get(x).contains("_rev")
              || contextName.get(x).contains("lastmodifiedby")
              || contextName.get(x).contains("_id") || contextName.get(x).contains("_to")
              || contextName.get(x).contains("_key") || contextName.get(x).contains("createdon")
              || contextName.get(x).contains("contextCounter")) {

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
      System.out.println("setWithUniqueValues" + setWithUniqueValues);
      List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
      System.out.println("listWithUniqueContextValues" + listWithUniqueContextValues);

      Set<Integer> setWithUniqueValues1 = new HashSet<>(contextName3);
      List<Integer> listWithUniqueContextValues1 = new ArrayList<>(setWithUniqueValues1);

      int largest = Collections.max(listWithUniqueContextValues1);
      System.out.println("largest" + largest);
      int largest1 = largest + 1;

      counterObj.put("counter", largest1);

      List<HashMap> business = (List<HashMap>) details.get("business");
      List<HashMap> dataDomain = (List<HashMap>) details.get("dataDomain");
      // teamdetails.get("dataDomain").toString();
      // .getJSONArray("dataDomain");
      List<HashMap> geography = (List<HashMap>) details.get("geography");
      List<HashMap> product = (List<HashMap>) details.get("products");
      List<Object> contexts = new ArrayList<Object>();
      business.forEach(b -> {
        JSONObject busines = new JSONObject(b);
        System.out.println("business" + busines);
        String id = busines.get("key").toString();
        JSONObject busiObject = new JSONObject();
        busiObject.put("id", id);
        contexts.add(busiObject);
      });

      dataDomain.forEach(d -> {
        JSONObject domain = new JSONObject(d);
        String id = domain.getString("key");
        JSONObject dataDomainObject = new JSONObject();
        dataDomainObject.put("id", id);
        contexts.add(dataDomainObject);
      });

      geography.forEach(g -> {
        JSONObject geograph = new JSONObject(g);
        String id = geograph.getString("key");
        JSONObject geographyObject = new JSONObject();
        geographyObject.put("id", id);
        contexts.add(geographyObject);
      });

      product.forEach(p -> {
        JSONObject prodct = new JSONObject(p);
        String id = prodct.getString("key");
        JSONObject productObject = new JSONObject();
        productObject.put("id", id);
        contexts.add(productObject);
      });

      ArangoCursor<Object> qcursor1 = null;
      String query1 =
          "insert {_from:'" + nodeId + "',_to:'" + nodeId + "'," + "Context" + counterObj.getInt(
              "counter") + ":" + contexts
              + ",createdby:'Admin',createdon:'12345',lastmodifiedby:'Admin',lastmodifiedon:'12345'"
              + ",contextCounter:" + counterObj.getInt("counter") + "} In " + profileCategories
              + "\r\n";

      System.out.println(query1);

      try {
        qcursor1 = arangoDB.query(query1, Object.class);
        presponse1 = qcursor1.asListRemaining();
        System.out.println(presponse1);
      } catch (Exception e) {
        log.error("Exception while addNodesCategoriesList_3 : " + e.getMessage().toString());
      }
    }

    return response1;

  }

  public List<Object> nodesCategories(String key) {

    List<String> response1 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();

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
    String queryToBeExecuted1 = "for a in " + profileCategories + "\r\n" + "filter a._from == '"
        + key + "'\r\n" + "return a";
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
          "for a in " + profileCategories + "\r\n" + "filter a._from == '" + key + "' AND  a."
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
    preferences.put("categories", usr);
    usr.clear();
    response3.add(preferences.toMap());
    return response3;


  }

  public List<Object> nodesUsage(String key) {
    List<HashMap> response = new ArrayList<>();
    List<Object> response1 = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for a in DataUsageArchive\r\n"
            +"filter a.itemIds !=null\r\n"
        + "for b in a.itemIds\r\n"
        + "filter b == '" + key + "'\r\n"
        + "return a";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while nodesUsage : " + e.getMessage().toString());
    }
    for (int i = 0; i < response.size(); i++) {
      JSONObject usage = new JSONObject(response.get(i));
      System.out.println("usage" + usage);
      JSONObject usagedetails = new JSONObject();
      String dataUsagenumber = usage.getString("_key");
      usagedetails.put("dataUsageNumber", dataUsagenumber);
      String callerName = usage.getString("callerName");
      usagedetails.put("callerName", callerName);
      JSONArray participants = usage.getJSONArray("requesterNames");
      usagedetails.put("participants", participants);
     // String requesterFor = usage.getString("requesterNames");
      usagedetails.put("requester", participants);
      String dsaNumber = usage.getString("DSA");
      usagedetails.put("dsaNumber", dsaNumber);
      String startDate = usage.getString("StartDate");
      usagedetails.put("startDate", startDate);
      String endDate = usage.getString("EndDate");
      usagedetails.put("endDate", endDate);
      String purpose = usage.getString("Purpose");
      usagedetails.put("purpose", purpose);
      String orderId = usage.getString("_key");
      usagedetails.put("orderId", orderId);
      if(usage.has("IncidentState")){
        String incidentState = usage.getString("IncidentState");
        usagedetails.put("incidentState", incidentState);
      }
      if(usage.has("IncidentNumber")){
        String incidentNumber = usage.getString("IncidentNumber");
        usagedetails.put("incidentNumber", incidentNumber);
      }
      if(usage.has("itemIds")){
        JSONArray itemIds = usage.getJSONArray("itemIds");
        usagedetails.put("itemIds", itemIds);
      }
      if(usage.has("itemUrls")){
        JSONArray itemUrls = usage.getJSONArray("itemUrls");
        usagedetails.put("itemUrls", itemUrls);
      }
      response1.add(usagedetails.toMap());
    }
    return response1;
  }

  public List<Object> nodesPicker(List<String> databaseName) {

    List<String> columns = new ArrayList<String>();

    for (int i = 0; i < databaseName.size(); i++) {
      columns.add("doc.name =='" + databaseName.get(i) + "'");
    }
    String columnIds1 = String.join(" OR ", columns);
    List<HashMap> response = new ArrayList<>();
    List<HashMap> response1 = new ArrayList<>();
    List<Object> picker = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query = "for doc in Nodes\r\n"
        + "filter " + columnIds1 + " AND doc.type.name == 'Database' \r\n"
        + "return doc";

    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, HashMap.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while retrieving Data in Search Results: " + e.getMessage().toString());
    }

    for (int i = 0; i < response.size(); i++) {
      JSONObject nodesinfo1 = new JSONObject();
      HashMap x = response.get(i);
      System.out.println(x);
      //picker.add(x);
      JSONObject nodes1 = new JSONObject(x);
      JSONObject nodes = new JSONObject(nodes1.toString());
      String databaseId = nodes.getString("id");

      String name = nodes.getString("displayName");
      String fullName = nodes.getString("name");
      JSONObject nodetype = nodes.getJSONObject("type");
      String nameType = nodetype.getString("name");
      JSONArray attributeInfo = nodes.getJSONArray("attributes");
      if (!attributeInfo.isEmpty()) {
        attributeInfo.forEach(eachAttribute -> {
          JSONObject attributes = new JSONObject(eachAttribute.toString());
          logger.info(String.valueOf(attributes));
          if (!attributes.isEmpty()) {
            if (attributes.get("name").toString().equals("Description")) {
              String value = attributes.getString("value");
              nodesinfo1.put("Description", value);
            }
          }
        });
      }
      nodesinfo1.put("id", databaseId);
      nodesinfo1.put("name", name);
      nodesinfo1.put("fullName", fullName);
      nodesinfo1.put("dataType", "");
      nodesinfo1.put("nodeType", nameType);
      nodesinfo1.put("length", "0");
      nodesinfo1.put("precision", "0");
      nodesinfo1.put("parent", "null");
      nodesinfo1.put("database", name);
      nodesinfo1.put("schema", "");
      nodesinfo1.put("table", "");
      nodesinfo1.put("column", "");
      picker.add(nodesinfo1.toMap());
      JSONObject relations = (JSONObject) nodes.get("relations");
      JSONArray targets = relations.getJSONArray("targets");
      if (!targets.isEmpty()) {
        for (int j = 0; j < targets.length(); j++) {
          JSONObject y = new JSONObject(targets.get(j).toString());
          System.out.println("y" + y);
          JSONObject ytarget = y.getJSONObject("target");
          String type = ytarget.getString("type");
          String id = ytarget.getString("id");
          if (type.equals("Schema")) {
            List<HashMap> response11 = getQuerysResult(id);
            if (!response11.isEmpty()) {
              JSONObject sc = new JSONObject(response11.get(0));
              System.out.println(sc);
              //sc.put("parent", databaseId);
              //picker.add(sc.toMap());
              System.out.println("picker" + picker);
              String schemaId = sc.getString("id");
              String scname = sc.getString("displayName");
              String scfullName = sc.getString("name");
              JSONObject scnodetype = sc.getJSONObject("type");
              String scnameType = scnodetype.getString("name");
              JSONArray scattributeInfo = sc.getJSONArray("attributes");
              if (!scattributeInfo.isEmpty()) {
                scattributeInfo.forEach(eachAttribute -> {
                  JSONObject attributes = new JSONObject(eachAttribute.toString());
                  logger.info(String.valueOf(attributes));
                  if (!attributes.isEmpty()) {
                    if (attributes.get("name").toString().equals("Description")) {
                      String value = attributes.getString("value");
                      nodesinfo1.put("Description", value);
                    }
                  }
                });
              }
              nodesinfo1.put("id", schemaId);
              nodesinfo1.put("name", scname);
              nodesinfo1.put("fullName", scfullName);
              nodesinfo1.put("dataType", "");
              nodesinfo1.put("nodeType", scnameType);
              nodesinfo1.put("length", "0");
              nodesinfo1.put("precision", "0");
              nodesinfo1.put("parent", databaseId);
              nodesinfo1.put("database", name);
              nodesinfo1.put("schema", scname);
              nodesinfo1.put("table", "");
              nodesinfo1.put("column", "");
              picker.add(nodesinfo1.toMap());
              JSONObject srelations = sc.getJSONObject("relations");
              JSONArray stargets = srelations.getJSONArray("targets");
              for (int s = 0; s < stargets.length(); s++) {
                JSONObject tl = new JSONObject(stargets.get(s).toString());
                System.out.println("tl" + tl);
                JSONObject tltarget = tl.getJSONObject("target");
                String tltype = tltarget.getString("type");
                String tlid = tltarget.getString("id");
                if (tltype.equals("Table")) {
                  List<HashMap> response12 = getQuerysResult(tlid);
                  if (!response12.isEmpty()) {
                    JSONObject tb = new JSONObject(response12.get(0));
                    System.out.println(tb);
                    tb.put("parent", schemaId);
                    //picker.add(tb.toMap());
                    String tableId = tb.getString("id");
                    String tbname = tb.getString("displayName");
                    String tbfullName = tb.getString("name");
                    JSONObject tbnodetype = tb.getJSONObject("type");
                    String tbnameType = tbnodetype.getString("name");
                    JSONArray tbattributeInfo = tb.getJSONArray("attributes");
                    if (!tbattributeInfo.isEmpty()) {
                      tbattributeInfo.forEach(eachAttribute -> {
                        JSONObject attributes = new JSONObject(eachAttribute.toString());
                        logger.info(String.valueOf(attributes));
                        if (!attributes.isEmpty()) {
                          if (attributes.get("name").toString().equals("Description")) {
                            String value = attributes.getString("value");
                            nodesinfo1.put("Description", value);
                          }
                        }
                      });
                    }
                    nodesinfo1.put("id", tableId);
                    nodesinfo1.put("name", tbname);
                    nodesinfo1.put("fullName", tbfullName);
                    nodesinfo1.put("dataType", "");
                    nodesinfo1.put("nodeType", tbnameType);
                    nodesinfo1.put("length", "0");
                    nodesinfo1.put("precision", "0");
                    nodesinfo1.put("parent", schemaId);
                    nodesinfo1.put("database", name);
                    nodesinfo1.put("schema", scname);
                    nodesinfo1.put("table", tbname);
                    nodesinfo1.put("column", "");
                    picker.add(nodesinfo1.toMap());
                    JSONObject trelations = tb.getJSONObject("relations");
                    JSONArray ttargets = trelations.getJSONArray("targets");
                    for (int t = 0; t < ttargets.length(); t++) {
                      JSONObject cm = new JSONObject(ttargets.get(t).toString());
                      JSONObject cmtarget = cm.getJSONObject("target");
                      String cmtype = cmtarget.getString("type");
                      String cmid = cmtarget.getString("id");
                      if (cmtype.equals("Column")) {

                        List<HashMap> response13 = getQuerysResult(cmid);
                        if (!response13.isEmpty()) {
                          JSONObject colmn = new JSONObject(response13.get(0));
                          System.out.println(colmn);
                          colmn.put("parent", tableId);
                          //picker.add(colmn.toMap());

                          String colmId = colmn.getString("id");
                          String colmname = colmn.getString("displayName");
                          String cfullName = colmn.getString("name");
                          JSONObject colmnodetype = colmn.getJSONObject("type");
                          String colmnameType = colmnodetype.getString("name");
                          JSONArray colmattributeInfo = colmn.getJSONArray("attributes");
                          if (!colmattributeInfo.isEmpty()) {
                            colmattributeInfo.forEach(eachAttribute -> {
                              JSONObject attributes = new JSONObject(eachAttribute.toString());
                              logger.info(String.valueOf(attributes));
                              if (!attributes.isEmpty()) {
                                if (attributes.get("name").toString().equals("Description")) {
                                  String value = attributes.getString("value");
                                  nodesinfo1.put("Description", value);
                                }
                              }
                            });
                          }
                          nodesinfo1.put("id", colmId);
                          nodesinfo1.put("name", colmname);
                          nodesinfo1.put("fullName", cfullName);
                          nodesinfo1.put("dataType", "");
                          nodesinfo1.put("nodeType", colmnameType);
                          nodesinfo1.put("length", "0");
                          nodesinfo1.put("precision", "0");
                          nodesinfo1.put("parent", tableId);
                          nodesinfo1.put("database", name);
                          nodesinfo1.put("schema", scname);
                          nodesinfo1.put("table", tbname);
                          nodesinfo1.put("column", colmname);
                          picker.add(nodesinfo1.toMap());
                        }
                      }

                    }
                  }
                }
              }

            }
          }
        }
      }


    }
    return picker;
  }


  public List<HashMap> nodesTeamResponsibilities(String key, List<HashMap> teamDetails) {

    List<HashMap> response = new ArrayList<>();
    ArangoCursor<HashMap> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    for (int i = 0; i < teamDetails.size(); i++) {
      JSONObject attr = new JSONObject(teamDetails.get(i));
      System.out.println(attr);
      String name = attr.get("name").toString();
      String roleName = attr.get("roleName").toString();
      String teamId = attr.get("teamId").toString();

      JSONObject responsibilities = new JSONObject();

      responsibilities.put("name", name);
      responsibilities.put("roleName", roleName);
      responsibilities.put("userName", "");
      responsibilities.put("groupName", "");
      responsibilities.put("teamId", teamId);

      String query1 = "for doc in Nodes\r\n"
          + "filter doc._key=='" + key + "'\r\n"
          + "let b=(for d in doc.responsibilities\r\n"
          + "return d)\r\n"
          + "UPDATE doc WITH { responsibilities:REMOVE_VALUES(doc.responsibilities,b) } IN Nodes";

      try {
        cursor = arangoDB.query(query1, HashMap.class);
        response = cursor.asListRemaining();
        logger.info("response" + response);
      } catch (Exception e) {
        log.error(
            "Exception while removing nodesTeamResponsibilities : " + e.getMessage().toString());
      }

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
            "Exception while nodesTeamResponsibilities : " + e.getMessage().toString());
      }


    }
    return response;

  }

  public List<Object> semanticNodes(HashMap columnsList) {

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
    List<String> columnkeys = (List<String>) columnsList.get("columns");

    List<String> columns = new ArrayList<>();
    for (int i = 0; i < columnkeys.size(); i++) {
      // String[] node = ((String) response.get(i)).split("/");
      columns.add("b.id == '" + columnkeys.get(i) + "'");
    }
    String columnIds = String.join(" OR ", columns);

//	      String query2 = "for a in PhysicalDataDictionary\r\n"
//	          + "filter " + columnIds + "\r\n"
//	          + "return a";
    String query2 = "for a in PhysicalDataDictionary\n"
        + "for b in a.nodes\n"
        + "filter " + columnIds + "\n"
        + "return a";
    logger.info("queryToBeExecuted----->" + query2);
    try {
      cursor1 = arangoDB.query(query2, HashMap.class);
      response1 = cursor1.asListRemaining();
    } catch (Exception e) {
      log.error("Exception while executing  Query: " + e.getMessage().toString());
    }

    for (int i = 0; i < response1.size(); i++) {
      JSONObject semantics = new JSONObject();
      JSONObject semanticJsonSuggested = new JSONObject();
      JSONArray suggestedName = new JSONArray();
      JSONObject physicalJson = new JSONObject(response1.get(i));
      // String source = physicalJson.getString("source");
      //String Id=null;
      //String name=null;
      if(!physicalJson.has("source")) {
        // String name = physicalJson.getString("name");
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
          log.error("Exception while executing  Query: " + e.getMessage().toString());
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
                  + "for b in a.nodes\n"
                  + "filter b.id == " + id + "\r\n"
                  + "return a";

          logger.info("queryToBeExecuted----->" + querytoexecute);

          try {

            cursor1 = arangoDB.query(querytoexecute, HashMap.class);
            businessResponse1 = cursor1.asListRemaining();

          } catch (Exception e) {
            log.error("Exception while executing  Query: " + e.getMessage().toString());
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
            log.error("Exception while executing  Query: " + e.getMessage().toString());
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

                if (mlJson.has("confidence")) {
                  String confidenceScore = mlJson.get("confidence").toString();
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
                  log.error("Exception while executing  Query: " + e.getMessage().toString());
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
              log.error("Exception while executing  Query: " + e.getMessage().toString());
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

  public List<String> collectionTeamIds(String pinkey, String teamId) {

    List<String> response = new ArrayList<>();
    ArangoCursor<String> cursor = null;
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    JSONObject teamDetails = new JSONObject();
    String temId = "Teams/" + teamId;

    teamDetails.put("id", temId);

    String query = "for doc in PinCollection\r\n"
        + "filter doc._key=='" + pinkey + "'\r\n"
        + "UPDATE doc WITH { teamId: push(doc.teamId," + teamDetails
        + ",true) } IN PinCollection";
    logger.info("query--->" + query);
    try {
      cursor = arangoDB.query(query, String.class);
      response = cursor.asListRemaining();
      logger.info("response" + response);
    } catch (Exception e) {
      log.error(
          "Exception while nodesTeamResponsibilities : " + e.getMessage().toString());
    }
    return response;
  }

  public List<HashMap> addSubCollection(String pinkey, HashMap pinDetails) {

    List<String> response1 = new ArrayList<>();
    List<HashMap> response2 = new ArrayList<>();
    List<Object> response3 = new ArrayList<>();
    List<Object> response4 = new ArrayList<>();
    List<HashMap> response5 = new ArrayList<>();
    List<HashMap> response = new ArrayList<>();
    List<Object> pinResponse = new ArrayList<>();
    List<String> columns = new ArrayList<String>();
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    ArangoCollection arangoCollection =
        arangorestclient.getArangoCollection(arangoDB, pincollection);
    ArangoCollection arangoCollection1 =
        arangorestclient.getArangoEdgeCollection(arangoDB, userRoles);
    HashMap document = new HashMap<>();
    Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String datestr = f.format(new Date());
    String displayname = pinDetails.get("displayname").toString();
    String Description = null;
    if (pinDetails.containsKey("Description")) {
      Description = pinDetails.get("Description").toString();
    } else {
      Description = "";
    }
    String cover = null;
    if (pinDetails.containsKey("cover")) {
      cover = pinDetails.get("cover").toString();
    } else {
      cover = "";
    }
    String classification = pinDetails.get("classification").toString();
    String type = pinDetails.get("type").toString();
    String createdby = pinDetails.get("createdby").toString();
    String createdbyId = pinDetails.get("createdbyId").toString();
    // String createdon=pinDetails.get("createdon").toString();
    String lastmodifiedby = pinDetails.get("lastmodifiedby").toString();
    List<String> teamId = (List<String>) pinDetails.get("teamId");

    List<Object> team = new ArrayList<Object>();
    JSONObject teamInfo = new JSONObject();
    for (int i = 0; i < teamId.size(); i++) {
      teamInfo.put("id", "Teams/" + teamId.get(i));
      team.add(teamInfo);
    }
    ArangoCursor<HashMap> cursor = null;
    ArangoCursor<String> cursor2 = null;

    String queryToBeExecuted = "INSERT {displayName:'" + displayname + "',Description:'"
        + Description + "',classification:'" + classification + "',type:'" + type
        + "',createdBy:'" + createdby + "',createdById:'" + createdbyId + "',createdon:'" + datestr
        + "',lastmodifiedby:'"
        + lastmodifiedby + "',lastmodifiedon:'" + datestr + "',pinNodes:[],pinCollection:[]"
        + ",cover:'" + cover + "',teamId:" + team + "} In " + pincollection + "\r\n"
        + "return NEW._id";
    logger.info(queryToBeExecuted);

    try {
      cursor2 = arangoDB.query(queryToBeExecuted, String.class);
      response1 = cursor2.asListRemaining();
      logger.info(String.valueOf(response1));
    } catch (Exception e) {
      log.error("Exception while addSectionTeamForSimple : " + e.getMessage().toString());
    }
    if (pinDetails.containsKey("tags")) {
      List<String> tags = (List<String>) pinDetails.get("tags");
      String Id = response1.get(0);
      for (int i = 0; i < tags.size(); i++) {
        String Tag = tags.get(i);
        String queryToExecute = "INSERT {Tag:'" + Tag + "',_key:'" + Tag + "'} in "
            + tagsCollection + "\r\n" + "return NEW";
        logger.info("queryToBeExecuted----->" + queryToExecute);
        ArangoCursor<HashMap> tagcursor = null;
        try {
          tagcursor = arangoDB.query(queryToExecute, HashMap.class);
          response = cursor.asListRemaining();
        } catch (Exception e) {
          log.error("Exception while addSectionTeamForSimple_2: " + e.getMessage().toString());
        }
        logger.info("response" + response);
        if (response.isEmpty()) {

          // ArangoCursor<HashMap> cursor1 = null;
          // List<HashMap> response2 = new ArrayList<>();
          String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n"
              + "filter doc._key=='" + Tag + "'\r\n" + "INSERT {_from: doc._id, _to: '" + Id
              + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges + "";
          logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
          try {
            cursor = arangoDB.query(queryToBeExecuted1, HashMap.class);
            response2 = cursor.asListRemaining();
          } catch (Exception e) {
            log.error("Exception while addSectionTeamForSimple_3 : " + e.getMessage().toString());
          }

        } else {
          response.forEach(a -> {
            JSONObject s = new JSONObject(a);
            String key = s.getString("_key");
            String tagsl = s.getString("Tag");
            String ids = s.getString("_id");

            ArangoCursor<HashMap> cursor1 = null;
            List<HashMap> responset = new ArrayList<>();

            String queryToBeExecuted1 = "for doc in " + tagsCollection + "\r\n"
                + "filter doc._key=='" + key + "'\r\n" + "INSERT {_from:'" + ids + "', _to: '"
                + Id + "',lastModifiedOn:\"15536766\",createdOn:\"7675657\"} INTO " + tagsEdges
                + "";
            logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
            try {
              cursor1 = arangoDB.query(queryToBeExecuted1, HashMap.class);
              responset = cursor1.asListRemaining();
            } catch (Exception e) {
              log.error("Exception while addSectionTeamForSimple_4 : " + e.getMessage().toString());
            }
          });
        }

        ArangoCursor<Object> cursorPin = null;

        JSONObject pinInfo = new JSONObject();
        pinInfo.put("displayName", displayname);
        pinInfo.put("type", "collection");
        pinInfo.put("arangokey", Id);
        pinInfo.put("cover", cover);
        pinInfo.put("createdon", datestr);
        pinInfo.put("lastmodiedon", datestr);
        String query = "for doc in " + pincollection + "\r\n" + "filter doc._key == '" + pinkey
            + "'\r\n" + "UPDATE doc WITH { pinCollection: push(doc.pinCollection ," + pinInfo
            + ") } IN PinCollection";

        logger.info("query--->" + query);

        try {

          cursorPin = arangoDB.query(query, Object.class);
          pinResponse = cursorPin.asListRemaining();
          logger.info("response" + response);

        } catch (Exception e) {
          log.error(
              "Exception while addPinCollectionFromPinCollections_2 : " + e.getMessage()
                  .toString());
        }

      }
    }
    return response2;
  }


  public String getDataProductTeamDetails(String key) throws ServiceException {

    List<HashMap> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    // HashMap<String, ArrayList<Object>> teamDetails = new HashMap<>();
    JSONObject getTeamDetails = new JSONObject();
    JSONObject teamDetail1 = new JSONObject();
    JSONObject teamDetails = new JSONObject();
    JSONObject teamdetails = new JSONObject();
    List<Object> contextteamDetails = new ArrayList<>();
    List<Object> keycolumns = new ArrayList<>();
    /* String tId = "Teams/" + teamId;*/
    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = "for a in Nodes\r\n"
            + "filter a.id == '" + key + "' AND a.responsibilities !=null\r\n"
            + "for c in a.responsibilities\r\n"
            + "FOR b IN Teams \r\n"
            + "filter b._key == c.teamId\r\n"
            + "return b";

    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    ArangoCursor<HashMap> cursor1 = null;
    ArangoCursor<String> cursor3 = null;
    try {

      cursor1 = arangoDB.query(queryToBeExecuted, HashMap.class);
      response = cursor1.asListRemaining();
      logger.info(String.valueOf(response));

    } catch (Exception e) {
      log.error("Exception while getTeamDetails : " + e.getMessage().toString());
    }

    String team=null;
    String teamId=null;
    for(int i = 0; i < response.size(); i++){
      JSONObject res=new JSONObject(response.get(i));
      team=res.getString("teamStructure");
      teamId=res.getString("_id");
    }
    if (response.isEmpty()) {
      System.out.println("there is no team that integrated with dataproduct");
    } else if(team.equals("matrix")) {
      //String teamId = String.valueOf(response.contains("id"));
      List<String> columns1 = new ArrayList<String>();
      for (int i = 0; i < response.size(); i++) {
        JSONObject s=new JSONObject(response.get(i));
        String teaId=s.getString("_id");
        columns1.add("a._from == '" + teamId + "'");
      }

      String columnIds = String.join(" OR ", columns1);

      String queryToBeExecuted1 =
              "for a in " + userRoles + "\r\n" + "filter " + columnIds + "\r\n" + "return a";
      logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted1, String.class);
        response1 = cursor3.asListRemaining();
        logger.info(String.valueOf(response1));

      } catch (Exception e) {
        log.error("Exception while getteamMDetails : " + e.getMessage().toString());
      }

      List<String> contextName = new ArrayList<>();
      HashSet<String> contextName2 = new HashSet<String>();
      List<String> contextColumns = new ArrayList<>();
      // response4.clear();
      for (int i = 0; i < response1.size(); i++) {

        JSONObject s = new JSONObject(response1.get(i));
        contextName.clear();
        logger.info("s" + s);
        Set<String> keys = s.keySet();
        Object[] namesArray = keys.toArray();
        for (int j = 0; j < namesArray.length; j++) {
          logger.info(j + ": " + namesArray[j]);
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
      logger.info("setWithUniqueValues" + setWithUniqueValues);
      List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
      logger.info("listWithUniqueContextValues" + listWithUniqueContextValues);

      for (int k = 0; k < listWithUniqueContextValues.size(); k++) {
        String contName = listWithUniqueContextValues.get(k);
        String query = "for a in " + userRoles + "\r\n" + "filter " + columns1.get(0) + " AND  a."
                + listWithUniqueContextValues.get(k) + " !=null \r\n" + "return a";
        logger.info("query----->" + query);

        try {

          cursor3 = arangoDB.query(query, String.class);
          response2 = cursor3.asListRemaining();
          logger.info(String.valueOf(response2));

        } catch (Exception e) {
          log.error("Exception while getMatrixteamMDetails1_3 : " + e.getMessage().toString());
        }
        JSONObject teamMDetails = new JSONObject();
        ArrayList<Object> response4 = new ArrayList<>();
       // JSONObject teamDetail1 = new JSONObject();
        JSONObject teamDetail3 = new JSONObject();
        String finalTeamId = teamId;
        response2.forEach(a -> {
          // response4.clear();
          JSONObject contxt = new JSONObject(a);
          logger.info("contxt" + contxt);
          String to = contxt.getString("_to");
          List<String> columns3 = new ArrayList<String>();

          // JSONArray users=contxt.getJSONArray("users");
          String[] Id = to.split("/");
          String nodetypes = Id[0];
          String name = Id[1];
          logger.info("name" + name);
          if (contxt.has("users")) {
            JSONArray users = contxt.getJSONArray("users");
            users.forEach(y -> {
              JSONObject usrId = new JSONObject(y.toString());
              logger.info("usrId" + usrId);
              String ids = usrId.getString("id");
              columns.add("a._key == '" + ids + "'");
              logger.info("columns" + columns);
            });
          }

          JSONArray context = contxt.getJSONArray(contName);
          teamMDetails.put("context", contName);
          context.forEach(z -> {
            JSONObject categoryId = new JSONObject(z.toString());
            String cids = categoryId.getString("id");
            columns3.add("a._key == '" + cids + "'");
          });

          ArangoCursor<HashMap> cursor2 = null;
          List<HashMap> response3 = new ArrayList<>();
          String columnIds1 = String.join(" OR ", columns);
          String queryToBeExecuted3 = "for a in " + userRegistration + "\r\n" + "filter "
                  + columnIds1 + "\r\n" + "return {" + name + ":a.FirstName,id:a._key}";

          logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
          try {

            cursor2 = arangoDB.query(queryToBeExecuted3, HashMap.class);
            response3 = cursor2.asListRemaining();
            logger.info(String.valueOf(response3));

          } catch (Exception e) {
            log.error("Exception while getMatrixteamMDetails1_4 : " + e.getMessage().toString());
          }

          columns.clear();

          JSONObject teamDetail2 = new JSONObject();
          // teamDetail1.put("id", to);
          // teamDetail1.put("name", name);
          for (int l = 0; l < response3.size(); l++) {

            JSONObject roleuserObject = new JSONObject();
            JSONObject tuser = new JSONObject(response3.get(l));
            logger.info("tuser" + tuser);
            roleuserObject.put("name", tuser.getString(name));
            roleuserObject.put("id", tuser.getString("id"));
            // Collection k = teamDetail.values();
            keycolumns.add(roleuserObject);


          }

          teamDetail3.put(name, keycolumns);
          keycolumns.clear();
          logger.info("teamDetail1" + teamDetail1);
          response4.add(teamDetail3.toMap());
          teamMDetails.put("roles", response4);
          response4.clear();
          List<Object> Business = new ArrayList<>();
          List<Object> DataDomain = new ArrayList<>();
          List<Object> Geography = new ArrayList<>();
          List<Object> Products = new ArrayList<>();
          List<HashMap> response5 = new ArrayList<>();
          ArangoCursor<HashMap> cursor4 = null;
          String columnIds3 = String.join(" OR ", columns3);
          String queryToBeExecuted4 = "for a in Business\r\n" + "filter " + columnIds3
                  + "\r\n" + "return {name:a.name,type:a.typeName,id:a._key}";

          logger.info("queryToBeExecuted----->" + queryToBeExecuted4);

          try {

            cursor4 = arangoDB.query(queryToBeExecuted4, HashMap.class);
            response5 = cursor4.asListRemaining();
            logger.info(String.valueOf(response5));

          } catch (Exception e) {
            log.error("Exception while getMatrixteamMDetails1_5: " + e.getMessage().toString());
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
            String id = cate.getString("id");
            if (type.equals("Data Domain")) {
              categoryObject.put("name", cname);
              categoryObject.put("key", id);
              DataDomain.add(categoryObject);
            } else if (type.equals("Product")) {
              categoryObject.put("name", cname);
              categoryObject.put("key", id);
              Products.add(categoryObject);
            } else if (type.equals("Region")) {
              categoryObject.put("name", cname);
              categoryObject.put("key", id);
              Geography.add(categoryObject);
            } else if (type.equals("Line of Business")) {
              categoryObject.put("name", cname);
              categoryObject.put("key", id);
              Business.add(categoryObject);
            }
          });
          if (!DataDomain.isEmpty()) {
            teamMDetails.put("dataDomain", DataDomain);
          }
          if (!Products.isEmpty()) {
            teamMDetails.put("products", Products);
          }
          if (!Geography.isEmpty()) {
            teamMDetails.put("geography", Geography);
          }
          if (!Business.isEmpty()) {
            teamMDetails.put("business", Business);
          }
          teamMDetails.put("teamId", finalTeamId);
        });

        contextteamDetails.add(teamMDetails);

      }

    }else if(team.equals("simple"))
    {


      List<String> column = new ArrayList<String>();
      for (int i = 0; i < response.size(); i++) {
        column.add("a._from == '" + teamId + "'");
      }

      String columnIds = String.join(" OR ", column);
      String queryToBeExecuted1 =
              "for a in " + userRoles + "\r\n" + "filter " + columnIds + "\r\n" + "return a";
      logger.info("queryToBeExecuted1----->" + queryToBeExecuted1);

      try {

        cursor3 = arangoDB.query(queryToBeExecuted1, String.class);
        response1 = cursor3.asListRemaining();
        logger.info(String.valueOf(response1));

      } catch (Exception e) {
        log.error("Exception while getTeamDetails : " + e.getMessage().toString());
      }

      List<String> columns2 = new ArrayList<String>();
      JSONObject teams = new JSONObject();
      teams.put("teamId", teamId);
      for (int i = 0; i < response1.size(); i++) {
        JSONObject s = new JSONObject(response1.get(i));
        String id = s.getString("_to");
        String[] Id = id.split("/");
        String nodetypes = Id[0];
        String name = Id[1];
        logger.info("name" + name);

        if (s.has("users")) {
          JSONArray s1 = new JSONArray();
          s1 = s.getJSONArray("users");
          s1.forEach(y -> {
            JSONObject usrId = new JSONObject(y.toString());
            logger.info("usrId" + usrId);
            String ids = usrId.getString("id");
            columns2.add("a._key == '" + ids + "'");
            logger.info("columns" + columns2);
          });
        }

        List<String> response3 = new ArrayList<>();
        ArangoCursor<String> cursor2 = null;
        String columnIds2 = String.join(" OR ", columns2);
        String queryToBeExecuted3 = "for a in " + userRegistration + "\r\n" + "filter " + columnIds2
                + "\r\n" + "return {" + name + ":a.FirstName,id:a._key}";

        logger.info("queryToBeExecuted----->" + queryToBeExecuted3);
        // ArangoCursor<Object> cursor = null;
        try {

          cursor2 = arangoDB.query(queryToBeExecuted3, String.class);
          response3 = cursor2.asListRemaining();
          logger.info(String.valueOf(response3));

        } catch (Exception e) {
          log.error("Exception while getTeamDetails_2: " + e.getMessage().toString());
        }
        columns2.clear();

        // teamDetail1.put("id", id);
        // teamDetail1.put("role", name);

        for (int l = 0; l < response3.size(); l++) {

          JSONObject roleuserObject = new JSONObject();
          JSONObject tuser = new JSONObject(response3.get(l));
          logger.info("tuser" + tuser);
          roleuserObject.put("name", tuser.getString(name));
          roleuserObject.put("id", tuser.getString("id"));
          // Collection k = teamDetail.values();
          keycolumns.add(roleuserObject);


        }
        teamDetails.put(name, keycolumns);
        keycolumns.clear();

        // response4.add(teamDetail1.toMap());
        response1.forEach(b -> {
          JSONObject x = new JSONObject(b);
          if (x.has("users")) {
            // JSONArray users=x.getJSONArray("users");
            // response4.add(teamDetail3.toMap());

            teamDetail1.put("roles", teamDetails);
          } else {
            teamDetail1.put("roles", teamdetails);
          }
        });
        teamDetail1.put("teamId", teamId);

      }
      contextteamDetails.add(teamDetail1);
    }
    return contextteamDetails.toString();



  }

  public List<Object> removeProfileContext(String profileId, String context)
      throws ServiceException {
    List<Object> response = new ArrayList<>();
    List<String> response1 = new ArrayList<>();
    List<String> response2 = new ArrayList<>();

    String from = "registerUsers/" + profileId;

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String query =
        "for a in profileCategories\r\n" + "filter a._from == '" + from + "'\r\n" + "return a";
    logger.info("query----->" + query);
    ArangoCursor<String> cursor1 = null;
    try {

      cursor1 = arangoDB.query(query, String.class);
      response1 = cursor1.asListRemaining();
      logger.info(String.valueOf(response1));

    } catch (Exception e) {
      log.error("Exception while removeContext : " + e.getMessage().toString());
    }

    List<String> contextName = new ArrayList<>();
    HashSet<String> contextName2 = new HashSet<String>();
    HashSet<Integer> contextName3 = new HashSet<Integer>();
    List<String> contextColumns = new ArrayList<>();
    // response4.clear();
    for (int i = 0; i < response1.size(); i++) {
      JSONObject s = new JSONObject(response1.get(i));
      contextName.clear();
      logger.info("s" + s);
      Set<String> keys = s.keySet();
      Object[] namesArray = keys.toArray();
      for (int j = 0; j < namesArray.length; j++) {
        logger.info(j + ": " + namesArray[j]);
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
    logger.info("setWithUniqueValues" + setWithUniqueValues);
    List<String> listWithUniqueContextValues = new ArrayList<>(setWithUniqueValues);
    logger.info("listWithUniqueContextValues" + listWithUniqueContextValues);

    int count = listWithUniqueContextValues.size();
    if (count == 1) {
      String queryToBeExecuted = "for a in profileCategories\r\n" + "filter a._from == '" + from
          + "' AND a." + context + "!=null\r\n" + "remove a._key in profileCategories";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      ArangoCursor<Object> cursor = null;
      try {

        cursor = arangoDB.query(queryToBeExecuted, Object.class);
        response = cursor.asListRemaining();
        logger.info(String.valueOf(response));

      } catch (Exception e) {
        log.error("Exception while removeContext_2 : " + e.getMessage().toString());
      }

      String queryToBeExecuted1 = "for a in " + Teams + "\r\n" + "filter a._key == '" + profileId
          + "'\r\n" + "remove a._key in " + Teams + "";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted1);

      ArangoCursor<String> cursor2 = null;
      try {

        cursor2 = arangoDB.query(queryToBeExecuted1, String.class);
        response2 = cursor2.asListRemaining();
        logger.info(String.valueOf(response2));

      } catch (Exception e) {
        log.error("Exception while removeContext_3 : " + e.getMessage().toString());
      }
    } else {

      String queryToBeExecuted = "for a in profileCategories\r\n" + "filter a._from == '" + from
          + "' AND a." + context + "!=null\r\n" + "remove a._key in profileCategories";

      logger.info("queryToBeExecuted----->" + queryToBeExecuted);

      ArangoCursor<Object> cursor = null;
      try {

        cursor = arangoDB.query(queryToBeExecuted, Object.class);
        response = cursor.asListRemaining();
        logger.info(String.valueOf(response));

      } catch (Exception e) {
        log.error("Exception while removeContext_4 : " + e.getMessage().toString());
      }
    }

    return response;
  }

  public List<Object> getMyCollectionKeys(String createdbyId, String order, String pinFilter) {

    List<String> response = new ArrayList<String>();
    final List<String>[] response1 = new List[]{new ArrayList<String>()};
    List<String> governed = new ArrayList<String>();
    List<String> userRoles = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }

    String queryToBeExecuted = null;


    /*
     * String queryToBeExecuted="for node in "+ pincollection +"\r\n" +"filter node.createdBy=='"+
     * createdby +"'\r\n" + "return node";
     */
    if (pinFilter.contains("lastmodifiedon")) {

      queryToBeExecuted = "for a in PinCollection\r\n" + "filter a.createdById=='" + createdbyId
          + "' AND a.classification == \"public\" OR a.classification == \"System\"\r\n"
          + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
          + "return a";


    } else if (pinFilter.contains("createdon")) {

      queryToBeExecuted = "for a in PinCollection\r\n" + "filter a.createdById=='" + createdbyId
          + "'AND a.classification == \"public\" OR a.classification == \"System\"\r\n" + "SORT a."
          + pinFilter + " " + order + "\r\n" // ASC/DESC
          + "return a";

    } else {

      queryToBeExecuted = "for a in PinCollection\r\n" + "filter a.createdById=='" + createdbyId
          + "' AND a.classification == \"public\" OR a.classification == \"System\"\r\n" + "SORT a."
          + pinFilter + " " + order + "\r\n" // ASC/DESC
          + "return a";

    }
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionHeaderskeys : " + e.getMessage().toString());
    }

//    String query="for a in PinCollection\n" +
//            "filter a._key == '"+52957556+"'\n" +
//            "for x in a.pinNodes[*]\n" +
//            "for b in PhysicalDataDictionary\n" +
//            "filter b.displayName == x.displayName\n" +
//            "for c in mllnkphy\n" +
//            "filter c._to == b._id\n" +
//            "return c";
//    logger.info("query----->" + query);
//
//    try {
//
//      cursor = arangoDB.query(query, String.class);
//      response1 = cursor.asListRemaining();
//
//    } catch (Exception e) {
//      log.error("Exception while getPinCollectionHeaderskeys : " + e.getMessage().toString());
//    }

   // List<String> finalResponse = response1;
    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayName"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      if (nodes.has("cover")) {
        pinHeaders.put("cover", nodes.getString("cover"));
      }
      String key = nodes.getString("_key");
      pinHeaders.put("key", nodes.getString("_key"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("createdon", nodes.getString("createdon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      response1[0] = getCurateNodes(key);
      System.out.println("response1[0]"+response1[0]);
      if(!response1[0].isEmpty()){
        pinHeaders.put("curate", true);
      }else{
        pinHeaders.put("curate", false);
      }
      pinHeaderInfo.add(pinHeaders.toMap());
    });
    return pinHeaderInfo;
  }

  public List<String> getCurateNodes(String key) {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    List<String> response1 = new ArrayList<String>();

    String query="for a in PinCollection\n" +
            "filter a._key == '"+key+"'\n" +
            "for x in a.pinNodes[*]\n" +
            "for b in PhysicalDataDictionary\n" +
            "filter b.displayName == x.displayName\n" +
            "for c in mllnkphy\n" +
            "filter c._to == b._id\n" +
            "return c";
    logger.info("query----->" + query);
    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(query, String.class);
      response1 = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionHeaderskeys : " + e.getMessage().toString());
    }
    return response1;
  }

  public List<Object> getCreatedByCollections(String createdbyId, String order, String pinFilter) {

    List<String> response = new ArrayList<String>();
    final List<String>[] response1 = new List[]{new ArrayList<String>()};
    List<String> governed = new ArrayList<String>();
    List<String> userRoles = new ArrayList<String>();
    JSONObject pinHeaders = new JSONObject();
    List<Object> pinHeaderInfo = new ArrayList<>();

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    List<String> columns = new ArrayList<>();
    String query = "for node in PinCollection\n"
        + "filter node.classification==\"governed\" AND node.teamId \n"
        + "for n in node.teamId\n"
        + "filter n.id\n"
        + "let usrRoles=(for b in userRoles\n"
        + "filter b._from == n.id\n"
        + "for c in b.users\n"
        + "return c.id\n"
        + ")\n"
        + "for s in usrRoles\n"
        + "filter s == '" + createdbyId + "'\n"
        + "return node._key";
    ArangoCursor<String> cursor2 = null;
    try {

      cursor2 = arangoDB.query(query, String.class);
      governed = cursor2.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionHeaderskeys : " + e.getMessage().toString());
    }
    System.out.println("query" + query);
    for (int i = 0; i < governed.size(); i++) {
      //JSONObject s=new JSONObject(governed.get(i));
      columns.add("a._key =='" + governed.get(i) + "'");
    }

    String userColumnIds = String.join(" OR ", columns);

    String queryToBeExecuted = null;


    /*
     * String queryToBeExecuted="for node in "+ pincollection +"\r\n" +"filter node.createdBy=='"+
     * createdby +"'\r\n" + "return node";
     */
    System.out.println("userColumnIds-->" + userColumnIds);
    if (pinFilter.contains("lastmodifiedon")) {
      if (!userColumnIds.isEmpty()) {
        queryToBeExecuted = "for a in PinCollection\r\n"
            + "filter " + userColumnIds + " OR (a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\") OR (a.createdById=='" + createdbyId
            + "' AND a.classification ==\"public\")\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      } else {

        queryToBeExecuted = "for a in PinCollection\r\n"
            + "filter (a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\") OR (a.createdById=='" + createdbyId
            + "' AND a.classification ==\"public\")\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      }

    } else if (pinFilter.contains("createdon")) {
      if (!userColumnIds.isEmpty()) {
        queryToBeExecuted = "for a in PinCollection\r\n"
            + "filter " + userColumnIds + " OR (a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\") OR (a.createdById=='" + createdbyId
            + "' AND a.classification ==\"public\")\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      } else {
        queryToBeExecuted = "for a in PinCollection\r\n"
            + "filter (a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\") OR (a.createdById=='" + createdbyId
            + "' AND a.classification ==\"public\")\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      }
    } else {
      if (!userColumnIds.isEmpty()) {
        queryToBeExecuted = "for a in PinCollection\r\n"
            + "filter " + userColumnIds + " OR (a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\") OR (a.createdById=='" + createdbyId
            + "' AND a.classification ==\"public\")\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      } else {
        queryToBeExecuted = "for a in PinCollection\r\n"
            + "filter (a.createdById=='" + createdbyId
            + "' AND a.classification == \"private\") OR (a.createdById=='" + createdbyId
            + "' AND a.classification ==\"public\")\r\n"
            + "SORT a." + pinFilter + " " + order + "\r\n" // ASC/DESC
            + "return a";
      }
    }
    logger.info("queryToBeExecuted----->" + queryToBeExecuted);

    ArangoCursor<String> cursor = null;
    try {

      cursor = arangoDB.query(queryToBeExecuted, String.class);
      response = cursor.asListRemaining();

    } catch (Exception e) {
      log.error("Exception while getPinCollectionHeaderskeys : " + e.getMessage().toString());
    }

    response.forEach(action -> {
      JSONObject nodes = new JSONObject(action);
      logger.info(String.valueOf(nodes));
      JSONArray pinNodes = new JSONArray();
      JSONArray pinCollection = new JSONArray();
      pinNodes = nodes.getJSONArray("pinNodes");
      pinCollection = nodes.getJSONArray("pinCollection");
      int counters = 0;
      for (int i = 1; i <= pinNodes.length(); i++) {
        counters = counters + 1;
      }
      logger.info(String.valueOf(counters));
      int counters1 = 0;
      for (int i = 1; i <= pinCollection.length(); i++) {
        counters1 = counters1 + 1;
      }
      logger.info(String.valueOf(counters1));
      // String displayname=nodes.get("displayName").toString();
      pinHeaders.put("displayName", nodes.getString("displayName"));
      // pinHeaders.put("Description", nodes.getString("Description"));
      if (nodes.has("cover")) {
        pinHeaders.put("cover", nodes.getString("cover"));
      }
      pinHeaders.put("key", nodes.getString("_key"));
      pinHeaders.put("classification", nodes.getString("classification"));
      pinHeaders.put("lastmodifiedon", nodes.getString("lastmodifiedon"));
      pinHeaders.put("createdon", nodes.getString("createdon"));
      pinHeaders.put("numberofpins", (counters + counters1));
      String key=nodes.getString("_key");
      response1[0] = getCurateNodes(key);

      if(!response1[0].isEmpty()){
        pinHeaders.put("curate", true);
      }else{
        pinHeaders.put("curate", false);
      }
      pinHeaderInfo.add(pinHeaders.toMap());
    });

    return pinHeaderInfo;

  }

  public List<HashMap> nodePrivacyRisk(String nodekey, String type) {

    ArangoDatabase arangoDB = this.arangoDB.getConnection();

    if (arangoDB == null) {
      throw new RuntimeException("Database connection is unavailable");
    }
    List<Node> nodesinfo=new ArrayList<>();
    HashMap<String, Set<String>> nodePrivacyRisk = new HashMap<>();
    HashMap<String, ArrayList<Object>> nodesPrivacy = new HashMap<>();
    List<HashMap> response=new ArrayList<>();
    if(type.equals("Database")) {
      String filterWithId =
              " FILTER node.id=='" + nodekey + "'";
      List<Node> nodes = repository.getNodes(filterWithId);
      List<String> databasetargetNames = buildRelation.getTargetNodes(nodes, "Schema");
      List<Node> databaseNodeInfo = nodeSearchMapperImpl.getNodes(databasetargetNames);
      List<String> schematargetNames = buildRelation.getTargetNodes(databaseNodeInfo, "Table");
      List<Node> schemaNodeInfo = nodeSearchMapperImpl.getNodes(schematargetNames);
      List<String> tabletargetNames = buildRelation.getTargetNodes(schemaNodeInfo, "Column");
      List<Node> tableNodeInfo = nodeSearchMapperImpl.getNodes(tabletargetNames);
      List<String> columntargetNames = buildRelation.getTargetNodes(tableNodeInfo, "SensitiveType");
      nodesinfo = nodeSearchMapperImpl.getNodes(columntargetNames);
    }else if(type.equals("Schema")){
      String filterWithId =
              " FILTER node.id=='" + nodekey + "'";
      List<Node> nodes = repository.getNodes(filterWithId);
      List<String> schematargetNames = buildRelation.getTargetNodes(nodes, "Table");
      List<Node> schemaNodeInfo = nodeSearchMapperImpl.getNodes(schematargetNames);
      List<String> tabletargetNames = buildRelation.getTargetNodes(schemaNodeInfo, "Column");
      List<Node> tableNodeInfo = nodeSearchMapperImpl.getNodes(tabletargetNames);
      List<String> columntargetNames = buildRelation.getTargetNodes(tableNodeInfo, "SensitiveType");
      nodesinfo = nodeSearchMapperImpl.getNodes(columntargetNames);
    }else if(type.equals("Table")){
      String filterWithId =
              " FILTER node.id=='" + nodekey + "'";
      List<Node> nodes = repository.getNodes(filterWithId);
      List<String> tabletargetNames = buildRelation.getTargetNodes(nodes, "Column");
      List<Node> tableNodeInfo = nodeSearchMapperImpl.getNodes(tabletargetNames);
      List<String> columntargetNames = buildRelation.getTargetNodes(tableNodeInfo, "SensitiveType");
      nodesinfo = nodeSearchMapperImpl.getNodes(columntargetNames);
    }else if(type.equals("Column")){
      String filterWithId =
              " FILTER node.id=='" + nodekey + "'";
      List<Node> nodes = repository.getNodes(filterWithId);
      List<String> columntargetNames = buildRelation.getTargetNodes(nodes, "SensitiveType");
      nodesinfo = nodeSearchMapperImpl.getNodes(columntargetNames);
    }

    List<String> keycolumns = new ArrayList<String>();

    Set<String> keycolumns1 = new HashSet<String>();

    ArrayList<Object> nodesList = new ArrayList<>();
    ArrayList<Object> nodesList1 = new ArrayList<>();
   nodesinfo.forEach(a->{

     JSONObject nodesinfo1 = new JSONObject();

     JSONObject nodes = new JSONObject(a);
     logger.info("nodes" + nodes);

     //getting asset fullName
     String fullName = nodes.getString("identifier");

     nodesinfo1.put("fullName", fullName);

     //getting asset displayName
     String nodeDisplayname = nodes.getString("displayName");
     nodesinfo1.put("displayName", nodeDisplayname);
     nodesinfo1.put("title", nodeDisplayname);


     //getting id
     String Id = nodes.getString("id");
     nodesinfo1.put("id", Id);

    // String key = nodes.getString("_key");
     nodesinfo1.put("key", Id);

     if (nodes.has("createdOn")) {
       String createdOn = nodes.getString("createdOn");
       nodesinfo1.put("createdOn", createdOn);
     }
     if (nodes.has("subType")) {
       String subType = nodes.getString("subType");

       nodesinfo1.put("subType", subType);
     }
     if (nodes.has("createdByUserName")) {
       String createdBy = nodes.getString("createdByUserName");
       nodesinfo1.put("createdBy", createdBy);
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
         nodesinfo1.put("communityName", communityName);
       }
     }

     if (nodes.has("domain")) {
       JSONObject domain = nodes.getJSONObject("domain");
       if (domain.has("name")) {
         String domainName = domain.getString("name");
         nodesinfo1.put("domainName", domainName);
       }
     }

     JSONObject nodeType = nodes.getJSONObject("type");
     if (nodeType.has("metaCollectionName")) {
       String name = nodeType.getString("metaCollectionName");
       nodesinfo1.put("metaCollectionName", name);
     } else {
       nodesinfo1.put("metaCollectionName", "null");
     }

     String name = null;
     if (nodeType.has("name")) {
       name = nodeType.getString("name");
       nodesinfo1.put("metaCollectionTypeName", name);
     } else {
       nodesinfo1.put("metaCollectionTypeName", "null");
     }
     if (nodes.has("status")) {
       JSONObject nodestatus = nodes.getJSONObject("status");
       String statusName = nodestatus.getString("name");
       nodesinfo1.put("status", statusName);
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


     JSONArray attributeInfo = nodes.getJSONArray("attributes");
     attributeInfo.forEach(eachAttribute -> {
       JSONObject attributes = new JSONObject(eachAttribute.toString());
       logger.info("attributes" + attributes);
       if (!attributes.isEmpty()) {

         String attributesname = attributes.get("name").toString();
         String attributesvalue = null;
         if (attributes.has("value")) {

            String attributesvalue1 = attributes.get("value").toString();
             if(attributesvalue1.contains("{}")){
               attributesvalue = null;
             }else{
               attributesvalue = attributesvalue1;
             }

         } else {
           attributesvalue = "null";
         }

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

           nodesinfo1.put("description", value);
         }

       }

     });

     Set<String> k = nodesinfo1.keySet();
     keycolumns1.addAll(k);
     logger.info("keys" + k);
     nodesList.add(nodesinfo1.toMap());
   });
    nodePrivacyRisk.put("NodeKeys",keycolumns1);
    nodesPrivacy.put("DataInfo",nodesList);
    response.add(nodePrivacyRisk);
    response.add(nodesPrivacy);
    return response;
  }

  public List<Object> classificationNodes(HashMap columnsList) {

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
    List<String> columnkeys = (List<String>) columnsList.get("columns");


    List<String> columns = new ArrayList<>();
    for (int i = 0; i < columnkeys.size(); i++) {
     // String[] node = ((String) columnkeys.get(i)).split("/");
      columns.add("b.id == '" + columnkeys.get(i) + "'");
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
     // JSONObject physicalJson = new JSONObject(response1.get(i));


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

            logger.info("queryToBeExecuted----->" + querytoexecute);

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
}

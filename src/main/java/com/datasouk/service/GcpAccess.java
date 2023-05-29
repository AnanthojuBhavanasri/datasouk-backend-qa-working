package com.datasouk.service;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.datasouk.service.arango.connect.ArangoRestClient;
import com.datasouk.service.arango.connect.ConnectArango;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GcpAccess {

  private static Logger logger = LoggerFactory.getLogger(GcpAccess.class);

  @Value("${gcp.url}")
  private String gcpUrl;

  @Value("${arango.database}")
  private String arangodatabase;

  @Autowired
  private AQL aql;

  @Autowired
  private ArangoRestClient arangorestclient;
  @Autowired
  private ConnectArango connectArango;

  @Value("${arango.gcpDataUsage.collection}")
  private String GcpDataUsage;


  public void getAccessOnGcp(HashMap details) {

    List<Object> requetForList = new ArrayList<>();
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);

      if (arangodb != null) {
        ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangodb,
            GcpDataUsage);
        List<HashMap> Requestedfor = (List<HashMap>) details.get("Requestedfor");
        String requesterMailId = details.get("RequesterMailId").toString();
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

        logger.info("requetForList" + requetForList);
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
        String EndDate = details.get("EndDate").toString();
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

        List<String> tableIdsList = aql.gcpNodeId(orderIds);
        JSONObject reqBody = new JSONObject();
        JSONObject accessRequest = new JSONObject();
        JSONArray tableIds = new JSONArray();

        accessRequest.put("roleId", "roles/bigquery.dataViewer");
        accessRequest.put("accessRequestPrincipal", "user:" + requesterMailId);
        //tableIds.put(0, "data-governance-arena.emloyee_kyc.emp_record_1");
        //accessRequest.put("tableIds", tableIds);
        accessRequest.put("tableIds", tableIdsList);
        reqBody.put("accessRequest", accessRequest);

        //String StartDate=details.get("StartDate").toString();
        //String EndDate=details.get("EndDate").toString();
        reqBody.put("effectiveStartDate", StartDate);
        reqBody.put("effectiveEndDate", EndDate);
        logger.info("requestBody for GCP Access :{}", reqBody);

        List<Object> response1 = new ArrayList<>();
        ArangoCursor<Object> cursor = null;
//		String query="INSERT "+ reqBody  +" In "+ GcpDataUsage +"\r\n"
//				+ "return NEW._key";
        String query =
            "INSERT {requesterNames:" + requetForList + ",Purpose:'" + Purpose + "',StartDate:'"
                + StartDate + "',EndDate:'" + EndDate + "',deliveryPreferences:'"
                + DeliveryPreferences + "',deliveryPlatform:'" + DeliveryPlatform
                + "',dataRefreshFrequency:'" + DataRefreshFrequency + "',priority:'" + Priority
                + "',requesterMailID:'" + RequesterMailId + "',callerName:'" + CallerName
                + "',Type:'" + Type + "',createdOn:'" + datestr + "',createdBy:'" + createdBy
                + "',PrivacyPolicy:'" + PrivacyPolicy + "',orderIds:" + columns2 + ",orderUrls:"
                + columns3 + ",accessRequest:" + accessRequest + "} In " + GcpDataUsage + "\r\n"
                + "return NEW._key";
        logger.info("queryToBeExecuted----->" + query);
        try {

          cursor = arangodb.query(query, Object.class);
          response1 = cursor.asListRemaining();
          logger.info("queryToBeExecuted----->" + response1);

        } catch (Exception e) {
          logger.error("Exception while executing  Query: " + e.getMessage().toString());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(reqBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(gcpUrl, HttpMethod.POST, entity,
            String.class);
        int statuscode = response.getStatusCodeValue();
        if (statuscode == 201 || statuscode == 200) {
          logger.info("Access Granted successfully");
        } else {
          ResponseEntity.status(statuscode)
              .body("Failed !! ServicenowClientFactory Due to " + response.getBody());
        }

      }
    }
  }
}

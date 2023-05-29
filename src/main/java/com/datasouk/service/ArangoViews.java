package com.datasouk.service;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.arangosearch.ArangoSearchPropertiesEntity;
import com.arangodb.entity.arangosearch.FieldLink;
import com.datasouk.service.arango.connect.ArangoRestClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ArangoViews {

  private static Logger logger = LoggerFactory.getLogger(ArangoViews.class);
  Logger log = LoggerFactory.getLogger(DGC2ArangoDB.class);
  @Autowired
  private ArangoRestClient arangorestclient;
  @Value("${arango.database}")
  private String arangodatabase;
  @Value("${arango.viewName}")
  private String viewName;

  public List<String> getArangoViewProperties() {
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {

      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);
      List<String> searchByValues = new ArrayList<String>();
      ArangoSearchPropertiesEntity searchView = arangodb.arangoSearch(viewName).getProperties();

      searchView.getLinks().forEach(eachCollection -> {
        Collection<FieldLink> fields = eachCollection.getFields();
        fields.forEach(eachField -> {
          searchByValues.add(eachField.getName());
        });
        logger.info("--->" + eachCollection.getFields());
      });

      logger.info("searchByValues---------->" + searchByValues);
      return searchByValues;
    }
    return null;
  }

}

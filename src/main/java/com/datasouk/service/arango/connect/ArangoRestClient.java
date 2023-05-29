package com.datasouk.service.arango.connect;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoSearch;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;
import com.datasouk.core.exception.ServiceException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ArangoRestClient {

  private static Logger logger = LoggerFactory.getLogger(ArangoRestClient.class);
  Logger log = LoggerFactory.getLogger(ArangoRestClient.class);
  @Value("${arango.port}")
  private int arangoPort;
  @Value("${arango.host}")
  private String arangohost;
  @Value("${arango.user}")
  private String arangouser;
  @Value("${arango.password}")
  private String arangopwd;

  public ArangoDB getArangoConnection() throws ServiceException {

    ArangoDB arangoDB = null;
    try {
      arangoDB = new ArangoDB.Builder().user(arangouser).password(arangopwd)
          .host(arangohost, arangoPort).build();
    } catch (Exception e) {
      log.error("Exception while connecting to Arango DB: " + e.getMessage().toString());
    }

    return arangoDB;
  }

  public ArangoDatabase getArangoDBConnection(ArangoDB arango, String dbName)
      throws ServiceException {

    ArangoDatabase arangodb = null;

    Collection<String> arangodbs = arango.getDatabases();

    if (arangodbs.contains(dbName)) {
      arangodb = arango.db(dbName);

    } else {

      Boolean createdb = arango.createDatabase(dbName);
      if (createdb) {
        arangodb = arango.db(dbName);

      } else {
        log.error("Exception while creating database: " + dbName);
      }
    }
    return arangodb;
  }

  public ArangoCollection getArangoCollection(ArangoDatabase arangodb, String collectionName)
      throws ServiceException {

    ArangoCollection arangoCollection = null;

    boolean createCollection = true;

    try {

      arangodb.createCollection(collectionName);

      arangoCollection = arangodb.collection(collectionName);


    } catch (Exception e) {
      log.info("Collection already exists with the name: " + collectionName);
      createCollection = false;
    }
    logger.info("collectionFound--->" + createCollection);
    if (!createCollection) {
      logger.info("collectionFound--->" + createCollection);
      arangoCollection = arangodb.collection(collectionName);
    }
    return arangoCollection;

  }

  public ArangoCollection getArangoEdgeCollection(ArangoDatabase arangodb, String collectionName)
      throws ServiceException {

    ArangoCollection arangoCollection = null;

    Collection<CollectionEntity> arangoCollections = arangodb.getCollections();

    boolean createCollection = true;

    try {
      arangodb.createCollection(collectionName,
          new CollectionCreateOptions().type(CollectionType.EDGES));
      arangoCollection = arangodb.collection(collectionName);
    } catch (Exception e) {
      log.info("Edge Collection already exists with the name: " + collectionName);
      createCollection = false;
    }
    if (!createCollection) {

      arangoCollection = arangodb.collection(collectionName);
    }
    return arangoCollection;

  }

  public ArangoSearch getArangoView(ArangoDatabase arangodb, String viewName)
      throws ServiceException {

    ArangoSearch arangoView = null;
    boolean createView = true;
    try {
      arangodb.createView(viewName, null);
    } catch (Exception e) {
      log.info("View already exists with the name: " + viewName);
      createView = false;
    }
    if (!createView) {
      arangoView = arangodb.arangoSearch(viewName);
    }
    return arangoView;
  }
}

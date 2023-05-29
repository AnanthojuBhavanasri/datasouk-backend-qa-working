package com.datasouk.service;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.datasouk.core.exception.UnAuthorizedException;
import com.datasouk.service.arango.connect.ArangoRestClient;
import com.datasouk.service.arango.connect.ConnectArango;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserRegistration {

  private static Logger logger = LoggerFactory.getLogger(UserRegistration.class);
  Logger log = LoggerFactory.getLogger(UserRegistration.class);
  @Autowired
  private ArangoRestClient arangorestclient;
  @Autowired
  private ConnectArango connectArango;
  @Value("${arango.database}")
  private String arangodatabase;
  @Value("${arango.viewName}")
  private String viewName;
  @Value("${arango.userRegistration.collection}")
  private String userRegistration;

//	@Bean
//	public PasswordEncoder encoder() {
//	    return new BCryptPasswordEncoder();
//	}

  public HashMap<String, String> usersRegistration1(Map<String, String> registerUserDetails)
      throws IOException {

    String response1 = null;
    List<Object> response = new ArrayList<>();
    List<String> response2 = new ArrayList<>();
    HashMap<String, String> document1 = new HashMap<>();
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);

      if (arangodb != null) {
        ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangodb,
            userRegistration);

        String password = registerUserDetails.get("password").toString();
        String encryptedpassword = null;
        try {
          MessageDigest m = MessageDigest.getInstance("MD5");
          m.update(password.getBytes());
          byte[] bytes = m.digest();
          StringBuilder s = new StringBuilder();
          for (int i = 0; i < bytes.length; i++) {
            s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
          }
          encryptedpassword = s.toString();
        } catch (Exception e) {
          e.printStackTrace();
        }

        /* Display the unencrypted and encrypted passwords. */
        logger.info("Plain-text password: " + password);
        logger.info("Encrypted password using MD5: " + encryptedpassword);

        HashMap<String, String> document = new HashMap<>();

        Date date = new Date();
        String firstName = registerUserDetails.get("firstName").toString();
        String lastName = registerUserDetails.get("lastName").toString();
        String email = registerUserDetails.get("email");
        document.put("FirstName", firstName);
        document.put("LastName", lastName);
        document.put("Email", email);

        document.put("Password", encryptedpassword);
        //document.put("Image", file.toString());
        document.put("CreatedOn", date.toString());
        document.put("LastModifiedOn", date.toString());
        document.put("Identifier", email);
        logger.info(String.valueOf(document));

        String queryToBeExecuted = "for user in registerUsers\r\n"
            + "filter user.Email == '" + email + "'\r\n"
            + "return user.Email";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted);
        ArangoCursor<Object> cursor = null;
        try {

          cursor = arangodb.query(queryToBeExecuted, Object.class);
          response = cursor.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while executing  Query: " + e.getMessage().toString());
        }

        if (!response.isEmpty()) {
          throw new UnAuthorizedException(queryToBeExecuted, null);
        } else {

          try {
            arangoCollection.insertDocument(document);
            logger.info("document" + document);
            logger.info("Search Value Document Created");
          } catch (ArangoDBException e) {
            log.error(
                "Exception while executing StoreSearches  Query: " + e.getMessage().toString());
          }
        }

        String queryToBeExecuted1 = "for user in registerUsers\r\n"
            + "filter user.Email == '" + email + "'\r\n"
            + "return user";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted1);
        ArangoCursor<String> cursor1 = null;
        try {

          cursor1 = arangodb.query(queryToBeExecuted1, String.class);
          response2 = cursor1.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while executing  Query: " + e.getMessage().toString());
        }
        response2.forEach(res -> {
          JSONObject regRes = new JSONObject(res);
          logger.info("regRes" + regRes);
          document1.put("email", regRes.getString("Email").toString());
          document1.put("firstName", regRes.getString("FirstName").toString());
          document1.put("lastName", regRes.getString("LastName").toString());
          document1.put("userId", regRes.getString("_key").toString());
          document1.put("response", "login successful");
        });
      }
      arangoConn.shutdown();
    }
    return document1;

  }

  public String usersRegistration(Map<String, String> registerUserDetails) throws IOException {

    String response1 = null;
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);

      if (arangodb != null) {
        ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangodb,
            userRegistration);
        String password = registerUserDetails.get("password").toString();
        String encryptedpassword = null;
        try {
          /* MessageDigest instance for MD5. */
          MessageDigest m = MessageDigest.getInstance("MD5");

          /* Add plain-text password bytes to digest using MD5 update() method. */
          m.update(password.getBytes());

          /* Convert the hash value into bytes */
          byte[] bytes = m.digest();

          /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
          StringBuilder s = new StringBuilder();
          for (int i = 0; i < bytes.length; i++) {
            s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
          }

          /* Complete hashed password in hexadecimal format */
          encryptedpassword = s.toString();
        } catch (Exception e) {
          e.printStackTrace();
        }

        /* Display the unencrypted and encrypted passwords. */
        logger.info("Plain-text password: " + password);
        logger.info("Encrypted password using MD5: " + encryptedpassword);

        //Resource resource = new ClassPathResource("images/male.jpg");
        //ClassPathResource resource = new ClassPathResource("male.jpg");
        //InputStream input = resource.getInputStream();

        //File file = resource.getFile();

        HashMap<String, String> document = new HashMap<>();
        Date date = new Date();
        String firstName = registerUserDetails.get("firstName").toString();
        String lastName = registerUserDetails.get("lastName").toString();
        String email = registerUserDetails.get("email");
        document.put("FirstName", firstName);
        document.put("LastName", lastName);
        document.put("Email", email);

        document.put("Password", encryptedpassword);
        //document.put("Image", file.toString());
        document.put("CreatedOn", date.toString());
        document.put("LastModifiedOn", date.toString());
        document.put("Identifier", email);
        logger.info(String.valueOf(document));
        try {
          arangoCollection.insertDocument(document);
          logger.info("Search Value Document Created");
        } catch (ArangoDBException e) {

          //arangoCollection.replaceDocuments(response, document);
          log.error("Exception while executing StoreSearches  Query: " + e.getMessage().toString());

//			logger.info("Search Value Updated");
        }
      }
      arangoConn.shutdown();
    }
    return response1;


  }

  public List<Object> getRegisterUsers(String identifier) {

    List<Object> response = new ArrayList<>();
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);
//			String queryToBeExecuted="for node in "+ pincollection +"\r\n"
//					+ "return node.responsibilities";
//			String queryToBeExecuted="for node in "+ userRegistration +"\r\n"
//					+ "return {Email:node.Email,FirstName:node.FirstName,LastModifiedOn:node.LastModifiedOn,LastName:node.LastName,CreatedOn:node.CreatedOn,Identifier:node.Identifier}";
//			
      String queryToBeExecuted = "for node in " + userRegistration + "\r\n"
          + "filter node.Identifier == '" + identifier + "'\r\n"
          + "return {Email:node.Email,FirstName:node.FirstName,LastModifiedOn:node.LastModifiedOn,LastName:node.LastName,CreatedOn:node.CreatedOn,Identifier:node.Identifier}";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted);
      ArangoCursor<Object> cursor = null;
      try {

        cursor = arangodb.query(queryToBeExecuted, Object.class);
        response = cursor.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while executing  Query: " + e.getMessage().toString());
      }
      arangoConn.shutdown();
    }
    return response;

  }

  public List<Object> getRegisterUsers() {
    List<Object> response = new ArrayList<>();
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);
      String queryToBeExecuted = "for node in " + userRegistration + "\r\n"
          + "return {Email:node.Email,FirstName:node.FirstName,LastModifiedOn:node.LastModifiedOn,LastName:node.LastName,CreatedOn:node.CreatedOn,Identifier:node.Identifier,id:node._key}";
      logger.info("queryToBeExecuted----->" + queryToBeExecuted);
      ArangoCursor<Object> cursor = null;
      try {

        cursor = arangodb.query(queryToBeExecuted, Object.class);
        response = cursor.asListRemaining();

      } catch (Exception e) {
        log.error("Exception while executing  Query: " + e.getMessage().toString());
      }
      arangoConn.shutdown();
    }
    return response;


  }

  public HashMap<String, String> getmylogin(HashMap loginDetails) {
    List<String> response = new ArrayList<>();
    List<Object> loginInfo = new ArrayList<>();
    //String resstr="login successful";
    //JSONObject resstr=new JSONObject();
    HashMap<String, String> resstr = new HashMap<>();
    ArangoDB arangoConn = arangorestclient.getArangoConnection();
    if (arangoConn != null) {
      ArangoDatabase arangodb = arangorestclient.getArangoDBConnection(arangoConn, arangodatabase);

      if (arangodb != null) {
        ArangoCollection arangoCollection = arangorestclient.getArangoCollection(arangodb,
            userRegistration);
        String userName = loginDetails.get("email").toString();
        String password = loginDetails.get("password").toString();

        String encryptedpassword = null;
        try {
          /* MessageDigest instance for MD5. */
          MessageDigest m = MessageDigest.getInstance("MD5");

          /* Add plain-text password bytes to digest using MD5 update() method. */
          m.update(password.getBytes());

          /* Convert the hash value into bytes */
          byte[] bytes = m.digest();

          /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
          StringBuilder s = new StringBuilder();
          for (int i = 0; i < bytes.length; i++) {
            s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
          }

          /* Complete hashed password in hexadecimal format */
          encryptedpassword = s.toString();
        } catch (Exception e) {
          e.printStackTrace();
        }

        /* Display the unencrypted and encrypted passwords. */
        logger.info("Plain-text password: " + password);
        logger.info("Encrypted password using MD5: " + encryptedpassword);

        String queryToBeExecuted = "for d in " + userRegistration + "\r\n"
            + "filter d.Email == '" + userName + "' && d.Password == '" + encryptedpassword
            + "'\r\n"
            + "return d";
        logger.info("queryToBeExecuted----->" + queryToBeExecuted);
        ArangoCursor<String> cursor = null;
        try {

          cursor = arangodb.query(queryToBeExecuted, String.class);
          response = cursor.asListRemaining();

        } catch (Exception e) {
          log.error("Exception while executing  Query: " + e.getMessage().toString());
        }
        logger.info("response" + response);
        if (response.isEmpty()) {
          //resstr="login failed";
//			resstr.put("response", "login failed");
//			resstr.put("Status", 200);
//			loginInfo.add(resstr.toMap());
          throw new UnAuthorizedException(queryToBeExecuted, null);
        } else {
          response.forEach(a -> {
            JSONObject b = new JSONObject(a);
            logger.info(String.valueOf(b));
            String fname = b.getString("FirstName");
            String lname = b.getString("LastName");
            String userId = b.getString("_key");
            String email = b.getString("Email");
            if (b.has("cover")) {
              String cover = b.getString("cover");
              resstr.put("cover", cover);
            }
            resstr.put("email", email);
            resstr.put("response", "login successful");
            resstr.put("FirstName", fname);
            resstr.put("LastName", lname);
            resstr.put("userId", userId);
            //loginInfo.add(resstr.toMap());
          });
          //resstr="login successful";
        }
      }
      arangoConn.shutdown();
    }
    return resstr;
  }

}

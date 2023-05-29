package com.datasouk.mapper;

import com.datasouk.service.collibra.CollibraRestClient;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolesMap {

  private static Logger logger = LoggerFactory.getLogger(RolesMap.class);

  @Autowired
  private CollibraRestClient collibrarestclient;

  public Map<String, String> MapRoles() {

    String relationsResponse = collibrarestclient.getRelationTypes();

    JSONObject relationTypesJson = new JSONObject(relationsResponse);

    JSONArray results = (JSONArray) relationTypesJson.get("results");
    logger.info("size--->" + results.get(0));
    Map<String, String> roles = new HashMap<String, String>();

    for (Object eachAssetObject : results) {

      JSONObject eachRelation = new JSONObject(eachAssetObject.toString());
      if (eachRelation.has("role") && eachRelation.has("coRole")) {
        String role = eachRelation.getString("role").toString();
        String corole = eachRelation.getString("coRole").toString();
        String id = eachRelation.get("id").toString();

        roles.put(id + "_Source", role);
        roles.put(id + "_Target", corole);
      }
    }

    logger.info("relationTypes---->" + relationsResponse);

    return roles;
  }

}

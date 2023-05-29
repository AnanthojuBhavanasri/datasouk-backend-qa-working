package com.datasouk.mapper;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResponsibilitiesMap {

  private static Logger logger = LoggerFactory.getLogger(ResponsibilitiesMap.class);

  public Map<String, String> maproleIds(String viewJson) {

    JSONObject tvc = new JSONObject(viewJson);
    JSONArray columns = (JSONArray) tvc.get("Columns");

    Map<String, String> map = new HashMap<String, String>();

    for (Object eachcolumn : columns) {

      JSONObject columnObject = (JSONObject) eachcolumn;

      if (columnObject.has("Group")) {

        JSONObject groupObject = (JSONObject) columnObject.get("Group");
        JSONArray groupColumns = (JSONArray) groupObject.get("Columns");

        for (int i = 0; i < groupColumns.length(); i++) {

          JSONObject eachgroupColumnObject = (JSONObject) ((JSONObject) groupColumns.get(i)).get(
              "Column");

          if (eachgroupColumnObject.get("fieldName")
              .equals(groupObject.get("name") + "_User_UserName")) {

            String label = eachgroupColumnObject.get("label").toString();
            String[] label1 = label.split(">");
            logger.info(String.valueOf(label1));
            String label2 = label1[1];
            map.put(groupObject.get("name").toString(), label2);
          }
        }
      }
    }
    return map;
  }


}

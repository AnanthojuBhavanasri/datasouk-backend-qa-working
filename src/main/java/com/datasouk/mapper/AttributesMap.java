package com.datasouk.mapper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


@Component
public class AttributesMap {

	public Map<String, String> mapAttributeIds(String viewJson) {

		JSONObject tvc = new JSONObject(viewJson);
		JSONArray columns = (JSONArray) tvc.get("Columns");

		Map<String, String> map = new HashMap<String, String>();

		for (Object eachcolumn : columns) {

			JSONObject columnObject = (JSONObject) eachcolumn;

			if (columnObject.has("Group")) {

				JSONObject groupObject = (JSONObject) columnObject.get("Group");
				JSONArray groupColumns = (JSONArray) groupObject.get("Columns");

				for (int i = 0; i < groupColumns.length(); i++) {

					JSONObject eachgroupColumnObject = (JSONObject) ((JSONObject) groupColumns.get(i)).get("Column");

					if (eachgroupColumnObject.get("fieldName").equals(groupObject.get("name") + "_Value")) {

						String label = eachgroupColumnObject.get("label").toString();
						map.put(groupObject.get("name").toString(), label);
					}
				}
			}
		}
		return map;
	}

}

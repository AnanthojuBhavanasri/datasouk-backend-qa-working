package com.datasouk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.datasouk.service.arango.connect.ConnectArango;
import com.datasouk.service.collibra.CollibraRestClient;
import com.datasouk.mapper.AttributesMap;
import com.datasouk.mapper.NodesMap;
import com.datasouk.mapper.ResponsibilitiesMap;

@Service
public class DGC2ArangoDB {

	Logger log = LoggerFactory.getLogger(DGC2ArangoDB.class);

	@Value("${arango.nodes.collection}")
	private String arangoNodesCollection;

	@Value("${arango.category.collection}")
	private String arangoCategoriesCollection;

	@Value("${arango.database}")
	private String arangodatabase;

	@Autowired
	private CollibraRestClient collibrarestclient;

	@Autowired
	private AttributesMap mapattributesbytype;

	@Autowired
	private NodesMap mapNodes;

	@Autowired
	private ConnectArango connectArango;
	
	@Autowired
	private ResponsibilitiesMap responsibilitiesMap;

	public String exportdgc2Arango(String viewId) throws IOException {

		String response = null;
		// Export TableViewConfig by passing viewId to outputmoduleapi
		String responseString = collibrarestclient.exportByViewId(viewId);

		if (responseString != null) {

			JSONObject viewJson = new JSONObject(responseString);
			JSONObject tableviewConfig = viewJson.getJSONObject("TableViewConfig");

			tableviewConfig.remove("displayLength");
			viewJson.put("TableViewConfig", tableviewConfig);

			// Mapping attributeIds with their typeNames
			Map<String, String> attributeMap = mapattributesbytype.mapAttributeIds(tableviewConfig.toString());
			
			// Mapping roleIds with their typeNames
						Map<String, String> ResponsibilityMap = responsibilitiesMap.maproleIds(tableviewConfig.toString());

			// Export Data from Collibra by passing "TableViewConfig" for outputmoduleapi
			String tvcData = collibrarestclient.exportJsonByTvc(viewJson.toString());

			if (tvcData != null) {

				// Mapping Assets to import into ArangoDB
				List<ArrayList<JSONObject>> assetsandRelationsList = mapNodes.MapNodes2Arango(tvcData, attributeMap,ResponsibilityMap);

				if (!assetsandRelationsList.isEmpty()) {

					ArrayList<JSONObject> nodes = assetsandRelationsList.get(0);
					ArrayList<JSONObject> edgeRelations = assetsandRelationsList.get(1);
					ArrayList<JSONObject> categoriesList = assetsandRelationsList.get(2);

					if (!nodes.isEmpty()) {

						// Importing Assets as Documents into Arango Collection
						connectArango.importDocuments2Arango(nodes.toString(), arangoNodesCollection);
						response = "Data Inserted";

					} else {
						log.info("No documents to insert into Arango");
					}

					if (!edgeRelations.isEmpty()) {

						// Importing Relations as Edges into Arango Collection
						connectArango.importEdges2Arango(edgeRelations.toString());
						response = "Data Inserted";

					} else {
						log.info("No Edges to insert into Arango");
					}

					if (!categoriesList.isEmpty()) {

						connectArango.importDocuments2Arango(categoriesList.toString(), arangoCategoriesCollection);
						response = "Data Inserted";
					} else {
						log.info("No documents to insert into Arango");
					}
				}
			}
		}

		return response;

	}

}

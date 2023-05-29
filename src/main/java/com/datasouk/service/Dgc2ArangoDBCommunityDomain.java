package com.datasouk.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.datasouk.service.arango.connect.ConnectArango;
import com.datasouk.service.collibra.CollibraRestClient;
import com.datasouk.mapper.AttributesMap;
import com.datasouk.mapper.CommunityMap;
import com.datasouk.mapper.NodesMap;
import com.datasouk.mapper.ResponsibilitiesMap;

@Service
public class Dgc2ArangoDBCommunityDomain {
	
	Logger logger = LoggerFactory.getLogger(Dgc2ArangoDBCommunityDomain.class);

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
	private CommunityMap mapCommunity;

	@Autowired
	private ConnectArango connectArango;
	
	@Autowired
	private ResponsibilitiesMap responsibilitiesMap;

	public String exportdgc2Arangocd() throws IOException {
		String response = null;
		InputStream initialStream = null;
		BufferedReader r = null;
		String l = null;
		String content = null;
		//String responseString = collibrarestclient.exportByViewId();
		 initialStream = getClass().getResourceAsStream("/communitycollection.json");
		r = new BufferedReader(new InputStreamReader(initialStream));
		 content = "";
		// reads each line
		while ((l = r.readLine()) != null) {
		content = content + l;
		}
		initialStream.close();
		
		JSONObject jsonObject = new JSONObject(content);

		String tvcData = null;

		if (jsonObject != null) {
			
			tvcData = collibrarestclient.exportJsonByTvc(jsonObject.toString());
			logger.info(tvcData);
			if (tvcData != null) {
				JSONObject communityinfo=mapCommunity.mapcommunities2Arango(tvcData);
				logger.info(String.valueOf(communityinfo));
			}
		}
		return tvcData;
	}
	public String exportdgc2Arangod() throws IOException {
		String response = null;
		InputStream initialStream = null;
		BufferedReader r = null;
		String l = null;
		String content = null;
		 initialStream = getClass().getResourceAsStream("/domaincollection.json");
		r = new BufferedReader(new InputStreamReader(initialStream));
		 content = "";
		while ((l = r.readLine()) != null) {
		content = content + l;
		}
		initialStream.close();
		JSONObject jsonObject = new JSONObject(content);
		String tvcData = null;
		if (jsonObject != null) {
			tvcData = collibrarestclient.exportJsonByTvc(jsonObject.toString());
			logger.info(tvcData);
			if (tvcData != null) {
				JSONObject domainInfo=mapCommunity.mapdomains2Arango(tvcData);	
			}
		}
		return tvcData;
	}

}

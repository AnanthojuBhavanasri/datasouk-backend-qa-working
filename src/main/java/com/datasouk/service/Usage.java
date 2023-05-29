package com.datasouk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.datasouk.service.arango.connect.ArangoRestClient;
import com.datasouk.service.arango.connect.ConnectArango;

@Service
public class Usage {
	
	@Autowired
	private ArangoRestClient arangorestclient;
	
	@Autowired
	private ConnectArango connectArango;
	
	@Value("${arango.database}")
	private String arangodatabase;
	
	@Value("${arango.viewName}")
	private String viewName;
	
	Logger log = LoggerFactory.getLogger(Usage.class);

	
	public String getQueryResultId(String id){
		final String query = "For node in " + viewName +"\r\n"
				+ "filter node.id == '"+ id +"'\r\n"
				+ "return node";
		return query;
		
	}
	
	public String getQueryResultName(String name){
		final String query = "For node in " + viewName +"\r\n"
				+ "filter node.name == '"+ name +"'\r\n"
				+ "return node";
		return query;
		
	}
	
}

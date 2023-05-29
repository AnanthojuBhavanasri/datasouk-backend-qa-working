package com.datasouk.service.collibra;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Service
public class CollibraRestClient {

	@Value("${collibra.url}")
	private String collibraUrl;

	@Value("${collibra.port}")
	private String collibraPort;

	@Value("${collibra.apipath}")
	private String collibraApiPath;

	@Value("${collibra.outputModuleApipath}")
	private String collibraOutputModuleApiPath;

	@Value("${collibra.user}")
	private String collibraUsername;

	@Value("${collibra.password}")
	private String collibraPassword;

	@Value("${collibra.exportjsonpath}")
	private String collibraexportjsonpath;
	
	@Value("${collibra.relationsTypesApipath}")
	private String collibrarelationtypespath;

	Logger log = LoggerFactory.getLogger(CollibraRestClient.class);


	public String exportByViewId(String viewId) {

		String response = null;
		try {

			String exportUrl = collibraUrl + ":" + collibraPort + collibraApiPath + collibraOutputModuleApiPath
					+ viewId;

			String auth = collibraUsername + ":" + collibraPassword;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodedAuth);

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set(HttpHeaders.AUTHORIZATION, authHeader);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<String> responseString = restTemplate.exchange(exportUrl, HttpMethod.GET, entity,
					String.class);
			response = responseString.getBody();
			log.info("Status : " + responseString.getStatusCodeValue());
		} catch (Exception e) {
			log.error("Exception caught during exporting TVC: " + e.toString());
		}

		return response;
	}

	public String exportJsonByTvc(String tvc) {

		String response = null;
		try {

			String exportUrl = collibraUrl + ":" + collibraPort + collibraApiPath + collibraexportjsonpath;

			String auth = collibraUsername + ":" + collibraPassword;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodedAuth);

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set(HttpHeaders.AUTHORIZATION, authHeader);

			HttpEntity<String> entity = new HttpEntity<>(tvc, headers);

			ResponseEntity<String> responseString = restTemplate.exchange(exportUrl, HttpMethod.POST, entity,
					String.class);
			response = responseString.getBody();
			log.info("Status : " + responseString.getStatusCodeValue());
		} catch (Exception e) {
			log.error("Exception caught during exporting data: " + e.toString());
		}

		return response;
	}
	
	public String getRelationTypes() {

		String response = null;
		try {

			String exportUrl = collibraUrl + ":" + collibraPort + collibraApiPath + collibrarelationtypespath;

			String auth = collibraUsername + ":" + collibraPassword;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodedAuth);

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set(HttpHeaders.AUTHORIZATION, authHeader);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<String> responseString = restTemplate.exchange(exportUrl, HttpMethod.GET, entity,
					String.class);
			response = responseString.getBody();
			log.info("Status : " + responseString.getStatusCodeValue());
		} catch (Exception e) {
			log.error("Exception caught during exporting TVC: " + e.toString());
		}

		return response;
	}
}

package com.datasouk.mapper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.datasouk.service.arango.connect.ConnectArango;

@Component
public class CommunityMap {
	Logger log = LoggerFactory.getLogger(CommunityMap.class);

	@Value("${arango.pin.collection}")
	private String arangoPinCollection;

	@Autowired
	private ConnectArango ca;

	public JSONObject mapcommunities2Arango(String tvcData)
	{
		JSONObject jsonresponse = new JSONObject(tvcData);
		JSONObject jsonresponse1 = (JSONObject) jsonresponse.get("view");
		JSONArray aaData = (JSONArray) jsonresponse1.get("Community0");
		JSONObject communityInfo = new JSONObject();
		JSONObject domainInfo = new JSONObject();
		JSONArray domainInfo1 = new JSONArray();
		//HashMap<String,Object> collections = new HashMap<>();
		List<JSONObject> communityList=new ArrayList<JSONObject>();
		ArrayList<JSONObject> rolesObject = new ArrayList<JSONObject>();
		ArrayList<JSONObject> Nodes = new ArrayList<JSONObject>();
		ArrayList<JSONObject> nodesInfo = new ArrayList<JSONObject>();
		ArrayList<JSONObject> collections = new ArrayList<JSONObject>();

		aaData.forEach(action ->{

			JSONObject node = new JSONObject(action.toString());
			log.info(String.valueOf(node));
			String meta=node.get("meta").toString();

			if(meta.contains("false")){
				if(node.has("Vocabulary1")){
					JSONArray domain = node.getJSONArray("Vocabulary1");
					domain.forEach(domainsInfo -> {
						JSONObject domains = new JSONObject(domainsInfo.toString());
						JSONObject domainName = new JSONObject();
						//JSONObject domainId = new JSONObject();
						String domainname=domains.getString("Domain_Name");
						String domainid=domains.getString("Domain_Id");
						domainName.put("name",domainname);
						domainName.put("id",domainid);
						collections.add(domainName);
					});
				}

				if(node.has("Member3")) {
					JSONArray roles = node.getJSONArray("Member3");
					log.info(String.valueOf(roles));
					roles.forEach(rolesInfo -> {
						JSONObject role= new JSONObject(rolesInfo.toString());
						log.info(String.valueOf(role));
						JSONObject roleName = new JSONObject();
						if(role.has("User5") && role.has("Role4")){
							JSONArray Users=role.getJSONArray("User5");
							JSONArray Roles=role.getJSONArray("Role4");
							Users.forEach(user->{
								String fullname;
								JSONObject userNameObject= new JSONObject(user.toString());
								if(userNameObject.has("firstName") && userNameObject.has("lastName")){
									String fname=userNameObject.getString("firstName");
									String lname=userNameObject.getString("lastName");
									fullname=fname.concat(lname);
									Roles.forEach(rol->{
										JSONObject roleNameObject= new JSONObject(rol.toString());
										String rname=roleNameObject.getString("roleName");
										roleName.put(rname, fullname);
										rolesObject.add(roleName);
									});
								}
							});

						}	

					});

					//communityInfo.put("roles",rolesObject);	
				}

				communityInfo.put("pinCollection",collections);
				communityInfo.put("pinNodes",Nodes);
				communityInfo.put("displayName",node.get("communityName"));
				communityInfo.put("createdOn",node.get("CreatedOn"));
				communityInfo.put("createdBy",node.get("CreatedBy"));
				communityInfo.put("lastModified",node.get("lastModified"));
				//communityInfo.put("lastModifiedBy",node);
				communityInfo.put("lastModifiedBy",node.get("lastModifiedBy"));
				communityInfo.put("roles",rolesObject);	

				//communityInfo.put("meta",node.get("meta"));
				log.info(String.valueOf(communityInfo));
				nodesInfo.add(communityInfo);
				log.info("nodesInfo->"+nodesInfo);
				ca.importDocuments2Arango(nodesInfo.toString(),arangoPinCollection);
				nodesInfo.clear();
			}

		});
		log.info(String.valueOf(communityInfo));
		return communityInfo;

	}

	public JSONObject mapdomains2Arango(String tvcData) {
		JSONObject jsonresponse = new JSONObject(tvcData);
		return jsonresponse;
	}
}

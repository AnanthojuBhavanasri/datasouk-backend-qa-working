package com.datasouk.mapper;

import com.datasouk.core.properties.DatasoukProperties;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NodesMap {

  Logger logger = LoggerFactory.getLogger(NodesMap.class);
  @Autowired
  private RolesMap mapRoles;

  public List<ArrayList<JSONObject>> MapNodes2Arango(String tvcData,
      Map<String, String> attributesMap, Map<String, String> ResponsibilityMap) {

    Map<String, String> nodeTypeMap = new HashMap<String, String>();
    ArrayList<JSONObject> nodesList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> categoriesList = new ArrayList<JSONObject>();
    ArrayList<JSONObject> edgerelations = new ArrayList<JSONObject>();
    List<ArrayList<JSONObject>> nodesAndRelList = new ArrayList<ArrayList<JSONObject>>();
    ArrayList<JSONObject> relationsList = new ArrayList<JSONObject>();

    JSONObject jsonresponse = new JSONObject(tvcData);
    JSONArray aaData = (JSONArray) jsonresponse.get("aaData");
    JSONObject node = (JSONObject) aaData.get(0);
    JSONArray targets = node.names();

    List<String> targetRelations = new ArrayList<String>();
    List<String> sourceRelations = new ArrayList<String>();
    List<String> responsibilities = new ArrayList<String>();
    List<String> attributestvc = new ArrayList<String>();

    targets.forEach(eachName -> {
      if (eachName.toString().contains("Attribute")) {
        attributestvc.add(eachName.toString());
      } else if (eachName.toString().startsWith("TargetRelation_")) {
        targetRelations.add(eachName.toString());
      } else if (eachName.toString().startsWith("SourceRelation_")) {
        sourceRelations.add(eachName.toString());
      } else if (eachName.toString().startsWith("Responsibility_")) {
        responsibilities.add(eachName.toString());
      }
    });
    Map<String, String> relationsTypeMap = mapRoles.MapRoles();

    for (Object eachnodeObject : aaData) {

      JSONObject eachnode = (JSONObject) eachnodeObject;
      JSONObject nodeInfo = new JSONObject();
      JSONObject categoricalData = new JSONObject();
      List<HashMap> category = new ArrayList<>();
      //JSONObject category1 = new JSONObject();
      HashMap<String, List<String>> category1 = new HashMap();
      List<String> Values = new ArrayList<String>();
      ArrayList<JSONObject> sources = new ArrayList<JSONObject>();
      ArrayList<JSONObject> targets2 = new ArrayList<JSONObject>();
      ArrayList<JSONObject> responsibilitiesObject = new ArrayList<JSONObject>();
      ArrayList<JSONObject> attributes = new ArrayList<JSONObject>();
      JSONObject relations = new JSONObject();

      try {
        String nodeName = eachnode.get("FullName").toString();

        nodeInfo.put("name", nodeName);
        nodeInfo.put("displayName", eachnode.get("DisplayName"));
        nodeInfo.put("id", eachnode.get("Id"));
        //nodeInfo.put("createdOn", eachnode.get("CreatedOn"));
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        nodeInfo.put("createdOn", utc.toInstant().toString());
        //source.setCreatedOn(utc.toInstant().toString());
        if (eachnode.has("CreatedBy_Id")) {
          nodeInfo.put("createdBy", eachnode.get("CreatedBy_Id"));
        }
//				nodeInfo.put("lastModifiedOn", eachnode.get("LastModifiedOn"));
        nodeInfo.put("articulationScore", eachnode.get("ArticulationScore"));
        nodeInfo.put("ratingsCount", eachnode.get("RatingsCount"));
        nodeInfo.put("avgRating", eachnode.get("AvgRating"));
        if (eachnode.has("CreatedBy_UserName")) {
          nodeInfo.put("createdByUserName", eachnode.get("CreatedBy_UserName"));
        } else {
          nodeInfo.put("createdByUserName", "");
        }
        if (eachnode.has("CreatedBy_FirstName") && eachnode.has("CreatedBy_LastName")) {
          nodeInfo.put("createdByFullName",
              eachnode.get("CreatedBy_FirstName") + " " + eachnode.get("CreatedBy_LastName"));
        } else {
          nodeInfo.put("createdByFullName", "");
        }
        // Domain Object
        JSONObject domainInfo = new JSONObject();
        String domainName = eachnode.get("Domain_Name").toString();
        domainInfo.put("name", domainName);
        domainInfo.put("id", eachnode.get("Domain_Id"));
        domainInfo.put("url", "http://datanomist.in:4400/domain/" + eachnode.get("Domain_Id"));
        nodeInfo.put("domain", domainInfo);

        // Community Object
        JSONObject communityInfo = new JSONObject();
        String communityName = eachnode.get("Domain_Community_Name").toString();
        communityInfo.put("name", communityName);
        communityInfo.put("id", eachnode.get("Domain_Community_Id"));
        communityInfo.put("url",
            "http://datanomist.in:4400/community/" + eachnode.get("Domain_Community_Id"));
        nodeInfo.put("community", communityInfo);

        // Status Object
        JSONObject statusInfo = new JSONObject();
        statusInfo.put("name", eachnode.get("Status_Name"));
        statusInfo.put("id", eachnode.get("Status_Id"));
        nodeInfo.put("status", statusInfo);

        // Type Object
        JSONObject typeInfo = new JSONObject();
        typeInfo.put("id", eachnode.get("AssetType_Id"));
        typeInfo.put("name", eachnode.get("AssetType_Name"));
        nodeTypeMap.put(eachnode.get("Id").toString(), eachnode.get("AssetType_Name").toString());
        typeInfo.put("metaCollectionName",
            DatasoukProperties.nodeTypes.get(eachnode.get("AssetType_Name")));
        nodeInfo.put("type", typeInfo);

        // Mapping Sources of the node
        for (int i = 0; i < targetRelations.size(); i++) {

          JSONArray targetlist = (JSONArray) eachnode.get(targetRelations.get(i));

          for (Object eachTarget : targetlist) {

            JSONObject targetInfo = new JSONObject();
            JSONObject edgeRelationInfo = new JSONObject();

            JSONObject target = (JSONObject) eachTarget;

            targetInfo.put("id", target.get(targetRelations.get(i) + "_Id"));
            targetInfo.put("typeId", targetRelations.get(i).toString().split("_")[1]);
            //relationInfo.put("role",relationsTypeMap.get(relationInfo.get("typeId")+"_Source"));
            targetInfo.put("role", relationsTypeMap.get(targetInfo.get("typeId") + "_Source"));

            JSONObject source = new JSONObject();
            source.put("displayName", target.get(targetRelations.get(i) + "_Source_DisplayName"));
            source.put("name", target.get(targetRelations.get(i) + "_Source_FullName"));
            source.put("id", target.get(targetRelations.get(i) + "_Source_Id"));
            //source.put("identifier","Datanomist" + " > " + eachnode.get("Domain_Community_Name").toString() + " > " + eachnode.get("Domain_Name").toString() + " > " + target.get(targetRelations.get(i) + "_Source_DisplayName"));

            targetInfo.put("source", source);

            JSONObject targetnodeInfo = new JSONObject();
            targetnodeInfo.put("displayName", eachnode.get("DisplayName"));
            targetnodeInfo.put("name", nodeName);
            targetnodeInfo.put("id", eachnode.get("Id"));
            //targetnodeInfo.put("identifier","Datanomist" + " > " + eachnode.get("Domain_Community_Name").toString() + " > " + eachnode.get("Domain_Name").toString() + " > " +  target.get(targetRelations.get(i) + "_Target_FullName"));

            // For Edge Documents
            edgeRelationInfo.put("id", target.get(targetRelations.get(i) + "_Id"));
            edgeRelationInfo.put("typeId", targetRelations.get(i).toString().split("_")[1]);
            edgeRelationInfo.put("source", source);
            edgeRelationInfo.put("target", targetnodeInfo);

            edgerelations.add(edgeRelationInfo);
            sources.add(targetInfo);
          }
        }
        relations.put("sources", sources);

        // Mapping Targets of the node
        for (int i = 0; i < sourceRelations.size(); i++) {

          JSONArray sourceList = (JSONArray) eachnode.get(sourceRelations.get(i));

          for (Object eachSource : sourceList) {

            JSONObject edgeRelationInfo = new JSONObject();
            JSONObject sourceInfo = new JSONObject();

            JSONObject source = (JSONObject) eachSource;

            sourceInfo.put("id", source.get(sourceRelations.get(i) + "_Id"));
            sourceInfo.put("typeId", sourceRelations.get(i).toString().split("_")[1]);
            sourceInfo.put("coRole", relationsTypeMap.get(sourceInfo.get("typeId") + "_Target"));

            JSONObject target = new JSONObject();
            target.put("displayName", source.get(sourceRelations.get(i) + "_Target_DisplayName"));
            target.put("name", source.get(sourceRelations.get(i) + "_Target_FullName"));
            target.put("id", source.get(sourceRelations.get(i) + "_Target_Id"));
            //target.put("identifier","Datanomist" + " > " + eachnode.get("Domain_Community_Name").toString() + " > " + eachnode.get("Domain_Name").toString() + " > " + source.get(sourceRelations.get(i) + "_Target_FullName"));

            sourceInfo.put("target", target);

            JSONObject sourcenodeInfo = new JSONObject();
            sourcenodeInfo.put("displayName", eachnode.get("DisplayName"));
            sourcenodeInfo.put("name", nodeName);
            sourcenodeInfo.put("id", eachnode.get("Id"));
            //sourcenodeInfo.put("identifier","Datanomist" + " > " + eachnode.get("Domain_Community_Name").toString() + " > " + eachnode.get("Domain_Name").toString() + " > " + target.get(targetRelations.get(i) + "_Source_FullName"));
//						sourceInfo.put("source", sourcenodeInfo);

            edgeRelationInfo.put("id", source.get(sourceRelations.get(i) + "_Id"));
            edgeRelationInfo.put("typeId", sourceRelations.get(i).toString().split("_")[1]);
            edgeRelationInfo.put("source", sourcenodeInfo);
            edgeRelationInfo.put("target", target);

            edgerelations.add(edgeRelationInfo);

            targets2.add(sourceInfo);
          }
        }

        relations.put("targets", targets2);
        nodeInfo.put("relations", relations);

        // Mapping Responsibilities
        for (int i = 0; i < responsibilities.size(); i++) {

          JSONArray responsibiltyList = (JSONArray) eachnode.get(responsibilities.get(i));

          for (Object eachresponsibility : responsibiltyList) {

            JSONObject responsibiltyInfo = new JSONObject();
            JSONObject responsibilty = (JSONObject) eachresponsibility;

            responsibiltyInfo.put("userName",
                responsibilty.get(responsibilities.get(i) + "_User_UserName"));

            responsibiltyInfo.put("name",
                responsibilty.get(responsibilities.get(i) + "_User_FirstName")
                    + " " + responsibilty.get(responsibilities.get(i) + "_User_LastName"));

            responsibiltyInfo.put("userId",
                responsibilty.get(responsibilities.get(i) + "_User_Id"));

            responsibiltyInfo.put("groupName",
                responsibilty.get(responsibilities.get(i) + "_UserGroup_Name"));

            responsibiltyInfo.put("groupId",
                responsibilty.get(responsibilities.get(i) + "_UserGroup_Id"));
            responsibiltyInfo.put("roleId", responsibilities.get(i).toString().split("_")[1]);
            responsibiltyInfo.put("roleName", ResponsibilityMap.get(responsibilities.get(i)));
            responsibilitiesObject.add(responsibiltyInfo);
          }
        }

        nodeInfo.put("responsibilities", responsibilitiesObject);

        JSONObject attrInfo = new JSONObject();
        // Mapping Attributes of the node
        for (int i = 0; i < attributestvc.size(); i++) {

          JSONArray attributesList = (JSONArray) eachnode.get(attributestvc.get(i));

          for (Object eachAttribute : attributesList) {

            JSONObject attributeInfo = new JSONObject();
            JSONObject attribute = (JSONObject) eachAttribute;

            if (attributesMap.get(attributestvc.get(i)).equals("Category")) {
              String[] attributeValues = attribute.get(attributestvc.get(i) + "_Value").toString()
                  .split(", ");
              for (String eachValue : attributeValues) {
                //category.add(eachValue);
                Values.add(eachValue);
                category1.put("cat", Values);
                //category1.put("category1", eachValue);

                logger.info(eachValue);

              }
              category.add(category1);
            } else {
              attributeInfo.put("id", attribute.get(attributestvc.get(i) + "_Id"));
              attributeInfo.put("name", attributesMap.get(attributestvc.get(i)));
              attributeInfo.put("value", attribute.get(attributestvc.get(i) + "_Value"));
            }

            attributes.add(attributeInfo);
          }
        }
        attrInfo.put("name", "LastModifiedOn");
        //ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        //nodeInfo.put("createdOn", utc.toInstant().toString());
        attrInfo.put("value", utc.toInstant().toString());
        attributes.add(attrInfo);

        if (!category.isEmpty()) {
          HashSet<Object> setCategory = new HashSet<Object>(category);
          categoricalData.put("_key", eachnode.get("Id"));
          categoricalData.put("name", nodeName);
          categoricalData.put("categories", setCategory);
          categoriesList.add(categoricalData);
        }

        nodeInfo.put("attributes", attributes);
        nodeInfo.put("_key", eachnode.get("Id"));
        nodeInfo.put("identifier",
            "Datanomist" + " > " + communityName + " > " + domainName + " > " + nodeName);
        nodesList.add(nodeInfo);

      } catch (Exception e) {
        logger.error("Exception while mapping nodes:" + e.getMessage().toString());
      }
    }

    nodesAndRelList.add(nodesList);

    edgerelations.forEach(relationInfo -> {

      relationInfo.put("role", relationsTypeMap.get(relationInfo.get("typeId") + "_Source"));
      relationInfo.put("coRole", relationsTypeMap.get(relationInfo.get("typeId") + "_Target"));
//			relationInfo.put("identifier",
//						"Datanomist" + " > " + nodesList.get(0).getString("identifier"));
      JSONObject source = (JSONObject) relationInfo.get("source");
      JSONObject target = (JSONObject) relationInfo.get("target");

      // Source Attribute of an edge
      relationInfo.put("_from", source.get("id"));

      source.put("type", nodeTypeMap.get(source.get("id")));
      relationInfo.put("source", source);

      // Target Attribute of an edge
      relationInfo.put("_to", target.get("id"));

      target.put("type", nodeTypeMap.get(target.get("id")));
      relationInfo.put("target", target);

      // Key attribute of an edge
      relationInfo.put("_key", relationInfo.get("id"));
      relationsList.add(relationInfo);
    });

    nodesAndRelList.add(relationsList);
    nodesAndRelList.add(categoriesList);

    return nodesAndRelList;
  }

}

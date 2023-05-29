package com.datasouk.service;

import com.datasouk.core.models.arango.MetagraphObject;
import com.datasouk.core.properties.DatasoukProperties;
import com.datasouk.service.arango.connect.ConnectArango;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DocumentsClassification {

  private static Logger logger = LoggerFactory.getLogger(DocumentsClassification.class);

  @Autowired
  private ConnectArango connectArango;

  @Value("${arango.nodes.collection}")
  private String arangoNodesCollection;

  @Value("${arango.edges.collection}")
  private String arangoEdgesCollection;

  @Value("${arango.edgesByName.collection}")
  private String arangoEdgesByNameCollection;

  @Value("${arango.nodetypes.collection}")
  private String arangoNodeTypesCollection;

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void groupByNodeTypes() {

    List<HashMap> documents = connectArango.groupBynodetype(arangoNodesCollection);

    // ArrayList<MetagraphObject> nodesList = new ArrayList<MetagraphObject>();

    documents.forEach(eachCollection -> {

      ArrayList<MetagraphObject> businessnodesList = new ArrayList<MetagraphObject>();
      ArrayList<MetagraphObject> governancenodesList = new ArrayList<MetagraphObject>();
      ArrayList<MetagraphObject> logicaldatanodesList = new ArrayList<MetagraphObject>();
      ArrayList<MetagraphObject> Physical_Data_DictionarynodesList = new ArrayList<MetagraphObject>();
      ArrayList<MetagraphObject> TechnologynodesList = new ArrayList<MetagraphObject>();

      if (eachCollection.get("metaCollection") != null) {

        List<HashMap> nodes = (List<HashMap>) eachCollection.get("docClassification");

        //ObjectMapper mapper = new ObjectMapper();

        if (eachCollection.get("metaCollection").equals("Business")) {

          for (int i = 0; i < nodes.size(); i++) {

            MetagraphObject metagraphobject = new MetagraphObject();

            HashMap eachNodeMap = (HashMap) nodes.get(i).get("doc");

            metagraphobject.setAssetId(eachNodeMap.get("id").toString());
            metagraphobject.setId(eachNodeMap.get("_id").toString());
            metagraphobject.setIdentifier(eachNodeMap.get("identifier").toString());
            metagraphobject.setName(eachNodeMap.get("name").toString());
            metagraphobject.setType(((HashMap) eachNodeMap.get("type")).get("name").toString());

            businessnodesList.add(metagraphobject);

          }
          ArrayList<JSONObject> collectionnodes1 = response(businessnodesList);
          connectArango.importDocuments2Arango1(collectionnodes1, "Business", "Document");


        } else if (eachCollection.get("metaCollection").equals("Governance")) {
          for (int i = 0; i < nodes.size(); i++) {

            MetagraphObject metagraphobject = new MetagraphObject();

            HashMap eachNodeMap = (HashMap) nodes.get(i).get("doc");

            metagraphobject.setAssetId(eachNodeMap.get("id").toString());
            metagraphobject.setId(eachNodeMap.get("_id").toString());
            metagraphobject.setIdentifier(eachNodeMap.get("identifier").toString());
            metagraphobject.setName(eachNodeMap.get("name").toString());
            metagraphobject.setType(((HashMap) eachNodeMap.get("type")).get("name").toString());
            //metagraphobject.setKey(eachNodeMap.get("name").toString());

            governancenodesList.add(metagraphobject);

          }
          ArrayList<JSONObject> collectionnodes2 = response(governancenodesList);
          connectArango.importDocuments2Arango1(collectionnodes2, "Governance", "Document");

        } else if (eachCollection.get("metaCollection").equals("LogicalData")) {
          for (int i = 0; i < nodes.size(); i++) {

            MetagraphObject metagraphobject = new MetagraphObject();

            HashMap eachNodeMap = (HashMap) nodes.get(i).get("doc");

            metagraphobject.setAssetId(eachNodeMap.get("id").toString());
            metagraphobject.setId(eachNodeMap.get("_id").toString());
            metagraphobject.setIdentifier(eachNodeMap.get("identifier").toString());
            metagraphobject.setName(eachNodeMap.get("name").toString());
            metagraphobject.setType(((HashMap) eachNodeMap.get("type")).get("name").toString());

            logicaldatanodesList.add(metagraphobject);

          }
          ArrayList<JSONObject> collectionnodes3 = response(logicaldatanodesList);
          connectArango.importDocuments2Arango1(collectionnodes3, "LogicalData", "Document");

        } else if (eachCollection.get("metaCollection").equals("PhysicalDataDictionary")) {

          for (int i = 0; i < nodes.size(); i++) {

            MetagraphObject metagraphobject = new MetagraphObject();

            HashMap eachNodeMap = (HashMap) nodes.get(i).get("doc");

            metagraphobject.setAssetId(eachNodeMap.get("id").toString());
            metagraphobject.setId(eachNodeMap.get("_id").toString());
            metagraphobject.setIdentifier(eachNodeMap.get("identifier").toString());
            metagraphobject.setName(eachNodeMap.get("name").toString());
            metagraphobject.setType(((HashMap) eachNodeMap.get("type")).get("name").toString());

            Physical_Data_DictionarynodesList.add(metagraphobject);

          }
          ArrayList<JSONObject> collectionnodes4 = response(Physical_Data_DictionarynodesList);

          connectArango.importDocuments2Arango1(collectionnodes4, "PhysicalDataDictionary",
              "Document");

        } else if (eachCollection.get("metaCollection").equals("Technology")) {

          for (int i = 0; i < nodes.size(); i++) {

            MetagraphObject metagraphobject = new MetagraphObject();

            HashMap eachNodeMap = (HashMap) nodes.get(i).get("doc");

            metagraphobject.setAssetId(eachNodeMap.get("id").toString());
            metagraphobject.setId(eachNodeMap.get("_id").toString());
            metagraphobject.setIdentifier(eachNodeMap.get("identifier").toString());
            metagraphobject.setName(eachNodeMap.get("name").toString());
            metagraphobject.setType(((HashMap) eachNodeMap.get("type")).get("name").toString());

            TechnologynodesList.add(metagraphobject);

          }
          ArrayList<JSONObject> collectionnodes5 = response(TechnologynodesList);
          connectArango.importDocuments2Arango1(collectionnodes5, "Technology", "Document");

        }

      }

      moveRelations();

    });
  }


  private ArrayList<JSONObject> response(ArrayList<MetagraphObject> nodesList) {

    Map<String, List<MetagraphObject>> nodesMap = new HashMap<>();
    ArrayList<JSONObject> collectionnodes = new ArrayList<JSONObject>();

    for (MetagraphObject assetObj : nodesList) {

      String name = assetObj.getName();

      if (nodesMap.containsKey(name)) {

        List<MetagraphObject> assetObjs = nodesMap.get(name);
        assetObjs.add(assetObj);
        nodesMap.put(name, assetObjs);
      } else {

        List<MetagraphObject> assetObjs = new ArrayList<MetagraphObject>();
        assetObjs.add(assetObj);
        nodesMap.put(name, assetObjs);
      }
    }

    Set<String> assetnames = nodesMap.keySet();

    assetnames.forEach(eachName -> {
      JSONObject assetInfo = new JSONObject();
      assetInfo.put("_key", eachName);
      assetInfo.put("nodes", nodesMap.get(eachName));
      collectionnodes.add(assetInfo);
    });
    return collectionnodes;
  }

  public void moveRelations() {

    List<String> documents = connectArango.getAllDocs(arangoEdgesCollection);

    List<JSONObject> edges = new ArrayList<JSONObject>();
    documents.forEach(eachRelation -> {

      logger.info("eachRelation--->" + eachRelation);
      JSONObject documentInfo = new JSONObject(eachRelation.toString());
//			logger.info("documentInfo--->"+documentInfo);
      JSONObject sourceObj = (JSONObject) documentInfo.get("source");
      JSONObject targetObj = (JSONObject) documentInfo.get("target");
      if (sourceObj.has("type") && targetObj.has("type")) {
        String sourceType = (String) sourceObj.get("type");

        String sourceName = (String) sourceObj.get("name");

        String targetType = (String) targetObj.get("type");
        String targetName = (String) targetObj.get("name");

        documentInfo.put("_from", DatasoukProperties.nodeTypes.get(sourceType) + "/" + sourceName);
        documentInfo.put("_to", DatasoukProperties.nodeTypes.get(targetType) + "/" + targetName);

        edges.add(documentInfo);
      }
    });

    connectArango.importDocuments2Arango(edges.toString(), arangoEdgesByNameCollection, "Edge");

  }


  public void metaNodes() {

    List<String> documents = connectArango.getAllDocs(arangoNodesCollection);

    //List<JSONObject> edges = new ArrayList<JSONObject>();
    ArrayList<String> NodeTypes = new ArrayList<String>();
    documents.forEach(eachNode -> {

      logger.info("eachRelation--->" + eachNode);
      JSONObject documentInfo = new JSONObject(eachNode.toString());
      JSONObject nodeInfo = new JSONObject();
      JSONObject typeInfo = new JSONObject();
      typeInfo = documentInfo.getJSONObject("type");
      String typeName = typeInfo.getString("name");
      logger.info("documentInfo--->" + documentInfo);

      nodeInfo.put("name", typeName);
      nodeInfo.put("DisplayName", typeName);
      nodeInfo.put("_key", typeName);
      NodeTypes.add(nodeInfo.toString());
      //NodeTypes.
    });

    connectArango.importDocuments2Arango(NodeTypes.toString(), arangoNodeTypesCollection);
    //connectArango.importDocuments2Arango2(NodeTypes, arangoNodeTypesCollection,"Document");
  }


}

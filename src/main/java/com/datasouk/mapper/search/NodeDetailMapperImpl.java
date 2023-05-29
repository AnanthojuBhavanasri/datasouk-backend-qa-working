package com.datasouk.mapper.search;

import com.datasouk.dto.search.*;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.models.arango.SourceRelation;
import com.datasouk.core.models.arango.TargetRelation;
import com.datasouk.core.repository.NodeRepository;
import com.datasouk.utils.arango.BuildAttribute;
import com.datasouk.utils.arango.BuildRelation;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class NodeDetailMapperImpl implements NodeDetailMapper {
    private final NodeRepository repository;
    private final BuildRelation buildRelation;
    private final BuildAttribute buildAttribute;
    private final NodeSearchMapperImpl nodeSearchMapperImpl;

    public MetaCollection nodeToMetaCollection(Node node) {

        MetaCollection bs = new MetaCollection();
        List<HashMap<String, String>> bisListData = new ArrayList<>();
        List<HashMap<String, String>> govListData = new ArrayList<>();
        HashMap<String, String> bisData = new HashMap<>();
        HashMap<String, String> govData = new HashMap<>();
        if (node == null) {
            return null;
        }

        node.getAttributes().forEach(attr -> {
            String name = attr.getName();
            String value = attr.getValue().toString();
            bisData.put(name, value);
        });
        List<TargetRelation> targets = node.getRelations().getTargets();
        List<SourceRelation> sources = node.getRelations().getSources();
        targets.forEach(target -> {
            String id = target.getTarget().getId();
            String name = target.getTarget().getName();
            String filterWithId =
                    " FILTER node.id==\"" + id + "\"";
            List<Node> nodes = repository.getNodes(filterWithId);
            List<String> metacollectionName = nodes.stream().
                    filter(n -> n.getType().getMetaCollectionName().equals("Business")).filter(n -> n.getType().getMetaCollectionName().equals("LogicalData")).filter(n -> n.getType().getMetaCollectionName().equals("Technology"))
                    .map(n -> n.getType().getMetaCollectionName()).
                    collect(Collectors.toList());
            if (!metacollectionName.isEmpty()) {
                bisData.put(name, metacollectionName.get(0));
            }

        });
        sources.forEach(source -> {
            String id = source.getSource().getId();
            String name = source.getSource().getName();
            String filterWithId =
                    " FILTER node.id=='" + id + "'";
            List<Node> nodes = repository.getNodes(filterWithId);
            // List<Node> nodes=repository.getNodeDetails(id);
            List<String> metacollectionName = nodes.stream().
                    filter(n -> n.getType().getMetaCollectionName().equals("Governance")).
                    map(n -> n.getType().getMetaCollectionName()).
                    collect(Collectors.toList());
            if (!metacollectionName.isEmpty()) {
                govData.put(name, metacollectionName.get(0));
            }
        });
        Set<String> k = bisData.keySet();
        Set<String> l = govData.keySet();
        bisListData.add(bisData);
        govListData.add(govData);
        bs.setBusinessMetaData(bisListData);
        bs.setGovernanceMetaData(govListData);
        bs.setBusinesskeys(k);
        bs.setGovernancekeys(l);
        return bs;
    }

    public OperationalMetaData nodeToOperationalData(Node node) {

        if (node == null) {
            return null;
        }
        OperationalMetaData od = new OperationalMetaData();
        if (!node.getDisplayName().isEmpty()) {
            List<Node> nodes = nodeSearchMapperImpl.getNodes(Arrays.asList(node.getDisplayName()));
            //String nodes = node.getDisplayName();
            String nodeType = "is part of";
            List<String> targetNames = buildRelation.getTargetNames(nodes, nodeType);
            System.out.print("targetNames" + targetNames);
            int columnCount1 = targetNames.size();
            String s = Integer.toString(columnCount1);
            od.setNumberOfElements(s);
            if (targetNames.isEmpty()) {
                od.setContentType(null);
            } else {
                od.setContentType(targetNames);
            }
        }
        return od;
    }

    public FrequencyAndFreshness nodeToFrequencyFreshness(Node node) {

        if (node == null) {
            return null;
        }
        FrequencyAndFreshness ff = new FrequencyAndFreshness();
        ff.setDateCreated(node.getCreatedOn());
        ff.setCurrentVersion("1.0");
        List<String> attributesFreshness = buildAttribute.getAttributesFilters(
                node.getAttributes(), "LastModifiedOn");
        System.out.println("attributesFreshness" + attributesFreshness);
        if (!attributesFreshness.isEmpty()) {
            ff.setLastModifiedOn(attributesFreshness.get(0));
        }
        return ff;
    }

    public Metric nodeMetric(Node node) {

        if (node == null) {
            return null;
        }
        Metric metricMetaData = new Metric();
        List<String> attributeValues=new ArrayList<>();


        //List<String> attributesMetrics = buildAttribute.buildAttributesMetric( node.getAttributes(),attributeValues,"Run");
        List<Object> attributesMetrics = buildAttribute.getNodeAttributesFilters(
                node.getAttributes(), "metrics");
        System.out.println("metrics" + attributesMetrics);

        List<HashMap<String, String>> metListData = new ArrayList<>();
        HashMap<String, String> metData = new HashMap<>();

        if (!attributesMetrics.isEmpty()) {

            for(int i=0;i<attributesMetrics.size();i++){
                JSONObject s=new JSONObject(attributesMetrics.get(i).toString());
                System.out.println("s"+s);
                String name=s.get("name").toString();
                String value=s.getString("value");
                metData.put("name",name);
                metData.put("value",value);
                metListData.add(metData);
            }
            metricMetaData.setMetricMetaData(metListData);     //setLastModifiedOn(attributesMetrics.get(0));
        }
        return metricMetaData;
    }

    public NodeParameter nodeParameter(Node node) {

        if (node == null) {
            return null;
        }
        NodeParameter parmMetaData = new NodeParameter();

        List<Object> attributesParams = buildAttribute.getNodeAttributesFilters(
                node.getAttributes(), "params");
        System.out.println("params" + attributesParams);
        List<HashMap<String, String>> metListData = new ArrayList<>();
        HashMap<String, String> metData = new HashMap<>();

        if (!attributesParams.isEmpty()) {

            for (int i = 0; i < attributesParams.size(); i++) {
                JSONObject s = new JSONObject(attributesParams.get(i).toString());
                System.out.println("s" + s);
                String name = s.get("name").toString();
                String value = s.getString("value");
                metData.put("name", name);
                metData.put("value", value);
                metListData.add(metData);
            }
            parmMetaData.setParmMetaData(metListData);     //setLastModifiedOn(attributesMetrics.get(0));
        }
        return parmMetaData;
    }
}
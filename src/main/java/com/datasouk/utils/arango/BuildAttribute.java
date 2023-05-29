package com.datasouk.utils.arango;

import com.datasouk.core.models.arango.Attribute;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.utils.Common;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BuildAttribute {

  private final Common common;

  private static boolean isDataSetOrSchema(String name) {
    return name.contains("Data Set") || name.contains("Schema");
  }

  private static boolean isNameType(String nameType, String nameValue) {
    return nameType != null && nameType.equals(nameValue);
  }

  public List<Integer> getAttributes(List<Node> nodeAttributes, String attributeType) {

    List<Integer> attributeResults = new ArrayList<>();
    nodeAttributes.forEach(nodeAttribute -> buildAttributesPassingFraction(nodeAttribute,
        attributeResults, attributeType));

    return attributeResults;
  }

  public List<String> getAttributesString(List<Node> nodeAttributes, String attributeType) {

    List<String> attributeResults = new ArrayList<>();
    nodeAttributes
        .forEach(nodeAttribute -> buildAttributes(nodeAttribute, attributeResults, attributeType));

    return attributeResults;
  }

  public List<String> getAttributesFiltersWithType(List<Attribute> nodeAttributes, String nameType,
      String attributeName) {
    List<String> attributeResults = new ArrayList<>();
    if (!isDataSetOrSchema(nameType)) {
      return attributeResults;
    }
    nodeAttributes.forEach(attribute -> {
      if (attribute != null && attribute.getName().equals(attributeName)) {
        String value = (String) attribute.getValue();
        attributeResults.add(value);
      }
    });
    return attributeResults;
  }

  public List<String> getAttributesFiltersWithDate(List<Attribute> nodeAttributes, String nameType,
      String attributeName) {
    List<String> attributeResults = new ArrayList<>();
    if (!isDataSetOrSchema(nameType)) {
      return attributeResults;
    }
    nodeAttributes.forEach(attribute -> {
      if (attribute != null && attribute.getName().equals(attributeName)) {
        String value = (String) attribute.getValue();
        //String getDate = common.convertLongToDateString(value, "yyyy-MM-dd HH:mm:ss");
        attributeResults.add(value);
      }
    });
    return attributeResults;
  }

  public List<Integer> getAttributesFiltersWithInteger(List<Attribute> nodeAttributes,
      String attributeType) {
    List<Integer> attributeValues = new ArrayList<>();

    nodeAttributes.forEach(attribute -> {
      String name = attribute.getName();
      if (name.equals(attributeType)) {
        String value = (String) attribute.getValue();
        int valueInt = Integer.valueOf(value);
        attributeValues.add(valueInt);
      }
    });
    return attributeValues;
  }

  public List<String> getAttributesFilters(List<Attribute> nodeAttributes,
      String attributeName) {

    List<String> attributeResults = new ArrayList<>();
    nodeAttributes.forEach(attribute -> {
      if (attribute != null && attribute.getName().equals(attributeName)) {
        String value = (String) attribute.getValue();
        attributeResults.add(value);
      }
    });
    return attributeResults;
  }


  public void buildAttributesPassingFraction(Node node, List<Integer> attributeValues,
      String attributeType) {
    List<Attribute> attributes = node.getAttributes();
    attributes.forEach(attribute -> {
      String name = attribute.getName();
      if (name.equals(attributeType)) {
        String value = (String) attribute.getValue();
        int valueInt = Integer.valueOf(value);
        attributeValues.add(valueInt);
      }
    });

  }

  public void buildAttributes(Node node, List<String> attributeValues, String attributeType) {
    List<Attribute> attributes = node.getAttributes();
    attributes.forEach(attribute -> {
      String name = attribute.getName();
      if (name.equals(attributeType)) {
        String value = (String) attribute.getValue();
        attributeValues.add(value);
      }
    });

  }

  public void buildAttributesCertified(Attribute attribute, List<String> attributeValues,
      String nameType) {

    String name = attribute.getName();
    if (name != null && name.equals("Certified")) {
      if (nameType.contains("Data Set") || nameType.contains("Schema")) {
        String value = (String) attribute.getValue();
        attributeValues.add(value);
      }
    }

  }
  public List<Object> getNodeAttributesFilters(List<Attribute> nodeAttributes,
                                               String attributeName) {

    List<Object> attributeResults = new ArrayList<>();
    JSONObject sx=new JSONObject();
    nodeAttributes.forEach(attribute -> {
      if (attribute != null && attribute.getName().equals(attributeName)) {
        List<HashMap> value = (List<HashMap>) attribute.getValue();
        System.out.println("value"+value);
        for(int i=0;i< value.size();i++){
          JSONObject s=new JSONObject(value.get(i));
          System.out.println("s"+s);
          String name=s.get("key").toString();
          String valu=s.get("value").toString();
          sx.put("name",name);
          sx.put("value",valu);
          attributeResults.add(sx);
        }

      }
    });
    return attributeResults;
  }

}

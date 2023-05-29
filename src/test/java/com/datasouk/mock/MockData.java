package com.datasouk.mock;

import com.datasouk.core.dto.request.Filters;
import com.datasouk.core.dto.request.Pagination;
import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.core.dto.request.PolicyPayload;
import com.datasouk.core.dto.request.SortFields;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.models.arango.Relation;
import com.datasouk.core.models.arango.RelationAttribute;
import com.datasouk.core.models.arango.SourceRelation;
import com.datasouk.core.models.arango.TargetRelation;
import com.datasouk.integration.dto.ranger.PolicyDto;
import com.datasouk.integration.dto.ranger.ServiceDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class MockData {

  public static PayLoad getPayload() {
    List<Filters> filters = new ArrayList<>();
    List<SortFields> sortFields = new ArrayList<>();
    SortFields sortField = new SortFields("doc.name", "ASC");
    sortFields.add(sortField);
    Filters filter = new Filters("doc.name", "Film Data", null, null, null, null, "STARTS_WITH",
        "SEARCH",
        0.0, null, null, null);
    Filters filter1 = new Filters("doc.name", "Film Data", null, null, "or", null, "NGRAM_MATCH",
        "SEARCH",
        0.4, null, null, null);
    filters.add(filter);
    filters.add(filter1);
    Pagination pagination = new Pagination(1, 50);
    PayLoad payLoad = new PayLoad(filters, pagination, sortFields);
    return payLoad;
  }

  public static Page<Node> getNodePage() {
    Pageable pageable = PageRequest.of(1, 5);
    Page<Node> nodePage = new PageImpl(getNodeResults(), pageable, 25);
    return nodePage;
  }

  public static Page<Node> getSearchCountNodePage() {
    //Add data set to node
    List<Node> nodes = getNodeResults();
    nodes.add(getDataSet());
    Pageable pageable = PageRequest.of(1, 1);
    Page<Node> nodePage = new PageImpl(nodes, pageable, 2);

    return nodePage;
  }

  public static List<Node> getNodeResults() {

    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/Node.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);
      readValue.getRelations().getTargets().get(0).setCoRole("contains");

      List<Node> nodes = new ArrayList<>();
      nodes.add(readValue);
      return nodes;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public static Node getDataSet() {

    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/DataSet.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);

      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public static Relation getRelations() {
    Relation relation = new Relation();
    SourceRelation sourceRelation = new SourceRelation();
    RelationAttribute sourceAttribute = new RelationAttribute();
    sourceAttribute.setId("79033d23-1bbf-4059-a5ef-ed765ffc5b1f");
    sourceAttribute.setDisplayName("view schema");
    sourceAttribute.setName("view schema");
    sourceAttribute.setType("Schema");
    sourceRelation.setId("bf0f77f1-84a9-4bb8-8734-900d93989387");
    sourceRelation.setSource(sourceAttribute);
    sourceRelation.setTypeId("00000000-0000-0000-0000-000000007043");
    sourceRelation.setRole("contains");

    List<SourceRelation> sourceRelations = new ArrayList<>();
    sourceRelations.add(sourceRelation);

    TargetRelation targetRelation = new TargetRelation();
    RelationAttribute targetAttribute = new RelationAttribute();
    targetAttribute.setId("9f6d8c58-0161-494e-8145-9d99eba298aa");
    targetAttribute.setDisplayName("netflix_titles.csv > country");
    targetAttribute.setName("netflix_titles.csv > country");
    targetAttribute.setType("Column");
    targetRelation.setId("d546dc01-158c-47aa-bee0-17b766ae682c");
    targetRelation.setTarget(targetAttribute);
    targetRelation.setTypeId("7d6f9686-061e-4feb-b3fc-25403232f65f");
    targetRelation.setCoRole("contains");

    List<TargetRelation> targetRelations = new ArrayList<>();
    targetRelations.add(targetRelation);

    relation.setSources(sourceRelations);
    relation.setTargets(targetRelations);
    return relation;
  }

  public static List<Node> getRepresentAsset() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/represents.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);
      readValue.getRelations().getTargets().get(2).setCoRole("contains");

      List<Node> nodes = new ArrayList<>();
      nodes.add(readValue);
      return nodes;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getClassifiesAsset() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/classifies.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);

      List<Node> nodes = new ArrayList<>();
      nodes.add(readValue);
      return nodes;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getCompliesToAsset() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/CompliesTo.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);

      readValue.getRelations().getTargets().get(1).setCoRole("executes");
      readValue.getRelations().getTargets().get(2).setCoRole("executes");
      readValue.getRelations().getTargets().get(3).setCoRole("executes");

      List<Node> nodes = new ArrayList<>();
      nodes.add(readValue);
      return nodes;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getExecutesAsset() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/Executes.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node[] readValue = mapper.readValue(responseStrBuilder.toString(), Node[].class);
      return Arrays.asList(readValue);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getFinanceNode() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/FinanceNode.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node[] readValue = mapper.readValue(responseStrBuilder.toString(), Node[].class);
      return Arrays.asList(readValue);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getPolicyPayload() {
    PolicyPayload policyPayload = new PolicyPayload("finance", "Database", "", "");
    StringBuilder filterQry = new StringBuilder();
    filterQry.append(" FILTER node.identifier==");
    filterQry.append("\"");
    filterQry.append(policyPayload.getIdentifier());
    filterQry.append("\"");
    filterQry.append(" and ");
    filterQry.append("\"");
    filterQry.append("Policy");
    filterQry.append("\"");
    filterQry.append(" IN ");
    filterQry.append(" node.relations.sources[*].source.type ");
    return filterQry.toString();
  }

  public static ServiceDto[] getRangerServices() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/RangerServices.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      ServiceDto[] readValue = mapper.readValue(responseStrBuilder.toString(), ServiceDto[].class);
      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static PolicyDto[] getRangerPolicySet1() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/RangerPolicySet1.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      PolicyDto[] readValue = mapper.readValue(responseStrBuilder.toString(), PolicyDto[].class);
      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static PolicyDto[] getRangerPolicySet2() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/RangerPolicySet2.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      PolicyDto[] readValue = mapper.readValue(responseStrBuilder.toString(), PolicyDto[].class);
      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static PolicyDto[] getRangerPolicySet3() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/RangerPolicySet3.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      PolicyDto[] readValue = mapper.readValue(responseStrBuilder.toString(), PolicyDto[].class);
      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Node getMappedPolicy() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/MappedPolicy.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);

      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Node getSavedPolicy() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/SavedPolicy.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);

      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Node getFinanceNodeWithSource() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/FinanceNodeWithSource.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);

      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Node getPolicyNode() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/PolicyNode.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);

      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static PolicyDto getRangerPolicyWithId() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/RangerPolicyWithId.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      PolicyDto readValue = mapper.readValue(responseStrBuilder.toString(), PolicyDto.class);

      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getNodeWithTable() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/NodeWithTable.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node[] readValue = mapper.readValue(responseStrBuilder.toString(), Node[].class);
      return Arrays.asList(readValue);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getNodeWithColumn() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/NodeWithColumn.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node[] readValue = mapper.readValue(responseStrBuilder.toString(), Node[].class);
      return Arrays.asList(readValue);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Node getMappedTablePolicy() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/MappedTablePolicy.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);
      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Node getMappedColumnPolicy() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/MappedColumnPolicy.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);
      return readValue;

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getNodePolicyControl1() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/NodePolicyControl1.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);
      return Arrays.asList(readValue);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getNodePolicyControl2() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/NodePolicyControl2.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);
      return Arrays.asList(readValue);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Node> getNodePolicyControl3() {
    try (Reader reader = new InputStreamReader(
        MockData.class.getResourceAsStream("/NodePolicyControl3.json"), "UTF-8")) {

      BufferedReader bR = new BufferedReader(reader);
      String line = "";

      StringBuilder responseStrBuilder = new StringBuilder();
      while ((line = bR.readLine()) != null) {
        responseStrBuilder.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Node readValue = mapper.readValue(responseStrBuilder.toString(), Node.class);
      return Arrays.asList(readValue);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

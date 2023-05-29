package com.datasouk.mapper.search;


import com.datasouk.core.models.arango.ActualDataNode;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.repository.DataUsageRepository;
import com.datasouk.core.repository.NodeRepository;
import com.datasouk.core.repository.ShoppingCartRepository;
import com.datasouk.core.repository.ViewsRepository;
import com.datasouk.core.utils.Common;
import com.datasouk.dto.search.ActualDataNodeDto;
import com.datasouk.dto.search.NodeSearchGetDto;
import com.datasouk.dto.search.ResponsibilitiesDto;
import com.datasouk.utils.arango.BuildAttribute;
import com.datasouk.utils.arango.BuildRelation;

import java.util.*;

import lombok.AllArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NodeSearchMapperImpl implements NodeSearchMapper {

  private final NodeRepository nodeRepository;
  private final ShoppingCartRepository shoppingCartRepository;

  private final ViewsRepository viewsRepository;

  private final DataUsageRepository dataUsageRepository;
  private final Common common;
  private final DozerBeanMapper mapper;

  private final BuildAttribute buildAttribute;
  private final BuildRelation buildRelation;


  @Override
  public NodeSearchGetDto nodeToNodeSearchGetDTO(Node node) {

    if (node == null) {
      return null;
    }
    NodeSearchGetDto nodeSearchGetDto = new NodeSearchGetDto();
    nodeSearchGetDto.setDisplayName(node.getDisplayName());
    String nameType = node.getName();

    if (nameType.trim().contains("Data Set") || nameType.trim().contains("Data Product")
        || nameType.trim().contains("API")) {
      nodeSearchGetDto.setAction("AddToCart");
    } else if (nameType.contains("Column") || nameType.contains("Schema")
        || nameType.contains("Table") || nameType.contains("Field") || nameType.contains("File")) {
      nodeSearchGetDto.setAction("AddToDataSet");
    } else {
      nodeSearchGetDto.setAction("Empty");
    }
    nodeSearchGetDto.setAttributes(node.getAttributes());
    nodeSearchGetDto.setAvgRating(node.getAvgRating());
    nodeSearchGetDto.setCreatedByFullName(node.getCreatedByFullName());
//    String createdOnDate =
//        common.convertLongToDateString(node.getCreatedOn(), "dd-MMM-yyyy HH:mm:ss");
    nodeSearchGetDto.setCreatedOn(node.getCreatedOn());

    String identifier = node.getIdentifier();
    if (identifier.contains("curated")) {
      nodeSearchGetDto.setCurated(true);
    } else {
      nodeSearchGetDto.setCurated(false);
    }

    List<String> attributesFrequency = buildAttribute.getAttributesFilters(
        node.getAttributes(), "Frequency");

    List<String> attributesFreshness = buildAttribute.getAttributesFiltersWithDate(
        node.getAttributes(),
        nameType, "LastModifiedOn");

    List<String> attributesPII = buildAttribute.getAttributesFilters(
        node.getAttributes(), "Personally Identifiable Information");

    List<String> securityClassification = buildAttribute.getAttributesFilters(
        node.getAttributes(), "Security Classification");

    List<Integer> attributePassingFractionVal = buildAttribute.getAttributesFiltersWithInteger(
        node.getAttributes(), "Passing Fraction");
    int sumPassingFractionVal = attributePassingFractionVal.stream().mapToInt(Integer::intValue)
        .sum();

    List<String> shoppable = buildAttribute.getAttributesFilters(
        node.getAttributes(), "Shoppable");

    List<String> searchable = buildAttribute.getAttributesFilters(
        node.getAttributes(), "searchable");

    List<String> url = buildAttribute.getAttributesFilters(
        node.getAttributes(), "url");

    List<String> definition = buildAttribute.getAttributesFilters(
        node.getAttributes(), "Definition");

    List<String> description = buildAttribute.getAttributesFilters(
        node.getAttributes(), "Description");

    if (!shoppable.isEmpty()) {
      nodeSearchGetDto.setShopable(shoppable.get(0));
    }

    if (!definition.isEmpty()) {
      nodeSearchGetDto.setDefinition(definition.get(0));
    }

    if (!url.isEmpty()) {
      nodeSearchGetDto.setUrl(url.get(0));
    }

    if (!searchable.isEmpty()) {
      nodeSearchGetDto.setSearchable(searchable.get(0));
    }

    if (!description.isEmpty()) {
      nodeSearchGetDto.setDescription(description.get(0));
    }

    if (!securityClassification.isEmpty()) {
      nodeSearchGetDto.setSecurityClassification(securityClassification.get(0));
    }
    if (!attributesPII.isEmpty()) {
      nodeSearchGetDto.setPersonallyIdentifiableInformation(attributesPII.get(0));
    }

    nodeSearchGetDto.setPassingFraction(sumPassingFractionVal);

    nodeSearchGetDto.setFreshness(attributesFreshness);

    nodeSearchGetDto.setFrequency(attributesFrequency);

    nodeSearchGetDto.setPercentage(node.getPercentage());

    // nodeSearchGetDto.setSecurityClassification(securityClassification);

    //nodeSearchGetDto.setPersonallyIdentifiableInformation(attributesPII);

    if (!node.getDisplayName().isEmpty()) {
      List<Node> nodes = getNodes(Arrays.asList(node.getDisplayName()));
      List<String> targetNames = buildRelation.getTargetNames(nodes, "is part of");
      List<String> sourceNames = buildRelation.getSourceNames(targetNames, "represents");
      List<String> sourceClassifyNames = buildRelation.getSourceNames(sourceNames, "classifies");
      List<String> sourceAssociateNames = buildRelation.getSourceNames(sourceNames, "associates");
      nodeSearchGetDto.setDataDomain(sourceClassifyNames);
      nodeSearchGetDto.setLineOfBusiness(sourceAssociateNames);
      // Build the quality score
      List<String> sourceCompliesToNames = buildRelation.getSourceNames(targetNames, "complies to");
      List<Node> nodeComplies = getNodes(sourceCompliesToNames);
      List<String> targetExecuteNames = buildRelation.getTargetNames(nodeComplies, "executes");
      List<Node> nodesAttribute = getNodes(targetExecuteNames);
      List<Integer> attributePassingFraction = buildAttribute.getAttributes(nodesAttribute,
          "Passing Fraction");
      int sumPassingFraction = attributePassingFraction.stream().mapToInt(Integer::intValue).sum();

      int qualityScore = 0;
      if (!targetExecuteNames.isEmpty()) {
        qualityScore = sumPassingFraction / targetExecuteNames.size();
      }
      nodeSearchGetDto.setQualityScore(qualityScore);
      // Build meta quality score
      System.out.println("nodes" + nodes);
      List<String> metaQualityIds = buildRelation.buildMetaQualityScore(nodes);
      System.out.println("metaQualityIds" + metaQualityIds);
      List<Node> nodesQuality = getNodesWithId(metaQualityIds);
      System.out.println("nodesQuality" + nodesQuality);
      List<String> attributesDescription = buildAttribute.getAttributesString(nodesQuality,
          "Description");
      if (!attributesDescription.isEmpty()) {
        nodeSearchGetDto.setDescription(attributesDescription.get(0));
      }

      List<String> attributeDescriptionTarget = buildRelation.getTargetNames(nodesQuality,
          "is part of");
      int metaQualityScore = 0;
      int attributesDescriptionCount = attributesDescription.size();
      int attributeDescriptionTargetCount = attributeDescriptionTarget.size();
      if (attributeDescriptionTargetCount != 0) {
        int k = 100;
        metaQualityScore = attributesDescriptionCount * k / attributeDescriptionTargetCount;
      }
      nodeSearchGetDto.setMetaQualityScore(metaQualityScore);
    }
    nodeSearchGetDto.setId(node.getId());
    nodeSearchGetDto.setIdentifier(node.getIdentifier());
    nodeSearchGetDto.setKey(node.getArangoId());

    //Tag
    List<String> attributesTags = buildAttribute.getAttributesFilters(node.getAttributes(), "tag");
    nodeSearchGetDto.setTags(attributesTags);

    List<ResponsibilitiesDto> responsibilities = new ArrayList<>();
    if (node.getResponsibilities() != null) {
      mapper.map(node.getResponsibilities(), responsibilities);
      nodeSearchGetDto.setResponsibilities(responsibilities);
    }

    if (node.getSourceCatalog() != null) {
      nodeSearchGetDto.setSourceSystem(node.getSourceCatalog());

    } else if (node.getSourceProduct() != null) {
      nodeSearchGetDto.setSourceSystem(node.getSourceProduct());
    }
    nodeSearchGetDto.setRatingsCount(node.getRatingsCount());

    if (node.getStatus() != null) {
      nodeSearchGetDto.setStatus(node.getStatus().getName());
    }

    nodeSearchGetDto.setType(node.getType().getName());
    List<Integer> favoriteCount = shoppingCartRepository.getCount(node.getArangoId());
    nodeSearchGetDto.setCount(favoriteCount.isEmpty() ? 0 : favoriteCount.get(0));

    List<Integer> requestCount = dataUsageRepository.getRequestCount(node.getId());
    nodeSearchGetDto.setRequestCount(requestCount.isEmpty() ? 0 : requestCount.get(0));

    List<Integer> viewsCount = viewsRepository.getViewCount(node.getId());
    nodeSearchGetDto.setViewCount(viewsCount.isEmpty() ? 0 : viewsCount.get(0));

    return nodeSearchGetDto;

  }


  public List<Node> getNodes(List<String> filters) {
    String searchType = " FILTER node.name in [" + common.arrayToStringWithComma(filters) + "]";
    return nodeRepository.getNodes(searchType);
  }

  public Page<Node> getSourceNodes(List<String> filters, Pageable pageable) {
    String searchType = " FILTER node.name in [" + common.arrayToStringWithComma(filters) + "]";
    return nodeRepository.getSourceNodes(searchType, pageable);
  }

  public List<Node> getNodesWithId(List<String> filters) {
    String searchType = " FILTER node.id in [" + common.arrayToStringWithComma(filters) + "]";
    return nodeRepository.getNodes(searchType);
  }


  @Override
  public NodeSearchGetDto nodeToNodeSearchCount(Node node) {

    NodeSearchGetDto nodeSearchGetDto = new NodeSearchGetDto();
    String nameType = node.getType().getName();
    nodeSearchGetDto.setType(node.getType().getName());
    String identifier = node.getIdentifier();
    if (identifier.contains("curated")) {
      nodeSearchGetDto.setCurated(true);
    } else {
      nodeSearchGetDto.setCurated(false);
    }
    List<String> attributesCertified = buildAttribute.getAttributesFiltersWithType(
        node.getAttributes(),
        nameType, "Certified");
    List<String> attributesFrequency = buildAttribute.getAttributesFiltersWithType(
        node.getAttributes(),
        nameType, "Frequency");
    List<String> attributesTags = buildAttribute.getAttributesFilters(node.getAttributes(), "tag");
    List<String> attributesFreshness = buildAttribute.getAttributesFiltersWithDate(
        node.getAttributes(),
        nameType, "LastModifiedOn");

    nodeSearchGetDto.setCertified(attributesCertified);

    if (node.getStatus() != null) {
      nodeSearchGetDto.setStatus(node.getStatus().getName());
    }

    nodeSearchGetDto.setAvgRating(node.getAvgRating());
    nodeSearchGetDto.setFrequency(attributesFrequency);
    nodeSearchGetDto.setTags(attributesTags);
    nodeSearchGetDto.setFreshness(attributesFreshness);

    if (!node.getDisplayName().isEmpty()) {
      List<Node> nodes = getNodes(Arrays.asList(node.getDisplayName()));
      List<String> targetNames = buildRelation.getTargetNames(nodes, "contains");
      List<String> sourceNames = buildRelation.getSourceNames(targetNames, "represents");
      List<String> sourceClassifyNames = buildRelation.getSourceNames(sourceNames, "classifies");
      List<String> sourceAssociateNames = buildRelation.getSourceNames(sourceNames, "associates");
      nodeSearchGetDto.setDataDomain(sourceClassifyNames);
      nodeSearchGetDto.setLineOfBusiness(sourceAssociateNames);
    }
    return nodeSearchGetDto;
  }


  public ActualDataNodeDto ActualDataNodeDto(ActualDataNode actualDataNode) {
    ActualDataNodeDto actualDataNodeDto = new ActualDataNodeDto();
    List<HashMap<String, String>> bisListData = new ArrayList<>();

    HashMap<String, String> bisData = new HashMap<>();

    bisData.put("customer_id",actualDataNode.getCustomer_id());
    bisData.put("store_id",actualDataNode.getStore_id());
    bisData.put("first_name",actualDataNode.getFirst_name());
    bisData.put("last_name",actualDataNode.getLast_name());
    bisData.put("email",actualDataNode.getEmail());
    bisData.put("address_id",actualDataNode.getAddress_id());
    bisData.put("activebool",actualDataNode.getActivebool());
    bisData.put("create_date",actualDataNode.getCreate_date());
    bisData.put("last_update",actualDataNode.getLast_update());
    bisData.put("active",actualDataNode.getActive());
    bisData.keySet();
    actualDataNodeDto.setKeys(bisData.keySet());
    return actualDataNodeDto;
  }
}

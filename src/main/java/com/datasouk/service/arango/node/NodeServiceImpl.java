package com.datasouk.service.arango.node;


import com.datasouk.core.dto.request.ApiModelPageAndSort;
import com.datasouk.core.dto.request.Filters;
import com.datasouk.core.dto.request.Pagination;
import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.ActualDataNode;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.models.arango.RelationAttribute;
import com.datasouk.core.models.arango.SourceRelation;
import com.datasouk.core.repository.MlQuery;
import com.datasouk.core.repository.NodeRepository;
import com.datasouk.dto.search.*;
import com.datasouk.mapper.search.NodeDetailMapperImpl;
import com.datasouk.mapper.search.NodeSearchMapperImpl;
import com.datasouk.utils.arango.BuildRelation;
import com.datasouk.utils.arango.QueryBuilder;
import com.datasouk.utils.dto.request.PinPagination;
import com.datasouk.utils.dto.request.PinPayload;
import com.datasouk.utils.pagination.PageBuilder;
import com.datasouk.utils.pagination.PageRequestBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class NodeServiceImpl implements NodeService {

  private static Logger logger = LoggerFactory.getLogger(NodeServiceImpl.class);
  private static int pageLimit = 50;
  private final NodeRepository repository;
  private final QueryBuilder queryBuilder;
  private final NodeSearchMapperImpl nodeSearchMapperImpl;
  private final PageBuilder pageBuilder;
  private final MlQuery mlQuery;
  private final NodeDetailMapperImpl nodeDetailMapper;
  private final BuildRelation buildRelation;


  @Override
  public NodeSearchResponse search(PayLoad payLoad) throws ServiceException {
    Pagination pagination = payLoad.getPagination();
    logger.info("payLoad : " + payLoad);

    PageRequest pageRequest =
        PageRequestBuilder.getPageRequest(pagination, payLoad.getSortFields());

    if (payLoad.getFilters() == null) {
      return null;
    }

    List<String> response = new ArrayList<String>();
    for (int i = 0; i < payLoad.getFilters().size(); i++) {
      Filters filter = payLoad.getFilters().get(i);
      response = mlQuery.mlView(filter.getFieldValue());
    }

    //Todo Need to verify from ajitha
    //Apply the filters top of the nodeViews
    List<String> mlValues = new ArrayList<>();
    for (int j = 0; j < response.size(); j++) {
      /*Filters ml = Filters.builder().fieldName("doc.name").fieldValue(response.get(j))
          .condition("or").searchType("FILTER_ML").op("eq").build();
      payLoad.getFilters().add(ml);*/
      mlValues.add(response.get(j));
    }
    Filters ml = Filters.builder().fieldName("doc.name").fieldValue(String.join(",", mlValues))
        .searchType("FILTER_ML").op("in").build();
    //payLoad.getFilters().add(ml);
    String queryParams = queryBuilder.build(payLoad.getFilters());
    logger.info("queryParams : " + queryParams);

    List<String> sortValue = new ArrayList<>();
    String sortKey = "SORT ";
    if (payLoad.getSortFields() != null) {
      List<Filters> filterType = payLoad.getFilters().stream()
          .filter(d -> d.getSearchType() != null && d.getSearchType().contains("PHRASE"))
          .collect(
              Collectors.toList());

      if (filterType.size() != 0) {
        sortValue.add(sortKey + " BM25(doc) DESC ");
        sortKey = "";
      }
      sortValue.add(
          sortKey + payLoad.getSortFields().get(0).getField() + " " + payLoad.getSortFields().get(0)
              .getSort());
    } else {
      // sortValue = "SORT doc.type.name == \"Data Set\" ? 1 : doc.type.name == \"Data Product\" ? 2 : doc.type.name == \"Schema\" ? 3 : 4";
      List<Filters> filterType = payLoad.getFilters().stream()
          .filter(d -> d.getSearchType() != null && d.getSearchType().contains("PHRASE"))
          .collect(
              Collectors.toList());
      if (filterType.size() != 0) {
        sortValue.add(sortKey + " BM25(doc) DESC ");
        sortKey = "";
      }
      sortValue.add(
          sortKey
              + "doc.type.name == \"Data Product\" ? 1 : doc.type.name == \"Data Set\" ? 2 : doc.type.name == \"Reports\" ? 3 : doc.type.name == \"Schema\" ? 4 : doc.type.name == \"File\" ? 5 : 6");
    }
    Page<Node> nodePage = repository.getNodesView(pageRequest, queryParams,
        String.join(",", sortValue));

    List<NodeSearchGetDto> nodeSearchData = nodePage.getContent().stream()
        .map(nodeSearchMapperImpl::nodeToNodeSearchGetDTO).collect(Collectors.toList());

    ApiModelPageAndSort apiModelPageAndSort =
        pageBuilder.build(nodePage.isLast(), nodePage.isFirst(), nodePage.getTotalPages(),
            nodePage.getTotalElements(), pageRequest.getPageNumber(), pageRequest.getPageSize());
    return NodeSearchResponse.builder().paging(apiModelPageAndSort).data(nodeSearchData).build();


  }

  @Override
  public SearchResultsCount searchFilters(PayLoad payLoad) throws ServiceException {

    int i = 1;
    PageRequest pageRequest =
        PageRequestBuilder.getPageRequest(new Pagination(i, pageLimit), payLoad.getSortFields());
    String queryParams = queryBuilder.build(payLoad.getFilters());

    String sortValue = "SORT doc.type.name == \"Data Set\" ? 1 : doc.type.name == \"Data Product\" ? 2 : doc.type.name == \"Schema\" ? 3 : 4";
    Page<Node> nodePage = repository.getNodesView(pageRequest, queryParams, sortValue);
    List<NodeSearchGetDto> nodeSearchDataAll = new ArrayList<>();
    List<NodeSearchGetDto> nodeSearchData = nodePage.getContent().stream()
        .map(nodeSearchMapperImpl::nodeToNodeSearchCount).collect(Collectors.toList());
    nodeSearchDataAll.addAll(nodeSearchData);

    while (!nodePage.isLast()) {
      i++;
      pageRequest =
          PageRequestBuilder.getPageRequest(new Pagination(i, pageLimit), payLoad.getSortFields());
      queryParams = queryBuilder.build(payLoad.getFilters());
      System.out.println("queryParams" + queryParams);
      nodePage = repository.getNodesView(pageRequest, queryParams, sortValue);
      nodeSearchData = nodePage.getContent().stream()
          .map(nodeSearchMapperImpl::nodeToNodeSearchCount).collect(Collectors.toList());
      nodeSearchDataAll.addAll(nodeSearchData);
    }

    //  Map<String, Long> nodeTypeGroup = nodeSearchDataAll.stream().filter(d -> d.getType() != null)
    //  .collect(Collectors.groupingBy(NodeSearchGetDto::getType, Collectors.counting()));

    Map<String, Long> statusGroup = nodeSearchDataAll.stream().filter(p -> p.getStatus() != null)
        .collect(Collectors.groupingBy(NodeSearchGetDto::getStatus, Collectors.counting()));

    Map<String, Long> avgRatingGroup =
        nodeSearchDataAll.stream().filter(p -> p.getAvgRating() != null)
            .collect(Collectors.groupingBy(NodeSearchGetDto::getAvgRating, Collectors.counting()));

    Map<Boolean, Long> curatedGroup = nodeSearchDataAll.stream()
        .collect(Collectors.groupingBy(NodeSearchGetDto::isCurated, Collectors.counting()));

    // List<SearchFieldValue> nodeTypeFieldList = new ArrayList<>();
    List<SearchFieldValue> statusFieldList = new ArrayList<>();
    List<SearchFieldValue> avarageRatingFieldList = new ArrayList<>();
    List<SearchFieldValue> curatedFieldList = new ArrayList<>();
    List<SearchFieldValue> certifiedFieldList = new ArrayList<>();
    List<SearchFieldValue> frequencyFieldList = new ArrayList<>();
    List<SearchFieldValue> tagFieldList = new ArrayList<>();
    List<SearchFieldValue> freshnessFieldList = new ArrayList<>();
    List<SearchFieldValue> lobFieldList = new ArrayList<>();
    List<SearchFieldValue> dataDomainFieldList = new ArrayList<>();

    Map<String, Long> groupCertified = new HashMap<>();
    Map<String, Long> groupFrequency = new HashMap<>();
    Map<String, Long> groupTags = new HashMap<>();
    Map<String, Long> groupFreshness = new HashMap<>();
    Map<String, Long> groupLOB = new HashMap<>();
    Map<String, Long> groupDataDomain = new HashMap<>();

    for (NodeSearchGetDto data : nodeSearchDataAll) {

      // Group Certified
      groupData(data.getCertified(), groupCertified);
      // Group Frequency
      groupData(data.getFrequency(), groupFrequency);
      // Group Tags
      groupData(data.getTags(), groupTags);

      // Group Freshness
      groupData(data.getFreshness(), groupFreshness);

      // Group Line of Business
      groupData(data.getLineOfBusiness(), groupLOB);

      // Group Data Domain
      groupData(data.getDataDomain(), groupDataDomain);

    }

    //groupFieldList(nodeTypeGroup, nodeTypeFieldList);

    groupFieldList(avgRatingGroup, avarageRatingFieldList);

    groupFieldList(statusGroup, statusFieldList);

    groupBooleanFieldList(curatedGroup, curatedFieldList);

    groupFieldList(groupCertified, certifiedFieldList);

    groupFieldList(groupFrequency, frequencyFieldList);

    groupFieldList(groupTags, tagFieldList);

    groupFieldList(groupFreshness, freshnessFieldList);

    groupFieldList(groupLOB, lobFieldList);

    groupFieldList(groupDataDomain, dataDomainFieldList);

    SearchResultsCount.SearchResultsCountBuilder builder = SearchResultsCount.builder();
    //builder.nodeType(nodeTypeFieldList);
    builder.status(statusFieldList);
    builder.averageRating(avarageRatingFieldList);
    builder.curated(curatedFieldList);
    builder.certifed(certifiedFieldList);
    builder.frequency(frequencyFieldList);
    builder.tags(tagFieldList);
    builder.freshness(freshnessFieldList);
    builder.lineOfBusiness(lobFieldList);
    builder.dataDomain(dataDomainFieldList);

    return builder.build();


  }

  @Override
  public List<Node> getNodes(String searchType) {
    return repository.getNodes(searchType);
  }


  public void groupData(List<String> dataResults, Map<String, Long> groupData) {
    // Group Data
    Map<String, Long> dataValues = dataResults.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    for (Map.Entry<String, Long> entry : dataValues.entrySet()) {
      if (groupData.containsKey(entry.getKey())) {
        groupData.put(entry.getKey(), groupData.get(entry.getKey()) + entry.getValue());
      } else {
        groupData.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public void groupFieldList(Map<String, Long> groupResults, List<SearchFieldValue> fieldList) {
    for (Map.Entry<String, Long> entry : groupResults.entrySet()) {
      fieldList.add(new SearchFieldValue(entry.getKey(), entry.getValue()));
    }
  }

  public void groupBooleanFieldList(Map<Boolean, Long> groupResults,
      List<SearchFieldValue> fieldList) {
    for (Map.Entry<Boolean, Long> entry : groupResults.entrySet()) {
      fieldList.add(new SearchFieldValue(String.valueOf(entry.getKey()), entry.getValue()));
    }
  }

  public void updateSourceRelation(List<Node> assetDetails, Node policy) {
    if (!assetDetails.isEmpty()) {
      if (assetDetails.get(0)
          .getRelations().getSources().isEmpty()) {
        SourceRelation sourceRelation = new SourceRelation();
        sourceRelation.setRole("applies to");
        sourceRelation.setTypeId("D0000000-0000-0000-0000-B00000000011");
        sourceRelation.setId(String.valueOf(UUID.randomUUID()));
        RelationAttribute relationAttribute = new RelationAttribute();
        relationAttribute.setName(policy.getName());
        relationAttribute.setDisplayName(policy.getName());
        relationAttribute.setId(policy.getId());
        relationAttribute.setType(policy.getType().getName());
        sourceRelation.setSource(relationAttribute);
        List<SourceRelation> sourceRelations = new ArrayList<>();
        sourceRelations.add(sourceRelation);
        assetDetails.get(0).getRelations().setSources(sourceRelations);

      } else {
        SourceRelation sourceRelation = new SourceRelation();
        sourceRelation.setRole("applies to");
        sourceRelation.setTypeId("D0000000-0000-0000-0000-B00000000011");
        sourceRelation.setId(String.valueOf(UUID.randomUUID()));
        RelationAttribute relationAttribute = new RelationAttribute();
        relationAttribute.setName(policy.getName());
        relationAttribute.setDisplayName(policy.getName());
        relationAttribute.setId(policy.getId());
        relationAttribute.setType(policy.getType().getName());
        sourceRelation.setSource(relationAttribute);
        assetDetails.get(0).getRelations().getSources().add(sourceRelation);
      }
      //Update the relation between node to policy
      repository.save(assetDetails.get(0));
    }
  }


  @Override
  public NodeDetailSearchResponse nodeInfo(String id) throws ServiceException {



    String filterWithId =
            " FILTER node.id=='" + id + "'";
    List<Node> nodes=repository.getNodes(filterWithId);
    MetaCollection meta = null;
    OperationalMetaData opdata = null;
    FrequencyAndFreshness freshnessdata = null;
    Metric metric = null;
    NodeSearchGetDto nodeSearchData =null;
    NodeParameter nodeParameter=null;

    if(!nodes.isEmpty()) {
      nodeSearchData = nodeSearchMapperImpl.nodeToNodeSearchGetDTO(nodes.get(0));
      String sourceCatalog=nodeSearchData.getSourceSystem();
      if(sourceCatalog.equals("MLFlow")){
        meta = nodeDetailMapper.nodeToMetaCollection(nodes.get(0));
        System.out.println(meta);
        metric=nodeDetailMapper.nodeMetric(nodes.get(0));
        nodeParameter=nodeDetailMapper.nodeParameter(nodes.get(0));
      }else {
        meta = nodeDetailMapper.nodeToMetaCollection(nodes.get(0));
        System.out.println(meta);
        opdata = nodeDetailMapper.nodeToOperationalData(nodes.get(0));

        System.out.println(opdata);
        freshnessdata = nodeDetailMapper.nodeToFrequencyFreshness(nodes.get(0));
        System.out.println(freshnessdata);
      }
    }

    return NodeDetailSearchResponse.builder().metric(metric).nodeParameter(nodeParameter).metaCollection(meta).operationalMetaData(opdata).frequencyAndFreshness(freshnessdata).nodeInfo(nodeSearchData).build();

  }


  public NodeSearchResponse nodeSourceSystemInfo(PinPayload payLoad) {

    String value = payLoad.getKey();
    PinPagination pagination = payLoad.getPagination();
    PageRequest pageRequest =
        PageRequestBuilder.getPageRequest(pagination);
    String filterWithId =
        " FILTER node.displayName=='" + value + "'";
    List<Node> nodes = repository.getNodes(filterWithId);
    List<String> targetNames = buildRelation.getTargetNames(nodes, "is part of");
    List<Node> nodesinfo = nodeSearchMapperImpl.getNodes(targetNames);
    List<String> targeNames = buildRelation.getTargetNames(nodesinfo, "is part of");

    Page<Node> nodeinfo = nodeSearchMapperImpl.getSourceNodes(targeNames, pageRequest);
    List<NodeSearchGetDto> nodeSearchLData = nodeinfo.stream()
        .map(nodeSearchMapperImpl::nodeToNodeSearchGetDTO).collect(Collectors.toList());
    ApiModelPageAndSort apiModelPageAndSort =
        pageBuilder.build(nodeinfo.isLast(), nodeinfo.isFirst(), nodeinfo.getTotalPages(),
            nodeinfo.getTotalElements(), pageRequest.getPageNumber(), pageRequest.getPageSize());
    return NodeSearchResponse.builder().paging(apiModelPageAndSort).data(nodeSearchLData).build();
  }

  public ActualNodeResponse nodeExamplesInfo(String value) {

    String filterWithId =
            " FILTER node._key=='" + value + "'";
    List<ActualDataNode> nodes = repository.getActualData(filterWithId);
    List<ActualDataNodeDto> nodeSearchLData = nodes.stream()
            .map(nodeSearchMapperImpl::ActualDataNodeDto).collect(Collectors.toList());
    return ActualNodeResponse.builder().actualDataNode(nodes).actualDataNodeDto(nodeSearchLData.get(0)).build();
  }

  public NodeSearchResponse  nodeProgressInfo(PinPayload payLoad) {

    String value = payLoad.getKey();
    PinPagination pagination = payLoad.getPagination();
    PageRequest pageRequest =
            PageRequestBuilder.getPageRequest(pagination);
    String filterWithId;
    String count;
    if(value.equals("Data Set")) {
      filterWithId =" FILTER i.type.name == '"+value+"' AND i.sourceCatalog == \"Datasouk\"";
             // " FILTER node._key=='" + value + "'";
      count="22";
    }else{
      filterWithId =
              "FILTER i.type.name == '"+value+"' AND i.sourceCatalog == \"Datasouk\"";
      count="22";
    }
    Page<Node> nodes=repository.getProgressNodes(count,filterWithId,pageRequest);
    List<NodeSearchGetDto> nodeSearchLData = nodes.stream()
            .map(nodeSearchMapperImpl::nodeToNodeSearchGetDTO).collect(Collectors.toList());
    ApiModelPageAndSort apiModelPageAndSort =
            pageBuilder.build(nodes.isLast(), nodes.isFirst(), nodes.getTotalPages(),
                    nodes.getTotalElements(), pageRequest.getPageNumber(), pageRequest.getPageSize());
    return NodeSearchResponse.builder().paging(apiModelPageAndSort).data(nodeSearchLData).build();
    //return nodeSearchLData;
  }
}


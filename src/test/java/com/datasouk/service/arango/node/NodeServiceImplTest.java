//package com.datasouk.service.arango.node;
//
//import static com.datasouk.mock.MockData.getClassifiesAsset;
//import static com.datasouk.mock.MockData.getCompliesToAsset;
//import static com.datasouk.mock.MockData.getExecutesAsset;
//import static com.datasouk.mock.MockData.getFinanceNode;
//import static com.datasouk.mock.MockData.getNodePage;
//import static com.datasouk.mock.MockData.getNodeResults;
//import static com.datasouk.mock.MockData.getPayload;
//import static com.datasouk.mock.MockData.getPolicyPayload;
//import static com.datasouk.mock.MockData.getRepresentAsset;
//import static com.datasouk.mock.MockData.getSearchCountNodePage;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import com.datasouk.dto.search.NodeSearchResponse;
//import com.datasouk.dto.search.SearchResultsCount;
//import com.datasouk.mapper.search.NodeSearchMapperImpl;
//import com.datasouk.models.arango.Node;
//import com.datasouk.repository.NodeRepository;
//import com.datasouk.repository.ShoppingCartRepository;
//import com.datasouk.service.arango.connect.ConnectArango;
//import com.datasouk.utils.Common;
//import com.datasouk.utils.arango.BuildAttribute;
//import com.datasouk.utils.arango.BuildRelation;
//import com.datasouk.utils.arango.QueryBuilder;
//import com.datasouk.utils.dto.request.PayLoad;
//import com.datasouk.utils.pagination.PageBuilder;
//import com.datasouk.utils.pagination.PageRequestBuilder;
//import java.util.Arrays;
//import java.util.List;
//import org.dozer.DozerBeanMapper;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.data.domain.PageRequest;
//
//@TestInstance(PER_CLASS)
////@ActiveProfiles("test")
//@Tag("UnitTest")
//@DisplayName("Node Service Unit Tests")
//public class NodeServiceImplTest {
//
//
//  private NodeRepository nodeRepository;
//  private QueryBuilder queryBuilder;
//  private NodeServiceImpl nodeService;
//
//  private ConnectArango connectArango;
//  private Common common = new Common();
//
//
//  @BeforeAll
//  public void init() {
//
//    nodeRepository = mock(NodeRepository.class);
//
//    ShoppingCartRepository shoppingCartRepository = mock(ShoppingCartRepository.class);
//    connectArango = mock(ConnectArango.class);
//    queryBuilder = new QueryBuilder(connectArango);
//    Common common = new Common();
//    DozerBeanMapper mapper = new DozerBeanMapper();
//    BuildAttribute buildAttribute = new BuildAttribute(common);
//    BuildRelation buildRelation = new BuildRelation(nodeRepository, common);
//    NodeSearchMapperImpl nodeSearchMapperImpl = new NodeSearchMapperImpl(nodeRepository,
//        shoppingCartRepository, common,
//        mapper, buildAttribute, buildRelation);
//    PageBuilder pageBuilder = new PageBuilder();
//
//    nodeService = new NodeServiceImpl(nodeRepository, queryBuilder, nodeSearchMapperImpl,
//        pageBuilder);
//
//  }
//
//  @Test
//  @DisplayName("Given filtered with pagination data, when search criteria with all required fields, then Search response is returned")
//  void searchTest() {
//    PayLoad payload = getPayload();
//
//    PageRequest pageRequest =
//        PageRequestBuilder.getPageRequest(payload.getPagination(), payload.getSortFields());
//    String queryParams = queryBuilder.build(payload.getFilters());
//
//    when(nodeRepository.getNodesView(pageRequest, queryParams)).thenReturn(getNodePage());
//
//    //Mock the get nodes with display name
//    String filters = " FILTER node.name in [" + common.arrayToStringWithComma(
//        Arrays.asList("user table")) + "]";
//    when(nodeRepository.getNodes(filters)).thenReturn(getNodeResults());
//
//    //Mock Represent node details
//    String searchType =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("netflix_titles.csv > country")) + "]";
//    when(nodeRepository.getNodes(searchType)).thenReturn(getRepresentAsset());
//
//    //Mock Classifies node details
//    String classifiesFilter =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("Assets")) + "]";
//    when(nodeRepository.getNodes(classifiesFilter)).thenReturn(getClassifiesAsset());
//
//    //Mock Associates node details
//    String associatesFilter =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("Assets")) + "]";
//    when(nodeRepository.getNodes(associatesFilter)).thenReturn(getClassifiesAsset());
//
//    //Mock Complies To Relation
//    String compliesToRelationFilter =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("netflix_titles.csv > country")) + "]";
//    when(nodeRepository.getNodes(compliesToRelationFilter)).thenReturn(getRepresentAsset());
//
//    //Mock Complies To node details
//    String compliesToFilter =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("eff-dt must be mmddyy")) + "]";
//    when(nodeRepository.getNodes(compliesToFilter)).thenReturn(getCompliesToAsset());
//
//    //Mock execute node details
//    String executeAssetFilters = " FILTER node.name in [" + common.arrayToStringWithComma(
//        Arrays.asList("adr_dt", "eff_dt must be mmddyy", "mnt_dt")) + "]";
//    when(nodeRepository.getNodes(executeAssetFilters)).thenReturn(getExecutesAsset());
//
//    //Mock get node details with id
//    String idFilters = " FILTER node.id in [" + common.arrayToStringWithComma(
//        Arrays.asList("9f6d8c58-0161-494e-8145-9d99eba298aa")) + "]";
//    when(nodeRepository.getNodes(idFilters)).thenReturn(getRepresentAsset());
//
//    NodeSearchResponse nodeSearchResponse = nodeService.search(payload);
//    assertEquals(nodeSearchResponse.getData().get(0).getDisplayName(), "user table");
//
//  }
//
//  @Test
//  @DisplayName("Given filtered conditions, when search criteria with all required fields, then search filter response is returned")
//  void searchFiltersTest() {
//
//    PayLoad payload = getPayload();
//    PageRequest pageRequest =
//        PageRequestBuilder.getPageRequest(payload.getPagination(), payload.getSortFields());
//    String queryParams = queryBuilder.build(payload.getFilters());
//
//    when(nodeRepository.getNodesView(pageRequest, queryParams)).thenReturn(
//        getSearchCountNodePage());
//
//    //Mock the get nodes with display name
//    String filters = " FILTER node.name in [" + common.arrayToStringWithComma(
//        Arrays.asList("user table")) + "]";
//    when(nodeRepository.getNodes(filters)).thenReturn(getNodeResults());
//
//    //Mock Represent node details
//    String searchType =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("netflix_titles.csv > country")) + "]";
//    when(nodeRepository.getNodes(searchType)).thenReturn(getRepresentAsset());
//
//    //Mock Classifies node details
//    String classifiesFilter =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("Assets")) + "]";
//    when(nodeRepository.getNodes(classifiesFilter)).thenReturn(getClassifiesAsset());
//
//    //Mock Associates node details
//    String associatesFilter =
//        " FILTER node.name in [" + common.arrayToStringWithComma(
//            Arrays.asList("Assets")) + "]";
//    when(nodeRepository.getNodes(associatesFilter)).thenReturn(getClassifiesAsset());
//
//    SearchResultsCount nodeSearchResponse = nodeService.searchFilters(payload);
//
//    //Verify the node type
//    assertEquals(nodeSearchResponse.getNodeType().get(0).getLabel(), "Table");
//    assertEquals(nodeSearchResponse.getNodeType().get(0).getValue(), 1);
//    assertEquals(nodeSearchResponse.getNodeType().get(1).getLabel(), "Data Set");
//    assertEquals(nodeSearchResponse.getNodeType().get(1).getValue(), 1);
//
//    //Verify the status
//    assertEquals(nodeSearchResponse.getStatus().get(0).getLabel(), "Candidate");
//    assertEquals(nodeSearchResponse.getStatus().get(0).getValue(), 1);
//    assertEquals(nodeSearchResponse.getStatus().get(1).getLabel(), "In Progress");
//    assertEquals(nodeSearchResponse.getStatus().get(1).getValue(), 1);
//
//    //Verify the average rating
//    assertEquals(nodeSearchResponse.getAverageRating().get(0).getLabel(), "0.0");
//    assertEquals(nodeSearchResponse.getAverageRating().get(0).getValue(), 1);
//    assertEquals(nodeSearchResponse.getAverageRating().get(1).getLabel(), "2.0");
//    assertEquals(nodeSearchResponse.getAverageRating().get(1).getValue(), 1);
//
//    //Verify the curated
//    assertEquals(nodeSearchResponse.getCurated().get(0).getLabel(), "false");
//    assertEquals(nodeSearchResponse.getCurated().get(0).getValue(), 2);
//
//    //Verify the certified
//    assertEquals(nodeSearchResponse.getCertifed().get(0).getLabel(), "true");
//    assertEquals(nodeSearchResponse.getCertifed().get(0).getValue(), 1);
//
//    //Verify the frequency
//    assertEquals(nodeSearchResponse.getFrequency().get(0).getLabel(), "Quarterly");
//    assertEquals(nodeSearchResponse.getFrequency().get(0).getValue(), 1);
//
//    //Verify the tags
//    assertEquals(nodeSearchResponse.getTags().get(0).getLabel(), "tag1");
//    assertEquals(nodeSearchResponse.getTags().get(0).getValue(), 1);
//
//    //Verify the freshness
//    //assertEquals(nodeSearchResponse.getFreshness().get(0).getLabel(), "2021-07-04 05:30:00");
//    //assertEquals(nodeSearchResponse.getFreshness().get(0).getValue(), 1);
//
//    //Verify the line of business
//    assertEquals(nodeSearchResponse.getLineOfBusiness().get(0).getLabel(), "Owner");
//    assertEquals(nodeSearchResponse.getLineOfBusiness().get(0).getValue(), 1);
//
//    //Verify the data domain
//    assertEquals(nodeSearchResponse.getDataDomain().get(0).getLabel(), "employees");
//    assertEquals(nodeSearchResponse.getDataDomain().get(0).getValue(), 1);
//  }
//
//
//  @Test
//  @DisplayName("Given search filters and returned nodes results")
//  void getNodesTest() {
//    String filters = getPolicyPayload();
//    when(nodeRepository.getNodes(filters)).thenReturn(getFinanceNode());
//    List<Node> nodeList = nodeService.getNodes(filters);
//    assertEquals(nodeList.get(0).getIdentifier(),
//        "finance");
//
//  }
//
//}

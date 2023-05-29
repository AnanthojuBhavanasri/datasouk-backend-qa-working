package com.datasouk.service.arango.node;

import com.datasouk.core.dto.request.ApiModelPageAndSort;
import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.models.arango.PinCollection;
import com.datasouk.core.repository.NodePinCollectionRepository;
import com.datasouk.dto.search.NodeSearchGetDto;
import com.datasouk.dto.search.NodeSearchResponse;
import com.datasouk.mapper.search.NodeSearchMapperImpl;
import com.datasouk.utils.dto.request.PinPagination;
import com.datasouk.utils.dto.request.PinPayload;
import com.datasouk.utils.pagination.PageBuilder;
import com.datasouk.utils.pagination.PageRequestBuilder;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PinCollectionServiceImpl implements PinCollectionService {

  private final NodePinCollectionRepository nodePinCollectionRepository;
  private final NodeSearchMapperImpl nodeSearchMapperImpl;
  private final PageBuilder pageBuilder;

  @Override
  public NodeSearchResponse pinCollection(PinPayload payLoad) throws ServiceException {
    String value = payLoad.getKey();
    PinPagination pagination = payLoad.getPagination();
    //logger.info("payLoad : "+payLoad);

    PageRequest pageRequest =
        PageRequestBuilder.getPageRequest(pagination);
    Page<Node> nodeinfo = nodePinCollectionRepository.getPinNodes(value, pageRequest);
    List<PinCollection> pininfo = nodePinCollectionRepository.getPinCollections(value);

    List<NodeSearchGetDto> nodeSearchInfo = nodeinfo.stream()
        .map(nodeSearchMapperImpl::nodeToNodeSearchGetDTO).collect(Collectors.toList());
    ApiModelPageAndSort apiModelPageAndSort =
        pageBuilder.build(nodeinfo.isLast(), nodeinfo.isFirst(), nodeinfo.getTotalPages(),
            nodeinfo.getTotalElements(), pageRequest.getPageNumber(), pageRequest.getPageSize());
//		 ApiModelPageAndSort apiModelPageAndSort1 =
//			        pageBuilder.build(pininfo.isLast(), pininfo.isFirst(), nodeinfo.getTotalPages(),
//			        		nodeinfo.getTotalElements(), pageRequest.getPageNumber(), pageRequest.getPageSize());
    return NodeSearchResponse.builder().paging(apiModelPageAndSort).data(nodeSearchInfo)
        .pinCollection(pininfo).build();
    //return NodeSearchResponse.builder().paging(apiModelPageAndSort).data(nodeSearchInfo).build();

    //List<Node> nodeSearchData = nodeinfo.stream().collect(Collectors.toList());
    //return nodePinCollectionRepository.getPinNodes(value);
    //return nodeSearchInfo;

  }

}

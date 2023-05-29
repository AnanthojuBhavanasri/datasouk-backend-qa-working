package com.datasouk.service.arango.node;

import com.datasouk.core.dto.request.ApiModelPageAndSort;
import com.datasouk.core.dto.request.Pagination;
import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.repository.PinNodeRepository;
import com.datasouk.dto.search.NodeSearchGetDto;
import com.datasouk.dto.search.NodeSearchResponse;
import com.datasouk.mapper.search.NodeSearchMapperImpl;
import com.datasouk.utils.pagination.PageBuilder;
import com.datasouk.utils.pagination.PageRequestBuilder;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PinNodeServiceImpl implements PinNodeService {

  private static Logger logger = LoggerFactory.getLogger(PinNodeServiceImpl.class);
  private static int pageLimit = 50;
  private final PinNodeRepository pinNodeRepository;
  private final PageBuilder pageBuilder;
  private final NodeSearchMapperImpl nodeSearchMapperImpl;

  @Override
  public NodeSearchResponse node(PayLoad payLoad) throws ServiceException {
    Pagination pagination = payLoad.getPagination();
    logger.info("payLoad : " + payLoad);

    PageRequest pageRequest =
        PageRequestBuilder.getPageRequest(pagination, payLoad.getSortFields());

    Page<Node> nodePage = pinNodeRepository.pinNodeQuery(pageRequest);

    List<NodeSearchGetDto> nodeSearchData = nodePage.getContent().stream()
        .map(nodeSearchMapperImpl::nodeToNodeSearchGetDTO).collect(Collectors.toList());

    ApiModelPageAndSort apiModelPageAndSort =
        pageBuilder.build(nodePage.isLast(), nodePage.isFirst(), nodePage.getTotalPages(),
            nodePage.getTotalElements(), pageRequest.getPageNumber(), pageRequest.getPageSize());
    return NodeSearchResponse.builder().paging(apiModelPageAndSort).data(nodeSearchData).build();


  }
}

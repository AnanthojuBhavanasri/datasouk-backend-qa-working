package com.datasouk.service.arango.node;

import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.Node;
import com.datasouk.dto.search.NodeDetailSearchResponse;
import com.datasouk.dto.search.NodeSearchGetDto;
import com.datasouk.dto.search.NodeSearchResponse;
import com.datasouk.dto.search.SearchResultsCount;
import com.datasouk.utils.dto.request.PinPayload;

import java.util.HashMap;
import java.util.List;

public interface NodeService {

  /**
   * @param payLoad
   * @return Get search node with pagination
   */
  NodeSearchResponse search(PayLoad payLoad) throws ServiceException;

  /**
   * @param payLoad
   * @return Get the search filters
   */
  SearchResultsCount searchFilters(PayLoad payLoad) throws ServiceException;

  /**
   * @param searchType
   * @return List of nodes with search criteria
   */
  List<Node> getNodes(String searchType) throws ServiceException;

  NodeDetailSearchResponse nodeInfo(String id) throws ServiceException;
  NodeSearchResponse nodeSourceSystemInfo(PinPayload payLoad) throws ServiceException;

}

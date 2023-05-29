package com.datasouk.service.arango.node;

import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.core.exception.ServiceException;
import com.datasouk.dto.search.NodeSearchResponse;

public interface PinNodeService {

  NodeSearchResponse node(PayLoad payLoad) throws ServiceException;

}

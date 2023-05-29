package com.datasouk.service.arango.node;

import com.datasouk.dto.search.NodeSearchResponse;
import com.datasouk.utils.dto.request.PinPayload;


public interface PinCollectionService {

  NodeSearchResponse pinCollection(PinPayload payLoad);
}

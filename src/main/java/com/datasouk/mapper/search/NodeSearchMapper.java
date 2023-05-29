package com.datasouk.mapper.search;

import com.datasouk.core.models.arango.Node;
import com.datasouk.dto.search.NodeSearchGetDto;


public interface NodeSearchMapper {

  NodeSearchGetDto nodeToNodeSearchGetDTO(Node node);

  NodeSearchGetDto nodeToNodeSearchCount(Node node);

}

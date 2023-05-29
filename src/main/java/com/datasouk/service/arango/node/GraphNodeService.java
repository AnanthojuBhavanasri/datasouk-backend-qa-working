package com.datasouk.service.arango.node;

import com.datasouk.dto.search.NodeSearchGetDto;

import java.util.List;

public interface GraphNodeService {
    List<NodeSearchGetDto> graphInfo(String name);
}

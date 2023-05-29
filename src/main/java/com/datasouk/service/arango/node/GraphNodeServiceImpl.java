package com.datasouk.service.arango.node;

import com.datasouk.core.models.arango.GraphNode;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.repository.GraphRepository;
import com.datasouk.dto.search.NodeSearchGetDto;
import com.datasouk.mapper.search.NodeSearchMapperImpl;
import org.springframework.stereotype.Service;

import com.datasouk.core.exception.ServiceException;



import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class GraphNodeServiceImpl implements GraphNodeService{

    private final GraphRepository graphRepository;
    private final NodeSearchMapperImpl nodeSearchMapperImpl;

    @Override
    public List<NodeSearchGetDto> graphInfo(String name) throws ServiceException{
        List<String> nodeIds=graphRepository.graph(name);
        List<Node> nodes=graphRepository.graphNodes(nodeIds);
        List<NodeSearchGetDto> nodeSearchData = nodes.stream().map(nodeSearchMapperImpl::nodeToNodeSearchGetDTO).collect(Collectors.toList());
        return nodeSearchData;
    }

    public List<GraphNode> graphNodeInfo(String name) {
        List<GraphNode> graphNode=graphRepository.graphSearchNode(name);
        return graphNode;
    }
}


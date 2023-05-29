package com.datasouk.controller;

import com.datasouk.core.models.arango.GraphNode;
import com.datasouk.dto.search.NodeSearchGetDto;
import com.datasouk.service.arango.node.GraphNodeServiceImpl;
import org.springframework.web.bind.annotation.*;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphNodeServiceImpl graphNodeServiceImpl;
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/graphSearchQuery")
    @Operation(summary = "returns graph search node")
    @Tag(name="graph",description = "Api's used to get graph search Nodes")
    public List<NodeSearchGetDto> graphSearch(@RequestParam String name) {
        return graphNodeServiceImpl.graphInfo(name);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/graphNodeSearch")
    @Operation(summary = "returns graph search node")
    @Tag(name="graph",description = "Api's used to get graph search Nodes")
    public List<GraphNode> graphNodesSearch(@RequestParam String name) {
        return graphNodeServiceImpl.graphNodeInfo(name);
    }
}


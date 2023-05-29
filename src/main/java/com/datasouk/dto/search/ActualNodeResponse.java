package com.datasouk.dto.search;

import com.datasouk.core.models.arango.ActualDataNode;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ActualNodeResponse {
    private List<ActualDataNode> actualDataNode;
    private ActualDataNodeDto actualDataNodeDto;
}

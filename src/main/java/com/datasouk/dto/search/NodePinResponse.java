package com.datasouk.dto.search;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NodePinResponse {
	
  private List<NodeSearchGetDto> data;
  
}

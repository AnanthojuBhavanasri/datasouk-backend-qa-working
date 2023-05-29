package com.datasouk.dto.search;

import com.datasouk.core.dto.request.ApiModelPageAndSort;
import com.datasouk.core.models.arango.PinCollection;
import java.util.List;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class NodeSearchResponse {

  private ApiModelPageAndSort paging;
  private List<NodeSearchGetDto> data;
  private List<PinCollection> pinCollection;


}

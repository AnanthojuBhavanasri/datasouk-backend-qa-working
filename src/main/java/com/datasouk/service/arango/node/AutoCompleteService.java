package com.datasouk.service.arango.node;

import com.datasouk.core.dto.search.AutoCompleteResponse;
import java.util.List;

public interface AutoCompleteService {

  List<AutoCompleteResponse> getAutoCompleteNodes(String value);
}

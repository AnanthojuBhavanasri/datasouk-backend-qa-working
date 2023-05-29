package com.datasouk.service.arango.node;

import com.datasouk.core.dto.search.AutoCompleteResponse;
import com.datasouk.core.repository.AutoCompleteNodeRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AutoCompleteServiceImpl implements AutoCompleteService {

  private final AutoCompleteNodeRepository autoCompleteRepository;

  @Override
  public List<AutoCompleteResponse> getAutoCompleteNodes(String value) {
    return autoCompleteRepository.getAutCompleteNodes(value);
  }

}

package com.datasouk.mapper.search;

import com.datasouk.dto.search.MetaCollection;
import com.datasouk.core.models.arango.Node;

public interface NodeDetailMapper {
    MetaCollection nodeToMetaCollection(Node node);
}

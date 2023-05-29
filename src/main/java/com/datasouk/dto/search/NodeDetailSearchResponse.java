package com.datasouk.dto.search;

import java.util.List;

import com.datasouk.dto.search.FrequencyAndFreshness;
import com.datasouk.dto.search.MetaCollection;
import com.datasouk.dto.search.OperationalMetaData;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NodeDetailSearchResponse {

    private NodeSearchGetDto nodeInfo;
    private MetaCollection metaCollection;
    private OperationalMetaData operationalMetaData;
    private FrequencyAndFreshness frequencyAndFreshness;
    private Metric metric;
    private NodeParameter nodeParameter;
}

package com.datasouk.dto.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Data
public class Metric {
    private List<HashMap<String,String>> metricMetaData;
}

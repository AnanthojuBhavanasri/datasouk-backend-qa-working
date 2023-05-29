package com.datasouk.dto.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Data
public class NodeParameter {
    private List<HashMap<String,String>> parmMetaData;
}

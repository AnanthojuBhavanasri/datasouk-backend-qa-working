package com.datasouk.dto.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
public class MetaCollection {
    private Set<String> businesskeys;
    private Set<String> governancekeys;
    private List<HashMap<String,String>> businessMetaData;
    private List<HashMap<String,String>> governanceMetaData;

}

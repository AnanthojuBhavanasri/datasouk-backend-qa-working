package com.datasouk.dto.search;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OperationalMetaData {

    private String numberOfElements;
    private List<String> ContentType;
}

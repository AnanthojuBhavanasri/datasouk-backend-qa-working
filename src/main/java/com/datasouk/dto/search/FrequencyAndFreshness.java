package com.datasouk.dto.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class FrequencyAndFreshness {

    private String dateCreated;
    private String currentVersion;
    private String lastModifiedOn;
}

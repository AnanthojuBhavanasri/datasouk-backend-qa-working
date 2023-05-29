package com.datasouk.dto.search;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class SearchResultsCount {

  //private List<SearchFieldValue> nodeType;
  private List<SearchFieldValue> status;
  private List<SearchFieldValue> averageRating;
  private List<SearchFieldValue> curated;
  private List<SearchFieldValue> certifed;
  private List<SearchFieldValue> frequency;
  private List<SearchFieldValue> tags;
  private List<SearchFieldValue> freshness;
  private List<SearchFieldValue> lineOfBusiness;
  private List<SearchFieldValue> dataDomain;
}

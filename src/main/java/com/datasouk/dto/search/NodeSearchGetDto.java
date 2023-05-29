package com.datasouk.dto.search;

import com.datasouk.core.models.arango.Attribute;
import java.util.List;
import lombok.Data;

@Data
public class NodeSearchGetDto {

  private String displayName;
  private String type;
  private String action;
  private boolean curated;
  private String ratingsCount;
  private String avgRating;
  private String identifier;
  private String id;
  private String key;
  private String createdByFullName;
  private String createdOn;
  private String status;
  private String sourceSystem;
  private int qualityScore;
  private int metaQualityScore;
  private int count;
  private int requestCount;
  private int viewCount;


  private List<String> lineOfBusiness;
  private List<String> dataDomain;
  private List<ResponsibilitiesDto> responsibilities;
  private List<String> certified;
  private List<String> frequency;
  private List<String> tags;
  private List<String> freshness;

  private String personallyIdentifiableInformation;


  private int passingFraction;

  private String shopable;

  private String description;

  private String searchable;

  private String url;

  private String definition;

  private String securityClassification;

  private List<Attribute> attributes;
  private String percentage;


}

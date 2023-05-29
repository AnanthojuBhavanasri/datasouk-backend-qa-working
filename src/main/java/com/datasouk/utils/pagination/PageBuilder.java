package com.datasouk.utils.pagination;


import com.datasouk.core.dto.request.ApiModelPageAndSort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PageBuilder {


  public ApiModelPageAndSort build(boolean hasNext, boolean hasPrevious, int totalPages,
      long totalElements, int pageNumber, int pageSize) {
    ApiModelPageAndSort.ApiModelPageAndSortBuilder builder = ApiModelPageAndSort.builder();

    // Set the flag to indicate next page exists
    builder.hasNextPage(!hasNext);

    // Set the flag to indicate previous page exists
    builder.hasPreviousPage(!hasPrevious);

    // Set the total number of records for the given Filter Specification
    builder.totalNumberOfRecords(totalElements);

    // Set the total number of pages for the given filter specification and pagerequests
    builder.totalNumberOfPages(totalPages);

    // Page numbers are indexed from 0 but to the consume we follow start index as 1
    builder.pageNumber(pageNumber + 1);

    // Number of records per page
    builder.pageSize(pageSize);

    return builder.build();

  }
}

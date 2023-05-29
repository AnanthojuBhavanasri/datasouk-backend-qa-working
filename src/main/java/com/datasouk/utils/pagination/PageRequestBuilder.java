package com.datasouk.utils.pagination;


import com.datasouk.core.dto.request.Pagination;
import com.datasouk.core.dto.request.SortFields;
import com.datasouk.utils.dto.request.PinPagination;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;


public final class PageRequestBuilder {

  private PageRequestBuilder() {
    // Do nothing
  }

  /**
   * Constructs PageRequest
   *
   * @param pagination
   * @param sortingFields
   * @return
   */
  public static PageRequest getPageRequest(Pagination pagination, List<SortFields> sortingFields) {

    int pageNumber = pagination == null ? 1 : pagination.getPageNumber();
    int pageSize = pagination == null ? 100 : pagination.getPageSize();
    //Todo Sorting should be implemented properly
    //List<SortFields> pageSortingFields = sortingFields == null ? new ArrayList<>() : sortingFields;
    List<SortFields> pageSortingFields = new ArrayList<>();
    List<Order> sortingOrders =
        pageSortingFields.stream().map(PageRequestBuilder::getOrder).collect(Collectors.toList());
    Sort sort = sortingOrders.isEmpty() ? Sort.unsorted() : Sort.by(sortingOrders);

    return PageRequest.of(ObjectUtils.defaultIfNull(pageNumber, 1) - 1,
        ObjectUtils.defaultIfNull(pageSize, 20), sort);
  }

  private static Order getOrder(SortFields sortField) {

    if (sortField.getSort().equals("desc")) {
      return new Order(Direction.DESC, sortField.getField());
    } else if (sortField.getSort().equals("asc")) {
      return new Order(Direction.ASC, sortField.getField());
    } else {
      // Sometimes '+' from query param can be replaced as ' '
      return new Order(Direction.ASC, sortField.getField());
    }

  }

  public static PageRequest getPageRequest(PinPagination pagination) {
    int pageNumber = pagination == null ? 1 : pagination.getPageNumber();
    int pageSize = pagination == null ? 100 : pagination.getPageSize();
    return PageRequest.of(ObjectUtils.defaultIfNull(pageNumber, 1) - 1,
        ObjectUtils.defaultIfNull(pageSize, 20));
  }

}

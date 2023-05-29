package com.datasouk.utils.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class PinPagination {
	private final int pageNumber;
    private final int pageSize;
}

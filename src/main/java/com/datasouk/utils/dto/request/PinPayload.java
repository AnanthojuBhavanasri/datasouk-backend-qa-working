package com.datasouk.utils.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class PinPayload {
	private final String key;
    private final PinPagination pagination;
}

package com.chensoul.monolith.infrastructure.errorhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import lombok.Builder;

@Builder
public record ErrorDetail(@JsonInclude(Include.NON_NULL) String pointer, String reason)
	implements Serializable {
}

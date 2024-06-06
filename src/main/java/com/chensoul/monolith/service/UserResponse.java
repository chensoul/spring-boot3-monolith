package com.chensoul.monolith.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record UserResponse(
	Long id,
	String name,
	String key,
	Boolean isActive,
	String createdBy,
	String updatedBy,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime updatedAt) {
}

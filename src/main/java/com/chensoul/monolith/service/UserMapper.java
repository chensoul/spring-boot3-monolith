package com.chensoul.monolith.service;

import com.chensoul.monolith.domain.User;
import com.chensoul.monolith.infrastructure.jpa.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper
	extends BaseMapper<
	User,
	CreateUserRequest,
	UpdateUserRequest,
	UserResponse> {

	@Override
	User toEntity(CreateUserRequest request);

	@Override
	UserResponse toResponse(User entity);
}

package com.chensoul.monolith.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(@NotNull Long companyId, @NotBlank String name) {}

package com.chensoul.sharedlib.jpa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * The interface Base mapper management.
 *
 * @param <E> the type parameter Entity
 * @param <C> the type parameter CreateRequest
 * @param <U> the type parameter UpdateRequest
 * @param <R> the type parameter Response
 */
public interface BaseMapper<E, C, U, R> {

	@ToEntity
	E toEntity(C request);

	@ToEntity
	E update(U request, @MappingTarget E entity);

	@ToEntity
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	E patch(U request, @MappingTarget E entity);

	R toResponse(E entity);

	Collection<R> toResponse(Collection<E> entity);

	@Retention(RetentionPolicy.CLASS)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	public @interface ToEntity {
	}
}

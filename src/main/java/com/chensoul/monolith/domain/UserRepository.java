package com.chensoul.monolith.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface UserRepository extends JpaRepository<User, Long> {

	String CACHE_NAME = "user";

	User findFirstByNameAndIsActive(String name, boolean isActive);

	@Cacheable(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #key}")
	Optional<User> findByKeyAndIsActive(String key, boolean isActive);

	@Caching(evict = {@CacheEvict(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #entity.key}")})
	@Override
	<S extends User> @NonNull S save(@NonNull S entity);

	/*
	 * This cache implementation is only valid if the table is not
	 * frequently updated since it will clear the cache at every update operation
	 * If you want to be more performant you can use something like https://github.com/ms100/cache-as-multi
	 * */
	@NonNull
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	<S extends User> List<S> saveAll(@NonNull Iterable<S> entities);

	@Caching(evict = {@CacheEvict(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #entity.key}")})
	@Override
	void delete(@NonNull User entity);

	/*
	 * This cache implementation is only valid if the table is not
	 * frequently updated since it will clear the cache at every delete operation
	 * If you want to be more performant you can use something like https://github.com/ms100/cache-as-multi
	 * */
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	void deleteAll(@NonNull Iterable<? extends User> entities);
}

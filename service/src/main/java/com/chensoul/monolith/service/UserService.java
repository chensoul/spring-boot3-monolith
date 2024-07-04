package com.chensoul.monolith.service;

import com.chensoul.monolith.domain.User;
import com.chensoul.monolith.domain.UserRepository;
import com.chensoul.sharedlib.exception.NotFoundException;
import com.chensoul.sharedlib.jpa.BaseService;
import static java.lang.String.format;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends BaseService<User> {
	@Getter
	private final UserRepository repository;

	@Override
	protected void doBeforeCreateEntity(final User entity) {
		entity.setIsActive(true);
		entity.setKey(RandomStringUtils.randomAlphabetic(18));
	}

	public Optional<User> findByKeyOptional(final String key) {
		log.debug("[retrieving] apiKey");
		return this.repository.findByKeyAndIsActive(key, true);
	}

	public User findFirstByNameAndIsActive(final String name) {
		log.debug("[retrieving] apiKey with name {}", name);
		return this.repository.findFirstByNameAndIsActive(name, true);
	}

	@Transactional(rollbackFor = Exception.class)
	public void inactivate(final Long id) {
		log.info("[inactivating] apiKey with id '{}'", id);

		final Optional<User> apiKeyOptional = this.getRepository().findById(id);
		if (!apiKeyOptional.isPresent()) {
			throw new NotFoundException(format("apiKey '%s' not found", id));
		}

		final User entity = apiKeyOptional.get();
		entity.setIsActive(false);
		this.update(entity);
	}
}

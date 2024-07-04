package com.chensoul.monolith.controller;

import static com.chensoul.monolith.Constants.API;
import com.chensoul.monolith.domain.User;
import com.chensoul.monolith.service.CreateUserRequest;
import com.chensoul.monolith.service.UpdateUserRequest;
import com.chensoul.monolith.service.UserMapper;
import com.chensoul.monolith.service.UserResponse;
import com.chensoul.monolith.service.UserService;
import com.chensoul.sharedlib.jpa.BaseController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Controller
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping(UserController.BASE_URL)
@RequiredArgsConstructor
public class UserController
	extends BaseController<User, CreateUserRequest, UpdateUserRequest, UserResponse> {
	public static final String BASE_URL = API + "/users";

	@Getter
	private final UserService service;
	@Getter
	private final UserMapper mapper;

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	@Override
	public void delete(@PathVariable("id") final Long id) {
		log.info("[request] inactive {} '{}'", User.TABLE_NAME, id);
		this.service.inactivate(id);
	}
}

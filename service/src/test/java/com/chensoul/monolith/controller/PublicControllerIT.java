package com.chensoul.monolith.controller;

import com.chensoul.monolith.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicControllerIT extends BaseIntegrationTest {

	private final String URL = PublicController.BASE_URL + "/hello-world";

	@Test
	void return_200() throws Exception {
		this.mockMvc
			.perform(get(this.URL))
			.andExpect(status().isOk());
	}
}

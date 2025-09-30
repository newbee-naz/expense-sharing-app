package com.splitmate.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User", description = "Operations related to the currently authenticated user")
public class UserController {

	@Operation(summary = "Get current authenticated user info")
	@GetMapping("/api/whoami")
	public Map<String, Object> whoami(
			@AuthenticationPrincipal OAuth2User principal) {
		return Map.of("principalAttributes", principal.getAttributes());
	}
}

package com.splitmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	private final CustomOAuth2UserService oAuth2UserService;

	public SecurityConfig(CustomOAuth2UserService oAuth2UserService) {
		this.oAuth2UserService = oAuth2UserService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/h2-console/**", "/actuator/**")
						.permitAll().requestMatchers("/api/admin/**")
						.hasAuthority("ADMIN").requestMatchers("/api/**")
						.authenticated().anyRequest().permitAll())
				.oauth2Login(oauth2 -> oauth2.userInfoEndpoint(
						userInfo -> userInfo.userService(oAuth2UserService))
						.defaultSuccessUrl("/api/whoami", true))
				.logout(logout -> logout.logoutSuccessUrl("/"));

		// allow frames for H2 console
		http.headers(
				headers -> headers.frameOptions(frame -> frame.sameOrigin()));
		return http.build();
	}
}

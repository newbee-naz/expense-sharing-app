package com.splitmate.config;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.splitmate.model.AppUser;
import com.splitmate.repository.AppUserRepository;

@Service
public class CustomOAuth2UserService
		implements
			OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final AppUserRepository userRepository;

	public CustomOAuth2UserService(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest)
			throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oauthUser = delegate.loadUser(userRequest);

		Map<String, Object> attributes = oauthUser.getAttributes();
		String oauthId = (String) attributes.get("sub"); // Google-specific
		String email = (String) attributes.get("email");
		String name = (String) attributes.get("name");

		// find or create local AppUser
		AppUser user = userRepository.findByOauthId(oauthId)
				.or(() -> userRepository.findByEmail(email)).orElseGet(() -> {
					AppUser u = AppUser.builder().oauthId(oauthId).email(email)
							.name(name).role("USER").build();
					return userRepository.save(u);
				});

		// create Spring Security principal
		List<SimpleGrantedAuthority> authorities = List
				.of(new SimpleGrantedAuthority(user.getRole()));
		return new DefaultOAuth2User(authorities,
				Map.of("id", user.getId(), "email", user.getEmail(), "name",
						user.getName(), "oauthId", user.getOauthId()),
				"email");
	}

}

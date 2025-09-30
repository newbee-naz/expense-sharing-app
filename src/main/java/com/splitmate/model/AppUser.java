package com.splitmate.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class AppUser implements Serializable {

	private static final long serialVersionUID = 7727740340353856503L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique = true)
	private String email;

	@Column(unique = true)
	private String oauthId;

	private String role; // "USER" or "ADMIN"

	// Default constructor
	public AppUser() {
	}

	// All args constructor
	public AppUser(Long id, String name, String email, String oauthId,
			String role) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.oauthId = oauthId;
		this.role = role;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOauthId() {
		return oauthId;
	}

	public void setOauthId(String oauthId) {
		this.oauthId = oauthId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// Builder pattern
	public static class Builder {
		private Long id;
		private String name;
		private String email;
		private String oauthId;
		private String role;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder oauthId(String oauthId) {
			this.oauthId = oauthId;
			return this;
		}

		public Builder role(String role) {
			this.role = role;
			return this;
		}

		public AppUser build() {
			return new AppUser(id, name, email, oauthId, role);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}

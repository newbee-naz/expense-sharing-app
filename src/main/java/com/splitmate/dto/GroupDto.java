package com.splitmate.dto;

import java.io.Serializable;

public class GroupDto implements Serializable {

	private static final long serialVersionUID = -3044126608509282486L;

	private Long id;
	private String name;
	private Long ownerId;

	// No-args constructor
	public GroupDto() {
	}

	// All-args constructor
	public GroupDto(Long id, String name, Long ownerId) {
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
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

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	// Builder
	public static class Builder {
		private Long id;
		private String name;
		private Long ownerId;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder ownerId(Long ownerId) {
			this.ownerId = ownerId;
			return this;
		}

		public GroupDto build() {
			return new GroupDto(id, name, ownerId);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}

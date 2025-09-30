package com.splitmate.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups_tbl")
public class GroupEntity implements Serializable {

	private static final long serialVersionUID = 3152823886705483365L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Long ownerId;

	// No-args constructor
	public GroupEntity() {
	}

	// All-args constructor
	public GroupEntity(Long id, String name, Long ownerId) {
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
	}

	// All-args constructor
	public GroupEntity(Long id, String name) {
		this.id = id;
		this.name = name;

	}

	// Getters and Setters
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

		public GroupEntity build() {
			return new GroupEntity(id, name, ownerId);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}

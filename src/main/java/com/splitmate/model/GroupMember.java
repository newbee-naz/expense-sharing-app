package com.splitmate.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "group_members", uniqueConstraints = @UniqueConstraint(columnNames = {
		"group_id", "user_id"}))
public class GroupMember implements Serializable {

	private static final long serialVersionUID = 1551987752579408467L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "user_id")
	private Long userId;

	// track running balance for this user in this group; not source of truth
	// but cached
	@Column(precision = 19, scale = 4)
	private BigDecimal balance = BigDecimal.ZERO;

	@Version
	private Long version;

	// No-args constructor
	public GroupMember() {
	}

	// All-args constructor
	public GroupMember(Long id, Long groupId, Long userId, BigDecimal balance,
			Long version) {
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.balance = balance;
		this.version = version;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	// Builder
	public static class Builder {
		private Long id;
		private Long groupId;
		private Long userId;
		private BigDecimal balance = BigDecimal.ZERO;
		private Long version;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder groupId(Long groupId) {
			this.groupId = groupId;
			return this;
		}

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder balance(BigDecimal balance) {
			this.balance = balance;
			return this;
		}

		public Builder version(Long version) {
			this.version = version;
			return this;
		}

		public GroupMember build() {
			return new GroupMember(id, groupId, userId, balance, version);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}

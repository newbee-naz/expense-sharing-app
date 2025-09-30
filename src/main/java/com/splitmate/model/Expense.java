package com.splitmate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "expenses")
public class Expense implements Serializable {

	private static final long serialVersionUID = 8609034645963380711L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(precision = 19, scale = 4)
	private BigDecimal amount;

	private Long createdBy;

	private Long groupId;

	private Instant createdAt;

	@Version
	private Long version;

	// No-args constructor
	public Expense() {
	}

	// All-args constructor
	public Expense(Long id, String title, BigDecimal amount, Long createdBy,
			Long groupId, Instant createdAt, Long version) {
		this.id = id;
		this.title = title;
		this.amount = amount;
		this.createdBy = createdBy;
		this.groupId = groupId;
		this.createdAt = createdAt;
		this.version = version;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
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
		private String title;
		private BigDecimal amount;
		private Long createdBy;
		private Long groupId;
		private Instant createdAt;
		private Long version;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder amount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public Builder createdBy(Long createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		public Builder groupId(Long groupId) {
			this.groupId = groupId;
			return this;
		}

		public Builder createdAt(Instant createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder version(Long version) {
			this.version = version;
			return this;
		}

		public Expense build() {
			return new Expense(id, title, amount, createdBy, groupId, createdAt,
					version);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}

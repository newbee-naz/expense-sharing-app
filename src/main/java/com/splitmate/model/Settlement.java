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

@Entity
@Table(name = "settlements")
public class Settlement implements Serializable {

	private static final long serialVersionUID = 7493902857741602497L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long payerId;
	private Long receiverId;

	@Column(precision = 19, scale = 4)
	private BigDecimal amount;

	private Long groupId;

	private Long expenseId; // optional

	private Instant createdAt;

	// No-args constructor
	public Settlement() {
	}

	// All-args constructor
	public Settlement(Long id, Long payerId, Long receiverId, BigDecimal amount,
			Long groupId, Long expenseId, Instant createdAt) {
		this.id = id;
		this.payerId = payerId;
		this.receiverId = receiverId;
		this.amount = amount;
		this.groupId = groupId;
		this.expenseId = expenseId;
		this.createdAt = createdAt;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPayerId() {
		return payerId;
	}

	public void setPayerId(Long payerId) {
		this.payerId = payerId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Long expenseId) {
		this.expenseId = expenseId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	// Builder
	public static class Builder {
		private Long id;
		private Long payerId;
		private Long receiverId;
		private BigDecimal amount;
		private Long groupId;
		private Long expenseId;
		private Instant createdAt;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder payerId(Long payerId) {
			this.payerId = payerId;
			return this;
		}

		public Builder receiverId(Long receiverId) {
			this.receiverId = receiverId;
			return this;
		}

		public Builder amount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public Builder groupId(Long groupId) {
			this.groupId = groupId;
			return this;
		}

		public Builder expenseId(Long expenseId) {
			this.expenseId = expenseId;
			return this;
		}

		public Builder createdAt(Instant createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Settlement build() {
			return new Settlement(id, payerId, receiverId, amount, groupId,
					expenseId, createdAt);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}

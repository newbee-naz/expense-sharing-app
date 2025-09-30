package com.splitmate.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "expense_splits")
public class Split implements Serializable {

	private static final long serialVersionUID = 6857443851176736442L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long expenseId;

	private Long userId;

	@Column(precision = 19, scale = 4)
	private BigDecimal amount; // what this user owes for this expense

	private boolean settled = false;

	// No-args constructor
	public Split() {
	}

	// All-args constructor
	public Split(Long id, Long expenseId, Long userId, BigDecimal amount,
			boolean settled) {
		this.id = id;
		this.expenseId = expenseId;
		this.userId = userId;
		this.amount = amount;
		this.settled = settled;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Long expenseId) {
		this.expenseId = expenseId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isSettled() {
		return settled;
	}

	public void setSettled(boolean settled) {
		this.settled = settled;
	}

	// Builder
	public static class Builder {
		private Long id;
		private Long expenseId;
		private Long userId;
		private BigDecimal amount;
		private boolean settled = false;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder expenseId(Long expenseId) {
			this.expenseId = expenseId;
			return this;
		}

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder amount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public Builder settled(boolean settled) {
			this.settled = settled;
			return this;
		}

		public Split build() {
			return new Split(id, expenseId, userId, amount, settled);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}

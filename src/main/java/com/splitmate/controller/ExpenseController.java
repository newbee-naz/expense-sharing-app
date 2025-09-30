package com.splitmate.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.splitmate.model.Expense;
import com.splitmate.repository.ExpenseRepository;
import com.splitmate.repository.GroupMemberRepository;
import com.splitmate.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Operations related to group expenses")
public class ExpenseController {

	private final ExpenseService expenseService;
	private final ExpenseRepository expenseRepository;
	private final GroupMemberRepository memberRepository;

	public ExpenseController(ExpenseService expenseService,
			ExpenseRepository expenseRepository,
			GroupMemberRepository memberRepository) {
		this.expenseService = expenseService;
		this.expenseRepository = expenseRepository;
		this.memberRepository = memberRepository;
	}

	@Operation(summary = "Add a new expense to a group", description = "Adds an expense for a group and automatically splits it among participants. Custom splits are supported.")
	@PostMapping
	@Transactional
	public ResponseEntity<?> addExpense(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Expense request payload", required = true) @RequestBody AddExpenseRequest req,
			@AuthenticationPrincipal OAuth2User principal) {

		Long userId = getCurrentUserId(principal);

		if (memberRepository.findByGroupIdAndUserId(req.getGroupId(), userId)
				.isEmpty()) {
			return ResponseEntity.status(403).body("User not member of group");
		}

		Expense e = expenseService.addExpense(req.getGroupId(), userId,
				req.getTitle(), req.getAmount(), req.getParticipantIds(),
				req.getCustomSplit());

		return ResponseEntity.ok(Map.of("expenseId", e.getId()));
	}

	@Operation(summary = "List all expenses for a group")
	@GetMapping("/group/{groupId}")
	public ResponseEntity<?> listByGroup(
			@Parameter(description = "ID of the group") @PathVariable Long groupId,
			@AuthenticationPrincipal OAuth2User principal) {

		Long userId = getCurrentUserId(principal);

		if (memberRepository.findByGroupIdAndUserId(groupId, userId)
				.isEmpty()) {
			return ResponseEntity.status(403).body("Not member of group");
		}

		List<Expense> expenses = expenseRepository.findByGroupId(groupId);
		return ResponseEntity.ok(expenses);
	}

	// --- helper ---
	private Long getCurrentUserId(OAuth2User principal) {
		String sub = principal.getAttribute("sub");
		return Math.abs(sub.hashCode() * 1L);
	}

	// --- Request DTO ---
	@io.swagger.v3.oas.annotations.media.Schema(description = "Request payload for adding an expense")
	public static class AddExpenseRequest {
		private String title;
		private BigDecimal amount;
		private Long groupId;
		private List<Long> participantIds;
		private Map<Long, BigDecimal> customSplit;

		// getters & setters
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
		public Long getGroupId() {
			return groupId;
		}
		public void setGroupId(Long groupId) {
			this.groupId = groupId;
		}
		public List<Long> getParticipantIds() {
			return participantIds;
		}
		public void setParticipantIds(List<Long> participantIds) {
			this.participantIds = participantIds;
		}
		public Map<Long, BigDecimal> getCustomSplit() {
			return customSplit;
		}
		public void setCustomSplit(Map<Long, BigDecimal> customSplit) {
			this.customSplit = customSplit;
		}
	}
}

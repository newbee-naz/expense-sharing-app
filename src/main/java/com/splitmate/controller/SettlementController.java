package com.splitmate.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.splitmate.model.Settlement;
import com.splitmate.service.SettlementServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/settlements")
@Tag(name = "Settlements", description = "Operations related to group settlements")
public class SettlementController {

	private final SettlementServiceImpl settlementService;

	public SettlementController(SettlementServiceImpl settlementService) {
		this.settlementService = settlementService;
	}

	@Operation(summary = "Make a settlement between two users")
	@PostMapping
	public ResponseEntity<?> makeSettlement(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Settlement request payload") @RequestBody SettlementRequest req,
			@AuthenticationPrincipal OAuth2User principal) {

		Long currentUserId = getCurrentUserId(principal);

		if (!currentUserId.equals(req.getPayerId())) {
			return ResponseEntity.status(403).body(
					Map.of("error", "Payer must be the authenticated user"));
		}

		try {
			Settlement s = settlementService.settle(req.getPayerId(),
					req.getReceiverId(), req.getAmount(), req.getGroupId(),
					req.getExpenseId());
			return ResponseEntity.ok(Map.of("settlementId", s.getId()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(
					Map.of("error", "Settlement failed: " + e.getMessage()));
		}
	}

	private Long getCurrentUserId(OAuth2User principal) {
		String sub = principal.getAttribute("sub");
		return Math.abs(sub.hashCode() * 1L);
	}

	@io.swagger.v3.oas.annotations.media.Schema(description = "Request payload for making a settlement")
	public static class SettlementRequest {

		@io.swagger.v3.oas.annotations.media.Schema(description = "ID of the payer", example = "1")
		private Long payerId;

		@io.swagger.v3.oas.annotations.media.Schema(description = "ID of the receiver", example = "2")
		private Long receiverId;

		@io.swagger.v3.oas.annotations.media.Schema(description = "Amount to settle", example = "100.50")
		private BigDecimal amount;

		@io.swagger.v3.oas.annotations.media.Schema(description = "ID of the group", example = "101")
		private Long groupId;

		@io.swagger.v3.oas.annotations.media.Schema(description = "ID of the expense (optional)", example = "5001")
		private Long expenseId;

		// getters & setters
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
	}
}

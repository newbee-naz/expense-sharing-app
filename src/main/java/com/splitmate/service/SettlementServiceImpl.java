package com.splitmate.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.splitmate.model.GroupMember;
import com.splitmate.model.Settlement;
import com.splitmate.repository.GroupMemberRepository;
import com.splitmate.repository.SettlementRepository;

import jakarta.transaction.Transactional;

@Service
public class SettlementServiceImpl {
	private final SettlementRepository settlementRepository;
	private final GroupMemberRepository memberRepository;
	private final BalanceServiceImpl balanceService;

	public SettlementServiceImpl(SettlementRepository settlementRepository,
			GroupMemberRepository memberRepository,
			BalanceServiceImpl balanceService) {
		this.settlementRepository = settlementRepository;
		this.memberRepository = memberRepository;
		this.balanceService = balanceService;
	}

	/**
	 * Payer pays receiver in a group. This operation is transactional & safe
	 * for concurrency.
	 */
	@Transactional
	public Settlement settle(Long payerId, Long receiverId, BigDecimal amount,
			Long groupId, Long expenseId) {

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
		if (payerId.equals(receiverId)) {
			throw new IllegalArgumentException(
					"Payer and receiver cannot be the same person");
		}

		var payerOpt = memberRepository.findByGroupIdAndUserId(groupId,
				payerId);
		var receiverOpt = memberRepository.findByGroupIdAndUserId(groupId,
				receiverId);
		if (payerOpt.isEmpty() || receiverOpt.isEmpty()) {
			throw new IllegalArgumentException(
					"Both users must be group members");
		}

		GroupMember payer = payerOpt.get();
		GroupMember receiver = receiverOpt.get();

		// Validate balances: payer must owe at least `amount`
		if (payer.getBalance().compareTo(amount.negate()) < 0) {
			throw new IllegalArgumentException(
					"Payer does not owe enough to settle this amount");
		}

		int maxRetries = 3;
		for (int i = 0; i < maxRetries; i++) {
			try {
				// Create settlement record
				Settlement s = Settlement.builder().payerId(payerId)
						.receiverId(receiverId).amount(amount).groupId(groupId)
						.expenseId(expenseId).createdAt(Instant.now()).build();
				settlementRepository.save(s);

				// Update balances (atomic with optimistic lock)
				payer.setBalance(payer.getBalance().add(amount)); // owes less
				receiver.setBalance(receiver.getBalance().subtract(amount)); // is
																				// owed
																				// less

				memberRepository.save(payer);
				memberRepository.save(receiver);

				return s;
			} catch (ObjectOptimisticLockingFailureException e) {
				if (i == maxRetries - 1) {
					throw new RuntimeException(
							"Settlement failed due to concurrent updates. Please retry.",
							e);
				}
				// retry by reloading fresh entities
				payer = memberRepository
						.findByGroupIdAndUserId(groupId, payerId).orElseThrow();
				receiver = memberRepository
						.findByGroupIdAndUserId(groupId, receiverId)
						.orElseThrow();
			}
		}
		throw new RuntimeException(
				"Settlement could not be completed after retries");
	}
}

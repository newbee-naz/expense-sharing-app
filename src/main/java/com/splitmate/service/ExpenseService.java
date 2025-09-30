package com.splitmate.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitmate.model.Expense;
import com.splitmate.model.GroupMember;
import com.splitmate.model.Split;
import com.splitmate.repository.ExpenseRepository;
import com.splitmate.repository.GroupMemberRepository;
import com.splitmate.repository.SplitRepository;

@Service
public class ExpenseService {

	private final ExpenseRepository expenseRepository;
	private final SplitRepository splitRepository;
	private final GroupMemberRepository memberRepository;

	public ExpenseService(ExpenseRepository expenseRepository,
			SplitRepository splitRepository,
			GroupMemberRepository memberRepository) {
		this.expenseRepository = expenseRepository;
		this.splitRepository = splitRepository;
		this.memberRepository = memberRepository;
	}

	@Transactional
	public Expense addExpense(Long groupId, Long createdBy, String title,
			BigDecimal amount, List<Long> participantIds,
			Map<Long, BigDecimal> customSplit) {

		// create expense
		Expense expense = Expense.builder().title(title).amount(amount)
				.groupId(groupId).createdBy(createdBy).createdAt(Instant.now())
				.build();
		expenseRepository.save(expense);

		// if no participants specified → include all group members
		if (participantIds == null || participantIds.isEmpty()) {
			participantIds = memberRepository.findByGroupId(groupId).stream()
					.map(GroupMember::getUserId).toList();
		}

		// validate custom split sum if provided
		if (customSplit != null && !customSplit.isEmpty()) {
			BigDecimal sum = customSplit.values().stream()
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			if (sum.subtract(amount).abs()
					.compareTo(new BigDecimal("0.0001")) > 0) {
				throw new IllegalArgumentException(
						"Custom split does not sum to expense amount");
			}

			for (Map.Entry<Long, BigDecimal> entry : customSplit.entrySet()) {
				Split s = Split.builder().expenseId(expense.getId())
						.userId(entry.getKey()).amount(entry.getValue())
						.settled(false).build();
				splitRepository.save(s);

				updateBalance(groupId, entry.getKey(),
						entry.getValue().negate());
			}
		} else {
			// equal split
			BigDecimal per = amount.divide(
					BigDecimal.valueOf(participantIds.size()), 4,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal sumPer = per
					.multiply(BigDecimal.valueOf(participantIds.size()));
			BigDecimal remainder = amount.subtract(sumPer);

			for (int i = 0; i < participantIds.size(); i++) {
				Long pid = participantIds.get(i);
				BigDecimal share = per;
				if (i == 0) { // assign remainder to first participant
					share = share.add(remainder);
				}

				Split s = Split.builder().expenseId(expense.getId()).userId(pid)
						.amount(share).settled(false).build();
				splitRepository.save(s);

				updateBalance(groupId, pid, share.negate());
			}
		}

		// credit the payer (creator) with the full amount paid
		updateBalance(groupId, createdBy, amount);

		return expense;
	}

	private void updateBalance(Long groupId, Long userId, BigDecimal delta) {
		GroupMember gm = memberRepository
				.findByGroupIdAndUserId(groupId, userId)
				.orElseThrow(() -> new IllegalArgumentException(
						"User not member of group"));

		gm.setBalance(gm.getBalance().add(delta));
		memberRepository.save(gm);
	}
}

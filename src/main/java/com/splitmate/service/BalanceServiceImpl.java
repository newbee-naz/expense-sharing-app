package com.splitmate.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.splitmate.model.Expense;
import com.splitmate.model.GroupMember;
import com.splitmate.model.Settlement;
import com.splitmate.model.Split;
import com.splitmate.repository.ExpenseRepository;
import com.splitmate.repository.GroupMemberRepository;
import com.splitmate.repository.SettlementRepository;
import com.splitmate.repository.SplitRepository;

import jakarta.transaction.Transactional;

@Service
public class BalanceServiceImpl {
	private final GroupMemberRepository memberRepository;
	private final ExpenseRepository expenseRepository;
	private final SplitRepository splitRepository;
	private final SettlementRepository settlementRepository;

	public BalanceServiceImpl(GroupMemberRepository memberRepository,
			ExpenseRepository expenseRepository,
			SplitRepository splitRepository,
			SettlementRepository settlementRepository) {
		this.memberRepository = memberRepository;
		this.expenseRepository = expenseRepository;
		this.splitRepository = splitRepository;
		this.settlementRepository = settlementRepository;
	}

	@Transactional
	public void recompute(Long groupId) {
		List<GroupMember> members = memberRepository.findByGroupId(groupId);
		Map<Long, BigDecimal> balances = new HashMap<>();
		for (GroupMember m : members) {
			balances.put(m.getUserId(), BigDecimal.ZERO);
		}

		List<Expense> expenses = expenseRepository.findByGroupId(groupId);
		for (Expense e : expenses) {
			List<Split> splits = splitRepository.findByExpenseId(e.getId());
			Long payer = e.getCreatedBy();
			for (Split s : splits) {
				balances.put(s.getUserId(),
						balances.getOrDefault(s.getUserId(), BigDecimal.ZERO)
								.subtract(s.getAmount()));
				balances.put(payer,
						balances.getOrDefault(payer, BigDecimal.ZERO)
								.add(s.getAmount()));
			}
		}

		// apply settlements
		List<Settlement> settlements = settlementRepository
				.findByGroupId(groupId);
		for (Settlement st : settlements) {
			// Payer pays receiver: cash flows payer -> receiver
			// So payer's balance should increase (they pay cash to receiver,
			// their owed position reduces)
			// We'll treat settlement as moving balances: payer owes less =>
			// balances[payer] += amount
			// receiver owed less => balances[receiver] -= amount
			balances.put(st.getPayerId(),
					balances.getOrDefault(st.getPayerId(), BigDecimal.ZERO)
							.add(st.getAmount()));
			balances.put(st.getReceiverId(),
					balances.getOrDefault(st.getReceiverId(), BigDecimal.ZERO)
							.subtract(st.getAmount()));
		}

		// persist back to GroupMember balances
		for (GroupMember m : members) {
			m.setBalance(balances.getOrDefault(m.getUserId(), BigDecimal.ZERO));
			memberRepository.save(m);
		}
	}
}

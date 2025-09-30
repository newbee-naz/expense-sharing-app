package com.splitmate.service;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import com.splitmate.model.GroupMember;
import com.splitmate.model.Settlement;
import com.splitmate.repository.GroupMemberRepository;
import com.splitmate.repository.SettlementRepository;

@SpringBootTest
public class SettlementServiceTest {

	@Autowired
	SettlementRepository settlementRepository;

	@Autowired
	GroupMemberRepository memberRepository;

	@Autowired
	BalanceServiceImpl balanceService;

	@Autowired
	SettlementServiceImpl settlementService;

	@Test
	public void testSettle() {
		// setup group members
		GroupMember m1 = GroupMember.builder().groupId(1L).userId(1L)
				.balance(new BigDecimal("50.00")).build();
		GroupMember m2 = GroupMember.builder().groupId(1L).userId(2L)
				.balance(new BigDecimal("-50.00")).build();
		m1 = memberRepository.save(m1);
		m2 = memberRepository.save(m2);

		Settlement s = settlementService.settle(2L, 1L, new BigDecimal("25.00"),
				1L, null);

		GroupMember after1 = memberRepository.findById(m1.getId()).get();
		GroupMember after2 = memberRepository.findById(m2.getId()).get();

		assertEquals(after1.getBalance(), new BigDecimal("25.00")); // 50 - 25
		assertEquals(after2.getBalance(), new BigDecimal("-25.00")); // -50 + 25
	}
}

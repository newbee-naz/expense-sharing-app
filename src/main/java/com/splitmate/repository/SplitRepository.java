package com.splitmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.splitmate.model.Split;
@Repository
public interface SplitRepository extends JpaRepository<Split, Long> {
	List<Split> findByExpenseId(Long expenseId);
	List<Split> findByUserIdAndExpenseId(Long userId, Long expenseId);
}

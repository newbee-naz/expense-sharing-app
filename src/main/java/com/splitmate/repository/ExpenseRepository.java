package com.splitmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.splitmate.model.Expense;
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByGroupId(Long groupId);

}

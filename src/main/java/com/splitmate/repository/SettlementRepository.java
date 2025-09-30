package com.splitmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.splitmate.model.Settlement;
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
	List<Settlement> findByGroupId(Long groupId);
}

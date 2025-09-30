package com.splitmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.splitmate.model.GroupEntity;
@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

}

package com.splitmate.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.splitmate.dto.GroupDto;
import com.splitmate.model.GroupEntity;
import com.splitmate.model.GroupMember;
import com.splitmate.repository.AppUserRepository;
import com.splitmate.repository.GroupMemberRepository;
import com.splitmate.repository.GroupRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Groups", description = "Operations related to group management")
public class GroupController {

	private final GroupRepository groupRepository;
	private final GroupMemberRepository memberRepository;
	private final AppUserRepository userRepository;

	@Operation(summary = "List all members of a group with balances")
	@GetMapping("/{groupId}/members")
	public ResponseEntity<?> listMembers(@PathVariable Long groupId,
			@AuthenticationPrincipal OAuth2User principal) {

		Long current = getCurrentUserId(principal);
		if (memberRepository.findByGroupIdAndUserId(groupId, current)
				.isEmpty()) {
			return ResponseEntity.status(403)
					.body("Not a member of this group");
		}

		List<GroupMember> members = memberRepository.findByGroupId(groupId);
		return ResponseEntity.ok(members);
	}

	@Operation(summary = "Remove a member from a group (leave group)")
	@DeleteMapping("/{groupId}/members/{userId}")
	public ResponseEntity<?> removeMember(@PathVariable Long groupId,
			@PathVariable Long userId,
			@AuthenticationPrincipal OAuth2User principal) {

		GroupEntity g = groupRepository.findById(groupId).orElseThrow();
		Long current = getCurrentUserId(principal);

		// Either the user themselves or group owner can remove
		if (!Objects.equals(current, g.getOwnerId())
				&& !Objects.equals(current, userId)) {
			return ResponseEntity.status(403)
					.body("Not allowed to remove this member");
		}

		var memberOpt = memberRepository.findByGroupIdAndUserId(groupId,
				userId);
		if (memberOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("User not in group");
		}

		GroupMember gm = memberOpt.get();
		if (gm.getBalance() != null
				&& gm.getBalance().compareTo(java.math.BigDecimal.ZERO) != 0) {
			return ResponseEntity.badRequest()
					.body("Cannot leave group with unsettled balance");
		}

		memberRepository.delete(gm);
		return ResponseEntity.ok("Member removed");
	}

	public GroupController(GroupRepository groupRepository,
			GroupMemberRepository memberRepository,
			AppUserRepository userRepository) {
		this.groupRepository = groupRepository;
		this.memberRepository = memberRepository;
		this.userRepository = userRepository;
	}

	@Operation(summary = "Leave a group")
	@DeleteMapping("/{groupId}/leave")
	public ResponseEntity<?> leaveGroup(@PathVariable Long groupId,
			@AuthenticationPrincipal OAuth2User principal) {

		Long currentUserId = getCurrentUserId(principal);

		// fetch membership
		GroupMember membership = memberRepository
				.findByGroupIdAndUserId(groupId, currentUserId)
				.orElseThrow(() -> new RuntimeException(
						"Not a member of this group"));

		// check unsettled balance
		if (membership.getBalance() != null
				&& membership.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			return ResponseEntity.status(400)
					.body("Cannot leave group with unsettled balance: "
							+ membership.getBalance());
		}

		// if user is group owner → block leaving unless group is empty
		GroupEntity group = groupRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Group not found"));

		if (Objects.equals(group.getOwnerId(), currentUserId)) {
			long memberCount = memberRepository.findByGroupId(groupId).size();
			if (memberCount > 1) {
				return ResponseEntity.status(400).body(
						"Group owner cannot leave until group is deleted or ownership transferred");
			}
		}

		// remove membership
		memberRepository.delete(membership);

		return ResponseEntity.ok().body("Left group successfully");
	}

	@Operation(summary = "Create a new group")
	@PostMapping
	public ResponseEntity<GroupDto> createGroup(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Group DTO with name") @RequestBody GroupDto dto,
			@AuthenticationPrincipal OAuth2User principal) {

		Long userId = getCurrentUserId(principal);
		GroupEntity g = GroupEntity.builder().name(dto.getName())
				.ownerId(userId).build();
		groupRepository.save(g);

		// add owner as member
		GroupMember gm = GroupMember.builder().groupId(g.getId()).userId(userId)
				.balance(java.math.BigDecimal.ZERO).build();
		memberRepository.save(gm);

		return ResponseEntity
				.ok(new GroupDto(g.getId(), g.getName(), g.getOwnerId()));
	}

	@Operation(summary = "Add a member to a group")
	@PostMapping("/{groupId}/members/{userId}")
	public ResponseEntity<?> addMember(
			@io.swagger.v3.oas.annotations.Parameter(description = "ID of the group") @PathVariable Long groupId,
			@io.swagger.v3.oas.annotations.Parameter(description = "ID of the user to add") @PathVariable Long userId,
			@AuthenticationPrincipal OAuth2User principal) {

		GroupEntity g = groupRepository.findById(groupId).orElseThrow();
		Long current = getCurrentUserId(principal);
		if (!Objects.equals(current, g.getOwnerId())) {
			return ResponseEntity.status(403)
					.body("Only group owner can add members");
		}

		memberRepository.findByGroupIdAndUserId(groupId, userId)
				.ifPresentOrElse(m -> {
				}, () -> memberRepository.save(
						GroupMember.builder().groupId(groupId).userId(userId)
								.balance(java.math.BigDecimal.ZERO).build()));

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "List all groups the current user belongs to")
	@GetMapping
	public ResponseEntity<List<GroupDto>> listMyGroups(
			@AuthenticationPrincipal OAuth2User principal) {

		Long current = getCurrentUserId(principal);
		List<GroupMember> memberships = memberRepository.findByUserId(current);
		List<Long> groupIds = memberships.stream().map(GroupMember::getGroupId)
				.toList();
		List<GroupDto> result = groupRepository.findAllById(groupIds).stream()
				.map(g -> new GroupDto(g.getId(), g.getName(), g.getOwnerId()))
				.collect(Collectors.toList());

		return ResponseEntity.ok(result);
	}

	// --- helper ---
	private Long getCurrentUserId(OAuth2User principal) {
		String sub = principal.getAttribute("sub");
		return Math.abs(sub.hashCode() * 1L);
	}
}

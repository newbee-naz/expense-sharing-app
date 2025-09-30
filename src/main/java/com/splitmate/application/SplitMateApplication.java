package com.splitmate.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.splitmate.model.AppUser;
import com.splitmate.model.Expense;
import com.splitmate.model.GroupEntity;
import com.splitmate.model.GroupMember;
import com.splitmate.model.Settlement;
import com.splitmate.repository.AppUserRepository;
import com.splitmate.repository.ExpenseRepository;
import com.splitmate.repository.GroupMemberRepository;
import com.splitmate.repository.GroupRepository;
import com.splitmate.repository.SettlementRepository;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication(scanBasePackages = {"com.splitmate"})
@EnableJpaRepositories(basePackages = "com.splitmate.repository")
@EntityScan(basePackages = "com.splitmate.model")
public class SplitMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitMateApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(
				new Info().title("SplitMate API").version("1.0").description(
						"API documentation for SplitMate application"));
	}

	@Bean
	CommandLineRunner loadData(AppUserRepository userRepo,
			GroupRepository groupRepo, GroupMemberRepository memberRepo,
			ExpenseRepository expenseRepo,
			SettlementRepository settlementRepo) {
		return args -> {
			// Users
			AppUser alice = new AppUser(null, "Alice", "alice@gmail.com",
					"google-oauth-1", "USER");
			AppUser bob = new AppUser(null, "Bob", "bob@gmail.com",
					"google-oauth-2", "USER");
			AppUser charlie = new AppUser(null, "Charlie", "charlie@gmail.com",
					"google-oauth-3", "ADMIN");
			userRepo.saveAll(List.of(alice, bob, charlie));

			// Groups
			GroupEntity trip = new GroupEntity(null, "Trip to Dubai",
					alice.getId());
			GroupEntity lunch = new GroupEntity(null, "Office Lunch",
					charlie.getId());
			groupRepo.saveAll(List.of(trip, lunch));

			// Group Members
			memberRepo.saveAll(List.of(
					new GroupMember(null, trip.getId(), alice.getId(),
							BigDecimal.ZERO, null),
					new GroupMember(null, trip.getId(), bob.getId(),
							BigDecimal.ZERO, null),
					new GroupMember(null, trip.getId(), charlie.getId(),
							BigDecimal.ZERO, null),
					new GroupMember(null, lunch.getId(), bob.getId(),
							BigDecimal.ZERO, null),
					new GroupMember(null, lunch.getId(), charlie.getId(),
							BigDecimal.ZERO, null)));

			// Expenses
			Expense exp1 = new Expense(null, "Dinner at Burj Khalifa",
					BigDecimal.valueOf(100), alice.getId(), trip.getId(),
					Instant.now(), null);

			Expense exp2 = new Expense(null, "Taxi fare",
					BigDecimal.valueOf(50), bob.getId(), trip.getId(),
					Instant.now(), null);

			Expense exp3 = new Expense(null, "Team Lunch",
					BigDecimal.valueOf(200), charlie.getId(), lunch.getId(),
					Instant.now(), null);

			// Settlements
			Settlement s1 = new Settlement(null, bob.getId(), alice.getId(),
					BigDecimal.valueOf(25), trip.getId(), exp1.getId(),
					Instant.now());
			Settlement s2 = new Settlement(null, charlie.getId(), bob.getId(),
					BigDecimal.valueOf(50), lunch.getId(), exp3.getId(),
					Instant.now());
			settlementRepo.saveAll(List.of(s1, s2));

		};
	}

}

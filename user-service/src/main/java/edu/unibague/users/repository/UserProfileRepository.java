package edu.unibague.users.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.unibague.users.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}

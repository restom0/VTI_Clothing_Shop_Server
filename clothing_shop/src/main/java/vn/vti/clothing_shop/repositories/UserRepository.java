package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/** Finds by deleted at is null. */
	List<User> findByDeletedAtIsNull();

	/** Finds by deleted at is null and role. */
	List<User> findByDeletedAtIsNullAndRole(String role);

	/** Finds by deleted at is null and id. */
	Optional<User> findByDeletedAtIsNullAndId(Long id);

	/** Finds by deleted at is null and email. */
	Optional<User> findByDeletedAtIsNullAndEmail(String email);

	/** Finds one by deleted at is null and username or email or phone number. */
	Optional<User> findOneByDeletedAtIsNullAndUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);

	/** Counts by deleted at is null and role. */
	long countByDeletedAtIsNullAndRole(UserRole role);

	/** Handles exists by deleted at is null and username. */
	boolean existsByDeletedAtIsNullAndUsername(String username);

	/** Handles exists by deleted at is null and email. */
	boolean existsByDeletedAtIsNullAndEmail(String email);

	/** Handles exists by deleted at is null and phone number. */
	boolean existsByDeletedAtIsNullAndPhoneNumber(String phoneNumber);
}

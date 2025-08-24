package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByDeletedAtIsNull();

    List<User> findByDeletedAtIsNullAndRole(String role);

    Optional<User> findByDeletedAtIsNullAndId(Long id);

    Optional<User> findOneByDeletedAtIsNullUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);

    long countByDeletedAtIsNullAndRole(UserRole role);

    boolean existsByDeletedAtIsNullAndUsername(String username);

    boolean existsByDeletedAtIsNullAndEmail(String email);

    boolean existsByDeletedAtIsNullAndPhoneNumber(String phoneNumber);
}

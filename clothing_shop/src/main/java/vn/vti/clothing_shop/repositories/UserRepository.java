package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.deleted_at IS NULL AND u.role = 'USER'")
    List<User> findAllUser();
    @Override
    @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.deleted_at IS NULL ORDER BY u.id ASC")
    @NotNull
    Optional<User> findById(@NotNull Long id);
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.deleted_at IS NULL")
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.deleted_at IS NULL")
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.phone_number = ?1 AND u.deleted_at IS NULL")
    Optional<User> findByPhoneNumber(String phone_number);
    @Override
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted_at IS NULL AND u.role = 'USER'")
    long count();
    
}

package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Chat;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> getAllByDeletedAtIsNullOrderByIdDesc();

    List<Chat> getByDeletedAtIsNullAndSenderId(Long senderId);

    Optional<Chat> findByDeletedAtIsNullAndIdAndSenderId(Long id, Long senderId);

    Optional<Chat> findByDeletedAtIsNullAndId(Long id);
}

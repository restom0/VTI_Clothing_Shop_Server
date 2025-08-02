package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> getAllByDeletedAtIsNullOrderByIdDesc();

    List<Chat> findByDeletedAtIsNullAndSenderId(Long senderId);
}

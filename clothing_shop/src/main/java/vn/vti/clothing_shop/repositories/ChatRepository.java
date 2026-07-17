package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Chat;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	/** Gets all by deleted at is null order by id desc. */
	List<Chat> getAllByDeletedAtIsNullOrderByIdDesc();

	/** Gets by deleted at is null and sender id. */
	List<Chat> getByDeletedAtIsNullAndSenderId(Long senderId);

	/** Finds by deleted at is null and id and sender id. */
	Optional<Chat> findByDeletedAtIsNullAndIdAndSenderId(Long id, Long senderId);

	/** Finds by deleted at is null and id. */
	Optional<Chat> findByDeletedAtIsNullAndId(Long id);
}

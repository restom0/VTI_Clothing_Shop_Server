package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.constants.SocialAuthProvider;
import vn.vti.clothing_shop.entities.UserSocialAccount;

import java.util.Optional;

@Repository
public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {
	/** Finds by deleted at is null and provider and provider user id. */
	Optional<UserSocialAccount> findByDeletedAtIsNullAndProviderAndProviderUserId(
			SocialAuthProvider provider,
			String providerUserId
	);
}

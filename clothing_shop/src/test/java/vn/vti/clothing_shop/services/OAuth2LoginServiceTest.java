package vn.vti.clothing_shop.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.vti.clothing_shop.constants.SocialAuthProvider;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.UserSocialAccount;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.repositories.UserSocialAccountRepository;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserSocialAccountRepository socialAccountRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @InjectMocks
    OAuth2LoginService service;

    @Test
    void loginCreatesUserAndSocialAccountForNewGoogleProfile() {
        Map<String, Object> attributes = Map.of(
                "sub", "google-123",
                "name", "Ada Lovelace",
                "email", "ada@example.com",
                "picture", "https://cdn.example.com/ada.png"
        );
        when(socialAccountRepository.findByDeletedAtIsNullAndProviderAndProviderUserId(
                SocialAuthProvider.GOOGLE,
                "google-123"
        )).thenReturn(Optional.empty());
        when(userRepository.findByDeletedAtIsNullAndEmail("ada@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByDeletedAtIsNullAndUsername(anyString())).thenReturn(false);
        when(userRepository.existsByDeletedAtIsNullAndPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(socialAccountRepository.save(any(UserSocialAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        UserLoginDTO login = service.login("google", attributes);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<UserSocialAccount> socialAccountCaptor = ArgumentCaptor.forClass(UserSocialAccount.class);
        verify(userRepository).save(userCaptor.capture());
        verify(socialAccountRepository).save(socialAccountCaptor.capture());

        User createdUser = userCaptor.getValue();
        assertThat(createdUser.getName()).isEqualTo("Ada Lovelace");
        assertThat(createdUser.getUsername()).isEqualTo("google_ada_example_com");
        assertThat(createdUser.getPassword()).isEqualTo("encoded-password");
        assertThat(createdUser.getEmail()).isEqualTo("ada@example.com");
        assertThat(createdUser.getPhoneNumber()).startsWith("oauth-google-");
        assertThat(createdUser.getAvatarUrl()).isEqualTo("https://cdn.example.com/ada.png");
        assertThat(createdUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(createdUser.getGender()).isEqualTo(UserGender.MALE);
        assertThat(createdUser.getSalt()).isNotBlank();

        UserSocialAccount socialAccount = socialAccountCaptor.getValue();
        assertThat(socialAccount.getUser()).isSameAs(createdUser);
        assertThat(socialAccount.getProvider()).isEqualTo(SocialAuthProvider.GOOGLE);
        assertThat(socialAccount.getProviderUserId()).isEqualTo("google-123");
        assertThat(socialAccount.getEmail()).isEqualTo("ada@example.com");

        assertThat(login.getToken()).isEqualTo("jwt-token");
        assertThat(login.getName()).isEqualTo("Ada Lovelace");
        assertThat(login.getAvatarUrl()).isEqualTo("https://cdn.example.com/ada.png");
    }

    @Test
    void loginLinksSocialAccountToExistingUserWithSameEmail() {
        User existingUser = new User();
        existingUser.setName("Existing Ada");
        existingUser.setEmail("ada@example.com");
        existingUser.setAvatarUrl("https://cdn.example.com/existing.png");

        Map<String, Object> attributes = Map.of(
                "sub", "google-123",
                "name", "Ada Lovelace",
                "email", "ada@example.com",
                "picture", "https://cdn.example.com/ada.png"
        );
        when(socialAccountRepository.findByDeletedAtIsNullAndProviderAndProviderUserId(
                SocialAuthProvider.GOOGLE,
                "google-123"
        )).thenReturn(Optional.empty());
        when(userRepository.findByDeletedAtIsNullAndEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(socialAccountRepository.save(any(UserSocialAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(existingUser)).thenReturn("jwt-token");

        UserLoginDTO login = service.login("google", attributes);

        ArgumentCaptor<UserSocialAccount> socialAccountCaptor = ArgumentCaptor.forClass(UserSocialAccount.class);
        verify(userRepository, never()).save(any(User.class));
        verify(socialAccountRepository).save(socialAccountCaptor.capture());

        UserSocialAccount socialAccount = socialAccountCaptor.getValue();
        assertThat(socialAccount.getUser()).isSameAs(existingUser);
        assertThat(socialAccount.getProvider()).isEqualTo(SocialAuthProvider.GOOGLE);
        assertThat(socialAccount.getProviderUserId()).isEqualTo("google-123");
        assertThat(socialAccount.getName()).isEqualTo("Ada Lovelace");

        assertThat(login.getToken()).isEqualTo("jwt-token");
        assertThat(login.getName()).isEqualTo("Existing Ada");
        assertThat(login.getAvatarUrl()).isEqualTo("https://cdn.example.com/existing.png");
    }
}

package side.oauthtest.domain.user.oauth2user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.oauthtest.domain.user.oauth2user.CustomOAuth2User;

import java.util.Optional;

public interface OAuth2UserRepository extends JpaRepository<CustomOAuth2User, Long> {
    Optional<CustomOAuth2User> findByEmail(String email); // 이미 email을 통해 생성된 사용자인지 체크
}

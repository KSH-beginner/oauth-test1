package side.oauthtest.domain.user.oauth2user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import side.oauthtest.domain.user.oauth2user.CustomOAuth2User;
import side.oauthtest.domain.user.oauth2user.SessionUser;
import side.oauthtest.domain.user.oauth2user.dto.OAuthAttributesDto;
import side.oauthtest.domain.user.oauth2user.repository.OAuth2UserRepository;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserRepository oAuth2UserRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 서비스 ID (구글, 카카오, 네이버)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드 값(PK)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2USerService
        OAuthAttributesDto attributesDto = OAuthAttributesDto.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        CustomOAuth2User user = saveOrUpdate(attributesDto);
        httpSession.setAttribute("user", new SessionUser(user)); // SessionUser (직렬화된 dto 클래스 사용)

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributesDto.getAttributes(),
                attributesDto.getNameAttributeKey());
    }

    // 유저 생성 및 수정 서비스 로직
    private CustomOAuth2User saveOrUpdate(OAuthAttributesDto oAuthAttributesDto) {
        CustomOAuth2User user = oAuth2UserRepository.findByEmail(oAuthAttributesDto.getEmail())
                .map(entity -> entity.update(oAuthAttributesDto.getName(), oAuthAttributesDto.getPicture()))
                .orElse(oAuthAttributesDto.toEntity());
        return oAuth2UserRepository.save(user);
    }
}

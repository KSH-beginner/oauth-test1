package side.oauthtest.domain.user.oauth2user.dto;

import lombok.Builder;
import lombok.Getter;
import side.oauthtest.domain.user.Role;
import side.oauthtest.domain.user.oauth2user.CustomOAuth2User;


import java.util.Map;

@Getter
public class OAuthAttributesDto {
    private Map<String, Object> attributes; // OAuth2 반환하는 유저 정보 Map
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributesDto(Map<String, Object> attributes, String nameAttributeKey,
                              String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    // 네이버와 카카오 등 서비스 구분 (ofNaver, ofKaKao)
    public static OAuthAttributesDto of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {

        // naver
        if(registrationId.equals("naver")) {
            return ofNaver("id", attributes);
        }

        // kakao
        if(registrationId.equals("kakao")) {
            return ofKakao("id", attributes);
        }

        // google
        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAttributesDto ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributesDto.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuthAttributesDto ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        // JSON 형태이기 때문에 Map을 통해서 데이터를 가져온다.
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributesDto.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuthAttributesDto ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        return OAuthAttributesDto.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public CustomOAuth2User toEntity() {
        return CustomOAuth2User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST) // 기본 권한 GUEST
                .build();
    }


}

package side.oauthtest.domain.user.oauth2user;

import lombok.Getter;

import java.io.Serializable;

/**
 * 직렬화 기능을 가진 User 클래스
 *
 * CustomOAuth2User 클래스를 그대로 사용하면 직렬화를 구현하지 않았으므로 에러 발생
 * 따라서, 직렬화된 SessionUser 클래스를 만들어준다.
 *
 * ※ CustomOAuth2User 클래스에 직렬화를 하지 않는 이유는,
 * 다른 Entity와도 연관관계가 생길 수 있는 엔티티이므로 성능 이슈, 부수적인 효과가 발생할 확률이 높으므로!
 * (@OneToMany, @ManyToOne 등 자식 엔티티를 가지면 성능 저하)
 */
@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionUser(CustomOAuth2User customOAuth2User) {
        this.name = customOAuth2User.getName();
        this.email = customOAuth2User.getEmail();
        this.picture = customOAuth2User.getPicture();
    }

}

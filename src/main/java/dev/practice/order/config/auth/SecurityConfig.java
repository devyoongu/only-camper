package dev.practice.order.config.auth;
import dev.practice.order.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/images/**", "/img/**", "/js/**", "/h2-console/**", "/profile","/item/**","/api/**").permitAll()
//                    .antMatchers("/api/v1/**").hasRole(Role.USER.name()) //api/v1/~ 시작되는 api는 user권한에 할당
//                    .antMatchers("/partner/**").hasRole(Role.USER.name())
                    .anyRequest().authenticated() // matcher 설정 이외 페이지는 인증된 사용자만 접근 설정
                .and()
                    .logout()
                        .logoutSuccessUrl("/")
                .and()
                    .oauth2Login()
                        .userInfoEndpoint()
                            .userService(customOAuth2UserService); // 로그인 성공 시 호출
    }
}
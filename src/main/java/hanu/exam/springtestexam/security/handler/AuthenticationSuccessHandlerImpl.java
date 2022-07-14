package hanu.exam.springtestexam.security.handler;

import hanu.exam.springtestexam.common.ApiResponse;
import hanu.exam.springtestexam.domain.account.entity.Account;
import hanu.exam.springtestexam.security.CustomAuthenticationToken;
import hanu.exam.springtestexam.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Value("${hanu.service.name}")
    private String serviceName;

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 전달받은 인증정보 SecurityContextHolder에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomAuthenticationToken customAuthenticationToken = (CustomAuthenticationToken) authentication;

        log.info("userId:" + customAuthenticationToken.getUserId());
        log.info("userName:" + customAuthenticationToken.getUsername());

        // JWT Token 발급 - accessToken
        String accessToken = jwtProvider.createAccessToken(
                customAuthenticationToken.getUserId()
                , customAuthenticationToken.getUsername()
                , null
                , serviceName);


        // JWT Token 발급 - refreshToken
        String refreshToken = jwtProvider.createAccessToken(
                customAuthenticationToken.getUserId()
                , customAuthenticationToken.getUsername()
                , null
                , serviceName);

        ApiResponse.token(response, accessToken, refreshToken);
    }

}
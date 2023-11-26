package com.kientruchanoi.ecommerce.authservicecore.service.impl;

import com.kientruchanoi.ecommerce.authservicecore.config.jwt.JwtTokenProvider;
import com.kientruchanoi.ecommerce.authservicecore.entity.RefreshToken;
import com.kientruchanoi.ecommerce.authservicecore.entity.User;
import com.kientruchanoi.ecommerce.authservicecore.mapper.OAuthUserMapper;
import com.kientruchanoi.ecommerce.authservicecore.mapper.TokenMapper;
import com.kientruchanoi.ecommerce.authservicecore.payload.OAuthUserInfo;
import com.kientruchanoi.ecommerce.authservicecore.payload.response.GithubResponseToken;
import com.kientruchanoi.ecommerce.authservicecore.payload.response.GithubResponseUser;
import com.kientruchanoi.ecommerce.authservicecore.payload.response.GoogleResponseUser;
import com.kientruchanoi.ecommerce.authservicecore.repository.RefreshTokenRepository;
import com.kientruchanoi.ecommerce.authservicecore.repository.UserRepository;
import com.kientruchanoi.ecommerce.authservicecore.service.OAuthService;
import com.kientruchanoi.ecommerce.authservicecore.service.TokenService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Gender;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Role;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.UserProvider;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.TokenObjectResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private static final String GOOGLE_API_INFO = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String GITHUB_API_TOKEN = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_API_INFO = "https://api.github.com/user";

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenMapper tokenMapper;
    private final ResponseFactory responseFactory;
    private final OAuthUserMapper oAuthUserMapper;

    @Override
    public ResponseEntity<BaseResponse<TokenObjectResponse>> validateGoogleToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleResponseUser> response = restTemplate.exchange(GOOGLE_API_INFO, HttpMethod.GET, entity, GoogleResponseUser.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                GoogleResponseUser data = response.getBody();
                log.info("response_user_info - {}", data);
                return saveUser(oAuthUserMapper.toUserInfo(data), UserProvider.GOOGLE);

            } else {
                log.info("Failure...");
                return responseFactory.fail(HttpStatus.BAD_REQUEST, "Đăng nhập thất bại...", null);
            }
        } catch (HttpClientErrorException e) {
            log.error("Failed request in try-catch - {}", e.getMessage());
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Đăng nhập thất bại...", null);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<TokenObjectResponse>> validateGithubCode(String code) {
        log.info("Logging github - {}", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GITHUB_API_TOKEN)
                .queryParam("client_id", githubClientId)
                .queryParam("client_secret", githubClientSecret)
                .queryParam("code", code);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<GithubResponseToken> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                request,
                GithubResponseToken.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Token - {}", response.getBody().getAccess_token());
            GithubResponseUser userInfo = getGithubUserInfo(response.getBody());
            return saveUser(oAuthUserMapper.toUserInfo(userInfo), UserProvider.GITHUB);

        } else {
            log.info("Failure...");
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Đăng nhập thất bại...", null);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<TokenObjectResponse>> validateFacebookToken(String code) {
        log.info("Logging facebook - {}", code);
        return null;
    }

    private GithubResponseUser getGithubUserInfo(GithubResponseToken responseToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, responseToken.getToken_type() + " " + responseToken.getAccess_token());
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GithubResponseUser> response = restTemplate.exchange(GITHUB_API_INFO, HttpMethod.GET, httpEntity, GithubResponseUser.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                GithubResponseUser data = response.getBody();
                log.info("response_user_info - {}", data);
                return data;
            } else {
                log.info("Failure...");
            }
        } catch (HttpClientErrorException e) {
            log.error("Failed request in try-catch - {}", e.getMessage());
        }

        return null;
    }

    private ResponseEntity<BaseResponse<TokenObjectResponse>> saveUser(OAuthUserInfo userInfo, UserProvider provider) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElse(User.builder()
                        .firstName(userInfo.getFirstName())
                        .lastName(userInfo.getLastName())
                        .email(userInfo.getEmail())
                        .avatarUrl(userInfo.getAvatarUrl())
                        .createdDate(LocalDateTime.now())
                        .role(Role.USER.name())
                        .provider(provider.name())
                        .status(Status.ACTIVE.name())
                        .gender(Gender.UNDEFINE.name())
                        .build());
        user = userRepository.save(user);

        RefreshToken refreshToken = tokenRepository.findByUserId(user.getId())
                .orElse(RefreshToken.builder()
                        .userId(user.getId())
                        .build());
        tokenRepository.save(refreshToken);
        refreshToken = tokenService.generateTokenObject(user);
        tokenRepository.save(refreshToken);
        String accessToken = jwtTokenProvider.generateToken(user.getEmail());

        TokenObjectResponse responseObject = TokenObjectResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(tokenMapper.mapToDto(refreshToken))
                .build();

        return responseFactory.success("Success", responseObject);
    }
}

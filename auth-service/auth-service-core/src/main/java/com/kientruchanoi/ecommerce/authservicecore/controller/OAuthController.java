package com.kientruchanoi.ecommerce.authservicecore.controller;

import com.kientruchanoi.ecommerce.authservicecore.service.OAuthService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.TokenObjectResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;
    private final ResponseFactory responseFactory;

    @PostMapping("/validate")
    @Transactional
    public ResponseEntity<BaseResponse<TokenObjectResponse>> oAuthValidateToken(@RequestParam(name = "provider") String provider,
                                                                                @RequestParam(name = "token") String token) {
        log.info("triggerr - {} - {}", provider, token);

        provider = provider.toUpperCase();
        return switch (provider) {
            case "GOOGLE" -> oAuthService.validateGoogleToken(token);
            case "GITHUB" -> oAuthService.validateGithubCode(token);
            case "FACEBOOK" -> oAuthService.validateFacebookToken(token);
            default -> responseFactory.fail(HttpStatus.BAD_REQUEST, "Defaul case - unknown error", null);
        };
    }

}

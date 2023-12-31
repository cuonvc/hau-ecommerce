package com.kientruchanoi.ecommerce.authservicecore.controller;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;
import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetailService;
import com.kientruchanoi.ecommerce.authservicecore.config.jwt.JwtTokenProvider;
import com.kientruchanoi.ecommerce.authservicecore.entity.DeviceToken;
import com.kientruchanoi.ecommerce.authservicecore.entity.User;
import com.kientruchanoi.ecommerce.authservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.authservicecore.payload.response.CustomUserDetailResponse;
import com.kientruchanoi.ecommerce.authservicecore.repository.DeviceTokenRepository;
import com.kientruchanoi.ecommerce.authservicecore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
@Slf4j
public class InternalController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final UserRepository userRepository;
    private final DeviceTokenRepository deviceTokenRepository;

    @GetMapping("/admins")
    public ResponseEntity<List<String>> getAllAdmin() {
        List<String> respose =  userRepository.findAllByRole("ADMIN")
                .stream().map(u -> u.getId())
                .collect(Collectors.toList());
        return ResponseEntity.ok(respose);
    }

    @GetMapping("/check")
    public ResponseEntity<CustomUserDetailResponse> validateToken(@RequestParam("token") String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);
            UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            CustomUserDetailResponse userDetailResponse = CustomUserDetailResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .grantedAuthorities(List.of(user.getRole().toString()))
                    .build();

            return ResponseEntity.ok(userDetailResponse);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/device_token")
    public ResponseEntity<String> getDeviceToken(@RequestParam("userId") String userId) {
        DeviceToken deviceToken = deviceTokenRepository.findByUserId(userId)
                .orElse(null);

        log.info("DEVICE_TOKEN_TRIGGER - {}", deviceToken);

        if (deviceToken == null) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.ok(deviceToken.getToken());
    }

    private UsernamePasswordAuthenticationToken buildAuthenticationToken(CustomUserDetail userDetails, List<GrantedAuthority> authorityList) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = authorityList.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList();

        return new UsernamePasswordAuthenticationToken(userDetails, null, simpleGrantedAuthorities);
    }
}

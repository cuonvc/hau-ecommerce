package com.kientruchanoi.ecommerce.authservicecore.service.impl;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;
import com.kientruchanoi.ecommerce.authservicecore.config.jwt.JwtTokenProvider;
import com.kientruchanoi.ecommerce.authservicecore.entity.DeviceToken;
import com.kientruchanoi.ecommerce.authservicecore.entity.RefreshToken;
import com.kientruchanoi.ecommerce.authservicecore.entity.User;
import com.kientruchanoi.ecommerce.authservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.authservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.authservicecore.mapper.TokenMapper;
import com.kientruchanoi.ecommerce.authservicecore.mapper.UserMapper;
import com.kientruchanoi.ecommerce.authservicecore.payload.request.PasswordChangeRequest;
import com.kientruchanoi.ecommerce.authservicecore.payload.request.RenewPasswordRequest;
import com.kientruchanoi.ecommerce.authservicecore.repository.DeviceTokenRepository;
import com.kientruchanoi.ecommerce.authservicecore.repository.RefreshTokenRepository;
import com.kientruchanoi.ecommerce.authservicecore.repository.UserRepository;
import com.kientruchanoi.ecommerce.authservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.authservicecore.service.FileImageService;
import com.kientruchanoi.ecommerce.authservicecore.service.TokenService;
import com.kientruchanoi.ecommerce.authservicecore.service.UserService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Role;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.LoginRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.ProfileRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.RegRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.PageResponseUsers;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.TokenObjectResponse;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.request.FileObjectRequest;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.notificationserviceshare.enumerate.NotificationType;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String AVATAR = "avatarUrl";
    private static final String COVER = "";

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final TokenMapper tokenMapper;
    private final ResponseFactory responseFactory;
    private final StreamBridge streamBridge;
    private final RedisTemplate<RegRequest, String> redisTemplateObject;
    private final RedisTemplate<String, String> redisTemplateString;
    private final FileImageService fileImageService;
    private final CommonService commonService;

    private static final String ORDER_NOTIFY_ACTION = "order.notify.action";

    @Override
    public ResponseEntity<BaseResponse<String>> register(RegRequest request) {

        request.setEmail(request.getEmail().trim().toLowerCase());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Tài khoản đã tồn tại", null);
        }

        String activeCode = String.valueOf(new Random().nextInt(900000) + 100000);
        redisTemplateObject.opsForValue().set(request, activeCode);
        redisTemplateObject.expire(request, 5, TimeUnit.MINUTES);
        log.info("Redis cached - {}", redisTemplateObject.opsForValue().get(request));

        Message<String> message = MessageBuilder.withPayload(activeCode)
                .setHeader(KafkaHeaders.KEY, request.getEmail().getBytes())
                .build();
        streamBridge.send("email-active", message);
        return responseFactory.success("Mã xác nhận đã được gửi tới email ", request.getEmail());


//        User user = userMapper.requestToEntity(request);
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        User saved = userRepository.save(user);
//        tokenService.initRefreshToken(user);
//        UserResponse response = userMapper.entityToResponse(saved);
//        return responseFactory.success("Success", response);
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> validate(RegRequest request, String code) {
        request.setEmail(request.getEmail().trim().toLowerCase());

        String activeCode = redisTemplateObject.opsForValue().get(request);
        if (activeCode == null) {
            return responseFactory.fail(HttpStatus.UNAUTHORIZED, "Mã xác nhận đã hết hạn, vui lòng xác thực lại!", null);
        }

        if (!activeCode.equals(code)) {
            return responseFactory.fail(HttpStatus.UNAUTHORIZED, "Mã xác thực không chính xác", null);
        }

        User user = userMapper.requestToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        tokenService.initRefreshToken(user);
        UserResponse response = userMapper.entityToResponse(saved);
        redisTemplateObject.delete(request);

        userRepository.findAllByRole("ADMIN").forEach(u -> {
            Message<NotificationBuilder> message = MessageBuilder
                    .withPayload(
                            NotificationBuilder.builder()
                                    .type(NotificationType.USER_CREATED)
                                    .title(NotificationType.USER_CREATED.getMessage())
                                    .content("Tài khoản " + user.getEmail() + " vừa mới đăng ký.")
                                    .recipient(u.getId())
                                    .build()
                    )
                    .setHeader(KafkaHeaders.KEY, user.getId().getBytes())
                    .build();
            streamBridge.send(ORDER_NOTIFY_ACTION, message);
        });

        return responseFactory.success("Kích hoạt tài khoản thành công, vui lòng đăng nhập lại!", response);
    }

    @Override
    public ResponseEntity<BaseResponse<TokenObjectResponse>> login(LoginRequest request) {
        request.setEmail(request.getEmail().trim().toLowerCase());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (!validPassword(request.getPassword(), user.getPassword())) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Mật khẩu không chính xác", null);
        }

        String accessToken = jwtTokenProvider.generateToken(request.getEmail());
        RefreshToken refreshToken = tokenService.generateTokenObject(user);
        UserResponse userResponse = userMapper.entityToResponse(user);
        userResponse.setProducts(commonService.getProductsByUser(userResponse.getId()));
        TokenObjectResponse response = TokenObjectResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(tokenMapper.mapToDto(refreshToken))
                .userResponse(userResponse)
                .build();

        DeviceToken deviceToken = deviceTokenRepository.findByUserId(user.getId())
                .orElse(
                        DeviceToken.builder()
                                .token(request.getExpoToken())
                                .userId(user.getId())
                                .build()
                );
        deviceToken.setToken(request.getExpoToken());
        deviceTokenRepository.save(deviceToken);

        return responseFactory.success("Success", response);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> logout() {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        tokenService.clearToken(userDetail.getId());
        deviceTokenRepository.findByUserId(userDetail.getId()).ifPresent(deviceTokenRepository::delete);
        return responseFactory.success("Đã đăng xuất", "Success");
    }

    @Override
    public ResponseEntity<BaseResponse<TokenObjectResponse>> renewAccessToken(String refreshToken) {

//        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RefreshToken rt = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Bạn đã đăng xuất trước đó"));

        User user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống"));

        if (rt.getExpireDate().compareTo(new Date()) > 0) {
            return responseFactory.success("Success",
                    TokenObjectResponse.builder()
                            .accessToken(jwtTokenProvider.generateToken(user.getEmail()))
                            .refreshToken(tokenMapper.mapToDto(rt))
                            .build());
        }

        return responseFactory.fail(HttpStatus.UNAUTHORIZED, "Refresh token đã hết hạn", null);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> forgotPasswordRequest(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String activeCode = String.valueOf(new Random().nextInt(900000) + 100000);
        redisTemplateString.opsForValue().set(activeCode, email);
        redisTemplateString.expire(email, 10, TimeUnit.MINUTES);
        log.info("Redis cached string - {}", redisTemplateString.opsForValue().get(activeCode));

        Message<String> message = MessageBuilder.withPayload(activeCode)
                .setHeader(KafkaHeaders.KEY, email.getBytes())
                .build();
        streamBridge.send("password-forgot", message);
        return responseFactory.success("Mã xác nhận đã được gửi tới email ", email);
    }
    @Override
    public ResponseEntity<BaseResponse<String>> renewPassword(RenewPasswordRequest request) {

        return (ResponseEntity<BaseResponse<String>>) Optional.ofNullable(redisTemplateString.opsForValue().get(request.getCode()))
                .map(email -> {
                    if (!request.getNewPassword().equals(request.getRetypePassword())) {
                        return responseFactory.fail(HttpStatus.BAD_REQUEST, "Mật khẩu không khớp", null);
                    }

                    User user = userRepository.findByEmail(email).get();
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepository.save(user);
                    redisTemplateString.delete(request.getCode());
                    return responseFactory.success("Thiết lập mật khẩu thành công, hãy đăng nhập lại", email);
                })
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Mã xác thực không chính xác"));
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> editProfile(ProfileRequest request) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(customUserDetail.getId())
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Lỗi không xác định, liên hệ admin :D"));  //not happen

        user = userMapper.profileToEntity(request, user);
        user.setModifiedDate(LocalDateTime.now());
        UserResponse response = userMapper.entityToResponse(userRepository.save(user));
        response.setProducts(commonService.getProductsByOwner());
        return responseFactory.success("Cập nhập trang cá nhân thành công!", response);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> changePassword(PasswordChangeRequest request) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetail.getId())
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Lỗi không xác định, liên hệ admin :D"));

        if (!request.getNewPassword().equals(request.getRetypePassword())) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Mật khâu không trùng khớp", null);
        }

        if (!validPassword(request.getOldPassword(), user.getPassword())) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Mật khẩu không chính xác", null);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return responseFactory.success("Thiết lập mật khẩu thành công", "Success");
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> getById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        UserResponse response = userMapper.entityToResponse(user);
        response.setProducts(commonService.getProductsByUser(user.getId()));

        return responseFactory.success("Success", response);
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseUsers>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> users = userRepository.findAll(pageable);
        PageResponseUsers pageResponse = paging(users);
        return responseFactory.success("Success", pageResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> uploadAvatar(MultipartFile file) throws IOException {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String url = fileImageService.saveImageFile(file.getBytes());
        User user = userRepository.findById(userDetail.getId())
                .orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "error"));
        user.setAvatarUrl(url);
        return responseFactory.success("Cập nhật ảnh đại diện thành công.",
                userMapper.entityToResponse(userRepository.save(user)));
    }

    @Override
    public void saveChangeImage(String userId, String field, String path) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        switch (field) {
            case AVATAR -> user.setAvatarUrl(path);
            case COVER -> user.setCoverUrl(path);
            default -> throw new RuntimeException();
        }

        user.setModifiedDate(LocalDateTime.now());
        userRepository.save(user);
    }

    private PageResponseUsers paging(Page<User> users) {
        List<UserResponse> userList = users.getContent()
                .stream().map(entity -> {
                    UserResponse response = userMapper.entityToResponse(entity);
                    response.setProducts(commonService.getProductsByUser(response.getId()));
                    return response;
                })
                .toList();

        return (PageResponseUsers) PageResponseUsers.builder()
                .pageNo(users.getNumber())
                .pageSize(userList.size())
                .content(userList)
                .totalPages(users.getTotalPages())
                .totalItems((int) users.getTotalElements())
                .last(users.isLast())
                .build();
    }

    private boolean validPassword(String rawPassword, String archivePassword) {
        return passwordEncoder.matches(rawPassword, archivePassword);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> assignRole(String role, String userId) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Trigger - {}", "");

        boolean roleMatch = Stream.of(Role.values())
                .map(eRole -> eRole.name())
                .toList()
                .contains(role);
        if (!roleMatch) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Role không phù hợp", null);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        int authorRole = Role.valueOf(String.valueOf(userDetail.getAuthorities().stream().toList().get(0))).getNumVal();
        if (authorRole < Role.valueOf(role).getNumVal()) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Không thể gán quyền cho user có quyền cao hơn bạn", null);
        }

        user.setRole(role);
        userRepository.save(user);

        return responseFactory.success("Success", role);
    }
}

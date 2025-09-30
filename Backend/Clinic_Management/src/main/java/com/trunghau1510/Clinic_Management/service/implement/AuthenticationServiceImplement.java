package com.trunghau1510.Clinic_Management.service.implement;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.trunghau1510.Clinic_Management.dto.request.*;
import com.trunghau1510.Clinic_Management.dto.response.*;
import com.trunghau1510.Clinic_Management.entity.InvalidatedToken;
import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.repository.InvalidatedTokenRepository;
import com.trunghau1510.Clinic_Management.repository.UserRepository;
import com.trunghau1510.Clinic_Management.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImplement implements AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    // Khóa bí mật dùng để ký token
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SINGER_KEY;

    // Thời gian hết hạn token (tính bằng phút)
    @NonFinal
    @Value("${jwt.expired}")
    protected long EXPIRED_TOKEN;

    // Thời gian có thể refresh token (tính bằng phút)
    @NonFinal
    @Value("${jwt.refreshabled}")
    protected long REFRESHED_TOKEN;

    // Xác thực token có hợp lệ không
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Lấy thời gian hết hạn
        Instant expiryTime = (isRefresh)
                ? signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHED_TOKEN, ChronoUnit.MINUTES)
                : signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();

        // Xác minh chữ ký
        var verifiedJWT = signedJWT.verify(verifier);

        // Nếu chữ ký sai hoặc token đã hết hạn thì ném lỗi
        if (!verifiedJWT || expiryTime.isBefore(Instant.now())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Nếu token đã bị invalidate (logout hoặc refresh rồi) thì chặn luôn
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    // Tạo scope từ role user
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRole() != null) stringJoiner.add("ROLE_" + user.getRole());
        return stringJoiner.toString();
    }

    // Sinh JWT mới
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet jwtclaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("clinic-management.com")
                .issueTime(java.util.Date.from(Instant.now())) // convert Instant -> Date
                .expirationTime(java.util.Date.from(Instant.now().plus(EXPIRED_TOKEN, ChronoUnit.MINUTES))) // convert Instant -> Date
                .claim("scope", buildScope(user))
                .jwtID(UUID.randomUUID().toString()) // JTI (dùng để invalidate sau này)
                .build();

        Payload payload = new Payload(jwtclaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Không thể tạo token lỗi:", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            return IntrospectResponse.builder().valid(false).build();
        }
        return IntrospectResponse.builder().valid(true).build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // So khớp mật khẩu
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) throw new AppException(ErrorCode.PASSWORD_FAIL);
        if (!user.getIsActive()) throw new AppException(ErrorCode.IS_ACTIVE_FALSE);

        // Tạo token mới
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        var singedJWT = verifyToken(request.getToken(), true);
        String jit = singedJWT.getJWTClaimsSet().getJWTID();
        Instant expiryTime = singedJWT.getJWTClaimsSet().getExpirationTime().toInstant();

        // Lưu token cũ vào DB để block sau này
        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        // Sinh token mới
        var username = singedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    @Override
    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            var signToken = verifyToken(request.getToken(), false);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Instant expiryTime = signToken.getJWTClaimsSet().getExpirationTime().toInstant();

            // Lưu token đã logout để không cho dùng lại
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);

        } catch (AppException e) {
            log.info("Token trong logout có lỗi", e);
        }
    }
}

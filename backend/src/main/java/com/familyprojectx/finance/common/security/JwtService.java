package com.familyprojectx.finance.common.security;

import com.familyprojectx.finance.common.exception.ApiException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secret;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.secret = secret;
        this.expirationSeconds = expirationMinutes * 60;
    }

    public String generateToken(UUID userId, String email) {
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        Instant now = Instant.now();
        String payload = base64Url("{\"sub\":\"" + userId + "\",\"email\":\"" + email + "\",\"iat\":" + now.getEpochSecond()
                + ",\"exp\":" + now.plusSeconds(expirationSeconds).getEpochSecond() + "}");
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public UUID extractUserId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        long expiration = Long.parseLong(value(payload, "exp"));
        if (Instant.now().getEpochSecond() > expiration) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token expired");
        }
        return UUID.fromString(value(payload, "sub"));
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign token", exception);
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String value(String json, String key) {
        String stringPattern = "\"" + key + "\":\"";
        int stringStart = json.indexOf(stringPattern);
        if (stringStart >= 0) {
            int valueStart = stringStart + stringPattern.length();
            int valueEnd = json.indexOf('"', valueStart);
            return json.substring(valueStart, valueEnd);
        }
        String numberPattern = "\"" + key + "\":";
        int numberStart = json.indexOf(numberPattern);
        if (numberStart >= 0) {
            int valueStart = numberStart + numberPattern.length();
            int valueEnd = json.indexOf(',', valueStart);
            if (valueEnd < 0) {
                valueEnd = json.indexOf('}', valueStart);
            }
            return json.substring(valueStart, valueEnd);
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token payload");
    }
}

package com.accounting.api;

import com.accounting.model.UserToken;
import com.accounting.repository.UserTokenRepository;
import com.accounting.service.UserService;
import com.accounting.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
/**
 * 认证控制器 (Authentication Controller)
 * <p>
 * 负责处理所有与用户身份认证相关的 HTTP 请求。
 * 核心功能包括：
 * 1. 用户注册 (Register)：创建新用户并分发恢复密钥。
 * 2. 用户登录 (Login)：验证身份并签发双 Token (Access + Refresh)。
 * 3. 令牌刷新 (Refresh Token)：通过长效 Token 换取新的短效 Token，实现无感续期。
 * 4. 密码重置 (Reset Password)：通过恢复密钥重置用户密码。
 * </p>
 */
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserTokenRepository userTokenRepository;
    
    // 短效 Access Token 有效期：30分钟
    private static final long ACCESS_TOKEN_TTL = 30 * 60 * 1000; 
    // 长效 Refresh Token 有效期：7天
    private static final long REFRESH_TOKEN_TTL = 7L * 24 * 60 * 60 * 1000; 
    
    public AuthController(UserService userService, JwtUtil jwtUtil, UserTokenRepository userTokenRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userTokenRepository = userTokenRepository;
    }
    
    /**
     * 用户注册接口
     * <p>
     * 接收用户名和密码，进行二次确认校验后创建用户。
     * 注册成功后，必须返回生成的【恢复密钥】，这是用户找回密码的唯一凭证。
     * </p>
     * @param body 包含 username, password, confirmPassword 的 JSON 对象
     * @return 注册成功的用户信息（包含 recoveryKey）或错误提示
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String confirmPassword = body.get("confirmPassword");
        
        // 基础非空校验：防止无效请求进入业务层
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_input"));
        }
        
        // 密码一致性校验：防止用户手误
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("error", "passwords_do_not_match"));
        }

        try {
            // 调用 Service 层执行核心注册逻辑
            var u = userService.register(username.trim(), password);
            
            // 返回关键信息，特别是 recoveryKey，前端需弹窗提示用户保存
            return ResponseEntity.ok(Map.of(
                "id", u.getId(), 
                "username", u.getUsername(),
                "recoveryKey", u.getRecoveryKey()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", "username_exists"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "server_error"));
        }
    }
    
    /**
     * 密码重置接口
     * <p>
     * 当用户忘记密码时调用。
     * 安全机制：必须提供注册时生成的【恢复密钥】才能重置，不依赖邮箱或手机号。
     * </p>
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String recoveryKey = body.get("recoveryKey");
        String newPassword = body.get("newPassword");
        
        if (username == null || recoveryKey == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_input"));
        }
        
        try {
            // 调用 Service 层验证密钥并更新密码
            userService.resetPassword(username.trim(), recoveryKey.trim(), newPassword);
            return ResponseEntity.ok(Map.of("message", "password_reset_success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "server_error"));
        }
    }
    
    /**
     * 用户登录接口
     * <p>
     * 验证用户名密码，成功后签发双 Token。
     * 1. Access Token: 用于访问 API，有效期短。
     * 2. Refresh Token: 用于刷新 Access Token，有效期长，绑定设备 ID。
     * 同时实现了多设备登录管理（最多允许 5 个设备同时在线）。
     * </p>
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        // 获取设备标识，用于多端会话管理（默认为 unknown）
        String deviceId = body.getOrDefault("deviceId", "unknown");
        
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_input"));
        }
        
        // 验证凭据
        var opt = userService.login(username, password);
        if (opt.isEmpty()) return ResponseEntity.status(401).build(); // 认证失败
        
        var user = opt.get();
        
        // 生成双Token机制：短效 AccessToken 用于接口鉴权，长效 RefreshToken 用于会话续期
        String accessToken = jwtUtil.generate(user.getId(), ACCESS_TOKEN_TTL);
        String refreshToken = UUID.randomUUID().toString(); // 随机生成不透明字符串作为刷新令牌
        
        // 将 Refresh Token 持久化到数据库，实现服务端可控（如强制下线）
        UserToken userToken = new UserToken(
            user.getId(),
            refreshToken,
            deviceId,
            LocalDateTime.now().plusDays(7)
        );
        userTokenRepository.save(userToken);
        
        // 多端会话治理策略：
        // 限制每个用户最多 5 个活跃会话。
        // 如果超过限制，按过期时间排序，踢掉最旧的会话（FIFO）。
        var tokens = userTokenRepository.findByUserId(user.getId());
        if (tokens.size() > 5) {
            tokens.stream()
                    .sorted((a, b) -> b.getExpiryDate().compareTo(a.getExpiryDate())) // 按过期时间降序（保留最新的）
                    .skip(5)
                    .forEach(userTokenRepository::delete);
        }
        
        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "token", accessToken, // 兼容性字段
            "refreshToken", refreshToken,
            "userId", user.getId(),
            "username", user.getUsername()
        ));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }
        
        var tokenOpt = userTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_token"));
        }
        
        UserToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            userTokenRepository.delete(token);
            return ResponseEntity.status(401).body(Map.of("error", "token_expired"));
        }
        
        // Generate new Access Token
        // Need username. Fetch user or store username in UserToken? 
        // UserToken has userId.
        // Assuming UserService can findById, or we add username to UserToken.
        // Let's fetch user by ID.
        var userOpt = userService.getUserById(token.getUserId());
        if (userOpt.isEmpty()) {
             return ResponseEntity.status(401).build();
        }
        
        String newAccessToken = jwtUtil.generate(userOpt.get().getId(), ACCESS_TOKEN_TTL);
        // Rotate refresh token? Or keep same? 
        // 最佳实践：刷新时旋转RefreshToken并续期
        String newRefreshToken = UUID.randomUUID().toString();
        token.setToken(newRefreshToken);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        userTokenRepository.save(token);
        // 多端管理（上限5台）：刷新后也进行上限治理
        var all = userTokenRepository.findByUserId(token.getUserId());
        if (all.size() > 5) {
            all.stream()
               .sorted((a, b) -> b.getExpiryDate().compareTo(a.getExpiryDate()))
               .skip(5)
               .forEach(userTokenRepository::delete);
        }
        
        return ResponseEntity.ok(Map.of(
            "accessToken", newAccessToken,
            "token", newAccessToken, // 兼容前端：返回token字段
            "refreshToken", newRefreshToken
        ));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken != null) {
            // 登出时移除对应的刷新Token，实现设备级登出
            userTokenRepository.deleteByToken(refreshToken);
        }
        return ResponseEntity.ok().build();
    }
}

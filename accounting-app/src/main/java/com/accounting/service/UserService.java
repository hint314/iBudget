package com.accounting.service;

import com.accounting.model.User;
import com.accounting.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
/**
 * 用户业务逻辑服务 (User Service)
 * <p>
 * 封装了所有与用户账户管理相关的核心业务规则。
 * 遵循“瘦 Controller，胖 Service”的设计原则，确保业务逻辑的复用性和一致性。
 * </p>
 */
public class UserService {
    private final UserRepository userRepository;
    // 使用 BCrypt 强哈希算法，自动处理加盐(Salt)，防止彩虹表攻击
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注册新用户
     * <p>
     * 1. 检查用户名唯一性。
     * 2. 执行强密码策略校验。
     * 3. 对密码进行哈希加密。
     * 4. 创建用户实体（自动生成恢复密钥）。
     * </p>
     * @param username 用户名
     * @param password 原始密码（明文）
     * @return 持久化后的用户实体
     * @throws IllegalStateException 如果用户名已存在
     * @throws IllegalArgumentException 如果密码不符合规则
     */
    public User register(String username, String password) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalStateException("username exists");
        }
        
        // 密码规则校验：至少6位，且包含字母和数字
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("password too short");
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("password must contain letters and numbers");
        }

        User u = new User(username, encoder.encode(password));
        return userRepository.save(u);
    }

    /**
     * 用户登录验证
     * <p>
     * 比较输入的明文密码与数据库中的哈希值是否匹配。
     * </p>
     * @return 如果验证成功返回 Optional<User>，否则返回 Empty
     */
    public Optional<User> login(String username, String password) {
        Optional<User> u = findByUsername(username);
        if (u.isPresent() && encoder.matches(password, u.get().getPasswordHash())) {
            return u;
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    /**
     * 重置密码
     * <p>
     * 使用恢复密钥 (Recovery Key) 进行鉴权。
     * 重置成功后，会自动生成一个新的恢复密钥，旧密钥立即作废（一次性使用原则）。
     * </p>
     * @param recoveryKey 用户提供的恢复密钥
     * @param newPassword 新密码
     */
    public void resetPassword(String username, String recoveryKey, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));
                
        // 核心安全校验：比对恢复密钥
        if (!recoveryKey.equals(user.getRecoveryKey())) {
            throw new IllegalArgumentException("invalid_recovery_key");
        }
        
        // 校验新密码强度
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("password too short");
        }
        if (!newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("password must contain letters and numbers");
        }
        
        user.setPasswordHash(encoder.encode(newPassword));
        // 安全策略：恢复密钥使用后立即轮换 (Rotate)，防止旧密钥泄露带来的风险
        user.setRecoveryKey(UUID.randomUUID().toString().substring(0, 8));
        userRepository.save(user);
    }
}

package com.accounting.model;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户实体类 (User Entity)
 * <p>
 * 对应数据库中的 'users' 表。
 * 存储用户的核心身份信息。
 * 注意：本系统不存储用户明文密码，仅存储加盐哈希值。
 * </p>
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @SerializedName("id")
    private String id; // UUID 主键
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("passwordHash")
    private String passwordHash; // 经过 BCrypt 加密的密码哈希，绝不存储明文
    
    @SerializedName("createdAt")
    private LocalDateTime createdAt;
    
    @SerializedName("lastSyncAt")
    private LocalDateTime lastSyncAt; // 上次同步时间
    
    @SerializedName("deviceId")
    private String deviceId; // 注册设备标识
    
    /**
     * 恢复密钥 (Recovery Key)
     * <p>
     * 这是一个随机生成的 8 位字符串，作为用户找回密码的唯一凭证。
     * 当用户注册成功时生成，当用户重置密码成功后会自动更新。
     * </p>
     */
    @SerializedName("recoveryKey")
    private String recoveryKey; 
    
    public User() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        // 默认生成 8 位随机字符作为恢复密钥
        this.recoveryKey = UUID.randomUUID().toString().substring(0, 8); 
    }
    
    public User(String username, String passwordHash) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getRecoveryKey() {
        return recoveryKey;
    }

    public void setRecoveryKey(String recoveryKey) {
        this.recoveryKey = recoveryKey;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastSyncAt() {
        return lastSyncAt;
    }
    
    public void setLastSyncAt(LocalDateTime lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}


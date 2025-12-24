package com.accounting.model;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户实体类
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @SerializedName("id")
    private String id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("passwordHash")
    private String passwordHash; // 密码哈希值
    
    @SerializedName("createdAt")
    private LocalDateTime createdAt;
    
    @SerializedName("lastSyncAt")
    private LocalDateTime lastSyncAt;
    
    @SerializedName("deviceId")
    private String deviceId; // 设备标识
    
    @SerializedName("recoveryKey")
    private String recoveryKey; // 恢复密钥
    
    public User() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.recoveryKey = UUID.randomUUID().toString().substring(0, 8); // 8位随机恢复密钥
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


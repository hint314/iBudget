package com.accounting.ui;

import com.accounting.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 桌面端 API 客户端 (Desktop API Client)
 * <p>
 * 负责桌面应用与后端 REST API 的通信。
 * 核心功能：
 * 1. 封装 HTTP 请求：统一处理 GET/POST 请求及 JSON 序列化/反序列化。
 * 2. 身份认证：自动管理 Token，并在请求头中携带 "Authorization: Bearer ..."。
 * 3. 异常处理：将 HTTP 状态码转换为 Java 运行时异常。
 * </p>
 */
public class ApiClient {
    private final String baseUrl;
    // 使用 Java 11+ 标准 HttpClient，无需引入第三方庞大依赖
    private final HttpClient client = HttpClient.newHttpClient();
    // Jackson JSON 处理器，配置 JavaTimeModule 以支持 LocalDateTime
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
    private String token; // 当前会话的 Access Token

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 用户注册
     * <p>
     * 发送注册请求，并接收包含恢复密钥的响应。
     * </p>
     */
    public Map<String,Object> register(String username, String password, String confirmPassword) {
        try {
            String body = mapper.writeValueAsString(Map.of("username", username, "password", password, "confirmPassword", confirmPassword));
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() != 200) throw new RuntimeException("register failed: " + resp.statusCode());
            return mapper.readValue(resp.body(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户登录
     * <p>
     * 登录成功后，会自动将返回的 Token 保存到内存中 (this.token)。
     * 后续所有请求都会自动使用该 Token。
     * </p>
     */
    public String login(String username, String password) {
        try {
            String body = mapper.writeValueAsString(Map.of("username", username, "password", password));
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() != 200) throw new RuntimeException("login failed: " + resp.statusCode());
            Map<String,Object> map = mapper.readValue(resp.body(), Map.class);
            this.token = (String) map.get("token");
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取交易列表
     * <p>
     * 演示了如何在请求头中添加 Bearer Token 进行鉴权。
     * </p>
     */
    public List<Transaction> listTransactions() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/api/sync/transactions"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() != 200) throw new RuntimeException("list failed: " + resp.statusCode());
            return mapper.readValue(resp.body(), mapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传交易
     * <p>
     * 用于数据同步，将本地新增的数据推送到服务器。
     * </p>
     */
    public List<Transaction> uploadTransactions(List<Transaction> txs) {
        try {
            String body = mapper.writeValueAsString(txs);
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/api/sync/transactions/upload"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() != 200) throw new RuntimeException("upload failed: " + resp.statusCode());
            return mapper.readValue(resp.body(), mapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }
}

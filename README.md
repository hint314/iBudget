# iBudget - 智能记账应用

一个现代化的跨平台记账应用，支持Web端和桌面端。采用Spring Boot + JavaFX架构，提供完整的账目管理、智能预算、数据分析和AI洞察功能。

## 📋 项目概述

**iBudget** 是一个功能完整的记账软件，包含Web界面和桌面应用两种使用方式，提供以下核心功能:

### 🧩 核心功能

#### 1. **账目管理**
- ✅ 交易记录的增删改查
- ✅ 支出/收入类型切换(扁平化按钮组)
- ✅ 智能日历选择器
- ✅ 实时账目列表显示
- ✅ 多条件过滤(日期范围、分类、类型)
- ✅ 编辑和删除功能
- ✅ 自动刷新机制

#### 2. **预算管理**
- ✅ 双模式预算: 月度预算 + 周期型预算
- ✅ 周期型预算支持: 天/周/月/年
- ✅ 实时统计分析:
  - 已消费金额
  - 剩余预算
  - 日均预算额度
  - 实际日均消费
  - 使用率进度条
- ✅ 智能超支预测(基于日均消费速率)
- ✅ 三级预警系统:
  - ⚠️ 预测超支 (按当前速率会超支)
  - 🔴 已超额 (当前已超过预算)
  - ⚠️ 预警 (使用率≥80%)
- ✅ 详细统计弹窗(13项数据指标)

#### 3. **数据同步与认证**
- ✅ 用户注册/登录系统
- ✅ JWT Token认证
- ✅ 离线数据缓存
- ✅ REST API接口
- ✅ Token过期自动处理
- ✅ 403错误友好提示

#### 4. **图表与趋势分析**
- ✅ 月度支出柱状图
- ✅ 分类支出饼图
- ✅ 收支趋势折线图
- ✅ 多维度数据可视化
- ✅ 图表导出功能

#### 5. **AI智能分析** (本地模型)
- ✅ 消费习惯分析
- ✅ 智能建议生成
- ✅ 异常支出检测
- ✅ 本地AI模型(无需联网)

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 3.1.5
- **安全**: Spring Security + JWT
- **数据库**: SQLite + JPA/Hibernate
- **API**: RESTful风格

### 前端
- **Web界面**: HTML5 + CSS3 + 原生JavaScript
- **桌面端**: JavaFX 21
- **主题**: 亮色/暗色模式切换
- **UI设计**: 扁平化现代风格

### 数据与工具
- **数据格式**: JSON
- **构建工具**: Maven
- **版本控制**: Git

## 📁 项目结构

```
iBudget-1/
├── accounting-app/
│   ├── pom.xml                              # Maven配置
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/accounting/
│   │   │   │   ├── AccountingApplication.java   # Spring Boot主入口
│   │   │   │   ├── model/                       # 数据模型层
│   │   │   │   │   ├── Transaction.java         # 交易实体
│   │   │   │   │   ├── Budget.java              # 预算实体(支持周期型)
│   │   │   │   │   ├── User.java                # 用户实体
│   │   │   │   │   └── UserToken.java           # Token实体
│   │   │   │   ├── service/                     # 业务逻辑层
│   │   │   │   │   ├── TransactionService.java  # 交易服务
│   │   │   │   │   ├── BudgetService.java       # 预算服务(含统计)
│   │   │   │   │   ├── UserService.java         # 用户服务
│   │   │   │   │   └── LocalAIAnalysisService.java # 本地AI分析
│   │   │   │   ├── api/                          # REST API控制器
│   │   │   │   │   ├── TransactionsController.java
│   │   │   │   │   ├── BudgetController.java
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   └── StatsController.java
│   │   │   │   ├── repository/                   # JPA数据访问层
│   │   │   │   │   ├── TransactionRepository.java
│   │   │   │   │   ├── BudgetRepository.java
│   │   │   │   │   └── UserRepository.java
│   │   │   │   ├── config/                       # 配置类
│   │   │   │   │   ├── SecurityConfig.java       # Spring Security配置
│   │   │   │   │   └── JwtAuthenticationFilter.java # JWT过滤器
│   │   │   │   ├── filter/                       # 过滤规则
│   │   │   │   │   └── FilterRule.java
│   │   │   │   ├── chart/                        # 图表生成
│   │   │   │   │   └── ComboBarChartView.java
│   │   │   │   └── ui/                           # JavaFX桌面UI
│   │   │   │       └── MainApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties        # Spring Boot配置
│   │   │       └── static/                       # Web静态资源
│   │   │           ├── index.html                # 首页
│   │   │           ├── login.html                # 登录页
│   │   │           ├── transactions.html         # 账目管理页
│   │   │           ├── budgets.html              # 预算管理页
│   │   │           ├── trends.html               # 趋势分析页
│   │   │           └── charts.html               # 图表展示页
│   │   └── test/                                 # 测试代码
│   └── accounting.db                             # SQLite数据库
└── README.md
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6+
- **浏览器**: Chrome/Firefox/Safari (支持ES6)
- **操作系统**: Windows/Linux/macOS

### 安装与运行

#### 方式一: Web界面(推荐)

1. **编译项目**
   ```bash
   cd accounting-app
   mvn clean compile
   ```

2. **启动服务器**
   ```bash
   mvn spring-boot:run
   ```
   
3. **访问应用**
   - 打开浏览器访问: `http://localhost:8080`
   - 首次使用需要注册账号
   - 登录后即可使用所有功能

#### 方式二: 桌面应用

**Windows系统:**
```bash
run-desktop.bat
```

**Linux/Mac系统:**
```bash
mvn exec:java -Dexec.mainClass="com.accounting.ui.MainApplication"
```

## 📖 功能使用指南

### 1️⃣ 账目管理 (/transactions.html)

**添加账目:**
1. 选择类型(支出/收入) - 使用扁平化按钮切换
2. 输入金额
3. 填写分类(如: 餐饮、交通、娱乐)
4. 添加描述(可选)
5. 点击日期输入框,使用智能日历选择日期
6. 点击"保存账目"按钮
7. 账目会立即显示在下方列表中

**筛选账目:**
- 设置开始和结束时间(格式: 2025-12-23T00:00:00)
- 输入分类名称
- 选择类型(全部/支出/收入)
- 点击"执行筛选"

**编辑/删除:**
- 点击账目列表中的"编辑"按钮修改
- 点击"删除"按钮移除账目

### 2️⃣ 预算管理 (/budgets.html)

**创建月度预算:**
1. 点击"月度预算"按钮
2. 选择年份和月份
3. 输入分类(留空则为总预算)
4. 设置预算金额
5. 点击"设置预算"

**创建周期型预算:**
1. 点击"周期型预算"按钮
2. 选择开始日期
3. 选择周期单位(天/周/月/年)
4. 设置周期数量
5. 输入预算金额
6. 点击"创建周期型预算"

**查看预算分析:**
- 预算列表自动显示:
  - 预算金额
  - 已消费金额(绿色)
  - 剩余金额(红色表示超支)
  - 日均预算
  - 实际日均消费
  - 使用率进度条
  - 智能状态(健康/预警/超支/预测超支)
- 点击"详情"按钮查看完整的13项统计数据

### 3️⃣ 趋势分析 (/trends.html)

- 查看月度收支对比
- 分析消费趋势变化
- 查看分类占比

### 4️⃣ 图表展示 (/charts.html)

- 柱状图: 月度支出趋势
- 饼图: 分类支出占比
- 折线图: 收支变化曲线
- 支持图表导出

### 5️⃣ 主题切换

- 点击顶部导航栏的"切换主题"按钮
- 支持亮色/暗色两种模式
- 设置会自动保存

## 🔌 API接口文档

### 认证相关

#### 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "user@example.com",
  "password": "password123"
}
```

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

响应:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "user-id"
}
```

### 账目管理

#### 获取账目列表
```http
GET /api/transactions?start=2025-01-01T00:00:00&end=2025-12-31T23:59:59&type=EXPENSE
Authorization: Bearer <token>
```

#### 创建账目
```http
POST /api/transactions
Authorization: Bearer <token>
Content-Type: application/json

{
  "type": "EXPENSE",
  "amount": 100.0,
  "categoryId": "餐饮",
  "description": "午餐",
  "date": "2025-12-23T12:00:00"
}
```

### 预算管理

#### 创建周期型预算
```http
POST /api/budgets
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": "user-id",
  "categoryId": "餐饮",
  "amount": 1000.0,
  "startDate": "2025-12-01",
  "periodUnit": "MONTHS",
  "periodCount": 1,
  "year": 2025,
  "month": 12
}
```

#### 获取预算统计
```http
GET /api/budgets/stats/{budgetId}
Authorization: Bearer <token>

响应:
{
  "budget": {...},
  "daysElapsed": 23,
  "totalDays": 31,
  "amountSpent": 650.0,
  "remaining": 350.0,
  "avgPerDayBudget": 32.26,
  "avgPerDayActual": 28.26,
  "projectedTotalByAvgSoFar": 875.87,
  "projectedRemainingByAvgSoFar": 124.13,
  "willBeOverspentByAvg": false,
  "last7DaysSpent": 180.0,
  "last30DaysSpent": 650.0
}
```

## 🔧 配置说明

### application.properties

```properties
# 服务器配置
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:sqlite:accounting.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update

# JWT配置
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# 日志配置
logging.level.root=INFO
logging.level.com.accounting=DEBUG
```

### 数据库Schema修复

如果遇到周期型预算创建失败(HTTP 500错误),需要修复数据库schema:

```sql
-- 备份数据
CREATE TABLE budgets_backup AS SELECT * FROM budgets;

-- 删除旧表
DROP TABLE budgets;

-- 创建新表(period_unit为varchar)
CREATE TABLE budgets (
    id varchar(255) not null,
    amount float not null,
    category_id varchar(255),
    created_at bigint,
    month integer not null,
    updated_at bigint,
    user_id varchar(255),
    year integer not null,
    period_count integer,
    period_unit varchar(20),
    start_date date,
    primary key (id)
);

-- 恢复数据
INSERT INTO budgets SELECT * FROM budgets_backup;
DROP TABLE budgets_backup;
```

## 🎯 功能特性

### ✅ 已实现功能

**账目管理:**
- [x] 交易记录增删改查
- [x] 扁平化类型切换按钮(支出/收入)
- [x] 智能日历选择器
- [x] 实时账目列表显示
- [x] 多条件过滤(日期/分类/类型)
- [x] 自动刷新机制

**预算管理:**
- [x] 月度预算设置
- [x] 周期型预算(天/周/月/年)
- [x] 实时统计分析(13项指标)
- [x] 智能超支预测
- [x] 三级预警系统
- [x] 详细统计弹窗
- [x] 已消费/剩余金额显示
- [x] 日均预算/实际对比

**用户系统:**
- [x] 用户注册/登录
- [x] JWT Token认证
- [x] Token过期自动处理
- [x] 403错误友好提示

**数据分析:**
- [x] 柱状图(月度趋势)
- [x] 饼图(分类占比)
- [x] 折线图(收支变化)
- [x] 本地AI智能分析

**UI/UX:**
- [x] 亮色/暗色主题切换
- [x] 扁平化现代设计
- [x] 响应式布局
- [x] 平滑动画过渡
- [x] 统一按钮组样式

### 🔮 技术亮点

- **智能预算算法**: 基于日均消费速率的超支预测
- **双模式预算**: 支持传统月度预算和灵活的周期型预算
- **周期计算引擎**: 自动计算预算起止日期和总天数
- **统计分析系统**: 13项实时数据指标计算
- **本地AI模型**: 离线智能分析,保护隐私
- **JWT认证**: 安全的Token机制
- **数据库兼容**: 向后兼容旧数据结构

## 🐛 常见问题

### 1. 创建周期型预算失败(HTTP 500)

**问题**: 数据库schema约束冲突

**解决方案**: 
```bash
# 停止服务器
pkill -f accounting-app

# 执行数据库修复SQL(见配置说明章节)
sqlite3 accounting.db < fix-schema.sql

# 重启服务器
mvn spring-boot:run
```

### 2. 登录后显示HTTP 403错误

**原因**: JWT Token过期或无效

**解决方案**: 
- 系统会自动提示"登录已过期,请重新登录"
- 重新登录即可获取新Token

### 3. 日期筛选不生效

**原因**: 日期格式不正确

**正确格式**: `YYYY-MM-DDTHH:mm:ss`

**示例**: `2025-12-23T00:00:00`

**注意**: 开始和结束时间必须同时填写

### 4. JavaFX桌面应用启动失败

**解决方案**:
```bash
# 确保JDK 17+
java -version

# 清理重新编译
mvn clean compile

# 使用Maven运行
mvn exec:java -Dexec.mainClass="com.accounting.ui.MainApplication"
```

## 📝 开发说明

### 架构设计

**分层架构:**
- **Controller层**: 处理HTTP请求,参数验证
- **Service层**: 业务逻辑,数据计算
- **Repository层**: JPA数据访问
- **Model层**: 数据实体定义
- **Config层**: 安全、JWT等配置

**设计模式:**
- **策略模式**: FilterRule实现多条件过滤
- **Builder模式**: Budget统计数据构建
- **单例模式**: JWT工具类
- **过滤器模式**: Spring Security认证链

### 关键技术实现

**1. 周期型预算计算**
```java
public LocalDate getEndDate() {
    if (startDate == null || periodUnit == null || periodCount == null) 
        return null;
    switch (periodUnit) {
        case DAYS: return startDate.plusDays(periodCount - 1);
        case WEEKS: return startDate.plusWeeks(periodCount).minusDays(1);
        case MONTHS: return startDate.plusMonths(periodCount).minusDays(1);
        case YEARS: return startDate.plusYears(periodCount).minusDays(1);
    }
}
```

**2. 超支预测算法**
```java
double avgPerDayActual = amountSpent / daysElapsed;
double projectedTotal = avgPerDayActual * totalDays;
boolean willBeOverspent = projectedTotal > budget.getAmount();
```

**3. JWT认证流程**
```java
// 生成Token
String token = jwtUtil.generateToken(username);

// 验证Token
if (jwtUtil.validateToken(token)) {
    String username = jwtUtil.getUsernameFromToken(token);
    // 设置认证信息
}
```

### 贡献指南

欢迎提交Issue和Pull Request!

**代码规范:**
- 使用驼峰命名
- 保持方法简洁(<50行)
- 添加必要注释
- 遵循RESTful API规范

## 📄 许可证

本项目采用 MIT 许可证。

## 🙏 致谢

- Spring Boot团队提供的优秀框架
- JavaFX社区的技术支持
- 所有贡献者和使用者

## 📧 联系方式

- 提交Issue: [GitHub Issues](https://github.com/kris2024kris-max/iBudget/issues)
- 项目主页: [iBudget](https://github.com/kris2024kris-max/iBudget)

---

**iBudget** - 让记账变得简单智能 💰✨


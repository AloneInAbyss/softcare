# 🏗️ Diagrama de Arquitetura - SoftCare Backend

## 📊 Arquitetura Hexagonal

```mermaid
graph TB
    %% ===== CAMADA EXTERNA =====
    subgraph EXTERNAL ["🌐 CAMADA EXTERNA"]
        direction TB
        CLIENT["👤 Client Applications<br/>🔸 Web Browser<br/>🔸 Mobile App<br/>🔸 REST Clients"]
        SWAGGER["📚 Swagger UI<br/>🔸 API Documentation<br/>🔸 Interactive Testing<br/>🔸 OpenAPI 3.0"]
        MONGO["🗄️ MongoDB Database<br/>🔸 Document Storage<br/>🔸 NoSQL Operations<br/>🔸 Atlas Cloud"]
        DOCKER["🐳 Docker Environment<br/>🔸 Containerization<br/>🔸 Microservices<br/>🔸 Development Setup"]
    end
    
    %% ===== CAMADA DE INTERFACE =====
    subgraph INTERFACE ["🎯 CAMADA DE INTERFACE - REST Controllers"]
        direction LR
        UC["👤 UserController<br/>━━━━━━━━━━━━━━<br/>🟢 POST /users<br/>🔵 GET /users/{id}<br/>🟡 PUT /users/{id}<br/>🔴 DELETE /users/{id}<br/>🟠 POST /users/login"]
        
        SCC["📞 SupportChannelController<br/>━━━━━━━━━━━━━━━━━━━━━<br/>🟢 POST /support-channels<br/>🔵 GET /support-channels<br/>🔍 GET /search?type=<br/>🟡 PUT /support-channels/{id}<br/>🔴 DELETE /support-channels/{id}"]
        
        EDC["📔 EmotionalDiaryController<br/>━━━━━━━━━━━━━━━━━━━━━<br/>🟢 POST /emotional-diary<br/>🔵 GET /emotional-diary<br/>📊 GET /user/{id}/entries<br/>📈 GET /analytics/{userId}<br/>🟡 PUT /emotional-diary/{id}"]
        
        PAC["🧠 PsychosocialAssessmentController<br/>━━━━━━━━━━━━━━━━━━━━━━━━━━━━<br/>🟢 POST /assessments<br/>🔵 GET /assessments/{id}<br/>📊 GET /user/{id}/assessments<br/>⚠️ GET /risk-analysis/{userId}<br/>🟡 PUT /assessments/{id}"]
        
        ALC["📊 AuditLogController<br/>━━━━━━━━━━━━━━━━━<br/>🔵 GET /audit-logs<br/>🔍 GET /search?event=<br/>👤 GET /user/{id}/logs<br/>📅 GET /date-range<br/>⚠️ GET /security-events"]
    end
    
    %% ===== CAMADA DE APLICAÇÃO =====
    subgraph APPLICATION ["🔧 CAMADA DE APLICAÇÃO - Business Services"]
        direction LR
        US["👤 UserService<br/>━━━━━━━━━━━━<br/>🔐 Password Encryption<br/>✅ Data Validation<br/>🔍 User Authentication<br/>👥 Role Management<br/>📧 Email Verification"]
        
        SCS["📞 SupportChannelService<br/>━━━━━━━━━━━━━━━━━━━<br/>🔍 Advanced Search<br/>📱 Contact Validation<br/>⏰ Availability Check<br/>🏷️ Category Management<br/>📊 Usage Analytics"]
        
        EDS["📔 EmotionalDiaryService<br/>━━━━━━━━━━━━━━━━━━━<br/>📈 Mood Trend Analysis<br/>🧮 Wellness Calculation<br/>📊 Statistical Reports<br/>🔔 Alert Generation<br/>📅 Timeline Management"]
        
        PAS["🧠 PsychosocialAssessmentService<br/>━━━━━━━━━━━━━━━━━━━━━━━━━━<br/>🧮 Risk Score Calculation<br/>📊 Assessment Analytics<br/>⚠️ Alert Threshold<br/>📈 Progress Tracking<br/>💡 Recommendations Engine"]
        
        ALS["📊 AuditLogService<br/>━━━━━━━━━━━━━━━<br/>🔒 Security Event Logging<br/>📝 Activity Tracking<br/>⚡ Real-time Monitoring<br/>📊 Audit Reports<br/>🔍 Forensic Analysis"]
    end
    
    %% ===== CAMADA DE DOMÍNIO =====
    subgraph DOMAIN ["📦 CAMADA DE DOMÍNIO - Core Business Logic"]
        direction TB
        
        subgraph MODELS ["🏷️ DOMAIN MODELS"]
            UMODEL["👤 User Entity<br/>━━━━━━━━━━━<br/>🆔 String id<br/>📧 String email<br/>👤 String name<br/>🔐 String password<br/>👥 UserRole role<br/>📅 LocalDateTime createdAt<br/>📝 LocalDateTime updatedAt"]
            
            SCMODEL["📞 SupportChannel Entity<br/>━━━━━━━━━━━━━━━━━━━<br/>🆔 String id<br/>📛 String name<br/>📝 String description<br/>🏷️ String type<br/>📱 String contactInfo<br/>⏰ String availability<br/>📅 LocalDateTime createdAt"]
            
            EDMODEL["📔 EmotionalDiary Entity<br/>━━━━━━━━━━━━━━━━━━━<br/>🆔 String id<br/>👤 String userId<br/>📅 LocalDate date<br/>😊 MoodLevel moodLevel<br/>📝 String notes<br/>😰 Integer stressLevel<br/>🏃 List activities<br/>📅 LocalDateTime createdAt"]
            
            PAMODEL["🧠 PsychosocialAssessment<br/>━━━━━━━━━━━━━━━━━━━━━━<br/>🆔 String id<br/>👤 String userId<br/>📊 Integer score<br/>⚠️ RiskLevel riskLevel<br/>📋 Map answers<br/>💡 List recommendations<br/>📅 LocalDateTime assessedAt"]
            
            ALMODEL["📊 AuditLog Entity<br/>━━━━━━━━━━━━━━━<br/>🆔 String id<br/>👤 String userId<br/>⏰ LocalDateTime timestamp<br/>🏷️ EventType eventType<br/>⚠️ Severity severity<br/>📝 String details<br/>✅ Boolean success"]
        end
        
        subgraph DTOS ["📋 DATA TRANSFER OBJECTS"]
            USER_DTOS["👤 User DTOs<br/>━━━━━━━━━━━<br/>� CreateUserRequest<br/>📤 UserResponse<br/>🔐 LoginRequest<br/>🔄 UpdateUserRequest<br/>🔍 UserSearchCriteria"]
            
            SUPPORT_DTOS["📞 Support DTOs<br/>━━━━━━━━━━━━━━<br/>📝 CreateSupportChannelRequest<br/>📤 SupportChannelResponse<br/>🔍 SearchSupportChannelRequest<br/>🔄 UpdateSupportChannelRequest"]
            
            DIARY_DTOS["📔 Diary DTOs<br/>━━━━━━━━━━━━━<br/>📝 CreateEmotionalDiaryRequest<br/>📤 EmotionalDiaryResponse<br/>📊 DiaryAnalyticsResponse<br/>🔄 UpdateEmotionalDiaryRequest"]
            
            ASSESSMENT_DTOS["🧠 Assessment DTOs<br/>━━━━━━━━━━━━━━━━━━<br/>📝 CreateAssessmentRequest<br/>📤 AssessmentResponse<br/>📊 RiskAnalysisResponse<br/>🔄 UpdateAssessmentRequest"]
            
            AUDIT_DTOS["📊 Audit DTOs<br/>━━━━━━━━━━━━━<br/>📤 AuditLogResponse<br/>🔍 AuditSearchRequest<br/>📊 AuditReportResponse<br/>📅 DateRangeRequest"]
        end
        
        subgraph ENUMS ["🔤 DOMAIN ENUMERATIONS"]
            ENUMS_LIST["🏷️ Business Enums<br/>━━━━━━━━━━━━━━━<br/>👥 UserRole (ADMIN, USER)<br/>😊 MoodLevel (1-10)<br/>⚠️ RiskLevel (LOW, MEDIUM, HIGH)<br/>🏷️ EventType (LOGIN, CREATE, UPDATE)<br/>⚠️ Severity (INFO, WARN, ERROR)"]
        end
    end
    
    %% ===== CAMADA DE INFRAESTRUTURA =====
    subgraph INFRASTRUCTURE ["🗄️ CAMADA DE INFRAESTRUTURA - Data Access"]
        direction LR
        UR["👤 UserRepository<br/>━━━━━━━━━━━━━━━<br/>🔍 findByEmail()<br/>🔍 findByRole()<br/>📊 existsByEmail()<br/>🔄 save() & update()<br/>🗑️ deleteById()"]
        
        SCR["📞 SupportChannelRepository<br/>━━━━━━━━━━━━━━━━━━━━━━<br/>🔍 findByType()<br/>🔍 findByContactInfo()<br/>⏰ findByAvailability()<br/>🔍 searchByKeyword()<br/>📊 findAllActive()"]
        
        EDR["📔 EmotionalDiaryRepository<br/>━━━━━━━━━━━━━━━━━━━━━━<br/>👤 findByUserId()<br/>📅 findByDateRange()<br/>😊 findByMoodLevel()<br/>📊 findMoodTrends()<br/>📈 calculateAverages()"]
        
        PAR["🧠 PsychosocialAssessmentRepository<br/>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<br/>👤 findByUserId()<br/>⚠️ findByRiskLevel()<br/>📊 findByScoreRange()<br/>📅 findByAssessmentDate()<br/>📈 calculateRiskTrends()"]
        
        ALR["📊 AuditLogRepository<br/>━━━━━━━━━━━━━━━━━<br/>👤 findByUserId()<br/>🏷️ findByEventType()<br/>📅 findByDateRange()<br/>⚠️ findBySeverity()<br/>🔍 findSecurityEvents()"]
    end
    
    %% ===== CAMADA DE CONFIGURAÇÃO =====
    subgraph CONFIG ["⚙️ CAMADA DE CONFIGURAÇÃO - Framework Setup"]
        direction LR
        SEC["🔒 SecurityConfig<br/>━━━━━━━━━━━━━━━<br/>🔐 HTTP Basic Auth<br/>🛡️ BCrypt Password Encoder<br/>🚪 Endpoint Security<br/>🔓 Public Endpoints<br/>👮 ADMIN Role Required"]
        
        OPENAPI["📚 OpenApiConfig<br/>━━━━━━━━━━━━━━━<br/>📄 API Documentation<br/>🔐 Security Schemes<br/>📊 Server Information<br/>📋 Contact Details<br/>🏷️ API Versioning"]
        
        MONGO_CONF["🗄️ MongoConfig<br/>━━━━━━━━━━━━━━<br/>🔗 Database Connection<br/>📡 Repository Scanning<br/>⚙️ Connection Settings<br/>🔄 Auto-configuration<br/>🏗️ Index Management"]
        
        APP["🚀 SoftcareApplication<br/>━━━━━━━━━━━━━━━━━━━<br/>▶️ Spring Boot Main Class<br/>🔄 Auto Configuration<br/>📦 Component Scanning<br/>🏗️ Bean Management<br/>🌱 Application Context"]
    end

    %% ===== CONEXÕES PRINCIPAIS =====
    
    %% External to Interface
    CLIENT -.->|"HTTP/REST API"| UC
    CLIENT -.->|"HTTP/REST API"| SCC
    CLIENT -.->|"HTTP/REST API"| EDC
    CLIENT -.->|"HTTP/REST API"| PAC
    CLIENT -.->|"HTTP/REST API"| ALC
    
    SWAGGER -.->|"API Documentation"| INTERFACE
    
    %% Interface to Application
    UC ==>|"Business Logic"| US
    SCC ==>|"Business Logic"| SCS
    EDC ==>|"Business Logic"| EDS
    PAC ==>|"Business Logic"| PAS
    ALC ==>|"Business Logic"| ALS
    
    %% Application to Domain
    US -.->|"Domain Models"| UMODEL
    SCS -.->|"Domain Models"| SCMODEL
    EDS -.->|"Domain Models"| EDMODEL
    PAS -.->|"Domain Models"| PAMODEL
    ALS -.->|"Domain Models"| ALMODEL
    
    US -.->|"DTOs"| USER_DTOS
    SCS -.->|"DTOs"| SUPPORT_DTOS
    EDS -.->|"DTOs"| DIARY_DTOS
    PAS -.->|"DTOs"| ASSESSMENT_DTOS
    ALS -.->|"DTOs"| AUDIT_DTOS
    
    %% Application to Infrastructure
    US ==>|"Data Access"| UR
    SCS ==>|"Data Access"| SCR
    EDS ==>|"Data Access"| EDR
    PAS ==>|"Data Access"| PAR
    ALS ==>|"Data Access"| ALR
    
    %% Infrastructure to External
    UR ==>|"MongoDB Operations"| MONGO
    SCR ==>|"MongoDB Operations"| MONGO
    EDR ==>|"MongoDB Operations"| MONGO
    PAR ==>|"MongoDB Operations"| MONGO
    ALR ==>|"MongoDB Operations"| MONGO
    
    %% Configuration Connections
    SEC -.->|"Security"| INTERFACE
    OPENAPI -.->|"Documentation"| INTERFACE
    MONGO_CONF -.->|"Database Config"| INFRASTRUCTURE
    APP -.->|"Bootstrap"| CONFIG
    
    %% Infrastructure
    DOCKER -.->|"Container"| MONGO
    DOCKER -.->|"Container"| APP

    %% ===== ESTILOS VISUAIS =====
    classDef external fill:#f8f9fa,stroke:#6c757d,stroke-width:3px,color:#212529
    classDef interface fill:#e3f2fd,stroke:#1976d2,stroke-width:3px,color:#0d47a1
    classDef application fill:#f3e5f5,stroke:#7b1fa2,stroke-width:3px,color:#4a148c
    classDef domain fill:#e8f5e8,stroke:#388e3c,stroke-width:3px,color:#1b5e20
    classDef infrastructure fill:#fff3e0,stroke:#f57c00,stroke-width:3px,color:#e65100
    classDef config fill:#fce4ec,stroke:#c2185b,stroke-width:3px,color:#880e4f
    
    class CLIENT,SWAGGER,MONGO,DOCKER external
    class UC,SCC,EDC,PAC,ALC interface
    class US,SCS,EDS,PAS,ALS application
    class UMODEL,SCMODEL,EDMODEL,PAMODEL,ALMODEL,USER_DTOS,SUPPORT_DTOS,DIARY_DTOS,ASSESSMENT_DTOS,AUDIT_DTOS,ENUMS_LIST domain
    class UR,SCR,EDR,PAR,ALR infrastructure
    class SEC,OPENAPI,MONGO_CONF,APP config
```

## 🎯 Responsabilidades por Camada

```mermaid
graph TB
    subgraph "🌐 Presentation Layer"
        subgraph "🎯 Controllers"
            UC["👤 UserController<br/>• CRUD Operations<br/>• Authentication<br/>• Password Management<br/>• User Validation"]
            SCC["📞 SupportChannelController<br/>• Channel Management<br/>• Search & Filters<br/>• Contact Information<br/>• Availability Tracking"]
            EDC["📔 EmotionalDiaryController<br/>• Diary Entry Management<br/>• Mood Tracking<br/>• Wellness Analytics<br/>• Timeline Queries"]
            PAC["🧠 PsychosocialAssessmentController<br/>• Risk Assessment<br/>• Evaluation Scoring<br/>• Recommendations<br/>• Progress Tracking"]
            ALC["📊 AuditLogController<br/>• Security Logging<br/>• Event Tracking<br/>• Access Monitoring<br/>• Compliance Reports"]
        end
    end

    subgraph "🔧 Business Layer"
        subgraph "💼 Services"
            US["👤 UserService<br/>• Business Logic<br/>• Password Validation<br/>• Role Management<br/>• Account Lifecycle"]
            SCS["📞 SupportChannelService<br/>• Channel Logic<br/>• Search Algorithms<br/>• Contact Validation<br/>• Availability Logic"]
            EDS["📔 EmotionalDiaryService<br/>• Mood Analysis<br/>• Wellness Calculation<br/>• Statistical Analysis<br/>• Trend Detection"]
            PAS["🧠 PsychosocialAssessmentService<br/>• Risk Calculation<br/>• Score Aggregation<br/>• Recommendation Engine<br/>• Assessment Logic"]
            ALS["📊 AuditLogService<br/>• Event Processing<br/>• Security Analysis<br/>• Compliance Tracking<br/>• Alert Generation"]
        end
    end

    subgraph "📦 Domain Layer"
        subgraph "🏗️ Core Models"
            UMODEL["👤 User Entity<br/>• Identity Management<br/>• Profile Information<br/>• Security Attributes<br/>• Lifecycle Tracking"]
            SCMODEL["📞 SupportChannel Entity<br/>• Channel Information<br/>• Contact Details<br/>• Service Availability<br/>• Operational Data"]
            EDMODEL["📔 EmotionalDiary Entity<br/>• Mood Records<br/>• Daily Entries<br/>• Emotional State<br/>• Activity Tracking"]
            PAMODEL["🧠 PsychosocialAssessment Entity<br/>• Assessment Data<br/>• Risk Metrics<br/>• Evaluation Results<br/>• Recommendations"]
            ALMODEL["📊 AuditLog Entity<br/>• Event Records<br/>• Security Data<br/>• Compliance Info<br/>• Access Traces"]
        end
    end

    subgraph "🗄️ Data Layer"
        subgraph "📊 Repositories"
            UR["👤 UserRepository<br/>• User Persistence<br/>• Email Queries<br/>• Role Filtering<br/>• Account Lookup"]
            SCR["📞 SupportChannelRepository<br/>• Channel Storage<br/>• Search Operations<br/>• Filter Queries<br/>• Availability Checks"]
            EDR["📔 EmotionalDiaryRepository<br/>• Entry Persistence<br/>• Timeline Queries<br/>• Mood Analytics<br/>• Date Filtering"]
            PAR["🧠 PsychosocialAssessmentRepository<br/>• Assessment Storage<br/>• Risk Queries<br/>• Score Analytics<br/>• History Tracking"]
            ALR["📊 AuditLogRepository<br/>• Log Persistence<br/>• Event Queries<br/>• Security Tracking<br/>• Compliance Reports"]
        end
    end

    subgraph "⚙️ Infrastructure Layer"
        subgraph "🔧 Configuration"
            SEC["🔒 SecurityConfig<br/>• Authentication Setup<br/>• Authorization Rules<br/>• Password Encoding<br/>• Endpoint Protection"]
            MONGO_CONF["🗄️ MongoConfig<br/>• Database Connection<br/>• Repository Configuration<br/>• Index Management<br/>• Query Optimization"]
        end
        
        subgraph "🗄️ Database"
            MONGODB["🍃 MongoDB<br/>• Document Storage<br/>• Index Management<br/>• Query Processing<br/>• Transaction Support"]
        end
    end

    %% Connections
    UC --> US
    SCC --> SCS
    EDC --> EDS
    PAC --> PAS
    ALC --> ALS

    US --> UMODEL
    SCS --> SCMODEL
    EDS --> EDMODEL
    PAS --> PAMODEL
    ALS --> ALMODEL

    US --> UR
    SCS --> SCR
    EDS --> EDR
    PAS --> PAR
    ALS --> ALR

    UR --> MONGODB
    SCR --> MONGODB
    EDR --> MONGODB
    PAR --> MONGODB
    ALR --> MONGODB

    SEC -.-> UC
    SEC -.-> SCC
    SEC -.-> EDC
    SEC -.-> PAC
    SEC -.-> ALC

    MONGO_CONF -.-> UR
    MONGO_CONF -.-> SCR
    MONGO_CONF -.-> EDR
    MONGO_CONF -.-> PAR
    MONGO_CONF -.-> ALR
```

## 🔄 Fluxo de Dados

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant Service
    participant Repository
    participant MongoDB
    participant AuditLog

    Client->>Controller: HTTP Request (authenticated)
    Controller->>Service: Business Logic Call
    Service->>Repository: Data Access Call
    Repository->>MongoDB: Query Execution
    MongoDB-->>Repository: Data Response
    Repository-->>Service: Entity Response
    Service->>AuditLog: Log Operation
    Service-->>Controller: Business Object
    Controller-->>Client: HTTP Response (JSON)
```

## 🛡️ Camadas de Segurança

```mermaid
graph TD
    subgraph "🔒 Security Layers"
        AUTH[🔐 HTTP Basic Authentication]
        ENC[🔑 BCrypt Password Encoding]
        VALID[✅ Jakarta Validation]
        AUDIT[📊 Audit Logging]
    end

    subgraph "🌐 Endpoints"
        PUBLIC[📖 Public Endpoints<br/>/actuator/health<br/>/actuator/info]
        PROTECTED[🔒 Protected Endpoints<br/>/api/v1/**]
    end

    AUTH --> PROTECTED
    ENC --> AUTH
    VALID --> PROTECTED
    AUDIT --> PROTECTED
    
    PUBLIC -.-> |No Auth Required| CLIENT
    PROTECTED --> |Auth Required| CLIENT[👤 Client]
```

## 📊 Estrutura de Dados

```mermaid
erDiagram
    USER {
        string id PK
        string name
        string email UK
        string password
        string phone
        UserRole role
        boolean active
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    SUPPORT_CHANNEL {
        string id PK
        string name
        string description
        string type
        string contactInfo
        string phoneNumber
        string email
        string website
        boolean available
        string operatingHours
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    EMOTIONAL_DIARY {
        string id PK
        string userId FK
        LocalDate date
        MoodLevel moodLevel
        int stressLevel
        string notes
        string activities
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    PSYCHOSOCIAL_ASSESSMENT {
        string id PK
        string userId FK
        int totalScore
        RiskLevel riskLevel
        Map answers
        string recommendations
        LocalDateTime assessmentDate
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    AUDIT_LOG {
        string id PK
        string userId FK
        LocalDateTime timestamp
        string eventType
        string description
        Severity severity
        boolean success
        string resourceId
        string httpMethod
        string requestUri
        string userAgent
        string ipAddress
    }

    USER ||--o{ EMOTIONAL_DIARY : "has many"
    USER ||--o{ PSYCHOSOCIAL_ASSESSMENT : "has many"
    USER ||--o{ AUDIT_LOG : "generates"
```

## 🔌 Tecnologias e Padrões

```mermaid
mindmap
  root((🏗️ SoftCare<br/>Architecture))
    🚀 Framework
      Spring Boot 3.5.6
      Spring Security
      Spring Data MongoDB
      Spring Boot Actuator
      Spring Boot DevTools
    
    🗄️ Database
      MongoDB 7.0
      Spring Data Repositories
      Embedded Test Database
      Mongo Express Interface
    
    🧪 Testing
      JUnit 5
      Mockito
      MockMvc
      Spring Security Test
      93 Integration Tests
    
    🔒 Security
      HTTP Basic Authentication
      BCrypt Password Encoding
      CSRF Protection
      Role-based Authorization
      Audit Logging
    
    📊 Monitoring
      Spring Boot Actuator
      Health Checks
      Metrics Collection
      Application Info
      Debug Logging
    
    🔧 Development
      Maven Build Tool
      Docker Containerization
      VS Code Configuration
      Live Reload
      Hot Swap Debugging
```

## 📁 Estrutura de Pacotes

```
br.com.fiap.softcare/
├── 🚀 SoftcareApplication.java          # Main Application Class
├── 🎯 controller/                       # REST Controllers Layer
│   ├── UserController.java
│   ├── SupportChannelController.java
│   ├── EmotionalDiaryController.java
│   ├── PsychosocialAssessmentController.java
│   └── AuditLogController.java
├── 🔧 service/                          # Business Logic Layer
│   ├── UserService.java
│   ├── SupportChannelService.java
│   ├── EmotionalDiaryService.java
│   ├── PsychosocialAssessmentService.java
│   └── AuditLogService.java
├── 📦 model/                            # Domain Entities
│   ├── User.java
│   ├── SupportChannel.java
│   ├── EmotionalDiary.java
│   ├── PsychosocialAssessment.java
│   └── AuditLog.java
├── 🗄️ repository/                       # Data Access Layer
│   ├── UserRepository.java
│   ├── SupportChannelRepository.java
│   ├── EmotionalDiaryRepository.java
│   ├── PsychosocialAssessmentRepository.java
│   └── AuditLogRepository.java
├── 📋 dto/                              # Data Transfer Objects
│   ├── CreateUserRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java
│   ├── CreateEmotionalDiaryRequest.java
│   └── CreatePsychosocialAssessmentRequest.java
├── 🏷️ enums/                            # Domain Enumerations
│   ├── UserRole.java
│   ├── MoodLevel.java
│   └── RiskLevel.java
└── ⚙️ config/                           # Configuration Classes
    ├── SecurityConfig.java
    └── MongoConfig.java
```

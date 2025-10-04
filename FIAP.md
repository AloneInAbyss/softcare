# FIAP - CHALLENGE SOFTTEK - SPRINT 2

## PROJETO SOFTCARE

Integrantes:

- Thiago Henrique Assi - RM 555570

## DIAGRAMAS DE ARQUITETURA DO BACKEND

> **SoftCare** é uma solução completa para oferecer suporte psicossocial e promover o bem-estar emocional dos colaboradores, desenvolvida com Spring Boot 3.5.6 e MongoDB.

### Estrutura de Dados

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


### Arquitetura Hexagonal

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




### Responsabilidades por Camada

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

### Fluxo de Dados

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

### Camadas de Segurança

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

## Endpoints

### Available authorizations
#### HTTPBasicAuthentication (HTTP, basic)
HTTP Basic Authentication. Use: admin / admin123  

---
## 👤 Usuários
API para gerenciamento de usuários do sistema SoftCare

### [GET] /api/v1/users/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [UserResponse](#userresponse)<br> |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

### [PUT] /api/v1/users/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [CreateUserRequest](#createuserrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [UserResponse](#userresponse)<br> |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

### [DELETE] /api/v1/users/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

### [GET] /api/v1/users
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [UserResponse](#userresponse) ]<br> |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

### [POST] /api/v1/users
**Criar novo usuário**

Cria um novo usuário no sistema SoftCare. Este endpoint não requer autenticação.

#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [CreateUserRequest](#createuserrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 201 | Usuário criado com sucesso | ***/***: [UserResponse](#userresponse)<br> |
| 400 | Dados inválidos fornecidos |  |
| 409 | Email já está em uso |  |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

### [POST] /api/v1/users/{id}/change-password
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [ChangePasswordRequest](#changepasswordrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: string<br> |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

### [POST] /api/v1/users/login
#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [LoginRequest](#loginrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: string<br> |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

### [GET] /api/v1/users/email/{email}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| email | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [UserResponse](#userresponse)<br> |

##### Security

| Security Schema | Scopes |
| --------------- | ------ |
| HTTPBasicAuthentication |  |

---
## default

### [GET] /api/v1/support-channels/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [SupportChannel](#supportchannel)<br> |

### [PUT] /api/v1/support-channels/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [SupportChannel](#supportchannel)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [SupportChannel](#supportchannel)<br> |

### [DELETE] /api/v1/support-channels/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |

### [GET] /api/v1/support-channels
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [SupportChannel](#supportchannel) ]<br> |

### [POST] /api/v1/support-channels
#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [SupportChannel](#supportchannel)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [SupportChannel](#supportchannel)<br> |

### [POST] /api/v1/support-channels/{id}/access
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |
| userId | query |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: string<br> |

### [GET] /api/v1/support-channels/with-website
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [SupportChannel](#supportchannel) ]<br> |

### [GET] /api/v1/support-channels/with-phone
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [SupportChannel](#supportchannel) ]<br> |

### [GET] /api/v1/support-channels/with-email
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [SupportChannel](#supportchannel) ]<br> |

### [GET] /api/v1/support-channels/search/name
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| name | query |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [SupportChannel](#supportchannel) ]<br> |

### [GET] /api/v1/support-channels/search/description
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| text | query |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [SupportChannel](#supportchannel) ]<br> |

---
## default

### [GET] /api/v1/psychosocial-assessments/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [PsychosocialAssessment](#psychosocialassessment)<br> |

### [PUT] /api/v1/psychosocial-assessments/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [CreatePsychosocialAssessmentRequest](#createpsychosocialassessmentrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [PsychosocialAssessment](#psychosocialassessment)<br> |

### [DELETE] /api/v1/psychosocial-assessments/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |

### [POST] /api/v1/psychosocial-assessments
#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [CreatePsychosocialAssessmentRequest](#createpsychosocialassessmentrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [PsychosocialAssessment](#psychosocialassessment)<br> |

### [GET] /api/v1/psychosocial-assessments/user/{userId}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [PsychosocialAssessment](#psychosocialassessment) ]<br> |

### [GET] /api/v1/psychosocial-assessments/user/{userId}/latest
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [PsychosocialAssessment](#psychosocialassessment)<br> |

### [GET] /api/v1/psychosocial-assessments/risk-level/{riskLevel}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| riskLevel | path |  | Yes | string, <br>**Available values:** "LOW", "MODERATE", "HIGH", "CRITICAL" |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [PsychosocialAssessment](#psychosocialassessment) ]<br> |

### [GET] /api/v1/psychosocial-assessments/high-risk
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [PsychosocialAssessment](#psychosocialassessment) ]<br> |

### [GET] /api/v1/psychosocial-assessments/date-range
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| startDate | query |  | Yes | dateTime |
| endDate | query |  | Yes | dateTime |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [PsychosocialAssessment](#psychosocialassessment) ]<br> |

---
## default

### [GET] /api/v1/emotional-diary/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [EmotionalDiary](#emotionaldiary)<br> |

### [PUT] /api/v1/emotional-diary/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [CreateEmotionalDiaryRequest](#createemotionaldiaryrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [EmotionalDiary](#emotionaldiary)<br> |

### [DELETE] /api/v1/emotional-diary/{id}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| id | path |  | Yes | string |

#### Responses

| Code | Description |
| ---- | ----------- |
| 200 | OK |

### [POST] /api/v1/emotional-diary
#### Request Body

| Required | Schema |
| -------- | ------ |
|  Yes | **application/json**: [CreateEmotionalDiaryRequest](#createemotionaldiaryrequest)<br> |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [EmotionalDiary](#emotionaldiary)<br> |

### [GET] /api/v1/emotional-diary/user/{userId}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [EmotionalDiary](#emotionaldiary) ]<br> |

### [GET] /api/v1/emotional-diary/user/{userId}/today
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: boolean<br> |

### [GET] /api/v1/emotional-diary/user/{userId}/range
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |
| startDate | query |  | Yes | date |
| endDate | query |  | Yes | date |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [EmotionalDiary](#emotionaldiary) ]<br> |

### [GET] /api/v1/emotional-diary/user/{userId}/latest
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [EmotionalDiary](#emotionaldiary)<br> |

### [GET] /api/v1/emotional-diary/user/{userId}/date/{date}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |
| date | path |  | Yes | date |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [EmotionalDiary](#emotionaldiary)<br> |

### [GET] /api/v1/emotional-diary/user/{userId}/average-wellness
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |
| startDate | query |  | Yes | date |
| endDate | query |  | Yes | date |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: double<br> |

### [GET] /api/v1/emotional-diary/low-mood
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [EmotionalDiary](#emotionaldiary) ]<br> |

### [GET] /api/v1/emotional-diary/high-stress
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [EmotionalDiary](#emotionaldiary) ]<br> |

---
## default

### [GET] /api/v1/audit-logs/user/{userId}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [AuditLog](#auditlog) ]<br> |

### [GET] /api/v1/audit-logs/user/{userId}/recent
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| userId | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [AuditLog](#auditlog) ]<br> |

### [GET] /api/v1/audit-logs/failed
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [AuditLog](#auditlog) ]<br> |

### [GET] /api/v1/audit-logs/event-type/{eventType}
#### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ------ |
| eventType | path |  | Yes | string |

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [AuditLog](#auditlog) ]<br> |

### [GET] /api/v1/audit-logs/critical
#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | ***/***: [ [AuditLog](#auditlog) ]<br> |

---
## Actuator
Monitor and interact
[Spring Boot Actuator Web API Documentation](https://docs.spring.io/spring-boot/docs/current/actuator-api/html/)

### [GET] /actuator
**Actuator root web endpoint**

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | **application/vnd.spring-boot.actuator.v3+json**: object<br>**application/json**: object<br>**application/vnd.spring-boot.actuator.v2+json**: object<br> |

### [GET] /actuator/health
**Actuator web endpoint 'health'**

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | **application/vnd.spring-boot.actuator.v3+json**: object<br>**application/json**: object<br>**application/vnd.spring-boot.actuator.v2+json**: object<br> |

### [GET] /actuator/health/**
**Actuator web endpoint 'health-path'**

#### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | **application/vnd.spring-boot.actuator.v3+json**: object<br>**application/json**: object<br>**application/vnd.spring-boot.actuator.v2+json**: object<br> |

---
### Schemas

#### CreateUserRequest

Dados do usuário a ser criado

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| name | string |  | Yes |
| email | string |  | Yes |
| password | string |  | Yes |
| role | string, <br>**Available values:** "EMPLOYEE", "MANAGER", "SYSTEM_ADMIN" | *Enum:* `"EMPLOYEE"`, `"MANAGER"`, `"SYSTEM_ADMIN"` | Yes |

#### UserResponse

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | string |  | No |
| name | string |  | No |
| email | string |  | No |
| role | string, <br>**Available values:** "EMPLOYEE", "MANAGER", "SYSTEM_ADMIN" | *Enum:* `"EMPLOYEE"`, `"MANAGER"`, `"SYSTEM_ADMIN"` | No |
| createdAt | dateTime |  | No |
| updatedAt | dateTime |  | No |

#### SupportChannel

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | string |  | No |
| name | string |  | Yes |
| description | string |  | Yes |
| phoneNumber | string |  | No |
| email | string |  | No |
| website | string |  | No |
| createdAt | dateTime |  | No |
| updatedAt | dateTime |  | No |

#### CreatePsychosocialAssessmentRequest

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| userId | string |  | Yes |
| workStressLevel | integer |  | No |
| workLifeBalance | integer |  | No |
| jobSatisfaction | integer |  | No |
| relationshipWithColleagues | integer |  | No |
| personalWellbeing | integer |  | No |

#### PsychosocialAssessment

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | string |  | No |
| userId | string |  | Yes |
| workStressLevel | integer |  | No |
| workLifeBalance | integer |  | No |
| jobSatisfaction | integer |  | No |
| relationshipWithColleagues | integer |  | No |
| personalWellbeing | integer |  | No |
| overallScore | double |  | No |
| riskLevel | string, <br>**Available values:** "LOW", "MODERATE", "HIGH", "CRITICAL" | *Enum:* `"LOW"`, `"MODERATE"`, `"HIGH"`, `"CRITICAL"` | No |
| isComplete | boolean |  | No |
| createdAt | dateTime |  | No |
| updatedAt | dateTime |  | No |

#### CreateEmotionalDiaryRequest

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| userId | string |  | Yes |
| entryDate | date |  | Yes |
| moodLevel | string, <br>**Available values:** "VERY_LOW", "LOW", "NEUTRAL", "GOOD", "VERY_GOOD" | *Enum:* `"VERY_LOW"`, `"LOW"`, `"NEUTRAL"`, `"GOOD"`, `"VERY_GOOD"` | Yes |
| energyLevel | integer |  | No |
| stressLevel | integer |  | No |
| sleepQuality | integer |  | No |

#### EmotionalDiary

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | string |  | No |
| userId | string |  | Yes |
| entryDate | date |  | Yes |
| moodLevel | string, <br>**Available values:** "VERY_LOW", "LOW", "NEUTRAL", "GOOD", "VERY_GOOD" | *Enum:* `"VERY_LOW"`, `"LOW"`, `"NEUTRAL"`, `"GOOD"`, `"VERY_GOOD"` | Yes |
| energyLevel | integer |  | No |
| stressLevel | integer |  | No |
| sleepQuality | integer |  | No |
| createdAt | dateTime |  | No |
| updatedAt | dateTime |  | No |

#### ChangePasswordRequest

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| oldPassword | string |  | No |
| newPassword | string |  | No |

#### LoginRequest

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| email | string |  | Yes |
| password | string |  | Yes |

#### AuditLog

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | string |  | No |
| eventType | string |  | Yes |
| description | string |  | Yes |
| userId | string |  | No |
| userEmail | string |  | No |
| httpMethod | string |  | No |
| requestUrl | string |  | No |
| ipAddress | string |  | No |
| severity | string |  | No |
| resourceId | string |  | No |
| success | boolean |  | No |
| errorMessage | string |  | No |
| timestamp | dateTime |  | Yes |

#### Link

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| href | string |  | No |
| templated | boolean |  | No |

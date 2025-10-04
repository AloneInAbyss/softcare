# 🌟 SoftCare - Plataforma de Bem-Estar Corporativo

> **SoftCare** é uma solução completa para oferecer suporte psicossocial e promover o bem-estar emocional dos colaboradores, desenvolvida com Spring Boot 3.5.6 e MongoDB.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-93%20Passing-success.svg)](#testes)

## 📋 Índice

- [🚀 Início Rápido](#-início-rápido)
- [🏗️ Arquitetura](#️-arquitetura)
- [🛠️ Tecnologias](#️-tecnologias)
- [📊 Funcionalidades](#-funcionalidades)
- [🔧 Configuração](#-configuração)
- [🧪 Testes](#-testes)
- [📚 API Documentation](#-api-documentation)
- [🐳 Docker](#-docker)
- [🔒 Segurança](#-segurança)
- [📈 Monitoramento](#-monitoramento)
- [🤝 Contribuição](#-contribuição)

## 🚀 Início Rápido

### Pré-requisitos
- ☕ **Java 17+**
- 📦 **Maven 3.9+**
- 🐳 **Docker + Docker Compose**

### 1️⃣ Iniciar MongoDB
```bash
docker compose up -d
```

### 2️⃣ Executar Aplicação
```bash
./mvnw spring-boot:run
```

### 4️⃣ Acessar Aplicação
- **API Base:** `http://localhost:8080/api/v1`
- **Health Check:** `http://localhost:8080/api/v1/actuator/health`
- **Mongo Express:** `http://localhost:8081`

## 🏗️ Arquitetura

```
📁 softcare/
├── 🌐 src/main/java/br/com/fiap/softcare/
│   ├── 🎯 controller/          # REST Controllers
│   ├── 📦 model/               # Entidades de Domínio
│   ├── 🗄️ repository/          # Repositórios MongoDB
│   ├── 🔧 service/             # Lógica de Negócio
│   ├── 🔒 config/              # Configurações
│   └── 🚀 SoftcareApplication.java
├── 🧪 src/test/                # Testes Unitários/Integração
├── 📝 src/main/resources/      # Configurações
├── 🐳 docker-compose.yml       # Infraestrutura
└── 📋 README.md
```

### 🎯 Controllers Implementados

| Controller | Endpoints | Funcionalidade |
|------------|-----------|----------------|
| 👤 **UserController** | `/users` | Gestão de usuários |
| 📞 **SupportChannelController** | `/support-channels` | Canais de apoio |
| 📔 **EmotionalDiaryController** | `/emotional-diaries` | Diário emocional |
| 🧠 **PsychosocialAssessmentController** | `/psychosocial-assessments` | Avaliações psicológicas |
| 📊 **AuditLogController** | `/audit-logs` | Logs de auditoria |

## 🛠️ Tecnologias

### Backend
- **🚀 Spring Boot 3.5.6** - Framework principal
- **🔒 Spring Security** - Autenticação e autorização
- **🗄️ Spring Data MongoDB** - Persistência de dados
- **✅ Spring Boot Validation** - Validação de dados
- **📊 Spring Boot Actuator** - Monitoramento
- **🔄 Spring Boot DevTools** - Desenvolvimento

### Banco de Dados
- **🍃 MongoDB 7.0** - Banco NoSQL principal
- **🌐 Mongo Express** - Interface web de administração

### Testes
- **🧪 JUnit 5** - Framework de testes
- **🎭 Mockito** - Mocking
- **🌐 MockMvc** - Testes de integração web
- **🔒 Spring Security Test** - Testes de segurança

### Desenvolvimento
- **📦 Maven** - Gerenciamento de dependências
- **🐳 Docker** - Containerização
- **🔧 VS Code** - IDE configurada
- **📝 Lombok** - Redução de boilerplate

## 📊 Funcionalidades

### 👤 Gestão de Usuários
- CRUD completo de usuários
- Validação de dados (email, telefone, etc.)
- Criptografia de senhas com BCrypt
- Autenticação HTTP Basic

### 📞 Canais de Suporte
- Cadastro de canais de apoio
- Filtros por tipo de contato
- Disponibilidade e horários
- Informações de contato

### 📔 Diário Emocional
- Registro de humor diário
- Análise de bem-estar
- Métricas e estatísticas
- Histórico emocional

### 🧠 Avaliações Psicossociais
- Questionários padronizados
- Cálculo de níveis de risco
- Análise de severidade
- Relatórios detalhados

### 📊 Sistema de Auditoria
- Log de todas as operações
- Rastreamento de usuários
- Níveis de severidade
- Consultas e filtros

## 📚 Documentação da API

### 🔐 Autenticação
Todas as APIs requerem autenticação HTTP Basic:
```
Username: admin
Password: admin123
```

### 👤 Users API

```http
GET    /api/v1/users           # Listar usuários
GET    /api/v1/users/{id}      # Buscar por ID
POST   /api/v1/users           # Criar usuário
PUT    /api/v1/users/{id}      # Atualizar usuário
DELETE /api/v1/users/{id}      # Remover usuário
```

### 📞 Support Channels API

```http
GET    /api/v1/support-channels                    # Listar canais
GET    /api/v1/support-channels/{id}               # Buscar por ID
POST   /api/v1/support-channels                    # Criar canal
PUT    /api/v1/support-channels/{id}               # Atualizar canal
DELETE /api/v1/support-channels/{id}               # Remover canal
GET    /api/v1/support-channels/contact/{type}     # Filtrar por tipo
```

### 📔 Emotional Diary API

```http
GET    /api/v1/emotional-diaries                   # Listar entradas
GET    /api/v1/emotional-diaries/{id}              # Buscar por ID
POST   /api/v1/emotional-diaries                   # Criar entrada
PUT    /api/v1/emotional-diaries/{id}              # Atualizar entrada
DELETE /api/v1/emotional-diaries/{id}              # Remover entrada
GET    /api/v1/emotional-diaries/user/{userId}     # Entradas do usuário
GET    /api/v1/emotional-diaries/user/{userId}/wellness-score  # Score de bem-estar
```

### 🧠 Psychosocial Assessment API

```http
GET    /api/v1/psychosocial-assessments            # Listar avaliações
GET    /api/v1/psychosocial-assessments/{id}       # Buscar por ID
POST   /api/v1/psychosocial-assessments            # Criar avaliação
PUT    /api/v1/psychosocial-assessments/{id}       # Atualizar avaliação
DELETE /api/v1/psychosocial-assessments/{id}       # Remover avaliação
GET    /api/v1/psychosocial-assessments/user/{userId}           # Por usuário
GET    /api/v1/psychosocial-assessments/user/{userId}/risk      # Nível de risco
```

### 📊 Audit Logs API (Read-Only)

```http
GET    /api/v1/audit-logs/user/{userId}            # Logs do usuário
GET    /api/v1/audit-logs/user/{userId}/recent     # Logs recentes
GET    /api/v1/audit-logs/events/{eventType}       # Por tipo de evento
GET    /api/v1/audit-logs/critical                 # Logs críticos
GET    /api/v1/audit-logs/failed-operations        # Operações falhadas
```

## 🐳 Docker

### Serviços Configurados

```yaml
# docker-compose.yml
services:
  mongodb:      # Banco principal
  mongo-express: # Interface web
  # Volumes persistentes configurados
```

### Comandos Úteis

```bash
# Iniciar infraestrutura
docker compose up -d

# Ver logs
docker compose logs mongodb

# Parar serviços
docker compose down

# Reset completo (APAGA DADOS)
docker compose down -v

# Acessar MongoDB
docker exec -it softcare-mongodb mongosh -u admin -p admin123
```

### Acessos

- **MongoDB:** `localhost:27017`
- **Mongo Express:** `http://localhost:8081`
- **Credenciais:** `admin / admin123`

## 🔒 Segurança

### Configurações Implementadas

- **🔐 HTTP Basic Authentication** - Spring Security
- **🔑 BCrypt Password Encoding** - Senhas criptografadas
- **🛡️ CSRF Protection** - Proteção contra ataques
- **🔒 Secure Headers** - Headers de segurança
- **👤 User Roles** - Sistema de permissões

### Endpoints Públicos
- `/actuator/health` - Health check
- `/actuator/info` - Informações da aplicação

### Endpoints Protegidos
- Todos os endpoints `/api/v1/**` requerem autenticação

## 📈 Monitoramento

### Spring Boot Actuator

Endpoints de monitoramento disponíveis:

```http
GET /actuator/health          # Status da aplicação
GET /actuator/info            # Informações do app
GET /actuator/metrics         # Métricas detalhadas
GET /actuator/env             # Variáveis de ambiente
GET /actuator/beans           # Beans do Spring
```

### Logs Estruturados

```bash
# Logs por nível
logging.level.br.com.fiap.softcare=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
logging.level.org.springframework.security=DEBUG
```

---
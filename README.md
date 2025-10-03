# SoftCare

## Pré-requisitos
- Docker + Docker Compose instalados
- Java 17
- Maven

## Configuração do MongoDB

### 1. Iniciar o MongoDB com Docker
```bash
# Iniciar os serviços
docker compose up -d

# Verificar se os containers estão rodando
docker compose ps
```

### 2. Acessar o MongoDB
- **MongoDB**: `localhost:27017`
- **Mongo Express** (Interface Web): `http://localhost:8081`

### 3. Credenciais
- **Admin Username**: admin
- **Admin Password**: admin123
- **Database**: softcare

### 4. Testar a aplicação
```bash
# Executar a aplicação Spring Boot
./mvnw spring-boot:run

# A aplicação estará disponível em:
# http://localhost:8080/api/v1
```

## Estrutura do Banco

### Collections Criadas Automaticamente:
- `users` - Dados dos usuários
- `psychosocial_assessments` - Avaliações psicossociais
- `emotional_diaries` - Diários emocionais
- `support_channels` - Canais de apoio
- `audit_logs` - Logs de auditoria

### Índices Criados:
- Email único para usuários
- Índices de performance para consultas frequentes
- Índices para logs de auditoria

## Comandos Úteis

```bash
# Parar os serviços
docker compose down

# Parar e remover volumes (APAGA TODOS OS DADOS)
docker compose down -v

# Ver logs do MongoDB
docker compose logs mongodb

# Acessar o MongoDB via linha de comando
docker exec -it softcare-mongodb mongosh -u admin -p admin123
```
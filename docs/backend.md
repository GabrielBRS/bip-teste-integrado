# 🏗️ Documentação Backend

---

## 📘 1. Visão Geral

Este repositório contém a solução para o **Desafio Fullstack Integrado**, com foco na **implementação e correção das camadas de backend**, incluindo:

- **Backend (Spring Boot)**
- **EJB (Jakarta EE)**
- **Banco de Dados (H2/SQL)**

### Escopo do Projeto

- ✅ Correção do bug de concorrência e lógica de negócio no `BeneficioEjbService`
- ✅ Implementação de uma **API RESTful** (`backend-module`) para o CRUD de Benefícios
- ✅ Integração entre o **backend Spring Boot** e o **módulo EJB** via interface remota
- ✅ Testes unitários e de integração para validação das regras de negócio e endpoints
- ✅ Configuração de **banco H2 em memória** com *seeding* automático

---

## 🧭 2. Estrutura de Diretórios

```bash
.
├── .github/workflows/   # Pipeline de CI (Maven Build)
├── backend-module/      # Aplicação Backend (Spring Boot)
├── db/                  # Scripts de schema (schema.sql) e seed (data.sql)
├── docs/                # Documentação original do desafio
├── ejb-module/          # Módulo EJB (Jakarta EE) com serviço corrigido
├── ear-module/          # (Opcional) Módulo para empacotamento EAR
└── frontend/            # (Fora do escopo) Aplicação Angular
```

---

## 🚀 3. Guia de Execução Rápida (Modo Local)

Esta seção descreve como executar a aplicação completa (**Backend + Lógica EJB**) localmente, usando o **banco H2 em memória** — **sem necessidade de servidor Jakarta EE**.

### 🧩 Pré-requisitos

- ☕ **JDK 17+**
- 🧱 **Maven 3.9+**

### ⚙️ Instruções de Build e Execução

Execute os comandos abaixo a partir da raiz do projeto:

#### 1️⃣ Compile todos os módulos
```bash
mvn clean package
```

#### 2️⃣ Instale o artefato do EJB localmente
(necessário para que o `backend-module` o reconheça como dependência)
```bash
mvn -f ejb-module/pom.xml -DskipTests install
```

#### 3️⃣ Inicie a aplicação Spring Boot
```bash
mvn -f backend-module spring-boot:run
```

### 🔍 Verificação

Após a inicialização:

- Aplicação: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

💡 O backend utiliza o **H2 em memória**, executando automaticamente os scripts:

- `db/schema.sql`
- `db/data.sql`

Esses scripts populam o banco com dados de teste na inicialização.

---

## 🏛️ 4. Arquitetura de Integração: Backend ↔️ EJB

O `backend-module` foi projetado para **desacoplar a lógica de negócio do EJB**, utilizando a interface `BeneficioTransferPort`.  
Isso permite dois modos de operação:

### ⚙️ Modo 1: Local (Padrão)

- **Implementação:** `LocalBeneficioTransferService`
- **Descrição:** Usa uma implementação local (`@Service` do Spring) que replica a lógica corrigida (com *locking otimista* e *rollback transacional*).
- **Vantagem:** Ideal para desenvolvimento ágil e testes automatizados, eliminando a dependência de um servidor Jakarta EE.

---

### 🌐 Modo 2: Remoto (Com Servidor Jakarta EE)

Para ativar o modo remoto, configure o `application.yml` do `backend-module`:

```yaml
ejb:
  beneficio:
    enabled: true  # Ativa o modo "Remoto"
    jndi-name: java:global/bip/BeneficioEjbService

jndi:
  java.naming.factory.initial: org.wildfly.naming.client.WildFlyInitialContextFactory
  java.naming.provider.url: http-remoting://localhost:8080
  java.naming.security.principal: usuario
  java.naming.security.credentials: senha
```

A interface `BeneficioTransferRemote` é **compartilhada** entre os módulos, garantindo contrato único entre as camadas.

### 🧱 Deploy do EJB (Modo Remoto)

1. Gere o artefato JAR:
   ```bash
   mvn -pl ejb-module clean package
   ```

2. Faça o deploy no servidor **Jakarta EE** (ex: WildFly, Payara).

3. Verifique o nome **JNDI Global** atribuído (ex: `java:global/bip/BeneficioEjbService`) e ajuste no `application.yml` do backend.

---

## 🧪 5. Testes Automatizados

A suíte de testes valida as regras de negócio e fluxos REST principais.

### ▶️ Execução
```bash
mvn test
```

### 📋 Principais Testes

- **`BeneficioServiceTest`**  
  Valida transferências (sucesso, falha por saldo insuficiente, *locking*).

- **`BeneficioControllerTest`**  
  Testa os endpoints REST (CRUD + Transferência) com `MockMvc`, garantindo:
    - Códigos HTTP corretos
    - Mensagens de validação
    - Retornos JSON padronizados

---

## 📡 6. Documentação da API (Endpoints)

A documentação interativa está disponível em:  
🔗 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 🔠 Endpoints Principais

| Método | Endpoint | Descrição |
|--------|-----------|-----------|
| GET | `/api/v1/beneficios` | Lista todos os benefícios |
| GET | `/api/v1/beneficios/{id}` | Retorna um benefício específico |
| POST | `/api/v1/beneficios` | Cria um novo benefício |
| PUT | `/api/v1/beneficios/{id}` | Atualiza um benefício existente |
| DELETE | `/api/v1/beneficios/{id}` | Remove um benefício |
| POST | `/api/v1/beneficios/transfer` | Realiza transferência de valores |

#### 📦 Exemplo de Body (Transferência)
```json
{
  "fromId": 1,
  "toId": 2,
  "amount": 100.00
}
```

#### ⚠️ Tratamento de Erros
- `400 Bad Request` → Dados inválidos
- `404 Not Found` → ID não encontrado
- `422 Unprocessable Entity` → Falha de validação (ex: saldo insuficiente)

---

## 📚 7. Observações Finais

- O projeto foi desenvolvido visando **modularidade, testabilidade e compatibilidade** entre ambientes Spring Boot e Jakarta EE.
- O uso do H2 em memória permite **execução rápida** e **CI simplificado**.
- A arquitetura segue princípios de **Clean Architecture** e **SOLID** para facilitar manutenção e extensão futura.
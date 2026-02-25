# GED Document Manager Core

Sistema de Gestão Eletrônica de Documentos (GED) desenvolvido como parte de um desafio técnico para **Desenvolvedor Java Backend Pleno**. A solução contempla o ciclo de vida completo de documentos, incluindo **storage em nuvem (S3), versionamento, auditoria e controle de acesso**.

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Infraestrutura com Docker](#-infraestrutura-com-docker)
- [Configuração e Execução](#️-configuração-e-execução)
- [Estratégia de Testes](#-estratégia-de-testes)
- [Documentação da API](#-documentação-da-api-swagger)
- [Decisões Técnicas](#-decisões-técnicas)

---

## 🚀 Sobre o Projeto

O sistema permite a gestão eficiente de metadados e arquivos (**PDF/PNG/JPG**), garantindo a integridade dos dados através de **versionamento incremental** e **trilhas de auditoria detalhadas** para cada operação.

### Funcionalidades Principais

- **Autenticação e Autorização via JWT**: Diferenciação de permissões entre os perfis `ADMIN`, `USER` e `VIEWER`.
- **Object Storage (S3/MinIO)**: Integração com armazenamento de objetos para escalabilidade de arquivos.
- **Versionamento Automático**: Controle de revisões de arquivos anexados aos documentos.
- **Checksum SHA-256**: Validação de integridade para garantir que o arquivo baixado é idêntico ao enviado originalmente.
- **Auditoria de Eventos**: Log detalhado de alterações críticas e acessos armazenados de forma estruturada.

---

## 🛠 Tecnologias

- **Linguagem:** Java 21 (LTS)
- **Framework:** Spring Boot 4.0.3
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de Dados:** PostgreSQL 15
- **Object Storage:** AWS SDK v2 (S3 Client & Presigner)
- **Migrações:** Flyway
- **Segurança:** Spring Security + OAuth2 Resource Server (JWT)
- **CI/CD:** GitHub Actions com integração Docker (Pipeline Automática)

---

## 🐳 Infraestrutura com Docker

O projeto utiliza **Docker Compose** para orquestrar os serviços necessários tanto para desenvolvimento quanto para a execução dos testes de integração.

### 1. Pré-requisitos
- Docker & Docker Compose instalados e configurados.

### 2. Subindo os Serviços
Na raiz do projeto, execute o comando abaixo para iniciar o **PostgreSQL** e o **MinIO**:
```bash
docker compose up -d
```

### 3. Detalhes de Conectividade e Credenciais

#### 🗄️ Banco de Dados (PostgreSQL)
| Ambiente | Porta Host | Database | Usuário | Senha |
| :--- | :--- | :--- | :--- | :--- |
| **Desenvolvimento** | `5432` | `ged_db` | `administrator` | `admin123` |
| **Testes** | `5433` | `ged_db_test` | `administratorTest` | `admin123Test` |

#### 📦 Object Storage (MinIO/S3)
| Serviço | URL/Porta                     | Usuário (Access Key) | Senha (Secret Key) |
| :--- |:------------------------------| :--- | :--- |
| **API/S3 Endpoint** | `http://localhost:9000`       | `minioadmin` | `minioadmin` |
| **Console UI** | `http://localhost:9001/login` | `minioadmin` | `minioadmin` |

> **Nota:** O container de inicialização `mc` cria automaticamente o bucket `ged-documents` ao subir o compose.

---

## ⚙️ Configuração e Execução

### 🔐 Acesso à API (Carga Inicial)
A aplicação utiliza **Spring Security com JWT**. Para realizar as requisições após o boot, utilize as credenciais padrão criadas via migrações:

| Perfil | Usuário | Senha |
| :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` |
| **USER** | `dhenSouza` | `user123` |

*(Nota: Estes dados são apenas para fins de teste local e desafio técnico)*
### Executando a Aplicação
Para rodar em ambiente de desenvolvimento (perfil default):
```bash
./mvnw spring-boot:run
```
## 🧪 Estratégia de Testes

O projeto adota uma abordagem de **Testes de Integração Reais** para validar a camada de persistência e a lógica de negócio com mocks controlados de infraestrutura Cloud.

Para rodar a suíte completa de testes:
```bash
./mvnw test -Ptest
```
**Diferenciais técnicos aplicados nos testes:**

* **Profile `test` Dedicado:** Uso de `application-test.properties` para total isolamento dos dados de desenvolvimento.
* **MockitoBean:** Utilizado para simular o comportamento do `S3Client` e `S3Presigner`, garantindo que os testes não dependam de conexão externa ou credenciais reais de nuvem.
* **Banco em Pipeline:** A pipeline CI no GitHub Actions provisiona automaticamente um Postgres via `services` na porta **5433** para execução dos testes.

---

## 📖 Documentação da API (Swagger)

A API utiliza **SpringDoc OpenAPI 3** para gerar documentação interativa e padronizada. Como o projeto utiliza tecnologias de ponta (**Spring Boot 4.0.3**), a documentação está configurada para refletir automaticamente todos os endpoints, esquemas de dados e requisitos de segurança.

### Acessando a Interface
Com a aplicação em execução, acesse os seguintes endereços no seu navegador:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Spec (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Autenticação no Swagger
Para testar os endpoints protegidos diretamente pela interface:
1. Realize o login no endpoint de autenticação.
2. Copie o **Token JWT** gerado.
3. No Swagger UI, clique no botão **"Authorize"** (ícone de cadeado).
4. Insira o token e aplique para liberar as requisições com os perfis `ADMIN`, `USER` ou `VIEWER`.

## 🧠 Decisões Técnicas

* **Java 21:** Escolha estratégica pela estabilidade (LTS) e uso de **Records** para garantir imutabilidade nos DTOs e maior clareza no código.
* **Abstração de Storage:** O uso do AWS SDK v2 permite que a aplicação seja facilmente portada de um MinIO local para uma instância real da AWS S3 com apenas alterações de configuração.
* **Flyway:** Adotado para controle rigoroso de versionamento de esquema de banco de dados, facilitando migrações consistentes entre ambientes.
* **UUID v4:** Todas as entidades utilizam UUIDs como chaves primárias, evitando a exposição de IDs sequenciais e aumentando a segurança contra ataques de enumeração.
* **Sem Lombok:** Opção por código Java explícito para demonstrar domínio de POJOs, encapsulamento e estrutura fundamental da linguagem.

---

**Desenvolvido por DhenSouza**
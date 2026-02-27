# GED Document Manager Core

Sistema de Gestão Eletrônica de Documentos (GED) desenvolvido como parte de um desafio técnico para **Desenvolvedor Java Backend Pleno**. A solução contempla o ciclo de vida completo de documentos, incluindo **storage em nuvem (S3), versionamento, auditoria e controle de acesso**.

## 📋 Sumário

- [📖 Sobre o Projeto](#-sobre-o-projeto)
    - [Funcionalidades Principais](#funcionalidades-principais)
- [🛠 Tecnologias](#-tecnologias)
- [🐳 Infraestrutura com Docker](#-infraestrutura-com-docker)
    - [Pré-requisitos](#1-pré-requisitos)
    - [Subindo os Serviços](#2-subindo-os-serviços)
    - [Detalhes de Conectividade](#3-detalhes-de-conectividade-e-credenciais)
- [🔑 Credenciais de Acesso](#-credenciais-de-acesso)
- [⚙️ Configuração e Execução](#️-configuração-e-execução)
    - [Acesso à API](#-acesso-à-api-carga-inicial)
    - [Executando a Aplicação](#executando-a-aplicação)
- [🧪 Estratégia de Testes](#-estratégia-de-testes)
- [📖 Documentação da API (Swagger)](#-documentação-da-api-swagger)
- [🧠 Decisões Técnicas](#-decisões-técnicas)
    - [Estratégia de Multi-tenancy](#-estratégia-de-multi-tenancy-isolamento-de-dados)
    - [Validação e Integridade](#-validação-de-input-e-integridade)
    - [Abordagem de Upload](#-abordagem-de-upload-multipart-form)
- [🧪 Guia de Testes (Insomnia / Postman)](#-guia-de-testes-insomnia--postman)
- [📂 Gestão de Versões e Histórico](#-gestão-de-versões-e-histórico-)
- [⚙️ Gestão de Configurações e Perfis](#️-gestão-de-configurações-e-perfis-profiles)
- [🔐 Variáveis de Ambiente e Segurança](#-variáveis-de-ambiente-e-segurança)

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
## 🔑 Credenciais de Acesso

Para validar o controle de permissões (**RBAC**) e as funcionalidades exclusivas de cada nível de acesso, utilize as contas abaixo:

| Perfil | Usuário | Senha | Acessos Exclusivos |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` | Gestão de Usuários e Trilha de Auditoria |
| **USER** | `dhenSouza` | `user123` | Gestão de Documentos e Upload de Versões |

> **Dica de Teste:** Ao logar como **ADMIN**, note que o menu lateral e as ações de edição de usuários estarão visíveis. Ao logar como **USER**, essas opções são ocultadas automaticamente pela lógica de Signals e Guards do Angular.
---

## ⚙️ Configuração e Execução

 **🔐 Acesso à API (Carga Inicial)**

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

## 🧠 Estratégia de Multi-tenancy (Isolamento de Dados)
  A aplicação foi projetada utilizando a abordagem de Shared Schema, onde os dados de diferentes organizações coexistem na mesma tabela, mas são isolados logicamente via uma coluna tenant_id.

* **Segurança Baseada** em Token: O tenantId é extraído diretamente das claims do Token JWT durante o processo de autenticação. Isso impede que um usuário mal-intencionado altere o contexto de dados através do corpo da requisição.

* **Filtragem Automática:** Todas as consultas ao banco de dados utilizam o DocumentSpecifications.hasTenant(tenantId), garantindo que um usuário nunca visualize ou manipule documentos de outra organização, atendendo ao requisito de isolamento do MVP.

### 📝 Validação de Input e Integridade
* ** Utilizamos o Jakarta Bean Validation integrado aos Java Records para garantir que a API seja resiliente a dados malformados.

* **Constraints Robustas:** Campos críticos como title possuem validações de presença (@NotBlank) e limites de tamanho (@Size), refletidas automaticamente na documentação Swagger.

* **Padronização de Erros:** Implementamos um GlobalExceptionHandler que captura falhas de validação e retorna um JSON estruturado com status 400 Bad Request, facilitando o tratamento de erros no Front-end Angular.

📤 Abordagem de Upload (Multipart Form)
* Diferente de APIs puramente JSON, a criação de documentos utiliza o padrão multipart/form-data para suportar o envio simultâneo de metadados e arquivos binários.

* Uso de @ModelAttribute: Optamos por esta anotação para permitir um mapeamento "achatado" (flat) dos campos de texto. Isso garante maior compatibilidade com bibliotecas de Front-end e ferramentas de teste, evitando problemas comuns de Content-Type em partes individuais da requisição.

* Versionamento Incremental: Cada upload é processado como uma transação única que gera um novo registro na trilha de auditoria e incrementa a versão do documento no storage.

---
## 🧪 Guia de Testes (Insomnia / Postman)

Como o endpoint de criação utiliza Multipart Form, a configuração do teste exige atenção aos detalhes de cada campo:

* Criando um Novo Documento  Método & URL: POST para http://localhost:8080/api/documents

* Autenticação: Selecione Bearer Token e insira o JWT gerado no login.

Corpo (Body): Selecione Multipart Form e adicione as seguintes chaves:

| Key | Value | Type |
| :--- | :--- | :--- |
| **title** | Meu Documento Técnico | Text |
| **description** | Descrição de teste para o desafio | Text |
| **tags** | JAVA | Text |
| **tags** | SPRING | Text |
| **file** | *(Selecione um arquivo PDF/PNG/JPG)* | File |

## --- GESTÃO DE VERSÕES E HISTÓRICO ---

1. ENDPOINT DE HISTÓRICO:
- Implementado o endpoint GET /api/documents/{id}/versions para fornecer a trilha completa de evolução de cada arquivo.
- A listagem é retornada em ordem decrescente (mais recente primeiro) para priorizar a visualização do estado atual do documento.

2. SEGURANÇA NA CAMADA DE SERVIÇO:
- O método listVersions realiza uma verificação prévia de existência do documento pai, lançando EntityNotFoundException caso o ID seja inválido, evitando inconsistências na resposta da API.

3. OTIMIZAÇÃO DE DTO (DocumentVersionResponse):
- O mapeamento no Service extrai apenas o 'username' do objeto 'Account' associado à versão. Isso isola dados sensíveis do usuário (como senhas ou tokens) e garante que o Front-end receba uma estrutura leve e pronta para exibição em tabelas.
---
## ⚙️ Gestão de Configurações e Perfis (Profiles)
A aplicação utiliza o recurso de Spring Profiles para separar as configurações de desenvolvimento local das configurações de produção, garantindo portabilidade e segurança.

* Perfil local (Desenvolvimento): Configurado para execução em ambiente controlado. Busca o banco de dados PostgreSQL e o storage MinIO (compatível com S3) rodando localmente via Docker Compose.

* Perfil prod (Produção): Utilizado para o deploy em plataformas Cloud (como Render e Vercel). Neste perfil, a aplicação consome variáveis de ambiente reais para conexão com o PostgreSQL gerenciado e o bucket S3 do Supabase.

## 🔐 Variáveis de Ambiente e Segurança
Seguindo as boas práticas de segurança, o projeto não armazena credenciais sensíveis (chaves de acesso, senhas de banco) diretamente no código-fonte ou em arquivos versionados. Utilizamos a estratégia de Placeholders que são resolvidos dinamicamente:

Fallbacks Locais: No arquivo application-local.properties, definimos valores padrão (ex: http://localhost:9000 para o MinIO). Isso permite que qualquer desenvolvedor suba o projeto imediatamente após o comando docker-compose up, sem configurações manuais no S3.

 * Injeção em Produção: Em ambiente Cloud, os placeholders (ex: ${STORAGE_S3_ENDPOINT}) são preenchidos automaticamente pelas variáveis de ambiente configuradas no painel administrativo da plataforma (Render/Vercel).
---

**Desenvolvido por DhenSouza**
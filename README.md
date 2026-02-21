# GED Document Manager Core

Sistema de Gestão Eletrônica de Documentos (GED) desenvolvido como parte de um desafio técnico para **Desenvolvedor Java Backend Pleno**. A solução contempla o ciclo de vida de documentos, incluindo **versionamento, auditoria e controle de acesso**.

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Infraestrutura com Docker](#-infraestrutura-com-docker)
- [Configuração e Execução](#️-configuração-e-execução)
- [Decisões Técnicas](#-decisões-técnicas)

---

## 🚀 Sobre o Projeto

O sistema permite a gestão de metadados e arquivos (**PDF/PNG/JPG**), garantindo a integridade através de **versionamento incremental** e **trilhas de auditoria detalhadas** para cada operação realizada.

### Funcionalidades Principais

- **Autenticação e Autorização via JWT** (`ADMIN`, `USER`, `VIEWER`)
- **CRUD completo de documentos** com filtros avançados e paginação
- **Upload/Download de arquivos** com controle de versão e checksum **SHA-256**
- **Auditoria completa** de eventos do sistema

---

## 🛠 Tecnologias

- **Linguagem:** Java 21 (LTS)
- **Framework:** Spring Boot 4.0.3
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de Dados:** PostgreSQL 15
- **Migrações:** Flyway
- **Segurança:** Spring Security + OAuth2 Resource Server (JWT)
- **Containerização:** Docker & Docker Compose

---

## 🐳 Infraestrutura com Docker

O projeto utiliza **Docker Compose** para orquestrar os ambientes de desenvolvimento e teste de forma isolada.

### 1. Pré-requisitos

- Docker instalado e configurado
- *(Opcional para Windows)* WSL2 configurado com integração Docker ativa

### 2. Subindo os Bancos de Dados

Na raiz do projeto, execute o comando abaixo para subir o banco de dados principal (**porta 5432**) e o banco de testes (**porta 5433**):

```bash
docker compose up -d
```

### 3. Caminhos de Execução no Docker

Existem dois caminhos principais para gerenciar os containers:

#### Via Terminal Nativo (Windows/Linux/Mac)

Basta rodar o comando acima, desde que o executável do Docker esteja no seu `PATH`.

#### Via WSL (Ubuntu)

Se você gerencia o Docker dentro do subsistema Linux, acesse o diretório montado e execute:

```bash
cd /mnt/c/caminho/para/projeto
docker compose up -d
```

---

## ⚙️ Configuração e Execução

### Migrações do Banco (Flyway)

As migrações são executadas automaticamente na inicialização da aplicação.  
Os scripts estão localizados em:

`src/main/resources/db/migration`

### Executando a Aplicação

```bash
./mvnw spring-boot:run
```

### Executando Testes

Para rodar os testes unitários e de integração (requisito mínimo de **5 testes relevantes**):

```bash
./mvnw test
```

---

## 🧠 Decisões Técnicas

- **Java 21:** escolha estratégica pela estabilidade (LTS) e uso de recursos modernos como **Records** para DTOs.
- **Flyway:** utilizado para garantir a reprodutibilidade do banco de dados e controle de versão de esquema.
- **UUID:** identificadores universais para evitar exposição de IDs sequenciais e facilitar integrações futuras.
- **Sem Lombok:** opção por código Java puro para demonstrar domínio da estrutura da linguagem e padrões de projeto manuais.

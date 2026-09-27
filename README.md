<div align="center">

# 🌱 Tópicos - Backend

-----

<a href="#sobre" >Sobre </a> |
<a href="#tecnologias"> Tecnologias </a> |
<a href="#estudos"> Estudos </a> |
<a href="#executar"> Executar </a> |
<a href="#comandos"> Comandos </a> |
<a href="#dependencias"> Dependências </a> |
<a href="#extensoes"> Extensões Úteis</a> |
<a href="#arquitetura"> Arquitetura </a> |
<a href="#estrutura"> Estrutura </a> |    
<a href="#endpoints"> Endpoints </a> |
<a href="#troubleshooting"> Troubleshooting </a>

</div>

## 🌿 Sobre <a id="sobre"></a>

API e plataforma acadêmica construída a partir de conceitos e aprendizados explorados ao longo da disciplina de **Tópicos Especiais em Informática**.

O projeto serve como ambiente de experimentação e estudo, permitindo aplicar na prática os conceitos apresentados em aula e documentar sua evolução ao longo do desenvolvimento.

## 🔧 Tecnologias <a id="tecnologias"></a>

![Spring](https://img.shields.io/badge/Spring-66BB6A?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-66BB6A?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-66BB6A?style=for-the-badge&logo=apachemaven&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-66BB6A?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-66BB6A?style=for-the-badge&logo=docker&logoColor=white)
![Insomnia](https://img.shields.io/badge/Insomnia-66BB6A?style=for-the-badge&logo=Insomnia&logoColor=white)


## 📖 Estudos <a id="estudos"></a>

### 🧪 Anotações
Você pode encontrar minhas anotações em [docs/](docs/)

## 🧠 Conceitos-chave

| Conceito | Definição curta |
|---|---|
| **JPA** | Especificação Java para mapear objetos ↔ tabelas |
| **Hibernate** | Implementação de JPA usada pelo Spring Boot |
| **Entity** | Classe Java anotada com `@Entity` que vira tabela |
| **Repository** | Interface que estende `JpaRepository` (CRUD pronto) |
| **Service** | Camada de regras de negócio |
| **Controller** | Camada HTTP (recebe requests, devolve responses) |
| **DTO** | Objeto para transferir dados entre camadas |
| **Flyway** | Ferramenta de versionamento de schema do banco |
| **Testcontainers** | Biblioteca que sobe containers para testes |
| **Dev Container** | Ambiente de dev dentro de um container Docker |

## 🚀 Como Executar <a id="executar"></a>

### Pré-requisitos
* Java JDK 17+ instalado
* Maven instalado (ou utilizar o `./mvnw` do projeto)
* Docker & Docker Compose (para subida do banco PostgreSQL)

### Passo a Passo

1. Clone o repositório:
```sh
git clone https://github.com/raphaelamonteiro/topicos-backend.git
cd topicos-backend
```

2. Suba o container do banco de dados:

```sh
docker compose up -d
```

3. Execute a aplicação:

```sh
mvn spring-boot:run

```

## 🧩 Comandos Úteis <a id="comandos"></a>

```sh
# Limpa o projeto e executa os testes
mvn clean test

# Compila e gera o arquivo .jar em target/
mvn clean package

# Exibe a árvore de dependências para verificar conflitos
mvn dependency:tree

```

## 📦 Dependências do Projeto <a id="dependencias"></a>
Adicionadas com o [Spring Initializr](https://start.spring.io/)
* Spring Boot DevTools
* Spring Web
* Spring Data JPA
* PostgreSQL Driver

## 📚 Extensões Úteis do VS Code <a id="extensoes"></a>
* [Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)
* [SQLTools PostgreSQL/Cockroach Driver](https://marketplace.visualstudio.com/items?itemName=mtxr.sqltools-driver-pg#review-details)
* [Prettier - Code formatter](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)
* [Thunder Client](https://marketplace.visualstudio.com/items?itemName=rangav.vscode-thunder-client) 


## 🏗️ Arquitetura <a id="arquitetura"></a>

```text
Cliente (Insomnia / curl / navegador)
        │
        ▼  HTTP
   Controller  (@RestController)
        │
        ▼  chamada Java
    Service    (@Service)
        │
        ▼  chamada Java
   Repository  (JpaRepository)
        │
        ▼  SQL
   PostgreSQL (container Docker)
```


## 📁 Estrutura do Projeto <a id="estrutura"></a>

```text
topicos-backend/
│
├── .devcontainer/              # Configuração do ambiente de desenvolvimento
├── .mvn/                       # Arquivos do Maven Wrapper
├── docs/                       # Documentação técnica do projeto
│
├── src/
│   ├── main/
│   │   ├── java/br/com/topicos/
│   │   │   ├── TopicosApplication.java   # Ponto de entrada
│   │   │   ├── config/                   # Beans globais e configurações
│   │   │   ├── controller/               # Controllers REST
│   │   │   ├── entity/                   # Entidades JPA
│   │   │   ├── repository/               # Interfaces Spring Data JPA
│   │   │   └── service/                  # Regras de negócio
│   │   │
│   │   └── resources/
│   │       └── application.properties    # Configuração
│   │
│   └── test/                             # Testes unitários e de integração
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
├── LICENSE
└── README.md
```

---

## 🎯 Guia de Endpoints: <a id="endpoints"></a>

| Método | Endpoint | Ação |
|---|---|---|
| `POST` | `/curso` | Cadastra um curso |
| `GET` | `/curso` | Lista todos os cursos |
| `GET` | `/curso/{id}` | Busca curso por ID (path) |
| `POST` | `/disciplina` | Cadastra uma disciplina |
| `GET` | `/disciplina` | Lista todas as disciplinas |
| `GET` | `/disciplina/{id}` | Busca disciplina por ID (path) |
| `GET` | `/disciplina/pesquisa?id=X` | Busca disciplina por ID (query param) |

Base URL: `http://localhost:8080`

> Mais detalhes em [docs/endpoints.md](docs/endpoints.md)

### 🔍 Status HTTP

| Status | Significado |
|---|---|
| **200 OK** | Requisição bem-sucedida (GET) |
| **201 Created** | Recurso criado com sucesso (POST) |
| **400 Bad Request** | JSON malformado ou validação falhou |
| **404 Not Found** | Recurso não existe (ex: ID inexistente) |
| **500 Internal Server Error** | Erro no servidor (ex: `NullPointerException` no service) |


## 🐛 Troubleshooting <a id="troubleshooting"></a>

### Erro: `password authentication failed for user "spring"`
**Causa:** credenciais no `application.properties` não batem com o `docker-compose.yml`
**Solução:** use `postgres`/`postgres` (do `POSTGRES_USER`/`POSTGRES_PASSWORD`)

### Erro: `Connection refused to localhost:5432`
**Causa:** dentro do devcontainer, o banco tem host `db`, não `localhost`
**Solução:** `spring.datasource.url=jdbc:postgresql://db:5432/postgres`

### Erro: `docker: command not found`
**Causa:** feature `docker-outside-of-docker` não ativa no devcontainer
**Solução:** adicionar no `devcontainer.json` + rebuild

### Erro: `permission denied /var/run/docker.sock`
**Solução:** `sudo chmod 666 /var/run/docker.sock`

---

### ⭐ Gostou do projeto?

Se este projeto foi útil para você, deixe uma ⭐ no repositório.

💻 Desenvolvido por [Raphaela Monteiro](https://github.com/raphaelamonteiro).

> Laboratório de estudos desenvolvido ao longo da disciplina de Tópicos Especiais em Informática, sob orientação do professor [Emanuel Mineda](https://github.com/mineda).

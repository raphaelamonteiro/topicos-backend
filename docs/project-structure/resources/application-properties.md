# Configuração: `application.properties`

 **Resumo:** arquivo central de configuração do Spring Boot.

 Define o nome da aplicação, a conexão com o banco de dados, o nível de logging do Hibernate e o modo de gerenciamento do schema pelo JPA/Hibernate.

 **Caminho:** `src/main/resources/application.properties`

 ## 📄 Conteúdo

```
spring.application.name=topicos

## Logging
logging.level.org.hibernate.SQL=debug

## Spring DATASOURCE
spring.datasource.url=jdbc:postgresql://db:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres

## Hibernate ddl auto
spring.jpa.hibernate.ddl-auto=update
```

 > **Arquivo fonte:** `src/main/resources/application.properties`

 ## 🎯 Objetivo

 O `application.properties` é o arquivo central de configuração do Spring Boot.

 Ele define:

 - **Identidade** da aplicação
- **Conexão** com o banco de dados
- **Comportamento** do Hibernate/JPA
- **Nível de log** das queries SQL

 > 💡 **Alternativa:** o Spring Boot também aceita `application.yml`, que utiliza o formato YAML. Este projeto utiliza `.properties`, no formato `chave=valor`.

---

 ## 🔧 Propriedades

 ### `spring.application.name=topicos`

 **Função:** define o nome da aplicação.

 **Onde é usado:**

 - Logs do Spring Boot
- Ferramentas de monitoramento
- Service discovery, quando configurado

 **Exemplo:**

```
2026-09-27T02:33:02.540Z  INFO 13803 --- [topicos] ...
                                          ^^^^^^^^
                                          nome da aplicação
```

---

 ### `logging.level.org.hibernate.SQL=debug`

 **Função:** habilita o nível `DEBUG` para os logs SQL gerados pelo Hibernate.

 **Efeito:** as queries executadas pelo Hibernate podem aparecer no console:

```
DEBUG org.hibernate.SQL : select c.cur_id, c.cur_nome, c.cur_sigla from cur_curso c
DEBUG org.hibernate.SQL : insert into aln_aluno (aln_nome, aln_ra) values (?, ?)
```

 #### Níveis de logging

 | Nível | Quando usar |
| --- | --- |
| `TRACE` | Log extremamente detalhado |
| `DEBUG` | Desenvolvimento |
| `INFO` | Informações gerais |
| `WARN` | Avisos |
| `ERROR` | Erros |
| `OFF` | Desabilitado |

> ⚠️ **Produção:** evite deixar o logging SQL em `DEBUG`, pois pode gerar grande quantidade de logs e, dependendo da configuração, expor informações que não deveriam aparecer no log.

---

 ### `spring.datasource.url`

 **Função:** define a URL JDBC utilizada para conectar ao banco de dados.

 **Formato:**

```
jdbc:postgresql://{host}:{porta}/{database}
```

 **Neste projeto:**

```
jdbc:postgresql://db:5432/postgres
```

 #### Anatomia da URL

```
jdbc:postgresql://db:5432/postgres
└──────┬───────┘  └┬┘ └┬┘ └───┬────┘
     driver      host porta  database
```

 | Componente | Valor | Descrição |
| --- | --- | --- |
| Driver | `jdbc:postgresql://` | Driver/protocolo JDBC do PostgreSQL |
| Host | `db` | Nome do serviço do banco no Docker Compose |
| Porta | `5432` | Porta padrão do PostgreSQL |
| Database | `postgres` | Nome do banco de dados |

> ⚠️ **Pegadinha comum:** dentro do devcontainer, o host não deve ser `localhost` quando o PostgreSQL está em outro container. Nesse caso, `localhost` aponta para o próprio container da aplicação. Como o banco está em outro container da mesma rede Docker, o serviço pode ser acessado pelo nome `db`.

---

 ### `spring.datasource.username`

 **Função:** define o usuário utilizado para acessar o banco de dados.

```
spring.datasource.username=postgres
```

 ### `spring.datasource.password`

 **Função:** define a senha utilizada para acessar o banco de dados.

```
spring.datasource.password=postgres
```

 Esses valores correspondem às configurações do PostgreSQL no `docker-compose.yml`:

```
db:
  environment:
    POSTGRES_USER: postgres
    POSTGRES_PASSWORD: postgres
```

 > ⚠️ **Segurança:** em produção, evite colocar credenciais diretamente no `application.properties`. Prefira variáveis de ambiente ou um gerenciador de segredos.

 Por exemplo:

```
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

 ### `spring.jpa.hibernate.ddl-auto=update`

 **Função:** define como o Hibernate deve lidar com o **schema** do banco de dados durante a inicialização da aplicação.

 #### Valores principais

 | Valor | Comportamento | Uso comum |
| --- | --- | --- |
| `none` | Não faz alterações no schema | Produção com migrations |
| `validate` | Apenas valida se entidades e tabelas são compatíveis | Produção |
| `update` | Cria/altera estruturas conforme as entidades | Desenvolvimento |
| `create` | Apaga e recria o schema ao iniciar | Testes rápidos |
| `create-drop` | Cria ao iniciar e remove ao finalizar | Testes |

> ⚠️ **Importante:** o comportamento exato de `ddl-auto` pode variar conforme o banco e a versão do Hibernate. Em ambientes de produção, migrations versionadas são normalmente preferíveis a deixar o Hibernate modificar o schema automaticamente.

 #### O que acontece com `update`?

 Ao iniciar a aplicação, o Hibernate:

 1. Lê as entidades anotadas com `@Entity`.
2. Compara as entidades com o schema existente.
3. Cria estruturas que estejam faltando, quando possível.
4. Realiza alterações de schema suportadas pelo Hibernate.
5. Não deve ser tratado como um mecanismo de migrations/versionamento.

 Exemplos de SQL que podem aparecer no log:

```
create table aln_aluno (...);

create table cur_curso (...);

alter table if exists dis_disciplina
    add constraint ...;
```

 > ⚠️ **Produção:** `update` é conveniente para desenvolvimento, mas não é uma estratégia adequada para controlar alterações de schema em ambientes de produção. Para isso, prefira migrations versionadas, como Flyway ou Liquibase.

---

 # 🔐 Boas práticas

 ## Desenvolvimento

 Uma configuração como a atual é adequada para um ambiente de desenvolvimento:

 - `ddl-auto=update`
- Credenciais simples para o ambiente local
- Logging SQL habilitado em `DEBUG`

 ## Produção

 Uma configuração mais apropriada normalmente envolve:

 - `ddl-auto=validate` ou gerenciamento equivalente do schema
- Credenciais fornecidas por variáveis de ambiente ou secret manager
- Logging adequado ao ambiente
- Migrations versionadas com Flyway ou Liquibase

---

 # 🧪 Exercícios para fixar

 ## 1\. O que acontece se eu trocar `update` por `validate`?

 \<details\> \<summary\>Resposta\</summary\> O Hibernate apenas **valida** se o schema existente é compatível com as entidades.

 Ele não cria nem altera tabelas para corrigir diferenças.

 Se houver uma incompatibilidade, a aplicação pode falhar durante a inicialização com um erro de validação.

 \</details\> ## 2\. Por que o host é `db` e não `localhost`?

 \<details\> \<summary\>Resposta\</summary\> Porque o banco está em **outro container** na mesma rede Docker.

 Dentro do container da aplicação, `localhost` representa o próprio container da aplicação.

 O nome `db` corresponde ao serviço do PostgreSQL definido no Docker Compose e pode ser usado para comunicação entre os containers na mesma rede.

 \</details\> ## 3\. Se eu mudar a senha no `docker-compose.yml`, o que preciso fazer?

 \<details\> \<summary\>Resposta\</summary\> É necessário garantir que a aplicação utilize a mesma senha:

 1. Alterar a senha utilizada pelo `application.properties` ou pela variável de ambiente.
2. Verificar o comportamento do volume existente do PostgreSQL.
3. Se for um banco de desenvolvimento descartável e você quiser recriá-lo do zero, pode utilizar:

```
docker compose down -v
docker compose up
```

 > ⚠️ `docker compose down -v` remove os volumes associados ao Compose. **Não utilize isso em um banco com dados que você precisa preservar.**

 Além disso, as variáveis `POSTGRES_USER`, `POSTGRES_PASSWORD` e `POSTGRES_DB` são utilizadas pelo PostgreSQL principalmente durante a inicialização de um diretório de dados novo. Alterar essas variáveis não significa necessariamente alterar automaticamente as credenciais de uma instalação já existente.

 \</details\>
---

 # 💡 Configurações adicionais

 ## `spring.jpa.show-sql=true`

 O Spring Boot/JPA também possui:

```
spring.jpa.show-sql=true
```

 Essa propriedade faz com que o SQL seja exibido diretamente no console.

 Como o projeto já utiliza:

```
logging.level.org.hibernate.SQL=debug
```

 não é necessário adicionar `spring.jpa.show-sql=true` apenas para visualizar as queries.

 > 💡 **Recomendação:** evite habilitar as duas opções sem necessidade, para não duplicar a saída SQL.

---

 ## `spring.jpa.properties.hibernate.format_sql=true`

 Essa propriedade formata o SQL para facilitar a leitura:

```
spring.jpa.properties.hibernate.format_sql=true
```

 ### Antes

```
select c.cur_id, c.cur_nome, c.cur_sigla from cur_curso c where c.cur_sigla=?
```

 ### Depois

```
select
    c.cur_id,
    c.cur_nome,
    c.cur_sigla
from
    cur_curso c
where
    c.cur_sigla=?
```

 É uma opção útil durante o desenvolvimento e debugging.

 Como ela não faz parte do `application.properties` atual, pode ser adicionada somente se você quiser essa formatação.

---

 # 📁 Organização da documentação

 Sugestão de estrutura:

```
docs/
└── config/
    ├── README.md
    ├── application-properties.md
    ├── pom-xml.md
    └── TopicosApplication.md
```

---

 # 📚 Índice de configuração

 Arquivo: `docs/config/README.md`

```
# ⚙️ Configuração

Documentação dos principais arquivos de configuração do projeto.

| Arquivo | Descrição |
|---|---|
| [`application-properties.md`](application-properties.md) | Configurações do Spring Boot |
| [`pom-xml.md`](pom-xml.md) | Dependências e configuração do Maven |
| [`TopicosApplication.md`](TopicosApplication.md) | Ponto de entrada da aplicação |
```

---

 # 🔗 Arquivos relacionados

 | Arquivo | Papel |
| --- | --- |
| `docker-compose.yml` | Define o serviço e as configurações do banco |
| `pom.xml` | Define dependências e configuração do Maven |
| `TopicosApplication.java` | Ponto de entrada da aplicação |

---

 # 📋 Resumo

 | Propriedade | O que você precisa saber |
| --- | --- |
| `spring.application.name` | Define o nome da aplicação |
| `logging.level.org.hibernate.SQL=debug` | Exibe as queries SQL do Hibernate no log |
| `spring.datasource.url` | Define a URL JDBC de conexão com o banco |
| `spring.datasource.username/password` | Define as credenciais de acesso ao banco |
| `spring.jpa.hibernate.ddl-auto=update` | Permite que o Hibernate atualize o schema durante o desenvolvimento |
| `spring.jpa.show-sql=true` | Alternativa para exibir SQL diretamente no console |
| `hibernate.format_sql=true` | Formata o SQL para facilitar a leitura |

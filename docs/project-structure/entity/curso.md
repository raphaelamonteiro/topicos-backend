# Entity: Curso

**Resumo:** Entidade JPA que representa a tabela `cur_curso`.

> Cada instância dessa classe corresponde a uma linha na tabela.
> Mapeia os campos `id`, `nome` e `sigla` para as colunas `cur_id`, `cur_nome` e `cur_sigla`.

## 📄 Código

```java
package br.com.topicos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cur_curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cur_id")
    private Long id;

    @Column(name = "cur_nome")
    private String nome;

    @Column(name = "cur_sigla")
    private String sigla;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

}
```

> **Arquivo fonte:** [`src/main/java/br/com/topicos/entity/Curso.java`](../../../src/main/java/br/com/topicos/entity/Curso.java)

## 🎯 Objetivo

A classe `Curso` é uma **entidade JPA** que representa a tabela `cur_curso` no banco de dados.

Ela faz parte da **camada de persistência** e é usada em toda a aplicação:
- Como **retorno** dos endpoints (`GET /curso`)
- Como **entrada** no cadastro (`POST /curso`)
- Como **tipo** nas camadas `Service` e `Repository`

> 💡 **Conceito-chave:** em JPA, uma **classe** vira uma **tabela**,
> e um **objeto** dessa classe vira uma **linha** dessa tabela.

## 🧩 Anotações principais

| Anotação | Função |
|---|---|
| `@Entity` | Marca a classe como entidade JPA (vira tabela no banco) |
| `@Table(name = "cur_curso")` | Define o nome exato da tabela no banco |
| `@Id` | Marca o campo como chave primária (PK) |
| `@GeneratedValue(strategy = IDENTITY)` | ID é gerado pelo banco (auto-incremento) |
| `@Column(name = "cur_id")` | Mapeia o campo Java `id` para a coluna `cur_id` |

## 🧬 Estrutura da entidade

### Classe

```java
@Entity
@Table(name = "cur_curso")
public class Curso { ... }
```

- **`@Entity`** diz ao Hibernate: *"essa classe é uma tabela"*
- **`@Table(name = "cur_curso")`** sobrescreve o nome padrão (`curso`)

> 💡 **Por que prefixo `cur_`?** Convenção do projeto para evitar conflito de nomes
> entre tabelas e deixar explícito a qual entidade a coluna pertence.

### Chave primária

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "cur_id")
private Long id;
```

| Anotação | Papel |
|---|---|
| `@Id` | Identifica o campo como PK |
| `@GeneratedValue(strategy = IDENTITY)` | Delega a geração do ID ao banco (auto-incremento) |
| `@Column(name = "cur_id")` | Nome da coluna no banco |

**Estratégias de geração de ID:**

| Estratégia | Como funciona | Banco |
|---|---|---|
| `IDENTITY` | Auto-increment do banco | PostgreSQL ✅ |
| `SEQUENCE` | Usa sequence do banco | Oracle, PostgreSQL |
| `AUTO` | Hibernate escolhe | Qualquer |
| `TABLE` | Tabela auxiliar | Raro |

### Campos de dados

```java
@Column(name = "cur_nome")
private String nome;

@Column(name = "cur_sigla")
private String sigla;
```

| Campo Java | Coluna no banco | Tipo Java | Tipo SQL |
|---|---|---|---|
| `id` | `cur_id` | `Long` | `BIGINT` (PK, identity) |
| `nome` | `cur_nome` | `String` | `VARCHAR(255)` |
| `sigla` | `cur_sigla` | `String` | `VARCHAR(255)` |

## 🗄️ Tabela gerada

Com `spring.jpa.hibernate.ddl-auto=update`, o Hibernate gera esta tabela automaticamente:

```sql
create table cur_curso (
    cur_id bigint generated always as identity,
    cur_nome varchar(255),
    cur_sigla varchar(255),
    primary key (cur_id)
);
```

> 💡 **Observação:** como o `@Column` não define `nullable=false` nem
> `unique=true`, essas restrições **não são aplicadas** pelo Hibernate.
> Se quiser que `cur_sigla` seja única, use:
> ```java
> @Column(name = "cur_sigla", nullable = false, unique = true)
> ```

## 🔗 Relacionamentos

**Esta entidade não possui relacionamentos diretos no lado JPA.**

Mas ela é **referenciada** por:

| Entidade | Tipo de relação | Como |
|---|---|---|
| `Disciplina` | N:1 | `Disciplina.dis_cur_id` → `Curso.cur_id` |

Ou seja: **um curso pode ter várias disciplinas**, mas a relação é mapeada **do lado de `Disciplina`** (não há `@OneToMany` aqui).

> 💡 **Modelagem unidirecional:** evita `@OneToMany` para simplificar e
> evitar problemas de serialização (loops infinitos no JSON).

## 🔧 Getters e Setters

```java
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getNome() { return nome; }
public void setNome(String nome) { this.nome = nome; }

public String getSigla() { return sigla; }
public void setSigla(String sigla) { this.sigla = sigla; }
```

**Para que servem:**
- **Hibernate** usa pra ler/escrever valores ao salvar/carregar do banco
- **Jackson** usa pra converter objeto ↔ JSON (nos controllers)
- **Encapsulamento** — o padrão Java de não expor campos diretamente

> 💡 **Dica:** você pode substituir todos esses métodos por **Lombok**:
> ```java
> @Data
> @Entity
> @Table(name = "cur_curso")
> public class Curso { ... }
> ```
> `@Data` gera getters, setters, `equals`, `hashCode` e `toString` automaticamente.

## 🏗️ Onde essa entidade se encaixa

```text
Cliente
   │
   │ HTTP (JSON)
   ▼
CursoController
   │
   │ Objeto Curso
   ▼
CursoService
   │
   │ Objeto Curso
   ▼
CursoRepository
   │
   │ Objeto Curso
   ▼
┌──────────────────┐
│  Entity: Curso   │ ← esta classe
└──────────────────┘
   │
   │ SQL (INSERT, SELECT, UPDATE, DELETE)
   ▼
PostgreSQL
```

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **JPA** | Especificação Java para mapear objetos ↔ tabelas |
| **Hibernate** | Implementação de JPA usada pelo Spring Boot |
| **`@Entity`** | Marca a classe como entidade persistível |
| **`@Table`** | Define o nome da tabela |
| **`@Id`** | Define a chave primária |
| **`@GeneratedValue`** | Define como o ID é gerado |
| **`@Column`** | Mapeia campo Java → coluna SQL |
| **`IDENTITY`** | ID auto-gerado pelo banco (auto-increment) |
| **ORM** | Object-Relational Mapping — mapeamento objeto-relacional |

## ⚠️ Boas práticas

### ✅ O que está bom no seu código
- Prefixos consistentes (`cur_`) evitam conflito de nomes
- `@Table` explícito (não depende do nome da classe)
- `@Column` explícito em todos os campos
- Uso de `Long` (wrapper) em vez de `long` (permite `null` antes de salvar)

### 💡 O que poderia melhorar
- **Adicionar Lombok** pra reduzir boilerplate
- **Adicionar `nullable = false`** e `unique = true` nos campos que exigem
- **Trocar `ddl-auto=update` por Flyway** em produção (você já tem o `01-schema.sql`)
- **Considerar `@Data` em vez de getters/setters manuais** (se usar Lombok)

## 🧪 Exercícios para fixar

1. **O que acontece se eu remover `@Table(name = "cur_curso")`?**
   <details>
   <summary>Resposta</summary>
   O Hibernate procuraria/criaria uma tabela chamada `curso` (nome da classe em
   minúsculo). Como o banco tem `cur_curso`, a validação falharia ou ele criaria
   uma tabela nova — gerando confusão.
   </details>

2. **O que muda se eu usar `@GeneratedValue(strategy = SEQUENCE)`?**
   <details>
   <summary>Resposta</summary>
   O Hibernate criaria/esperaria uma **sequence** no banco (ex: `hibernate_sequence`)
   em vez de usar o auto-increment `identity`. O SQL do banco seria diferente:
   em vez de `generated always as identity`, usaria `default nextval('sequence')`.
   </details>

3. **Por que o campo `id` é `Long` e não `long`?**
   <details>
   <summary>Resposta</summary>
   Porque `Long` (wrapper) permite `null`, enquanto `long` (primitivo) não.
   Antes de salvar no banco, o ID é `null` — o banco é quem gera o valor.
   Se fosse `long`, começaria com `0`, o que causaria problemas.
   </details>

4. **Se eu quiser que `cur_sigla` seja única, o que faço?**
   <details>
   <summary>Resposta</summary>
   Adicionar `unique = true` no `@Column`:
   ```java
   @Column(name = "cur_sigla", unique = true, nullable = false)
   private String sigla;
   ```
   </details>

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`CursoController.md`](../controllers/CursoController.md) | Camada HTTP |
| [`CursoService.md`](../services/CursoService.md) | Regras de negócio |
| [`CursoRepository.md`](../repositories/CursoRepository.md) | Acesso ao banco |
| [`application-properties.md`](../../config/application-properties.md) | Configuração do `ddl-auto` |

---

## 📝 Observações importantes sobre o seu `Curso.java`

### 1. Falta `@Column` de unicidade

No banco (seu `01-schema.sql`), a tabela tem:

```sql
constraint cur_sigla_uk unique (cur_sigla)
```

Mas a entidade **não tem** essa restrição:

```java
@Column(name = "cur_sigla")   // ← sem unique=true, nullable=false
private String sigla;
```

**Consequência:** se o Hibernate for o único a criar a tabela (`ddl-auto=update` sem rodar o schema), **a restrição de unicidade NÃO será aplicada**. Só existe porque você rodou o `01-schema.sql` manualmente.

**Sugestão:**

```java
@Column(name = "cur_sigla", nullable = false, unique = true)
private String sigla;
```

Assim a entidade **espelha** o banco, e não importa quem criou a tabela.

### 2. Sem Lombok

Você tem Lombok no `pom.xml`? Se sim, pode simplificar **muito**:

```java
@Entity
@Table(name = "cur_curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cur_id")
    private Long id;

    @Column(name = "cur_nome", nullable = false)
    private String nome;

    @Column(name = "cur_sigla", nullable = false, unique = true)
    private String sigla;
}
```

`@Data` gera getters, setters, `equals`, `hashCode` e `toString`. Menos 30 linhas de código.

**Mas atenção:** em prova, pode ser que o professor queira ver os getters/setters manuais (pra avaliar se você sabe fazer). Pergunta antes de refatorar.

### 3. Sobre o nome do arquivo

Seu arquivo está em:
```
src/main/java/br/com/topicos/Entity/Curso.java
```

**Convenção Java:** pacotes são **minúsculos** (`entity`, não `Entity`). Se está com maiúscula, vale corrigir antes de commitar — não quebra nada, mas é padrão.
**Próximo que sugiro:** `Disciplina.java` (entity), porque é o **único com relacionamento** (`@ManyToOne` com `Curso`). Se você entender esse, entende todos os outros.

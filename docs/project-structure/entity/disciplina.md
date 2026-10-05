# Entity: Disciplina

**Resumo:** Entidade JPA que representa a tabela `dis_disciplina`.

> Possui **dois relacionamentos**: `@ManyToOne` com `Curso` e
> `@ManyToMany` com `Aluno` (via tabela intermediária `mat_matricula`).

## 📄 Código

```java
package br.com.topicos.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dis_disciplina")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dis_id")
    private Long id;

    @Column(name = "dis_codigo")
    private String codigo;

    @Column(name = "dis_nome")
    private String nome;

    @Column(name = "dis_carga_horaria")
    private Integer cargaHoraria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dis_cur_id")
    private Curso curso;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "mat_matricula", joinColumns = @JoinColumn(name = "mat_dis_id"), inverseJoinColumns = @JoinColumn(name = "mat_aln_id"))
    private Set<Aluno> alunos;

    // getters e setters...
}
```

> **Arquivo fonte:** [`src/main/java/br/com/topicos/entity/Disciplina.java`](../../../src/main/java/br/com/topicos/entity/Disciplina.java)

## 🎯 Objetivo

A classe `Disciplina` é uma **entidade JPA** que representa a tabela `dis_disciplina`.

Além dos campos próprios (código, nome, carga horária), ela modela **dois relacionamentos**:
- **N:1** com `Curso` — cada disciplina pertence a **um curso**
- **N:N** com `Aluno` — uma disciplina tem **vários alunos**, e um aluno está em **várias disciplinas**

## 🧩 Anotações principais

| Anotação | Função |
|---|---|
| `@Entity` | Marca a classe como entidade JPA |
| `@Table(name = "dis_disciplina")` | Define o nome da tabela |
| `@Id` + `@GeneratedValue(IDENTITY)` | Chave primária auto-gerada |
| `@Column(name = "dis_...")` | Mapeia cada campo para sua coluna |
| `@ManyToOne` | Relação N:1 com `Curso` |
| `@JoinColumn` | Define a coluna da FK no lado "N" |
| `@ManyToMany` | Relação N:N com `Aluno` |
| `@JoinTable` | Define a tabela intermediária (`mat_matricula`) |
| `FetchType.EAGER` | Carrega o relacionamento **junto** com a entidade |

## 🧬 Estrutura da entidade

### Campos próprios

| Campo Java | Coluna no banco | Tipo Java | Tipo SQL |
|---|---|---|---|
| `id` | `dis_id` | `Long` | `BIGINT` (PK, identity) |
| `codigo` | `dis_codigo` | `String` | `VARCHAR(255)` |
| `nome` | `dis_nome` | `String` | `VARCHAR(255)` |
| `cargaHoraria` | `dis_carga_horaria` | `Integer` | `INT` |

## 🔗 Relacionamento 1: `@ManyToOne` com Curso

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "dis_cur_id")
private Curso curso;
```

**Significado:** *"Muitas disciplinas pertencem a um curso"*.

**Mapeamento no banco:**
```sql
alter table dis_disciplina
    add constraint ... foreign key (dis_cur_id)
    references cur_curso(cur_id);
```

**Anatomia:**

| Anotação | Papel |
|---|---|
| `@ManyToOne` | Lado "N" da relação (o lado que tem a FK) |
| `@JoinColumn(name = "dis_cur_id")` | Nome da coluna que guarda o ID do curso |
| `FetchType.EAGER` | Carrega o `Curso` junto com a `Disciplina` |

**Exemplo de JSON retornado:**
```json
{
  "id": 1,
  "codigo": "IMB003",
  "nome": "Arquitetura e Modelagem de BD",
  "cargaHoraria": 80,
  "curso": {
    "id": 1,
    "nome": "Banco de Dados",
    "sigla": "BD"
  }
}
```

## 🔗 Relacionamento 2: `@ManyToMany` com Aluno

```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "mat_matricula",
    joinColumns = @JoinColumn(name = "mat_dis_id"),
    inverseJoinColumns = @JoinColumn(name = "mat_aln_id")
)
private Set<Aluno> alunos;
```

**Significado:** *"Uma disciplina tem vários alunos, e um aluno está em várias disciplinas"*.

**Mapeamento no banco:** como é N:N, precisa de uma **tabela intermediária**:

```sql
create table mat_matricula (
    mat_dis_id bigint,   -- ← joinColumns
    mat_aln_id bigint,   -- ← inverseJoinColumns
    primary key (mat_dis_id, mat_aln_id),
    foreign key (mat_dis_id) references dis_disciplina(dis_id),
    foreign key (mat_aln_id) references aln_aluno(aln_id)
);
```

**Anatomia do `@JoinTable`:**

| Atributo | Papel |
|---|---|
| `name = "mat_matricula"` | Nome da tabela intermediária |
| `joinColumns` | Coluna que aponta pra **esta** entidade (`Disciplina`) |
| `inverseJoinColumns` | Coluna que aponta pra **outra** entidade (`Aluno`) |

**Por que `Set<Aluno>` e não `List<Aluno>`?**

| Coleção | Quando usar |
|---|---|
| `Set` | Sem duplicatas (aluno não pode estar 2x) ✅ |
| `List` | Permite duplicatas e mantém ordem |

**Exemplo de JSON retornado:**
```json
{
  "id": 1,
  "codigo": "IMB003",
  "curso": { "id": 1, "sigla": "BD" },
  "alunos": [
    { "id": 1, "ra": 1, "nome": "John Doe" },
    { "id": 2, "ra": 2, "nome": "Jane Smith" }
  ]
}
```

## 📊 FetchType — EAGER vs LAZY

| Tipo | Comportamento | Quando usar |
|---|---|---|
| **EAGER** | Carrega o relacionamento **junto** com a entidade (JOIN) | Quando você **sempre** precisa do dado |
| **LAZY** | Só carrega quando você **acessa** o campo (query separada) | Padrão recomendado |

**Neste projeto:**
- `@ManyToOne` com `Curso` → `EAGER` (o service sempre quer o curso)
- `@ManyToMany` com `Aluno` → `EAGER` (⚠️ funciona, mas não é ideal)

> ⚠️ **Boas práticas:** `@ManyToMany` deveria ser `LAZY`.
> Com `EAGER`, buscar uma disciplina traz **todos** os alunos matriculados,
> o que pode ser pesado. Além disso, `EAGER` em várias relações pode
> causar `MultipleBagFetchException` ou consultas gigantes.

## 🗄️ Tabela gerada

```sql
create table dis_disciplina (
    dis_id bigint generated always as identity,
    dis_codigo varchar(255),
    dis_nome varchar(255),
    dis_carga_horaria int,
    dis_cur_id bigint,
    primary key (dis_id),
    foreign key (dis_cur_id) references cur_curso(cur_id)
);
```

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **`@ManyToOne`** | Muitos → 1. O lado "N" carrega a FK |
| **`@JoinColumn`** | Define a coluna da FK na tabela "dona" |
| **`@ManyToMany`** | N ↔ N. Precisa de tabela intermediária |
| **`@JoinTable`** | Define a tabela intermediária e suas colunas |
| **`joinColumns`** | Coluna da tabela intermediária que aponta pra **esta** entidade |
| **`inverseJoinColumns`** | Coluna da tabela intermediária que aponta pra **outra** entidade |
| **`FetchType.EAGER`** | Carrega o relacionamento junto (JOIN) |
| **`FetchType.LAZY`** | Carrega só quando acessado (query separada) |
| **`Set` vs `List`** | `Set` sem duplicatas; `List` com ordem e duplicatas |

## 🧪 Exercícios para fixar

1. **Se eu trocar `EAGER` por `LAZY` no `@ManyToMany`, o que muda no JSON?**
   <details>
   <summary>Resposta</summary>
   O campo `alunos` não viria no JSON automaticamente — daria erro de
   `LazyInitializationException` se o controller tentasse serializar.
   Pra funcionar, seria preciso usar `@Transactional` no service ou um
   `JOIN FETCH` na query.
   </details>

2. **Se eu remover `@JoinTable`, o que acontece?**
   <details>
   <summary>Resposta</summary>
   O Hibernate criaria uma tabela intermediária com nome padrão
   (`disciplina_aluno` ou similar) e colunas padrão. Como você já tem
   `mat_matricula` no banco, ele não bateria — geraria erro ou tabela duplicada.
   </details>

3. **Por que usar `@ManyToOne` no lado de `Disciplina` (e não `@OneToMany` em `Curso`)?**
   <details>
   <summary>Resposta</summary>
   Porque **só o lado "N" guarda a FK**. Se fosse `@OneToMany` em `Curso`,
   a coluna `dis_cur_id` estaria em `Disciplina` de qualquer forma, mas o
   mapeamento do Hibernate seria diferente. Modelar como `@ManyToOne` é
   mais simples e direto.
   </details>

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`curso.md`](curso.md) | Entidade referenciada (N:1) |
| [`aluno.md`](aluno.md) | Entidade referenciada (N:N) |
| [`disciplina-controller.md`](../controllers/disciplina-controller.md) | Camada HTTP |
| [`disciplina-service.md`](../services/disciplina-service.md) | Regras de negócio |
| [`DisciplinaRepository.md`](../repositories/DisciplinaRepository.md) | Acesso ao banco |

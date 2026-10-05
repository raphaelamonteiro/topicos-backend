# Entity: Trabalho

**Resumo:** Entidade JPA que representa a tabela `tra_trabalho`.

> Cada instância corresponde a um trabalho entregue por um aluno.
> Possui `@ManyToOne` com `Aluno`.

## 📄 Código

```java
package br.com.topicos.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tra_trabalho")
public class Trabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tra_id")
    private Long id;

    @Column(name = "tra_titulo")
    private String titulo;

    @Column(name = "tra_data_hora_entrega")
    private LocalDateTime dataHoraEntrega;

    @Column(name = "tra_descricao")
    private String descricao;

    @Column(name = "tra_nota")
    private Integer nota;

    @Column(name = "tra_justificativa")
    private String justificativa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tra_aluno")
    private Aluno aluno;

    // getters e setters...
}
```

> **Arquivo fonte:** [`src/main/java/br/com/topicos/entity/Trabalho.java`](../../../src/main/java/br/com/topicos/entity/Trabalho.java)

## 🎯 Objetivo

A classe `Trabalho` é uma **entidade JPA** que representa a tabela `tra_trabalho`.

Ela modela os trabalhos entregues pelos alunos, incluindo:
- **Dados do trabalho:** título, descrição, data/hora de entrega
- **Avaliação:** nota e justificativa
- **Relacionamento:** cada trabalho pertence a **um aluno**

## 🧩 Anotações principais

| Anotação | Função |
|---|---|
| `@Entity` | Marca a classe como entidade JPA |
| `@Table(name = "tra_trabalho")` | Define o nome da tabela |
| `@Id` + `@GeneratedValue(IDENTITY)` | Chave primária auto-gerada |
| `@Column(name = "tra_...")` | Mapeia cada campo para sua coluna |
| `@ManyToOne` | Relação N:1 com `Aluno` |
| `@JoinColumn(name = "tra_aluno")` | Coluna da FK no lado "N" |
| `FetchType.EAGER` | Carrega o aluno junto com o trabalho |

## 🧬 Estrutura da entidade

| Campo Java | Coluna no banco | Tipo Java | Tipo SQL |
|---|---|---|---|
| `id` | `tra_id` | `Long` | `BIGINT` (PK, identity) |
| `titulo` | `tra_titulo` | `String` | `VARCHAR(255)` |
| `dataHoraEntrega` | `tra_data_hora_entrega` | `LocalDateTime` | `TIMESTAMP` |
| `descricao` | `tra_descricao` | `String` | `VARCHAR(255)` |
| `nota` | `tra_nota` | `Integer` | `INT` |
| `justificativa` | `tra_justificativa` | `String` | `VARCHAR(255)` |
| `aluno` | `tra_aluno` | `Aluno` (FK) | `BIGINT` |

## 📅 Tipos de data/hora em Java

| Tipo | O que representa | Uso típico |
|---|---|---|
| `LocalDate` | Só data | Data de nascimento |
| `LocalTime` | Só hora | Hora de um evento |
| **`LocalDateTime`** | **Data + hora (sem timezone)** ✅ | **Entrega de trabalho** |
| `Instant` | Momento UTC | Timestamps globais |
| `ZonedDateTime` | Data + hora + timezone | Eventos com fuso |

**Por que `LocalDateTime` aqui:** a entrega é um momento "local" — não importa o fuso. Se fosse um log de sistema global, seria `Instant`.

## 🔗 Relacionamento: `@ManyToOne` com Aluno

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "tra_aluno")
private Aluno aluno;
```

**Significado:** *"Muitos trabalhos pertencem a um aluno"*.

**Mapeamento no banco:**

```sql
alter table tra_trabalho
    add constraint tra_aln_fk
    foreign key (tra_aluno) references aln_aluno(aln_id);
```

**Anatomia:**

| Anotação | Papel |
|---|---|
| `@ManyToOne` | Lado "N" da relação (guarda a FK) |
| `@JoinColumn(name = "tra_aluno")` | Coluna que armazena o ID do aluno |
| `FetchType.EAGER` | Carrega o aluno junto (JOIN) |

**Exemplo de JSON:**

```json
{
  "id": 1,
  "titulo": "Teste 1",
  "dataHoraEntrega": "2026-10-05T14:30:00",
  "descricao": "Trabalho sobre Spring Boot",
  "nota": 6,
  "justificativa": "Bom, mas falta conteúdo",
  "aluno": {
    "id": 1,
    "ra": 1,
    "nome": "John Doe"
  }
}
```

## ⚠️ Campo `nota` é `Integer` (wrapper), não `int`

```java
@Column(name = "tra_nota")
private Integer nota;
```

**Por que `Integer` e não `int`:**

| Tipo | Permite `null`? | Comportamento padrão |
|---|---|---|
| `int` (primitivo) | ❌ Não | `0` se não atribuído |
| `Integer` (wrapper) | ✅ Sim | `null` se não atribuído |

**Impacto:** um trabalho **não corrigido** tem `nota = null`. Se fosse `int`, apareceria como `nota = 0` — que seria uma **nota válida** (e incorreta!).

**Mesma lógica vale para:**
- `Disciplina.cargaHoraria` (`Integer`, pode ser `null`)

**Regra geral:** use wrappers (`Integer`, `Long`, `Double`) quando o valor puder ser `null`.

## 🗄️ Tabela gerada

```sql
create table tra_trabalho (
    tra_id bigint generated always as identity,
    tra_titulo varchar(100) not null unique,
    tra_data_hora_entrega timestamp not null,
    tra_descricao varchar(200),
    tra_aluno bigint not null,
    tra_nota int,
    tra_justificativa varchar(100),
    primary key (tra_id),
    constraint tra_aln_fk foreign key (tra_aluno) references aln_aluno(aln_id)
);
```

> ⚠️ **Observação:** como a entidade não tem `nullable = false` nem
> `unique = true` no `@Column`, o Hibernate **não aplicaria** essas
> restrições se fosse ele a criar a tabela. Elas existem porque você
> rodou o `01-schema.sql` manualmente.

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **`LocalDateTime`** | Data + hora sem timezone |
| **`@ManyToOne`** | Muitos → 1 (lado "N" guarda a FK) |
| **`Integer` vs `int`** | Wrapper permite `null`; primitivo não |
| **`FetchType.EAGER`** | Carrega o relacionamento junto (JOIN) |
| **`FetchType.LAZY`** | Carrega só quando acessado |

## 🧪 Exercícios para fixar

1. **Se eu trocar `Integer` por `int` no campo `nota`, o que muda?**
   <details>
   <summary>Resposta</summary>
   Um trabalho não corrigido teria `nota = 0` em vez de `null`.
   Isso confundiria "não corrigido" com "nota zero". Sempre use
   wrapper quando o valor puder ser nulo.
   </details>

2. **Se eu trocar `EAGER` por `LAZY` no `@ManyToOne`, o JSON quebra?**
   <details>
   <summary>Resposta</summary>
   Provavelmente sim — daria `LazyInitializationException` na hora de
   serializar. Pra funcionar com `LAZY`, seria preciso `@Transactional`
   no service ou um `JOIN FETCH` na query.
   </details>

3. **Como buscar todos os trabalhos de um aluno específico?**
   <details>
   <summary>Resposta</summary>
   ```java
   // No TrabalhoRepository:
   List<Trabalho> findByAlunoRa(Long ra);
   ```
   O Spring Data JPA gera a query automaticamente a partir do nome
   do método (`findBy` + campo + operação).
   </details>

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`aluno.md`](aluno.md) | Entidade referenciada (N:1) |
| [`trabalho-controller.md`](../controllers/trabalho-controller.md) | Camada HTTP |
| [`trabalho-service.md`](../services/trabalho-service.md) | Regras de negócio |
| [`trabalho-repository.md`](../repositories/trabalho-repository.md) | Acesso ao banco |


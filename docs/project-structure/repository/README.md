# Repositories (Spring Data JPA)

**Resumo:** Interfaces que estendem `JpaRepository` e fornecem acesso ao banco de dados.

> Cada entidade tem seu repository. O Spring Data JPA implementa os métodos
> automaticamente — você só declara a interface.

## 📚 Visão geral

| Repository | Entidade | Complexidade |
|---|---|---|
| [`AlunoRepository`](#alunorepository) | `Aluno` | Simples (só CRUD) |
| [`CursoRepository`](#cursorerepository) | `Curso` | Simples (só CRUD) |
| [`DisciplinaRepository`](#disciplinarepository) | `Disciplina` | Média (queries + JPQL) |
| [`TrabalhoRepository`](#trabalhorepository) | `Trabalho` | Média (query + JPQL) |

## 🎯 O que é um Repository?

É uma **interface** que o Spring Data JPA **implementa automaticamente** em tempo de execução. Você só declara o que precisa, e ele gera o SQL.

```java
public interface CursoRepository extends JpaRepository<Curso, Long> {
}
```

**Anatomia do `JpaRepository<T, ID>`:**

| Parâmetro | Descrição | Exemplo |
|---|---|---|
| `T` | Tipo da entidade | `Curso` |
| `ID` | Tipo da chave primária | `Long` |

## 🎁 Métodos que vêm de graça

Todo `JpaRepository` já tem implementados:

| Método | O que faz |
|---|---|
| `save(entity)` | Insere ou atualiza |
| `findAll()` | Lista todos |
| `findById(id)` | Busca por ID (retorna `Optional`) |
| `deleteById(id)` | Remove por ID |
| `count()` | Conta registros |
| `existsById(id)` | Verifica se existe |
| `deleteAll()` | Remove tudo |

**Isso é o que o Service usa por padrão.** Só criamos queries customizadas quando precisamos de filtros específicos.

---

## `AlunoRepository`

```java
package br.com.topicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.topicos.entity.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
```

**Função:** fornece CRUD básico para a entidade `Aluno`.

**Não tem métodos customizados** — usa apenas o que `JpaRepository` oferece.

**Exemplos de uso:**
```java
alunoRepository.save(aluno);          // insere ou atualiza
alunoRepository.findAll();            // lista todos
alunoRepository.findById(1L);         // retorna Optional<Aluno>
alunoRepository.deleteById(1L);       // remove
```

---

## `CursoRepository`

```java
package br.com.topicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.topicos.entity.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
```

**Função:** fornece CRUD básico para a entidade `Curso`.

**Também não tem métodos customizados.**

---

## `DisciplinaRepository`

```java
package br.com.topicos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import br.com.topicos.entity.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    public Optional<Disciplina> findByCodigo(String codigo);

    @Query("SELECT d FROM Disciplina d WHERE d.codigo = :codigo")
    public Optional<Disciplina> buscarPeloCodigo(String codigo);

    public List<Disciplina> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT d FROM Disciplina d WHERE LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    public List<Disciplina> buscarPeloNome(String nome);

    public List<Disciplina> findByAlunosNomeContainingIgnoreCase(String nomeAluno);

    @Query("SELECT d FROM Disciplina d JOIN d.alunos a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nomeAluno, '%'))")
    public List<Disciplina> buscarPorNomeAluno(String nomeAluno);

    public List<Disciplina> findByCargaHorariaGreaterThanEqualOrNomeContainingIgnoreCase(Integer cargaHoraria, String nome);

    @Query("SELECT d FROM Disciplina d WHERE d.cargaHoraria >= :cargaHoraria OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    public List<Disciplina> buscarPorCargaHorariaMaiorIgualOuNome(Integer cargaHoraria, String nome);
}
```

### Métodos documentados

| Método | Tipo | O que faz |
|---|---|---|
| `findByCodigo(String)` | Derived | Busca disciplina pelo código exato |
| `buscarPeloCodigo(String)` | `@Query` | Mesmo que o acima, mas com JPQL |
| `findByNomeContainingIgnoreCase(String)` | Derived | Busca por parte do nome, sem case-sensitive |
| `buscarPeloNome(String)` | `@Query` | Mesmo que o acima, mas com JPQL |
| `findByAlunosNomeContainingIgnoreCase(String)` | Derived | Busca disciplinas por nome de aluno matriculado |
| `buscarPorNomeAluno(String)` | `@Query` | Mesmo que o acima, mas com JOIN explícito |
| `findByCargaHorariaGreaterThanEqualOrNomeContainingIgnoreCase(Integer, String)` | Derived | Busca por carga ≥ X **ou** nome contém Y |
| `buscarPorCargaHorariaMaiorIgualOuNome(Integer, String)` | `@Query` | Mesmo que o acima, mas com JPQL |

> 💡 **Observação didática:** cada par (derived + `@Query`) faz a **mesma coisa**. Serve pra mostrar as duas formas — e provavelmente o professor vai perguntar a diferença.

### Como o Spring Data monta as queries (derived queries)

O Spring lê o **nome do método** e gera o SQL:

```
findByCodigo
└─┬─┘ └──┬──┘
findBy   campo: codigo
```

```
findByNomeContainingIgnoreCase
└─┬─┘ └─┬─┘ └──┬───┘ └────┬────┘
findBy  nome  Containing  IgnoreCase
```

**Prefixos comuns:**

| Prefixo | SQL gerado |
|---|---|
| `findBy` | `WHERE campo = ?` |
| `Containing` | `WHERE campo LIKE '%?%'` |
| `StartingWith` | `WHERE campo LIKE '?%'` |
| `EndingWith` | `WHERE campo LIKE '%?'` |
| `IgnoreCase` | `WHERE LOWER(campo) = LOWER(?)` |
| `GreaterThan` | `WHERE campo > ?` |
| `LessThan` | `WHERE campo < ?` |
| `GreaterThanEqual` | `WHERE campo >= ?` |
| `Between` | `WHERE campo BETWEEN ? AND ?` |
| `And` / `Or` | Combina condições |
| `OrderBy...Asc/Desc` | Ordenação |

### JPQL — a linguagem das `@Query`

**JPQL** = *Java Persistence Query Language*. É **parecido** com SQL, mas opera em **entidades**, não em tabelas.

| SQL | JPQL |
|---|---|
| `SELECT * FROM dis_disciplina` | `SELECT d FROM Disciplina d` |
| `FROM dis_disciplina` | `FROM Disciplina` |
| `WHERE dis_codigo = ?` | `WHERE d.codigo = :codigo` |
| Nome da tabela | Nome da **entidade** |
| Nome da coluna | Nome do **atributo** |

**Exemplo destrinchado:**
```java
@Query("SELECT d FROM Disciplina d WHERE d.codigo = :codigo")
```

| Parte | Significado |
|---|---|
| `SELECT d` | Selecione o objeto `Disciplina` (não colunas) |
| `FROM Disciplina d` | Da **entidade** `Disciplina`, apelidada de `d` |
| `WHERE d.codigo = :codigo` | Onde o **atributo** `codigo` = parâmetro `:codigo` |

**Sobre o `JOIN`:**
```java
@Query("SELECT d FROM Disciplina d JOIN d.alunos a WHERE LOWER(a.nome) LIKE ...")
```
- `JOIN d.alunos a` → faz join usando o **atributo Java** (`alunos`), não a tabela
- `a.nome` → atributo da entidade `Aluno`
- **Nada de `mat_matricula` no JPQL** — o Hibernate sabe a tabela intermediária

---

## `TrabalhoRepository`

```java
package br.com.topicos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import br.com.topicos.entity.Trabalho;

public interface TrabalhoRepository extends JpaRepository<Trabalho, Long> {

    public List<Trabalho> findByTituloContainingIgnoreCaseAndAlunoRa(String titulo, Long ra);

    @Query("SELECT t FROM Trabalho t JOIN t.aluno a WHERE LOWER(t.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) AND a.ra = :ra")
    public List<Trabalho> buscarPorRaAlunoETitulo(Long ra, String titulo);
}
```

### Métodos documentados

| Método | Tipo | O que faz |
|---|---|---|
| `findByTituloContainingIgnoreCaseAndAlunoRa(String, Long)` | Derived | Busca por título (parcial, case-insensitive) **E** RA do aluno |
| `buscarPorRaAlunoETitulo(Long, String)` | `@Query` | Mesma coisa, mas com JPQL + JOIN |

**Anatomia da derived query:**
```
findByTituloContainingIgnoreCaseAndAlunoRa
└─┬─┘ └──┬─┘ └───┬───┘ └────┬────┘ └─┬─┘ └─┬─┘ └──┬──┘
findBy  titulo  Containing  IgnoreCase  And  aluno  ra
```

> 💡 **Ponto-chave:** `AlunoRa` → o Spring navega pelo **relacionamento** `aluno`, depois pelo campo `ra`. Isso só funciona porque `Trabalho` tem `@ManyToOne Aluno`.

---

## 📌 Padrão: Derived Query vs `@Query`

| Aspecto | Derived Query | `@Query` (JPQL) |
|---|---|---|
| **Como** | Nome do método | Anotação `@Query` |
| **Vantagem** | Simples, sem código extra | Flexível, permite JOIN complexo |
| **Desvantagem** | Nomes ficam gigantes | Mais verboso, precisa saber JPQL |
| **Exemplo** | `findByCodigo(String)` | `@Query("SELECT d FROM Disciplina d WHERE d.codigo = :codigo")` |
| **Quando usar** | Filtros simples | JOINs, agregações, condições complexas |

**Regra prática:**
- Se dá pra fazer com derived query **e o nome não fica absurdo** → usa derived
- Se precisa de JOIN, `GROUP BY`, ou condição complexa → usa `@Query`

---

## 🎓 Cheat sheet de Derived Queries (pra prova!)

| Palavra-chave no método | Vira isso em SQL |
|---|---|
| `findBy` | `WHERE` |
| `And` | `AND` |
| `Or` | `OR` |
| `Containing` | `LIKE '%...%'` |
| `StartingWith` | `LIKE '...%'` |
| `EndingWith` | `LIKE '%...'` |
| `IgnoreCase` | `LOWER()` em ambos os lados |
| `GreaterThan` | `>` |
| `GreaterThanEqual` | `>=` |
| `LessThan` | `<` |
| `Between` | `BETWEEN` |
| `IsNull` / `IsNotNull` | `IS NULL` / `IS NOT NULL` |
| `OrderBy...Asc` / `Desc` | `ORDER BY ... ASC/DESC` |
| `In` | `IN (...)` |
| `Not` | `<>` |

### Como o Spring "navega" nos relacionamentos

Em `findByAlunoRa`:
1. Pega `Aluno` (relacionamento em `Trabalho`)
2. Pega `ra` (campo de `Aluno`)
3. Vira `WHERE aluno.ra = ?`

**CamelCase importa!** `AlunoRa` = `aluno.ra`. Se fosse `Alunora`, o Spring procuraria um campo chamado `alunora` (não existe).

---

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **`JpaRepository<T, ID>`** | Interface base com CRUD pronto |
| **Derived Query** | Query gerada a partir do nome do método |
| **`@Query`** | Anotação pra escrever JPQL manualmente |
| **JPQL** | Linguagem de query que opera em **entidades** (não tabelas) |
| **`Optional<T>`** | Container que pode ou não ter valor (evita `NullPointerException`) |
| **`:`** | Marca parâmetro nomeado em JPQL (`:codigo`) |

## 🧪 Exercícios

1. **Como eu buscaria um curso pela sigla?**
   <details>
   <summary>Resposta</summary>
   ```java
   Optional<Curso> findBySigla(String sigla);
   ```
   </details>

2. **Como eu buscaria alunos cujo nome começa com "Jo"?**
   <details>
   <summary>Resposta</summary>
   ```java
   List<Aluno> findByNomeStartingWithIgnoreCase(String prefixo);
   ```
   </details>

3. **Se eu quiser disciplinas com carga horária entre 40 e 80?**
   <details>
   <summary>Resposta</summary>
   ```java
   List<Disciplina> findByCargaHorariaBetween(Integer min, Integer max);
   ```
   </details>

4. **Por que `buscarPeloCodigo` usa `:codigo` e não `?1`?**
   <details>
   <summary>Resposta</summary>
   `:nome` é **parâmetro nomeado** (mais legível). `?1` é **posicional**.
   Os dois funcionam, mas nomeado evita confusão quando há vários parâmetros.
   </details>

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`Curso.md`](../entities/Curso.md) | Entidade do `CursoRepository` |
| [`Disciplina.md`](../entities/Disciplina.md) | Entidade do `DisciplinaRepository` |
| [`Trabalho.md`](../entities/Trabalho.md) | Entidade do `TrabalhoRepository` |
| [`Aluno.md`](../entities/Aluno.md) | Entidade do `AlunoRepository` |
| Services | Camada que usa os repositories |

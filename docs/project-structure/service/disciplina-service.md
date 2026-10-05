# Service: Disciplina

**Resumo:** Camada de regras de negócio para a entidade `Disciplina`.

> O service mais completo do projeto — injeta **3 dependências**
> (`DisciplinaRepository`, `CursoService`, `AlunoService`), usa
> `@Transactional` e implementa operação N:N (`matricularAluno`).

## 📄 Código

### Interface `DisciplinaService.java`

```java
package br.com.topicos.service;

import java.util.List;
import br.com.topicos.entity.Disciplina;

public interface DisciplinaService {

    public Disciplina cadastrar(Disciplina disciplina);

    public List<Disciplina> listar();

    public Disciplina buscarPorId(Long id);

    public void matricularAluno(Long disciplinaId, Long alunoId);

}
```

### Implementação `DisciplinaServiceImpl.java`

```java
package br.com.topicos.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Aluno;
import br.com.topicos.entity.Disciplina;
import br.com.topicos.repository.DisciplinaRepository;

@Service
public class DisciplinaServiceImpl implements DisciplinaService {

    private final DisciplinaRepository repo;
    private final CursoService cursoService;
    private final AlunoService alunoService;

    public DisciplinaServiceImpl(DisciplinaRepository repo, CursoService cursoService, AlunoService alunoService) {
        this.repo = repo;
        this.cursoService = cursoService;
        this.alunoService = alunoService;
    }

    @Override
    @Transactional
    public Disciplina cadastrar(Disciplina disciplina) {
        if (disciplina == null ||
                disciplina.getId() != null ||
                disciplina.getNome() == null ||
                disciplina.getNome().isBlank() ||
                disciplina.getCodigo() == null ||
                disciplina.getCodigo().isBlank() ||
                disciplina.getCurso() == null ||
                disciplina.getCurso().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da disciplina inválidos");
        }
        disciplina.setCurso(cursoService.buscarPorId(disciplina.getCurso().getId()));
        if (disciplina.getAlunos() != null) {
            disciplina.getAlunos().forEach(aluno -> {
                alunoService.buscarPorId(aluno.getId());
            });
        }
        try {
            disciplina = repo.save(disciplina);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao cadastrar disciplina: " + e.getMessage(), e);
        }
        return disciplina;
    }

    @Override
    public List<Disciplina> listar() {
        return repo.findAll();
    }

    @Override
    public Disciplina buscarPorId(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id da disciplina não pode ser nulo");
        }
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));
    }

    @Override
    public void matricularAluno(Long disciplinaId, Long alunoId) {
        Disciplina disciplina = buscarPorId(disciplinaId);
        if (disciplina.getAlunos() == null) {
            disciplina.setAlunos(new HashSet<Aluno>());
        }
        disciplina.getAlunos().add(alunoService.buscarPorId(alunoId));
        repo.save(disciplina);
    }

}
```

> **Arquivos fonte:**
> [`DisciplinaService.java`](../../../src/main/java/br/com/topicos/service/DisciplinaService.java) ·
> [`DisciplinaServiceImpl.java`](../../../src/main/java/br/com/topicos/service/DisciplinaServiceImpl.java)

## 🎯 Objetivo

Aplicar as **regras de negócio** relacionadas a disciplinas, incluindo:
- Validação dos dados de entrada
- **Validação de FK** (curso e alunos precisam existir)
- Operação de **matrícula N:N** entre disciplina e aluno
- Uso de `@Transactional` para operações atômicas

## 🧩 Injeção de múltiplas dependências

```java
private final DisciplinaRepository repo;
private final CursoService cursoService;
private final AlunoService alunoService;

public DisciplinaServiceImpl(DisciplinaRepository repo, CursoService cursoService, AlunoService alunoService) {
    this.repo = repo;
    this.cursoService = cursoService;
    this.alunoService = alunoService;
}
```

Esse service injeta **3 dependências**:

| Dependência | Para quê |
|---|---|
| `DisciplinaRepository` | Acesso ao banco (CRUD de disciplinas) |
| `CursoService` | Validar se o curso existe |
| `AlunoService` | Validar se os alunos existem |

> 💡 **Injeção em cascata:** services podem injetar outros services.
> Isso permite **reutilizar** a lógica de validação já existente —
> em vez de reimplementar, você chama `cursoService.buscarPorId()`,
> que já lança 404 se não existir.

## 📌 Métodos

### `cadastrar(Disciplina disciplina)` — `@Transactional`

**O que faz:** valida, resolve o curso, valida os alunos e salva.

**Etapas:**

```java
// 1. Valida campos obrigatórios
if (disciplina == null || ... || disciplina.getCurso().getId() == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da disciplina inválidos");
}

// 2. Resolve o curso pelo ID (lança 404 se não existir)
disciplina.setCurso(cursoService.buscarPorId(disciplina.getCurso().getId()));

// 3. Valida cada aluno (lança 404 se algum não existir)
if (disciplina.getAlunos() != null) {
    disciplina.getAlunos().forEach(aluno -> {
        alunoService.buscarPorId(aluno.getId());
    });
}

// 4. Salva (com try/catch pra erros de banco)
try {
    disciplina = repo.save(disciplina);
} catch (Exception e) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao cadastrar disciplina: " + e.getMessage(), e);
}
```

**Por que isso importa:**

O JSON que chega no POST tem só os **IDs** do curso e dos alunos:
```json
{
    "nome": "Estrutura de Dados",
    "codigo": "ED001",
    "cargaHoraria": 80,
    "curso": { "id": 1 },
    "alunos": [ { "id": 1 }, { "id": 2 } ]
}
```

Antes de salvar, o service **troca os IDs pelos objetos completos**, chamando `buscarPorId()`:
- Se o curso com ID 1 não existir → **404** (o `cursoService.buscarPorId` lança)
- Se algum aluno não existir → **404** (o `alunoService.buscarPorId` lança)

> 💡 **Isso valida a FK antes de tentar inserir.** Sem isso, o banco
> lançaria erro de constraint, e a mensagem seria feia.

### `@Transactional` — por que usar?

```java
@Override
@Transactional
public Disciplina cadastrar(Disciplina disciplina) { ... }
```

**O que faz:** envolve o método numa **transação**. Se **qualquer coisa** falhar (ex: o `save`), **tudo é desfeito** (rollback).

**Sem `@Transactional`:**
- Cada `repo.save()` é uma transação separada
- Se o segundo falhar, o primeiro já foi commitado → inconsistência

**Com `@Transactional`:**
- Tudo é uma transação só
- Se qualquer passo falhar, **nada** é commitado

**Quando usar:** métodos que **modificam várias coisas** ou **leem + escrevem** na mesma operação.

### `listar()`

```java
return repo.findAll();
```

Simples: retorna todos os disciplinas.

### `buscarPorId(Long id)`

```java
if (id == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id da disciplina não pode ser nulo");
}
return repo.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));
```

Mesma lógica dos outros services.

### `matricularAluno(Long disciplinaId, Long alunoId)`

```java
Disciplina disciplina = buscarPorId(disciplinaId);
if (disciplina.getAlunos() == null) {
    disciplina.setAlunos(new HashSet<Aluno>());
}
disciplina.getAlunos().add(alunoService.buscarPorId(alunoId));
repo.save(disciplina);
```

**O que faz:** adiciona um aluno à coleção de alunos da disciplina (operação N:N).

**Passo a passo:**

| Etapa | O que faz |
|---|---|
| `buscarPorId(disciplinaId)` | Busca a disciplina (404 se não existir) |
| `if (getAlunos() == null)` | Inicializa o `Set` se ainda estiver nulo |
| `alunoService.buscarPorId(alunoId)` | Busca o aluno (404 se não existir) |
| `.getAlunos().add(...)` | Adiciona o aluno ao Set |
| `repo.save(disciplina)` | Persiste a atualização |

> 💡 **Por que `HashSet`?** Porque o atributo `alunos` é `Set<Aluno>` (não permite duplicatas).
> Se você tentar matricular o mesmo aluno 2x, o `Set` ignora.

> 💡 **Por que salvar a disciplina?** Porque o Hibernate gerencia a tabela
> intermediária `mat_matricula` a partir da coleção `alunos`. Ao salvar
> a disciplina, ele insere o registro em `mat_matricula` automaticamente.

## 🎯 `ResponseStatusException` — quando usar cada status

| Status | Quando | Exemplo |
|---|---|---|
| **400 Bad Request** | Dados inválidos (validação) | Nome vazio, curso nulo |
| **404 Not Found** | Recurso referenciado não existe | Curso ID 999 não encontrado |
| **500 Internal Server Error** | Erro inesperado | (não usado aqui) |

## 🏗️ Fluxo completo do `cadastrar`

```text
Cliente
   │ POST /disciplina
   │ { "nome": "...", "curso": { "id": 1 }, "alunos": [{ "id": 1 }] }
   ▼
DisciplinaController
   │ service.cadastrar(disciplina)
   ▼
DisciplinaServiceImpl
   │
   │ 1. Valida campos → 400 se faltar algo
   │ 2. cursoService.buscarPorId(1) → 404 se não existir
   │ 3. alunoService.buscarPorId(1) → 404 se não existir
   │ 4. repo.save(disciplina)
   ▼
DisciplinaRepository → SQL INSERT
   ▼
PostgreSQL
```

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **Injeção múltipla** | Um service pode depender de vários repositories/services |
| **`@Transactional`** | Envolve o método numa transação (tudo ou nada) |
| **Reutilização via service** | `cursoService.buscarPorId()` reutiliza validação existente |
| **Resolução de FK** | Trocar ID por objeto antes de salvar |
| **`Set` vs `List`** | `Set` não permite duplicatas (usado em N:N) |
| **`HashSet`** | Implementação concreta de `Set` |

## 🧪 Exercícios

1. **Por que o `cadastrar` precisa do `@Transactional`?**
   <details>
   <summary>Resposta</summary>
   Porque faz **várias operações**: valida curso, valida alunos, salva disciplina
   (que por sua vez insere em `mat_matricula`). Se algo falhar no meio, tudo
   precisa ser desfeito. `@Transactional` garante isso.
   </details>

2. **O que acontece se eu mandar `{ "curso": { "id": 999 } }`?**
   <details>
   <summary>Resposta</summary>
   O `cursoService.buscarPorId(999)` lança **404 Not Found** com
   `"Curso não encontrado"`. A disciplina não é salva.
   </details>

3. **Por que `matricularAluno` inicializa o Set se estiver null?**
   <details>
   <summary>Resposta</summary>
   Porque uma disciplina recém-criada pode não ter alunos ainda — o campo
   `alunos` vem `null` do banco. Sem inicializar, o `.add()` daria
   `NullPointerException`.
   </details>

4. **Se eu tentar matricular o mesmo aluno 2x, o que acontece?**
   <details>
   <summary>Resposta</summary>
   O `Set` ignora a segunda adição (não permite duplicatas). A tabela
   `mat_matricula` não cria um segundo registro — a PK composta
   (`mat_aln_id`, `mat_dis_id`) também impediria.
   </details>

## ⚠️ Observações sobre o código

### Pontos fortes ✅
- `@Transactional` no método que modifica várias tabelas
- Validação de FK via `buscarPorId` dos outros services
- Tratamento de `try/catch` no `repo.save` (captura erros de constraint)
- Reutilização de lógica (não reimplementa `buscarPorId`)

### Pontos de atenção ⚠️
- A validação de alunos só **verifica** se existem, mas não **atualiza** a referência. No JSON final, os alunos voltam com os dados que vieram da requisição — não com os do banco.
  ```java
  // Isso valida mas não substitui:
  disciplina.getAlunos().forEach(aluno -> {
      alunoService.buscarPorId(aluno.getId());  // ← retorno ignorado!
  });
  ```
  O correto seria:
  ```java
  Set<Aluno> alunosCompletos = new HashSet<>();
  for (Aluno aluno : disciplina.getAlunos()) {
      alunosCompletos.add(alunoService.buscarPorId(aluno.getId()));
  }
  disciplina.setAlunos(alunosCompletos);
  ```

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`disciplina.md`](../entities/Disciplina.md) | Entidade manipulada |
| [`disciplina-repository.md`](../repositories/README.md) | Repository usado |
| [`curso-service.md`](curso-service.md) | Service injetado (validação de curso) |
| [`aluno-service.md`](aluno-service.md) | Service injetado (validação de aluno) |

# Service: Trabalho

**Resumo:** Camada de regras de negócio para a entidade `Trabalho`.

> Injeta `TrabalhoRepository` e `AlunoService`. Possui uma regra
> interessante: se `dataHoraEntrega` não for informada, usa `LocalDateTime.now()`.

## 📄 Código

### Interface `TrabalhoService.java`

```java
package br.com.topicos.service;

import java.util.List;
import br.com.topicos.entity.Trabalho;

public interface TrabalhoService {

    public List<Trabalho> listarTodos();

    public Trabalho cadastrar(Trabalho trabalho);

    public List<Trabalho> buscarPorRaAlunoETitulo(Long ra, String titulo);

}
```

### Implementação `TrabalhoServiceImpl.java`

```java
package br.com.topicos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Trabalho;
import br.com.topicos.repository.TrabalhoRepository;

@Service
public class TrabalhoServiceImpl implements TrabalhoService {

    private final TrabalhoRepository repo;
    private final AlunoService alunoService;

    public TrabalhoServiceImpl(TrabalhoRepository repo, AlunoService alunoService) {
        this.repo = repo;
        this.alunoService = alunoService;
    }

    @Override
    public List<Trabalho> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Trabalho cadastrar(Trabalho trabalho) {
        if (trabalho == null ||
                trabalho.getId() != null ||
                trabalho.getTitulo() == null ||
                trabalho.getTitulo().isBlank() ||
                trabalho.getAluno() == null ||
                trabalho.getAluno().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos!");
        }
        if (trabalho.getDataHoraEntrega() == null) {
            trabalho.setDataHoraEntrega(LocalDateTime.now());
        }
        trabalho.setAluno(alunoService.buscarPorId(trabalho.getAluno().getId()));
        return repo.save(trabalho);
    }

    @Override
    public List<Trabalho> buscarPorRaAlunoETitulo(Long ra, String titulo) {
        if (ra == null || ra <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RA inválido!");
        }
        return repo.buscarPorRaAlunoETitulo(ra, titulo);
    }

}
```

> **Arquivos fonte:**
> [`TrabalhoService.java`](../../../src/main/java/br/com/topicos/service/TrabalhoService.java) ·
> [`TrabalhoServiceImpl.java`](../../../src/main/java/br/com/topicos/service/TrabalhoServiceImpl.java)

## 🎯 Objetivo

Aplicar as **regras de negócio** relacionadas a trabalhos:
- Validar dados de entrada
- **Preencher `dataHoraEntrega` automaticamente** se não for informada
- Resolver a FK do aluno antes de salvar
- Filtrar trabalhos por RA do aluno e título

## 🧩 Injeção de dependências

```java
private final TrabalhoRepository repo;
private final AlunoService alunoService;

public TrabalhoServiceImpl(TrabalhoRepository repo, AlunoService alunoService) {
    this.repo = repo;
    this.alunoService = alunoService;
}
```

| Dependência | Para quê |
|---|---|
| `TrabalhoRepository` | CRUD de trabalhos |
| `AlunoService` | Validar se o aluno existe (via `buscarPorId`) |

## 📌 Métodos

### `listarTodos()`

```java
return repo.findAll();
```

Simples: retorna todos os trabalhos.

> ⚠️ **Nome diferente:** esse método se chama `listarTodos()`, enquanto
> nos outros services é `listar()`. Apenas uma convenção adotada — o importante
> é o Controller e o Service usarem o mesmo nome.

### `cadastrar(Trabalho trabalho)`

**O que faz:** valida, preenche data padrão, resolve aluno e salva.

**Etapas:**

```java
// 1. Valida campos obrigatórios
if (trabalho == null ||
        trabalho.getId() != null ||
        trabalho.getTitulo() == null ||
        trabalho.getTitulo().isBlank() ||
        trabalho.getAluno() == null ||
        trabalho.getAluno().getId() == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos!");
}
```

**Validações aplicadas:**

| Verificação | Motivo |
|---|---|
| `trabalho == null` | Nada enviado |
| `trabalho.getId() != null` | Não pode cadastrar com ID (banco gera) |
| `titulo == null \|\| isBlank()` | Título obrigatório |
| `aluno == null \|\| aluno.getId() == null` | Trabalho precisa de aluno |

> 💡 **Observação:** a validação é mais enxuta que a de `Disciplina`.
> Não valida `nota`, `descricao`, `justificativa` — porque são **opcionais**.

```java
// 2. Preenche data automaticamente se não vier
if (trabalho.getDataHoraEntrega() == null) {
    trabalho.setDataHoraEntrega(LocalDateTime.now());
}
```

**⭐ Regra especial deste service:** se o cliente **não enviar** `dataHoraEntrega`, o service preenche com a **data/hora atual**. Isso é útil porque:
- O cliente pode querer só registrar "entreguei agora"
- Evita validação desnecessária no frontend

```java
// 3. Resolve o aluno pelo ID (lança 404 se não existir)
trabalho.setAluno(alunoService.buscarPorId(trabalho.getAluno().getId()));
```

Substitui o objeto `{ "id": 1 }` por um `Aluno` completo.

```java
// 4. Salva
return repo.save(trabalho);
```

### `buscarPorRaAlunoETitulo(Long ra, String titulo)`

```java
if (ra == null || ra <= 0) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RA inválido!");
}
return repo.buscarPorRaAlunoETitulo(ra, titulo);
```

**O que faz:**
1. Valida o `ra` (não nulo e positivo) → 400 se inválido
2. Delega ao repository a query customizada

**Sobre o `titulo`:** não é validado porque pode ser `null` — nesse caso, a query filtra só pelo RA (depende do comportamento do `LIKE` no banco).

> 💡 **Vem do `TrabalhoRepository`:**
> ```java
> @Query("SELECT t FROM Trabalho t JOIN t.aluno a WHERE LOWER(t.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) AND a.ra = :ra")
> List<Trabalho> buscarPorRaAlunoETitulo(Long ra, String titulo);
> ```

## 🎯 Regras especiais deste service

### 1. Preenchimento automático de `dataHoraEntrega`

```java
if (trabalho.getDataHoraEntrega() == null) {
    trabalho.setDataHoraEntrega(LocalDateTime.now());
}
```

**Comportamento:**

| Requisição | Resultado |
|---|---|
| `{ "titulo": "...", "aluno": {"id": 1} }` | Data = **agora** (preenchida pelo service) |
| `{ "titulo": "...", "aluno": {"id": 1}, "dataHoraEntrega": "2026-10-05T10:00:00" }` | Data = **a enviada** |

**Por que isso é útil:** o cliente pode só querer registrar "entreguei agora".

### 2. Validação do RA

```java
if (ra == null || ra <= 0) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RA inválido!");
}
```

Diferente dos outros services, que validam `id`, esse valida **`ra`** — porque é o que a busca precisa.

## 🏗️ Fluxo completo do `cadastrar`

```text
Cliente
   │ POST /trabalho
   │ { "titulo": "Teste 1", "aluno": { "id": 1 } }
   ▼
TrabalhoController
   │ service.cadastrar(trabalho)
   ▼
TrabalhoServiceImpl
   │
   │ 1. Valida campos → 400 se faltar
   │ 2. Preenche data se estiver null
   │ 3. alunoService.buscarPorId(1) → 404 se não existir
   │ 4. repo.save(trabalho)
   ▼
TrabalhoRepository → SQL INSERT
   ▼
PostgreSQL
```

## 📊 Status HTTP lançados

| Status | Quando |
|---|---|
| **400 BAD_REQUEST** | Dados inválidos ou RA inválido |
| **404 NOT_FOUND** | Aluno não existe (via `alunoService`) |
| **200 OK** | Listagem ou busca bem-sucedida |
| **201 Created** | Cadastro (definido pelo Controller) |

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **Default value no Service** | Preencher campo automaticamente se vier `null` |
| **`LocalDateTime.now()`** | Retorna data/hora atual do servidor |
| **Validação de RA** | Validar o campo que a operação realmente precisa |
| **Injeção cruzada** | Service injeta outro service pra reutilizar validação |
| **Validação enxuta** | Só validar o que é obrigatório (campos opcionais ficam de fora) |

## 🧪 Exercícios

1. **Se eu não enviar `dataHoraEntrega` no POST, o que acontece?**
   <details>
   <summary>Resposta</summary>
   O service preenche com `LocalDateTime.now()` (data/hora atual do servidor)
   antes de salvar. O trabalho é cadastrado com a data atual.
   </details>

2. **Por que `titulo` não é validado em `buscarPorRaAlunoETitulo`?**
   <details>
   <summary>Resposta</summary>
   Porque pode ser `null` — nesse caso a query filtra só pelo RA.
   Como é um parâmetro opcional, não faz sentido lançar 400.
   </details>

3. **Qual a diferença entre `listarTodos()` e os outros services que usam `listar()`?**
   <details>
   <summary>Resposta</summary>
   Só o nome — o comportamento é idêntico (`repo.findAll()`). A escolha é
   de estilo; o importante é o Controller chamar o mesmo nome.
   </details>

4. **Por que o `cadastrar` não tem `@Transactional` como o de `Disciplina`?**
   <details>
   <summary>Resposta</summary>
   Porque faz **uma única operação de escrita** (`repo.save`). Não há
   risco de inconsistência parcial. `@Transactional` é mais necessário
   em métodos que escrevem em **múltiplas tabelas**.
   </details>

## ⚠️ Comparação com outros services

| Service | `@Transactional`? | Regra especial |
|---|---|---|
| `AlunoService` | ❌ | Nenhuma |
| `CursoService` | ❌ | Nenhuma |
| `DisciplinaService` | ✅ | Valida curso + alunos, matrícula N:N |
| `TrabalhoService` | ❌ | Preenche `dataHoraEntrega` automaticamente |

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`trabalho.md`](../entities/trabalho.md) | Entidade manipulada |
| [`trabalho-repository.md`](../repositories/README.md) | Repository com a query customizada |
| [`aluno-service.md`](alunoS-service.md) | Service injetado (validação de aluno) |
| [`trabalho-cntroller.md`](../controllers/trabalho-controller.md) | Camada HTTP |

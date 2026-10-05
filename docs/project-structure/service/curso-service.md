# Service: Curso

**Resumo:** Camada de regras de negócio para a entidade `Curso`.

> Segue o padrão **Interface + Implementação** (`CursoService` +
> `CursoServiceImpl`). Valida os dados de entrada e delega ao
> `CursoRepository` o acesso ao banco.

## 📄 Código

### Interface `CursoService.java`

```java
package br.com.topicos.service;

import java.util.List;
import br.com.topicos.entity.Curso;

public interface CursoService {

    public Curso cadastrar(Curso curso);

    public List<Curso> listar();

    public Curso buscarPorId(Long id);

}
```

### Implementação `CursoServiceImpl.java`

```java
package br.com.topicos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Curso;
import br.com.topicos.repository.CursoRepository;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository repo;

    public CursoServiceImpl(CursoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Curso cadastrar(Curso curso) {
        if (curso == null ||
                curso.getId() != null ||
                curso.getNome() == null ||
                curso.getNome().isBlank() ||
                curso.getSigla() == null ||
                curso.getSigla().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do curso inválidos");
        }
        return repo.save(curso);
    }

    @Override
    public List<Curso> listar() {
        return repo.findAll();
    }

    @Override
    public Curso buscarPorId(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id do curso não pode ser nulo");
        }
        Optional<Curso> cursoOp = repo.findById(id);
        if (cursoOp.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado");
        }
        return cursoOp.get();
    }

}
```

> **Arquivos fonte:**
> [`CursoService.java`](../../../src/main/java/br/com/topicos/service/CursoService.java) ·
> [`CursoServiceImpl.java`](../../../src/main/java/br/com/topicos/service/CursoServiceImpl.java)

## 🎯 Objetivo

Aplicar as **regras de negócio** relacionadas a cursos:
- **Validar** os dados antes de salvar
- **Delegar** as operações ao `CursoRepository`
- **Lançar exceções HTTP** quando algo estiver inválido

> ⚠️ O Service **não lida com HTTP diretamente** — ele só lança
> `ResponseStatusException`, e o Spring traduz pra uma resposta HTTP.

## 🧩 Padrão Interface + Implementação

```
┌─────────────────────┐
│   CursoService      │  ← INTERFACE (contrato)
│  + cadastrar()      │
│  + listar()         │
│  + buscarPorId()    │
└──────────┬──────────┘
           │ implements
           ▼
┌─────────────────────┐
│  CursoServiceImpl   │  ← CLASSE (@Service)
│  + cadastrar()      │     Contém a lógica de verdade
│  + listar()         │
│  + buscarPorId()    │
└─────────────────────┘
```

**Por que existe:**
- Desacoplamento (Controller depende da interface, não da implementação)
- Facilidade de testes (mock da interface)
- Clareza (contrato separado da lógica)

> 💡 **Só a impl tem `@Service`.** A interface não tem anotação do Spring.

## 🔗 Injeção do Repository

```java
private final CursoRepository repo;

public CursoServiceImpl(CursoRepository repo) {
    this.repo = repo;
}
```

Injeção **via construtor**:
- O campo é `final` (imutável)
- O Spring injeta automaticamente
- Facilita testes unitários

## 📌 Métodos

### `cadastrar(Curso curso)`

**O que faz:** valida e salva um novo curso.

**Validações:**
| Verificação | Motivo |
|---|---|
| `curso == null` | Nada foi enviado |
| `curso.getId() != null` | Não pode cadastrar com ID já definido (banco gera) |
| `curso.getNome() == null \|\| isBlank()` | Nome obrigatório |
| `curso.getSigla() == null \|\| isBlank()` | Sigla obrigatória |

**Se algo falhar:**
```java
throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do curso inválidos");
```
→ HTTP **400 Bad Request**

**Se tudo OK:**
```java
return repo.save(curso);
```
→ `INSERT` no banco + retorna o curso com ID preenchido.

### `listar()`

```java
return repo.findAll();
```

**O que faz:** retorna todos os cursos do banco. Sem validação.

### `buscarPorId(Long id)`

```java
if (id == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id do curso não pode ser nulo");
}
Optional<Curso> cursoOp = repo.findById(id);
if (cursoOp.isEmpty()) {
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado");
}
return cursoOp.get();
```

**Fluxo:**
1. `id == null` → **400 Bad Request**
2. `repo.findById(id)` → retorna `Optional<Curso>`
3. `cursoOp.isEmpty()` → **404 Not Found**
4. Se encontrou, retorna o curso

**Anatomia:**
```java
Optional<Curso> cursoOp = repo.findById(id);
cursoOp.isEmpty()        // true se não encontrou
cursoOp.get()            // devolve o valor (só use se isEmpty() == false)
```

> 💡 **Alternativa mais idiomática** (como no `AlunoServiceImpl`):
> ```java
> return repo.findById(id)
>     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
> ```
> As duas formas funcionam. A com `orElseThrow` é mais concisa.

## 📊 Status HTTP lançados

| Status | Quando |
|---|---|
| **400 BAD_REQUEST** | Dados inválidos (null, vazio, ID já definido) |
| **404 NOT_FOUND** | Curso não existe |
| **200 OK** | Retorno de `listar()` / `buscarPorId()` bem-sucedidos |
| **201 Created** | Retorno do `cadastrar()` (definido pelo Controller) |

## 🏗️ Fluxo completo

```text
Cliente
   │ POST /curso
   ▼
CursoController
   │ service.cadastrar(curso)
   ▼
CursoServiceImpl   ← esta camada
   │ 1. valida os dados
   │    └─ se inválido → 400
   │ 2. repo.save(curso)
   ▼
CursoRepository
   │ SQL INSERT
   ▼
PostgreSQL
```

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **`@Service`** | Marca a classe como bean do Spring (só na impl) |
| **`@Override`** | Sobrescreve método da interface |
| **Injeção via construtor** | Recebe dependências no construtor |
| **`ResponseStatusException`** | Exceção que mapeia direto pra status HTTP |
| **`Optional<T>`** | Container que pode ou não ter valor |
| **`isEmpty()`** | Verifica se o Optional está vazio |
| **`orElseThrow()`** | Se vazio, lança exceção (forma idiomática) |

## 🧪 Exercícios

1. **Por que o Service não usa `@ControllerAdvice`?**
   <details>
   <summary>Resposta</summary>
   Porque `ResponseStatusException` já é tratada automaticamente pelo Spring.
   O `@ControllerAdvice` seria útil pra **centralizar** o tratamento de
   várias exceções diferentes — não é necessário aqui.
   </details>

2. **Se eu chamar `buscarPorId(999L)` num curso que não existe, o que volta?**
   <details>
   <summary>Resposta</summary>
   HTTP **404 Not Found** com mensagem `"Curso não encontrado"`.
   </details>

3. **Por que o campo `repo` é `final`?**
   <details>
   <summary>Resposta</summary>
   Porque é injetado **uma vez** no construtor e nunca muda.
   `final` garante imutabilidade e deixa claro que não deve ser reatribuído.
   </details>

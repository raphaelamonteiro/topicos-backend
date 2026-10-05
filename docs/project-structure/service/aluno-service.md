# Services (Spring)

**Resumo:** Camada de regras de negócio da aplicação.

> Os Services são responsáveis por validar dados, orquestrar chamadas ao
> Repository e lançar exceções HTTP quando necessário.

## 📚 Visão geral

| Service | Interface | Implementação | Entidade |
|---|---|---|---|
| `AlunoService` | ✅ | `AlunoServiceImpl` | `Aluno` |
| `CursoService` | ? | ? | `Curso` |
| `DisciplinaService` | ? | ? | `Disciplina` |
| `TrabalhoService` | ? | ? | `Trabalho` |

## 🎯 Por que Interface + Implementação?

Este projeto usa o padrão **Service + ServiceImpl**, muito comum em Spring:

```
┌─────────────────────┐
│   AlunoService      │  ← INTERFACE (contrato)
│  + cadastrar()      │     Define O QUE pode ser feito
│  + listar()         │
│  + buscarPorId()    │
└──────────┬──────────┘
           │ implements
           ▼
┌─────────────────────┐
│  AlunoServiceImpl   │  ← CLASSE (@Service)
│  + cadastrar()      │     Define COMO é feito
│  + listar()         │
│  + buscarPorId()    │
└─────────────────────┘
```

### Vantagens

| Vantagem | Explicação |
|---|---|
| **Desacoplamento** | O Controller depende da interface, não da classe |
| **Testabilidade** | Fácil criar mocks nos testes |
| **Flexibilidade** | Permite múltiplas implementações |
| **Clareza** | Contrato separado da lógica |

> 💡 **Não é obrigatório.** Muitos projetos usam só a classe com `@Service` (sem interface). Este projeto escolheu separar.

## 🧩 Como o Spring injeta

Quando alguém pede `AlunoService` (a interface):

```java
private final AlunoService service;  // ← pede a interface
```

O Spring procura uma classe `@Service` que **implemente** essa interface:

```java
@Service                             // ← Spring encontra
public class AlunoServiceImpl implements AlunoService { ... }
```

E injeta automaticamente. **Por isso só `AlunoServiceImpl` tem `@Service`** — a interface não tem anotação.

## 📦 Estrutura de um Service

### Interface (`AlunoService.java`)

```java
package br.com.topicos.service;

import java.util.List;
import br.com.topicos.entity.Aluno;

public interface AlunoService {
    Aluno cadastrar(Aluno aluno);
    List<Aluno> listar();
    Aluno buscarPorId(Long id);
}
```

**Papel:** só declara os métodos. **Sem lógica**.

### Implementação (`AlunoServiceImpl.java`)

```java
package br.com.topicos.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import br.com.topicos.entity.Aluno;
import br.com.topicos.repository.AlunoRepository;

@Service
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository repo;

    public AlunoServiceImpl(AlunoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Aluno cadastrar(Aluno aluno) {
        if (aluno == null ||
                aluno.getId() != null ||
                aluno.getNome() == null ||
                aluno.getNome().isBlank() ||
                aluno.getRa() == null ||
                aluno.getRa() <= 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do aluno inválidos");
        }
        return repo.save(aluno);
    }

    @Override
    public List<Aluno> listar() {
        return repo.findAll();
    }

    @Override
    public Aluno buscarPorId(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id do aluno não pode ser nulo");
        }
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }
}
```

## 🔍 Explicação método por método

### `cadastrar(Aluno aluno)`

```java
if (aluno == null || aluno.getId() != null || ...) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do aluno inválidos");
}
return repo.save(aluno);
```

**O que faz:**
1. **Valida** os dados de entrada
2. Se algo estiver errado, lança `400 Bad Request`
3. Se estiver tudo OK, salva no banco via `repo.save()`

**Validações aplicadas:**

| Verificação | Motivo |
|---|---|
| `aluno == null` | Nada foi enviado |
| `aluno.getId() != null` | Não pode cadastrar com ID já definido (o banco gera) |
| `aluno.getNome() == null \|\| isBlank()` | Nome é obrigatório |
| `aluno.getRa() == null \|\| <= 0` | RA precisa ser positivo |

### `listar()`

```java
return repo.findAll();
```

**O que faz:** retorna todos os alunos. Delega direto ao repository.

**Sem validação** — não há o que validar.

### `buscarPorId(Long id)`

```java
if (id == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id do aluno não pode ser nulo");
}
return repo.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
```

**O que faz:**
1. Valida se `id` não é nulo → `400 Bad Request`
2. Busca no banco
3. Se não encontrar, lança `404 Not Found`

**Anatomia do `orElseThrow`:**
```java
repo.findById(id)              // retorna Optional<Aluno>
    .orElseThrow(() -> ...)    // se vazio, lança exceção
```

## 🎯 Tratamento de erros com `ResponseStatusException`

`ResponseStatusException` é uma exceção do Spring que **mapeia direto para um status HTTP**.

```java
throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mensagem");
                                  └──────┬──────┘  └────┬────┘
                                    status HTTP      mensagem
```

**Status HTTP mais usados:**

| Status | Quando usar |
|---|---|
| `400 BAD_REQUEST` | Dados inválidos (falha de validação) |
| `401 UNAUTHORIZED` | Falta autenticação |
| `403 FORBIDDEN` | Autenticado mas sem permissão |
| `404 NOT_FOUND` | Recurso não existe |
| `409 CONFLICT` | Conflito (ex: email duplicado) |
| `500 INTERNAL_SERVER_ERROR` | Erro inesperado |

**Exemplo de resposta que o cliente recebe:**
```json
{
    "timestamp": "2026-10-05T14:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Dados do aluno inválidos",
    "path": "/aluno"
}
```

## 🏗️ Fluxo de uma requisição

```text
Cliente
   │ HTTP
   ▼
Controller
   │ chama service.cadastrar(aluno)
   ▼
AlunoServiceImpl   ← esta camada
   │ valida os dados
   │ (lança 400 se inválido)
   │ chama repo.save(aluno)
   ▼
AlunoRepository
   │ SQL INSERT
   ▼
PostgreSQL
```

## 📌 Conceitos para memorizar (prova)

| Conceito | Definição |
|---|---|
| **Padrão Service + ServiceImpl** | Interface (contrato) + classe (implementação) |
| **`@Service`** | Marca a classe como bean do Spring (só na impl, não na interface) |
| **`@Override`** | Indica que o método sobrescreve o da interface |
| **Injeção via construtor** | Recebe dependências no construtor (boa prática) |
| **`ResponseStatusException`** | Exceção que mapeia pra status HTTP |
| **`HttpStatus.BAD_REQUEST`** | Status 400 — dados inválidos |
| **`HttpStatus.NOT_FOUND`** | Status 404 — recurso não existe |
| **`Optional.orElseThrow()`** | Se vazio, lança exceção |
| **`repo.save()`** | Insere ou atualiza no banco |

## 🧪 Exercícios

1. **Por que `AlunoService` não tem `@Service`?**
   <details>
   <summary>Resposta</summary>
   Porque é uma **interface**. O `@Service` só marca classes concretas
   (beans do Spring). A impl é que tem.
   </details>

2. **Se eu apagar o `AlunoService` (interface) e deixar só `AlunoServiceImpl`, funciona?**
   <details>
   <summary>Resposta</summary>
   Funciona, mas você precisa ajustar tudo que **injeta** `AlunoService` pra
   injetar `AlunoServiceImpl` diretamente. O padrão perde a flexibilidade.
   </details>

3. **O que acontece se eu mandar `{ "nome": "", "ra": 0 }` no POST?**
   <details>
   <summary>Resposta</summary>
   O service lança `400 Bad Request` com mensagem
   `"Dados do aluno inválidos"`, porque `nome` está vazio e `ra` ≤ 0.
   </details>

4. **Como o Controller recebe o `400` e devolve pro cliente?**
   <details>
   <summary>Resposta</summary>
   O Spring captura a `ResponseStatusException` automaticamente e monta
   uma resposta HTTP com o status e a mensagem. Você não precisa de `try/catch`.
   </details>

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`aluno.md`](../entities/aluno.md) | Entidade do service |
| [`aluno-repository.md`](../repositories/README.md) | Repository usado |
| [`aluno-controller.md`](../controllers/aluno-controller.md) | Camada HTTP (se existir) |

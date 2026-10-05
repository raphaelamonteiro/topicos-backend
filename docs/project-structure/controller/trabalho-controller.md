# Controller: Trabalho

**Resumo:** Controller REST que expõe endpoints HTTP para gerenciar trabalhos.

> Delega as operações ao `TrabalhoService`. Caminho base: `/trabalho`.

## 📄 Código

```java
package br.com.topicos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.topicos.entity.Trabalho;
import br.com.topicos.service.TrabalhoService;

@RestController
@CrossOrigin
@RequestMapping("/trabalho")
public class TrabalhoController {

    private final TrabalhoService service;

    public TrabalhoController(TrabalhoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Trabalho> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public ResponseEntity<Trabalho> cadastrar(@RequestBody Trabalho trabalho) {
        Trabalho trabalhoCadastrado = service.cadastrar(trabalho);
        return ResponseEntity.created(URI.create("/trabalho")).body(trabalhoCadastrado);
    }

    @GetMapping("/buscar")
    public List<Trabalho> buscarPorRaAlunoETitulo(@RequestParam("ra") Long ra, @RequestParam("titulo") String titulo) {
        return service.buscarPorRaAlunoETitulo(ra, titulo);
    }

}
```

> **Arquivo fonte:** [`src/main/java/br/com/topicos/controller/TrabalhoController.java`](../../../src/main/java/br/com/topicos/controller/TrabalhoController.java)

## 🎯 Objetivo

Camada HTTP da aplicação para o recurso **trabalho**. Recebe requisições,
delega ao `TrabalhoService` e devolve a resposta em JSON.

> ⚠️ O Controller **não tem regras de negócio** — isso fica no Service.

## 🧩 Anotações principais

| Anotação | Função |
|---|---|
| `@RestController` | Marca como controller REST (retorno = JSON) |
| `@RequestMapping("/trabalho")` | Caminho base dos endpoints |
| `@CrossOrigin` | Permite requisições de outras origens (CORS) |
| `@GetMapping` | Endpoint HTTP GET |
| `@PostMapping` | Endpoint HTTP POST |
| `@RequestBody` | Converte JSON do corpo → objeto Java |
| `@RequestParam` | Pega valor da **query string** (`?ra=1`) |

> 💡 **Diferença importante:** este controller **não usa `@PathVariable`**.
> Todas as buscas usam `@RequestParam` (query string).

## 🔗 Injeção do Service

```java
private final TrabalhoService service;

public TrabalhoController(TrabalhoService service) {
    this.service = service;
}
```

Injeção via **construtor** — boa prática do Spring:
- O campo pode ser `final` (imutável)
- Facilita testes unitários
- Obriga a dependência a existir

## 📌 Endpoints

### `POST /trabalho`

Cadastra um novo trabalho.

**Request:**
```http
POST http://localhost:8080/trabalho
Content-Type: application/json

{
    "titulo": "Teste 1",
    "dataHoraEntrega": "2026-10-05T14:30:00",
    "descricao": "Trabalho sobre Spring Boot",
    "aluno": { "id": 1 },
    "nota": 6,
    "justificativa": "Bom, mas falta conteúdo"
}
```

**Response (201 Created):**
```http
Location: /trabalho
Content-Type: application/json
```
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

> ⚠️ **Observação:** o `Location` aponta para `/trabalho` (lista) em vez
> de `/trabalho/{id}` (recurso específico). O padrão REST recomendaria
> incluir o ID. **Não é erro grave**, mas é inconsistente com o `CursoController`.

### `GET /trabalho`

Lista todos os trabalhos.

**Response (200 OK):**
```json
[
    { "id": 1, "titulo": "Teste 1", "nota": 6 },
    { "id": 2, "titulo": "Teste 2", "nota": null }
]
```

### `GET /trabalho/buscar?ra=X&titulo=Y`

Busca trabalhos de um aluno pelo RA, filtrando pelo título.

**Request:**
```http
GET http://localhost:8080/trabalho/buscar?ra=1&titulo=Teste
```

**Response (200 OK):**
```json
[
    { "id": 1, "titulo": "Teste 1", "nota": 6 }
]
```

> 💡 **No curl**, use aspas por causa do `&`:
> ```bash
> curl "http://localhost:8080/trabalho/buscar?ra=1&titulo=Teste"
> ```

## 🔎 `@RequestParam` vs `@PathVariable`

| Anotação | Onde o valor está | Exemplo |
|---|---|---|
| `@PathVariable` | No **caminho** da URL | `/curso/1` |
| `@RequestParam` | Na **query string** | `/trabalho/buscar?ra=1&titulo=Teste` |

| Controller | Usa |
|---|---|
| `CursoController` | `@PathVariable` (busca por ID) |
| `TrabalhoController` | `@RequestParam` (busca por filtros) |

**Regra prática:**
- Recurso específico por ID → `@PathVariable` (`/curso/1`)
- Busca com filtros/parâmetros → `@RequestParam` (`/trabalho/buscar?ra=1`)

## 📊 Status HTTP

| Status | Quando |
|---|---|
| **200 OK** | Listagem ou busca retornada |
| **201 Created** | Trabalho cadastrado |
| **400 Bad Request** | JSON ou parâmetro inválido |
| **404 Not Found** | Rota não existe |
| **500 Internal Server Error** | Erro no Service |

## 🏗️ Fluxo

```text
Cliente
   │ HTTP
   ▼
TrabalhoController   ← esta classe
   │ chamada Java
   ▼
TrabalhoService
   │
   ▼
TrabalhoRepository
   │ SQL
   ▼
PostgreSQL
```

## 🧪 Exercícios

1. **Por que esse controller usa `@RequestParam` e não `@PathVariable`?**
   <details>
   <summary>Resposta</summary>
   Porque a busca envolve **filtros** (RA + título), não um identificador
   único. `@RequestParam` é ideal para query strings com múltiplos critérios.
   </details>

2. **Se eu trocar `@RequestParam` por `@PathVariable` no `/buscar`, o que muda?**
   <details>
   <summary>Resposta</summary>
   A URL viraria `/trabalho/buscar/1/Teste` em vez de
   `/trabalho/buscar?ra=1&titulo=Teste`. Ambos funcionam — a escolha é
   de design.
   </details>

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`trabalho.md`](../entities/trabalho.md) | Entidade JPA |
| [`trabalho-service.md`](../services/trabalho-service.md) | Regras de negócio |
| [`trabalho-repository.md`](../repositories/trabalho-repository.md) | Acesso ao banco |

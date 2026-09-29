# Controller: Curso

**Resumo:** Controller REST que expõe endpoints HTTP para gerenciar cursos.
> Delega toda lógica ao `CursoService`. Caminho base: `/curso`.

Arquivo:

```java
package br.com.topicos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.topicos.entity.Curso;
import br.com.topicos.service.CursoService;

@RestController
@RequestMapping("/curso")
@CrossOrigin
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Curso> cadastrar(@RequestBody Curso curso) {
        Curso novo = service.cadastrar(curso);
        return ResponseEntity.created(URI.create("/curso/" + novo.getId())).body(novo);
    }

    @GetMapping
    public List<Curso> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Curso buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }
}
```

> **Arquivo fonte:** [`src/main/java/br/com/topicos/controller/CursoController.java`](../../src/main/java/br/com/topicos/controller/CursoController.java)

## :dart: Objetivo: 
O arquivo `CursoController` tem como objetivo expor os endpoints HTTP relacionados aos cursos da aplicação.

Ele atua como uma camada de entrada da API, recebendo as requisições HTTP, encaminhando as operações para o `CursoService` e retornando os dados ao cliente.

> :warning: O Controller não concentra as regras de negócio. <br>
> Essa responsabilidade fica na camada de Service.

## 🧩 Anotações principais
| Anotação	| Função	| 
|---|---|
| @RestController	| Indica que a classe é um Controller REST e que seus métodos retornam dados diretamente na resposta HTTP. | 
| @RequestMapping("/curso")	| Define /curso como o caminho base dos endpoints dessa classe. | 
| @CrossOrigin	| Permite requisições de diferentes origens, facilitando a comunicação com aplicações frontend. | 
| @PostMapping	| Define um endpoint HTTP POST. | 
| @GetMapping		| Define um endpoint HTTP GET. | 
| @PathVariable	| Obtém um valor diretamente da URL, como o ID do curso. | 
| @RequestBody	| Converte o corpo JSON da requisição em um objeto Java. | 

## 🔗 Injeção do Service

```java
private final CursoService service;

public CursoController(CursoService service) {
    this.service = service;
}
```

`CursoService` é recebido pelo construtor e armazenado no atributo service.

Dessa forma, o Controller delega ao Service as operações relacionadas aos cursos, mantendo uma separação entre a camada HTTP e as regras de negócio.

## 📌 Endpoints

###  `POST /curso`  
Responsável por cadastrar um novo curso.

```java
@PostMapping
public ResponseEntity<Curso> cadastrar(@RequestBody Curso curso) {
    Curso novo = service.cadastrar(curso);
    return ResponseEntity.created(URI.create("/curso/" + novo.getId())).body(novo);
}
```

O objeto Curso é recebido através do @RequestBody. O Controller encaminha esse objeto para: `service.cadastrar(curso);` 

Após o cadastro, é retornado um ResponseEntity com:
- Status HTTP 201 Created
- URI do novo curso no header Location
- O curso cadastrado no corpo da resposta

Exemplo de resposta:
```bash
HTTP/1.1 201 Created
Location: /curso/1
```

Exemplo de body:
```bash
{
    "id": 1,
    "nome": "Análise e Desenvolvimento de Sistemas",
    "sigla": "ADS"
}
```

###  `GET /curso`  
Responsável por listar todos os cursos cadastrados.

```java
@GetMapping
public List<Curso> listar() {
    return service.listar();
}
```

O Controller chama: `service.listar();` e retorna uma lista de objetos `Curso`

###  `GET /curso/{id}`
Responsável por buscar um curso específico pelo seu ID.

```java
@GetMapping("/{id}")
public Curso buscarPorId(@PathVariable("id") Long id) {
    return service.buscarPorId(id);
}
```

O valor `{id}` presente na URL é capturado pelo `@PathVariable`:

```java
@PathVariable("id") Long id
```

Exemplo de request:
```bash
GET /curso/1
```
Nesse caso, o valor 1 será recebido como Long e encaminhado para:

```bash
service.buscarPorId(id);
```

### 📊 Status e possíveis respostas

| Status | Quando acontece |
|---|---|
| 200 OK | Curso encontrado |
| 404 Not Found | Nenhum curso com esse ID |
| 500 Internal Server Error | Erro inesperado no service |

## 🏗️ Fluxo da requisição
O fluxo desse Controller segue a arquitetura em camadas do projeto:

```text
Cliente
   │
   │ HTTP Request
   ▼
CursoController
   │
   │ chamada Java
   ▼
CursoService
   │
   │ chamada Java
   ▼
CursoRepository
   │
   │ SQL
   ▼
PostgreSQL
```

Assim, o CursoController é responsável principalmente pela comunicação HTTP, enquanto o CursoService concentra as operações e regras relacionadas aos cursos.

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`Curso.java`](../entity/Curso.md) | Entidade JPA |
| [`CursoService.java`](../service/CursoService.md) | Regras de negócio |
| [`CursoRepository.java`](../repository/CursoRepository.md) | Acesso ao banco |
# Controller: Displina

Resumo: Controller REST que expõe endpoints HTTP para gerenciar disciplinas.

> Delega as operações ao DisciplinaService. Caminho base: /disciplina.
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.topicos.entity.Disciplina;
import br.com.topicos.service.DisciplinaService;

@RestController
@CrossOrigin
@RequestMapping("/disciplina")
public class DisciplinaController {

    private final DisciplinaService service;

    public DisciplinaController(DisciplinaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Disciplina> cadastrar(@RequestBody Disciplina disciplina) {
        Disciplina nova = service.cadastrar(disciplina);
        return ResponseEntity.created(URI.create("/disciplina/" + nova.getId())).body(nova);
    }

    @GetMapping
    public List<Disciplina> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Disciplina buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/pesquisa")
    public Disciplina buscarPorIdParam(@RequestParam("id") Long id) {
        return service.buscarPorId(id);
    }

}
```

> **Arquivo fonte:** [`src/main/java/br/com/topicos/controller/DisciplinaController.java`](../../src/main/java/br/com/topicos/controller/DisciplinaController.java)

## :dart: Objetivo: 
O arquivo `DisciplinaController` tem como objetivo expor os endpoints HTTP relacionados às disciplinas da aplicação.

Ele atua como uma camada de entrada da API, recebendo as requisições HTTP, encaminhando as operações para o `DisciplinaService` e retornando os dados ao cliente.

> :warning: O Controller não concentra as regras de negócio.
> Essa responsabilidade fica na camada de Service.


## 🧩 Anotações principais
| Anotação	| Função	| 
|---|---|
| @RestController	| Indica que a classe é um Controller REST e que seus métodos retornam dados diretamente na resposta HTTP. | 
| @RequestMapping("/disciplina")	| Define /disciplina como o caminho base dos endpoints dessa classe. | 
| @CrossOrigin	| Permite requisições de diferentes origens, facilitando a comunicação com aplicações frontend. | 
| @PostMapping	| Define um endpoint HTTP POST. | 
| @GetMapping		| Define um endpoint HTTP GET. | 
| @PathVariable	| Obtém um valor diretamente da URL, como o ID da disciplina. | 
| @RequestBody	| Converte o corpo JSON da requisição em um objeto Java. | 

## 🔗 Injeção do Service

```java
private final DisciplinaService service;

public DisciplinaController(DisciplinaService service) {
    this.service = service;
}
```

`DisciplinaService` é recebido pelo construtor e armazenado no atributo service.

Dessa forma, o Controller delega ao Service as operações relacionadas às disciplinas, mantendo uma separação entre a camada HTTP e as regras de negócio.

## 📌 Endpoints

###  `POST / disciplina`  
Responsável por cadastrar uma nova disciplina.

```java
@PostMapping
public ResponseEntity<Disciplina> cadastrar(@RequestBody Disciplina disciplina) {
    Disciplina nova = service.cadastrar(disciplina);
    return ResponseEntity.created(URI.create("/disciplina/" + nova.getId())).body(nova);
}
```

O objeto Discipllina é recebido através do @RequestBody. O Controller encaminha esse objeto para: `service.cadastrar(disciplina);` 

Após o cadastro, é retornado um ResponseEntity com:
- Status HTTP 201 Created
- URI da nova disciplina no header Location
- Disciplina cadastrada no corpo da resposta

Exemplo de resposta:
```bash
HTTP/1.1 201 Created
Location: /disciplina/1
```

Exemplo de body:
```bash
{
    "id": 1,
    "codigo": "TINF01",
    "nome": "Tópicos Especiais em Informática"
}
```

> Os valores do exemplo podem variar de acordo com os dados cadastrados no banco.

###  `GET /disciplina`  
Responsável por listar todas as disciplinas cadastradas.

```java
@GetMapping
public List<Disciplina> listar() {
    return service.listar();
}
```

O Controller chama: `service.listar();` e retorna uma lista de objetos `Disciplina`

###  `GET /disciplina/{id}`
Responsável por buscar uma disciplina específica pelo seu ID.

```java
@GetMapping("/{id}")
public Disciplina buscarPorId(@PathVariable("id") Long id) {
    return service.buscarPorId(id);
}
```

O valor `{id}` presente na URL é capturado pelo `@PathVariable`:

```java
@PathVariable("id") Long id
```

Exemplo de request:
```bash
GET /disciplina/1
```
Nesse caso, o valor 1 será recebido como Long e encaminhado para:

```bash
service.buscarPorId(id);
```

## 🔎 @PathVariable x @RequestParam
A Controller possui duas formas de buscar uma disciplina pelo ID:

| Forma | Endpoint | Anotação |
|---|---|---|
| Path Variable | /disciplina/1 | 	@PathVariable |
| Query Parameter | /disciplina/pesquisa?id=1 | @RequestParam |

Ambas as abordagens utilizam o mesmo método do Service: 
```java
service.buscarPorId(id);
```
		

### 📊 Status e possíveis respostas

| Status | Quando acontece |
|---|---|
| 200 OK | Disciplina encontrada |
| 404 Not Found | Nenhuma disciplina com esse ID |
| 500 Internal Server Error | Erro inesperado no service |

## 🏗️ Fluxo da requisição
O fluxo desse Controller segue a arquitetura em camadas do projeto:

```text
Cliente
   │
   │ HTTP Request
   ▼
DisciplinaController
   │
   │ chamada Java
   ▼
DisciplinaService
   │
   │ chamada Java
   ▼
DisciplinaRepository
   │
   │ SQL
   ▼
PostgreSQL
```

Assim, DisciplinaController é responsável principalmente pela comunicação HTTP, enquanto o DisciplinaService concentra as operações e regras relacionadas as disciplinas.

## 🔗 Arquivos relacionados

| Arquivo | Papel |
|---|---|
| [`Disciplina.java`](../entity/Disciplina.md) | Entidade JPA |
| [`DisciplinaService.java`](../service/DisciplinaService.md) | Regras de negócio |
| [`DisciplinaRepository.java`](../repository/DisciplinaRepository.md) | Acesso ao banco |
# 🚀 Cheatsheet — CRUD Spring Boot

## 📁 5 arquivos para criar (nessa ordem)

```
entity/Xxx.java
repository/XxxRepository.java
service/XxxService.java           ← interface
service/XxxServiceImpl.java       ← classe
controller/XxxController.java
```

---

## 1️⃣ ENTITY

```java
package br.com.topicos.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xxx_xxx")
public class Xxx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "xxx_id")
    private Long id;

    @Column(name = "xxx_nome")
    private String nome;

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
```

**Relacionamentos (se precisar):**

```java
// Muitos pra 1 (Disciplina → Curso)
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "xxx_id_fk")
private Outra outra;

// Muitos pra muitos
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "tabela_intermediaria",
    joinColumns = @JoinColumn(name = "fk_esta_entidade"),
    inverseJoinColumns = @JoinColumn(name = "fk_outra_entidade")
)
private Set<Outra> outras;
```

---

## 2️⃣ REPOSITORY

```java
package br.com.topicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.topicos.entity.Xxx;

public interface XxxRepository extends JpaRepository<Xxx, Long> {
}
```

**Se precisar de query customizada:**

```java
// Derived query (nome do método gera SQL)
List<Xxx> findByNomeContainingIgnoreCase(String nome);

// JPQL manual
@Query("SELECT x FROM Xxx x WHERE x.nome = :nome")
List<Xxx> buscarPeloNome(String nome);
```

---

## 3️⃣ SERVICE (interface)

```java
package br.com.topicos.service;

import java.util.List;
import br.com.topicos.entity.Xxx;

public interface XxxService {
    Xxx cadastrar(Xxx xxx);
    List<Xxx> listar();
    Xxx buscarPorId(Long id);
}
```

---

## 4️⃣ SERVICE (implementação) ⭐ IMPORTANTE

```java
package br.com.topicos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Xxx;
import br.com.topicos.repository.XxxRepository;

@Service
public class XxxServiceImpl implements XxxService {

    private final XxxRepository repo;

    public XxxServiceImpl(XxxRepository repo) {
        this.repo = repo;
    }

    @Override
    public Xxx cadastrar(Xxx xxx) {
        if (xxx == null ||
                xxx.getId() != null ||
                xxx.getNome() == null ||
                xxx.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos");
        }
        return repo.save(xxx);
    }

    @Override
    public List<Xxx> listar() {
        return repo.findAll();
    }

    @Override
    public Xxx buscarPorId(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id não pode ser nulo");
        }
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Xxx não encontrado"));
    }
}
```

> 💡 **Regras de ouro do Service:**
> - `@Service` **só na classe**, nunca na interface
> - Injeção via construtor: `private final XxxRepository repo;`
> - `@Override` em todo método
> - **Validação no `cadastrar`** → 400
> - **Validação no `buscarPorId`** → 400 se `id == null`, 404 se não existir

---

## 5️⃣ CONTROLLER

```java
package br.com.topicos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.topicos.entity.Xxx;
import br.com.topicos.service.XxxService;

@RestController
@RequestMapping("/xxx")
@CrossOrigin
public class XxxController {

    private final XxxService service;

    public XxxController(XxxService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Xxx> cadastrar(@RequestBody Xxx xxx) {
        Xxx novo = service.cadastrar(xxx);
        return ResponseEntity.created(URI.create("/xxx/" + novo.getId())).body(novo);
    }

    @GetMapping
    public List<Xxx> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Xxx buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }
}
```

---

## 🎯 Anotações essenciais

| Anotação | Onde | O que faz |
|---|---|---|
| `@Entity` | Entity (classe) | Vira tabela |
| `@Table(name=...)` | Entity (classe) | Nome da tabela |
| `@Id` | Entity (campo) | Chave primária |
| `@GeneratedValue(strategy = IDENTITY)` | Entity (campo) | ID auto-gerado |
| `@Column(name=...)` | Entity (campo) | Nome da coluna |
| `@ManyToOne` / `@ManyToMany` | Entity (campo) | Relacionamento |
| `@Service` | Impl (classe) | Bean de service |
| `@RestController` | Controller (classe) | Controller REST |
| `@RequestMapping("/xxx")` | Controller (classe) | Prefixo das rotas |
| `@CrossOrigin` | Controller (classe) | Habilita CORS |
| `@PostMapping` / `@GetMapping` | Controller (método) | Verbo HTTP |
| `@RequestBody` | Controller (parâmetro) | JSON → objeto |
| `@PathVariable` | Controller (parâmetro) | Valor da URL `/xxx/{id}` |
| `@RequestParam` | Controller (parâmetro) | Valor da query `?ra=1` |
| `@Override` | Impl (método) | Sobrescreve interface |

---

## 📊 Status HTTP

| Status | Quando |
|---|---|
| **200 OK** | GET bem-sucedido |
| **201 Created** | POST bem-sucedido |
| **400 Bad Request** | Dados inválidos |
| **404 Not Found** | Recurso não existe |

**Como lançar:**
```java
throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "msg");
throw new ResponseStatusException(HttpStatus.NOT_FOUND, "msg");
```

---

## 🎁 Métodos prontos do JpaRepository

| Método | Retorno |
|---|---|
| `save(entity)` | Entity (com ID preenchido) |
| `findAll()` | `List<Entity>` |
| `findById(id)` | `Optional<Entity>` |
| `deleteById(id)` | void |
| `existsById(id)` | boolean |
| `count()` | long |

**Buscar e lançar 404:**
```java
return repo.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "não encontrado"));
```

---

## 🚀 Comandos úteis

```bash
# Rodar a aplicação
mvn spring-boot:run

# Rodar os testes
mvn test

# Limpar e rodar
mvn clean test

# Testar endpoint
curl http://localhost:8080/xxx

# POST com JSON
curl -X POST http://localhost:8080/xxx \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste"}'
```

---

## ⚠️ Checklist antes de rodar

- [ ] `@Service` está **só na impl**, não na interface
- [ ] Injeção via **construtor** (não `@Autowired` em campo)
- [ ] Controller importa `service`, não `repository`
- [ ] `@RequestMapping` do controller bate com a URL
- [ ] Package correto em todos os arquivos (`br.com.topicos.xxx`)

---

## 🔑 Credenciais do ambiente

| Item | Valor |
|---|---|
| Host do banco | `db` (**não** `localhost`) |
| Porta | `5432` |
| User | `postgres` |
| Password | `postgres` |
| Database | `postgres` |
| App | `http://localhost:8080` |

---

## 🆘 Erros comuns e solução

| Erro | Solução |
|---|---|
| `password authentication failed` | Use `postgres`/`postgres` no `application.properties` |
| `Connection refused to localhost` | Use `db` em vez de `localhost` |
| `cannot find symbol: class Xxx` | Falta criar o arquivo `Xxx.java` |
| `package does not exist` | Verifica o package no topo do arquivo |
| `BUILD FAILURE` | Lê o primeiro `[ERROR]` — geralmente indica o arquivo e a linha |






Boa pergunta — e isso **cai em prova**! Vou te explicar quando precisa de DDL, mostrar um exemplo, e depois te dar o **tutorial de CRUD do zero em minutos**.

---

## 🧠 Quando você precisa criar DDL?

Depende do modo do `ddl-auto` no `application.properties`:

| Modo | Precisa criar DDL manual? |
|---|---|
| `update` (o seu) | ❌ **Não** — o Hibernate cria/atualiza tabelas sozinho |
| `create` | ❌ **Não** — o Hibernate recria tudo |
| `validate` | ✅ **Sim** — você precisa criar as tabelas antes |
| `none` | ✅ **Sim** — o Hibernate não faz nada |

**No seu projeto (que usa `update`):**
- Quando você cria uma **entidade nova** (`@Entity`), o Hibernate **cria a tabela automaticamente** quando a app sobe.
- **Não precisa escrever DDL manual.**

**Mas na prova**, o professor pode pedir:
- *"Crie a tabela X primeiro, depois a entidade"* → nesse caso **precisa DDL**
- *"Use `ddl-auto=update` e deixe o Hibernate criar"* → **não precisa**

> 💡 **Como saber?** Se o professor der um script SQL inicial no enunciado → use. Se falar "crie a entidade" → deixa o Hibernate fazer.

---

## 📄 Exemplo de DDL (caso precise)

Digamos que a prova peça uma entidade **`Professor`** com os campos: `id`, `nome`, `email`, `titulo`.

### DDL da tabela

```sql
create table pro_professor (
    pro_id bigint generated always as identity,
    pro_nome varchar(100) not null,
    pro_email varchar(150) not null unique,
    pro_titulo varchar(50),
    primary key (pro_id)
);
```

**Anatomia:**

| Parte | Significado |
|---|---|
| `pro_professor` | Nome da tabela (padrão `prefixo_entidade`) |
| `pro_id bigint generated always as identity` | PK auto-incremento |
| `not null` | Campo obrigatório |
| `unique` | Não permite duplicata |
| `varchar(N)` | String com limite de N caracteres |
| `primary key (pro_id)` | Define a PK |

### Se tiver FK

```sql
create table dis_disciplina (
    dis_id bigint generated always as identity,
    dis_codigo varchar(10) not null,
    dis_nome varchar(100) not null,
    dis_cur_id bigint not null,
    primary key (dis_id),
    constraint dis_cur_fk foreign key (dis_cur_id) references cur_curso(cur_id)
);
```

⚠️ **Ordem importa:** crie a tabela **pai** antes da **filha** (a que tem FK).

### Se for N:N

```sql
create table mat_matricula (
    mat_aln_id bigint,
    mat_dis_id bigint,
    primary key (mat_aln_id, mat_dis_id),
    constraint mat_aln_fk foreign key (mat_aln_id) references aln_aluno(aln_id),
    constraint mat_dis_fk foreign key (mat_dis_id) references dis_disciplina(dis_id)
);
```

---

## 🍰 TUTORIAL: CRUD do zero em minutos

Suponha que a prova peça: **"Crie o CRUD de `Professor` com `nome` e `email`"**.

### Passo 1: Definir os dados

| Item | Valor |
|---|---|
| Entidade | `Professor` |
| Tabela | `pro_professor` (padrão `pro_`) |
| Colunas | `pro_id`, `pro_nome`, `pro_email` |
| URL base | `/professor` |

---

### Passo 2: Criar a **Entity**

Arquivo: `src/main/java/br/com/topicos/entity/Professor.java`

```java
package br.com.topicos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pro_professor")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pro_id")
    private Long id;

    @Column(name = "pro_nome")
    private String nome;

    @Column(name = "pro_email")
    private String email;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

**⏱️ ~2 min**

---

### Passo 3: Criar o **Repository**

Arquivo: `src/main/java/br/com/topicos/repository/ProfessorRepository.java`

```java
package br.com.topicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.topicos.entity.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
```

**⏱️ ~30 segundos**

---

### Passo 4: Criar o **Service (interface)**

Arquivo: `src/main/java/br/com/topicos/service/ProfessorService.java`

```java
package br.com.topicos.service;

import java.util.List;
import br.com.topicos.entity.Professor;

public interface ProfessorService {
    Professor cadastrar(Professor professor);
    List<Professor> listar();
    Professor buscarPorId(Long id);
}
```

**⏱️ ~30 segundos**

---

### Passo 5: Criar o **Service (implementação)**

Arquivo: `src/main/java/br/com/topicos/service/ProfessorServiceImpl.java`

```java
package br.com.topicos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Professor;
import br.com.topicos.repository.ProfessorRepository;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository repo;

    public ProfessorServiceImpl(ProfessorRepository repo) {
        this.repo = repo;
    }

    @Override
    public Professor cadastrar(Professor professor) {
        if (professor == null ||
                professor.getId() != null ||
                professor.getNome() == null ||
                professor.getNome().isBlank() ||
                professor.getEmail() == null ||
                professor.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do professor inválidos");
        }
        return repo.save(professor);
    }

    @Override
    public List<Professor> listar() {
        return repo.findAll();
    }

    @Override
    public Professor buscarPorId(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id não pode ser nulo");
        }
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));
    }
}
```

**⏱️ ~2 min**

---

### Passo 6: Criar o **Controller**

Arquivo: `src/main/java/br/com/topicos/controller/ProfessorController.java`

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

import br.com.topicos.entity.Professor;
import br.com.topicos.service.ProfessorService;

@RestController
@RequestMapping("/professor")
@CrossOrigin
public class ProfessorController {

    private final ProfessorService service;

    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Professor> cadastrar(@RequestBody Professor professor) {
        Professor novo = service.cadastrar(professor);
        return ResponseEntity.created(URI.create("/professor/" + novo.getId())).body(novo);
    }

    @GetMapping
    public List<Professor> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Professor buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }
}
```

**⏱️ ~2 min**

---

### Passo 7: Se quiser DDL manual

Roda no SQLTools:

```sql
create table pro_professor (
    pro_id bigint generated always as identity,
    pro_nome varchar(100) not null,
    pro_email varchar(150) not null,
    primary key (pro_id)
);
```

**⏱️ ~30 segundos**

> 💡 **Ou** deixa o Hibernate criar sozinho (com `ddl-auto=update`).

---

### Passo 8: Rodar e testar

```bash
mvn spring-boot:run
```

Em outro terminal:

```bash
# Listar (deve retornar [])
curl http://localhost:8080/professor

# Cadastrar
curl -X POST http://localhost:8080/professor \
  -H "Content-Type: application/json" \
  -d '{"nome":"Raphaela","email":"rapha@test.com"}'

# Listar de novo (deve ter 1)
curl http://localhost:8080/professor

# Buscar por ID
curl http://localhost:8080/professor/1
```

**⏱️ ~2 min**

---

## ⏱️ Tempo total: ~10 minutos

| Passo | Tempo |
|---|---|
| Entity | 2 min |
| Repository | 30s |
| Service (interface) | 30s |
| Service (impl) | 2 min |
| Controller | 2 min |
| DDL (opcional) | 30s |
| Rodar e testar | 2 min |
| **TOTAL** | **~10 min** |

---

## 🎯 Ordem de criação (memorize!)

```
1. Entity         ← mais fácil testar se compila
2. Repository     ← interface
3. Service (interface)
4. Service (impl) ← lógica
5. Controller     ← endpoints
6. DDL (opcional) ← se ddl-auto=validate/none
```

> 💡 **Dica:** cria um de cada vez e vai salvando. Se der erro, é no arquivo que você acabou de criar.

---

## 📋 Checklist rápido

- [ ] Entity com `@Entity`, `@Table`, `@Id`, `@Column`
- [ ] Repository `extends JpaRepository<Entity, Long>`
- [ ] Service interface com os 3 métodos
- [ ] ServiceImpl com `@Service` + injeção via construtor
- [ ] Controller com `@RestController`, `@RequestMapping`, `@CrossOrigin`
- [ ] DDL (se precisar) na ordem: pai → filho
- [ ] `mvn spring-boot:run` → deve subir sem erro
- [ ] Testar com curl/Insomnia

---

## ⚠️ Pegadinhas comuns

| Pegadinha | Solução |
|---|---|
| Esqueci `@Service` na impl | A app não sobe com "bean not found" |
| Coloquei `@Service` na interface | Não é erro, mas não faz nada |
| Esqueci `@Override` | Não é erro, mas é boa prática |
| Package errado | Dá erro de "cannot find symbol" |
| URL do `@RequestMapping` não bate com o path do curl | 404 |
| FK aponta pra tabela que não existe | Erro de constraint |

---

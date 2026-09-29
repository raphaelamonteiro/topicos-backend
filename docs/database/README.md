# Explicação e divisão do arquivo "Topicos.session.sql"

```text

📦 Script completo (bootstrap) - Topicos.session.sql
├── 🔨 DDL (estrutura)
│   ├── create table aln_aluno
│   ├── create table cur_curso
│   ├── create table dis_disciplina
│   ├── create table mat_matricula
│   ├── drop user if exists spring
│   ├── create user spring
│   └── create table tra_trabalho
│
├── 🌱 DML (seed propriamente dito)
│   ├── insert into aln_aluno
│   ├── insert into cur_curso
│   ├── insert into dis_disciplina
│   ├── insert into mat_matricula
│   └── insert into tra_trabalho
│
└── 🔐 DCL (permissões)
    └── grant ... to spring

```

## 🌱 Seed

O seed do projeto está em [`docs/02-seed.sql`](02-seed.sql).

Ele popula o banco com dados de teste:
- **2 alunos**
- **2 cursos**
- **2 disciplinas**
- **3 matrículas**
- **2 trabalhos**

### Ordem de execução
O seed respeita a ordem de dependências das FKs:
1. `aln_aluno` (sem FK)
2. `cur_curso` (sem FK)
3. `dis_disciplina` (FK → cur_curso)
4. `mat_matricula` (FK → aln_aluno, dis_disciplina)
5. `tra_trabalho` (FK → aln_aluno)

### Como rodar
1. Confirme que as tabelas existem (`\dt` no psql, ou veja no SQLTools)
2. Abra `docs/02-seed.sql` no SQLTools
3. Rode o arquivo inteiro

## 🏗️ Schema (DDL)

O schema (estrutura das tabelas) está em [`docs/01-schema.sql`](01-schema.sql).
Ele contém apenas `CREATE TABLE` e configurações de usuário/permissões.
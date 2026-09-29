-- 1. Apaga os filhos primeiro (quem tem FK)
delete from tra_trabalho;
delete from mat_matricula;
delete from dis_disciplina;
delete from aln_aluno;
delete from cur_curso;
alter table aln_aluno
alter column aln_id restart with 1;
alter table cur_curso
alter column cur_id restart with 1;
alter table dis_disciplina
alter column dis_id restart with 1;
alter table tra_trabalho
alter column tra_id restart with 1;
drop table if exists tra_trabalho cascade;
drop table if exists mat_matricula cascade;
drop table if exists dis_disciplina cascade;
drop table if exists aln_aluno cascade;
drop table if exists cur_curso cascade;
-- Deve mostrar 2 alunos com IDs 1 e 2
select *
from aln_aluno;
-- Deve mostrar 2 cursos com IDs 1 e 2
select *
from cur_curso;
-- Deve mostrar 2 disciplinas
select *
from dis_disciplina;
-- Deve mostrar 3 matrículas
select *
from mat_matricula;
-- Deve mostrar 2 trabalhos
select *
from tra_trabalho;
-- Popula com dados de teste
insert into aln_aluno(aln_ra, aln_nome, aln_data_nascimento)
values (1, 'John Doe', '2001-10-08'),
    (2, 'Jane Smith', '2002-10-21');
insert into cur_curso(cur_sigla, cur_nome)
values ('BD', 'Banco de Dados'),
    ('ADS', 'Análise e Desenvolvimento de Sistemas');
insert into dis_disciplina(
        dis_codigo,
        dis_nome,
        dis_carga_horaria,
        dis_cur_id
    )
values (
        'IMB003',
        'Arquitetura e Modelagem de Banco de Dados',
        80,
        1
    ),
    ('IES001', 'Engenharia de Software I', null, 1);
insert into mat_matricula(mat_aln_id, mat_dis_id)
values (1, 1),
    (1, 2),
    (2, 1);
insert into tra_trabalho(
        tra_titulo,
        tra_data_hora_entrega,
        tra_aluno,
        tra_nota,
        tra_justificativa
    )
values (
        'Teste 1',
        current_timestamp,
        1,
        6,
        'Bom, mas falta conteúdo'
    ),
    (
        'Teste 2',
        current_timestamp,
        2,
        null,
        'Incompleto'
    );
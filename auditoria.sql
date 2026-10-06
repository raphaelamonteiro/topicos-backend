create table aud_auditoria (
    aud_id bigint generated always as identity,
    aud_nome_antigo varchar(30) not null,
    aud_nome_novo varchar(30) not null,
    aud_data_hora timestamp not null,
    aud_curso bigint not null,
    aud_autorizacao int,
    foreign key (aud_curso) references cur_curso (cur_id)
);
insert into aud_auditoria (
        aud_nome_antigo,
        aud_nome_novo,
        aud_data_hora,
        aud_curso,
        aud_autorizacao
    )
values (
        'Redes e Banco de Dados',
        'Banco de Dados',
        '2016-03-03 10:00:00',
        1,
        5342
    ),
    (
        'Banco Dados',
        'Banco de Dados',
        current_timestamp,
        1,
        null
    );
GRANT update,
    delete,
    insert,
    select on all tables in schema public to spring;
package br.com.topicos.service;

import java.util.List;

import br.com.topicos.entity.Opcao;

public interface OpcaoService {

    public Opcao cadastrar(Opcao opcao);

    public List<Opcao> buscarPorTextoEQuestaoId(String texto, Long questaoId);

    public Opcao buscarPorId(Long id);

    public List<Opcao> listar();
    
}

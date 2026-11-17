package br.com.topicos.service;

import java.util.List;

import br.com.topicos.entity.Instrucao;

public interface InstrucaoService {
    
    public Instrucao cadastrar(Instrucao instrucao);

    public List<Instrucao> buscarPorOrdemEAvaliacaoId(Integer ordem, Long avaliacaoId);

    public Instrucao buscarPorId(Long id);

    public List<Instrucao> listar();
}

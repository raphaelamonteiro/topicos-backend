package br.com.topicos.service;

import java.util.List;

import br.com.topicos.entity.Trabalho;

public interface TrabalhoService {

    public List<Trabalho> listarTodos();

    public Trabalho cadastrar(Trabalho trabalho);

    public List<Trabalho> buscarPorRaAlunoETitulo(Long ra, String titulo);

}
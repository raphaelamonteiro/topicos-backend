package br.com.topicos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.com.topicos.entity.Questao;

public interface QuestaoService {

    public Questao cadastrar(Questao questao);

    public List<Questao> listar();

    public Questao buscarPorId(Long id);

    public List<Questao> listarPorAvaliacaoDataHoraEDisciplinaCodigo(LocalDateTime avaliacaoDataHora, String disciplinaCodigo);

    public List<Questao> listarPorEnunciadoEIdAvaliacao(String enunciado, Long avaliacaoId);
    
}

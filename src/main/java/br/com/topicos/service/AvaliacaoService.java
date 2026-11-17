package br.com.topicos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.com.topicos.entity.Avaliacao;

public interface AvaliacaoService {

    public Avaliacao cadastrar(Avaliacao avaliacao);

    public List<Avaliacao> listar();

    public Avaliacao buscarPorId(Long id);

    public List<Avaliacao> listarPorDataHoraEDisciplinaCodigo(LocalDateTime dataHora, String disciplinaCodigo);
    
}

package br.com.topicos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.topicos.entity.Questao;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    
    public List<Questao> findByAvaliacaoDataHoraLessThanAndAvaliacaoDisciplinaCodigo(LocalDateTime dataHora, String disciplinaCodigo);

    public List<Questao> findByEnunciadoContainingIgnoreCaseAndAvaliacaoId(String enunciado, Long avaliacaoId);
    
}

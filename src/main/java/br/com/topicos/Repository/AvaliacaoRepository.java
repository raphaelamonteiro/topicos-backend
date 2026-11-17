package br.com.topicos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.topicos.entity.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    
    public List<Avaliacao> findByDataHoraGreaterThanEqualAndDisciplinaCodigo(LocalDateTime dataHora, String disciplinaCodigo);
    
}

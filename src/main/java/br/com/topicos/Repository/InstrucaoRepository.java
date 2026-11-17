package br.com.topicos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.topicos.entity.Instrucao;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {

    public List<Instrucao> findByOrdemGreaterThanEqualAndAvaliacaoId(Integer ordem, Long avaliacaoId);
    
}

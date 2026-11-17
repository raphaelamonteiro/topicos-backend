package br.com.topicos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.topicos.entity.Opcao;

public interface OpcaoRepository extends JpaRepository<Opcao, Long> {

    public List<Opcao> findByTextoContainingIgnoreCaseAndQuestaoId(String texto, Long questaoId);
    
}

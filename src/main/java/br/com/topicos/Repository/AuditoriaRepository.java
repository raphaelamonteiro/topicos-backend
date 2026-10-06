package br.com.topicos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.topicos.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

        List<Auditoria> findByAudNomeAntigoContainingIgnoreCase(String audNomeAntigo);

        @Query("SELECT a FROM Auditoria a WHERE LOWER(a.audNomeAntigo) LIKE LOWER(CONCAT('%', :nomeAntigo, '%')) AND a.curso.nome = :nomeCurso")
        List<Auditoria> buscarPorNomeAntigoECurso(
                        @Param("nomeAntigo") String nomeAntigo,
                        @Param("nomeCurso") String nomeCurso);
}
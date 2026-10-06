package br.com.topicos.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "aud_auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aud_id")
    private Long id;

    @Column(name = "aud_nome_antigo")
    private String audNomeAntigo;

    @Column(name = "aud_nome_novo")
    private String audNomeNovo;

    @Column(name = "aud_data_hora")
    private LocalDateTime audDataHora;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aud_curso")
    private Curso curso;

    @Column(name = "aud_autorizacao")
    private Integer audAutorizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAudNomeAntigo() {
        return audNomeAntigo;
    }

    public void setAudNomeAntigo(String audNomeAntigo) {
        this.audNomeAntigo = audNomeAntigo;
    }

    public String getAudNomeNovo() {
        return audNomeNovo;
    }

    public void setAudNomeNovo(String audNomeNovo) {
        this.audNomeNovo = audNomeNovo;
    }

    public LocalDateTime getAudDataHora() {
        return audDataHora;
    }

    public void setAudDataHora(LocalDateTime audDataHora) {
        this.audDataHora = audDataHora;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Integer getAudAutorizacao() {
        return audAutorizacao;
    }

    public void setAudAutorizacao(Integer audAutorizacao) {
        this.audAutorizacao = audAutorizacao;
    }

}

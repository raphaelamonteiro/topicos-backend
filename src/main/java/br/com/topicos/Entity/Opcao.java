package br.com.topicos.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "opc_opcao")
public class Opcao {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opc_id")
    private Long id;

    @Column(name = "opc_texto")
    private String texto;

    @Column(name = "opc_data_criacao")
    private LocalDate dataCriacao;

    @Column(name = "opc_explicacao")
    private String explicacao;

    @Column(name = "opc_correta")
    private Boolean correta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "opc_questao")
    private Questao questao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public Boolean getCorreta() {
        return correta;
    }

    public void setCorreta(Boolean correta) {
        this.correta = correta;
    }

    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }
    
}

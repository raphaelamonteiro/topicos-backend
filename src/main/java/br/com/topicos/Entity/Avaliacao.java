package br.com.topicos.entity;

import java.time.LocalDateTime;

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
@Table(name = "ava_avaliacao")
public class Avaliacao {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ava_id")
    private Long id;

    @Column(name = "ava_titulo")
    private String titulo;

    @Column(name = "ava_data_hora")
    private LocalDateTime dataHora;

    @Column(name = "ava_descricao")
    private String descricao;

    @Column(name = "ava_peso")
    private Float peso;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ava_disciplina")
    private Disciplina disciplina;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }
    
}

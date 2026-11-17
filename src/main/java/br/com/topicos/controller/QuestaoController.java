package br.com.topicos.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.topicos.entity.Questao;
import br.com.topicos.service.QuestaoService;

@RestController 
@CrossOrigin 
@RequestMapping("/questao")
public class QuestaoController {

    private final QuestaoService service;

    public QuestaoController(QuestaoService service) {
        this.service = service;
    }

    @GetMapping 
    public List<Questao> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<Questao> cadastrar(@RequestBody Questao questao) {
        Questao novaQuestao = service.cadastrar(questao);
        return ResponseEntity.created(URI.create("/questao/" + novaQuestao.getId())).body(novaQuestao);
    }

    @GetMapping("/{id}")
    public Questao obterPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/buscarPorAvaliacao")
    public List<Questao> listarPorAvaliacaoDataHoraEDisciplinaCodigo(
        @RequestParam ("avaliacaoDataHora") LocalDateTime avaliacaoDataHora, 
        @RequestParam("disciplinaCodigo") String disciplinaCodigo) {
        return service.listarPorAvaliacaoDataHoraEDisciplinaCodigo(
            avaliacaoDataHora, disciplinaCodigo);
    }

    @GetMapping ("/buscar")
    public List<Questao> listarPorEnunciadoEIdAvaliacao(
        @RequestParam ("enunciado") String enunciado, 
        @RequestParam("avaliacaoId") Long avaliacaoId) {
        return service.listarPorEnunciadoEIdAvaliacao(enunciado, avaliacaoId);
    }
    
}

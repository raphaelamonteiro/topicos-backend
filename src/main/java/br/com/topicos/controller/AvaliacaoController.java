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

import br.com.topicos.entity.Avaliacao;
import br.com.topicos.service.AvaliacaoService;

@RestController 
@CrossOrigin 
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping 
    public List<Avaliacao> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<Avaliacao> cadastrar(@RequestBody Avaliacao avaliacao) {
        Avaliacao novaAvaliacao = service.cadastrar(avaliacao);
        return ResponseEntity.created(URI.create("/avaliacao/" + novaAvaliacao.getId())).body(novaAvaliacao);
    }

    @GetMapping("/{id}")
    public Avaliacao obterPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public List<Avaliacao> listarPorDataHoraEDisciplinaCodigo(
        @RequestParam ("dataHora") LocalDateTime dataHora, 
        @RequestParam("disciplinaCodigo") String disciplinaCodigo) {
        return service.listarPorDataHoraEDisciplinaCodigo(
            dataHora, disciplinaCodigo);
    }
}

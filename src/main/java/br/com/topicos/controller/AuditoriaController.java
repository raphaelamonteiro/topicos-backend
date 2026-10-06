package br.com.topicos.controller;

import java.net.URI;
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

import br.com.topicos.entity.Auditoria;

import br.com.topicos.service.AuditoriaService;

@RestController
@CrossOrigin
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Auditoria> cadastrar(@RequestBody Auditoria auditoria) {
        Auditoria nova = service.cadastrar(auditoria);
        return ResponseEntity.created(URI.create("/auditoria/" + nova.getAudNomeAntigo())).body(nova);
    }

    @GetMapping
    public List<Auditoria> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Auditoria buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/pesquisa")
    public Auditoria buscarPorIdParam(@RequestParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public List<Auditoria> buscarPorNomeAntigoECurso(
            @RequestParam("nomeAntigo") String nomeAntigo,
            @RequestParam("curso") String nomeCurso) {
        return service.buscarPorNomeAntigoECurso(nomeAntigo, nomeCurso);
    }

}
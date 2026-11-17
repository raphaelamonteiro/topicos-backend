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

import br.com.topicos.entity.Instrucao;
import br.com.topicos.service.InstrucaoService;

@RestController 
@CrossOrigin 
@RequestMapping("/instrucao")
public class InstrucaoController {

    private final InstrucaoService service;

    public InstrucaoController(InstrucaoService service) {
        this.service = service;
    }

    @GetMapping 
    public List<Instrucao> listar() {
        return service.listar();
    }

    @GetMapping("/buscar")
    public List<Instrucao> buscarPorOrdemEAvaliacaoId(@RequestParam("ordem") Integer ordem, @RequestParam("avaliacaoId") Long avaliacaoId) {
        return service.buscarPorOrdemEAvaliacaoId(ordem, avaliacaoId);
    }

    @GetMapping("/{id}")
    public Instrucao buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Instrucao> cadastrar(@RequestBody Instrucao instrucao) {
        Instrucao instrucaoCadastrada = service.cadastrar(instrucao);
        return ResponseEntity.created(URI.create("/instrucao/" + instrucaoCadastrada.getId())).body(instrucaoCadastrada);
    }
    
}

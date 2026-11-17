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

import br.com.topicos.entity.Opcao;
import br.com.topicos.service.OpcaoService;

@RestController 
@CrossOrigin 
@RequestMapping("/opcao")
public class OpcaoController {

    private final OpcaoService service;

    public OpcaoController(OpcaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Opcao> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<Opcao> cadastrar(@RequestBody Opcao opcao) {
        Opcao opcaoCadastrada = service.cadastrar(opcao);
        return ResponseEntity.created(URI.create("/opcao/" + opcaoCadastrada.getId())).body(opcaoCadastrada);
    }

    @GetMapping("/buscar")
    public List<Opcao> buscarPorTextoEQuestaoId(@RequestParam("texto") String texto, @RequestParam("questaoId") Long questaoId) {
        return service.buscarPorTextoEQuestaoId(texto, questaoId);
    }

    @GetMapping("/{id}")
    public Opcao buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }
}

package br.com.topicos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.topicos.entity.Aluno;
import br.com.topicos.service.AlunoService;

@RestController 
@CrossOrigin 
@RequestMapping("/aluno")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @GetMapping 
    public List<Aluno> listarTodos() {
        return service.listar();
    }
    
}

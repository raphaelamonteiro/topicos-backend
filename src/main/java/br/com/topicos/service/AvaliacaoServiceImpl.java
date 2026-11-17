package br.com.topicos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Avaliacao;
import br.com.topicos.repository.AvaliacaoRepository;

@Service 
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoRepository repo;

    private final DisciplinaService disciplinaService;

    public AvaliacaoServiceImpl(AvaliacaoRepository repo, DisciplinaService disciplinaService) {
        this.repo = repo;
        this.disciplinaService = disciplinaService;
    }

    @Override
    public Avaliacao cadastrar(Avaliacao avaliacao) {
        if(avaliacao == null ||
                avaliacao.getDisciplina() == null || 
                avaliacao.getDisciplina().getId() == null || 
                avaliacao.getTitulo() == null ||
                avaliacao.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avaliação inválida");
        }
        if(avaliacao.getDataHora() == null) {
            avaliacao.setDataHora(LocalDateTime.now());
        }
        avaliacao.setDisciplina(disciplinaService.buscarPorId(avaliacao.getDisciplina().getId()));
        return repo.save(avaliacao);
    }

    @Override
    public List<Avaliacao> listar() {
        return repo.findAll();
    }

    @Override
    public Avaliacao buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada")
        );
    }

    @Override
    public List<Avaliacao> listarPorDataHoraEDisciplinaCodigo(LocalDateTime dataHora, String disciplinaCodigo) {
        if(dataHora == null || disciplinaCodigo == null || disciplinaCodigo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data/hora e código da disciplina são obrigatórios");
        }
        return repo.findByDataHoraGreaterThanEqualAndDisciplinaCodigo(dataHora, disciplinaCodigo);
    }
    
}

package br.com.topicos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Questao;
import br.com.topicos.repository.QuestaoRepository;

@Service 
public class QuestaoServiceImpl implements QuestaoService {

    private final QuestaoRepository repo;

    private final AvaliacaoService avaliacaoService;

    public QuestaoServiceImpl(QuestaoRepository repo, AvaliacaoService avaliacaoService) {
        this.repo = repo;
        this.avaliacaoService = avaliacaoService;
    }

    @Override
    public Questao cadastrar(Questao questao) {
        if(questao == null ||
                questao.getEnunciado() == null || 
                questao.getEnunciado().isBlank() || 
                questao.getAvaliacao() == null || 
                questao.getAvaliacao().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Questão inválida");
        }
        if(questao.getDataCriacao() == null) {
            questao.setDataCriacao(LocalDate.now());
        }
        questao.setAvaliacao(avaliacaoService.buscarPorId(questao.getAvaliacao().getId()));
        return repo.save(questao);
    }

    @Override
    public List<Questao> listar() {
        return repo.findAll();
    }

    @Override
    public Questao buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questão não encontrada")
        );
    }

    @Override
    public List<Questao> listarPorAvaliacaoDataHoraEDisciplinaCodigo(LocalDateTime avaliacaoDataHora, String disciplinaCodigo) {
        if(avaliacaoDataHora == null || disciplinaCodigo == null || disciplinaCodigo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data/hora e código da disciplina são obrigatórios");
        }
        return repo.findByAvaliacaoDataHoraLessThanAndAvaliacaoDisciplinaCodigo(avaliacaoDataHora, disciplinaCodigo);
    }

    @Override
    public List<Questao> listarPorEnunciadoEIdAvaliacao(String enunciado, Long avaliacaoId) {
        if(enunciado == null || enunciado.isBlank()|| avaliacaoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enunciado e ID da avaliação são obrigatórios");
        }
        return repo.findByEnunciadoContainingIgnoreCaseAndAvaliacaoId(enunciado, avaliacaoId);
    }
    
}

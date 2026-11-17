package br.com.topicos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Instrucao;
import br.com.topicos.repository.InstrucaoRepository;

@Service 
public class InstrucaoServiceImpl implements InstrucaoService {

    private final InstrucaoRepository repo;

    private final AvaliacaoService avaliacaoService;

    public InstrucaoServiceImpl(InstrucaoRepository repo, AvaliacaoService avaliacaoService) {
        this.repo = repo;
        this.avaliacaoService = avaliacaoService;
    }

    @Override
    public Instrucao cadastrar(Instrucao instrucao) {
        if(instrucao == null ||
                instrucao.getTitulo() == null || instrucao.getTitulo().isBlank() ||
                instrucao.getOrdem() == null || instrucao.getOrdem() < 0 ||
                instrucao.getAvaliacao() == null || instrucao.getAvaliacao().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título, ordem e ID da avaliação são obrigatórios");
        }
        if(instrucao.getDataCriacao() == null) {
            instrucao.setDataCriacao(LocalDate.now());
        }
        instrucao.setAvaliacao(avaliacaoService.buscarPorId(instrucao.getAvaliacao().getId()));

        return repo.save(instrucao);
    }

    @Override
    public List<Instrucao> buscarPorOrdemEAvaliacaoId(Integer ordem, Long avaliacaoId) {
        if(ordem == null || avaliacaoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ordem e ID da avaliação são obrigatórios");
        }
        return repo.findByOrdemGreaterThanEqualAndAvaliacaoId(ordem, avaliacaoId);
    }

    @Override
    public Instrucao buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrução não encontrada")
        );
    }

    @Override
    public List<Instrucao> listar() {
        return repo.findAll();
    }
    
}

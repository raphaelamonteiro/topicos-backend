package br.com.topicos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Opcao;
import br.com.topicos.repository.OpcaoRepository;

@Service 
public class OpcaoServiceImpl implements OpcaoService {

    private final OpcaoRepository repo;

    private final QuestaoService questaoService;

    public OpcaoServiceImpl(OpcaoRepository repo, QuestaoService questaoService) {
        this.repo = repo;
        this.questaoService = questaoService;
    }

    @Override
    public Opcao cadastrar(Opcao opcao) {
        if(opcao == null ||
                opcao.getTexto() == null || opcao.getTexto().isBlank() ||
                opcao.getQuestao() == null || opcao.getQuestao().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Texto e ID da questão são obrigatórios");
        }
        if(opcao.getDataCriacao() == null) {
            opcao.setDataCriacao(LocalDate.now());
        }
        if(opcao.getCorreta() == null) {
            opcao.setCorreta(false);
        }
        opcao.setQuestao(questaoService.buscarPorId(opcao.getQuestao().getId()));

        return repo.save(opcao);
    }

    @Override
    public List<Opcao> buscarPorTextoEQuestaoId(String texto, Long questaoId) {
        if(texto == null || texto.isBlank() || questaoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Texto e ID da questão são obrigatórios");
        }
        return repo.findByTextoContainingIgnoreCaseAndQuestaoId(texto, questaoId);
    }

    @Override
    public Opcao buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opção não encontrada")
        );
    }

    @Override
    public List<Opcao> listar() {
        return repo.findAll();
    }
    
}

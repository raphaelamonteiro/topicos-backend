package br.com.topicos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.topicos.entity.Auditoria;
import br.com.topicos.repository.AuditoriaRepository;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository repo;
    private final CursoService cursoService;

    public AuditoriaServiceImpl(AuditoriaRepository repo, CursoService cursoService) {
        this.repo = repo;
        this.cursoService = cursoService;
    }

    @Override
    @Transactional
    public Auditoria cadastrar(Auditoria auditoria) {

        if (auditoria == null ||
                auditoria.getId() != null ||
                auditoria.getAudNomeAntigo() == null ||
                auditoria.getAudNomeAntigo().isBlank() ||
                auditoria.getAudNomeNovo() == null ||
                auditoria.getAudNomeNovo().isBlank() ||
                auditoria.getCurso() == null ||
                auditoria.getCurso().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos");
        }

        if (auditoria.getAudDataHora() == null) {
            auditoria.setAudDataHora(LocalDateTime.now());
        }

        if (auditoria.getAudNomeAntigo().equals(auditoria.getAudNomeNovo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O nome novo deve ser diferente do antigo");
        }

        if (auditoria.getAudNomeNovo().length() <= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O nome novo deve ter mais de 5 caracteres");
        }

        if (!Character.isUpperCase(auditoria.getAudNomeNovo().charAt(0))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O nome novo deve começar com letra maiúscula");
        }

        if (auditoria.getAudAutorizacao() != null && auditoria.getAudAutorizacao() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O código de autorização deve ser maior que 0");
        }

        auditoria.setCurso(cursoService.buscarPorId(auditoria.getCurso().getId()));

        return repo.save(auditoria);
    }

    @Override
    public List<Auditoria> listar() {
        return repo.findAll();
    }

    @Override
    public Auditoria buscarPorId(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id da auditoria não pode ser nulo");
        }
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auditoria não encontrada"));
    }

    @Override
    public List<Auditoria> buscarPorNomeAntigoECurso(String nomeAntigo, String nomeCurso) {
        if (nomeAntigo == null || nomeAntigo.isBlank() ||
                nomeCurso == null || nomeCurso.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetros inválidos");
        }
        return repo.buscarPorNomeAntigoECurso(nomeAntigo, nomeCurso);
    }
}
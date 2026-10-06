package br.com.topicos.service;

import java.util.List;

import br.com.topicos.entity.Auditoria;

public interface AuditoriaService {

    public Auditoria cadastrar(Auditoria auditoria);

    public List<Auditoria> listar();

    public Auditoria buscarPorId(Long id);

}
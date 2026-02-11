package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.PessoaJuridicaAtividadeCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PessoaJuridicaAtividadeCategoriaRepository extends JpaRepository<PessoaJuridicaAtividadeCategoria, String> {
    List<PessoaJuridicaAtividadeCategoria> findAllByIsActiveTrueOrderBySortOrderAscLabelAsc();
}

package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.PessoaJuridicaPorteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PessoaJuridicaPorteEmpresaRepository extends JpaRepository<PessoaJuridicaPorteEmpresa, String> {
    List<PessoaJuridicaPorteEmpresa> findAllByIsActiveTrueOrderBySortOrderAscLabelAsc();
}

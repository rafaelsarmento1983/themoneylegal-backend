package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.PessoaJuridicaNaturezaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PessoaJuridicaNaturezaJuridicaRepository extends JpaRepository<PessoaJuridicaNaturezaJuridica, String> {
    List<PessoaJuridicaNaturezaJuridica> findAllByIsActiveTrueOrderBySortOrderAscLabelAsc();
}

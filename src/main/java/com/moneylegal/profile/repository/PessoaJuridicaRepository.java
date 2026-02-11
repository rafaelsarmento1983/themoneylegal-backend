package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.PessoaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, String> {

    Optional<PessoaJuridica> findByProfileId(String profileId);

    boolean existsByCnpj(String cnpj);

    void deleteByProfileId(String profileId);
}

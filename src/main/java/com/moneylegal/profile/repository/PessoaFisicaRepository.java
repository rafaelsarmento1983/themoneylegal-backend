package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, String> {

    Optional<PessoaFisica> findByProfileId(String profileId);

    boolean existsByCpf(String cpf);

    void deleteByProfileId(String profileId);
}


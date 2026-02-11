package com.moneylegal.profile.dto;

import com.moneylegal.profile.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {

    private String id;
    private String userId;
    private String tipo; // PESSOA_FISICA ou PESSOA_JURIDICA
    private String slug;
    private String avatarUrl;
    private Boolean isCompleted;

    // Dados de Pessoa Física (se aplicável)
    private PessoaFisicaDTO pessoaFisica;

    // Dados de Pessoa Jurídica (se aplicável)
    private PessoaJuridicaDTO pessoaJuridica;

    // Endereço
    private AddressDTO address;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PessoaFisicaDTO {
        private String nomeCompleto;
        private String cpf;
        private LocalDate dataNascimento;
        private String telefone;

        public static PessoaFisicaDTO fromEntity(PessoaFisica entity) {
            if (entity == null) return null;
            return PessoaFisicaDTO.builder()
                    .nomeCompleto(entity.getNomeCompleto())
                    .cpf(entity.getCpf())
                    .dataNascimento(entity.getDataNascimento())
                    .telefone(entity.getTelefone())
                    .build();
        }
    }

    /**
     * ✅ Lookup resolvido (id + label + icon)
     * Usado em: porte, natureza, atividade (categoria/item)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LookupResolvedDTO {
        private String id;
        private String label;
        private String icon;

        public static LookupResolvedDTO of(String id, String label, String icon) {
            if (id == null && label == null && icon == null) return null;
            return LookupResolvedDTO.builder()
                    .id(id)
                    .label(label)
                    .icon(icon)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PessoaJuridicaDTO {

        // campos originais
        private String razaoSocial;
        private String nomeFantasia;
        private String cnpj;
        private String inscricaoEstadual;
        private String inscricaoMunicipal;
        private LocalDate dataFundacao;

        // ✅ NOVO: ids gravados na PJ
        private String porteEmpresaId;
        private String naturezaJuridicaId;
        private String atividadeItemId;

        // ✅ NOVO: resolvidos (label/icon)
        private LookupResolvedDTO porteEmpresa;
        private LookupResolvedDTO naturezaJuridica;

        // ✅ NOVO: atividade é categoria + item (ambos com label/icon)
        private LookupResolvedDTO atividadeCategoria;
        private LookupResolvedDTO atividadeItem;

        private String telefone;
        private String nomeResponsavel;
        private String emailResponsavel;

        /**
         * Monta o DTO a partir da entity PJ + lookups já resolvidos (opcionalmente nulos).
         */
        public static PessoaJuridicaDTO fromEntity(
                PessoaJuridica pj,
                PessoaJuridicaPorteEmpresa porte,
                PessoaJuridicaNaturezaJuridica natureza,
                PessoaJuridicaAtividadeCategoria atividadeCategoria,
                PessoaJuridicaAtividadeItem atividadeItem
        ) {
            if (pj == null) return null;

            return PessoaJuridicaDTO.builder()
                    .razaoSocial(pj.getRazaoSocial())
                    .nomeFantasia(pj.getNomeFantasia())
                    .cnpj(pj.getCnpj())
                    .inscricaoEstadual(pj.getInscricaoEstadual())
                    .inscricaoMunicipal(pj.getInscricaoMunicipal())
                    .dataFundacao(pj.getDataFundacao())

                    // IDs
                    .porteEmpresaId(pj.getPorteEmpresaId())
                    .naturezaJuridicaId(pj.getNaturezaJuridicaId())
                    .atividadeItemId(pj.getAtividadeItemId())

                    // Resolvidos (label/icon)
                    .porteEmpresa(porte != null ? LookupResolvedDTO.of(porte.getId(), porte.getLabel(), porte.getIcon()) : null)
                    .naturezaJuridica(natureza != null ? LookupResolvedDTO.of(natureza.getId(), natureza.getLabel(), natureza.getIcon()) : null)

                    .atividadeCategoria(atividadeCategoria != null ? LookupResolvedDTO.of(atividadeCategoria.getId(), atividadeCategoria.getLabel(), atividadeCategoria.getIcon()) : null)
                    .atividadeItem(atividadeItem != null ? LookupResolvedDTO.of(atividadeItem.getId(), atividadeItem.getLabel(), atividadeItem.getIcon()) : null)

                    .telefone(pj.getTelefone())
                    .nomeResponsavel(pj.getNomeResponsavel())
                    .emailResponsavel(pj.getEmailResponsavel())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private String cep;
        private String logradouro;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String estado;
        private String pais;

        public static AddressDTO fromEntity(Address entity) {
            if (entity == null) return null;
            return AddressDTO.builder()
                    .cep(entity.getCep())
                    .logradouro(entity.getLogradouro())
                    .numero(entity.getNumero())
                    .complemento(entity.getComplemento())
                    .bairro(entity.getBairro())
                    .cidade(entity.getCidade())
                    .estado(entity.getEstado())
                    .pais(entity.getPais())
                    .build();
        }
    }
}

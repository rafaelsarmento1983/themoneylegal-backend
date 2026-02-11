package com.moneylegal.profile.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompletePessoaJuridicaRequestDTO {

    @NotBlank(message = "Razão social é obrigatória")
    @Size(min = 3, max = 200, message = "Razão social deve ter entre 3 e 200 caracteres")
    private String razaoSocial;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ inválido. Use o formato: 12.345.678/0001-90")
    private String cnpj;

    private String inscricaoEstadual;
    private String inscricaoMunicipal;

    @Past(message = "Data de fundação deve ser no passado")
    private LocalDate dataFundacao;

    // ✅ agora são IDs
    @NotBlank(message = "Porte da empresa é obrigatório")
    private String porteEmpresaId;
    @NotBlank(message = "Natureza jurídica é obrigatória")
    private String naturezaJuridicaId;
    @NotBlank(message = "Atividade principal é obrigatória")
    private String atividadeItemId;

    @Pattern(
            regexp = "^$|\\(\\d{2}\\) \\d{4,5}-\\d{4}$",
            message = "Telefone inválido. Use o formato: (11) 98765-4321"
    )
    private String telefone;

    @NotBlank(message = "Nome do responsável é obrigatório")
    private String nomeResponsavel;

    @NotBlank(message = "Email do responsável é obrigatório")
    @Email(message = "Email do responsável inválido")
    private String emailResponsavel;
}

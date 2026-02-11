package com.moneylegal.profile.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompletePessoaFisicaRequestDTO {
    
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    private String nomeCompleto;
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF inválido. Use o formato: 123.456.789-00")
    private String cpf;
    
    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    @Pattern(
            regexp = "^$|\\(\\d{2}\\) \\d{4,5}-\\d{4}$",
            message = "Telefone inválido. Use o formato: (11) 98765-4321"
    )
    private String telefone;
}

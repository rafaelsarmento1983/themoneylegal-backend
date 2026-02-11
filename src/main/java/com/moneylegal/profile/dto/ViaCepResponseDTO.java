package com.moneylegal.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViaCepResponseDTO {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade; // cidade
    private String uf; // estado
    private String ibge;
    private String gia;
    private String ddd;
    private String siafi;
    private Boolean erro; // true se CEP não encontrado
}

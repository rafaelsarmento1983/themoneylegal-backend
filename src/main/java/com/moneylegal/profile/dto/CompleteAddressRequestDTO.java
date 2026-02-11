package com.moneylegal.profile.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CompleteAddressRequestDTO {
    
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP inválido. Use o formato: 12345-678")
    private String cep;
    
    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200)
    private String logradouro;
    
    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20)
    private String numero;
    
    @Size(max = 100)
    private String complemento;
    
    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100)
    private String bairro;
    
    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100)
    private String cidade;
    
    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
    private String estado;
    
    @Size(max = 50)
    private String pais = "Brasil";
}

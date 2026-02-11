package com.moneylegal.profile.service;

import com.moneylegal.profile.dto.ViaCepResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViaCepService {
    
    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";
    
    private final RestTemplate restTemplate;
    
    public ViaCepResponseDTO consultarCep(String cep) {
        try {
            String cepLimpo = cep.replaceAll("\\D", "");
            
            log.info("Consultando CEP: {}", cepLimpo);
            
            ViaCepResponseDTO response = restTemplate.getForObject(
                    VIACEP_URL,
                    ViaCepResponseDTO.class,
                    cepLimpo
            );
            
            if (response != null && Boolean.TRUE.equals(response.getErro())) {
                throw new RuntimeException("CEP não encontrado");
            }
            
            log.info("CEP consultado com sucesso: {}", cepLimpo);
            return response;
            
        } catch (Exception e) {
            log.error("Erro ao consultar CEP: {}", cep, e);
            throw new RuntimeException("Erro ao consultar CEP: " + e.getMessage());
        }
    }
}

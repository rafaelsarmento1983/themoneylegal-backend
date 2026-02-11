package com.moneylegal.profile.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentValidationService {
    
    /**
     * Valida CPF (Cadastro de Pessoa Física)
     */
    public void validateCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        
        if (cpfLimpo.length() != 11) {
            throw new RuntimeException("CPF deve conter 11 dígitos");
        }
        
        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            throw new RuntimeException("CPF inválido");
        }
        
        // Calcula primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (10 - i);
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 >= 10) digito1 = 0;
        
        // Calcula segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (11 - i);
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 >= 10) digito2 = 0;
        
        // Verifica se os dígitos calculados conferem
        if (Character.getNumericValue(cpfLimpo.charAt(9)) != digito1 ||
                Character.getNumericValue(cpfLimpo.charAt(10)) != digito2) {
            throw new RuntimeException("CPF inválido");
        }
        
        log.info("CPF validado com sucesso");
    }
    
    /**
     * Valida CNPJ (Cadastro Nacional de Pessoa Jurídica)
     */
    public void validateCnpj(String cnpj) {
        String cnpjLimpo = cnpj.replaceAll("\\D", "");
        
        if (cnpjLimpo.length() != 14) {
            throw new RuntimeException("CNPJ deve conter 14 dígitos");
        }
        
        // Verifica se todos os dígitos são iguais
        if (cnpjLimpo.matches("(\\d)\\1{13}")) {
            throw new RuntimeException("CNPJ inválido");
        }
        
        // Calcula primeiro dígito verificador
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos1[i];
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 >= 10) digito1 = 0;
        
        // Calcula segundo dígito verificador
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos2[i];
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 >= 10) digito2 = 0;
        
        // Verifica se os dígitos calculados conferem
        if (Character.getNumericValue(cnpjLimpo.charAt(12)) != digito1 ||
                Character.getNumericValue(cnpjLimpo.charAt(13)) != digito2) {
            throw new RuntimeException("CNPJ inválido");
        }
        
        log.info("CNPJ validado com sucesso");
    }
}

package com.moneylegal.profile.service;

import com.moneylegal.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlugService {
    
    private final ProfileRepository profileRepository;
    
    public String generateUniqueSlug(String text) {
        String baseSlug = generateSlug(text);
        String slug = baseSlug;
        int counter = 1;
        
        // Garantir unicidade
        while (profileRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        log.info("Slug gerado: {} (base: {})", slug, baseSlug);
        return slug;
    }
    
    private String generateSlug(String text) {
        if (text == null || text.isBlank()) {
            return "usuario";
        }
        
        // Remover acentos
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalized).replaceAll("");
        
        // Converter para lowercase e substituir espaços por hífens
        String slug = withoutAccents
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "") // Remover caracteres especiais
                .replaceAll("\\s+", "-") // Substituir espaços por hífens
                .replaceAll("-+", "-"); // Remover hífens duplicados
        
        // Limitar tamanho
        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
        }
        
        // Remover hífen no início/fim
        slug = slug.replaceAll("^-|-$", "");
        
        return slug.isEmpty() ? "usuario" : slug;
    }
}

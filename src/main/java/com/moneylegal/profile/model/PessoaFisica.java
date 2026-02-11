package com.moneylegal.profile.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pessoa_fisica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaFisica {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "profile_id", unique = true, nullable = false)
    private String profileId; // FK para Profile

    @Column(name = "nome_completo", nullable = false, length = 200)
    private String nomeCompleto;

    @Column(name = "cpf", unique = true, nullable = false, length = 14)
    private String cpf; // Formato: 123.456.789-00

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "telefone", length = 20)
    private String telefone; // Formato: (11) 98765-4321

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

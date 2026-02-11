package com.moneylegal.profile.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pessoa_juridica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaJuridica {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "profile_id", unique = true, nullable = false)
    private String profileId;

    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Column(name = "cnpj", unique = true, nullable = false, length = 18)
    private String cnpj;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Column(name = "inscricao_municipal", length = 20)
    private String inscricaoMunicipal;

    @Column(name = "data_fundacao")
    private LocalDate dataFundacao;

    // ✅ IDs de lookup (no banco: CHAR(36))
    @Column(name = "porte_empresa_id", columnDefinition = "CHAR(36)")
    private String porteEmpresaId;

    @Column(name = "natureza_juridica_id", columnDefinition = "CHAR(36)")
    private String naturezaJuridicaId;

    @Column(name = "atividade_item_id", columnDefinition = "CHAR(36)")
    private String atividadeItemId;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "nome_responsavel", length = 200)
    private String nomeResponsavel;

    @Column(name = "email_responsavel", length = 100)
    private String emailResponsavel;

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

package com.moneylegal.profile.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pessoa_juridica_atividade_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaJuridicaAtividadeItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "category_id", nullable = false, columnDefinition = "CHAR(36)")
    private String categoryId;

    @Column(name = "label", nullable = false, length = 220)
    private String label;

    @Column(name = "icon", length = 120)
    private String icon;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

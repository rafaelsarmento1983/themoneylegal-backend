package com.moneylegal.profile.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId; // Relacionamento 1:1 com User

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private TipoCadastro tipo;

    @Column(name = "slug", unique = true, length = 100)
    private String slug;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TipoCadastro {
        PESSOA_FISICA,
        PESSOA_JURIDICA
    }

    public boolean isPessoaFisica() {
        return TipoCadastro.PESSOA_FISICA.equals(this.tipo);
    }

    public boolean isPessoaJuridica() {
        return TipoCadastro.PESSOA_JURIDICA.equals(this.tipo);
    }
}

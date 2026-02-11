-- V6: Tabela de Profiles
CREATE TABLE profiles (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    tipo ENUM('PESSOA_FISICA', 'PESSOA_JURIDICA'),
    slug VARCHAR(100) UNIQUE,
    avatar_url VARCHAR(500),
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_slug (slug),
    INDEX idx_tipo (tipo),
    
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

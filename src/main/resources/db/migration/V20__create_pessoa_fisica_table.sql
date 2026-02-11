-- V7: Tabela de Pessoa Física
CREATE TABLE pessoa_fisica (
    id VARCHAR(36) PRIMARY KEY,
    profile_id VARCHAR(36) NOT NULL UNIQUE,
    nome_completo VARCHAR(200) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    
    INDEX idx_profile_id (profile_id),
    INDEX idx_cpf (cpf),
    
    CONSTRAINT fk_pessoa_fisica_profile FOREIGN KEY (profile_id) 
        REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

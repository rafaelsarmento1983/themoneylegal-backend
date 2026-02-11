-- V9: Tabela de Endereços
CREATE TABLE addresses (
    id VARCHAR(36) PRIMARY KEY,
    profile_id VARCHAR(36) NOT NULL UNIQUE,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(200) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(100),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    pais VARCHAR(50) NOT NULL DEFAULT 'Brasil',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    
    INDEX idx_profile_id (profile_id),
    INDEX idx_cep (cep),
    INDEX idx_cidade_estado (cidade, estado),
    
    CONSTRAINT fk_address_profile FOREIGN KEY (profile_id) 
        REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

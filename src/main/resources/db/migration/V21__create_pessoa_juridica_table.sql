-- V8: Tabela de Pessoa Jurídica
CREATE TABLE pessoa_juridica (
    id VARCHAR(36) PRIMARY KEY,
    profile_id VARCHAR(36) NOT NULL UNIQUE,
    razao_social VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200),
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(20),
    inscricao_municipal VARCHAR(20),
    data_fundacao DATE,
    porte_empresa VARCHAR(50),
    natureza_juridica VARCHAR(100),
    atividade_principal VARCHAR(200),
    telefone VARCHAR(20),
    nome_responsavel VARCHAR(200),
    email_responsavel VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    
    INDEX idx_profile_id (profile_id),
    INDEX idx_cnpj (cnpj),
    
    CONSTRAINT fk_pessoa_juridica_profile FOREIGN KEY (profile_id) 
        REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

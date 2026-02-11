-- Sprint 1: Tabela de Refresh Tokens
-- Tokens de longa duração para renovar access tokens

CREATE TABLE refresh_tokens (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID do token',
    user_id VARCHAR(36) NOT NULL COMMENT 'Usuário dono do token',
    token VARCHAR(500) NOT NULL UNIQUE COMMENT 'Token de refresh',
    expires_at DATETIME(6) NOT NULL COMMENT 'Data de expiração',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data de criação',
    revoked BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Token foi revogado?',
    
    -- Foreign Keys
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    -- Índices
    INDEX idx_refresh_tokens_user (user_id),
    INDEX idx_refresh_tokens_expires (expires_at),
    INDEX idx_refresh_tokens_token (token)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Refresh tokens para renovação de sessão';

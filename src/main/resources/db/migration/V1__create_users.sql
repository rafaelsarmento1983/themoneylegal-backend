-- Sprint 1: Tabela de Usuários
-- Compatível com MySQL 8.0.31 e Hibernate 6

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID do usuário',
    name VARCHAR(100) NOT NULL COMMENT 'Nome completo',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Email único para login',
    password_hash VARCHAR(255) COMMENT 'Hash BCrypt da senha',
    phone VARCHAR(20) COMMENT 'Telefone com DDD',
    avatar_url VARCHAR(500) COMMENT 'URL da foto de perfil',
    
    -- Verificações
    email_verified BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Email foi verificado?',
    phone_verified BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Telefone foi verificado?',
    is_active BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Conta está ativa?',
    
    -- OAuth
    oauth_provider VARCHAR(20) COMMENT 'GOOGLE, FACEBOOK, APPLE',
    oauth_provider_id VARCHAR(255) COMMENT 'ID do usuário no provedor OAuth',
    
    -- Timestamps (DATETIME(6) para compatibilidade com Hibernate)
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data de criação',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Última atualização',
    last_login DATETIME(6) NULL COMMENT 'Último login realizado',
    
    -- Índices para performance
    INDEX idx_users_email (email),
    INDEX idx_users_phone (phone),
    INDEX idx_users_is_active (is_active),
    INDEX idx_users_created_at (created_at)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Usuários do sistema - pode pertencer a múltiplos tenants';

-- Sprint 1: Tabela de Tenants (Workspaces)
-- PERSONAL, FAMILY ou BUSINESS

CREATE TABLE tenants (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID do tenant',
    name VARCHAR(100) NOT NULL COMMENT 'Nome do workspace',
    slug VARCHAR(100) NOT NULL UNIQUE COMMENT 'Slug único para URL',
    type VARCHAR(20) NOT NULL DEFAULT 'PERSONAL' COMMENT 'PERSONAL, FAMILY, BUSINESS',
    plan VARCHAR(20) NOT NULL DEFAULT 'FREE' COMMENT 'FREE, PREMIUM, ENTERPRISE',
    owner_id VARCHAR(36) NOT NULL COMMENT 'Dono do tenant',
    
    -- Subscription
    subscription_status VARCHAR(20) DEFAULT 'TRIAL' COMMENT 'TRIAL, ACTIVE, CANCELLED, EXPIRED, SUSPENDED',
    subscription_started_at DATETIME(6) NULL COMMENT 'Início da assinatura',
    subscription_expires_at DATETIME(6) NULL COMMENT 'Expiração da assinatura',
    
    -- Limits
    max_members INT NULL COMMENT 'Máximo de membros (NULL = ilimitado)',
    max_accounts INT NULL COMMENT 'Máximo de contas (NULL = ilimitado)',
    max_budgets INT NULL COMMENT 'Máximo de orçamentos (NULL = ilimitado)',
    
    -- Branding
    logo_url VARCHAR(500) COMMENT 'URL do logo customizado',
    primary_color VARCHAR(7) COMMENT 'Cor primária (#RRGGBB)',
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Tenant está ativo?',
    
    -- Timestamps
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    
    -- Foreign Keys
    CONSTRAINT fk_tenants_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE RESTRICT,
    
    -- Índices
    INDEX idx_tenants_slug (slug),
    INDEX idx_tenants_owner (owner_id),
    INDEX idx_tenants_plan (plan),
    INDEX idx_tenants_is_active (is_active)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Workspaces (família, empresa, pessoal)';

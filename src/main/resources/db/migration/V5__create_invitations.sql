-- Sprint 1: Tabela de Convites
-- Convites para usuários entrarem em tenants

CREATE TABLE invitations (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID do convite',
    tenant_id VARCHAR(36) NOT NULL COMMENT 'Tenant que está convidando',
    email VARCHAR(255) NOT NULL COMMENT 'Email do convidado',
    code VARCHAR(8) NOT NULL UNIQUE COMMENT 'Código de 8 caracteres',
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT 'Role que será atribuída',
    invited_by VARCHAR(36) NOT NULL COMMENT 'Quem enviou o convite',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, ACCEPTED, EXPIRED, CANCELLED',
    message TEXT COMMENT 'Mensagem personalizada do convite',
    
    -- Timestamps
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data de criação',
    expires_at DATETIME(6) NOT NULL COMMENT 'Data de expiração (7 dias)',
    accepted_at DATETIME(6) NULL COMMENT 'Data de aceitação',
    
    -- Foreign Keys
    CONSTRAINT fk_invitations_tenant FOREIGN KEY (tenant_id) 
        REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_invitations_invited_by FOREIGN KEY (invited_by) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    -- Índices
    INDEX idx_invitations_tenant (tenant_id),
    INDEX idx_invitations_email (email),
    INDEX idx_invitations_code (code),
    INDEX idx_invitations_status (status),
    INDEX idx_invitations_expires (expires_at)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Convites para entrar em tenants';

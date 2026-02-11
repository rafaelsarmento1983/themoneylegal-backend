-- Sprint 1: Tabela de Membros dos Tenants
-- Relacionamento N:N entre Users e Tenants com Role

CREATE TABLE tenant_members (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID do membership',
    tenant_id VARCHAR(36) NOT NULL COMMENT 'Tenant ao qual pertence',
    user_id VARCHAR(36) NOT NULL COMMENT 'Usuário membro',
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT 'OWNER, ADMIN, MANAGER, MEMBER, VIEWER',
    is_active BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Membership está ativo?',
    joined_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data de entrada',
    
    -- Foreign Keys
    CONSTRAINT fk_tenant_members_tenant FOREIGN KEY (tenant_id) 
        REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant_members_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    -- Índices
    UNIQUE INDEX idx_tenant_members_unique (tenant_id, user_id),
    INDEX idx_tenant_members_tenant (tenant_id),
    INDEX idx_tenant_members_user (user_id),
    INDEX idx_tenant_members_role (role),
    INDEX idx_tenant_members_is_active (is_active)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Membros dos tenants com suas roles';

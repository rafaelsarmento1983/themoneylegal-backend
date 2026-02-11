-- Migração: Adicionar tabela access_requests + ajustes em tenants.slug e índices
-- Data: 21/01/2026
-- Versão: 2.0.0
-- Compatível com MySQL 8.x (sem CREATE INDEX IF NOT EXISTS)

-- Criar tabela de solicitações de acesso
CREATE TABLE IF NOT EXISTS access_requests (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_access_request_tenant FOREIGN KEY (tenant_id)
    REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_access_request_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_access_requests_tenant_id (tenant_id),
    INDEX idx_access_requests_user_id (user_id),
    INDEX idx_access_requests_status (status),
    INDEX idx_access_requests_tenant_user (tenant_id, user_id),
    INDEX idx_access_requests_created_at (created_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Mensagem de sucesso
SELECT 'Migration completed successfully!' AS status;

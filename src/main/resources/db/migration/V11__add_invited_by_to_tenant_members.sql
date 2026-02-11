ALTER TABLE tenant_members
    ADD COLUMN invited_by VARCHAR(36) COLLATE utf8mb4_unicode_ci NULL;

ALTER TABLE tenant_members
    ADD CONSTRAINT fk_tenant_members_invited_by
        FOREIGN KEY (invited_by) REFERENCES users(id);

ALTER TABLE tenant_members
    MODIFY COLUMN role ENUM('VIEWER','MEMBER','MANAGER','ADMIN','OWNER') NOT NULL;

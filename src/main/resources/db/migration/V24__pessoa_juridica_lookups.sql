-- V24__pessoa_juridica_lookups.sql
-- Lookups PJ: porte, natureza, atividade (categoria + item)
-- MySQL 8.x

/* =========================================================
   0) Helper: tipos UUID string padronizados (ASCII)
   ========================================================= */
-- Padrão: VARCHAR(36) ASCII (UUID com hífen)

/* =========================================================
   1) Tabelas
   ========================================================= */

CREATE TABLE IF NOT EXISTS pessoa_juridica_porte_empresa (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    label VARCHAR(120) NOT NULL,
    icon VARCHAR(120) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pj_porte_label (label),
    KEY idx_pj_porte_active_sort (is_active, sort_order)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS pessoa_juridica_natureza_juridica (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    label VARCHAR(160) NOT NULL,
    icon VARCHAR(120) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pj_natureza_label (label),
    KEY idx_pj_natureza_active_sort (is_active, sort_order)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS pessoa_juridica_atividade_categoria (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    label VARCHAR(200) NOT NULL,
    icon VARCHAR(120) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pj_atv_cat_label (label),
    KEY idx_pj_atv_cat_active_sort (is_active, sort_order)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS pessoa_juridica_atividade_item (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    category_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
    label VARCHAR(220) NOT NULL,
    icon VARCHAR(120) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pj_atv_item_cat_label (category_id, label),
    KEY idx_pj_atv_item_cat (category_id),
    KEY idx_pj_atv_item_active_sort (is_active, sort_order)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


/* =========================================================
   2) Garante colunas na pessoa_juridica (UUID ASCII)
   ========================================================= */

-- porte_empresa_id
SET @col := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND column_name = 'porte_empresa_id'
);
SET @sql := IF(@col = 0,
  'ALTER TABLE pessoa_juridica ADD COLUMN porte_empresa_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NULL',
  'ALTER TABLE pessoa_juridica MODIFY COLUMN porte_empresa_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- natureza_juridica_id
SET @col := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND column_name = 'natureza_juridica_id'
);
SET @sql := IF(@col = 0,
  'ALTER TABLE pessoa_juridica ADD COLUMN natureza_juridica_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NULL',
  'ALTER TABLE pessoa_juridica MODIFY COLUMN natureza_juridica_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- atividade_item_id
SET @col := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND column_name = 'atividade_item_id'
);
SET @sql := IF(@col = 0,
  'ALTER TABLE pessoa_juridica ADD COLUMN atividade_item_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NULL',
  'ALTER TABLE pessoa_juridica MODIFY COLUMN atividade_item_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- Índices (condicionais)
SET @idx1 := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND index_name = 'idx_pj_porte_empresa_id'
);
SET @sql := IF(@idx1 = 0, 'CREATE INDEX idx_pj_porte_empresa_id ON pessoa_juridica(porte_empresa_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx2 := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND index_name = 'idx_pj_natureza_juridica_id'
);
SET @sql := IF(@idx2 = 0, 'CREATE INDEX idx_pj_natureza_juridica_id ON pessoa_juridica(natureza_juridica_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx3 := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND index_name = 'idx_pj_atividade_item_id'
);
SET @sql := IF(@idx3 = 0, 'CREATE INDEX idx_pj_atividade_item_id ON pessoa_juridica(atividade_item_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


/* =========================================================
   3) Foreign Keys (condicionais)
   ========================================================= */

-- item -> categoria
SET @fk := (
  SELECT COUNT(*) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'pessoa_juridica_atividade_item'
    AND constraint_name = 'fk_pj_atv_item_categoria'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE pessoa_juridica_atividade_item
     ADD CONSTRAINT fk_pj_atv_item_categoria
     FOREIGN KEY (category_id)
     REFERENCES pessoa_juridica_atividade_categoria(id)
     ON DELETE RESTRICT ON UPDATE CASCADE',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- pessoa_juridica -> porte
SET @fk := (
  SELECT COUNT(*) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND constraint_name = 'fk_pj_porte'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE pessoa_juridica
     ADD CONSTRAINT fk_pj_porte
     FOREIGN KEY (porte_empresa_id)
     REFERENCES pessoa_juridica_porte_empresa(id)
     ON DELETE RESTRICT ON UPDATE CASCADE',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- pessoa_juridica -> natureza
SET @fk := (
  SELECT COUNT(*) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND constraint_name = 'fk_pj_natureza'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE pessoa_juridica
     ADD CONSTRAINT fk_pj_natureza
     FOREIGN KEY (natureza_juridica_id)
     REFERENCES pessoa_juridica_natureza_juridica(id)
     ON DELETE RESTRICT ON UPDATE CASCADE',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- pessoa_juridica -> atividade_item
SET @fk := (
  SELECT COUNT(*) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'pessoa_juridica'
    AND constraint_name = 'fk_pj_atividade_item'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE pessoa_juridica
     ADD CONSTRAINT fk_pj_atividade_item
     FOREIGN KEY (atividade_item_id)
     REFERENCES pessoa_juridica_atividade_item(id)
     ON DELETE RESTRICT ON UPDATE CASCADE',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


/* =========================================================
   4) Seeds (idempotentes por LABEL/UNIQUE)
   ========================================================= */

-- -------------------------
-- 4.1) Porte empresa (sigla + nome completo)
-- -------------------------
INSERT INTO pessoa_juridica_porte_empresa (id, label, icon, is_active, sort_order)
SELECT UUID(), 'MEI - Microempreendedor Individual', '🧾', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_porte_empresa WHERE label='MEI - Microempreendedor Individual');

INSERT INTO pessoa_juridica_porte_empresa (id, label, icon, is_active, sort_order)
SELECT UUID(), 'ME - Microempresa', '🏪', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_porte_empresa WHERE label='ME - Microempresa');

INSERT INTO pessoa_juridica_porte_empresa (id, label, icon, is_active, sort_order)
SELECT UUID(), 'EPP - Empresa de Pequeno Porte', '🏢', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_porte_empresa WHERE label='EPP - Empresa de Pequeno Porte');

INSERT INTO pessoa_juridica_porte_empresa (id, label, icon, is_active, sort_order)
SELECT UUID(), 'DEMAIS - Média/Grande empresa', '🏭', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_porte_empresa WHERE label='DEMAIS - Média/Grande empresa');


-- -------------------------
-- 4.2) Natureza jurídica (sigla + nome completo)
-- -------------------------
INSERT INTO pessoa_juridica_natureza_juridica (id, label, icon, is_active, sort_order)
SELECT UUID(), 'LTDA - Sociedade Limitada', '📄', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_natureza_juridica WHERE label='LTDA - Sociedade Limitada');

INSERT INTO pessoa_juridica_natureza_juridica (id, label, icon, is_active, sort_order)
SELECT UUID(), 'SLU - Sociedade Limitada Unipessoal', '🧑‍💼', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_natureza_juridica WHERE label='SLU - Sociedade Limitada Unipessoal');

INSERT INTO pessoa_juridica_natureza_juridica (id, label, icon, is_active, sort_order)
SELECT UUID(), 'EI - Empresário Individual', '👤', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_natureza_juridica WHERE label='EI - Empresário Individual');

INSERT INTO pessoa_juridica_natureza_juridica (id, label, icon, is_active, sort_order)
SELECT UUID(), 'S/A - Sociedade Anônima', '🏦', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_natureza_juridica WHERE label='S/A - Sociedade Anônima');


/* =========================================================
   4.3) Atividades: Categorias + Itens
   - Categoria: pega por label; se não existir, cria UUID()
   - Itens: protegidos por UNIQUE (category_id, label)
   ========================================================= */

/* ---------- Categoria: Tecnologia e Internet (💻) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Tecnologia e Internet' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Tecnologia e Internet', '💻', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Tecnologia e Internet');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Desenvolvimento de software / Programação', '⌨️', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Desenvolvimento de software / Programação');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Consultoria em tecnologia da informação (TI)', '🧠', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Consultoria em tecnologia da informação (TI)');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Suporte técnico / Help desk', '🛠️', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Suporte técnico / Help desk');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Hospedagem de sites e serviços em nuvem', '☁️', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Hospedagem de sites e serviços em nuvem');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Criação de sites e design digital', '🎨', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Criação de sites e design digital');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Marketing digital', '📣', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Marketing digital');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Produção de conteúdo / Influencer / YouTube', '🎥', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Produção de conteúdo / Influencer / YouTube');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Portais, provedores de conteúdo e aplicativos', '📱', 1, 80
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Portais, provedores de conteúdo e aplicativos');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Segurança da informação', '🔐', 1, 90
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Segurança da informação');


/* ---------- Categoria: Comércio (lojas e vendas) (🛍️) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Comércio (lojas e vendas)' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Comércio (lojas e vendas)', '🛍️', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Comércio (lojas e vendas)');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Comércio varejista de roupas e acessórios', '👕', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Comércio varejista de roupas e acessórios');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Comércio varejista de produtos diversos (loja online)', '🛒', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Comércio varejista de produtos diversos (loja online)');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Comércio de eletrônicos e informática', '🖥️', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Comércio de eletrônicos e informática');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Comércio de cosméticos e perfumaria', '🧴', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Comércio de cosméticos e perfumaria');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Comércio de alimentos e bebidas', '🥤', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Comércio de alimentos e bebidas');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Comércio de móveis e decoração', '🛋️', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Comércio de móveis e decoração');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'E-commerce / Loja virtual', '🌐', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='E-commerce / Loja virtual');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Distribuidora / Atacado', '📦', 1, 80
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Distribuidora / Atacado');


/* ---------- Categoria: Alimentação (🍔) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Alimentação' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Alimentação', '🍔', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Alimentação');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Restaurante', '🍽️', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Restaurante');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Lanchonete / Hamburgueria', '🍔', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Lanchonete / Hamburgueria');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Bar / Pub', '🍺', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Bar / Pub');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Cafeteria', '☕', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Cafeteria');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Padaria / Confeitaria', '🥐', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Padaria / Confeitaria');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Delivery de alimentos', '🛵', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Delivery de alimentos');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Food truck', '🚚', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Food truck');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Produção de alimentos artesanais', '🧑‍🍳', 1, 80
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Produção de alimentos artesanais');


/* ---------- Categoria: Serviços administrativos e profissionais (🧑‍💼) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Serviços administrativos e profissionais' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Serviços administrativos e profissionais', '🧑‍💼', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Serviços administrativos e profissionais');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Consultoria empresarial', '📊', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Consultoria empresarial');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Serviços administrativos / Escritório', '🗂️', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Serviços administrativos / Escritório');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Contabilidade', '🧾', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Contabilidade');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Advocacia / Serviços jurídicos', '⚖️', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Advocacia / Serviços jurídicos');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Recursos humanos / Recrutamento', '🧑‍🤝‍🧑', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Recursos humanos / Recrutamento');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Treinamentos e cursos', '🎓', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Treinamentos e cursos');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Tradução e revisão de textos', '📝', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Tradução e revisão de textos');


/* ---------- Categoria: Construção e engenharia (🏗️) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Construção e engenharia' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Construção e engenharia', '🏗️', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Construção e engenharia');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Construção civil', '🏗️', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Construção civil');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Reformas e obras', '🧱', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Reformas e obras');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Engenharia', '📐', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Engenharia');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Arquitetura', '🏛️', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Arquitetura');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Instalações elétricas', '⚡', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Instalações elétricas');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Instalações hidráulicas', '🚰', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Instalações hidráulicas');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Serviços de pintura', '🖌️', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Serviços de pintura');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Serviços de manutenção predial', '🔧', 1, 80
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Serviços de manutenção predial');


/* ---------- Categoria: Transporte e logística (🚚) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Transporte e logística' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Transporte e logística', '🚚', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Transporte e logística');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Transporte rodoviário de cargas', '🚛', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Transporte rodoviário de cargas');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Transporte de passageiros', '🚌', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Transporte de passageiros');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Motoboy / Entregas rápidas', '🛵', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Motoboy / Entregas rápidas');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Logística e armazenagem', '🏬', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Logística e armazenagem');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Mudanças', '📦', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Mudanças');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Aplicativos de transporte', '📱', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Aplicativos de transporte');


/* ---------- Categoria: Saúde e bem-estar (🏥) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Saúde e bem-estar' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Saúde e bem-estar', '🏥', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Saúde e bem-estar');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Clínica médica', '🩺', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Clínica médica');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Odontologia', '🦷', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Odontologia');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Psicologia / Terapias', '🧠', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Psicologia / Terapias');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Fisioterapia', '🦴', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Fisioterapia');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Academia / Personal trainer', '💪', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Academia / Personal trainer');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Estética e beleza', '💆', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Estética e beleza');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Nutrição', '🥗', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Nutrição');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Laboratório / exames', '🧪', 1, 80
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Laboratório / exames');


/* ---------- Categoria: Beleza e estética (💇) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Beleza e estética' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Beleza e estética', '💇', 1, 80
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Beleza e estética');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Salão de beleza', '💇‍♀️', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Salão de beleza');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Barbearia', '💈', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Barbearia');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Manicure / Pedicure', '💅', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Manicure / Pedicure');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Estética facial e corporal', '✨', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Estética facial e corporal');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Maquiagem profissional', '💄', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Maquiagem profissional');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Depilação', '🧴', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Depilação');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Spa / massagens', '🧖', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Spa / massagens');


/* ---------- Categoria: Educação (🎓) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Educação' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Educação', '🎓', 1, 90
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Educação');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Escola / Ensino básico', '🏫', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Escola / Ensino básico');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Curso profissionalizante', '🧰', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Curso profissionalizante');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Curso online / EAD', '💻', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Curso online / EAD');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Aulas particulares', '📚', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Aulas particulares');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Escola de idiomas', '🗣️', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Escola de idiomas');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Treinamentos corporativos', '🏢', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Treinamentos corporativos');


/* ---------- Categoria: Mídia, eventos e criatividade (🎨) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Mídia, eventos e criatividade' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Mídia, eventos e criatividade', '🎨', 1, 100
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Mídia, eventos e criatividade');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Agência de publicidade', '📣', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Agência de publicidade');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Produção audiovisual', '🎬', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Produção audiovisual');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Fotografia', '📷', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Fotografia');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Filmagem de eventos', '📹', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Filmagem de eventos');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Organização de eventos', '🎟️', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Organização de eventos');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Design gráfico', '🧩', 1, 60
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Design gráfico');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Produção musical', '🎵', 1, 70
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Produção musical');


/* ---------- Categoria: Imobiliário (🏠) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Imobiliário' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Imobiliário', '🏠', 1, 110
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Imobiliário');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Corretagem de imóveis', '🏡', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Corretagem de imóveis');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Administração de imóveis', '🧾', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Administração de imóveis');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Aluguel por temporada', '🗓️', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Aluguel por temporada');


/* ---------- Categoria: Serviços gerais (🧺) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Serviços gerais' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Serviços gerais', '🧺', 1, 120
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Serviços gerais');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Limpeza residencial/comercial', '🧹', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Limpeza residencial/comercial');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Segurança privada', '🛡️', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Segurança privada');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Jardinagem e paisagismo', '🌿', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Jardinagem e paisagismo');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Lavanderia', '🧼', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Lavanderia');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Pet shop / serviços para pets', '🐶', 1, 50
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Pet shop / serviços para pets');


/* ---------- Categoria: Financeiro (💰) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Financeiro' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Financeiro', '💰', 1, 130
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Financeiro');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Serviços financeiros', '💳', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Serviços financeiros');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Correspondente bancário', '🏦', 1, 20
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Correspondente bancário');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Consultoria financeira', '📈', 1, 30
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Consultoria financeira');
INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Seguros', '🛡️', 1, 40
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Seguros');


/* ---------- Categoria: Outros (🧾) ---------- */
SET @cat_id := (SELECT id FROM pessoa_juridica_atividade_categoria WHERE label='Outros' LIMIT 1);
SET @cat_id := IFNULL(@cat_id, UUID());
INSERT INTO pessoa_juridica_atividade_categoria (id, label, icon, is_active, sort_order)
SELECT @cat_id, 'Outros', '🧾', 1, 140
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_categoria WHERE label='Outros');

INSERT INTO pessoa_juridica_atividade_item (id, category_id, label, icon, is_active, sort_order)
SELECT UUID(), @cat_id, 'Outros serviços', '🧩', 1, 10
    WHERE NOT EXISTS (SELECT 1 FROM pessoa_juridica_atividade_item WHERE category_id=@cat_id AND label='Outros serviços');

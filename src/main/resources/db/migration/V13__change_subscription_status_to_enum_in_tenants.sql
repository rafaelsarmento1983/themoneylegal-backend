UPDATE tenants
SET subscription_status = 'trial'
WHERE subscription_status IS NULL OR subscription_status = '';

ALTER TABLE tenants
    MODIFY COLUMN subscription_status
    ENUM('TRIAL','ACTIVE','CANCELLED','EXPIRED','SUSPENDED') NULL;

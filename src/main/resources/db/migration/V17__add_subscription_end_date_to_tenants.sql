ALTER TABLE tenants
    ADD COLUMN subscription_start_date DATE NULL AFTER subscription_status,
  ADD COLUMN subscription_end_date   DATE NULL AFTER subscription_start_date;

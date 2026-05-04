-- Add audit columns to all auditable tables
-- Using individual statements to avoid PL/pgSQL dollar-quoting issues with Spring Boot ScriptUtils

-- admin_users
ALTER TABLE IF EXISTS admin_users ADD COLUMN IF NOT EXISTS created_at timestamp(6);
ALTER TABLE IF EXISTS admin_users ADD COLUMN IF NOT EXISTS updated_at timestamp(6);
ALTER TABLE IF EXISTS admin_users ADD COLUMN IF NOT EXISTS created_by varchar(255);
ALTER TABLE IF EXISTS admin_users ADD COLUMN IF NOT EXISTS updated_by varchar(255);
UPDATE admin_users SET created_at = COALESCE(created_at, now()) WHERE created_at IS NULL;
UPDATE admin_users SET updated_at = COALESCE(updated_at, created_at, now()) WHERE updated_at IS NULL;
UPDATE admin_users SET created_by = COALESCE(created_by, 'SYSTEM') WHERE created_by IS NULL;
UPDATE admin_users SET updated_by = COALESCE(updated_by, created_by, 'SYSTEM') WHERE updated_by IS NULL;
ALTER TABLE IF EXISTS admin_users ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE IF EXISTS admin_users ALTER COLUMN updated_at SET DEFAULT now();
ALTER TABLE IF EXISTS admin_users ALTER COLUMN created_by SET DEFAULT 'SYSTEM';
ALTER TABLE IF EXISTS admin_users ALTER COLUMN updated_by SET DEFAULT 'SYSTEM';

-- redemptions
ALTER TABLE IF EXISTS redemptions ADD COLUMN IF NOT EXISTS created_at timestamp(6);
ALTER TABLE IF EXISTS redemptions ADD COLUMN IF NOT EXISTS updated_at timestamp(6);
ALTER TABLE IF EXISTS redemptions ADD COLUMN IF NOT EXISTS created_by varchar(255);
ALTER TABLE IF EXISTS redemptions ADD COLUMN IF NOT EXISTS updated_by varchar(255);
UPDATE redemptions SET created_at = COALESCE(created_at, now()) WHERE created_at IS NULL;
UPDATE redemptions SET updated_at = COALESCE(updated_at, created_at, now()) WHERE updated_at IS NULL;
UPDATE redemptions SET created_by = COALESCE(created_by, 'SYSTEM') WHERE created_by IS NULL;
UPDATE redemptions SET updated_by = COALESCE(updated_by, created_by, 'SYSTEM') WHERE updated_by IS NULL;
ALTER TABLE IF EXISTS redemptions ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE IF EXISTS redemptions ALTER COLUMN updated_at SET DEFAULT now();
ALTER TABLE IF EXISTS redemptions ALTER COLUMN created_by SET DEFAULT 'SYSTEM';
ALTER TABLE IF EXISTS redemptions ALTER COLUMN updated_by SET DEFAULT 'SYSTEM';

-- trash_stations
ALTER TABLE IF EXISTS trash_stations ADD COLUMN IF NOT EXISTS created_at timestamp(6);
ALTER TABLE IF EXISTS trash_stations ADD COLUMN IF NOT EXISTS updated_at timestamp(6);
ALTER TABLE IF EXISTS trash_stations ADD COLUMN IF NOT EXISTS created_by varchar(255);
ALTER TABLE IF EXISTS trash_stations ADD COLUMN IF NOT EXISTS updated_by varchar(255);
UPDATE trash_stations SET created_at = COALESCE(created_at, now()) WHERE created_at IS NULL;
UPDATE trash_stations SET updated_at = COALESCE(updated_at, created_at, now()) WHERE updated_at IS NULL;
UPDATE trash_stations SET created_by = COALESCE(created_by, 'SYSTEM') WHERE created_by IS NULL;
UPDATE trash_stations SET updated_by = COALESCE(updated_by, created_by, 'SYSTEM') WHERE updated_by IS NULL;
ALTER TABLE IF EXISTS trash_stations ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE IF EXISTS trash_stations ALTER COLUMN updated_at SET DEFAULT now();
ALTER TABLE IF EXISTS trash_stations ALTER COLUMN created_by SET DEFAULT 'SYSTEM';
ALTER TABLE IF EXISTS trash_stations ALTER COLUMN updated_by SET DEFAULT 'SYSTEM';

-- trash_submissions
ALTER TABLE IF EXISTS trash_submissions ADD COLUMN IF NOT EXISTS created_at timestamp(6);
ALTER TABLE IF EXISTS trash_submissions ADD COLUMN IF NOT EXISTS updated_at timestamp(6);
ALTER TABLE IF EXISTS trash_submissions ADD COLUMN IF NOT EXISTS created_by varchar(255);
ALTER TABLE IF EXISTS trash_submissions ADD COLUMN IF NOT EXISTS updated_by varchar(255);
UPDATE trash_submissions SET created_at = COALESCE(created_at, now()) WHERE created_at IS NULL;
UPDATE trash_submissions SET updated_at = COALESCE(updated_at, created_at, now()) WHERE updated_at IS NULL;
UPDATE trash_submissions SET created_by = COALESCE(created_by, 'SYSTEM') WHERE created_by IS NULL;
UPDATE trash_submissions SET updated_by = COALESCE(updated_by, created_by, 'SYSTEM') WHERE updated_by IS NULL;
ALTER TABLE IF EXISTS trash_submissions ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE IF EXISTS trash_submissions ALTER COLUMN updated_at SET DEFAULT now();
ALTER TABLE IF EXISTS trash_submissions ALTER COLUMN created_by SET DEFAULT 'SYSTEM';
ALTER TABLE IF EXISTS trash_submissions ALTER COLUMN updated_by SET DEFAULT 'SYSTEM';

-- users
ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS created_at timestamp(6);
ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS updated_at timestamp(6);
ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS created_by varchar(255);
ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS updated_by varchar(255);
UPDATE users SET created_at = COALESCE(created_at, now()) WHERE created_at IS NULL;
UPDATE users SET updated_at = COALESCE(updated_at, created_at, now()) WHERE updated_at IS NULL;
UPDATE users SET created_by = COALESCE(created_by, 'SYSTEM') WHERE created_by IS NULL;
UPDATE users SET updated_by = COALESCE(updated_by, created_by, 'SYSTEM') WHERE updated_by IS NULL;
ALTER TABLE IF EXISTS users ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE IF EXISTS users ALTER COLUMN updated_at SET DEFAULT now();
ALTER TABLE IF EXISTS users ALTER COLUMN created_by SET DEFAULT 'SYSTEM';
ALTER TABLE IF EXISTS users ALTER COLUMN updated_by SET DEFAULT 'SYSTEM';

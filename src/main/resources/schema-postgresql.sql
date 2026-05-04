DO $$
DECLARE
    table_name text;
BEGIN
    FOREACH table_name IN ARRAY ARRAY[
        'admin_users',
        'redemptions',
        'trash_stations',
        'trash_submissions',
        'users'
    ]
    LOOP
        IF to_regclass(table_name) IS NOT NULL THEN
            EXECUTE format('ALTER TABLE %I ADD COLUMN IF NOT EXISTS created_at timestamp(6)', table_name);
            EXECUTE format('ALTER TABLE %I ADD COLUMN IF NOT EXISTS updated_at timestamp(6)', table_name);
            EXECUTE format('ALTER TABLE %I ADD COLUMN IF NOT EXISTS created_by varchar(255)', table_name);
            EXECUTE format('ALTER TABLE %I ADD COLUMN IF NOT EXISTS updated_by varchar(255)', table_name);

            EXECUTE format('UPDATE %I SET created_at = COALESCE(created_at, now())', table_name);
            EXECUTE format('UPDATE %I SET updated_at = COALESCE(updated_at, created_at, now())', table_name);
            EXECUTE format('UPDATE %I SET created_by = COALESCE(created_by, ''SYSTEM'')', table_name);
            EXECUTE format('UPDATE %I SET updated_by = COALESCE(updated_by, created_by, ''SYSTEM'')', table_name);

            EXECUTE format('ALTER TABLE %I ALTER COLUMN created_at SET NOT NULL', table_name);
            EXECUTE format('ALTER TABLE %I ALTER COLUMN updated_at SET NOT NULL', table_name);
            EXECUTE format('ALTER TABLE %I ALTER COLUMN created_by SET NOT NULL', table_name);
            EXECUTE format('ALTER TABLE %I ALTER COLUMN updated_by SET NOT NULL', table_name);
        END IF;
    END LOOP;
END $$;

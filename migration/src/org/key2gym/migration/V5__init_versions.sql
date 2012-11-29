-- Adds an integer version column with 0 as the default value
ALTER TABLE order_ord ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE client_cln ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE attendance_atd ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
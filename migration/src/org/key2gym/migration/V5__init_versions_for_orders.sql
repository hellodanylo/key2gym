-- Adds an integer version column with 0 as the default value
ALTER TABLE order_ord ADD COLUMN version INTEGER NOT NULL DEFAULT 0
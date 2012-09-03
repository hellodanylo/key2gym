-- Changes the column's type.
ALTER TABLE client_profile_cpf ALTER COLUMN idads_cpf DROP NOT NULL;

-- Updates the values to match the new type.
UPDATE client_profile_cpf SET idads_cpf = NULL WHERE idads_cpf = 1;

-- Removes the obsolete ad source.
DELETE FROM ad_source_ads WHERE id_ads = 1;
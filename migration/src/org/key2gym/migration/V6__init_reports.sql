-- Creates the sequence
CREATE SEQUENCE id_rpt_seq;

-- Creates the tables for reports.
CREATE TABLE report_rpt (id_rpt INTEGER NOT NULL DEFAULT nextval('id_rpt_seq'), title TEXT NOT NULL, timestamp_generated TIMESTAMP NOT NULL, generator_reporter_class TEXT NOT NULL, note TEXT NOT NULL, primary_format TEXT NOT NULL, primary_body bytea NULL);
CREATE TABLE report_secondary_rpb (idrpt_rpb INTEGER NOT NULL, secondary_format TEXT NOT NULL, secondary_body BYTEA NULL, PRIMARY KEY(idrpt_rpb, secondary_format))

-- Adds the primary key constraint
ALTER TABLE report_secondary_rpb ADD CONSTRAINT idrpt_rpb FOREIGN KEY (idrpt_rpb) REFERENCES report_rpt (id_rpt);

-- Assigns the sequence to its owner
ALTER SEQUENCE id_rpt_seq OWNED BY report_rpt.id_rpt;
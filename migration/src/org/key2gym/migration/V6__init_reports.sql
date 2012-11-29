-- Creates the sequence
CREATE SEQUENCE id_rpt_seq;

-- Creates the tables for reports.
CREATE TABLE report_rpt (id_rpt INTEGER NOT NULL DEFAULT nextval('id_rpt_seq'), title TEXT NOT NULL, timestamp_generated TIMESTAMP NOT NULL, report_generator_class TEXT NOT NULL, note TEXT NOT NULL, primary_format TEXT NOT NULL, PRIMARY KEY(id_rpt));
CREATE TABLE report_body_rpb (idrpt_rpb INTEGER NOT NULL, format TEXT NOT NULL, body BYTEA NULL, PRIMARY KEY(idrpt_rpb, format));

-- Adds the primary key constraint
ALTER TABLE report_body_rpb ADD CONSTRAINT idrpt_rpb FOREIGN KEY (idrpt_rpb) REFERENCES report_rpt (id_rpt);

-- Assigns the sequence to its owner
ALTER SEQUENCE id_rpt_seq OWNED BY report_rpt.id_rpt;
-- Creates sequences for primary keys
CREATE SEQUENCE id_dsc_seq;
CREATE SEQUENCE id_atd_seq;
CREATE SEQUENCE id_its_seq;
CREATE SEQUENCE id_orl_seq;
CREATE SEQUENCE id_ads_seq;
CREATE SEQUENCE id_itm_seq;
CREATE SEQUENCE id_cfz_seq;
CREATE SEQUENCE id_adm_seq;
CREATE SEQUENCE id_cln_seq;
CREATE SEQUENCE id_ssn_seq;
CREATE SEQUENCE id_tsp_seq;
CREATE SEQUENCE id_ord_seq;
CREATE SEQUENCE id_key_seq;

-- Creates tables
CREATE TABLE discount_dsc (id_dsc INTEGER NOT NULL DEFAULT nextval('id_dsc_seq'), percent INTEGER NOT NULL, title TEXT NOT NULL, PRIMARY KEY (id_dsc));
CREATE TABLE attendance_atd (id_atd INTEGER NOT NULL DEFAULT nextval('id_atd_seq'), datetime_begin TIMESTAMP NOT NULL, datetime_end TIMESTAMP NOT NULL, idcln_atd INTEGER NULL, idkey_atd INTEGER NOT NULL, PRIMARY KEY (id_atd));
CREATE TABLE item_subscription_its (iditm_its INTEGER NOT NULL DEFAULT nextval('id_its_seq'), term_days INTEGER NOT NULL, term_months INTEGER NOT NULL, term_years INTEGER NOT NULL, units INTEGER NOT NULL, idtsp_its INTEGER, PRIMARY KEY (iditm_its));
CREATE TABLE order_line_orl (id_orl INTEGER NOT NULL DEFAULT nextval('id_orl_seq'), quantity INTEGER NOT NULL, iddsc_orl INTEGER NULL, iditm_orl INTEGER NOT NULL, idord_orl INTEGER NOT NULL, PRIMARY KEY (id_orl));
CREATE TABLE ad_source_ads (id_ads INTEGER NOT NULL DEFAULT nextval('id_ads_seq'), title TEXT NOT NULL, PRIMARY KEY (id_ads));
CREATE TABLE property_pty (id_pty INTEGER NOT NULL, name TEXT NOT NULL, current_value TEXT NOT NULL, PRIMARY KEY (id_pty));
CREATE TABLE cash_adjustment_cad (date_recorded DATE NOT NULL, amount DECIMAL(6,2) NOT NULL, note TEXT NOT NULL, PRIMARY KEY (date_recorded));
CREATE TABLE item_itm (id_itm INTEGER NOT NULL DEFAULT nextval('id_itm_seq'), barcode BIGINT NULL, price DECIMAL(5,2) NOT NULL, quantity INTEGER NULL, title TEXT NOT NULL, PRIMARY KEY (id_itm));
CREATE TABLE client_freeze_cfz (id_cfz INTEGER NOT NULL DEFAULT nextval('id_cfz_seq'), date_issued DATE NOT NULL, days INTEGER NOT NULL, note TEXT NOT NULL, idadm_cfz INTEGER NOT NULL, idcln_cfz INTEGER NOT NULL, PRIMARY KEY (id_cfz));
CREATE TABLE client_profile_cpf (idcln_cpf INTEGER NOT NULL, address TEXT NOT NULL, birthday DATE NOT NULL, favourite_sport TEXT NOT NULL, fitness_experience INTEGER NOT NULL, goal TEXT NOT NULL, health_restrictions TEXT NOT NULL, height INTEGER NOT NULL, possible_attendance_rate TEXT NOT NULL, sex INTEGER NOT NULL, special_wishes TEXT NOT NULL, telephone TEXT NOT NULL, weight INTEGER NOT NULL, idads_cpf INTEGER NOT NULL, PRIMARY KEY (idcln_cpf));
CREATE TABLE administrator_adm (id_adm INTEGER NOT NULL DEFAULT nextval('id_adm_seq'), address TEXT NOT NULL, full_name TEXT NOT NULL, note TEXT NOT NULL, password TEXT NOT NULL, permissions_level INTEGER NOT NULL, telephone TEXT NOT NULL, username TEXT NOT NULL, PRIMARY KEY (id_adm));
CREATE TABLE client_cln (id_cln INTEGER NOT NULL DEFAULT nextval('id_cln_seq'), attendances_balance INTEGER NOT NULL, card BIGINT NULL, expiration_date DATE NOT NULL, full_name TEXT NOT NULL, money_balance DECIMAL(6,2) NOT NULL, note TEXT NOT NULL, registration_date DATE NOT NULL, PRIMARY KEY (id_cln));
CREATE TABLE session_ssn (id_ssn INTEGER NOT NULL DEFAULT nextval('id_ssn_seq'), datetime_begin TIMESTAMP NOT NULL, datetime_end TIMESTAMP NOT NULL, idadm_ssn INTEGER NOT NULL, PRIMARY KEY (id_ssn));
CREATE TABLE time_split_tsp (id_tsp INTEGER NOT NULL DEFAULT nextval('id_tsp_seq'), end_time TIME NOT NULL, title TEXT NOT NULL, PRIMARY KEY (id_tsp));
CREATE TABLE order_ord (id_ord INTEGER NOT NULL DEFAULT nextval('id_ord_seq'), date_recorded DATE NOT NULL, payment DECIMAL(6,2) NOT NULL, idcln_ord INTEGER, idatd_ord INTEGER, PRIMARY KEY (id_ord));
CREATE TABLE key_key (id_key INTEGER NOT NULL DEFAULT nextval('id_key_seq'), title TEXT NOT NULL, PRIMARY KEY (id_key));

-- Assigns sequences to columns
ALTER SEQUENCE id_atd_seq OWNED BY attendance_atd.id_atd;
ALTER SEQUENCE id_dsc_seq OWNED BY discount_dsc.id_dsc;
ALTER SEQUENCE id_its_seq OWNED BY item_subscription_its.iditm_its;
ALTER SEQUENCE id_orl_seq OWNED BY order_line_orl.id_orl;
ALTER SEQUENCE id_ads_seq OWNED BY ad_source_ads.id_ads;
ALTER SEQUENCE id_itm_seq OWNED BY item_itm.id_itm;
ALTER SEQUENCE id_cfz_seq OWNED BY item_itm.id_itm;
ALTER SEQUENCE id_adm_seq OWNED BY administrator_adm.id_adm;
ALTER SEQUENCE id_cln_seq OWNED BY client_cln.id_cln;
ALTER SEQUENCE id_ssn_seq OWNED BY session_ssn.id_ssn;
ALTER SEQUENCE id_tsp_seq OWNED BY time_split_tsp.id_tsp;
ALTER SEQUENCE id_ord_seq OWNED BY order_ord.id_ord;
ALTER SEQUENCE id_key_seq OWNED BY key_key.id_key;

-- Adds contstraints
ALTER TABLE attendance_atd ADD CONSTRAINT idcln_atd FOREIGN KEY (idcln_atd) REFERENCES client_cln (id_cln);
ALTER TABLE attendance_atd ADD CONSTRAINT idkey_atd FOREIGN KEY (idkey_atd) REFERENCES key_key (id_key);
ALTER TABLE item_subscription_its ADD CONSTRAINT idtsp_its FOREIGN KEY (idtsp_its) REFERENCES time_split_tsp (id_tsp);
ALTER TABLE item_subscription_its ADD CONSTRAINT iditm_its FOREIGN KEY (iditm_its) REFERENCES item_itm (id_itm);
ALTER TABLE order_line_orl ADD CONSTRAINT idord_orl FOREIGN KEY (idord_orl) REFERENCES order_ord (id_ord);
ALTER TABLE order_line_orl ADD CONSTRAINT iditm_orl FOREIGN KEY (iditm_orl) REFERENCES item_itm (id_itm);
ALTER TABLE order_line_orl ADD CONSTRAINT iddsc_orl FOREIGN KEY (iddsc_orl) REFERENCES discount_dsc (id_dsc);
ALTER TABLE client_freeze_cfz ADD CONSTRAINT idcln_cfz FOREIGN KEY (idcln_cfz) REFERENCES client_cln (id_cln);
ALTER TABLE client_freeze_cfz ADD CONSTRAINT idadm_cfz FOREIGN KEY (idadm_cfz) REFERENCES administrator_adm (id_adm);
ALTER TABLE client_profile_cpf ADD CONSTRAINT idads_cpf FOREIGN KEY (idads_cpf) REFERENCES ad_source_ads (id_ads);
ALTER TABLE client_profile_cpf ADD CONSTRAINT idcln_cpf FOREIGN KEY (idcln_cpf) REFERENCES client_cln (id_cln);
ALTER TABLE session_ssn ADD CONSTRAINT idadm_ssn FOREIGN KEY (idadm_ssn) REFERENCES administrator_adm (id_adm);
ALTER TABLE order_ord ADD CONSTRAINT idatd_ord FOREIGN KEY (idatd_ord) REFERENCES attendance_atd (id_atd);
ALTER TABLE order_ord ADD CONSTRAINT idcln_ord FOREIGN KEY (idcln_ord) REFERENCES client_cln (id_cln);

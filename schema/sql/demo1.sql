-- Demo data dump #1

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

INSERT INTO ad_source_ads (id_ads, title) VALUES (1, 'Friends');
INSERT INTO ad_source_ads (id_ads, title) VALUES (2, 'Newspaper ad');
INSERT INTO ad_source_ads (id_ads, title) VALUES (3, 'Internet ad');

-- Password: qwerty
INSERT INTO administrator_adm (id_adm, address, full_name, note, password, telephone, username) VALUES (1, '', 'John Smith', '', '65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5', '', 'john');

-- Groups: manager, senior administrator, reports_manager
INSERT INTO administrator_group_agr (idadm_agr, idgrp_agr) VALUES (1, 1);
INSERT INTO administrator_group_agr (idadm_agr, idgrp_agr) VALUES (1, 2);
INSERT INTO administrator_group_agr (idadm_agr, idgrp_agr) VALUES (1, 4);

INSERT INTO client_cln (id_cln, attendances_balance, card, expiration_date, full_name, money_balance, note, registration_date, version) VALUES (1, 4, 11115648, current_date+interval '1 month', 'Michael Brown', 0.00, 'Our first client!', current_date, 1);

INSERT INTO client_profile_cpf (idcln_cpf, address, birthday, favourite_sport, fitness_experience, goal, health_restrictions, height, possible_attendance_rate, sex, special_wishes, telephone, weight, idads_cpf) VALUES (1, '', '1986-05-16', '', 0, 'Become a Mr. Olympia', '', 175, 'Twice a week', 1, 'Would like a personal locker', '', 70, NULL);

INSERT INTO key_key (id_key, title) VALUES (1, '1');
INSERT INTO key_key (id_key, title) VALUES (2, '2');
INSERT INTO key_key (id_key, title) VALUES (3, '3');
INSERT INTO key_key (id_key, title) VALUES (4, '4');
INSERT INTO key_key (id_key, title) VALUES (5, '5');
INSERT INTO key_key (id_key, title) VALUES (6, '6');
INSERT INTO key_key (id_key, title) VALUES (7, '7');
INSERT INTO key_key (id_key, title) VALUES (8, '8');
INSERT INTO key_key (id_key, title) VALUES (9, '9');
INSERT INTO key_key (id_key, title) VALUES (10, '10');

INSERT INTO attendance_atd (id_atd, datetime_begin, datetime_end, idcln_atd, idkey_atd, version) VALUES (1, current_timestamp - interval '15 minute', '2004-04-04 09:00:01', 1, 1, 1);
INSERT INTO attendance_atd (id_atd, datetime_begin, datetime_end, idcln_atd, idkey_atd, version) VALUES (2, current_timestamp - interval '1 hour', current_timestamp, NULL, 2, 1);

INSERT INTO discount_dsc (id_dsc, percent, title) VALUES (1, 50, 'Promo vaucher');
INSERT INTO discount_dsc (id_dsc, percent, title) VALUES (2, 10, 'Holidays');

INSERT INTO item_itm (id_itm, barcode, price, quantity, title) VALUES (1, NULL, 5.00, NULL, 'Interval Mismatch Penalty');
INSERT INTO item_itm (id_itm, barcode, price, quantity, title) VALUES (2, NULL, 20.00, NULL, 'Evening Month Subscription');
INSERT INTO item_itm (id_itm, barcode, price, quantity, title) VALUES (3, NULL, 5.00, NULL, 'Evening Casual Subscription');
INSERT INTO item_itm (id_itm, barcode, price, quantity, title) VALUES (4, NULL, 3.00, 12, 'Water');
INSERT INTO item_itm (id_itm, barcode, price, quantity, title) VALUES (5, NULL, 5.00, 55, 'Protein Bar');
INSERT INTO item_itm (id_itm, barcode, price, quantity, title) VALUES (6, NULL, 2.00, 30, 'Snack ''Chip-N-Chip''');

INSERT INTO time_split_tsp (id_tsp, end_time, title) VALUES (1, '11:59:59', 'Morning');
INSERT INTO time_split_tsp (id_tsp, end_time, title) VALUES (2, '16:59:59', 'Afternoon');
INSERT INTO time_split_tsp (id_tsp, end_time, title) VALUES (3, '22:00:00', 'Evening');

INSERT INTO item_subscription_its (iditm_its, term_days, term_months, term_years, units, idtsp_its) VALUES (2, 0, 1, 0, 12, 3);
INSERT INTO item_subscription_its (iditm_its, term_days, term_months, term_years, units, idtsp_its) VALUES (3, 1, 0, 0, 1, 3);

INSERT INTO property_pty (id_pty, property_value) VALUES ('time_range_mismatch_penalty_item_id', '1');

CREATE OR REPLACE FUNCTION reset_sequence(tablename text, columnname text, sequence_name text)
RETURNS void AS
$BODY$
DECLARE
BEGIN

EXECUTE 'SELECT setval( ''' || sequence_name  || ''', ' || '(SELECT MAX(' || columnname || ') FROM ' || tablename || ')' || '+1)';

END;

$BODY$
LANGUAGE plpgsql;
       
select column_name || '_seq', reset_sequence(table_name, column_name, column_name || '_seq') from information_schema.columns where column_default like 'nextval%';
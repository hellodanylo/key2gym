-- Creates the sequence
CREATE SEQUENCE id_rpt_seq;

-- Creates the tables for reports.
CREATE TABLE report_rpt (id_rpt INTEGER NOT NULL DEFAULT nextval('id_rpt_seq'), title TEXT NOT NULL, timestamp_generated TIMESTAMP NOT NULL, report_generator_class TEXT NOT NULL, note TEXT NOT NULL, primary_format TEXT NOT NULL, PRIMARY KEY(id_rpt));
CREATE TABLE report_body_rpb (idrpt_rpb INTEGER NOT NULL, format TEXT NOT NULL, body BYTEA NULL, PRIMARY KEY(idrpt_rpb, format));

-- Adds the primary key constraint
ALTER TABLE report_body_rpb ADD CONSTRAINT idrpt_rpb FOREIGN KEY (idrpt_rpb) REFERENCES report_rpt (id_rpt);

-- Assigns the sequence to its owner
ALTER SEQUENCE id_rpt_seq OWNED BY report_rpt.id_rpt;

-- View for the daily revenue reports
CREATE VIEW v_daily_revenue AS SELECT COALESCE(SUM(payment), 0) AS revenue, 
       generate_series::date AS date_recorded
FROM order_ord
	RIGHT JOIN generate_series((SELECT MIN(date_recorded) FROM order_ord), 
	      	   		current_date, interval '1 day') 
		ON generate_series = date_recorded
GROUP BY generate_series;

-- View for the monthly revenue reports
CREATE VIEW v_monthly_revenue AS SELECT COALESCE(SUM(payment), 0) AS revenue, 
       generate_series::date AS month_recorded
FROM order_ord
	RIGHT JOIN generate_series((SELECT MIN(date_trunc('month', date_recorded)) FROM order_ord), 
	      	   		date_trunc('month', current_date), interval '1 month') 
		ON generate_series = date_trunc('month', date_recorded)
GROUP BY generate_series;

-- View for the daily attendances reports
CREATE VIEW v_daily_attendances AS SELECT generate_series::date AS date_recorded,
       COALESCE(COUNT(datetime_begin), 0) AS attendances
FROM attendance_atd
	RIGHT JOIN generate_series((SELECT MIN(datetime_begin::date) FROM attendance_atd), current_date, interval '1 day')
		ON generate_series = datetime_begin::date
GROUP BY generate_series;

-- View for the monthly attendances reports
CREATE VIEW v_monthly_attendances AS SELECT generate_series::date AS month_recorded,
       COALESCE(COUNT(datetime_begin), 0) AS attendances
FROM attendance_atd
	RIGHT JOIN generate_series((SELECT MIN(date_trunc('month', datetime_begin::date)) FROM attendance_atd), date_trunc('month', current_date), interval '1 month')
		ON generate_series = date_trunc('month', datetime_begin::date)
GROUP BY generate_series;
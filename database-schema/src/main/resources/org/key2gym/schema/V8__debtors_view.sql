create view v_debtors as select id_cln, full_name, money_balance,
  (select datetime_begin
   from attendance_atd a
   where c.id_cln = a.idcln_atd
   order by datetime_begin desc
   limit 1)::date as last_attendance
from client_cln c
where c.money_balance < 0;
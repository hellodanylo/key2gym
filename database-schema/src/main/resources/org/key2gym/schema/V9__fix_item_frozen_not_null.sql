-- fix invalid values
update item_itm
  set frozen = false where frozen is null;

-- add constraint
alter table item_itm
  alter column frozen set not null;
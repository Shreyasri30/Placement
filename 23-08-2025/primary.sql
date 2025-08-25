show databases;
create database foodapp;
use foodapp;
create table Orders (
    OrderID int,
    ProductID int,
    Quantity int,
    primary key (OrderID, ProductID)  
);
insert into Orders (OrderID, ProductID, Quantity)
values (1, 200, 300);
alter table Orders
drop primary key;
alter table Orders
add constraint pk_orders primary key (OrderID);
show indexes from Orders;
select * from Orders;
drop table Orders;
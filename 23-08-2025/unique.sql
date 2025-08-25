show databases;
create database company;
use company;
create TABLE Employees (
    ID int not null,
    Email VARCHAR(100) unique,
    Phone VARCHAR(20),
    age int,
    unique(ID)
);
insert into Employees (ID, Email, Phone, Age)
values
 (1, 'maya@gmail.com', '1234556789', 11),
 (2,'laya@gmail.com',0987653429,12);
show indexes from Employees;
alter table Employees drop index Email;
select * from Employees;
drop table Employees;
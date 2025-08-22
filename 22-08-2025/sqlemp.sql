create database employees;
use employees;

create table technical(
emp_id int primary key auto_increment,
name varchar(100),
skill varchar(100),
experience int
);
create table hr(
emp_id int primary key auto_increment,
name varchar(100),
dept varchar(100),
salary decimal(10,2)
);
select * from hr;
select * from technical;

insert into hr(emp_id, name, dept, salary)
values (1, 'Maria', 'Development', 25000),
(2, 'Laura', 'Support', '20000'),
(3, 'Cristina', 'Strategy', 50000);
select * from hr;
insert into technical(emp_id, name, skill, experience)
values(1, 'Alan', 'Debugging', 10),
(2, 'Laila', 'Customer Facing', 3),
(3, 'Aisha', 'Coding', 7);
select* from technical;

ALTER TABLE technical
ADD Email varchar(255);
select* from technical;

Alter table  hr
ADD Email varchar(255);
select* from hr;
update hr set salary = 30000 where emp_id = 1;
select * from hr;

update hr set Email = 'maria@gmail.com' where emp_id = 1;
update hr set Email = 'laura@gmail.com' where emp_id = 2;
update hr set Email = 'cristina@gmail.com' where emp_id = 3;
select * from hr;
select * from technical;

alter table technical rename column Email to phone;
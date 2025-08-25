create database restaurants;
use restaurants;
create table cuisine (
  cuisineid int primary key,
  cuisinename varchar(50)
);
insert into cuisine (cuisineid, cuisinename) values
(1, 'italian'),
(2, 'indian'),
(3, 'mexican');
create table dishes (
  dishid int primary key,
  dishname varchar(50),
  cuisineid int,
  foreign key (cuisineid) references cuisine(cuisineid)
    on delete cascade
    on update cascade
);
insert into dishes values
(101, 'pizza', 1),
(102, 'biryani', 2),
(103, 'tacos', 3);
select * from cuisine;
select * from dishes;
update dishes set dishname = 'Pasta' where dishid = 101;
delete from cuisine where cuisineid = 1;
select * from cuisine;
select * from dishes;
update cuisine set cuisineid = 20 where cuisineid = 2;
select * from cuisine;
select * from dishes;

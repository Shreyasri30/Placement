show databases;
create database shops;
use shops;
create table category(
categoryID int primary key,
categoryname varchar(50)
);
insert into category (categoryID, categoryname)
values(1001, 'electronics'),
(1002, 'furniture');
create table product (
pID int primary key,
Pname varchar(50),
categoryID int,
foreign key(categoryID) references category(categoryID)
);
insert into product values(10003,'chair',1001);
select * from category;
select * from product;
update product set Pname = 'Office Chair' where pID = 10003;
delete from product where categoryID = 1001;

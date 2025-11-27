show databases;
CREATE DATABASE company;
USE company;
CREATE TABLE Worker (
    WorkerID INT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(30),
    LastName VARCHAR(30),
    Salary INT,
    Department VARCHAR(20)
);
select* from Worker;
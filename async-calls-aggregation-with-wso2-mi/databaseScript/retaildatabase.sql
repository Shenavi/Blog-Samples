DROP database IF EXISTS `retail`;
create database `retail`;

use retail;

create table customers (
customerName varchar(200),
firstName varchar(100),
lastName varchar(100),
phone varchar(50),
address varchar(500),
country varchar(100)
);

create table creditLimit(
customerNumber varchar(100), 
creditLimit varchar(10)
);
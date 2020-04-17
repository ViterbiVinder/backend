DROP DATABASE VinderDB;
CREATE DATABASE VinderDB;
USE VinderDB;
CREATE TABLE Users (
	ID integer primary key NOT NULL AUTO_INCREMENT,
    `Date` varchar(100),
    `Name` varchar(100) UNIQUE NOT NULL,
    UserName varchar(100),
    Email varchar(100),
    `Password` varchar(100),
    Bio varchar(200),
    Avatar varchar(100)
);
CREATE TABLE Posts (
	ID integer primary key NOT NULL AUTO_INCREMENT,
    `Date` varchar(50) NOT NULL,
    AuthorName varchar(50) NOT NULL,
    AuthorID integer Not NULL,
    Content varchar(50),
    foreign key fk1(AuthorName) references Users(`Name`),
    foreign key fk2(AuthorID) references Users(ID)
);
CREATE TABLE Tags (
	`Name` varchar(100) primary key NOT NULL,
    PostID integer NOT NULL,
    foreign key fk3(PostID) references Posts(ID)
);

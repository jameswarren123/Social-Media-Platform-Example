-- Create the database.
create database if not exists cs4370_mb_platform;

-- Use the created database.
use cs4370_mb_platform;

-- Create the user table.
create table if not exists user (
    userId int auto_increment,
    username varchar(255) not null,
    password varchar(255) not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    primary key (userId),
    unique (username),
    constraint userName_min_length check (char_length(trim(userName)) >= 2),
    constraint firstName_min_length check (char_length(trim(firstName)) >= 2),
    constraint lastName_min_length check (char_length(trim(lastName)) >= 2)
);
CREATE TABLE post(
    postId INT NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,
    date VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (postId),
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE
);
CREATE TABLE comment(
    commentId INT NOT NULL AUTO_INCREMENT,
    postId INT NOT NULL,
    userId INT NOT NULL,
    commentDate VARCHAR(255) NOT NULL,
    commentText VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (commentId),
    FOREIGN KEY (postId) REFERENCES post(postId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE
);
CREATE TABLE heart(
    postId INT NOT NULL,
    userId INT NOT NULL,
    PRIMARY KEY (postId, userId),
    FOREIGN KEY (postId) REFERENCES post(postId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE
);
CREATE TABLE bookmark(
    postId INT NOT NULL,
    userId INT NOT NULL,
    PRIMARY KEY (postId, userId),
    FOREIGN KEY (postId) REFERENCES post(postId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE
);
CREATE TABLE follow (
    followerId INT NOT NULL,
    followedId INT NOT NULL,
    PRIMARY KEY (followerId, followedId),
    FOREIGN KEY (followerId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (followedId) REFERENCES user(userId) ON DELETE CASCADE
);

--demo data
INSERT INTO user (userId, username, password, firstName, lastName) VALUES (2,'bob_mark_93','$2a$10$PqMJ7fdRywP5IOqjeWttYOh0YfU7ybFoVwDcHqihUffP0XZElC93i','Bob','Mark'),(3,'lauren_mack_200','$2a$10$I.opc18kxU15fjsP7AIou.dc.4tNK3Rd0S5TVp0KXfjH5/zDueDoW','Lauren','Mack'),(4,'john_more_82','$2a$10$u9CxOevJeLh4fL.qNpSvRe1PQpoinUsH2jiG8KyJbPL1wISnUp8A6','John','More'),(5,'lily_davis_65','$2a$10$3umhQ/jaKK36n0a3nZ1dBub0DpPflEPH6jbSR/TBmYJDTy5ATgKue','Lilly','Davis'),(13,'luke_mullins','$2a$10$RPmwhw6HB9yY2wFd/G2EFuWsOb9rXbDUhGluTtx8V0hHYqMh36jMa','Luke','Mullins');
INSERT INTO post (postId, userId, date, content, created_at) VALUES (18,2,'March 22,2025, 03:54 PM','Hey everyone it''s me Bob! #GreatDay','2025-03-22 15:54:42'),(19,3,'March 22,2025, 03:55 PM','Have a good day everyone #GreatDay','2025-03-22 15:55:44'),(20,4,'March 22,2025, 03:56 PM','Nice weather today #weather #Awesome','2025-03-22 15:56:45'),(27,13,'March 22,2025, 10:49 PM','Hey it''s me Luke','2025-03-22 22:49:56'),(28,13,'March 22,2025, 10:51 PM','yo','2025-03-22 22:51:23');
INSERT INTO follow (followerId, followedId) VALUES (3,2),(4,2),(5,2),(13,2),(5,3),(13,3),(2,4),(3,4),(5,4),(13,4),(3,5),(13,5);
INSERT INTO comment (commentId, postId, userId, commentDate, commentText) VALUES (13,18,3,'March 22,2025, 03:55 PM','Hey Bob!'),(14,18,4,'March 22,2025, 03:56 PM','Have a good day bob!!!'),(15,20,5,'March 22,2025, 03:57 PM','Indeed!'),(19,27,13,'March 22,2025, 10:50 PM','Hey '),(20,18,13,'March 22,2025, 10:50 PM','Hey bob!!!!');
INSERT INTO heart (postId, userId) VALUES (18,3),(19,3),(18,4),(20,4),(18,5),(20,5),(18,13),(27,13);
INSERT INTO bookmark (postId, userId) VALUES (18,3),(18,4),(19,5),(20,5),(19,13),(20,13);

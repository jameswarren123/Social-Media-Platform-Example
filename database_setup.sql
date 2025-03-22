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
mysqldump -u root -p --complete-insert --compact cs4370_mb_platform user
--REPEAT ABOVE FOR ALL TABLES
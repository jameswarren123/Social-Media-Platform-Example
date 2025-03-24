--URL starts where all sql statement can be executed
--insert new user into user table with the passed in user info
--http://localhost:8081/register
insert into user (username, password, firstName, lastName) values (?, ?, ?, ?);


--get all user table information of the user with the username passed in
--http://localhost:8081/login
select * from user where username = ?;


--returns all post information and user information of either the current user or any user the user follows in the follow table ordered by creation date
--http://localhost:8081/login
--http://localhost:8081/
select distinct p.postId,p.content, p.date, p.userId, u.firstName, u.lastName,p.created_at from post p, follow f,user u where u.userId = ? and u.userId = p.userId or u.userId = p.userId and p.userId = some (select distinct f.followedId from user u, follow f where u.userId = ? and u.userId = f.followerId) order by p.created_at desc;   

--return the number of hearts associated with a post of postid
--http://localhost:8081/login
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/hashtagsearch
select count(*) as row_count from heart where postId = ?;


--return the number of comments associated with a post of postid
--http://localhost:8081/login
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/hashtagsearch
select count(*) as row_count from comment where postId = ?;


--return one if user of userid hearted post of postid zero otherwise
--http://localhost:8081/login
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/bookmarks
--http://localhost:8081/hashtagsearch
select count(*) as row_count from heart where postId = ? and userId = ?;


--return one if user of userid bookmarked post of postid zero otherwise
--http://localhost:8081/login
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/hashtagsearch
select count(*) as row_count from bookmark where postId = ? and userId = ?;


--gets all post table information of posts made by user with userId
--http://localhost:8081/login
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/hashtagsearch
select * from post where userId = ?;


--insert into heart table user of userId hearting post of postId
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/bookmarks
--http://localhost:8081/hashtagsearch
insert into heart (postId,userId) values (?,?);


--delete from heart table user of userId hearting post of postId
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/bookmarks
--http://localhost:8081/hashtagsearch
delete from heart (postId,userId) values (?,?);


--return all table comment information of all comments on post postid
--http://localhost:8081/post/{post number}
select * from comment where postId = ?;

--return all user table information of user with userid
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/bookmarks
--http://localhost:8081/hashtagsearch
select * from user where userId = ?;


--insert into bookmark table user of userId bookmarking post of postId
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/bookmarks
--http://localhost:8081/hashtagsearch
insert into bookmark (postId,userId) values (?,?);


--delete from bookmark table user of userId bookmarking post of postId
--http://localhost:8081/
--http://localhost:8081/post/{post number}
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
--http://localhost:8081/bookmarks
--http://localhost:8081/hashtagsearch
delete from bookmark (postId,userId) values (?,?);


--insert into comment table a comment from user with userid into post with postid with its date and text
--http://localhost:8081/post/{post number}
insert into comment (postId,userId,commentDate,commentText) values (?,?,DATE_FORMAT(now(), "%M %e,%Y, %h:%i %p"),?);


--retrieves all posts made by a specific user, ordered from the most recent to the oldest based on the created_at timestamp
--http://localhost:8081/profile
--http://localhost:8081/profile/{user number}
SELECT * FROM post WHERE userId = ? ORDER BY created_at DESC;


--return all users that are not the logged in user and whether the logged in user follows them
--http://localhost:8081/people
SELECT u.userId, u.firstName, u.lastName, EXISTS (SELECT 1 FROM follow f WHERE f.followerId = ? AND f.followedId = u.userId) AS isFollowed, (SELECT MAX(p.created_at) FROM post p WHERE p.userId = u.userId) AS lastPostDate FROM user u WHERE u.userId != ?;


--return all posts the logged in user has bookmarked based on the assocation in the bookmark table with all information to build a post
--http://localhost:8081/bookmarks
SELECT p.*, (SELECT COUNT(*) FROM heart h WHERE h.postId = p.postId) AS heartsCount, (SELECT COUNT(*) FROM comment c WHERE c.postId = p.postId) AS commentsCount FROM post p JOIN bookmark b ON p.postId = b.postId WHERE b.userId = ? ORDER BY p.created_at DESC;


--get user information from the user table of a post the loggedin user follows
--http://localhost:8081/bookmarks
SELECT u.* FROM user u JOIN post p ON u.userId = p.userId WHERE p.postId = ?;


--insert into follow table association between user followerId following user followed id
--http://localhost:8081/people
INSERT INTO follow (followerId, followedId) VALUES (?, ?);

--delete from follow table association between user followerId following user followed id
--http://localhost:8081/people
DELETE FROM follow (followerId, followedId) VALUES (?, ?);

--http://localhost:8081/hashtagsearch?hashtags=%23dogs (where hashtag input is dogs)
--http://localhost:8081/hashtagsearch?hashtags=%23dogs+%23soloLeveling (where hashtag input is dogs & soloLeveling)
--This is a dynamic query because it adjusts based on the number of hashes in user input.
--It selects post and user information based on a generic expression beginning with a hash
--and having a few other constraints. It looks for this substring in content and displays
--in the order of the hashes in the search.
"SELECT p.postId, p.content, p.created_at, p.userId, " +
"u.firstName, u.lastName FROM Post p JOIN User u ON p.userId = u.userId WHERE "
/*
    for (int i = 0; i < hashtagList.size(); i++) {
        sql.append("p.content LIKE ?");
        if (i < hashtagList.size() - 1) {
            sql.append(" OR ");
        }

    for (int i = 0; i < hashtagList.size(); i++) {
        pstmt.setString(i + 1, "%" + hashtagList.get(i) + "%");
    }
*/

--http://localhost:8081/people
--THis updates the isFollowed UI icon and lastPostDate value on the people page. It also provides
--users that are not the logged in user.
"SELECT u.userId, u.firstName, u.lastName, " +
"EXISTS (SELECT 1 FROM follow f WHERE f.followerId = ? AND f.followedId = u.userId) AS isFollowed, " +
"(SELECT MAX(p.created_at) FROM post p WHERE p.userId = u.userId) AS lastPostDate " +
"FROM user u WHERE u.userId != ?"

--http://localhost:8081/people
--The following two SQL statements were apart of the same ternary operator.
--It is the basic insert and delete from the follow table based on the one
--followed and the follower. Does the proper operation based on user action.
"INSERT INTO follow (followerId, followedId) VALUES (?, ?)"
"DELETE FROM follow WHERE followerId = ? AND followedId = ?"

--get all user table information of the user with the username passed in
"select * from user where username = ?";

--insert new user into user table with the passed in user info
"insert into user (username, password, firstName, lastName) values (?, ?, ?, ?)";

--insert into post table post data with post date formated properly
"insert into post (content,userId,date) values (?,?,DATE_FORMAT(now(), \"%M %e,%Y, %h:%i %p\"))";

--gets all information from the user table of the user who made the post with postid
"SELECT u.* FROM user u " +
    "JOIN post p ON u.userId = p.userId " +
    "WHERE p.postId = ?";

--return one if user of userid bookmarked post of postid zero otherwise
 "select count(*) as row_count from bookmark where postId = ? and userId = ?";

--return one if user of userid hearted post of postid zero otherwise
"select count(*) as row_count from heart where postId = ? and userId = ?";

--return the number of comments associated with a post of postid
"select count(*) as row_count from comment where postId = ?";

--return the number of hearts associated with a post of postid
"select count(*) as row_count from heart where postId = ?";

--return all table information of user with userid 
"select * from user where userId = ?";

--return all comment information on post with postid in descending order of creation date
"select * from comment where postId = ? order by created_at desc;";

--return all post information of post with postid
"select * from post where postId = ?";

--insert that user with userid liked post with post id into heart table
"insert into heart (postId,userId) values (?,?)";

--delete that user with userid liked post with post id from heart table
"delete from heart where postId = ? and userId = ?";

--insert that user with userid bookmarked post with post id into heart table
"insert into bookmark (postId,userId) values (?,?)";

--delete that user with userid bookmarked post with post id from heart table
"delete from bookmark where postId = ? and userId = ?";

--insert into comment table a comment from user with userid into post with postid with its date and text
"insert into comment (postId,userId,commentDate,commentText) values (?,?,DATE_FORMAT(now(), \"%M %e,%Y, %h:%i %p\"),?)";

--return all post information of all posts made by user with userid
"SELECT * FROM post WHERE userId = ? ORDER BY date DESC";

--get all post information from post table include all additional information to create post including
--heartCount(number of userids associated with postid in heart table)
--commentCount(all comments associated with post with postid)
--of posts which user with userid is associated with in bookmark table
"SELECT p.*, (SELECT COUNT(*) FROM heart h WHERE h.postId = p.postId) AS heartsCount, (SELECT COUNT(*) FROM comment c WHERE c.postId = p.postId) AS commentsCount FROM post p JOIN bookmark b ON p.postId = b.postId WHERE b.userId = ? ORDER BY p.created_at DESC;";

--NEEDS HELP
"SELECT u.userId, u.firstName, u.lastName, " +
    "EXISTS (SELECT 1 FROM follow f WHERE f.followerId = ? AND f.followedId = u.userId) AS isFollowed, " +
    "(SELECT MAX(p.created_at) FROM post p WHERE p.userId = u.userId) AS lastPostDate " +
    "FROM user u WHERE u.userId != ?";

--returns all post information and user information of either the current user or any user the user follows in the follow table ordered by creation date
"select distinct p.postId,p.content, p.date, p.userId, u.firstName, u.lastName,p.created_at from post p, follow f,user u where u.userId = ? and u.userId = p.userId or u.userId = p.userId and p.userId = some (select distinct f.followedId from user u, follow f where u.userId = ? and u.userId = f.followerId) order by p.created_at desc;";

--select all post information in post table of all posts made by user ordered by date dsecending
"select * from post where userId = ? order by date desc";

--insert into follow table that user of followerId followed user of followedId
"INSERT INTO follow (followerId, followedId) VALUES (?, ?)";

--delete from follow table that user of followerId followed user of followedId
"DELETE FROM follow WHERE followerId = ? AND followedId = ?";

--NEEDS HELP in Hashtag Service
"";
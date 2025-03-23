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

--return all comment information on post with postid in descending order of creation date per id autoincrement
"select * from comment where postId = ?;";

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

--retrieves a list of users (excluding a given user), indicating whether the given user follows them and the date of their most recent post.
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

--retrieves all information about the user who created a specific post, identified by its postId
"SELECT u.* FROM user u " + 
"JOIN post p ON u.userId = p.userId " +
"WHERE p.postId = ?"

--retrieves all posts made by a specific user, ordered from the most recent to the oldest based on the created_at timestamp
"SELECT * FROM post WHERE userId = ? ORDER BY created_at DESC"

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
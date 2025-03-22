/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import uga.menik.cs4370.models.FollowableUser;
import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.models.User;
import uga.menik.cs4370.services.PostService;;

/**
 * This service contains people related functions.
 */
@Service
@SessionScope
public class PeopleService {
    /**
     * This function should query and return all users that
     * are followable. The list should not contain the user
     * with id userIdToExclude.
     */
    private final DataSource dataSource;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public PeopleService(DataSource dataSource, UserService userService, PostService postService) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.postService = postService;
    }

    // ====================================================================================================
    public List<FollowableUser> getFollowableUsers(String userIdToExclude) throws SQLException {
        final String sql = "SELECT u.userId, u.firstName, u.lastName, " +
                "EXISTS (SELECT 1 FROM follow f WHERE f.followerId = ? AND f.followedId = u.userId) AS isFollowed, " +
                "(SELECT MAX(p.created_at) FROM post p WHERE p.userId = u.userId) AS lastPostDate " +
                "FROM user u WHERE u.userId != ?";

        List<FollowableUser> followableUsers = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userService.getLoggedInUser().getUserId());
            pstmt.setString(2, userIdToExclude);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    boolean isFollowed = rs.getBoolean("isFollowed");

                    // Retrieve and format the date
                    java.sql.Timestamp lastPostTimestamp = rs.getTimestamp("lastPostDate");
                    String formattedDate = (lastPostTimestamp != null)
                            ? new SimpleDateFormat("MMM dd, yyyy, hh:mm a").format(lastPostTimestamp)
                            : "(No Posts)";

                    followableUsers.add(new FollowableUser(
                            rs.getString("userId"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            isFollowed,
                            formattedDate));
                }
            }
        }
        return followableUsers;
    }
    // ====================================================================================================
    public boolean createPost(String content, String userId) throws SQLException{
        final String postSql = "insert into post (content,userId,date) values (?,?,DATE_FORMAT(now(), \"%M %e,%Y, %h:%i %p\"))";

        try(Connection conn = dataSource.getConnection();
        PreparedStatement sqlStmt = conn.prepareStatement(postSql)){
            sqlStmt.setString(1, content);
            sqlStmt.setString(2, userId);

            int rowsAffected = sqlStmt.executeUpdate();
            return rowsAffected > 0;
        }
        

    }
    
    public List<Post> getCreatedPosts(String userId) throws SQLException {
        final String sql = "select distinct p.postId,p.content, p.date, p.userId, u.firstName, u.lastName,p.created_at from post p, follow f,user u where u.userId = ? and u.userId = p.userId or u.userId = p.userId and p.userId = some (select distinct f.followedId from user u, follow f where u.userId = ? and u.userId = f.followerId) order by p.created_at desc;";

        List<Post> posts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String postUserId = rs.getString("userId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");

                    User postUser = new User(postUserId, firstName, lastName);

                    posts.add(new Post(rs.getString("postId"), rs.getString("content"), rs.getString("date"),
                           postUser , postService.getHeartCount(rs.getString("postId")), postService.getCommentCount(rs.getString("postId")), postService.userHearted(rs.getString("postId"),userService.getLoggedInUser().getUserId()), postService.userBookmarked(rs.getString("postId"),userService.getLoggedInUser().getUserId())));
                }
            }

        }

        final String sql2 = "select * from post where userId = ?";
        
        if(posts.isEmpty()){
            try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            pstmt.setString(1, userId);
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){

                    posts.add(new Post(rs.getString("postId"), rs.getString("content"), rs.getString("date"),
                    userService.getLoggedInUser() , postService.getHeartCount(rs.getString("postId")), postService.getCommentCount(rs.getString("postId")), postService.userHearted(rs.getString("postId"),userService.getLoggedInUser().getUserId()), postService.userBookmarked(rs.getString("postId"),userService.getLoggedInUser().getUserId())));
                }
            }
        }
        }

        return posts;
    }

    // ====================================================================================================
    public void updateFollowStatus(String followerId, String followedId, boolean isFollow) throws SQLException {
        final String sql = isFollow
                ? "INSERT INTO follow (followerId, followedId) VALUES (?, ?)" // Follow
                : "DELETE FROM follow WHERE followerId = ? AND followedId = ?"; // Unfollow

        
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, followerId);
            stmt.setString(2, followedId);
            stmt.executeUpdate();
        }

    }
    // ====================================================================================================
}

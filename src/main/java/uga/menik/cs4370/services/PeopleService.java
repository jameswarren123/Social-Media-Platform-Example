/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import uga.menik.cs4370.models.FollowableUser;
import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.utility.Utility;

/**
 * =============================================================================
 *  FILE: PeopleService.java
 * =============================================================================
 *  
 *  AUTHOR:       Landon Cartrette
 *  WORK UNIT:    1 & 3
 *  LAST EDITED:  2025-03-15 11:54:00
 *  
 *  WARNING: DO NOT EDIT THIS FILE WITHOUT PROPER AUTHORIZATION.
 *           Unauthorized modifications may cause unexpected behavior.
 *  
 * =============================================================================
 */

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

    @Autowired
    public PeopleService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    public List<FollowableUser> getFollowableUsers(String userIdToExclude) throws SQLException {
        // Write an SQL query to find the users that are not the current user.
        final String sql = "select * from user where userId != ?";
        // Run the query with a datasource.
        // See UserService.java to see how to inject DataSource instance and
        // use it to run a query.
        List<FollowableUser> followableUsers = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdToExclude);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    followableUsers.add(new FollowableUser(rs.getString("userID"), rs.getString("firstName"),
                            rs.getString("lastName"),
                            false, "Mar 02, 2024, 08:15 PM"));
                }
            }
        }
        // Use the query result to create a list of followable users.
        // See UserService.java to see how to access rows and their attributes
        // from the query result.
        // Check the following createSampleFollowableUserList function to see
        // how to create a list of FollowableUsers.

        // Replace the following line and return the list you created.
        return followableUsers;
    }

    public List<Post> getCreatedPosts(String userId) throws SQLException {
        final String sql = "select * from post where userId = ? order by date desc";

        List<Post> posts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(rs.getString("postId"), rs.getString("content"), rs.getString("date"),
                            userService.getLoggedInUser(), 0, 0, false, false));
                }
            }

        }
        System.out.println(posts);

        return posts;
    }

    public void updateFollowStatus(String followerId, String followedId, boolean isFollow) throws SQLException {
        // isFollow =
        final String sql = isFollow
                ? "DELETE FROM follow WHERE followerId = ? AND followedId = ?" // Unfollow
                : "INSERT INTO follow (followerId, followedId) VALUES (?, ?)"; // Follow

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, followerId);
            stmt.setString(2, followedId);
            stmt.executeUpdate();
        }
    }

    // ======================================LANDONS CODE=================================================
    public void updateFollowStatus(String followerId, String followedId) throws SQLException {
        // First, check if user 1 follows the user n
        final String checkFollowSql = "SELECT COUNT(*) FROM follow WHERE followerId = ? AND followedId = ?";

        boolean isFollow = false; // Default to false (not following)

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(checkFollowSql)) {

            pstmt.setString(1, followerId); // Set followerId (e.g., "1")
            pstmt.setString(2, followedId); // Set followedId (e.g., "n")

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If the count is greater than 0, it means the user is following
                    isFollow = rs.getInt(1) > 0;
                }
            }
        }

        System.out.println("\tisFollow: " + isFollow);

        // Now perform the follow/unfollow action based on isFollow status
        final String sql = isFollow
                ? "DELETE FROM follow WHERE followerId = ? AND followedId = ?" // Unfollow
                : "INSERT INTO follow (followerId, followedId) VALUES (?, ?)"; // Follow

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, followerId);
            stmt.setString(2, followedId);
            stmt.executeUpdate();
        }
    }
    // ====================================================================================================
}

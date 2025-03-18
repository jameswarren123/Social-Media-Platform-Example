package uga.menik.cs4370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import uga.menik.cs4370.models.Comment;
import uga.menik.cs4370.models.ExpandedPost;
import uga.menik.cs4370.models.FollowableUser;
import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.models.User;
import uga.menik.cs4370.utility.Utility;
/**
 
=============================================================================
FILE: PostService.java
=============================================================================
AUTHOR:       James Warren
WORK UNIT:    All of post controller
LAST EDITED:  2025-03-17 16:45:00
WARNING: DO NOT EDIT THIS FILE WITHOUT PROPER AUTHORIZATION.
Unauthorized modifications may cause unexpected behavior.
=============================================================================*/

@Service
@SessionScope
public class PostService {
    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public PostService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    private boolean userBookmarked(String postId, String userId) throws SQLException {
        boolean bookmarked = false;
        final String sql = "select count(*) as row_count from bookmark where postId = ? and userId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postId);
            pstmt.setString(2, userId);
            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                if(Integer.parseInt(rs.getString("row_count")) > 0) bookmarked = true;           
            }
        }
        return bookmarked;
    }

    private boolean userHearted(String postId, String userId) throws SQLException {
        boolean hearted = false;
        final String sql = "select count(*) as row_count from heart where postId = ? and userId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postId);
            pstmt.setString(2, userId);
            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                if(Integer.parseInt(rs.getString("row_count")) > 0) hearted = true;           
            }
        }
        return hearted;
    }

    private int getCommentCount(String postId) throws SQLException {
        int commentCount = 0;
        final String sql = "select count(*) as row_count from comment where postId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postId);
            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                commentCount = Integer.parseInt(rs.getString("row_count"));
            }
        }
        return commentCount;
    }

    private int getHeartCount(String postId) throws SQLException {
        int heartCount = 0;
        final String sql = "select count(*) as row_count from heart where postId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postId);
            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                heartCount = Integer.parseInt(rs.getString("row_count"));
            }
        }
        return heartCount;
    }

    private User getUser(String userId) throws SQLException {
        final String sql = "select * from user where userId = ?";
        User user = null;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                user = new User(userId, rs.getString("firstName"), rs.getString("lastName"));
            }
        }

        return user;
    }

    public List<ExpandedPost> getPostComments(String postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        final String sql = "select * from comment where postId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(new Comment(rs.getString("postId"), rs.getString("commentText"),
                        rs.getString("commentDate"), this.getUser(rs.getString("userId"))));
                }
            }
        }
        
        String content = null;
        String postDate = null;
        User user = null;
        int heartsCount = 0;
        int commentsCount = 0;
        boolean isHearted = false;
        boolean isBookmarked = false;
        final String sql1 = "select * from post where postId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql1)) {
            pstmt.setString(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    content = rs.getString("content");
                    postDate = rs.getString("date");
                    System.out.println("through post");
                    user = this.getUser(rs.getString("userId"));
                    heartsCount = this.getHeartCount(postId);
                    System.out.println("through get heart count");
                    commentsCount = this.getCommentCount(postId);
                    System.out.println("through get comment count");
                    isHearted = this.userHearted(postId, userService.getLoggedInUser().getUserId());  
                    System.out.println("through user hearted count");  
                    isBookmarked = this.userBookmarked(postId, userService.getLoggedInUser().getUserId());
                    System.out.println("through get bookmarked count");
                }
            }
        }
        //need full post plus comments
        ExpandedPost postWithComments = new ExpandedPost(postId, content, postDate, user, heartsCount, commentsCount, isHearted, isBookmarked, comments);
        return List.of(postWithComments);
    }

    public boolean likePost(boolean isAdd, String postId, User user) throws SQLException{
        if(isAdd){
            final String sql = "insert into heart (postId,userId) values (?,?)";
            try(Connection conn = dataSource.getConnection();
            PreparedStatement sqlStmt = conn.prepareStatement(sql)){
                sqlStmt.setString(1, postId);
                sqlStmt.setString(2, user.getUserId());

                int rowsAffected = sqlStmt.executeUpdate();
                return rowsAffected > 0;
            }
        } else {
            final String sql = "delete from heart where postId = ? and userId = ?";
            try(Connection conn = dataSource.getConnection();
            PreparedStatement sqlStmt = conn.prepareStatement(sql)){
                sqlStmt.setString(1, postId);
                sqlStmt.setString(2, user.getUserId());

                int rowsAffected = sqlStmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }
    
    public boolean bookmarkPost(boolean isAdd, String postId, User user) throws SQLException{
        if(isAdd){
            final String sql = "insert into bookmark (postId,userId) values (?,?)";
            try(Connection conn = dataSource.getConnection();
            PreparedStatement sqlStmt = conn.prepareStatement(sql)){
                sqlStmt.setString(1, postId);
                sqlStmt.setString(2, user.getUserId());

                int rowsAffected = sqlStmt.executeUpdate();
                return rowsAffected > 0;
            }
        } else {
            final String sql = "delete from bookmark where postId = ? and userId = ?";
            try(Connection conn = dataSource.getConnection();
            PreparedStatement sqlStmt = conn.prepareStatement(sql)){
                sqlStmt.setString(1, postId);
                sqlStmt.setString(2, user.getUserId());

                int rowsAffected = sqlStmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }

    public boolean createComment (String comment, String postId, User user) throws SQLException{
        final String sql = "insert into comment (postId,userId,commentDate,commentText) values (?,?,NOW(),?)";
        try(Connection conn = dataSource.getConnection();
        PreparedStatement sqlStmt = conn.prepareStatement(sql)){
            sqlStmt.setString(1, postId);
            sqlStmt.setString(2, user.getUserId());
            sqlStmt.setString(3, comment);
            int rowsAffected = sqlStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}

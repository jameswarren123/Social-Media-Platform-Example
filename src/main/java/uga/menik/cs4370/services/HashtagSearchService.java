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

import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.models.User;

@Service
@SessionScope
public class HashtagSearchService {
    private final DataSource dataSource;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public HashtagSearchService(DataSource dataSource, PostService postService, UserService userService) {
        this.dataSource = dataSource;
        this.postService = postService;
        this.userService = userService;
    }

    public List<Post> getPostWithHashtag(String hashtags) throws SQLException {
        List<String> hashtagList = new ArrayList<>();
        int length = hashtags.length();

        // Extract hashtags from the input string
        for (int i = 0; i < length; i++) {
            if (hashtags.charAt(i) == '#') {
                int start = i;
                i++;

                while (i < length && hashtags.charAt(i) != ' ' && hashtags.charAt(i) != '#') {
                    i++;
                }

                String hashtag = hashtags.substring(start, i);
                if (hashtag.length() > 1) {
                    hashtagList.add(hashtag);
                }
                i--;
            }
        }

        System.out.println("Hashtag List: " + hashtagList);
        List<Post> postObjsWithHashtag = new ArrayList<>();

        if (!hashtagList.isEmpty()) {
            StringBuilder sql = new StringBuilder("SELECT p.postId, p.content, p.created_at, p.userId, " +
                    "u.firstName, u.lastName FROM Post p JOIN User u ON p.userId = u.userId WHERE ");

            for (int i = 0; i < hashtagList.size(); i++) {
                sql.append("p.content LIKE ?");
                if (i < hashtagList.size() - 1) {
                    sql.append(" OR ");
                }
            }

            try (Connection conn = dataSource.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                for (int i = 0; i < hashtagList.size(); i++) {
                    pstmt.setString(i + 1, "%" + hashtagList.get(i) + "%");
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {

                        String postId = rs.getString("postId");

                        int heartCount = postService.getHeartCount(postId);
                        int commentCount = postService.getCommentCount(postId);
                        boolean isLiked = postService.userHearted(postId, userService.getLoggedInUser().getUserId());
                        boolean isBookMarked = postService.userBookmarked(postId, userService.getLoggedInUser().getUserId());

                        User user = new User(
                                rs.getString("userId"),
                                rs.getString("firstName"),
                                rs.getString("lastName"));

                        Post post = new Post(
                                rs.getString("postId"),
                                rs.getString("content"),
                                rs.getString("created_at"),
                                user,
                                heartCount,
                                commentCount,
                                isLiked,
                                isBookMarked);

                        postObjsWithHashtag.add(post);
                    }
                }
            }
            System.out.println("Posts containing hashtags: " + postObjsWithHashtag.size());
        } else {
            System.out.println("Error: empty list of hashtags");
        }

        return postObjsWithHashtag;
    }
}

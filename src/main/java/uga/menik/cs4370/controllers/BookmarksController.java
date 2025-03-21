/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.cs4370.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.services.PostService;
import uga.menik.cs4370.services.UserService;

@Controller
@RequestMapping("/bookmarks")
public class BookmarksController {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public BookmarksController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    /**
     * Handles /bookmarks URL to show the logged-in user's bookmarked posts.
     */
    @GetMapping
    public ModelAndView webpage() throws SQLException {
        // Get the logged-in user's ID
        String userId = userService.getLoggedInUser().getUserId();

        // Fetch the user's bookmarked posts
        List<Post> bookmarkedPosts = postService.getBookmarkedPosts(userId);

        // Create the ModelAndView object to pass data to the template
        ModelAndView mv = new ModelAndView("posts_page");

        // Add the posts to the model
        mv.addObject("posts", bookmarkedPosts);

        // If no bookmarked posts, show a "No content" message
        if (bookmarkedPosts.isEmpty()) {
            mv.addObject("isNoContent", true);
        }

        return mv;
    }
}


    // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        // String errorMessage = "Some error occured!";
        // mv.addObject("errorMessage", errorMessage);

        // Enable the following line if you want to show no content message.
        // Do that if your content list is empty.
        // mv.addObject("isNoContent", true);


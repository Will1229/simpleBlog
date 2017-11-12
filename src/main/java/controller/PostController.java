package controller;

import config.ServiceConfiguration;
import entity.Message;
import entity.Post;
import exception.PostServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;
import service.PostService;

import java.util.Optional;

@RestController
@Import(ServiceConfiguration.class)
@RequestMapping(path = "/post")
public class PostController {

    public static final String MSG_POST_NOT_FOUND = "Post not found";

    @Autowired
    private PostService postService;

    @GetMapping(path = "/get", params = {"title"})
    Message getPost(@RequestParam("title") String title) {
        Optional<Post> post = postService.getPost(title);
        final Message message = new Message();
        post.ifPresent(message::setPost);
        if (!post.isPresent()) {
            message.setMessage(MSG_POST_NOT_FOUND);
        }
        return message;
    }

    @PostMapping(path = "/add")
    Message addPost(@RequestParam("title") String title, @RequestParam("content") String content) {
        final Post post = postService.newPost(title, content);
        Message message = new Message();
        message.setPost(post);
        return message;
    }

    @PostMapping(path = "/edit")
    Message editPost(@RequestParam("title") String title, @RequestParam("content") String content) {
        Message message = new Message();
        try {
            final Post post = postService.editPost(title, content);
            message.setPost(post);
        } catch (PostServiceException e) {
            message.setMessage(e.getMessage());
        }
        return message;
    }

    @DeleteMapping(path = "/delete", params = {"title"})
    Message deletePost(@RequestParam("title") String title) {
        Message message = new Message();
        try {
            postService.deletePost(title);
        } catch (PostServiceException e) {
            message.setMessage(e.getMessage());
        }
        return message;
    }
}

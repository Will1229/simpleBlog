package controller;

import config.ServiceConfiguration;
import entity.Message;
import entity.Post;
import exception.PostServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.PostService;

import java.util.List;
import java.util.Optional;

@RestController
@Import(ServiceConfiguration.class)
@RequestMapping(path = "/post")
public class PostController {

    public static final String MSG_POST_NOT_FOUND = "Post not found";

    @Autowired
    private PostService postService;

    @GetMapping(path = "/get", params = {"id"})
    ResponseEntity<Message> getPost(@RequestParam("id") Long id) {
        Optional<Post> post = postService.getPost(id);
        if (!post.isPresent()) {
            return new ResponseEntity<>(new Message(MSG_POST_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        ResponseEntity<Message> response = new ResponseEntity<>(new Message(), HttpStatus.OK);
        post.ifPresent(p -> response.getBody().getPosts().add(p));
        return response;
    }

    @GetMapping(path = "/get/all")
    ResponseEntity<Message> getAllPost() {
        List<Post> posts = postService.getAllPost();
        return new ResponseEntity<>(new Message(posts), HttpStatus.OK);
    }

    @GetMapping(path = "/search")
    ResponseEntity<Message> searchPost(@RequestParam("keyword") String keyword) {
        List<Post> posts = postService.searchPosts(keyword);
        return new ResponseEntity<>(new Message(posts), HttpStatus.OK);
    }

    @PostMapping(path = "/add")
    ResponseEntity<Message> addPost(Long userId, @RequestBody Post post) {
        final String title = post.getTitle();
        final String content = post.getContent();
        final Post p = postService.newPost(title, content);
        Message message = new Message();
        message.getPosts().add(p);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping(path = "/edit")
    ResponseEntity<Message> editPost(@RequestParam("id") Long id, @RequestBody Post post) {
        final String title = post.getTitle();
        final String content = post.getContent();
        Message message = new Message();
        try {
            postService.editPost(id, title, content);
        } catch (PostServiceException e) {
            message.setMessage(e.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete", params = {"id"})
    ResponseEntity<Message> deletePost(@RequestParam("id") Long id) {
        Message message = new Message();
        try {
            postService.deletePost(id);
        } catch (PostServiceException e) {
            message.setMessage(e.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}

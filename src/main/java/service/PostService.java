package service;

import entity.Post;
import entity.User;
import exception.PostServiceException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private List<Post> postList = new LinkedList<>();

    private User admin = new User("admin", "admin");

    public Optional<Post> getPost(String title) {
        return postList.stream().filter(post -> title.equals(post.getPostTitle())).findFirst();
    }

    public Post newPost(String title, String content) {
        final Post post = new Post(admin, title, content);
        postList.add(post);
        return post;
    }

    public Post editPost(String title, String content) throws PostServiceException {
        Optional<Post> post = getPost(title);
        post.ifPresent(p -> p.setContent(content));
        return post.orElseThrow(() -> new PostServiceException("post to edit not found"));
    }

    public void deletePost(String title) throws PostServiceException {
        Optional<Post> post = postList.stream().filter(p -> title.equals(p.getPostTitle())).findFirst();
        postList.remove(post.orElseThrow(() -> new PostServiceException("post to delete not found")));
    }

}

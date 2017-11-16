package service;

import entity.Post;
import entity.User;
import exception.PostServiceException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private List<Post> postList = new LinkedList<>();
    private static Long nextId = 0L;

    private User admin = new User("admin", "admin");

    public Optional<Post> getPost(Long id) {
        return postList.stream().filter(post -> id.equals(post.getId())).findFirst();
    }

    public Post newPost(String title, String content) {
        final Post post = new Post(admin, title, content);
        post.setId(nextId++);
        postList.add(post);
        return post;
    }

    public void editPost(Long id, String title, String content) throws PostServiceException {
        Optional<Post> post = getPost(id);
        post.ifPresent(p -> updatePost(p, title, content));
    }

    private void updatePost(final Post post, final String title, final String content) {
        post.setTitle(title);
        post.setContent(content);
    }

    public void deletePost(Long id) throws PostServiceException {
        Optional<Post> post = postList.stream().filter(p -> id.equals(p.getId())).findFirst();
        postList.remove(post.orElseThrow(() -> new PostServiceException("post to delete not found")));
    }

    public List<Post> getAllPost() {
        return postList;
    }

    public List<Post> searchPosts(final String keyword) {
        return postList.stream().filter(post -> post.getContent().contains(keyword) || post.getTitle().contains(keyword)).collect(Collectors.toList());
    }
}

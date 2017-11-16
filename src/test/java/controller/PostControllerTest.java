package controller;

import app.Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Message;
import entity.Post;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URL;

import static controller.PostController.MSG_POST_NOT_FOUND;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private URL baseUrl;

    @Before
    public void before() throws Exception {
        baseUrl = new URL("http://localhost:" + port + "/post");
    }

    @Test
    public void getEmptyPost() throws Exception {
        final Message message = getPost(999L);
        assertThat(message.getPosts().isEmpty(), is(true));
        assertThat(message.getMessage(), is(MSG_POST_NOT_FOUND));
    }

    @Test
    public void addAndGet() throws Exception {
        final String title1 = "title1";
        final String title2 = "title2";
        final String content = "content";
        Message message1 = (Message) addPost(title1, content);
        Message message2 = (Message) addPost(title2, content);
        final Long id1 = message1.getPosts().get(0).getId();
        Post post1 = getPost(id1).getPosts().get(0);
        assertThat(post1, is(notNullValue()));
        assertThat(post1.getTitle(), is(title1));
        assertThat(post1.getContent(), is(content));
        Post post2 = getPost(message2.getPosts().get(0).getId()).getPosts().get(0);
        assertThat(post2.getTitle(), is(title2));
        assertThat(post2.getContent(), is(content));
        assertThat(post2.getId(), is(post1.getId() + 1));
    }

    @Test
    public void edit() throws Exception {
        final String title = "title";
        final String content = "content";
        Message message = (Message) addPost(title, content);
        String newContent = "new content";
        final Long id = message.getPosts().get(0).getId();
        editPost(id, title, newContent);
        Post post = getPost(id).getPosts().get(0);
        assertThat(post, is(notNullValue()));
        assertThat(post.getContent(), is(newContent));
    }

    @Test
    public void delete() throws Exception {
        final String title = "title";
        final String content = "content";
        final Message message = (Message) addPost(title, content);
        final Long id = message.getPosts().get(0).getId();
        Post post = getPost(id).getPosts().get(0);
        assertThat(post, is(notNullValue()));
        deletePost(post.getId());
        assertThat(getPost(id).getMessage(), is(MSG_POST_NOT_FOUND));
    }

    @Test
    public void getAllPosts() {
        Message message = (Message) getAllPost();
        final int size = message.getPosts().size();
        final String title1 = "title1";
        final String title2 = "title2";
        final String content = "content";
        addPost(title1, content);
        addPost(title2, content);
        message = (Message) getAllPost();
        assertThat(message.getPosts().size(), is(size + 2));
    }

    @Test
    public void searchPosts() {
        Message message = searchPost("keyword");
        assertThat(message.getPosts().size(), is(0));

        final String title1 = "title with keyword";
        final String title2 = "title";
        final String title3 = "333";
        final String content1 = "content";
        final String content2 = "content with keyword";
        final String content3 = "333";
        addPost(title1, content1);
        addPost(title2, content2);
        addPost(title3, content3);
        message = searchPost("keyword");
        assertThat(message.getPosts().size(), is(2));
    }


    private Message getPost(Long id) {
        final ResponseEntity<Message> response = template.getForEntity(
                String.format("%s/get?id=%d", baseUrl.toString(), id), Message.class);
        return response.getBody();
    }

    private Object addPost(String title, String content) {
        HttpEntity<String> entity = createHttpEntity(title, content);
        final ResponseEntity<String> response = template.postForEntity(
                String.format("%s/add?userId=1", baseUrl.toString()), entity, String.class);
        return getObject(response, Message.class);
    }

    private void editPost(Long id, String title, String content) {
        HttpEntity<String> entity = createHttpEntity(title, content);
        template.put(String.format("%s/edit?id=%d", baseUrl.toString(), id), entity);
    }

    private void deletePost(Long id) {
        template.delete(String.format("%s/delete?id=%d", baseUrl.toString(), id), String.class);
    }

    private Object getAllPost() {
        final ResponseEntity<String> response = template.getForEntity(String.format("%s/get/all", baseUrl.toString()), String.class);
        return getObject(response, Message.class);
    }

    private Message searchPost(final String keyword) {
        final ResponseEntity<Message> response = template.getForEntity(String.format("%s/search?keyword=%s", baseUrl.toString(), keyword), Message.class);
        return response.getBody();
    }

    private HttpEntity<String> createHttpEntity(final String title, final String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Post post = new Post(null, title, content);
        try {
            return new HttpEntity<>(mapper.writeValueAsString(post), headers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getObject(final ResponseEntity<String> response, final Class<?> clazz) {
        final String body = response.getBody();
        try {
            return body == null ? null : mapper.readValue(body, clazz);
        } catch (IOException e) {
            return body;
        }
    }
}
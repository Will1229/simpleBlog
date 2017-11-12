package controller;

import app.Application;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URL;

import static controller.PostController.MSG_POST_NOT_FOUND;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
        final Message message = (Message) getPost("empty");
        assertThat(message.getPost(), is(nullValue()));
        assertThat(message.getMessage(), is(MSG_POST_NOT_FOUND));
    }

    @Test
    public void addAndGet() throws Exception {
        final String title = "title";
        final String content = "content";
        addPost(title, content);
        Post post = ((Message) getPost(title)).getPost();
        assertThat(post, is(notNullValue()));
        assertThat(post.getContent(), is(content));
    }

    @Test
    public void edit() throws Exception {
        final String title = "title";
        final String content = "content";
        addPost(title, content);
        String newContent = "new content";
        editPost(title, newContent);
        Post post = ((Message) getPost(title)).getPost();
        assertThat(post, is(notNullValue()));
        assertThat(post.getContent(), is(newContent));
    }

    @Test
    public void delete() throws Exception {
        final String title = "title";
        final String content = "content";
        addPost(title, content);
        Post post = ((Message) getPost(title)).getPost();
        assertThat(post, is(notNullValue()));
        deletePost(title);
        post = ((Message) getPost(title)).getPost();
        assertThat(post, is(nullValue()));
    }


    private Object getPost(String title) {
        final ResponseEntity<String> response = template.getForEntity(
                String.format("%s/get?title=%s", baseUrl.toString(), title), String.class);
        return getObject(response, Message.class);
    }

    private Object addPost(String title, String content) {
        final ResponseEntity<String> response = template.postForEntity(
                String.format("%s/add?title=%s&content=%s", baseUrl.toString(), title, content), null, String.class);
        return getObject(response, Message.class);
    }

    private Object editPost(String title, String content) {
        final ResponseEntity<String> response = template.postForEntity(
                String.format("%s/edit?title=%s&content=%s", baseUrl.toString(), title, content), null, String.class);
        return getObject(response, Message.class);
    }

    private void deletePost(String title) {
        template.delete(String.format("%s/delete?title=%s", baseUrl.toString(), title), String.class);
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
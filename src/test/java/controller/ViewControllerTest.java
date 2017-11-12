package controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ViewControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private URL baseUrl;

    @Before
    public void before() throws Exception {
        baseUrl = new URL("http://localhost:" + port);
    }

    @Test
    public void test() {

    }

}
package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.PostService;

@Configuration
public class ServiceConfiguration {

    @Bean
    PostService postService() {
        return new PostService();
    }
}

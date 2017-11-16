package entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Message {

    public Message(final String message) {
        this.message = message;
    }

    public Message(final List<Post> posts) {
        this.posts = posts;
    }

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private List<Post> posts = new ArrayList<>();
}


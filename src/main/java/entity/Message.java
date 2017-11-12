package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private Post post;
}


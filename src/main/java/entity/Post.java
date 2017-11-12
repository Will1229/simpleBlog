package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Post extends Base {

    private User user;

    private String postTitle;

    private String content;
}

package entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Base {
    private Long id;
    private Long version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
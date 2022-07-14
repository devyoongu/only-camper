package dev.practice.order.domain.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@Component
public class Book {
    private long id;

    private String title;

    private String author;
}

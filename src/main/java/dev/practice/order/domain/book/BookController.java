package dev.practice.order.domain.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("books")
@RequiredArgsConstructor
public class BookController {

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        BooksCreationDto booksForm = new BooksCreationDto(new ArrayList<Book>());

        for (int i = 1; i <= 3; i++) {
            booksForm.addBook(new Book());
        }

        model.addAttribute("form", booksForm);
        return "books/createBooksForm";
    }

    @PostMapping("/save")
    public String saveBooks(@ModelAttribute BooksCreationDto form, Model model) {
        log.info("111");
        return null;
    }
}

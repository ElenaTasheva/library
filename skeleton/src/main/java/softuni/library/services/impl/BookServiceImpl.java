package softuni.library.services.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.BookImportDto;
import softuni.library.models.entities.Author;
import softuni.library.models.entities.Book;
import softuni.library.repositories.AuthorRepository;
import softuni.library.repositories.BookRepository;
import softuni.library.services.BookService;
import softuni.library.util.ValidatorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {


    private final BookRepository bookRepository;
    private final ValidatorUtil validatorUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final String BOOK_PATH = "D:\\Users\\User\\Downloads\\Library_Skeleton\\skeleton\\src\\main\\resources\\files\\json\\books.json";
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, ValidatorUtil validatorUtil, ModelMapper modelMapper, Gson gson, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.validatorUtil = validatorUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.authorRepository = authorRepository;
    }

    @Override
    public boolean areImported() {
        return this.bookRepository.count() > 0;
    }

    @Override
    public String readBooksFileContent() throws IOException {
        return String.join(System.lineSeparator(), Files.readAllLines(Path.of(BOOK_PATH)));
    }

    @Override
    public String importBooks() throws IOException {
        StringBuilder sb = new StringBuilder();
        BookImportDto [] bookImportDtos = this.gson.fromJson(readBooksFileContent(), BookImportDto [].class);

        for (BookImportDto bookImportDto : bookImportDtos) {
            Optional<Author> author = this.authorRepository.findById(bookImportDto.getAuthor());
            if(this.validatorUtil.isValid(bookImportDto) && author.isPresent()){
                Book book = this.modelMapper.map(bookImportDto, Book.class);
                book.setAuthor(author.get());
                this.bookRepository.saveAndFlush(book);
                sb.append(String.format("Successfully imported Book: %s written in %s",
                        book.getName(), book.getWritten()));
            }
            else{
                sb.append("Invalid Book");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}

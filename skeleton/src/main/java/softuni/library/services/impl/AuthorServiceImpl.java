package softuni.library.services.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.AuthorImportDto;
import softuni.library.models.entities.Author;
import softuni.library.repositories.AuthorRepository;
import softuni.library.services.AuthorService;
import softuni.library.util.ValidatorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {


    private final AuthorRepository authorRepository;
    private final ValidatorUtil validatorUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final String AUTHOR_PATH = "D:\\Users\\User\\Downloads\\Library_Skeleton\\skeleton\\src\\main\\resources\\files\\json\\authors.json";


    public AuthorServiceImpl(AuthorRepository authorRepository, ValidatorUtil validatorUtil, ModelMapper modelMapper, Gson gson) {
        this.authorRepository = authorRepository;
        this.validatorUtil = validatorUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }


    @Override
    public boolean areImported() {
        return this.authorRepository.count() > 0;
    }

    @Override
    public String readAuthorsFileContent() throws IOException {
      return String.join("", Files.readAllLines(Path.of(AUTHOR_PATH)));
    }

    @Override
    public String importAuthors() throws IOException {
        StringBuilder sb = new StringBuilder();
        AuthorImportDto [] authorImportDtos = this.gson.fromJson(readAuthorsFileContent(), AuthorImportDto[].class);

        for (AuthorImportDto authorImportDto : authorImportDtos) {
            Optional<Author> author =  this.authorRepository.findByFirstNameAndLastName(authorImportDto.getFirstName(), authorImportDto.getLastName());
            if(this.validatorUtil.isValid(authorImportDto) && author.isEmpty()){
                this.authorRepository.saveAndFlush(this.modelMapper.map(authorImportDto, Author.class));
                sb.append(String.format("Successfully imported Author: %s %s - %s",authorImportDto.getFirstName(),
                        authorImportDto.getLastName(), authorImportDto.getBirthTown()));
            }
            else{
                sb.append("Invalid Author");
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString();

    }
}

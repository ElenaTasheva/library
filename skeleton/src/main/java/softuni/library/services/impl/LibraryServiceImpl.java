package softuni.library.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.LibraryImportDto;
import softuni.library.models.dtos.LibraryImportRootDto;
import softuni.library.models.entities.Book;
import softuni.library.models.entities.Library;
import softuni.library.repositories.BookRepository;
import softuni.library.repositories.CharacterRepository;
import softuni.library.repositories.LibraryRepository;
import softuni.library.services.LibraryService;
import softuni.library.util.ValidatorUtil;
import softuni.library.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final LibraryRepository libraryRepository;
    private final ValidatorUtil validatorUtil;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final String LIBRARY_PATH = "D:\\Users\\User\\Downloads\\Library_Skeleton\\skeleton\\src\\main\\resources\\files\\xml\\libraries.xml";
    private final BookRepository bookRepository;

    public LibraryServiceImpl(LibraryRepository libraryRepository, ValidatorUtil validatorUtil, ModelMapper modelMapper, XmlParser xmlParser, BookRepository bookRepository) {
        this.libraryRepository = libraryRepository;
        this.validatorUtil = validatorUtil;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean areImported() {
        return this.libraryRepository.count() > 0;
    }

    @Override
    public String readLibrariesFileContent() throws IOException {
        return String.join(System.lineSeparator(), Files.readAllLines(Path.of(LIBRARY_PATH)));
    }

    @Override
    public String importLibraries() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        LibraryImportRootDto libraryImportRootDto = this.xmlParser.parseXml(LibraryImportRootDto.class, LIBRARY_PATH);

        for (LibraryImportDto library : libraryImportRootDto.getLibraries()) {
            Optional<Book> book = this.bookRepository.findById(library.getBook().getId());
            Optional<Library> libraryByName = this.libraryRepository.findByName(library.getName());
            if(book.isPresent() && this.validatorUtil.isValid(library) && libraryByName.isEmpty()){
                Library library1 = this.modelMapper.map(library, Library.class);
                library1.getBooks().add(book.get());
                this.libraryRepository.saveAndFlush(library1);
                sb.append(String.format("Successfully added Library: %s - %s", library.getName(), library.getLocation()));
            }
            else{
                sb.append("Invalid Library");
            }
            sb.append(System.lineSeparator());

        }

        return sb.toString();
    }
}

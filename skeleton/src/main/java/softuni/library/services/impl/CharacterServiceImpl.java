package softuni.library.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.CharacterImportDto;
import softuni.library.models.dtos.CharacterImportRootDto;
import softuni.library.models.entities.Book;
import softuni.library.models.entities.Character;
import softuni.library.repositories.BookRepository;
import softuni.library.repositories.CharacterRepository;
import softuni.library.services.CharacterService;
import softuni.library.util.ValidatorUtil;
import softuni.library.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class CharacterServiceImpl implements CharacterService {

    private final CharacterRepository characterRepository;
    private final ValidatorUtil validatorUtil;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final String CHARACTER_PATH = "D:\\Users\\User\\Downloads\\Library_Skeleton\\skeleton\\src\\main\\resources\\files\\xml\\characters.xml";
    private final BookRepository bookRepository;


    public CharacterServiceImpl(CharacterRepository characterRepository, ValidatorUtil validatorUtil, ModelMapper modelMapper, XmlParser xmlParser, BookRepository bookRepository) {
        this.characterRepository = characterRepository;
        this.validatorUtil = validatorUtil;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean areImported() {
        return this.characterRepository.count() > 0;
    }

    @Override
    public String readCharactersFileContent() throws IOException {
        return String.join(System.lineSeparator(), Files.readAllLines(Path.of(CHARACTER_PATH)));

    }
        @Override
        public String importCharacters () throws JAXBException {
            StringBuilder sb = new StringBuilder();
            CharacterImportRootDto characterImportRootDto = this.xmlParser.parseXml(CharacterImportRootDto.class, CHARACTER_PATH);


            for (CharacterImportDto character : characterImportRootDto.getCharacters()) {
                Optional<Book> book = this.bookRepository.findById(character.getBook().getId());

                if(book.isPresent() && this.validatorUtil.isValid(character)){
                    Character character1 = this.modelMapper.map(character, Character.class);

                    character1.setBook(book.get());
                    this.characterRepository.saveAndFlush(character1);
                    sb.append(String.format("Successfully imported Character - %s %s %d", character.getFirstName(), character.getLastName(),
                            character.getAge()));
                }
                else{
                    sb.append("Invalid Character");
                }
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }


        @Override
        public String findCharactersInBookOrderedByLastNameDescendingThenByAge () {
        StringBuilder sb = new StringBuilder();
        List<Character> characterList = this.characterRepository.findAllByAgeBiggerThan32();
            for (Character character : characterList) {
                sb.append(String.format("Character name %s %s %s, age %d, in book %s\n",
                        character.getFirstName(), character.getMiddleName(), character.getLastName(),
                        character.getAge(), character.getBook().getName()));
            }

            return sb.toString();
        }
    }


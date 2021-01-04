package softuni.library.models.dtos;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "characters")
@XmlAccessorType(XmlAccessType.FIELD)
public class CharacterImportRootDto {

    @XmlElement(name = "character")
    private List<CharacterImportDto> characters;

    public CharacterImportRootDto() {
    }

    public List<CharacterImportDto> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterImportDto> characters) {
        this.characters = characters;
    }
}

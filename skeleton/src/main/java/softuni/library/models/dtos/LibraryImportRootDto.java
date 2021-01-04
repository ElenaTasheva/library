package softuni.library.models.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "libraries")
@XmlAccessorType(XmlAccessType.FIELD)
public class LibraryImportRootDto {

    @XmlElement(name = "library")
    private List<LibraryImportDto> libraries;

    public LibraryImportRootDto() {
    }

    public List<LibraryImportDto> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<LibraryImportDto> libraries) {
        this.libraries = libraries;
    }
}

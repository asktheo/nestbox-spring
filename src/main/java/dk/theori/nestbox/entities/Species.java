package dk.theori.nestbox.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Data
@Document(collection = "species")
@RepositoryRestResource(exported = false)
@Builder
public class Species {
    String localName;
    @Builder.Default
    boolean isBirdSpecies = true;
}

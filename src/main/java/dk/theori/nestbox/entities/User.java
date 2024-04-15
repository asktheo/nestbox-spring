package dk.theori.nestbox.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Data
@Document(collection = "user")
@RepositoryRestResource(exported = false)
public class User {
    private String username;
    private String password;
}

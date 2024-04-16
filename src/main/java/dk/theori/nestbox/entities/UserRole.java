package dk.theori.nestbox.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Modified from UserRole in medium article by Zeeshan Adil
 */
@Data
@Document(collection = "role")
@RepositoryRestResource(exported = false)
public class UserRole {

    @Id
    private long id;
    private String name;
}

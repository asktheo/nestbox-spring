package dk.theori.nestbox.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.HashSet;
import java.util.Set;

/**
 * Modified from UserInfo in medium article by Zeeshan Adil
 */

@Data
@Document(collection = "user")
@RepositoryRestResource(exported = false)
public class UserInfo {
    @Id
    private ObjectId _id;
    private String username;
    @JsonIgnore
    private String password;
    private Set<UserRole> roles = new HashSet<>();
}

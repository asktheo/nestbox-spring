package dk.theori.nestbox.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Data
@Document(collection = "status")
@RepositoryRestResource(exported = false)
public class NestBoxStatus {
    private String statusName;
    private Integer intervalInDaysSelected;
}

package dk.theori.nestbox.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Data
@Document(collection = "status")
@RepositoryRestResource(exported = false)
public class NestBoxStatus {
    @Id
    ObjectId id;
    String statusName;
    Integer intervalInDaysSelected;

    public NestBoxStatus(){
        this.statusName = "ny";
        this.intervalInDaysSelected = 21;
    }
}

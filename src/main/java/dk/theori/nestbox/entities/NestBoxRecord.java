package dk.theori.nestbox.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.Date;

@Data
@Document(collection = "record")
@RepositoryRestResource(exported = false)
public class NestBoxRecord {
    @Id
    ObjectId id;
    Integer fid;
    LocalDate datetime; //2024-03-27T19:25:00
    String comment;
    NestBoxStatus status;
    NestingDetails nesting;
    String[] rings;

}


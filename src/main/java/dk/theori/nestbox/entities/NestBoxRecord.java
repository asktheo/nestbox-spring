package dk.theori.nestbox.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "record")
@RepositoryRestResource(exported = false)
public class NestBoxRecord {
    @Id
    ObjectId id;
    Integer fid;
    @JsonFormat(locale="da_DK") //Makes sure that it is GMT+1 and summertime if necessary
    LocalDateTime datetime; //2024-03-27T19:25:00
    @JsonFormat(locale="da_DK")
    LocalDate recorddate;
    String comment;
    NestBoxStatus status;
    NestingDetails nesting;
    String[] rings;
    @JsonIgnore
    String userEmail; //not used. Present in import
}


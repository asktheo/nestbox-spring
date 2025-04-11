package dk.theori.nestbox.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;

@Data
@Document(collection = "repair")
@RepositoryRestResource(exported = false)
public class Repair {
    @Id
    ObjectId id;
    Integer fid;
    RepairType repairType;
    NestBoxProperties nestBoxProperties;

    Boolean isRepaired = false;

    @JsonFormat(locale="da_DK") //Makes sure that it is CET (or CEST)
    LocalDateTime createdAt = LocalDateTime.now();
    String comment;
    //on repair
    @JsonFormat(locale="da_DK") //Makes sure that it is CET (or CEST)
    LocalDateTime repairedAt;
    String repairComment;

}

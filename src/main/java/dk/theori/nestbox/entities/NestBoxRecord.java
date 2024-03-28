package dk.theori.nestbox.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
public class NestBoxRecord {
    @Id
    ObjectId record;
    Integer fid;
    String datetime;
    String comment;
    String[] rings;

}


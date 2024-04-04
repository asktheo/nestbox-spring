package dk.theori.nestbox.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.ArrayList;

@Data
@Document(collection = "zone")
@RepositoryRestResource(exported = false)
public class Zone {
    String zoneId;
    ArrayList<Integer> boxIds;
}

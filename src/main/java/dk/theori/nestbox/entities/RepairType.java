package dk.theori.nestbox.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Data
@Document(collection = "repairtype")
@RepositoryRestResource(exported = false)
public class RepairType {
    //1: Minor repairs (includes wire, screws, hangers)
    //2: Structural repairs (includes faceplate, roof, entry hole)
    //3: Installation (Faded boxes, new box)
    @Id
    private Integer repairTypeId;
    private String repairTypeName;
}

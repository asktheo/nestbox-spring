package dk.theori.nestbox.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "nestbox")
public class NestBox {

    @Id
    private ObjectId _id;
    NestBoxProperties properties;
    NestBoxGeometry geometry;
    String type;

    @JsonIgnore
    public String getPropertyBoxId(){
            return properties.getPropertyBoxId();
        }

    @JsonIgnore
    public NestBoxProperties getNestBoxProperties(){
        return this.getProperties();
    }
}

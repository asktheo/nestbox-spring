package dk.theori.nestbox.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@Data
@Document(collection = "nestbox")
@RepositoryRestResource(exported = false)
public class NestBox {

    @Id
    private ObjectId _id;
    private NestBoxProperties properties;
    private NestBoxGeometry geometry;
    private String type;

    private List<NestBoxRecord> records;

    private Integer repairLevel = 0;

    /*
    - unique identifier fid is nested
     */
    @JsonIgnore
    public Integer getFid(){
        return properties.getFid();
    }

    /* the queryable properties
        - altitude
        - boxId
        - zone
        are nested */
    @JsonIgnore
    public String getBoxId(){
            return properties.getBoxId();
        }

    @JsonIgnore
    public Integer getAltitude(){
        return properties.getAltitude();
    }

    @JsonIgnore
    public String getZone(){
        return properties.getZone();
    }

    @JsonIgnore
    public String getOrientation(){ return properties.getOrientation(); }

    /*
    @JsonIgnore
    public boolean getIsOffline(){
        return properties.getIsOffline();
    }

    public void setIsOffline(Boolean isOffline){
        properties.setIsOffline(isOffline);
    }
    */
}

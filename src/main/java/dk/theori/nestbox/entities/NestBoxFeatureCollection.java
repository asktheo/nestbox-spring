package dk.theori.nestbox.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) //added for Jackson Json - POJO in the nestboxesgeojson import
public class NestBoxFeatureCollection {

    ArrayList<NestBox> features;
    String name; //GeoJson standard
    String type; //GeoJSON standard
    private Object crs;



    public ArrayList<NestBox> getFeatures() {
        return this.features;
    }
}

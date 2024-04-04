package dk.theori.nestbox.entities;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;

@Data
public class NestBoxFeatureCollection {

    @Getter
    ArrayList<NestBox> features;
    String name; //GeoJson standard: name of featuretype

    String type; //GeoJSON standard: featurecollection

    Object crs; // GeoJson standard: Coordinate reference system

}

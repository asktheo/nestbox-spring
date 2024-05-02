package dk.theori.nestbox.entities;

import lombok.Data;

import java.util.ArrayList;

@Data
public class NestBoxFeatureCollection {

    ArrayList<NestBox> features;
    String typeName; //GeoJson standard

    String type; //GeoJSON standard



    public ArrayList<NestBox> getFeatures() {
        return this.features;
    }
}

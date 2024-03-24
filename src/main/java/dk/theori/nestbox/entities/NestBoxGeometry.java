package dk.theori.nestbox.entities;

import lombok.Data;

@Data
class NestBoxGeometry {
    String type; //place of Nestbox

    Double[] coordinates; // coordinate in WGS 84 lon,lat

}

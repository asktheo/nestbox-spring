package dk.theori.nestbox.entities;

import lombok.Data;

@Data
public class NestBoxProperties {

    private Integer fid; // id of geojson feature: 1 - 123,
    private Integer altitude; // 1 = LOW, 2 = HIGH,
    private String boxId; // real identificator of / number of box,
    private String zone; // the zone in which the box is located

}

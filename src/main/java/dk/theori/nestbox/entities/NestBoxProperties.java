package dk.theori.nestbox.entities;

import lombok.Data;

@Data
public class NestBoxProperties {

    Integer fid; //: 1,
    Integer altitude; // : 2,
    String boxId; //: "1",
    String zone; //: "4"

    String getPropertyBoxId() {
        return boxId;
    }
}

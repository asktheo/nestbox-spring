package dk.theori.nestbox.entities;

import lombok.Data;

import java.util.List;

@Data
public class NestBoxCheckList {
        List<NestBox> boxesForChecking;
        List<NestBox> boxesNotChecked;
        List<NestBox> boxesChecked;
}

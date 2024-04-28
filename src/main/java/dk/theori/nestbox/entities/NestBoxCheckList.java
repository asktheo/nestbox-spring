package dk.theori.nestbox.entities;

import lombok.Data;

import java.util.List;

@Data
public class NestBoxCheckList {
        List<NestBoxProperties> boxesForChecking;
        List<NestBoxProperties> boxesNotChecked;
        List<NestBoxProperties> boxesChecked;
}

package dk.theori.nestbox.entities;

import lombok.Data;

import java.util.List;

@Data
public class NestBoxPropertyCheckList {
        List<NestBoxProperties> boxesForChecking;
        List<NestBoxProperties> boxesNotChecked;
        List<NestBoxProperties> boxesChecked;
}

package dk.theori.nestbox.utils;

import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxCheckList;
import dk.theori.nestbox.entities.NestBoxProperties;
import dk.theori.nestbox.entities.NestBoxRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckCalculator {

    public static NestBoxCheckList calcuLatest(List<NestBox> boxes, Integer beforeInDays){

        LocalDateTime beforeDateTime = LocalDateTime.now().plusDays((beforeInDays == null) ? 7 : beforeInDays);

        //list for boxes to be checked within bounds for check criteria in data and request
        List<NestBoxRecord> latestRecordsForBoxesToBeChecked = new ArrayList<>();
        //list for boxes already checked and outside bounds for check criteria in data and request
        List<NestBoxRecord> latestRecordsForBoxesChecked = new ArrayList<>();
        //unchecked boxes
        List<NestBoxProperties> boxesNotChecked = new ArrayList<>();
        for(NestBox b : boxes){
            //add if the date to be checked is before the date constructed from the query (default now + 7d)
            if(!b.getRecords().isEmpty()) {
                NestBoxRecord latest = b.getRecords().get(0);
                if(latest.getDatetime().plusDays(latest.getStatus().getIntervalInDaysSelected()).isBefore(beforeDateTime)){
                    latestRecordsForBoxesToBeChecked.add(latest);
                }
                else{
                    latestRecordsForBoxesChecked.add(latest);
                }
            }
            else
                boxesNotChecked.add(b.getProperties());
        }

        //Hashmap for returning two lists with names:
       NestBoxCheckList nestBoxChecklist = new NestBoxCheckList();

        //match the records with NestBoxProperties
        List<NestBoxProperties> boxesForChecking = boxes.stream()
                .filter(p -> latestRecordsForBoxesToBeChecked.stream().anyMatch(r -> r.getFid() == p.getFid()))
                .map(NestBox::getProperties)
                .toList();

        List<NestBoxProperties> boxesChecked = boxes.stream()
                .filter(p -> latestRecordsForBoxesChecked.stream().anyMatch(t -> t.getFid() == p.getFid()))
                .map(NestBox::getProperties)
                .toList();

        //put results in HasHMap:
        nestBoxChecklist.setBoxesForChecking(boxesForChecking);
        nestBoxChecklist.setBoxesNotChecked(boxesNotChecked);
        nestBoxChecklist.setBoxesChecked(boxesChecked);
        return nestBoxChecklist;
    }

}

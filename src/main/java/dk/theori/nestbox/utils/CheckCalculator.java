package dk.theori.nestbox.utils;

import dk.theori.nestbox.entities.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckCalculator {

    public static NestBoxPropertyCheckList calcuLatest(List<NestBox> boxes, Integer beforeInDays){

        LocalDate beforeDate = LocalDate.now().plusDays((beforeInDays == null) ? 7 : beforeInDays);
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
                if(latest.getRecorddate().plusDays(latest.getStatus().getIntervalInDaysSelected()).isBefore(beforeDate)){
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
       NestBoxPropertyCheckList nestBoxChecklist = new NestBoxPropertyCheckList();

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

    public static NestBoxCheckList calcuLatest2(List<NestBox> boxes, Integer beforeInDays){

        LocalDate beforeDate = LocalDate.now().plusDays((beforeInDays == null) ? 7 : beforeInDays);
        LocalDateTime beforeDateTime = LocalDateTime.now().plusDays((beforeInDays == null) ? 7 : beforeInDays);

        //list for boxes to be checked within bounds for check criteria in data and request
        List<NestBoxRecord> latestRecordsForBoxesToBeChecked = new ArrayList<>();
        //list for boxes already checked and outside bounds for check criteria in data and request
        List<NestBoxRecord> latestRecordsForBoxesChecked = new ArrayList<>();
        //unchecked boxes
        List<NestBox> boxesNotChecked = new ArrayList<>();
        for(NestBox b : boxes){
            //add if the date to be checked is before the date constructed from the query (default now + 7d)
            if(!b.getRecords().isEmpty()) {
                NestBoxRecord latest = b.getRecords().get(0);
                if(latest.getRecorddate().plusDays(latest.getStatus().getIntervalInDaysSelected()).isBefore(beforeDate)){
                    latestRecordsForBoxesToBeChecked.add(latest);
                }
                else{
                    latestRecordsForBoxesChecked.add(latest);
                }
            }
            else
                boxesNotChecked.add(b);
        }

        //Hashmap for returning two lists with names:
        NestBoxCheckList nestBoxChecklist = new NestBoxCheckList();

        //match the records with NestBoxProperties
        List<NestBox> boxesForChecking = boxes.stream()
                .filter(p -> latestRecordsForBoxesToBeChecked.stream().anyMatch(r -> Objects.equals(r.getFid(), p.getFid())))
                .toList();

        List<NestBox> boxesChecked = boxes.stream()
                .filter(p -> latestRecordsForBoxesChecked.stream().anyMatch(t -> Objects.equals(t.getFid(), p.getFid())))
                .toList();

        //put results in HasHMap:
        nestBoxChecklist.setBoxesForChecking(boxesForChecking);
        nestBoxChecklist.setBoxesNotChecked(boxesNotChecked);
        nestBoxChecklist.setBoxesChecked(boxesChecked);
        return nestBoxChecklist;
    }

}

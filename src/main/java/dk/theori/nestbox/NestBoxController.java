package dk.theori.nestbox;

import dk.theori.nestbox.entities.*;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import dk.theori.nestbox.repositories.NestBoxRecordMongoRepository;
import dk.theori.nestbox.repositories.NestBoxStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController()
@RequestMapping("nestbox")
public class NestBoxController {

    @Autowired
    private NestBoxMongoRepository nestBoxMongoRepository;

    @Autowired
    private NestBoxRecordMongoRepository nestBoxRecordMongoRepository;

    @Autowired
    private NestBoxStatusRepository nestBoxStatusRepository;

    @GetMapping("")
    public List<NestBoxProperties> nestBoxProperties(
            @RequestParam(value ="boxId", required=false) String boxId,
            @RequestParam(value = "altitude", required=false) Integer altitude,
            @RequestParam(value = "zone", required=false) String zone){
        //find all and filter later
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getBoxId()))
                .filter(nestBox -> altitude == null || altitude.equals(nestBox.getAltitude()))
                .filter(nestBox -> zone == null || zone.equals(nestBox.getZone()))
                .map(NestBox::getProperties)
                .toList();
    }

    @GetMapping("feature")
    public List<NestBox> nestBoxFeatures(
            @RequestParam(value ="boxId", required=false) String boxId,
            @RequestParam(value = "altitude", required=false) Integer altitude,
            @RequestParam(value = "zone", required=false) String zone){
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getBoxId()))
                .filter(nestBox -> altitude == null || altitude.equals(nestBox.getAltitude()))
                .filter(nestBox -> zone == null || zone.equals(nestBox.getZone()))
                .toList();
    }

    @GetMapping("feature/{fid}")

    public NestBox nestBoxFeatureById(@PathVariable("fid") Integer fid){
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        Optional<NestBox> optNestBox = allNestBoxes
                .stream()
                .filter(nestBox -> fid.equals(nestBox.getFid()))
                .toList()
                .stream().findAny();
        return optNestBox.orElse(null);
    }

    @PostMapping("takedown/{fid}/{offline}")
    public NestBoxProperties setOffLine(@PathVariable("fid") Integer fid, @PathVariable("offline") Boolean offline){
        NestBox nestBox = nestBoxFeatureById(fid);
        if(nestBox != null) {
            NestBoxProperties props = nestBox.getProperties();
            props.setIsOffline(offline);
            this.nestBoxMongoRepository.save(nestBox);
            return nestBox.getProperties();
        }
        else return null;
    }

    @GetMapping("record/{fid}/new")
    public NestBoxRecord newNestBoxRecord(@PathVariable("fid") Integer fid){
        NestBoxRecord record = new NestBoxRecord();
        record.setFid(fid);
        record.setDatetime(LocalDateTime.now());
        record.setStatus(new NestBoxStatus());
        record.setNesting(new NestingDetails());
        //rings is an interval (from ring #, to ring #)
        record.setRings(new String[]{null, null});
        return record;
    }

    @GetMapping("record/{fid}/latest")
    public NestBoxRecord latestNestBoxRecord(@PathVariable("fid") Integer fid){
        Optional<NestBoxRecord> latestRecord = nestBoxRecordMongoRepository.findAll()
                .stream()
                .filter(rec -> fid.equals(rec.getFid()))
                .max(Comparator.comparing(NestBoxRecord::getDatetime));
        return latestRecord.orElse(null);
    }

    @PostMapping("record/add")
    public void record(@RequestBody() NestBoxRecord record){
        if(record.getDatetime() == null){ record.setDatetime(LocalDateTime.now());}

        this.nestBoxRecordMongoRepository.insert(record);
    }

    @PostMapping("status")
    public void addStatusType(@RequestBody() NestBoxStatus newStatusType){
       this.nestBoxStatusRepository.insert(newStatusType);
    }

    @GetMapping("status")
    public List<NestBoxStatus> getStatusTypes()
    {
        return this.nestBoxStatusRepository.findAll();
    }

    @GetMapping("checkme")
    public HashMap<String, List<NestBoxProperties>> getNestBoxesForChecking(@RequestParam(value ="before", required=false) Integer beforeInDays){
        LocalDateTime beforeDateTime = LocalDateTime.now().plusDays((beforeInDays == null) ? 7 : beforeInDays);

        //find records for all Nestboxes not offline
        List<NestBox> allActiveNestBoxes = this.nestBoxMongoRepository.findAll()
                .stream()
                .filter(nestBox -> !nestBox.getProperties().getIsOffline())
                .toList();
        List<NestBoxRecord> latestRecordsForActiveBoxes = new ArrayList<>();
        //unchecked boxes
        List<NestBoxProperties> boxesNotChecked = new ArrayList<>();
        for(NestBox b : allActiveNestBoxes){
            NestBoxRecord latest = latestNestBoxRecord(b.getFid());
            //add if the date to be checked is before the date constructed from the query (default now + 7d)
            if(latest != null && latest.getDatetime().plusDays(latest.getStatus().getIntervalInDaysSelected()).isBefore(beforeDateTime)){
                latestRecordsForActiveBoxes.add(latest);
            }
            else
                boxesNotChecked.add(b.getProperties());
        }
        //use controller method to get all box properties
        List<NestBoxProperties> allProperties = nestBoxProperties(null,null,null);

        //Hashmap for returning two lists with names:
        HashMap returnLists = new HashMap<String, List<NestBoxProperties>>();

        //match the records with NestBoxProperties
        List<NestBoxProperties> boxesForChecking = allProperties.stream()
                .filter(p -> !p.getIsOffline())
                .filter(p -> latestRecordsForActiveBoxes.stream().anyMatch(r -> r.getFid() == p.getFid()))
                .toList();

        //put results in HasHMap:
        returnLists.put("boxesForChecking", boxesForChecking);
        returnLists.put("boxesNotChecked", boxesNotChecked);

        return returnLists;

    }




}

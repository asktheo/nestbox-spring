package dk.theori.nestbox;

import dk.theori.nestbox.entities.*;
import dk.theori.nestbox.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private ZoneMongoRepository zoneMongoRepository;

    @Autowired
    private SpeciesMongoRepository speciesMongoRepository;

    @GetMapping("")
    public List<NestBoxProperties> nestBoxProperties(
            @RequestParam(value ="boxId", required=false) String boxId,
            @RequestParam(value = "altitude", required=false) Integer altitude,
            @RequestParam(value = "zone", required=false) String zone,
            @RequestParam(value = "offline", required=false) Boolean offline){
        //find all and filter later
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getBoxId()))
                .filter(nestBox -> altitude == null || altitude.equals(nestBox.getAltitude()))
                .filter(nestBox -> zone == null || zone.equals(nestBox.getZone()))
                .filter(nestBox -> offline == null || offline.equals(nestBox.getIsOffline()))
                .map(NestBox::getProperties)
                .toList();
    }

    @GetMapping("feature")
    public List<NestBox> nestBoxFeatures(
            @RequestParam(value ="boxId", required=false) String boxId,
            @RequestParam(value = "altitude", required=false) Integer altitude,
            @RequestParam(value = "zone", required=false) String zone,
            @RequestParam(value = "offline", required=false) Boolean offline){
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getBoxId()))
                .filter(nestBox -> altitude == null || altitude.equals(nestBox.getAltitude()))
                .filter(nestBox -> zone == null || zone.equals(nestBox.getZone()))
                .filter(nestBox -> offline == null || !offline.equals(nestBox.getIsOffline()))
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
    public ResponseEntity<String> record(@RequestBody() NestBoxRecord record){
        if(record.getDatetime() == null){ record.setDatetime(LocalDateTime.now());}
        if(record.getStatus() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else try{
            record.setStatus(fixStatus(record));
        }
        catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.nestBoxRecordMongoRepository.insert(record);
        return new ResponseEntity<>(HttpStatus.CREATED);
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
        //list for boxes to be checked within bounds for check criteria in data and request
        List<NestBoxRecord> latestRecordsForBoxesToBeChecked = new ArrayList<>();
        //list for boxes already checked and outside bounds for check criteria in data and request
        List<NestBoxRecord> latestRecordsForBoxesChecked = new ArrayList<>();
        //unchecked boxes
        List<NestBoxProperties> boxesNotChecked = new ArrayList<>();
        for(NestBox b : allActiveNestBoxes){
            NestBoxRecord latest = latestNestBoxRecord(b.getFid());
            //add if the date to be checked is before the date constructed from the query (default now + 7d)
            if(latest != null) {
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
        //use controller method to get all box properties
        List<NestBoxProperties> pAllActiveBoxes = nestBoxProperties(null,null,null, false);

        //Hashmap for returning two lists with names:
        HashMap returnLists = new HashMap<String, List<NestBoxProperties>>();

        //match the records with NestBoxProperties
        List<NestBoxProperties> boxesForChecking = pAllActiveBoxes.stream()
                .filter(p -> latestRecordsForBoxesToBeChecked.stream().anyMatch(r -> r.getFid() == p.getFid()))
                .toList();

        List<NestBoxProperties> boxesChecked = pAllActiveBoxes.stream()
                .filter(p -> latestRecordsForBoxesChecked.stream().anyMatch(t -> t.getFid() == p.getFid()))
                .toList();

        //put results in HasHMap:
        returnLists.put("boxesForChecking", boxesForChecking);
        returnLists.put("boxesNotChecked", boxesNotChecked);
        returnLists.put("boxesChecked", boxesChecked);

        return returnLists;

    }

    private NestBoxStatus fixStatus(NestBoxRecord box) throws NoSuchElementException{
        List<NestBoxStatus> allowedStatus = nestBoxStatusRepository.findAll();
        Optional<NestBoxStatus> status1 = allowedStatus.stream()
                .filter(s-> s.getStatusName().equals(box.getStatus().getStatusName())).findAny();
        return status1.get();
    }

    @GetMapping("zones")
    public List<Zone> getZones(){
        return zoneMongoRepository.findAll();
    }

    @GetMapping("species")
    public List<Species> getAllSpecies() {
        return speciesMongoRepository.findAll();
    }

    @PostMapping("species")
    public ResponseEntity<String> addSpecies(@RequestBody() Species species){
        speciesMongoRepository.insert(species);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



}

package dk.theori.nestbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.theori.nestbox.entities.*;
import dk.theori.nestbox.inout.XSLGenerator;
import dk.theori.nestbox.repositories.*;
import dk.theori.nestbox.utils.CheckCalculator;
import dk.theori.nestbox.utils.ImportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin
@RestController()
@Slf4j
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

    @Autowired
    private RepairController repairController;

    @GetMapping("")
    public List<NestBoxProperties> nestBoxProperties(
            @RequestParam(value ="boxId", required=false) String boxId,
            @RequestParam(value = "altitude", required=false) Integer altitude,
            @RequestParam(value = "zone", required=false) String zone
           // @RequestParam(value = "offline", required=false) Boolean offline
    ){
        //find all and filter later
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getBoxId()))
                .filter(nestBox -> altitude == null || altitude.equals(nestBox.getAltitude()))
                .filter(nestBox -> zone == null || zone.equals(nestBox.getZone()))
                //.filter(nestBox -> offline == null || offline.equals(nestBox.getIsOffline()))
                .map(NestBox::getProperties)
                .toList();
    }

    @GetMapping("feature")
    public List<NestBox> nestBoxFeatures(
            @RequestParam(value ="boxId", required=false) String boxId,
            @RequestParam(value = "altitude", required=false) Integer altitude,
            @RequestParam(value = "zone", required=false) String zone
            //@RequestParam(value = "offline", required=false) Boolean offline
            ){
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getBoxId()))
                .filter(nestBox -> altitude == null || altitude.equals(nestBox.getAltitude()))
                .filter(nestBox -> zone == null || zone.equals(nestBox.getZone()))
                //.filter(nestBox -> offline == null || !offline.equals(nestBox.getIsOffline()))
                .toList();
    }

    @GetMapping("feature/{fid}")

    public NestBox nestBoxFeatureById(@PathVariable("fid") Integer fid){
        return nestBoxMongoRepository.findByPropertiesFid(fid);
    }


    @GetMapping("record/{fid}/new")
    public ResponseEntity newNestBoxRecord(@PathVariable("fid") Integer fid){
        NestBoxRecord record = new NestBoxRecord();
        record.setFid(fid);
        record.setRecorddate(LocalDate.now());
        record.setStatus(new NestBoxStatus());
        record.setNesting(new NestingDetails());
        //rings is an interval (from ring #, to ring #)
        record.setRings(new String[]{null, null});
        HashMap<String,Object> responseMap = new HashMap<>();
        //I don't want a new entity with mix a of objects, so I just make a HashMap
        responseMap.put("record",record);
        ResponseEntity<Repair> resp = repairController.getRepairByFid(fid);
        boolean hasRepair = false;
        if(resp.getStatusCode().value() < 400) {
            responseMap.put("repairType", resp.getBody().getRepairType());
            hasRepair = true;
        }
        responseMap.put("hasRepair", hasRepair);

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("record/{fid}/latest")
    public NestBoxRecord latestNestBoxRecord(@PathVariable("fid") Integer fid){
        Optional<NestBoxRecord> latestRecord = nestBoxRecordMongoRepository.findByFid(fid).stream()
                .max(Comparator.comparing(NestBoxRecord::getDatetime));
        return latestRecord.orElse(null);
    }

    @PostMapping("record/add")
    public ResponseEntity<String> record(@RequestBody() NestBoxRecord record){
        if(record.getDatetime() == null){ record.setDatetime(LocalDateTime.now());}
        if(record.getRecorddate() == null) {record.setRecorddate((LocalDate.now()));}
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
    public NestBoxCheckList getNestBoxesForChecking(@RequestParam(value ="before", required=false) Integer beforeInDays){

        //use controller method to get all box properties
        List<NestBox> pAllBoxes = this.nestBoxMongoRepository.findAll();
        //assertFalse(pAllBoxes.isEmpty());
        for(NestBox b : pAllBoxes){
            List<NestBoxRecord> records = new ArrayList<>();
            //TODO implement that all records for the box can be added here
            NestBoxRecord record = latestNestBoxRecord(b.getFid());
            if (record != null)
                records.add(record);
            b.setRecords(records);
            ResponseEntity<Repair> resp = repairController.getRepairByFid(b.getFid());
            b.setRepairLevel(resp.getStatusCode().value() > 400
                    ? 0
                    : (resp.getBody()).getRepairType().getRepairTypeId());
        }
        return CheckCalculator.calcuLatest2(pAllBoxes, beforeInDays);
    }

    @GetMapping("checkme2")
    public NestBoxCheckList getNestBoxesForChecking2(@RequestParam(value ="before", required=false) Integer beforeInDays){

        //fix: use repository method to get all boxes
        List<NestBox> pAllBoxes = this.nestBoxMongoRepository.findAll();
        //assertFalse(pAllBoxes.isEmpty());
        for(NestBox b : pAllBoxes){
            List<NestBoxRecord> records = new ArrayList<>();
            //TODO implement that all records for the box can be added here
            NestBoxRecord record = latestNestBoxRecord(b.getFid());
            if (record != null)
                records.add(record);
            b.setRecords(records);
        }

        return CheckCalculator.calcuLatest2(pAllBoxes, beforeInDays);
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

    @GetMapping("records")
    public List<NestBox> activeNestBoxesWithRecords(){
        //use controller method to get all box properties
        //List<NestBox> pAllActiveBoxes = this.nestBoxMongoRepository.findByPropertiesIsOfflineFalse();
        List<NestBox> pAllBoxes = this.nestBoxMongoRepository.findAll();
        //List<NestBoxRecord> allRecords = this.nestBoxRecordMongoRepository
        //        .findAll();
        for(NestBox b : pAllBoxes){
            List<NestBoxRecord> allRecords = this.nestBoxRecordMongoRepository.findByFid(b.getFid());
            //b.setRecords(allRecords.stream().filter(r -> r.getFid() == b.getFid()).toList());
            b.setRecords(allRecords);
        }
        return pAllBoxes;
    }

    @PostMapping("records/translate")
    public List<NestBoxRecord> translateRecords(@RequestBody() String tsv) throws JsonProcessingException {
            return tsv2Records(tsv)
                    .stream()
                    .filter(p -> p.getTidsstempel() != null)
                    .map(p -> {
                NestBoxRecord r = new NestBoxRecord();
                NestingDetails nd = new NestingDetails();
                nd.setChicks(p.getUnger() == null || p.getUnger().isEmpty() ? null : Integer.parseInt(p.getUnger()));
                nd.setEggs(p.getÆg() == null || p.getÆg().isEmpty() ? null : Integer.parseInt(p.getÆg()));
                nd.setSpecies(p.getArt());
                r.setNesting(nd);
                Optional<NestBox> nb = nestBoxMongoRepository.findAll().stream().filter(n -> n.getBoxId().equals(p.getKasse_nummer())).findAny();
                nb.ifPresent(nestBox -> r.setFid(nestBox.getFid()));
                NestBoxStatus status = new NestBoxStatus();
                status.setStatusName(p.getStatus());
                List<NestBoxStatus> statuses = nestBoxStatusRepository.findAll().stream().toList();
                Optional<NestBoxStatus> foundStatus = statuses.stream().filter(s -> s.getStatusName().equalsIgnoreCase(p.getStatus())).findAny();
                foundStatus.ifPresent(nestBoxStatus -> status.setIntervalInDaysSelected(nestBoxStatus.getIntervalInDaysSelected()));
                r.setStatus(status);
                r.setComment(p.getBemærkninger());//tidsstempel
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH.mm.ss").localizedBy(new Locale("da", "DK"));
                LocalDateTime dateTime = LocalDateTime.parse(p.getTidsstempel(), formatter);
                r.setDatetime(dateTime);
                //dato
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").localizedBy(new Locale("da","DK"));
                r.setRecorddate(LocalDate.parse(p.getDato(), dateFormatter));
                r.setUserEmail(p.getMailadresse());
                return r;
            }).toList();

    }
    @PostMapping("records/import")
    public List<NestBoxRecord> importRecords(@RequestBody() String tsv) throws JsonProcessingException {
        List<NestBoxRecord> records2Import=translateRecords(tsv);
        nestBoxRecordMongoRepository.insert(records2Import);
    return nestBoxRecordMongoRepository.findAll();
    }

    @GetMapping("records/download")
    public ResponseEntity<byte[]> downloadRecords(){

        List<NestBox> allNestboxes = nestBoxMongoRepository.findAll();
        //List<NestBoxRecord> records = nestBoxRecordMongoRepository.findAll();
        for(NestBox b : allNestboxes) {
            List<NestBoxRecord> records = nestBoxRecordMongoRepository.findByFid(b.getFid());
            //b.setRecords(records.stream().filter(r -> r.getFid() == b.getFid()).toList());
            b.setRecords(records);
        }

        ByteArrayOutputStream out = XSLGenerator.generateXSLRecords(allNestboxes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "redekassetjek.xlsx");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }

    private List<RecordFromTSV> tsv2Records(String tsv) throws JsonProcessingException {

        String json = ImportUtil.TSV2Json(tsv);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, new TypeReference<>(){});

    }

    @GetMapping("download/checkme")
    public ResponseEntity<byte[]>  downloadBoxesForChecking(@RequestParam(value ="before", required=false) Integer beforeInDays){

        NestBoxCheckList nestBoxesForChecking = getNestBoxesForChecking(beforeInDays);

        ByteArrayOutputStream out = XSLGenerator.generateXSLCheckList(nestBoxesForChecking, beforeInDays);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "tjeklister.xlsx");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }



}

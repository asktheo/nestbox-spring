package dk.theori.nestbox;

import dk.theori.nestbox.entities.*;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import dk.theori.nestbox.repositories.NestBoxRecordMongoRepository;
import dk.theori.nestbox.repositories.NestBoxStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        NestBoxRecord record = new NestBoxRecord(fid);
        record.setStatus(new NestBoxStatus());
        record.setNesting(new NestingDetails());
        return record;
    }

    @PostMapping("record/add")
    public void record(@RequestBody() NestBoxRecord record){
        this.nestBoxRecordMongoRepository.insert(record);
    }

    @PostMapping("status")
    public void statusType(@RequestBody() NestBoxStatus newStatusType){
       this.nestBoxStatusRepository.insert(newStatusType);
    }


}

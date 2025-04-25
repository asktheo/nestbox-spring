package dk.theori.nestbox;

import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxProperties;
import dk.theori.nestbox.entities.Repair;
import dk.theori.nestbox.entities.RepairType;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import dk.theori.nestbox.repositories.RepairRepository;
import dk.theori.nestbox.repositories.RepairTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("repair")
public class RepairController {

    @Autowired
    private RepairTypeRepository repairTypeRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private NestBoxMongoRepository nestBoxMongoRepository;

    @Autowired
    private ApplicationContext context;

    @PostMapping("type")
    public void setNewRepairType(@RequestBody() RepairType newRepairType){
        this.repairTypeRepository.insert(newRepairType);
    }

    @GetMapping("type")
    public List<RepairType> getRepairTypes()
    {
        return this.repairTypeRepository.findAll();
    }

    @PostMapping("type/insertmany")
    public void insertRepairType(@RequestBody() List<RepairType> repairTypes){
        this.repairTypeRepository.insert(repairTypes);
    }

    /*
    Nestbox repairs
     */
    @GetMapping("/{fid}")
    public ResponseEntity<Repair> getRepairByFid(@PathVariable("fid") Integer fid){
        /**
         * get the repair entity of (repair.fid = fid and isRepaired = false)
         */
        List<Repair> repair = this.repairRepository.findByFid(fid).stream()
                .filter(it-> !it.getIsRepaired()).toList();
        if(repair.isEmpty()) return ResponseEntity.notFound().build();
        else {
            Repair rep = repair.get(0);
            RepairType repairType = rep.getRepairType();
            repairType.setRepairTypeName(repairTypeRepository.findByRepairTypeId(repairType.getRepairTypeId()).getRepairTypeName());
            return ResponseEntity.ok(repair.get(0));
        }
    }




    @GetMapping("list")
    public List<Repair> repairList(){
        return this.repairRepository.findByIsRepairedFalse();
    }

    @GetMapping("candidates")
    public List<NestBoxProperties> nestboxCandidates(){
        //hent redekasser, der er til reparation
        List<Integer> repairList = this.repairRepository.findByIsRepairedFalse().stream()
                .map(Repair::getFid).toList();
        //filtrer liste over redekasser, med dem der er til reparation
        List<NestBox> boxCandidates = nestBoxMongoRepository.findAll().stream()
                .filter(nestBox -> !repairList.contains(nestBox.getProperties().getFid())).toList();
        return boxCandidates.stream().map(NestBox::getProperties).toList();
    }

    @GetMapping("new")
    public Repair newRepair(){
        Repair repair = new Repair();
        repair.setRepairType(new RepairType());
        return repair;
    }

    @PostMapping("update")
    public ResponseEntity<Object> updateRepair(@RequestBody() Repair repair) {
        //hvis id ikke er null, er det en update
        if (repair.getId() != null) {
            //hent redekasser, der er til reparation
            List<Repair> repairList = this.repairRepository.findByIsRepairedFalse().stream()
                    .filter(it -> repair.getFid().equals(it.getFid())).toList();
            if (repairList.isEmpty()) return ResponseEntity.notFound().build();
            Repair updateable = repairList.get(0);
            updateable.setIsRepaired(repair.getIsRepaired());
            if (repair.getIsRepaired()) {
                updateable.setRepairComment(repair.getComment());
                updateable.setRepairedAt(LocalDateTime.now());
            } else {
                updateable.setComment(repair.getComment());
                updateable.setRepairType(repair.getRepairType());
            }
            this.repairRepository.save(updateable);
        }
        else { //det er en add operation
            NestBoxProperties nestBoxProperties = this.nestBoxMongoRepository.findByPropertiesFid(repair.getFid()).getProperties();
            repair.setNestBoxProperties(nestBoxProperties);
            this.repairRepository.insert(repair);
        }

        return ResponseEntity.ok(null);
    }


}

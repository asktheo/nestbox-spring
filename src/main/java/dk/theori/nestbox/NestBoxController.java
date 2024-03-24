package dk.theori.nestbox;

import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxProperties;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController()
@RequestMapping("nestbox")
public class NestBoxController {

    @Autowired
    private NestBoxMongoRepository nestBoxMongoRepository;

    @RequestMapping("")
    public List<NestBoxProperties> nestBoxProperties(@RequestParam(value ="boxId", required=false) String boxId){
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getPropertyBoxId()))
                .map(NestBox::getNestBoxProperties)
                .toList();
    }

    @RequestMapping("feature")
    public List<NestBox> nestBoxFeatures(@RequestParam(value = "boxId", required=false) String boxId) {
        List<NestBox> allNestBoxes = nestBoxMongoRepository.findAll();

        return allNestBoxes
                .stream()
                .filter(nestBox -> boxId == null || boxId.equals(nestBox.getPropertyBoxId()))
                .toList();
    }



}

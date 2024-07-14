package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.NestBox;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NestBoxMongoRepository extends MongoRepository<NestBox, Integer> {

    List<NestBox> findByPropertiesIsOfflineFalse();

    NestBox findByPropertiesFid(Integer fid);

    NestBox findByPropertiesBoxId(String boxId);

}

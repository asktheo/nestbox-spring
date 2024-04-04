package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.Zone;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ZoneMongoRepository extends MongoRepository<Zone, Integer> {

}

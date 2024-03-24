package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.NestBox;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NestBoxMongoRepository extends MongoRepository<NestBox, Integer> {

}

package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.NestBoxRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NestBoxRecordMongoRepository extends MongoRepository<NestBoxRecord, Integer> {

}

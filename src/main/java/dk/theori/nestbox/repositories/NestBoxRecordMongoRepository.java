package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.NestBoxRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NestBoxRecordMongoRepository extends MongoRepository<NestBoxRecord, Integer> {

    List<NestBoxRecord> findByFid(Integer fid);

}

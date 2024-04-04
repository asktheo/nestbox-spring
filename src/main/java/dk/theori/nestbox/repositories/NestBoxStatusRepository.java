package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.NestBoxStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NestBoxStatusRepository extends MongoRepository<NestBoxStatus, Integer> {
}

package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.RepairType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepairTypeRepository extends MongoRepository<RepairType, Integer> {

}


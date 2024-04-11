package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.Species;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpeciesMongoRepository extends MongoRepository<Species, Integer> {

}

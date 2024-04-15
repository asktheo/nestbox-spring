package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}

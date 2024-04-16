package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserInfo, String> {
    UserInfo findByUsername(String username);
}

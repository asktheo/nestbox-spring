package dk.theori.nestbox.repositories;

import dk.theori.nestbox.entities.Repair;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepairRepository extends MongoRepository<Repair, Object> {

    List<Repair> findByIsRepairedFalse();

    List<Repair> findByFid(Integer fid);

}


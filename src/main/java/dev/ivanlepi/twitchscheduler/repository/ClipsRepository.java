package dev.ivanlepi.twitchscheduler.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import dev.ivanlepi.twitchscheduler.models.Clip;

public interface ClipsRepository extends MongoRepository<Clip, String> {
    
}

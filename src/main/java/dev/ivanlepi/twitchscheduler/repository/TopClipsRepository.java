package dev.ivanlepi.twitchscheduler.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import dev.ivanlepi.twitchscheduler.models.Clip;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "topclips")
public interface TopClipsRepository extends MongoRepository<Clip, String> {
    
}

package dev.ivanlepi.twitchscheduler.repository;

import dev.ivanlepi.twitchscheduler.models.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
}

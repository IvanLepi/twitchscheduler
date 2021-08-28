package dev.ivanlepi.twitchscheduler.services;

import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import dev.ivanlepi.twitchscheduler.models.Game;
import dev.ivanlepi.twitchscheduler.repository.ClipsRepository;
import dev.ivanlepi.twitchscheduler.repository.GameRepository;
import dev.ivanlepi.twitchscheduler.repository.TopClipsRepository;
import dev.ivanlepi.twitchscheduler.models.Feed;
import dev.ivanlepi.twitchscheduler.models.ClipsFeed;
import dev.ivanlepi.twitchscheduler.models.Clip;


@Service
public class Twitch extends ApiBinding {

    private static final String TWITCH_API_BASE_URL = "https://api.twitch.tv/helix";

    private final GameRepository gameRepository;
    private final ClipsRepository clipsRepository;
    private final TopClipsRepository topClipsRepository;

 
    public Twitch(String accessToken, GameRepository gameRepository, ClipsRepository clipsRepository, TopClipsRepository topClipsRepository) {
        super(accessToken);
        this.gameRepository = gameRepository;
        this.clipsRepository = clipsRepository;
        this.topClipsRepository = topClipsRepository;
    }

    /**
     * This method updates our database with top 100 games from Twitch API.
     * 
     * @return List<Game>
     */
    public List<Game> updateGames() {
        List<Game> listOfGames = restTemplate.getForObject(TWITCH_API_BASE_URL + "/games/top?first=100", Feed.class)
                .getData();

        // Empty the database to see if it is updating properly
        gameRepository.deleteAll();

        // Iterate over List of games and update the database
        for (Game game : listOfGames) {
            gameRepository.save(game);
        }
        return listOfGames;
    }

    /**
     * This method updates our database with clips for a particular Game.
     * 
     * @param game_id Every Game has its own game_id field.
     */
    @Async
    public void getAsyncClips(String game_id, Optional<String> startDate) throws InterruptedException {

        List<Clip> listOfClips;

        LOG.info("Looking up clips {}", game_id);

        if (startDate.isEmpty()) {
            listOfClips = restTemplate
                    .getForObject(TWITCH_API_BASE_URL + "/clips/?game_id=" + game_id + "&first=100", ClipsFeed.class)
                    .getData();
        } else {
            listOfClips = restTemplate.getForObject(
                    TWITCH_API_BASE_URL + "/clips/?game_id=" + game_id + "&first=100" + "&started_at=" + startDate.get(),
                    ClipsFeed.class).getData();
        }

        // Iterate over list of clips and update the database
        for (Clip clip : listOfClips) {
            if(startDate.isEmpty()){
                clipsRepository.save(clip);
            } else {
                topClipsRepository.save(clip);
            }
            
        }
    }

    public void cleanDb(boolean trending) {
        if(trending){
            topClipsRepository.deleteAll();
        }else {
            clipsRepository.deleteAll();
        }
    }
}

package dev.ivanlepi.twitchscheduler.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import dev.ivanlepi.twitchscheduler.models.Game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);

    private final Twitch twitchService;

    public ScheduledTasks(Twitch twitchService) {
        this.twitchService = twitchService;
    }

    // Update our database with Top Clips every day at 1 AM.
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateTopClips() {
        LOG.info("Updating Top Clips");
        updateDb(false); // TODO REFACTOR THIS METHOD with something other than true/false.
    }

    // Update our database with Trending Clips every 3 hours.
    @Scheduled(cron = "0 0 */3 ? * *")
    public void updateTrendingClips() {
        LOG.info("Updating Trending Clips");
        updateDb(true); // TODO REFACTOR THIS METHOD with something other than true/false.
    }

    private void updateDb(Boolean trending) {

        try {
            List<Game> listOfGames = twitchService.updateGames();

            for (Game game : listOfGames) {
                if (!trending) {
                    twitchService.getAsyncClips(game.getId(), Optional.empty());
                } else {
                    twitchService.getAsyncClips(game.getId(), Optional.of(startDate()));
                }

            }

        } catch (Exception e) {
            LOG.info("ERROR BRO" + e.getMessage());
        }

    }

    /**
     * @return Formatted date string suitable for Twitch endpoints.
     */
    // Calculate 48 hours earlier date.
    private String startDate() {
        long currentDate = new Date().getTime();
        long newDate = currentDate - 172800000;
        return dateFormat.format(new Date(newDate));
    }
}

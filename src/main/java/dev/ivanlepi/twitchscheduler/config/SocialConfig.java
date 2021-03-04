package dev.ivanlepi.twitchscheduler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ivanlepi.twitchscheduler.repository.ClipsRepository;
import dev.ivanlepi.twitchscheduler.repository.GameRepository;
import dev.ivanlepi.twitchscheduler.services.Twitch;

@Configuration
public class SocialConfig {

	@Autowired
	Environment env;

	private GameRepository gameRepo;
	private ClipsRepository clipRepo;

	final Logger log = LoggerFactory.getLogger(SocialConfig.class);

	@Bean
	public Twitch twitch(GameRepository gameRepository, ClipsRepository clipRepository) {
		this.gameRepo = gameRepository;
		this.clipRepo = clipRepository;
		String accessToken = "";
		try {
			log.info("Obtaining new access Token");

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "client_credentials");
			params.add("client_id", env.getProperty("client_id"));
			params.add("client_secret", env.getProperty("client_secret"));

			WebClient.RequestHeadersSpec<?> requestSpec1 = WebClient.create().post()
					.uri("https://id.twitch.tv/oauth2/token").body(BodyInserters.fromValue(params));

			String response3 = requestSpec1.retrieve().bodyToMono(String.class).block();

			// Get the Access Token From the recieved JSON response
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response3);
			String token = node.path("access_token").asText();

			accessToken = token;

		} catch (Exception e) {
			log.info("Failed to obtain access Token");
			e.printStackTrace();
		}
		return new Twitch(accessToken, gameRepo, clipRepo);
	}
}

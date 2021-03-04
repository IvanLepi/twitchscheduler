# Twitch Scheduler

## [Part of Twitch Clips Application](https://github.com/IvanLepi/twitchclips)

This app is a task scheduler for Twitch Clips application. It uses spring OAuth2 to authenticate against Twitch servers and afterwards intercept all web requests sent to Twitch API endpoints and insert generated Bearer Token that is required.

The data is stored in MongoDB database that is a Mongo Docker Container. 

For more info look at the more detailed readme at [Twitch Clips Application](https://github.com/IvanLepi/twitchclips)




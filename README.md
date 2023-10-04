# Server Integration with Dicord
This plugin is designed to make the administration's work more pleasant and efficient by sending reports to a selected discord channel.
- Logs of commands used by players (in addition to /login, /register, /changepassword etc.).
It also offers a plugin to check how much time players have spent on the server

 

 

The plugin at this stage of development only offers 2 languages Polish and English.

Changing the language can be done in the configuration file in the "language:" line.

 

In the config.yml file.
Please enter the TKOEN of your discord bot and paste in the ID of the channel you want the logs and reports on.

![Discord options](https://github.com/karpik122/ServerIntegrationwithDicord/blob/master/Discord.png)

Minecraft command
/report <player> <content> - report a player that someone has broken server rules.

 

Discord commands
/playtime <player_name> - sends a message to the discord chat about how much time a player has spent on the server (the "playtime" command can be edited to whatever name you want in the lang file in the line where "discord_playtime:" is).
/alltime - sends a list of all players and their time spent on the server. This command can only be used by a person who has admin rights on a discord server.
Discord commands
/playtime <player_name> - sends a message to the discord chat about how much time a player has spent on the server (the "playtime" command can be edited to whatever name you want in the lang file in the line where "discord_playtime:" is).
/alltime - sends a list of all players and their time spent on the server. This command can only be used by a person who has admin rights on a discord server.

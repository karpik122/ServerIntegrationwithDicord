# Server Integration with Dicord

Plugin Minecraft (Paper/Spigot 1.21), ktory laczy serwer z Discordem przez bota JDA.

## Co robi plugin

- wysyla zgloszenia graczy z komendy `/report` na wskazany kanal Discord,
- loguje komendy graczy na Discord (z wykluczeniem komend logowania/rejestracji),
- obsluguje komendy slash Discord do sprawdzania playtime i laczenia kont,
- wspiera integracje z Vault (`/money` na Discord po polaczeniu kont),
- ma tryb diagnostyczny i health-check admina.

![Discord options](https://github.com/karpik122/ServerIntegrationwithDicord/blob/master/Discord.png)

![Serwer Integration with Discord stats](https://bstats.org/signatures/bukkit/Serwer%20Integration%20with%20Discord.svg)

## Wymagania

- Java 21
- Paper/Spigot API 1.21.x
- token bota Discord
- opcjonalnie: Vault + plugin ekonomii (dla komendy `/money`)

## Szybki start

1. Zbuduj plugin:

```bash
./gradlew clean build
```

2. Skopiuj wygenerowany plik JAR do folderu `plugins` na serwerze.
3. Uruchom serwer raz, aby plugin utworzyl plik konfiguracyjny.
4. Ustaw wartosci w `plugins/ServerIntegrationwithDicord/config.yml`:

```yml
TOKEN: "twoj_token_bota"
report_channel: "id_kanalu_reportow"
id_log_channel: "id_kanalu_logow"
guildID: "id_serwera_discord"
language: "pl-PL" # albo en-US
debug: false
```

5. Zrestartuj serwer lub wykonaj reload komenda admina.

## Komendy Minecraft

- `/report <gracz> <powod>` - zgloszenie gracza na Discord.
- `/link` - generuje kod do polaczenia konta Minecraft z Discord.
- `/discordintegration reload` - przeladowanie pluginu i restart polaczenia bota.
- `/discordintegration past token <token>` - zapis tokenu do configu.
- `/discordintegration past log <channelId>` - zapis kanalu logow.
- `/discordintegration past report <channelId>` - zapis kanalu reportow.
- `/discordintegration health` - health-check runtime (JDA, timery, kanal logow, debug).

## Komendy Discord (slash)

- `/playtime <nick>` - pokazuje czas gry wskazanego gracza.
- `/link <kod>` - laczy konto Discord z kontem Minecraft.
- `/playtimetop` - top 5 graczy z najwiekszym playtime.
- `/money` - stan konta gracza (wymaga Vault i polaczonego konta).

Uwaga: nazwa komendy `/playtime` jest konfigurowalna przez plik jezykowy (`discord_playtime`).

## Health-check i debug

Nowa komenda admina:

```text
/discordintegration health
```

Pokazuje m.in.:

- status JDA,
- status timerow pluginu,
- czy ID kanalu logow jest ustawione i osiagalne,
- czy wlaczony jest tryb `debug`.

Aby uzyskac dodatkowe logi diagnostyczne w konsoli, ustaw w `config.yml`:

```yml
debug: true
```

## Jezyki

Aktualnie plugin wspiera:

- `pl-PL`
- `en-US`

Zmiane jezyka wykonasz przez pole `language` w `config.yml`.

## Kontakt

- Discord autora: `karpik122`
- Serwer Discord: [Server Discord](https://discord.gg/Rzq3fHXPAs)

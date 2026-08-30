# Server Integration with Dicord

Plugin dla Paper/Spigot 1.21.x, który łączy serwer Minecraft z Discordem przez bota JDA.

> Nazwa projektu i głównego folderu pluginu zachowuje oryginalną pisownię `Dicord`, aby aktualizacja nie utworzyła drugiego katalogu konfiguracyjnego.

## Funkcje

- `/report <gracz> <powód>` wysyła zgłoszenie na wybrany kanał Discord;
- logowanie komend graczy z bezpiecznym pomijaniem komend logowania, rejestracji i konfiguracji tokenu;
- slash-commandy Discord: `/playtime`, `/playtimetop`, `/link` i opcjonalne `/money`;
- jednorazowe kody łączenia kont z czasem ważności;
- trwałe powiązania Minecraft UUID ↔ Discord ID;
- niezależne naliczanie playtime — działa także wtedy, gdy bot Discord jest wyłączony;
- integracja z Vault i pluginem ekonomii;
- opcjonalne alerty o wskazanych słowach na czacie;
- opcjonalne przyciski ban/kick dla określonych ról Discord;
- przeładowanie połączenia bez restartu serwera i rozbudowany health-check.

## Wymagania

- Java 21;
- Paper lub Spigot 1.21.x;
- bot Discord i jego token;
- opcjonalnie Vault oraz plugin ekonomii obsługiwany przez Vault.

JAR nie zawiera JDA, dzięki czemu pozostaje bardzo mały. Przy pierwszym
uruchomieniu Paper/Spigot odczyta sekcję `libraries` z `plugin.yml` i pobierze
JDA 6.4.1 z Maven Central. Serwer musi wtedy mieć dostęp do internetu.

## Konfiguracja bota Discord

1. Utwórz aplikację i bota w Discord Developer Portal.
2. Zaproś bota na serwer z zakresami `bot` oraz `applications.commands`.
3. Nadaj mu na kanałach logów i zgłoszeń uprawnienia:
   - View Channel,
   - Send Messages,
   - Embed Links.
4. Skopiuj token bota oraz ID serwera i kanałów.

Plugin korzysta z domyślnych intentów JDA. Nie wymaga włączania `Message Content`, `Server Members` ani `Presence Intent`.

## Instalacja

1. Skopiuj JAR do katalogu `plugins` serwera.
2. Uruchom serwer raz, aby utworzyć pliki konfiguracyjne.
3. Zatrzymaj serwer i uzupełnij `plugins/ServerIntegrationwithDicord/config.yml`:

```yml
TOKEN: "token_bota"
report_channel: "123456789012345678"
id_log_channel: "123456789012345678"
guildID: "123456789012345678"
language: "pl-PL"
debug: false
```

4. Ponownie uruchom serwer.
5. Wykonaj `/discordintegration health` i sprawdź kanały Discord.

Nie publikuj `config.yml` zawierającego prawdziwy token. Jeżeli token trafił do repozytorium lub wiadomości publicznej, wygeneruj nowy w Discord Developer Portal.

## Komendy Minecraft

| Komenda | Uprawnienie | Działanie |
| --- | --- | --- |
| `/report <gracz> <powód>` | `serverintegrationwithdicord.report` | Wysyła zgłoszenie na Discord. |
| `/link` | `serverintegrationwithdicord.link` | Generuje jednorazowy kod połączenia kont. |
| `/discordintegration reload` | `serverintegrationwithdicord.admin` | Przeładowuje config, język, Vault i połączenie JDA. |
| `/discordintegration health` | `serverintegrationwithdicord.admin` | Pokazuje stan JDA, kanałów, zadań i Vault. |
| `/discordintegration set token <token>` | `serverintegrationwithdicord.admin` | Zapisuje token i restartuje integrację. |
| `/discordintegration set guild <guildId>` | `serverintegrationwithdicord.admin` | Ustawia serwer Discord. |
| `/discordintegration set log <channelId>` | `serverintegrationwithdicord.admin` | Ustawia kanał logów. |
| `/discordintegration set report <channelId>` | `serverintegrationwithdicord.admin` | Ustawia kanał zgłoszeń. |

Dla zgodności z poprzednią wersją słowo `past` nadal działa jako alias `set`.

Uprawnienie administratora domyślnie otrzymują tylko operatorzy. `/report` i `/link` są domyślnie dostępne dla graczy.

## Komendy Discord

| Komenda | Działanie |
| --- | --- |
| `/playtime <nick>` | Pokazuje czas gry wskazanego gracza. |
| `/playtimetop` | Pokazuje pięciu graczy z największym playtime. |
| `/link <kod>` | Łączy konto Discord z Minecraftem. |
| `/money` | Pokazuje saldo połączonego konta; rejestruje się tylko z działającym Vault. |

Slash-commandy są rejestrowane wyłącznie na serwerze wskazanym w `guildID`, dlatego po uruchomieniu pojawiają się bez oczekiwania na propagację komend globalnych.

## Ważniejsze opcje config.yml

```yml
link_code_ttl_minutes: 10
report_cooldown_seconds: 30

command_log:
  enabled: true
  excluded_commands:
    - login
    - register
    - discordintegration

discordadmininteraction: false
admindiscordid: []

flag: false
flagwords:
  - "przykładowe niedozwolone słowo"
```

Jeżeli włączysz `discordadmininteraction`, dodaj do `admindiscordid` ID ról, które mogą używać przycisków ban/kick. Pusta lista nie daje dostępu nikomu.

## Health-check i debug

```text
/discordintegration health
```

Health-check pokazuje:

- stan JDA;
- konfigurację i dostępność serwera Discord;
- dostępność kanałów logów i zgłoszeń;
- stan zadania aktualizacji statusu i licznika playtime;
- stan Vault;
- liczbę aktywnych kodów linkowania;
- tryb debug.

W razie problemów ustaw:

```yml
debug: true
```

Następnie wykonaj `/discordintegration reload` i sprawdź konsolę.

## Pliki danych

- `playtime.yml` — naliczony czas graczy w minutach;
- `link/players.yml` — trwałe powiązania kont;
- `lang/pl-PL.yml` i `lang/en-US.yml` — teksty pluginu.

## Kontakt

- Discord autora: `karpik122`
- [Serwer Discord](https://discord.gg/Rzq3fHXPAs)

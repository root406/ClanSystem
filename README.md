# ClanSystem

Ein leistungsstarkes Clan-Plugin für **SpaceportX**, entwickelt von **6tea9ine**.  
Verwalte Clans direkt im Spiel, nutze Admin-Tools zur Moderation und erweitere dein Server-Setup mit **PlaceholderAPI**-Support.

---

## 🔧 Features

- Eigene Clans erstellen und verwalten
- Clan-Einladungen und Clan-Chat
- Admin-Kommandos zum Verwalten, Löschen oder Sperren von Clans
- MySQL-Anbindung für persistente Speicherung
- Integration mit **PlaceholderAPI** zur Anzeige von Clan-Tags

---

## 📥 Installation

1. Lade die neueste Version des Plugins herunter.
2. Lege die `.jar`-Datei in den Ordner `/plugins/` deines Servers.
3. Starte den Server – es wird automatisch eine `config.yml` erstellt.
4. Öffne die `config.yml` und trage deine MySQL-Daten ein:

```yaml
MySQL:
  Host: '127.0.0.1'
  Port: 3306
  Username: 'root'
  Database: 'LobbySystem'
  Passowrd: ''
```

> ⚠️ Hinweis: In der Config steht `Passowrd` – das ist ein Tippfehler. Bitte ändere es in `Password`, wenn dein Plugin das korrekt ausliest.

5. Stelle sicher, dass **PlaceholderAPI** auf dem Server installiert ist, wenn du Platzhalter nutzen möchtest.

---

## 💬 Commands

### 👥 Spielerbefehle (`/clan`)

| Befehl                     | Beschreibung                          |
|----------------------------|---------------------------------------|
| `/clan accept`             | Akzeptiert eine Clan-Einladung         |
| `/clan confirm`            | Bestätigt eine Aktion (z. B. Clanlöschung) |
| `/clan create <Name>`      | Erstellt einen neuen Clan              |
| `/clan delete`             | Löscht den eigenen Clan                |
| `/clan deny`               | Lehnt eine Clan-Einladung ab           |
| `/clan help`               | Zeigt eine Hilfeseite mit Befehlen     |
| `/clan info`               | Zeigt Informationen über deinen Clan   |
| `/clan invite <Spieler>`   | Lädt einen Spieler in den Clan ein     |
| `/clan kick <Spieler>`     | Entfernt ein Clanmitglied              |
| `/clan leave`              | Verlässt deinen Clan                   |
| `/clan list`               | Zeigt alle Clans oder Clanmitglieder   |
| `/clan move <Spieler>`     | Überträgt den Clan an ein anderes Mitglied |
| `/clan promote <Spieler>`  | Befördert ein Clanmitglied             |

### 🛡️ Adminbefehle (`/clanadmin`)

| Befehl                        | Beschreibung                            |
|------------------------------|-----------------------------------------|
| `/clanadmin ban <Name>`      | Sperrt einen Clannamen                  |
| `/clanadmin delete <Clan>`   | Löscht einen bestehenden Clan           |
| `/clanadmin list`            | Zeigt alle existierenden Clans an       |
| `/clanadmin unban <Name>`    | Entsperrt einen Clannamen               |

---

## 🛡️ Permissions

| Permission                    | Beschreibung                         | Standard |
|------------------------------|--------------------------------------|----------|
| `clansystem.admin`           | Zugriff auf alle Admin-Kommandos     | OP       |
| `clansystem.admin.ban`       | Clannamen bannen                     | OP       |
| `clansystem.admin.unban`     | Clannamen entbannen                  | OP       |
| `clansystem.admin.delete`    | Clan löschen                         | OP       |
| `clansystem.admin.list`      | Alle Clans auflisten                 | OP       |

---

## 🧩 PlaceholderAPI Support

Das Plugin unterstützt [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) zur Integration von Clan-Tags in Scoreboards, Chat, Tablist uvm.

### Verfügbare Placeholder

| Placeholder      | Beschreibung                                                |
|------------------|-------------------------------------------------------------|
| `%clan_tag%`     | Gibt den Clan-Tag des Spielers zurück (z. B. `[ABC]`)       |

- Wird automatisch formatiert mit Farben: `§7[§eCLAN§7]`
- Aktualisiert sich automatisch bei Clanwechsel oder Löschung
- Kann in Tablists, Scoreboards, Nametags oder dem Chat verwendet werden

---

## 🧠 Technisches

- **Plugin-Name:** ClanSystem
- **Version:** 1.0-SNAPSHOT
- **API-Version:** 1.21
- **Autor:** 6tea9ine
- **Softdepend:** PlaceholderAPI
- **Main-Klasse:** `de.joel.clansystem.ClanSystem`

---

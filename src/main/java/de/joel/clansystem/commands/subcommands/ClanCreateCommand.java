package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.entity.Player;

public class ClanCreateCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cVerwendung: /clan create <Name> <Tag>");
            return;
        }

        String clanName = args[1];
        String clanTag = args[2];

        // Optionale Begrenzungen
        if (clanName.length() > 16) {
            sender.sendMessage("§cDer Clan-Name darf maximal 16 Zeichen lang sein!");
            return;
        }
        if (clanTag.length() > 6) {
            sender.sendMessage("§cDer Clan-Tag darf maximal 6 Zeichen lang sein!");
            return;
        }

        if (clanManager.isInClan(sender.getUniqueId())) {
            sender.sendMessage("§cDu bist bereits in einem Clan!");
            return;
        }

        if (clanManager.clanExists(clanName)) {
            sender.sendMessage("§cEin Clan mit diesem Namen existiert bereits!");
            return;
        }

        if (clanManager.tagExists(clanTag)) {
            sender.sendMessage("§cDieser Clan-Tag ist bereits vergeben!");
            return;
        }

        // Überprüfe, ob der Clan-Name oder Clan-Tag gesperrt ist
        if (clanManager.isClanBanned(clanName)) {
            sender.sendMessage("§cDieser Clan-Name ist gesperrt und kann nicht verwendet werden.");
            return;
        }

        if (clanManager.isClanBanned(clanTag)) { // Hier überprüfen wir auch den Tag
            sender.sendMessage("§cDieser Clan-Tag ist gesperrt und kann nicht verwendet werden.");
            return;
        }

        // Clan erstellen
        clanManager.createClan(clanName, clanTag, sender.getUniqueId());
        sender.sendMessage("§aDu hast erfolgreich den Clan §e" + clanName + " §aerstellt!");
    }
}
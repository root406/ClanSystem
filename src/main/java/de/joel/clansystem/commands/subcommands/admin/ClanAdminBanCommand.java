package de.joel.clansystem.commands.subcommands.admin;

import de.joel.clansystem.commands.AdminSubCommand;
import de.joel.clansystem.manager.AdminManager;
import org.bukkit.entity.Player;

public class ClanAdminBanCommand implements AdminSubCommand {

    @Override
    public void execute(Player player, AdminManager adminManager, String[] args) {
        if (!player.hasPermission("clansystem.admin.ban")) {
            player.sendMessage("§cDazu hast du keine Rechte!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§cVerwendung: /clanadmin ban <ClanName>");
            return;
        }

        String clanName = args[1];

        // Prüfen, ob der Clan erfolgreich gesperrt werden konnte
        if (adminManager.banClan(clanName)) {
            player.sendMessage("§aDer Clan §e" + clanName + " §awurde gesperrt und gelöscht!");
        } else {
            // Fehlerbehandlung: Geben wir mehr Informationen zurück
            player.sendMessage("§cDer Clan konnte nicht gesperrt werden. " +
                    "Möglicherweise existiert er nicht oder der Name ist bereits gesperrt.");
        }
    }
}

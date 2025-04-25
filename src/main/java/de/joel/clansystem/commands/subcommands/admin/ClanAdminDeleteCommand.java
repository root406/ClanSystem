package de.joel.clansystem.commands.subcommands.admin;

import de.joel.clansystem.commands.AdminSubCommand;
import de.joel.clansystem.manager.AdminManager;
import org.bukkit.entity.Player;

public class ClanAdminDeleteCommand implements AdminSubCommand {

    @Override
    public void execute(Player player, AdminManager adminManager, String[] args) {
        if (!player.hasPermission("clansystem.admin.delete")) {
            player.sendMessage("§cDazu hast du keine Rechte!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§cVerwendung: /clanadmin delete <ClanName>");
            return;
        }

        String clanName = args[1];

        if (adminManager.deleteClan(clanName)) {
            player.sendMessage("§aDer Clan §e" + clanName + " §awurde vollständig gelöscht.");
        } else {
            player.sendMessage("§cDer Clan konnte nicht gelöscht werden (vielleicht existiert er nicht).");
        }
    }
}

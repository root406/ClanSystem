package de.joel.clansystem.commands.subcommands.admin;

import de.joel.clansystem.commands.AdminSubCommand;
import de.joel.clansystem.manager.AdminManager;
import org.bukkit.entity.Player;

public class ClanAdminUnbanCommand implements AdminSubCommand {

    @Override
    public void execute(Player player, AdminManager adminManager, String[] args) {
        if (!player.hasPermission("clansystem.admin.unban")) {
            player.sendMessage("§cDazu hast du keine Rechte!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§cVerwendung: /clanadmin unban <ClanName>");
            return;
        }

        String clanName = args[1];

        if (adminManager.unbanClan(clanName)) {
            player.sendMessage("§aDer Clan §e" + clanName + " §awurde entsperrt.");
        } else {
            player.sendMessage("§cDieser Clan ist nicht gesperrt oder existiert nicht.");
        }
    }
}

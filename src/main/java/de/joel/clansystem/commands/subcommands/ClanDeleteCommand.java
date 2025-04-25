package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.entity.Player;

public class ClanDeleteCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("delete")) {
            if (!clanManager.isInClan(sender.getUniqueId())) {
                sender.sendMessage("§cDu bist in keinem Clan.");
                return;
            }

            String clanName = clanManager.getClanName(sender.getUniqueId());

            if (!clanManager.getRank(sender.getUniqueId()).equals("LEADER")) {
                sender.sendMessage("§cNur der Clan-Leader kann den Clan löschen.");
                return;
            }

            // Markiert den Clan als zur Löschung vorgemerkt
            clanManager.prepareToDeleteClan(sender.getUniqueId(), clanName);
            sender.sendMessage("§aDer Clan §e" + clanName + " §awurde zum Löschen vorgemerkt.");
            sender.sendMessage("§aGebe §e/clan confirm §aein, um den Clan endgültig zu löschen.");
        } else {
            sender.sendMessage("§cVerwendung: /clan delete");
        }
    }
}

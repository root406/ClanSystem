package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.entity.Player;

public class ClanConfirmCommand implements SubCommand {

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            if (!clanManager.hasClanToDelete(sender.getUniqueId())) {
                sender.sendMessage("§cDu hast keinen Clan zum Löschen vorgemerkt. Verwende zuerst /clan delete.");
                return;
            }

            String clanName = clanManager.getClanName(sender.getUniqueId());
            clanManager.deleteClan(sender.getUniqueId());

            sender.sendMessage("§aDein Clan §e" + clanName + " §awurde erfolgreich gelöscht.");
            clanManager.prepareToDeleteClan(sender.getUniqueId(), null); // Cache zurücksetzen
        } else {
            sender.sendMessage("§cVerwendung: /clan confirm");
        }
    }
}

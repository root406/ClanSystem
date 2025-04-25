package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ClanDenyCommand implements SubCommand {

    private final HashMap<UUID, String> pendingInvites;

    public ClanDenyCommand(HashMap<UUID, String> pendingInvites) {
        this.pendingInvites = pendingInvites;
    }

    @Override
    public void execute(Player sender, de.joel.clansystem.manager.ClanManager clanManager, String[] args) {
        UUID playerUUID = sender.getUniqueId();

        if (!pendingInvites.containsKey(playerUUID)) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Clan-Einladung!");
            return;
        }

        pendingInvites.remove(playerUUID);
        sender.sendMessage(ChatColor.RED + "Du hast die Clan-Einladung abgelehnt!");
    }
}

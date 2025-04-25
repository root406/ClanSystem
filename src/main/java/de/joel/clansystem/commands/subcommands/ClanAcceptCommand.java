package de.joel.clansystem.commands.subcommands;

import de.joel.clansystem.commands.SubCommand;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ClanAcceptCommand implements SubCommand {

    private final HashMap<UUID, String> pendingInvites;

    public ClanAcceptCommand(HashMap<UUID, String> pendingInvites) {
        this.pendingInvites = pendingInvites;
    }

    @Override
    public void execute(Player sender, ClanManager clanManager, String[] args) {
        UUID playerUUID = sender.getUniqueId();

        if (!pendingInvites.containsKey(playerUUID)) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Clan-Einladung!");
            return;
        }

        String clanName = pendingInvites.get(playerUUID);
        clanManager.addPlayerToClan(playerUUID, clanName, "MEMBER");
        pendingInvites.remove(playerUUID);
    }
}

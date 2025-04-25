package de.joel.clansystem.commands;

import de.joel.clansystem.manager.ClanManager;
import org.bukkit.entity.Player;

public interface SubCommand {
    void execute(Player player, ClanManager clanManager, String[] args);
}

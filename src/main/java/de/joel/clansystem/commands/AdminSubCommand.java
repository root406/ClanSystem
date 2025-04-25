package de.joel.clansystem.commands;

import de.joel.clansystem.manager.AdminManager;
import org.bukkit.entity.Player;

public interface AdminSubCommand {
    void execute(Player player, AdminManager adminManager, String[] args);
}

package de.joel.clansystem.listener;

import de.joel.clansystem.events.ClanTagUpdateEvent;
import de.joel.clansystem.manager.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private final ClanManager clanManager;

    // Konstruktor zur Injektion des ClanManagers
    public JoinListener(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Erst Clan-Tag laden oder entfernen
        if (clanManager.isInClan(playerUUID)) {
            clanManager.loadClanTag(playerUUID); // Clan-Tag nachladen
        } else {
            clanManager.clanTagCache.remove(playerUUID); // Tag aus Cache entfernen
        }

        // 🔥 ClanTagUpdateEvent auslösen, damit es sofort aktualisiert wird
        Bukkit.getPluginManager().callEvent(new ClanTagUpdateEvent(playerUUID));
    }

}

package de.joel.clansystem.placeholders;

import de.joel.clansystem.ClanSystem;
import de.joel.clansystem.manager.ClanManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {

    private final ClanManager clanManager;

    public ClanPlaceholderExpansion(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clan";
    }

    @Override
    public @NotNull String getAuthor() {
        return "joel";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("tag")) {
            UUID uuid = player.getUniqueId();
            String clanTag = clanManager.getClanTag(uuid);  // Clan-Tag aus dem Cache holen
            return (clanTag != null && !clanTag.isEmpty()) ? "§7[§e" + clanTag + "§7] " : "";
        }
        return null;
    }

    public void updateClanTag(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            String clanTag = clanManager.getClanTag(playerUUID);
            String displayName = player.getName();

            if (clanTag != null && !clanTag.isEmpty()) {
                displayName = "§7[§e" + clanTag + "§7] " + player.getName();
            }

            // 🛠️ Setze den neuen Namen für die Tablist
            player.setPlayerListName(displayName);
            player.setDisplayName(displayName);

            // 🛠️ PlaceholderAPI sofort updaten
            Bukkit.getScheduler().runTaskLater(ClanSystem.getInstance(), () -> {
                PlaceholderAPI.setPlaceholders(player, "%clan_tag%");
            }, 1L);
        }
    }
}
package de.joel.clansystem.listener;

import de.joel.clansystem.events.ClanTagUpdateEvent;
import de.joel.clansystem.placeholders.ClanPlaceholderExpansion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ClanTagUpdateListener implements Listener {

    private final ClanPlaceholderExpansion placeholderExpansion;

    public ClanTagUpdateListener(ClanPlaceholderExpansion placeholderExpansion) {
        this.placeholderExpansion = placeholderExpansion;
    }

    @EventHandler
    public void onClanTagUpdate(ClanTagUpdateEvent event) {
        UUID playerUUID = event.getPlayerUUID();
        placeholderExpansion.updateClanTag(playerUUID);
    }
}
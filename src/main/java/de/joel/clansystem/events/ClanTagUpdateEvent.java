package de.joel.clansystem.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ClanTagUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID playerUUID;

    public ClanTagUpdateEvent(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
package de.joel.clansystem.manager;

import de.joel.clansystem.api.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AdminManager {

    private final MySQL database;
    private final ClanManager clanManager;

    public AdminManager(MySQL database, ClanManager clanManager) {
        this.database = database;
        this.clanManager = clanManager;
    }

    /**
     * 🛑 Clan sperren (Clan wird gelöscht und Name wird blockiert)
     */
    public boolean banClan(String clanName) {
        try (Connection conn = database.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.out.println("❌ MySQL-Verbindung ist geschlossen oder nicht verfügbar!");
                return false;
            }

            System.out.println("🔍 Prüfe, ob der Clan bereits gesperrt ist: " + clanName);
            if (isWordBanned(clanName)) {
                System.out.println("❌ Der Clan-Name '" + clanName + "' ist bereits gesperrt.");
                return false;
            }

            System.out.println("✅ Clan wird in 'banned_clans' eingetragen...");
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO banned_clans (word) VALUES (?)")) {
                stmt.setString(1, clanName);
                stmt.executeUpdate();
            }
            System.out.println("✅ Clan-Name erfolgreich gesperrt: " + clanName);

            System.out.println("🔍 Prüfe, ob der Clan existiert: " + clanName);
            try (PreparedStatement checkClanStmt = conn.prepareStatement("SELECT id FROM clans WHERE name = ? OR tag = ?")) {
                checkClanStmt.setString(1, clanName);
                checkClanStmt.setString(2, clanName);
                ResultSet rs = checkClanStmt.executeQuery();

                if (rs.next()) {
                    int clanId = rs.getInt("id");
                    System.out.println("✅ Clan existiert mit ID: " + clanId);

                    System.out.println("🗑️ Lösche Clan-Mitglieder...");
                    try (PreparedStatement deleteMembersStmt = conn.prepareStatement("DELETE FROM clan_members WHERE clan_id = ?")) {
                        deleteMembersStmt.setInt(1, clanId);
                        deleteMembersStmt.executeUpdate();
                    }

                    System.out.println("🗑️ Lösche Clan...");
                    try (PreparedStatement deleteClanStmt = conn.prepareStatement("DELETE FROM clans WHERE id = ?")) {
                        deleteClanStmt.setInt(1, clanId);
                        deleteClanStmt.executeUpdate();
                    }

                    System.out.println("✅ Clan " + clanName + " erfolgreich gelöscht!");
                } else {
                    System.out.println("⚠️ Clan '" + clanName + "' existiert nicht in der Datenbank.");
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Fehler beim Bannen des Clans: " + clanName);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ✅ Clan entsperren (damit der Name wieder genutzt werden kann)
     */
    public boolean unbanClan(String clanName) {
        // Überprüfen, ob der Clan-Name oder das Tag ein gesperrtes Wort enthält
        if (containsBannedWord(clanName)) {
            return false;  // Der Clan kann nicht entsperrt werden, wenn der Name oder Tag ein gesperrtes Wort enthält
        }

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM banned_clans WHERE word = ?")) {
            stmt.setString(1, clanName);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ❌ Prüft, ob ein Clan gesperrt ist.
     */
    public boolean isClanBanned(String clanName) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT word FROM banned_clans WHERE word = ?")) {
            stmt.setString(1, clanName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ❌ Clan vollständig löschen (ohne Sperre)
     */
    public boolean deleteClan(String clanName) {
        // Entferne Farbcodes aus dem Clanname
        String cleanClanName = ChatColor.stripColor(clanName);

        try (Connection conn = database.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.out.println("❌ MySQL-Verbindung ist geschlossen oder nicht verfügbar!");
                return false;
            }

            // Clan-ID abrufen
            int clanId = -1;
            try (PreparedStatement getClanIdStmt = conn.prepareStatement(
                    "SELECT id FROM clans WHERE name = ? OR tag = ?")) {
                getClanIdStmt.setString(1, cleanClanName);
                getClanIdStmt.setString(2, cleanClanName);
                ResultSet rs = getClanIdStmt.executeQuery();
                if (rs.next()) {
                    clanId = rs.getInt("id");
                    System.out.println("✅ Clan-ID für '" + cleanClanName + "': " + clanId);
                } else {
                    System.out.println("⚠️ Clan '" + cleanClanName + "' existiert nicht in der Datenbank.");
                    return false;
                }
            }

            // Mitglieder entfernen
            try (PreparedStatement deleteMembersStmt = conn.prepareStatement(
                    "DELETE FROM clan_members WHERE clan_id = ?")) {
                deleteMembersStmt.setInt(1, clanId);
                deleteMembersStmt.executeUpdate();
                System.out.println("✅ Mitglieder des Clans '" + cleanClanName + "' erfolgreich entfernt.");
            }

            // Clan löschen
            try (PreparedStatement deleteClanStmt = conn.prepareStatement(
                    "DELETE FROM clans WHERE id = ?")) {
                deleteClanStmt.setInt(1, clanId);
                deleteClanStmt.executeUpdate();
                System.out.println("✅ Clan '" + cleanClanName + "' erfolgreich gelöscht.");
            }

            // Caches aktualisieren
            clanManager.updateCachesAfterClanDeletion(cleanClanName);

            return true;
        } catch (SQLException e) {
            System.out.println("❌ Fehler beim Löschen des Clans: " + cleanClanName);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 📜 Holt alle Clans mit ihren Leadern.
     */
    public Map<String, String> getAllClansWithLeaders() {
        Map<String, String> clans = new LinkedHashMap<>();

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.name, c.tag, m.uuid FROM clans c " +
                             "JOIN clan_members m ON c.id = m.clan_id " +
                             "WHERE m.rank = 'LEADER'")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String clanName = rs.getString("name");
                String clanTag = rs.getString("tag");
                String leaderUUID = rs.getString("uuid");
                OfflinePlayer leader = Bukkit.getOfflinePlayer(UUID.fromString(leaderUUID));
                // Kombiniere Clanname und Tag für die Anzeige
                clans.put(clanName + " [" + clanTag + "]", leader.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clans;
    }

    /**
     * 👥 Holt alle Mitglieder eines Clans.
     */
    public List<String> getClanMembers(String clanName) {
        List<String> members = new ArrayList<>();

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT m.uuid FROM clan_members m " +
                             "JOIN clans c ON c.id = m.clan_id WHERE c.name = ?")) {
            stmt.setString(1, clanName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String memberUUID = rs.getString("uuid");
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(memberUUID));
                members.add(player.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    /**
     * 🚫 Prüft, ob der Clan-Name oder Tag ein gesperrtes Wort enthält.
     */
    private boolean containsBannedWord(String input) {
        try (Connection conn = database.getConnection()) {
            // Überprüfen, ob ein gesperrtes Wort im Clan-Name oder Tag enthalten ist
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM banned_clans WHERE ? LIKE CONCAT('%', word, '%')")) {
                stmt.setString(1, input);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getInt(1) > 0;  // Wenn ein gesperrtes Wort im Input enthalten ist
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ✅ Sperre ein Wort (auch wenn noch kein Clan existiert)
     */
    public boolean banWord(String word) {
        try (Connection conn = database.getConnection()) {
            // Überprüfen, ob das Wort bereits gesperrt ist
            if (isWordBanned(word)) {
                return false;  // Wenn das Wort schon gesperrt ist, tue nichts
            }

            // Sperre das Wort, indem es in die Tabelle eingefügt wird
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO banned_clans (word) VALUES (?)")) {
                stmt.setString(1, word);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ❌ Überprüft, ob ein Wort in der gesperrten Liste enthalten ist
     */
    private boolean isWordBanned(String word) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT word FROM banned_clans WHERE word = ?")) {
            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // Wenn das Wort gefunden wird, return true
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
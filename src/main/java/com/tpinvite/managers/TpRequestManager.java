package com.tpinvite.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpRequestManager {
    
    private final Plugin plugin;
    private final Map<UUID, UUID> pendingRequests; // Destinataire -> Demandeur
    private final Map<UUID, BukkitTask> teleportTasks; // Joueur en téléportation -> Tâche
    private final Map<UUID, Location> teleportLocations; // Joueur en téléportation -> Position de départ
    
    public TpRequestManager(Plugin plugin) {
        this.plugin = plugin;
        this.pendingRequests = new HashMap<>();
        this.teleportTasks = new HashMap<>();
        this.teleportLocations = new HashMap<>();
    }
    
    public boolean hasRequest(UUID target, UUID requester) {
        return pendingRequests.get(target) != null && pendingRequests.get(target).equals(requester);
    }
    
    public void addRequest(UUID target, UUID requester) {
        pendingRequests.put(target, requester);
        
        // Expirer la demande après 60 secondes
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pendingRequests.get(target) != null && pendingRequests.get(target).equals(requester)) {
                    pendingRequests.remove(target);
                    Player targetPlayer = Bukkit.getPlayer(target);
                    Player requesterPlayer = Bukkit.getPlayer(requester);
                    
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage("§7La demande de téléportation a expiré.");
                    }
                    if (requesterPlayer != null) {
                        requesterPlayer.sendMessage("§7Votre demande de téléportation a expiré.");
                    }
                }
            }
        }.runTaskLater(plugin, 1200L); // 60 secondes = 1200 ticks
    }
    
    public UUID getRequester(UUID target) {
        return pendingRequests.get(target);
    }
    
    public void removeRequest(UUID target) {
        pendingRequests.remove(target);
    }
    
    public void startTeleport(Player player, Player target) {
        UUID playerId = player.getUniqueId();
        
        // Annuler toute téléportation en cours
        cancelTeleport(playerId);
        
        // Sauvegarder la position de départ
        teleportLocations.put(playerId, player.getLocation().clone());
        
        player.sendMessage("§aTéléportation dans 5 secondes... Ne bougez pas!");
        target.sendMessage("§a" + player.getName() + " va être téléporté vers vous dans 5 secondes.");
        
        BukkitTask task = new BukkitRunnable() {
            int countdown = 5;
            
            @Override
            public void run() {
                if (!player.isOnline() || !target.isOnline()) {
                    cancel();
                    teleportTasks.remove(playerId);
                    teleportLocations.remove(playerId);
                    return;
                }
                
                // Vérifier si le joueur a bougé
                Location startLocation = teleportLocations.get(playerId);
                if (startLocation != null && startLocation.distance(player.getLocation()) > 0.5) {
                    player.sendMessage("§cTéléportation annulée - vous avez bougé!");
                    target.sendMessage("§cTéléportation de " + player.getName() + " annulée - il a bougé!");
                    cancel();
                    teleportTasks.remove(playerId);
                    teleportLocations.remove(playerId);
                    return;
                }
                
                if (countdown > 0) {
                    player.sendMessage("§e" + countdown + "...");
                    countdown--;
                } else {
                    // Téléporter le joueur
                    player.teleport(target.getLocation());
                    player.sendMessage("§aTéléporté vers " + target.getName() + "!");
                    target.sendMessage("§a" + player.getName() + " a été téléporté vers vous!");
                    
                    cancel();
                    teleportTasks.remove(playerId);
                    teleportLocations.remove(playerId);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Chaque seconde
        
        teleportTasks.put(playerId, task);
    }
    
    public void cancelTeleport(UUID playerId) {
        BukkitTask task = teleportTasks.get(playerId);
        if (task != null) {
            task.cancel();
            teleportTasks.remove(playerId);
            teleportLocations.remove(playerId);
        }
    }
    
    public void clearAllRequests() {
        pendingRequests.clear();
        for (BukkitTask task : teleportTasks.values()) {
            task.cancel();
        }
        teleportTasks.clear();
        teleportLocations.clear();
    }
}
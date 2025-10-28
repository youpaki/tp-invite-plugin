package com.tpinvite.listeners;

import com.tpinvite.managers.TpRequestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

public class ChatClickListener implements Listener {
    
    private final TpRequestManager tpRequestManager;
    
    public ChatClickListener(TpRequestManager tpRequestManager) {
        this.tpRequestManager = tpRequestManager;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Ce listener gère déjà l'annulation de téléportation si le joueur bouge
        // La logique est déjà dans TpRequestManager.startTeleport()
        // Pas besoin d'ajouter de logique supplémentaire ici
    }
}
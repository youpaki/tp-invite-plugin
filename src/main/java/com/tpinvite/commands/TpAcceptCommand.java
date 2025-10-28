package com.tpinvite.commands;

import com.tpinvite.managers.TpRequestManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpAcceptCommand implements CommandExecutor {
    
    private final TpRequestManager tpRequestManager;
    
    public TpAcceptCommand(TpRequestManager tpRequestManager) {
        this.tpRequestManager = tpRequestManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("tpinvite.accept")) {
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }
        
        UUID playerId = player.getUniqueId();
        UUID requesterId = tpRequestManager.getRequester(playerId);
        
        if (args.length == 1) {
            // Commande avec nom de joueur spécifique
            Player requester = Bukkit.getPlayer(args[0]);
            if (requester == null) {
                player.sendMessage("§cJoueur introuvable: " + args[0]);
                return true;
            }
            
            if (!tpRequestManager.hasRequest(playerId, requester.getUniqueId())) {
                player.sendMessage("§cAucune demande de téléportation de " + requester.getName() + ".");
                return true;
            }
            
            requesterId = requester.getUniqueId();
        } else if (args.length == 0) {
            // Commande sans argument - accepter la dernière demande
            if (requesterId == null) {
                player.sendMessage("§cAucune demande de téléportation en attente.");
                return true;
            }
        } else {
            player.sendMessage("§cUsage: /" + label + " [joueur]");
            return true;
        }
        
        Player requester = Bukkit.getPlayer(requesterId);
        if (requester == null) {
            player.sendMessage("§cLe joueur qui a fait la demande n'est plus en ligne.");
            tpRequestManager.removeRequest(playerId);
            return true;
        }
        
        // Supprimer la demande
        tpRequestManager.removeRequest(playerId);
        
        // Messages de confirmation
        player.sendMessage("§aVous avez accepté la demande de téléportation de " + requester.getName() + ".");
        requester.sendMessage("§a" + player.getName() + " a accepté votre demande de téléportation!");
        
        // Démarrer la téléportation avec délai
        tpRequestManager.startTeleport(requester, player);
        
        return true;
    }
}
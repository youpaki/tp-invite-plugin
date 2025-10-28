package com.tpinvite.commands;

import com.tpinvite.managers.TpRequestManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpDenyCommand implements CommandExecutor {
    
    private final TpRequestManager tpRequestManager;
    
    public TpDenyCommand(TpRequestManager tpRequestManager) {
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
            // Commande sans argument - refuser la dernière demande
            if (requesterId == null) {
                player.sendMessage("§cAucune demande de téléportation en attente.");
                return true;
            }
        } else {
            player.sendMessage("§cUsage: /" + label + " [joueur]");
            return true;
        }
        
        Player requester = Bukkit.getPlayer(requesterId);
        
        // Supprimer la demande
        tpRequestManager.removeRequest(playerId);
        
        // Messages de confirmation
        player.sendMessage("§cVous avez refusé la demande de téléportation.");
        
        if (requester != null) {
            requester.sendMessage("§c" + player.getName() + " a refusé votre demande de téléportation.");
        }
        
        return true;
    }
}
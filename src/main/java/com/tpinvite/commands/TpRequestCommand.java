package com.tpinvite.commands;

import com.tpinvite.managers.TpRequestManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpRequestCommand implements CommandExecutor {
    
    private final TpRequestManager tpRequestManager;
    
    public TpRequestCommand(TpRequestManager tpRequestManager) {
        this.tpRequestManager = tpRequestManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("tpinvite.request")) {
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }
        
        if (args.length != 1) {
            player.sendMessage("§cUsage: /" + label + " <joueur>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cJoueur introuvable: " + args[0]);
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cVous ne pouvez pas vous téléporter vers vous-même!");
            return true;
        }
        
        if (tpRequestManager.hasRequest(target.getUniqueId(), player.getUniqueId())) {
            player.sendMessage("§cVous avez déjà une demande en attente pour ce joueur.");
            return true;
        }
        
        // Ajouter la demande
        tpRequestManager.addRequest(target.getUniqueId(), player.getUniqueId());
        
        // Message au demandeur
        player.sendMessage("§aDemande de téléportation envoyée à " + target.getName() + ".");
        
        // Message cliquable au destinataire
        Component acceptButton = Component.text("[ACCEPTER]")
                .color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour accepter")))
                .clickEvent(ClickEvent.runCommand("/tpaccept " + player.getName()));
        
        Component denyButton = Component.text("[REFUSER]")
                .color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour refuser")))
                .clickEvent(ClickEvent.runCommand("/tpdeny " + player.getName()));
        
        Component message = Component.text(player.getName() + " demande à se téléporter vers vous. ")
                .color(NamedTextColor.YELLOW)
                .append(acceptButton)
                .append(Component.text(" "))
                .append(denyButton);
        
        target.sendMessage(message);
        
        return true;
    }
}
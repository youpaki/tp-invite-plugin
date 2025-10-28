package com.tpinvite;

import com.tpinvite.commands.TpRequestCommand;
import com.tpinvite.commands.TpAcceptCommand;
import com.tpinvite.commands.TpDenyCommand;
import com.tpinvite.managers.TpRequestManager;
import com.tpinvite.listeners.ChatClickListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TpInvitePlugin extends JavaPlugin {

    private TpRequestManager tpRequestManager;

    @Override
    public void onEnable() {
        // Initialisation du gestionnaire de demandes
        tpRequestManager = new TpRequestManager(this);
        
        // Enregistrement des commandes
        getCommand("tpr").setExecutor(new TpRequestCommand(tpRequestManager));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(tpRequestManager));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(tpRequestManager));
        
        // Enregistrement des listeners
        getServer().getPluginManager().registerEvents(new ChatClickListener(tpRequestManager), this);
        
        getLogger().info("TpInvitePlugin activé avec succès!");
    }

    @Override
    public void onDisable() {
        // Nettoyage des demandes en attente
        if (tpRequestManager != null) {
            tpRequestManager.clearAllRequests();
        }
        
        getLogger().info("TpInvitePlugin désactivé!");
    }

    public TpRequestManager getTpRequestManager() {
        return tpRequestManager;
    }
}
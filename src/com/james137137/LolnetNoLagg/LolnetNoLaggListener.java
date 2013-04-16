/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.james137137.LolnetNoLagg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author James
 */
public class LolnetNoLaggListener implements Listener{

    private LolnetNoLagg LolnetNoLagg;
    
     public LolnetNoLaggListener(LolnetNoLagg LolnetNoLagg)
    {
        this.LolnetNoLagg = LolnetNoLagg;
    }
    
     
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
         Player player = event.getPlayer();
        
    }
     
     
     @EventHandler (priority = EventPriority.LOW)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
         
         if (event.isCancelled())
         {
             return;
         }
         if (event.getEntityType().isAlive())
         {
             //event.getEntity().remove();
         }
         
        
    }
     
     
}

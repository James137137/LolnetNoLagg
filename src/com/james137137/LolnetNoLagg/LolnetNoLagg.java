/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.james137137.LolnetNoLagg;

import com.avaje.ebeaninternal.server.el.ElSetValue;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import com.james137137.mcstats.Metrics;
import java.sql.Time;
import java.util.Calendar;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author James
 */
public class LolnetNoLagg extends JavaPlugin {

    public static BukkitTask task;
    Calendar mytime = Calendar.getInstance();
    public static int spawnLimit = 50;
    public static int autoRunTimeMinutes = 1;
    public double lastRun = (double) mytime.get(Calendar.HOUR_OF_DAY) + (double) mytime.get(Calendar.MINUTE) / 60.0;
    static final Logger log = Logger.getLogger("Minecraft");

    /**
     * @param args the command line arguments
     */
    @Override
    public void onEnable() {


        FileConfiguration config = getConfig();
        config.addDefault("TotalSpawnLimit", 600);
        config.addDefault("AutoRun", true);
        config.addDefault("autoRunTimeMinutes", 5);

        config.options().copyDefaults(true);
        saveConfig();

        spawnLimit = config.getInt("TotalSpawnLimit");
        autoRunTimeMinutes = config.getInt("autoRunTimeMinutes");
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        //getServer().getPluginManager().registerEvents(new LolnetNoLaggListener(this), this);
        String PluginVersion = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        log.log(Level.INFO, "{0}:Version {1} enabled", new Object[]{this.getName(), PluginVersion});

        if (config.getBoolean("AutoRun")) {
            autoRun();
        }

    }

    @Override
    public void onDisable() {
        String PluginVersion = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        log.log(Level.INFO, "{0}:Version {1} disabled", new Object[]{this.getName(), PluginVersion});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName().toLowerCase();
        String[] trimmedArgs = args;


        if (commandName.equalsIgnoreCase("LolnetNoLagg") && sender.hasPermission("LolnetNoLagg.admin")) {
            LolnetNoLaggCommand(sender, args);
        }



        return false;
    }

    private int getMobCount() {

        List<World> worlds = getServer().getWorlds();
        int count = 0;
        int newcount = 0;
        int entitysID;
        for (int j = 0; j < worlds.size(); j++) {
            List<Entity> entitys = worlds.get(j).getEntities();
            for (int i = 0; i < entitys.size(); i++) {
                if (!entitys.get(i).isDead()) {
                    entitysID = entitys.get(i).getType().getTypeId();
                    if (entitysID >= 50 && entitysID <= 65 || entitysID == 94) {
                        count++;
                    }

                }

            }
        }

        if (count >= spawnLimit) {
            Random myRandom = new Random();
            newcount = 0;
            if (count == 0) {
                return count;
            }
            double percentRemove = 1 - ((double) spawnLimit / (double) count);
            //System.out.println("target: " + percentRemove);
            for (int j = 0; j < worlds.size(); j++) {
                List<Entity> entitys = worlds.get(j).getEntities();
                for (int i = 0; i < entitys.size(); i++) {
                    if (!entitys.get(i).isDead()) {
                        newcount++;
                        entitysID = entitys.get(i).getType().getTypeId();
                        if (entitysID >= 50 && entitysID <= 65 || entitysID == 94) {
                            double random = myRandom.nextDouble();
                            if (random <= percentRemove) {
                                entitys.get(i).remove();

                            }

                        }
                    }

                }
            }



        }

        return count;
    }

    private void LolnetNoLaggCommand(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            FileConfiguration config = getConfig();
            if (args[0].equalsIgnoreCase("SpawnLimit")) {

                if (args.length >= 2) {
                    int limit = Integer.parseInt(args[1]);
                    config.set("TotalSpawnLimit", limit);
                    spawnLimit = limit;
                    sender.sendMessage("Spawnlimit is now: " + spawnLimit);
                } else {
                    sender.sendMessage("Spawnlimit is: " + spawnLimit);
                }
                saveConfig();
            } else if (args[0].equalsIgnoreCase("run")) {
                int runMax = 1;
                if (args.length >= 2) {
                    runMax = Integer.parseInt(args[1]);
                }
                int Runcount = 0;
                while (getMobCount() >= spawnLimit * 1.1 && Runcount <= runMax) {
                    Runcount++;
                }
                sender.sendMessage("[lolnetNoLagg] complete: mobcount = " + getMobCount());
                mytime = Calendar.getInstance();
                lastRun = (double) mytime.get(Calendar.HOUR_OF_DAY) + (double) mytime.get(Calendar.MINUTE) / 60.0;
            } else if (args[0].equalsIgnoreCase("autoRun")) {


                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("on")) {
                        if (!config.getBoolean("AutoRun")) {
                            autoRun();
                        }
                        config.set("AutoRun", true);
                        saveConfig();
                        sender.sendMessage("AutoRun is on");

                    } else if (args[1].equalsIgnoreCase("off")) {
                        config.set("AutoRun", false);
                        saveConfig();
                        sender.sendMessage("AutoRun is off");
                    }
                } else {
                    if (config.getBoolean("AutoRun")) {
                        sender.sendMessage("AutoRun is on");
                        sender.sendMessage("to turn on please type /lolnetnolagg autoRun on");
                    } else {
                        sender.sendMessage("AutoRun is off");
                        sender.sendMessage("to turn off please type /lolnetnolagg autoRun off");
                    }
                    
                }
            } else if (args[0].equalsIgnoreCase("autoRunTime")) {
                
                if (args.length >= 2) {
                    try {
                        int newtime = Integer.parseInt(args[1]);
                        config.set("autoRunTimeMinutes", newtime);
                        saveConfig();
                        sender.sendMessage("LolnetNoLagg is set to run every " + config.getInt("autoRunTimeMinutes") + " minutes");
                    } catch (Exception e) {
                        sender.sendMessage("Error: Please make sure you input a number and not " + args[1]);
                    }


                } else {
                    sender.sendMessage("LolnetNoLagg is set to run every " + config.getInt("autoRunTimeMinutes") + " minutes");
                    sender.sendMessage("to change please type /lolnetnolagg autoRunTime #");
                }
            } else
            {
                sender.sendMessage("error please use /lolnetNolagg [spawnlimit,run,]");
            }

        } else {
            sender.sendMessage("error please use /lolnetNolagg [spawnlimit,run,AutoRun,autoRunTime]");
        }
    }

    public void autoRun() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            double now;

            @Override
            public void run() {
                FileConfiguration config = getConfig();
                if (!config.getBoolean("AutoRun")) {
                    Bukkit.getScheduler().cancelTasks(Bukkit.getServer().getPluginManager().getPlugin("LolnetNoLagg"));
                }
                mytime = Calendar.getInstance();
                now = (double) mytime.get(Calendar.HOUR_OF_DAY) + (double) mytime.get(Calendar.MINUTE) / 60.0;
                if (Math.abs(now - lastRun + 0.001) >= (double) autoRunTimeMinutes / 60) {


                    log.log(Level.INFO, "[lolnetNoLagg] complete: mobcount = {0}", getMobCount());
                    lastRun = (double) mytime.get(Calendar.HOUR_OF_DAY) + (double) mytime.get(Calendar.MINUTE) / 60.0;
                }

            }
        }, 20L, 20 * 30);

    }
}

package me.captain.warp;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Class that handles all warp related functions
 *
 * @author andrewbulkeley
 */
public class WarpHandler {

    /**
     * Stores main class for local use
     */
    public CaptainWarp plugin;

    /**
     * Stores warps for local use
     */
    public ArrayList<Warp> warps;

    /**
     * Creates new WarpHandler
     *
     * @param instance
     */
    public WarpHandler(CaptainWarp instance) {
        plugin = instance;
        warps = new ArrayList();
    }

    /**
     * Add a warp to the warp list
     *
     * @param warp
     */
    public void addWarp(Warp warp) {
        this.warps.add(warp);
    }

    /**
     * Remove a warp from the warp list
     *
     * @param warp
     */
    public void removeWarp(Warp warp) {
        this.warps.remove(warp);
    }

    /**
     * Update a warp - Adds the warp if it does not exist, updates if it does
     *
     * @param warp
     */
    public void updateWarp(Warp warp) {
        Warp warpFromList = getWarp(warp.getName());
        if (warpFromList == null) {
            addWarp(warp);
        } else {
            removeWarp(warpFromList);
            addWarp(warp);
        }
    }
    
    /**
     * Update a warp's permissions
     * 
     * @param original
     * @param newWarp
     */
    public void updateWarpPermissions(Warp original, Warp newWarp) {
        String oldPerms = "captainwarp.warps." + original.getNode();
        String newPerms = "captainwarp.warps." + newWarp.getNode();
    }

    /**
     * Send the warp list to a player, show only warps they are allowed to use
     *
     * @param player
     */
    public void sendList(Player player) {
        ArrayList<String> wList = new ArrayList();
        if (warps.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "There are no warps");
            return;
        }
        for (Warp w : warps) {
            if (!w.getName().contains("@")) {
                if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.warps." + w.getNode())) {
                    wList.add(ChatColor.GREEN + w.getName() + ChatColor.GRAY);
                } else {
                    wList.add(ChatColor.RED + w.getName() + ChatColor.GRAY);
                }
            } else if((player.hasPermission("captainwarp.*") || w.getOwner().equals(player.getName())) && !w.getName().contains("@home")) {
                wList.add(ChatColor.GRAY + w.getName());
            }
        }
        player.sendMessage(ChatColor.GRAY + "Warps: " + wList.toString());
    }
    
    /**
     * Send the homes list to a player, show only warps they are allowed to use
     *
     * @param player
     */
    public void sendHomes(Player player) {
        ArrayList<String> wList = new ArrayList();
        if (warps.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "There are no warps");
            return;
        }
        if(!player.hasPermission("captainwarp.home.list")) {
            player.sendMessage(ChatColor.GRAY + "You don't have permission to view the home list");
            return;
        }
        for (Warp w : warps) {
            if (w.getName().startsWith("@home")) {
                if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.home.@" + w.getOwner()) || player.getName().equals(w.getOwner())) {
                    wList.add(ChatColor.GREEN + w.getName() + ChatColor.GRAY);
                } else {
                    wList.add(ChatColor.RED + w.getName() + ChatColor.GRAY);
                }
            }
        }
        player.sendMessage(ChatColor.GRAY + "Homes: " + wList.toString());
    }
    

    /**
     * Send the warp list to the console, shows all warps (hidden and homes)
     *
     * @param sender
     */
    public void sendList(CommandSender sender) {
        ArrayList<String> wList = new ArrayList();
        ArrayList<String> hList = new ArrayList();
        if (warps.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "There are no warps");
            return;
        }
        for (Warp w : warps) {
            if (w.getName().contains("@home-")) {
                hList.add(ChatColor.GREEN + w.getName() + ChatColor.GRAY);
            } else if (w.getName().contains("@")) {
                wList.add(ChatColor.RED + w.getName() + ChatColor.GRAY);
            } else {
                wList.add(ChatColor.GREEN + w.getName() + ChatColor.GRAY);
            }
        }
        sender.sendMessage(ChatColor.GRAY + "Warps: " + wList.toString());
        sender.sendMessage(ChatColor.GRAY + "Homes: " + hList.toString());
    }

    /**
     * Return warp with the specified name (if it exists)
     *
     * @param name
     * @return
     */
    public Warp getWarp(String name) {
        for (Warp w : warps) {
            if (w.getName().equals(name)) {
                return w;
            }
        }
        return null;
    }

    /**
     * Load warps from file
     */
    public void loadWarps() {
        try {
            File f = new File(plugin.getDataFolder(), "warps.yml");
            YamlConfiguration warpf = new YamlConfiguration();
            warpf.load(f);
            for (String s : warpf.getKeys(false)) {
                String owner = warpf.getString(s + ".owner");
                String wn = warpf.getString(s + ".world");
                World world = this.plugin.getServer().getWorld(wn);
                String node = warpf.getString(s + ".node");
                Double x = warpf.getDouble(s + ".x");
                Double y = warpf.getDouble(s + ".y");
                Double z = warpf.getDouble(s + ".z");
                Location loc = new Location(world, x, y, z);
                Warp w = new Warp(s, owner, loc, node);
                addWarp(w);
            }
            System.out.println("[CaptainWarp] Warps loaded.");
        } catch (Exception e) {
            System.out.println("[CaptainWarp] Error while loading warps.yml");
        }
    }
    
        /**
     * Reload warps from file
     */
    public void reloadWarps() {
        try {
            File f = new File(plugin.getDataFolder(), "warps.yml");
            YamlConfiguration warpf = new YamlConfiguration();
            warpf.load(f);
            warps.removeAll(warps);
            for (String s : warpf.getKeys(false)) {
                String owner = warpf.getString(s + ".owner");
                String wn = warpf.getString(s + ".world");
                World world = this.plugin.getServer().getWorld(wn);
                String node = warpf.getString(s + ".node");
                Double x = warpf.getDouble(s + ".x");
                Double y = warpf.getDouble(s + ".y");
                Double z = warpf.getDouble(s + ".z");
                Location loc = new Location(world, x, y, z);
                Warp w = new Warp(s, owner, loc, node);
                addWarp(w);
            }
            System.out.println("[CaptainWarp] Warps loaded.");
        } catch (Exception e) {
            System.out.println("[CaptainWarp] Error while loading warps.yml");
        }
    }

    /**
     * Save warps to file
     */
    public void saveWarps() {
        try {
            File f = new File(plugin.getDataFolder(), "warps.yml");
            YamlConfiguration warpf = new YamlConfiguration();
            for (Warp w : warps) {
                warpf.set(w.getName() + ".owner", w.getOwner());
                warpf.set(w.getName() + ".world", w.getLoc().getWorld().getName());
                warpf.set(w.getName() + ".node", w.getNode());
                warpf.set(w.getName() + ".x", w.getLoc().getX());
                warpf.set(w.getName() + ".y", w.getLoc().getY());
                warpf.set(w.getName() + ".z", w.getLoc().getZ());
            }
            warpf.save(f);
            System.out.println("[CaptainWarp] Warps saved.");
        } catch (Exception e) {
            System.out.println("[CaptainWarp] Error while saving warps.yml");
        }
    }
}

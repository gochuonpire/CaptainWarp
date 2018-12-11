package me.captain.warp;

import me.captain.lock.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author andrewbulkeley
 */
public class HomeCmd
        implements CommandExecutor {

    private final CaptainWarp plugin;

    /**
     * Create a new HomeCmd
     *
     * @param instance
     */
    public HomeCmd(CaptainWarp instance) {
        this.plugin = instance;
    }

    /**
     * Method to handle execution of /home
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            switch (args.length) {
                case 0:
                    if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.home.self")) {
                        Zone z = plugin.zones.getZoneHandler().isInside(player.getLocation());
                        if(z!=null) {
                            if(!player.hasPermission("captainwarp.*") && !player.hasPermission("captainwarp." + z.getNode())) {
                                player.sendMessage(ChatColor.GRAY + "You are not allowed to teleport from this location.");
                                return false;
                            }
                        }
                        Warp warpTo = plugin.wh.getWarp("@home-" + player.getName());
                        if (warpTo == null) {
                            displayHelp(player);
                        } else {
                            Location loc = warpTo.getLoc();
                            player.teleport(loc);
                            player.sendMessage(ChatColor.GRAY + "Welcome home");
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + "You don't have permission to use /home");
                    }
                    break;
                case 1: {
                    String arg = args[0];
                    if (arg.equals("set")) {
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.home.self")) {
                            Warp home = plugin.wh.getWarp("@home-" + player.getName());
                            if (home == null) {
                                home = new Warp("@home-" + player.getName(), player.getName(), player.getLocation(), "home.@" + player.getName());
                            } else {
                                home.setLoc(player.getLocation());
                            }
                            plugin.wh.updateWarp(home);
                            player.sendMessage(ChatColor.GRAY + "Your home was set");
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to use /home set");
                        }
                    } else if (arg.equals("help")) {
                        displayHelp(player);
                    } else if (arg.equals("list")) {
                        plugin.wh.sendHomes(player);
                    } else {
                        String pName = arg;
                        Warp pHome = plugin.wh.getWarp("@home-" + pName);
                        if (pHome == null) {
                            player.sendMessage(ChatColor.GRAY + "Home for " + ChatColor.RED + pName + ChatColor.GRAY + " not found");
                        } else if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.home.@" + pName)) {
                            player.teleport(pHome.getLoc());
                            player.sendMessage(ChatColor.GRAY + "Welcome to " + ChatColor.GREEN + pName + ChatColor.GRAY + "'s home");
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to visit " + ChatColor.RED + pName + ChatColor.GRAY + "'s home");
                        }
                    }
                    break;
                }
                case 2: {
                    String arg = args[0];
                    if (arg.equals("allow")) {
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.home.self")) {
                            String pName = args[1];
                            plugin.pm.getUser(pName).addPermission("captainwarp.home.@" + player.getName());
                            player.sendMessage(ChatColor.GRAY + "Home access given to " + ChatColor.GREEN + pName);
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to give a user access to your home");
                        }
                    } else if (arg.equals("disallow")) {
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.home.self")) {
                            String pName = args[1];
                            plugin.pm.getUser(pName).removePermission("captainwarp.home.@" + player.getName());
                            player.sendMessage(ChatColor.GRAY + "Home access revoked from " + ChatColor.RED + pName);
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to revoke a user's access to your home");
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
        return true;
    }

    public void displayHelp(Player player) {
        player.sendMessage(ChatColor.GRAY + "=== " + ChatColor.GREEN + "CaptainHome" + ChatColor.GRAY + " ===");
        player.sendMessage(ChatColor.GRAY + "/home - travel to your home");
        player.sendMessage(ChatColor.GRAY + "/home set - set your home to your current location");
        player.sendMessage(ChatColor.GRAY + "/home playername - travel to that player's home");
        player.sendMessage(ChatColor.GRAY + "/home allow player - give the player permission to travel to your home");
        player.sendMessage(ChatColor.GRAY + "/home disallow player - revoke the player's access to your home");
        player.sendMessage(ChatColor.GRAY + "/home help - display this help screen");
    }
}

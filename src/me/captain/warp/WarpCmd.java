package me.captain.warp;

import java.util.List;
import java.util.Set;
import me.captain.lock.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;

/**
 * Class that handles /warp execution
 *
 * @author andrewbulkeley
 */
public class WarpCmd
        implements CommandExecutor {

    private final CaptainWarp plugin;

    /**
     * Create new WarpCmd
     *
     * @param instance
     */
    public WarpCmd(CaptainWarp instance) {
        this.plugin = instance;
    }

    /**
     * Method to handle execution of /warp
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
                    player.sendMessage(ChatColor.GRAY + "=== " + ChatColor.GREEN + "CaptainWarp " + ChatColor.GRAY + "===");
                    if (player.hasPermission("captainwarp.list") || player.hasPermission("captainwarp.*")) {
                        player.sendMessage(ChatColor.GRAY + "/warp list - list all warps");
                    }
                    player.sendMessage(ChatColor.GRAY + "/warp <warp> - warps to <warp>");
                    if (player.hasPermission("captainwarp.create") || player.hasPermission("captainwarp.*")) {
                        player.sendMessage(ChatColor.GRAY + "/warp create <warp> <node> - creates a new warp");
                    }
                    if (player.hasPermission("captainwarp.private") || player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.create")) {
                        player.sendMessage(ChatColor.GRAY + "/warp private <warp> - creates a new private warp");
                    }
                    if (player.hasPermission("captainwarp.create") || player.hasPermission("captainwarp.remove") || player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.private") || player.hasPermission("captainwarp.update")) {
                        player.sendMessage(ChatColor.GRAY + "/warp remove <warp> - removes a warp");
                        player.sendMessage(ChatColor.GRAY + "/warp update <warp> - updates warp location (if owner)");
                        player.sendMessage(ChatColor.GRAY + "/warp allow <warp> <player> - allows player to use warp");
                        player.sendMessage(ChatColor.GRAY + "/warp disallow <warp> <player> - disallows player from using warp");
                    }
                    if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.rename") || player.hasPermission("captainwarp.create")) {
                        player.sendMessage(ChatColor.GRAY + "/warp rename <warp> <new name> - renames a warp");
                    }
                    if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.renode") || player.hasPermission("captainwarp.create")) {
                        player.sendMessage(ChatColor.GRAY + "/warp renode <warp> <new node> - renodes a warp");
                    }
                    if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.refactor") || player.hasPermission("captainwarp.create")) {
                        player.sendMessage(ChatColor.GRAY + "/warp refactor <old node> <new node> <flag> - read readme.txt");
                    }
                    break;
                case 1:
                    String arg = args[0];
                    if (arg.equals("list")) {
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.list")) {
                            plugin.wh.sendList(player);
                            return true;
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to list warps.");
                            return true;
                        }
                    } else if (arg.equals("@save")) {
                        if (player.hasPermission("captainwarp.*")) {
                            plugin.wh.saveWarps();
                            player.sendMessage(ChatColor.GRAY + "Warps saved");
                        }
                    } else if (arg.equals("@load")) {
                        if (player.hasPermission("captainwarp.*")) {
                            plugin.wh.loadWarps();
                            player.sendMessage(ChatColor.GRAY + "Warps loaded");
                        }
                    } else {
                        Zone z = plugin.zones.getZoneHandler().isInside(player.getLocation());
                        if (z != null) {
                            if (!player.hasPermission("captainwarp.*") && !player.hasPermission("captainwarp." + z.getNode())) {
                                player.sendMessage(ChatColor.GRAY + "You are not allowed to teleport from this location.");
                                return false;
                            }
                        }
                        Warp w = plugin.wh.getWarp(args[0]);
                        if (w != null) {
                            if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.warps." + w.getNode()) || player.hasPermission("captainwarp.home.@" + args[0])) {
                                player.teleport(w.getLoc());
                                player.sendMessage(ChatColor.GRAY + "You've been warped to " + ChatColor.GREEN + w.getName());
                                return true;
                            } else {
                                player.sendMessage(ChatColor.GRAY + "You don't have permission to warp to " + ChatColor.GREEN + w.getName());
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + args[0] + ChatColor.GRAY + " not found.");
                            return true;
                        }
                    }
                    break;
                case 2: {
                    arg = args[0];
                    if (arg.equals("remove")) {
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.remove")) {
                            Warp w = plugin.wh.getWarp(args[1]);
                            if (w != null) {
                                plugin.wh.removeWarp(w);
                                player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.GREEN + w.getName() + ChatColor.GRAY + " has been removed.");
                                return true;
                            } else {
                                player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + args[1] + ChatColor.GRAY + " not found.");
                                return true;
                            }
                        } else {
                            Warp w = plugin.wh.getWarp(args[1]);
                            if (w != null) {
                                if (w.getOwner().equals(player.getName())) {
                                    plugin.wh.removeWarp(w);
                                    player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.GREEN + w.getName() + ChatColor.GRAY + " has been removed.");
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.GRAY + "You don't have permission to remove " + ChatColor.RED + w.getName());
                                    return true;
                                }
                            }
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to remove warps.");
                            return true;
                        }
                    } else if (arg.equals("update")) {
                        String w = args[1];
                        Warp warp = plugin.wh.getWarp(w);
                        if (warp != null) {
                            if (player.getName().equals(warp.getOwner()) || player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.update")) {
                                warp.setLoc(player.getLocation());
                                plugin.wh.updateWarp(warp);
                                player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.GREEN + w + ChatColor.GRAY + " has been updated.");
                            } else {
                                player.sendMessage(ChatColor.GRAY + "You don't have permission to update " + w);
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + w + ChatColor.GRAY + " not found.");
                            return true;
                        }
                    } else if (arg.equals("private")) {
                        if (plugin.wh.warps.size() > plugin.totalwarps) {
                            player.sendMessage(ChatColor.GRAY + "The maximum of " + plugin.totalwarps + " warps has been reached.");
                            return true;
                        }
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.private") || player.hasPermission("captainwarp.create")) {
                            String w = args[1];
                            if (w.contains("@") || w.equals("list")) {
                                if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.override")) {
                                    if (w.equals("@save") || w.equals("@load")) {
                                        player.sendMessage(ChatColor.GRAY + "@save and @load reserved for admin commands");
                                        return true;
                                    }
                                    player.sendMessage(ChatColor.GRAY + "Creating hidden private warp " + ChatColor.GREEN + w);
                                } else {
                                    player.sendMessage(ChatColor.GRAY + "Warp names cannot be 'list' or contain '@'");
                                    return true;
                                }
                            } else {
                                Warp warp = new Warp(w, player.getName(), player.getLocation(), w);
                                plugin.wh.addWarp(warp);
                                if(!player.hasPermission("captainwarp.*")) {
                                    plugin.pm.getUser(player).addPermission("captainwarp.warps." + w);
                                }
                                player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.GREEN + w + ChatColor.GRAY + " created");
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to create a private warp");
                        }
                    } else if (arg.equals("info")) {
                        String w = args[1];
                        Warp warp = plugin.wh.getWarp(w);
                        if (warp != null) {
                            if (player.getName().equals(warp.getOwner()) || player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.info")) {
                                player.sendMessage(ChatColor.GRAY + "Warp Name: " + ChatColor.GRAY + warp.getName());
                                if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.warps." + warp.getNode())) {
                                    player.sendMessage(ChatColor.GRAY + "Warp Node: " + ChatColor.GREEN + warp.getNode());
                                } else {
                                    player.sendMessage(ChatColor.GRAY + "Warp Node: " + ChatColor.RED + warp.getNode());
                                }
                                if (player.getName().equals(warp.getOwner())) {
                                    player.sendMessage(ChatColor.GRAY + "Warp Owner: " + ChatColor.GREEN + warp.getOwner());
                                } else {
                                    player.sendMessage(ChatColor.GRAY + "Warp Owner: " + ChatColor.GRAY + warp.getOwner());
                                }
                                Location loc = warp.getLoc();
                                player.sendMessage(ChatColor.GRAY + "Warp World: " + ChatColor.GRAY + loc.getWorld().getName());
                                player.sendMessage(ChatColor.GRAY + "Warp Coordinates: (" + ChatColor.GRAY + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")");
                            } else {
                                if (warp.getName().startsWith("@")) {
                                    player.sendMessage(ChatColor.GRAY + "Couldn't find warp " + ChatColor.RED + w);
                                    return true;
                                }
                                player.sendMessage(ChatColor.GRAY + "Warp Name: " + ChatColor.GRAY + warp.getName());
                                player.sendMessage(ChatColor.GRAY + "Warp Owner: " + ChatColor.GRAY + warp.getOwner());
                                if (player.hasPermission("captainwarp.warps." + warp.getNode())) {
                                    Location loc = warp.getLoc();
                                    player.sendMessage(ChatColor.GRAY + "Warp World: " + ChatColor.GRAY + loc.getWorld().getName());
                                    player.sendMessage(ChatColor.GRAY + "Warp Coordinates: " + ChatColor.GRAY + "(" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")");
                                    player.sendMessage(ChatColor.GRAY + "Warp Access: " + ChatColor.GREEN + "Granted");
                                } else {
                                    player.sendMessage(ChatColor.GRAY + "Warp Access: " + ChatColor.RED + "Denied");
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + w + ChatColor.GRAY + " not found.");
                            return true;
                        }
                    }
                }
                case 3:
                    arg = args[0];
                    if (arg.equals("create")) {
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.create")) {
                            if (plugin.wh.warps.size() > plugin.totalwarps) {
                                player.sendMessage(ChatColor.GRAY + "The maximum of " + plugin.totalwarps + " warps has been reached.");
                                return true;
                            }
                            String w = args[1];
                            if (w.contains("list")) {
                                player.sendMessage(ChatColor.GRAY + "You can't use names that contain 'list'");
                                return true;
                            } else if (w.contains("@")) {
                                if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.override")) {
                                    if (w.equals("@save") || w.equals("@load")) {
                                        player.sendMessage(ChatColor.GRAY + "@save and @load reserved for admin commands");
                                        return true;
                                    }
                                    player.sendMessage(ChatColor.GRAY + "Creating hidden warp with name " + w);
                                } else {
                                    player.sendMessage(ChatColor.GRAY + "You can't use names that contain '@'");
                                    return true;
                                }
                            }
                            String n = args[2];
                            Warp warp = new Warp(w, player.getName(), player.getLocation(), n);
                            plugin.wh.addWarp(warp);
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.GREEN + w + ChatColor.GRAY + " created with permissions node " + ChatColor.GREEN + n);
                            return true;
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to create new warps.");
                            return true;
                        }
                    } else if (arg.equals("allow")) {
                        String wName = args[1];
                        String pName = args[2];
                        Warp w = plugin.wh.getWarp(wName);
                        if (w == null) {
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " not found");
                        } else if (player.hasPermission("captainwarp.*") || w.getOwner().equals(player.getName()) || player.hasPermission("captainwarp.access")) {
                            plugin.pm.getUser(pName).addPermission("captainwarp.warps." + w.getNode());
                            player.sendMessage(ChatColor.GREEN + pName + ChatColor.GRAY + " given access to " + ChatColor.GREEN + w.getName());
                            for (Player p : plugin.getServer().getOnlinePlayers()) {
                                if (p.getName().equals(pName)) {
                                    p.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + " has given you access to " + ChatColor.GREEN + w.getName());
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to allow users access to " + ChatColor.RED + wName);
                        }
                    } else if (arg.equals("disallow")) {
                        String wName = args[1];
                        String pName = args[2];
                        Warp w = plugin.wh.getWarp(wName);
                        if (w == null) {
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " not found");
                        } else if (player.hasPermission("captainwarp.*") || w.getOwner().equals(player.getName()) || player.hasPermission("captainwarp.access")) {
                            plugin.pm.getUser(pName).removePermission("captainwarp.warps." + w.getNode());
                            player.sendMessage(ChatColor.GREEN + pName + ChatColor.GRAY + " no longer has access to " + ChatColor.GREEN + wName);
                            for (Player p : plugin.getServer().getOnlinePlayers()) {
                                if (p.getName().equals(pName)) {
                                    p.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + " has revoked your access to " + ChatColor.RED + w.getName());
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to disallow users access to " + ChatColor.RED + wName);
                        }
                    } else if (arg.equals("rename")) {
                        String wName = args[1];
                        String wNewName = args[2];
                        Warp w = plugin.wh.getWarp(wName);
                        if (w == null) {
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " not found");
                            return true;
                        } else if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.rename") || (player.hasPermission("captainwarp.private.reanme") && w.getOwner().equals(player.getName())) || (player.hasPermission("captainwarp.create") && w.getOwner().equals(player.getName()))) {
                            Warp testNew = plugin.wh.getWarp(wNewName);
                            if (testNew == null) {
                                w.setName(wNewName);
                                plugin.wh.updateWarp(w);
                                player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " renamed to " + ChatColor.GREEN + w.getName());
                                return true;
                            } else {
                                player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wNewName + ChatColor.GRAY + " already exists");
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "You don't have permission to rename " + ChatColor.RED + wName);
                        }
                    } else if (arg.equals("renode")) {
                        Warp w = plugin.wh.getWarp(args[1]);
                        if (w == null) {
                            player.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + args[1] + ChatColor.GRAY + " not found");
                            return true;
                        } else if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.renode") || (player.hasPermission("captainwarp.create") && w.getOwner().equals(player.getName()))) {
                            String newNode = args[2];
                            String oldNode = w.getNode();
                            w.setNode(newNode);
                            plugin.wh.updateWarp(w);
                            player.sendMessage(ChatColor.GRAY + "Updated " + ChatColor.GREEN + w.getName() + ChatColor.GRAY + " node from " + ChatColor.RED + oldNode + ChatColor.GRAY + " to " + ChatColor.GREEN + w.getNode());
                            player.sendMessage(ChatColor.GRAY + "To edit permissions, use /warp refactor or manually edit permissions.yml");
                        }
                    }
                    break;
                case 4:
                    arg = args[0];
                    if (arg.equals("refactor")) {
                        if (player.hasPermission("captainwarp.*") || player.hasPermission("captainwarp.refactor")) {
                            String wPerm = "captainwarp.warps." + args[1];
                            String wNewPerm = "captainwarp.warps." + args[2];
                            try {
                                Integer groupFlag = Integer.valueOf(args[3]);
                                switch (groupFlag) {
                                    case 0:
                                        List<PermissionGroup> groups;
                                        groups = plugin.pm.getGroupList();
                                        for (PermissionGroup group : groups) {
                                            if (group.has(wPerm)) {
                                                group.addPermission(wNewPerm);
                                            }
                                        }
                                        player.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " added for groups");
                                        break;
                                    case 1:
                                        groups = plugin.pm.getGroupList();
                                        for (PermissionGroup group : groups) {
                                            if (group.has(wPerm)) {
                                                group.addPermission(wNewPerm);
                                                group.removePermission(wPerm);
                                            }
                                        }
                                        player.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.RED + wPerm + ChatColor.GRAY + " replaced by " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " for groups");
                                        break;
                                    case 2:
                                        Set<PermissionUser> users;
                                        users = plugin.pm.getUsers();
                                        for (PermissionUser user : users) {
                                            if (user.has(wPerm)) {
                                                user.addPermission(wNewPerm);
                                            }
                                        }
                                        player.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " added for users");

                                        break;
                                    case 3:
                                        users = plugin.pm.getUsers();
                                        for (PermissionUser user : users) {
                                            if (user.has(wPerm)) {
                                                user.addPermission(wNewPerm);
                                                user.removePermission(wPerm);
                                            }
                                        }
                                        player.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.RED + wPerm + ChatColor.GRAY + " replaced by " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " for users");
                                        break;
                                    case 4:
                                        groups = plugin.pm.getGroupList();
                                        for (PermissionGroup group : groups) {
                                            if (group.has(wPerm)) {
                                                group.addPermission(wNewPerm);
                                            }
                                        }
                                        users = plugin.pm.getUsers();
                                        for (PermissionUser user : users) {
                                            if (user.has(wPerm)) {
                                                user.addPermission(wNewPerm);
                                            }
                                        }
                                        player.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " added for groups and users");
                                        break;
                                    case 5:
                                        groups = plugin.pm.getGroupList();
                                        for (PermissionGroup group : groups) {
                                            if (group.has(wPerm)) {
                                                group.addPermission(wNewPerm);
                                                group.removePermission(wPerm);
                                            }
                                        }
                                        users = plugin.pm.getUsers();
                                        for (PermissionUser user : users) {
                                            if (user.has(wPerm)) {
                                                user.addPermission(wNewPerm);
                                                user.removePermission(wPerm);
                                            }
                                        }
                                        player.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.RED + wPerm + ChatColor.GRAY + " replaced by " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " for groups and users");
                                        break;
                                    default:
                                        player.sendMessage(ChatColor.GRAY + "Invalid group flag, read readme.txt");
                                }
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.GRAY + "Invalid group flag, read readme.txt");
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (args.length) {
                case 0:
                    sender.sendMessage(ChatColor.GRAY + "=== " + ChatColor.GREEN + "CaptainWarp " + ChatColor.GRAY + "===");
                    sender.sendMessage(ChatColor.GRAY + "warp list - list all warps");
                    sender.sendMessage(ChatColor.GRAY + "warp remove <warp> - removes a warp");
                    sender.sendMessage(ChatColor.GRAY + "warp allow <warp> <player> - allows player to use warp (if owner)");
                    sender.sendMessage(ChatColor.GRAY + "warp disallow <warp> <player> - disallows player from using warp (if owner)");
                    sender.sendMessage(ChatColor.GRAY + "warp rename <warp> <new name> - renames a warp");
                    sender.sendMessage(ChatColor.GRAY + "warp renode <warp> <new node> - renodes a warp");
                    sender.sendMessage(ChatColor.GRAY + "warp refactor <old node> <new node> <flag> - read readme.txt");
                    sender.sendMessage(ChatColor.GRAY + "warp @save - save warps");
                    sender.sendMessage(ChatColor.GRAY + "warp @load - load warps");
                    return (true);
                case 1:
                    String arg = args[0];
                    if (arg.equals("list")) {
                        plugin.wh.sendList(sender);
                    } else if (arg.equals("@save")) {
                        plugin.wh.saveWarps();
                        sender.sendMessage(ChatColor.GRAY + "Warps saved");
                    } else if (arg.equals("@load")) {
                        plugin.wh.reloadWarps();
                        sender.sendMessage(ChatColor.GRAY + "Warps loaded");
                    }
                    break;
                case 2:
                    arg = args[0];
                    if (arg.equals("remove")) {
                        String wName = args[1];
                        Warp toRemove = plugin.wh.getWarp(wName);
                        if (toRemove == null) {
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " not found");
                        } else {
                            plugin.wh.removeWarp(toRemove);
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.GREEN + wName + ChatColor.GRAY + " removed");
                        }
                    } else if (arg.equals("info")) {
                        String w = args[1];
                        Warp warp = plugin.wh.getWarp(w);
                        if (warp != null) {
                            if (warp.getName().startsWith("@")) {
                                sender.sendMessage(ChatColor.GRAY + "Warp Name: " + ChatColor.RED + warp.getName());
                            } else {
                                sender.sendMessage(ChatColor.GRAY + "Warp Name: " + ChatColor.GREEN + warp.getName());
                            }
                            sender.sendMessage(ChatColor.GRAY + "Warp Node: " + ChatColor.GRAY + warp.getNode());
                            sender.sendMessage(ChatColor.GRAY + "Warp Owner: " + ChatColor.GRAY + warp.getOwner());
                            Location loc = warp.getLoc();
                            sender.sendMessage(ChatColor.GRAY + "Warp World: " + ChatColor.GRAY + loc.getWorld().getName());
                            sender.sendMessage(ChatColor.GRAY + "Warp Coordinates: (" + ChatColor.GRAY + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ")");
                        } else {
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + w + ChatColor.GRAY + " not found.");
                            return true;
                        }
                    }
                    break;
                case 3:
                    arg = args[0];
                    if (arg.equals("allow")) {
                        String wName = args[1];
                        String pName = args[2];
                        Warp w = plugin.wh.getWarp(wName);
                        if (w == null) {
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " not found");
                        } else {
                            plugin.pm.getUser(pName).addPermission("captainwarp.warps." + w.getNode());
                            for (Player p : plugin.getServer().getOnlinePlayers()) {
                                if (p.getName().equals(pName)) {
                                    p.sendMessage(ChatColor.GREEN + "console" + ChatColor.GRAY + " has given you access to " + ChatColor.GREEN + w.getName());
                                }
                            }
                            sender.sendMessage(ChatColor.GRAY + "Player " + ChatColor.GREEN + pName + ChatColor.GRAY + " given access to " + ChatColor.GREEN + wName);
                        }
                    } else if (arg.equals("disallow")) {
                        String wName = args[1];
                        String pName = args[2];
                        Warp w = plugin.wh.getWarp(wName);
                        if (w == null) {
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " not found");
                        } else {
                            plugin.pm.getUser(pName).removePermission("captainwarp.warps." + w.getNode());
                            for (Player p : plugin.getServer().getOnlinePlayers()) {
                                if (p.getName().equals(pName)) {
                                    p.sendMessage(ChatColor.GREEN + "console" + ChatColor.GRAY + " has revoked you access to " + ChatColor.RED + w.getName());
                                }
                            }
                            sender.sendMessage(ChatColor.GRAY + "Player " + ChatColor.GREEN + pName + ChatColor.GRAY + " no longer has access to " + ChatColor.GREEN + wName);
                        }
                    } else if (arg.equals("rename")) {
                        String wName = args[1];
                        String wNewName = args[2];
                        Warp w = plugin.wh.getWarp(wName);
                        if (w == null) {
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " not found");
                            return true;
                        }
                        Warp testNew = plugin.wh.getWarp(wNewName);
                        if (testNew == null) {
                            w.setName(wNewName);
                            plugin.wh.updateWarp(w);
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wName + ChatColor.GRAY + " renamed to " + ChatColor.GREEN + w.getName());
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + wNewName + ChatColor.GRAY + " already exists");
                        }

                    } else if (arg.equals("renode")) {
                        Warp w = plugin.wh.getWarp(args[1]);
                        if (w == null) {
                            sender.sendMessage(ChatColor.GRAY + "Warp " + ChatColor.RED + args[1] + ChatColor.GRAY + " not found");
                            return true;
                        }
                        String newNode = args[2];
                        String oldNode = w.getNode();
                        w.setNode(newNode);
                        plugin.wh.updateWarp(w);
                        sender.sendMessage(ChatColor.GRAY + "Updated " + ChatColor.GREEN + w.getName() + ChatColor.GRAY + " node from " + ChatColor.RED + oldNode + ChatColor.GRAY + " to " + ChatColor.GREEN + w.getNode());
                        sender.sendMessage(ChatColor.GRAY + "To edit permissions, use /warp refactor or manually edit permissions.yml");
                    }
                    break;
                case 4:
                    arg = args[0];
                    if (arg.equals("refactor")) {
                        String wPerm = "captainwarp.warps." + args[1];
                        String wNewPerm = "captainwarp.warps." + args[2];
                        try {
                            Integer groupFlag = Integer.valueOf(args[3]);
                            switch (groupFlag) {
                                case 0:
                                    List<PermissionGroup> groups;
                                    groups = plugin.pm.getGroupList();
                                    for (PermissionGroup group : groups) {
                                        if (group.has(wPerm)) {
                                            group.addPermission(wNewPerm);
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " added for groups");
                                    break;
                                case 1:
                                    groups = plugin.pm.getGroupList();
                                    for (PermissionGroup group : groups) {
                                        if (group.has(wPerm)) {
                                            group.addPermission(wNewPerm);
                                            group.removePermission(wPerm);
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.RED + wPerm + ChatColor.GRAY + " replaced by " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " for groups");
                                    break;
                                case 2:
                                    Set<PermissionUser> users;
                                    users = plugin.pm.getUsers();
                                    for (PermissionUser user : users) {
                                        if (user.has(wPerm)) {
                                            user.addPermission(wNewPerm);
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " added for users");

                                    break;
                                case 3:
                                    users = plugin.pm.getUsers();
                                    for (PermissionUser user : users) {
                                        if (user.has(wPerm)) {
                                            user.addPermission(wNewPerm);
                                            user.removePermission(wPerm);
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.RED + wPerm + ChatColor.GRAY + " replaced by " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " for users");
                                    break;
                                case 4:
                                    groups = plugin.pm.getGroupList();
                                    for (PermissionGroup group : groups) {
                                        if (group.has(wPerm)) {
                                            group.addPermission(wNewPerm);
                                        }
                                    }
                                    users = plugin.pm.getUsers();
                                    for (PermissionUser user : users) {
                                        if (user.has(wPerm)) {
                                            user.addPermission(wNewPerm);
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " added for groups and users");
                                    break;
                                case 5:
                                    groups = plugin.pm.getGroupList();
                                    for (PermissionGroup group : groups) {
                                        if (group.has(wPerm)) {
                                            group.addPermission(wNewPerm);
                                            group.removePermission(wPerm);
                                        }
                                    }
                                    users = plugin.pm.getUsers();
                                    for (PermissionUser user : users) {
                                        if (user.has(wPerm)) {
                                            user.addPermission(wNewPerm);
                                            user.removePermission(wPerm);
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GRAY + "Permission node " + ChatColor.RED + wPerm + ChatColor.GRAY + " replaced by " + ChatColor.GREEN + wNewPerm + ChatColor.GRAY + " for groups and users");
                                    break;
                                default:
                                    sender.sendMessage(ChatColor.GRAY + "Invalid group flag, read readme.txt");
                            }
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.GRAY + "Invalid group flag, read readme.txt");
                        }
                    }
                    break;
            }
        }
        return true;
    }
}

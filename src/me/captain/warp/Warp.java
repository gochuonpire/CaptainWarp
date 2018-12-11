package me.captain.warp;

import org.bukkit.Location;

/**
 * Class to hold warp values, for ease of use
 *
 * @author andrewbulkeley
 */
public class Warp {

    //Values
    private String owner;
    private Location loc;
    private String node;
    private String name;

    /**
     * Create a new Warp
     *
     * @param nName
     * @param nOwner
     * @param nLoc
     * @param nNode
     */
    public Warp(String nName, String nOwner, Location nLoc, String nNode) {
        owner = nOwner;
        loc = nLoc;
        node = nNode;
        name = nName;
    }

    /**
     * Get the warp's owner (player name as string)
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Set the warp's owner (player name as string)
     *
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get the warp's location
     *
     * @return the location
     */
    public Location getLoc() {
        return loc;
    }

    /**
     * Set the warp's location
     *
     * @param loc the location to set
     */
    public void setLoc(Location loc) {
        this.loc = loc;
    }

    /**
     * Get the warp's permission node
     *
     * @return the node
     */
    public String getNode() {
        return node;
    }

    /**
     * Set the warp's permission node
     *
     * @param node the node to set
     */
    public void setNode(String node) {
        this.node = node;
    }

    /**
     * Get the warp's name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the warp's name
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}

package me.captain.warp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import me.captain.lock.CaptainLock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Main class of CaptainWarp. Overrides required methods and loads configuration
 *
 * @author andrewbulkeley
 * @version 2.0
 */
public class CaptainWarp extends JavaPlugin {

    /**
     * Configuration file
     */
    public FileConfiguration config;
    
    public CaptainLock zones;

    /**
     * Maximum number of allowed warps
     */
    public Integer totalwarps;

    /**
     * Create ReadMe
     */
    public Boolean createReadMe;
    
    /**
     * WarpHandler for public use
     */
    public WarpHandler wh;

    /**
     * PermissionManager to give/revoke user permissions
     */
    public PermissionManager pm;

    /**
     * Overrides default method, runs when enabled
     */
    @Override
    public void onEnable() {
        this.getCommand("warp").setExecutor(new WarpCmd(this));
        this.getCommand("home").setExecutor(new HomeCmd(this));
        loadConfig();
        wh = new WarpHandler(this);
        wh.loadWarps();
        pm = PermissionsEx.getPermissionManager();
        if(createReadMe) {
            String created = createReadMeFile();
            System.out.println(created);
        }
        zones = null;
        try {
            zones = (CaptainLock) this.getServer().getPluginManager().getPlugin("CaptainLock");
        } catch(Exception e) {
            this.getServer().getConsoleSender().sendMessage("[CaptainWarp] CaptainLock hook failed.");
        }
    }

    /**
     * Overrides default method, runs when disabled
     */
    @Override
    public void onDisable() {
        wh.saveWarps();
    }
    

    /**
     * Loads configuration from file
     */
    public void loadConfig() {
        config = getConfig();
        //set defaults
        config.addDefault("totalwarpsallowed", 100);
        config.addDefault("createReadMe", true);
        config.options().copyDefaults(true);
        this.saveConfig();
        totalwarps = config.getInt("totalwarpsallowed");
        createReadMe = config.getBoolean("createReadMe");
    }

    /**
     * Creates readme if none found
     *
     * @return
     */
    public String createReadMeFile() {
        File readMe = new File(this.getDataFolder().getAbsolutePath() + File.separator + "readme.txt");
        if (!readMe.exists()) {
            try {
                InputStream inStream = this.getClass().getResourceAsStream("readme.txt");
                FileOutputStream outStream = new FileOutputStream(readMe);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }
                if (inStream != null) {
                    inStream.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Exception e) {
                return "[CaptainWarp] Error creating readme file.";
            }
            return "[CaptainWarp] ReadMe file created at /plugins/CaptainWarp/readme.txt";
        } else {
            return "[CaptainWarp] ReadMe file located at /plugins/CaptainWarp/readme.txt";
        }
    }
}

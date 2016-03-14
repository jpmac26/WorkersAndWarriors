package nmt.minecraft.WorkersAndWarriors.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;

/**
 * Static single-ton configuration class for grabbing details about plugin-wide configuration.<br />
 * <b>If a getter doesn't exist</b> for a key value you want, use {@link #getValue(Key)} and just cast it.
 * @author Skyler
 *
 */
public class PluginConfiguration {
	
	/**
	 * Class that holds keys and default values
	 * @author Skyler
	 *
	 */
	public enum Key {
		
		VERSION("version", 1.00),
		TEAM("teams", new LinkedList<TeamConfiguration>()),
		FLAGPROTECTSIZE("flagzone.size", 3),
		FLAGISPROTECTED("flagzone.protected", true),
		TEAMBLOCKS("teamblocks", 30),
		POINTSTOWIN("points", 10),
		RESPAWNTIME("respawn.time", 3),
		TEAMMAX("numberofteams", 2);
		
		private String key;
		
		private Object def;
		
		private Key(String key) {
			this(key, null);
		}
		
		private Key(String key, Object def) {
			this.key = key;
			this.def = def;
		}
		
		public String getKey() {
			return this.key;
		}
		
		public Object getDefault() {
			return this.def;
		}
	}
	
	/**
	 * Static reference to the current config. Null if no config has been created yet
	 */
	public static PluginConfiguration config;
	
	/**
	 * Map of all config options, stored by key. Fancy, eh?
	 */
	private Map<Key, Object> configMap;
	
	/**
	 * Attempts to create a new {@link PluginConfiguration} based on the provided config file.<br />
	 * If the file is null or the file doesn't exist but no config exists yet, a default config is made. If
	 * the file is null or doesn't exist and there already exists a config, no action is performed. Otherwise,
	 * the stored PluginConfiguration is discarded and replaced with the one made by reading the file. The 
	 * same PluginConfiguration is also returned.
	 * @param configFile
	 * @return
	 */
	public static PluginConfiguration makeConfiguration(File configFile) {
		
		if (config == null && (configFile == null || !configFile.exists())) {
			config = new PluginConfiguration();
			for (Key key : Key.values()) {
				config.configMap.put(key, key.getDefault());
			}
			
			return config;
		}
		
		if (configFile == null || !configFile.exists()) {
			return config;
		}
		
		//for ease of use
		Logger logger = WorkersAndWarriorsPlugin.plugin.getLogger(); 
		
		YamlConfiguration yConfig = new YamlConfiguration();
		
		try {
			yConfig.load(configFile);
		} catch (FileNotFoundException e) {
			logger.warning("Unable to locate config file " + configFile.getAbsolutePath());
			return config;
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Error when reading config file " + configFile.getAbsolutePath());
			return config;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			logger.warning("Unable to load configuration from config file " + configFile.getAbsolutePath());
			return config;
		}
		
		PluginConfiguration.config = new PluginConfiguration();
		
		//everything worked, yaml loaded. Now grab values
		Object o;
		boolean trip = false;
		for (Key key : Key.values()) {
			if (!yConfig.contains(key.getKey())) {
				if (!trip) {
					logger.info("Unable to find some keys. Setting default values for:");
					trip = true;
				}
				logger.info(key.name() + " [" + key.getKey() + "]");
				PluginConfiguration.config.configMap.put(key, key.getDefault());
				continue;
			}
			
			o = yConfig.get(key.getKey());
			if (!o.getClass().equals(key.getDefault().getClass())) {
				//classes don't match! Invalid key entry!
				logger.warning("Unable to load config value for key [" + ChatColor.BOLD + key.key + 
						ChatColor.RESET + "] because of a class mismatch. Default used instead." );
				logger.warning("  -> Class [" + o.getClass().getName() + "] <> [" + key.getDefault().getClass().getName() + "]");
				PluginConfiguration.config.configMap.put(key, key.getDefault());
				continue;
			}
			
			//else classes match, so update
			PluginConfiguration.config.configMap.put(key, o);
		}
		
		return PluginConfiguration.config;
	}
	
	/**
	 * Completely overwrites the given file (creating if it doesn't exist) with the configuration currently
	 * stored.
	 * @param saveFile
	 * @throws IOException 
	 */
	public static void save(File saveFile) throws IOException {
		if (config == null) {
			WorkersAndWarriorsPlugin.plugin.getLogger().warning("Null config cannot be saved! Skipping!");
			return;
		}
		
		YamlConfiguration yConfig = new YamlConfiguration();
		for (Key key : Key.values()) {
			yConfig.set(key.getKey(), config.getValue(key));
		}
		
		yConfig.save(saveFile);
	}
	
	/**
	 * Makes a new plugin configuration with a default config map
	 */
	private PluginConfiguration() {
		configMap = new HashMap<>();
	}
	
	/**
	 * Raw getter method for any key value. Use it, I dare ya!<br />
	 * When using this method, be aware that the object is returned uncast. Be careful with casts, but don't
	 * be afraid to use it where you have to. Don't forget to wrap in 'instanceof's!<br />
	 * To find out what class each key returns, see the {@link Key} class itself. Classes will always
	 * be that of the default value.
	 * <p>
	 * For an example of how to use this function, see {@link #getValue(Key)} and the like.
	 * </p>
	 * @param key
	 * @return
	 */
	public Object getValue(Key key) {
		return configMap.get(key);
	}
	
	public double getVersion() {
		return (Double) getValue(Key.VERSION);
	}
	
	public int getPointsToWin() {
		return (Integer) getValue(Key.POINTSTOWIN);
	}
	
	public boolean getFlagAreaProtected() {
		return (Boolean) getValue(Key.FLAGISPROTECTED);
	}
	
	public int getFlagAreaSize() {
		return (Integer) getValue(Key.FLAGPROTECTSIZE);
	}
	
	/**
	 * Returns the maximum number of teams allowed by the plugin configuration.
	 * @return
	 */
	public int getMaxTeams() {
		return (Integer) getValue(Key.TEAMMAX);
	}
	
	/**
	 * Returns the maximum number of ablock a team should have.
	 * @return
	 */
	public int getBlockCount() {
		return (Integer) getValue(Key.TEAMBLOCKS);
	}
	
	/**
	 * Returns the cooldown time to respawn, <b>in seconds</b>
	 * @return
	 */
	public int getRespawnCooldown() {
		return (Integer) getValue(Key.RESPAWNTIME);
	}
	
}

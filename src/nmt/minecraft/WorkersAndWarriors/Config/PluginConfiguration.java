package nmt.minecraft.WorkersAndWarriors.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;

/**
 * Static single-ton configuration class for grabbing details about plugin-wide configuration
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
		TEAM1COLOR("team1.color", ChatColor.RED),
		TEAM1NAME("team1.name", "Red Team"),
		TEAM2COLOR("team2.color", ChatColor.BLUE),
		TEAM2NAME("team2.name", "Blue Team"),
		FLAGPROTECTSIZE("flagzone.size", 3),
		FLAGISPROTECTED("flagzone.protected", true),
		POINTSTOWIN("points", 10);
		
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
	
	public static PluginConfiguration config;
	
	private Map<Key, Object> configMap;
	
	/**
	 * Attempts to create a new {@link PluginConfiguration} based on the provided config file.<br />
	 * If the file is null or the file doesn't exist, no action is performed. Otherwise, the stored 
	 * PluginConfiguration is discarded and replaced with the one made by reading the file. The same 
	 * PluginConfiguration is also returned.
	 * @param configFile
	 * @return
	 */
	public static PluginConfiguration makeConfiguration(File configFile) {
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
		for (Key key : Key.values()) {
			if (!yConfig.contains(key.getKey())) {
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
	
	public String getTeam1Name() {
		return (String) getValue(Key.TEAM1NAME);
	}
	
	public String getTeam2Name() {
		return (String) getValue(Key.TEAM2NAME);
	}
	
	public ChatColor getTeam1Color() {
		return (ChatColor) getValue(Key.TEAM1COLOR);
	}
	
	public ChatColor getTeam2Color() {
		return (ChatColor) getValue(Key.TEAM2COLOR);
	}
	
}

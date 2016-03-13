package nmt.minecraft.WorkersAndWarriors;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import nmt.minecraft.WorkersAndWarriors.Config.PluginConfiguration;
import nmt.minecraft.WorkersAndWarriors.Config.SessionConfiguration;
import nmt.minecraft.WorkersAndWarriors.Config.Util.ParsedLocation;
import nmt.minecraft.WorkersAndWarriors.Config.Util.ParsedVector;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * Main plugin class. Creates top-level infrastructure, excluding game-session-specific componenets.
 * @author Skyler
 *
 */
public class WorkersAndWarriorsPlugin extends JavaPlugin {
	
	private static final String configFileName = "config.yml";
	
	public static final Random random = new Random();

	public static WorkersAndWarriorsPlugin plugin;
	
	private Set<GameSession> sessions;
	

	
	
	@Override
	public void onLoad() {
		WorkersAndWarriorsPlugin.plugin = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		
		File configFile = new File(getDataFolder(), WorkersAndWarriorsPlugin.configFileName);
		
		PluginConfiguration.makeConfiguration(configFile);
		
		if (!configFile.exists()) {
			try {
				PluginConfiguration.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
				getLogger().warning("Unable to save default config file!");
			}
		}
		
		File templateDirectory = new File(getDataFolder(), SessionConfiguration.sessionDirName);
		if (!templateDirectory.exists()) {
			templateDirectory.mkdirs();
		}
	}
	
	@Override
	public void onEnable() {
		ParsedLocation.registerWithAliases();
		ParsedVector.registerWithAliases();
		
		this.sessions = new HashSet<GameSession>();
	}
	
	@Override
	public void onDisable() {
		for (GameSession session : sessions) {
			session.stop(true);
		}
	}


	public void onReload() {
		
	}
	
	public Set<GameSession> getSessions() {
		return sessions;
	}
	
	/**
	 * Searches for a game session with the given name, and returns it. If none is found, null is returned
	 * instead.
	 * @param name
	 * @return
	 */
	public GameSession getSession(String name) {
		if (sessions.isEmpty()) {
			return null;
		}
		
		for (GameSession session : sessions) {
			if (session.getName().equals(name)) {
				return session;
			}
		}
		
		return null;
	}
	
	/**
	 * Attempts to find a session the provided player is in.<br />
	 * Since there's nothing stopping a player form being in multiple sessions, this method returns the
	 * <i>first</i> session it finds that contains the given player. Order of traversal is undefined.
	 * @param player
	 * @return
	 */
	public GameSession getSession(WWPlayer player) {
		return getSession(player.getPlayer());
	}

	/**
	 * Attempts to find a session the provided player is in.<br />
	 * Since there's nothing stopping a player form being in multiple sessions, this method returns the
	 * <i>first</i> session it finds that contains the given player. Order of traversal is undefined.
	 * @param player
	 * @return
	 */
	public GameSession getSession(OfflinePlayer player) {
		for (GameSession session : sessions) {
			if (session.getPlayer(player) != null) {
				return session;
			}
		}
		
		return null;
	}
}

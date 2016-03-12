package nmt.minecraft.WorkersAndWarriors;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

import nmt.minecraft.WorkersAndWarriors.Session.GameSession;

/**
 * Main plugin class. Creates top-level infrastructure, excluding game-session-specific componenets.
 * @author Skyler
 *
 */
public class WorkersAndWarriorsPlugin extends JavaPlugin {

	public static WorkersAndWarriorsPlugin plugin;
	
	private Set<GameSession> sessions;
	

	
	
	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onEnable() {
		WorkersAndWarriorsPlugin.plugin = this;
		
		this.sessions = new HashSet<GameSession>();
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
}

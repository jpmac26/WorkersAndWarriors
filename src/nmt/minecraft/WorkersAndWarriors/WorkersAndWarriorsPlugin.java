package nmt.minecraft.WorkersAndWarriors;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class. Creates top-level infrastructure, excluding game-session-specific componenets.
 * @author Skyler
 *
 */
public class WorkersAndWarriorsPlugin extends JavaPlugin {

	public static WorkersAndWarriorsPlugin plugin;
	
	
	
	
	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onEnable() {
		WorkersAndWarriorsPlugin.plugin = this;
	}
	
}

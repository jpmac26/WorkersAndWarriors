package nmt.minecraft.WorkersAndWarriors.Team.WWPlayer;

import static nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin.plugin;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Player wrapper class.
 * <p>
 * This class wraps player information and holds WW game information about that player.<br />
 * This includes:
 * <ul>
 * <li>Whether the player has a flag</li>
 * <li>Whether the player is currently spawned, and if they're invincible</li>
 * <li><b>The type of player</b> the player is</li>
 * </ul>
 * </p>
 * @author Skyler
 *
 */
public class WWPlayer {
	
	public enum Type {
		WORKER,
		WARRIOR
	}
	
	private OfflinePlayer player;
	
	private Type type;
	
	private boolean hasFlag;
	
	private Location pregameLocation;
	
	public WWPlayer(OfflinePlayer player, Type type) {
		this.player = player;
		this.hasFlag = false;
		this.type = type;
		if (player.isOnline()) {
			pregameLocation = ((Player) player).getLocation();
		}
	}
	
	public void setPregameLocation(Location l) {
		this.pregameLocation = l;
	}
	
	public Location getPregameLocation() {
		return pregameLocation;
	}
	
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	public boolean hasFlag() {
		return hasFlag;
	}
	
	public void setFlag(boolean flag) {
		this.hasFlag = flag;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	/**
	 * Pretty-spawns a player at a location.<br />
	 * Sets gamemode, sets proper effects, etc
	 * @param spawnLocation
	 */
	public void spawn(Location spawnLocation) {
		// Check if player is still online
		if (!this.getPlayer().isOnline()) {
			// Player is not online
			System.out.println("Error! Attempted to spawn Offline player: " + this.getPlayer().getName());
			return;
		}
		Player p = this.getPlayer().getPlayer();
		p.teleport(spawnLocation);
		p.setGameMode(GameMode.SURVIVAL);
	}
	
	/**
	 * "<i>kills</i>" a player, queueing them for respawn.<br />
	 * Handles gamemode, potion effects, etc
	 */
	public void die() {
		
	}
	
	/**
	 * Gives the player blocks to place.<br />
	 * Does not check whether this player is a builder (and therefore would be able to place it) or not.
	 * @param count
	 */
	public void giveBlock(int count) {
            this.getPlayer().getPlayer().getInventory().addItem(plugin.getSession(this).getTeam(this).getBlockType().toItemStack(count));
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof WWPlayer) {
			WWPlayer op = (WWPlayer) o;
			return op.player.getUniqueId().equals(player.getUniqueId());
		}
		
		return false;
	}
	
}

package nmt.minecraft.WorkersAndWarriors.Team.WWPlayer;

import org.bukkit.OfflinePlayer;

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
	
	private OfflinePlayer player;
	
	private boolean hasFlag;
	
	public WWPlayer(OfflinePlayer player) {
		this.player = player;
		this.hasFlag = false;
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
	
}

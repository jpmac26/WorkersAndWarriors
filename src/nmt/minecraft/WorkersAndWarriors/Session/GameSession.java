package nmt.minecraft.WorkersAndWarriors.Session;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * An individual game session. This is the starting block of a running game.<br />
 * The session should hold <i>all</i> of the information it needs to run. It's important attention is paid
 * to encapsulation to allow for multiple sessions to run concurrently.
 * @author Skyler
 *
 */
public class GameSession {
	
	public enum State {
		STOPPED,
		OPEN,
		RUNNING,
		ENDED;
	}
	
	private State state;
	
	private Set<Team> teams;
	
	/**
	 * Create a new game session in the default stopped state.
	 */
	public GameSession() {
		this.state = State.STOPPED;
		teams = new HashSet<Team>();
	}
	
	/**
	 * Attempts to start the game.
	 * @return Whether or not the game was started as a result of this command (<b>false</b> if already 
	 * running, not '<i>open</i>, etc)
	 */
	public boolean start() {
		
		if (state != State.OPEN) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Stops (forcefully?) the game session. Deals with players, game blocks, etc
	 * @param force Force the game to stop (if it's not running, for example)
	 * @return Whether the game stopped as a result of this method
	 */
	public boolean stop(boolean force) {
		
		
		return false;
	}
	
	/**
	 * Attempts to get the WWPlayer matching the passed offline player. This is effectively a check to see
	 * whether the passed player is part of this session. If they are, their WWPlayer will be returned.
	 * Otherwise, this method returns <i>null</i>
	 * @param player
	 * @return the corresponding WWPlayer, or <i>null</i> if that player isn't in this session
	 */
	public WWPlayer getPlayer(OfflinePlayer player) {
		if (teams.isEmpty()) {
			return null;
		}
		
		WWPlayer cache = null;
		for (Team team : teams) {
			cache = team.getPlayer(player);
			if (cache != null) {
				return cache;
			}
		}
		
		return null;
	}
	
	
}

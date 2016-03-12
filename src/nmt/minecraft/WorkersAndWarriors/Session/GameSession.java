package nmt.minecraft.WorkersAndWarriors.Session;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.IO.ChatFormat;
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
	
	private String name;
	
	/**
	 * A list of players who have joined the session, but who are not on a team
	 */
	private List<WWPlayer> unsortedPlayers;
	
	private Set<Team> teams;
	
	/**
	 * Create a new game session in the default stopped state.
	 */
	public GameSession() {
		this.state = State.STOPPED;
		teams = new HashSet<Team>();
		unsortedPlayers = new LinkedList<WWPlayer>();
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
		
		if (!unsortedPlayers.isEmpty()) {
			for (WWPlayer p : unsortedPlayers) {
				if (p.getPlayer().getUniqueId().equals(player.getUniqueId())) {
					return p;
				}
			}
		}
		
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
	
	/**
	 * Returns the current list of players who have not been put on a team.
	 * @return
	 */
	public List<WWPlayer> getUnsortedPlayers() {
		return unsortedPlayers;
	}
	
	/**
	 * REturns the set of teams.
	 * @return
	 */
	public Set<Team> getTeams() {
		return teams;
	}
	
	/**
	 * Looks up a team from a given team name, returning it if it's in this session.
	 * @param teamName
	 * @return null if the team list is empty, the name is null, or team doensn't exist in this session. 
	 * Otherwise, returns the team.
	 */
	public Team getTeam(String teamName) {
		if (teams.isEmpty() || teamName == null) {
			return null;
		}
		
		for (Team team : teams) {
			if (team.getTeamName().equals(teamName)) {
				return team;
			}
		}
		
		return null;
	}
	
	/**
	 * Tries to get a team to match a specific player. That is, returns the team a given player is in, if
	 * such a team exists in this session.
	 * @param player 
	 * @return The team the player is on, if they are in this session
	 */
	public Team getTeam(WWPlayer player) {
		return getTeam(player.getPlayer());
	}
	
	/**
	 * Tries to get a team to match a specific player. That is, returns the team a given player is in, if
	 * such a team exists in this session.
	 * @param player 
	 * @return The team the player is on, if they are in this session
	 */
	public Team getTeam(OfflinePlayer player) {
		if (teams.isEmpty()) {
			return null;
		}
		
		for (Team team : teams) {
			if (team.getPlayer(player) != null) {
				return team;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds a player to this session with no team.<br />
	 * Creates a WWPlayer for the player and returns it as well.
	 * <p>
	 * If the player <b>already is in</b> this sesssion, returns null and does nothing.
	 * </p>
	 * @param player The player who would like to join the session
	 * @return A new WWPlayer object for the player, or null if the player already is in (or is null)
	 */
	public WWPlayer addPlayer(OfflinePlayer player) {
		if (player == null) {
			return null;
		}
		
		if (getPlayer(player) != null) {
			return null;
		}
		
		WWPlayer.Type[] types = WWPlayer.Type.values();
		WWPlayer newPlayer = new WWPlayer(player, 
				types[
				      WorkersAndWarriorsPlugin.random.nextInt(types.length)
				      ]
				);
		
		//TODO do cool stuff, like messages and tell them how to join a team, set class?
		if (player.isOnline())  {
			player.getPlayer().sendMessage(ChatFormat.SUCCESS.wrap(
					"You've joined the game session ") + ChatFormat.SESSION.wrap(name));
		}
		
		return newPlayer;
	}
	
}

package nmt.minecraft.WorkersAndWarriors.Team;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.util.Vector;

import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * Holds the players belonging to a team and provides team functions, like chat to a team
 * @author Skyler
 *
 */
public class Team {
	
	private List<WWPlayer> players;
	
	private FlagArea flagArea;
	
	/**
	 * Creates a team, making a flag area from the passed points.
	 * @param flagAreaPoint1
	 * @param flagAreaPoint2
	 */
	public Team(Vector flagAreaPoint1, Vector flagAreaPoint2) {
		this();
		this.flagArea = new FlagArea(flagAreaPoint1, flagAreaPoint2);
	}
	
	/**
	 * Creates a team with no defined flag area.
	 */
	public Team() {
		players = new LinkedList<WWPlayer>();
		this.flagArea = null;
	}
	
	/**
	 * Attempts to add a player to the team. Doesn't add them twice if they're already in it.
	 * @param player
	 * @return Whether the player was added, or not (if they're already in it, or null, etc)
	 */
	public boolean addPlayer(WWPlayer player) {
		if (player != null && !players.contains(player)) {
			players.add(player);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets the flag area to the provided points.<br />
	 * If a flag area was previously defined, it's discarded and overwritten with the new area.<br />
	 * If any of the parameters are null, no action is performed.
	 * @param point1
	 * @param point2
	 */
	public void setFlagArea(Vector point1, Vector point2) {
		if (point1 == null || point2 == null) {
			return;
		}
		
		flagArea = new FlagArea(point1, point2);
	}
	
	/**
	 * Returns the {@link FlagArea} associated with this team, or null if none is.
	 * @return
	 */
	public FlagArea getFlagArea() {
		return flagArea;
	}
	
	/**
	 * Returns the list of players currently in the team.
	 * @return
	 */
	public List<WWPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Checks whether a team has a player on it, and returns the respective WWPlayer is so
	 * @param player the Offline player to check for
	 * @return The WW player if it is in the team, null otherwise
	 */
	public WWPlayer getPlayer(OfflinePlayer player) {
		for (WWPlayer p : players) {
			if (p.getPlayer().getUniqueId().equals(player.getUniqueId())) {
				return p;
			}
		}
		
		return null;
	}
	
}

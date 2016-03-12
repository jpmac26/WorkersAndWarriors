package nmt.minecraft.WorkersAndWarriors.Team;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.OfflinePlayer;

import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * Holds the players belonging to a team and provides team functions, like chat to a team
 * @author Skyler
 *
 */
public class Team {
	
	private List<WWPlayer> players;
	
	public Team() {
		players = new LinkedList<WWPlayer>();
	}
	
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

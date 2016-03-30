package nmt.minecraft.WorkersAndWarriors.Team;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.PluginConfiguration;
import nmt.minecraft.WorkersAndWarriors.IO.ChatFormat;
import nmt.minecraft.WorkersAndWarriors.Session.BlockListener;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * Holds the players belonging to a team and provides team functions, like chat to a team
 * @author Skyler
 *
 */
public class Team {
	
	private GameSession session;
	
	private List<WWPlayer> players;
	
	private List<Location> spawnPoints;
	
	private FlagArea flagArea;
	
	private ChatColor teamColor;
	
	private String teamName;
	
	private MaterialData blockType;
	
	private MaterialData goalType;
	
	private int points;
	
	/**
	 * Creates a team, making a flag area from the passed points.
	 * @param flagAreaPoint1
	 * @param flagAreaPoint2
	 */
	public Team(GameSession session, Vector flagAreaPoint1, Vector flagAreaPoint2) {
		this(session);
		this.flagArea = new FlagArea(flagAreaPoint1, flagAreaPoint2);
	}
	
	public Team(GameSession session, String name) {
		this(session);
		this.teamName = name;
	}
	
	public Team(GameSession session, String name, ChatColor color) {
		this(session, name);
		this.teamColor = color;
	}
	
	public Team(GameSession session, String name, ChatColor color, Vector flagArea1, Vector flagArea2) {
		this(session, name, color);
		this.flagArea = new FlagArea(flagArea1, flagArea2);
	}
	
	public Team(GameSession session, String name, ChatColor color, Vector flagArea1, Vector flagArea2, MaterialData blockType,
			MaterialData goalType) {
		this(session, name, color, flagArea1, flagArea2);
		this.goalType = goalType;
		this.blockType = blockType;
	}
	
	/**
	 * Creates a team with no defined flag area, no defined teamColor, and no Name!
	 */
	public Team(GameSession session) {
		players = new LinkedList<WWPlayer>();
		this.flagArea = null;
		this.spawnPoints = new LinkedList<Location>();
		this.session = session;
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
	 * Tries to remove a player from the team, cleaning them up and causing any handling of team-removal that
	 * needs to (like making players no longer see thtem as a team member).
	 * @param player
	 * @return whether the palyer was on the team and has been removed
	 */
	public boolean removePlayer(WWPlayer player) {
		if (player == null || !players.contains(player)) {
			return false;
		}
		
		players.remove(player);
		sendMessage(ChatFormat.TEAM.wrap(player.getPlayer().getName() + " has left the team."));
		
		//TODO cool stuff?
		
		return true;
	}
	
	/**
	 * Sets the flag area to the provided points.<br />
	 * If a flag area was previously defined, it's discarded and overwritten with the new area.<br />
	 * If any of the parameters are null, no action is performed.
	 * @param point1
	 * @param point2
	 */
	@Deprecated
	public void setFlagArea(Vector point1, Vector point2) {
		if (point1 == null || point2 == null) {
			return;
		}
		
		flagArea = new FlagArea(point1, point2);
	}
	
	public void setFlagArea(Vector center, int radius) {
		if (center == null) {
			return;
		}
		
		flagArea = new FlagArea(center, radius);
	}
	
	/**
	 * Returns the {@link FlagArea} associated with this team, or null if none is.
	 * @return
	 */
	public FlagArea getFlagArea() {
		return flagArea;
	}
	
	/**
	 * Adds a spawn point to this team's available spawn point.
	 * @param spawnPoint
	 */
	public void addSpawnPoint(Location spawnPoint) {
		this.spawnPoints.add(spawnPoint);
	}
	
	/**
	 * Returns a random spawn point from this team's spawn points. Convenience :D
	 * @return
	 */
	public Location getRandomSpawn() {
		if (spawnPoints.isEmpty()) {
			return null;
		}
		
		return spawnPoints.get(
				WorkersAndWarriorsPlugin.random.nextInt(spawnPoints.size())
				);
	}
	
	/**
	 * Returns THE LIST of spawn points.
	 * @return
	 */
	public List<Location> getSpawnPoints() {
		return this.spawnPoints;
	}
	
	public void clearSpawnPoints() {
		spawnPoints.clear();
	}
	
	/**
	 * Sets the team name to the provided string.<br />
	 * If name is null, does nothing. Doesn't reset name.
	 * @param name
	 */
	public void setTeamName(String name) {
		if (name == null) {
			return;
		}
		
		this.teamName = name;
	}
	
	/**
	 * Returns the team's name
	 * @return
	 */
	public String getTeamName() {
		return this.teamName;
	}
	
	/**
	 * If color is non-null, sets the team color. If null, does nothing.
	 * @param color
	 */
	public void setTeamColor(ChatColor color) {
		if (color == null) {
			return;
		}
		
		this.teamColor = color;
	}
	
	/**
	 * Returns the team's color
	 * @return
	 */
	public ChatColor getTeamColor() {
		return teamColor;
	}
	
	public MaterialData getBlockType() {
		return blockType;
	}

	public void setBlockType(MaterialData blockType) {
		this.blockType = blockType;
	}

	public MaterialData getGoalType() {
		return goalType;
	}

	public void setGoalType(MaterialData goalType) {
		this.goalType = goalType;
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
	
	/**
	 * Sends a message to the whole team.
	 * @param message
	 */
	public void sendMessage(String message) {
		if (message == null) {
			return;
		}
		
		if (players.isEmpty()) {
			return;
		}
		
		for (WWPlayer player : players) {
			if (!player.getPlayer().isOnline()) {
				continue;
			}
			
			player.getPlayer().getPlayer().sendMessage(message);
		}
	}
	
	/**
	 * Prepares and spawns all players within this team at a random spawn point.<br />
	 * Takes care of first-spawn stuff, like distributing blocks
	 */
	public void spawnTeam(int teamBlockCount) {
		int quot, rem, bteamsize = 0;
		
		for (WWPlayer player : players) {
			if (player.getType() == WWPlayer.Type.WORKER)
				bteamsize++;
		}
		
		if (bteamsize == 0) {
			WorkersAndWarriorsPlugin.plugin.getLogger().info(
					ChatFormat.WARNING.wrap("There are no builders on team " + teamName)
					);
			quot = 0;
			rem = 0;
		} else {
			quot = teamBlockCount / bteamsize;
			rem = teamBlockCount % bteamsize;
		}
		
		for (WWPlayer player : players) {
			player.spawn(getRandomSpawn());
			
			if (player.getType() == WWPlayer.Type.WORKER) {
				player.giveBlock(quot);
				
				//give out the extra blocks too
				if (rem > 0) {
					player.giveBlock(1);
					rem--;
				}
			}
		}
	}
	
	/**
	 * Performs a personalized check to make sure that this team object would be ready to open if the
	 * game is opened.
	 * @return
	 * @see {@link nmt.minecraft.WorkersAndWarriors.Session.GameSession#start() GameSession.oepn()}
	 */
	public boolean isReady() {
		/*
		 * Check we have a team, name, block type, goal type, goal pos, color...
		 */
		
		if (flagArea == null || teamColor == null || teamName == null || blockType == null
				|| goalType == null || spawnPoints.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adds an amount of points to a team.<br />
	 * If the point total after addition is equal to or greater than the defined point goal,
	 * the game will be won.
	 * @param count
	 */
	public void addPoints(int count) {
		points += count;
		
		if (points >= PluginConfiguration.config.getPointsToWin()) {
			session.win(this);
			
		}
		
		// Update score
		// Since we are returned a set of scores, assume the first
		Score s = this.session.getScoreboard().getScores(this.teamName).iterator().next();
		s.setScore(points);
		
		// Alert players
		this.sendMessage("Your team scored!");;
		for (WWPlayer p : this.getPlayers()) {
			Player tmp = ((Player) p.getPlayer());
			tmp.playSound(tmp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
			tmp.playSound(tmp.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 8, 1);
			tmp.playSound(tmp.getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 8, 1);
		}
	}
	
	/**
	 * Sets the flag block to be the flag block. In other words, changes the block area from whatever it is
	 * to the block type. This is great for returning the flag to it's original spot.<br />
	 * <b>Be Careful</b> not to call this when the flag is still in game, or we will have <b>duplicates</b>
	 */
	public void resetFlagBlock() {
		if (flagArea == null || spawnPoints.isEmpty()) {
			return;
		}
		
		Vector center = flagArea.getCenter();
		Location flagLoc = new Location(spawnPoints.get(0).getWorld(),
				center.getBlockX(),
				center.getBlockY(),
				center.getBlockZ());
		BlockListener.setBlockType(flagLoc.getBlock(), goalType);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Team)
		if (((Team) o).session.equals(session))
		if (((Team) o).getTeamName().equals(teamName)) {
			return true;
		}
		
		return false;
	}
}

package nmt.minecraft.WorkersAndWarriors.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.PluginConfiguration;
import nmt.minecraft.WorkersAndWarriors.IO.ChatFormat;
import nmt.minecraft.WorkersAndWarriors.Scheduling.GameFinishAnimationEndEvent;
import nmt.minecraft.WorkersAndWarriors.Scheduling.Scheduler;
import nmt.minecraft.WorkersAndWarriors.Scheduling.Tickable;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession.Reminders;
import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * An individual game session. This is the starting block of a running game.<br />
 * The session should hold <i>all</i> of the information it needs to run. It's important attention is paid
 * to encapsulation to allow for multiple sessions to run concurrently.
 * @author Skyler
 *
 */
public class GameSession implements Listener, Tickable<Reminders>{
	
	public enum State {
		STOPPED,
		OPEN,
		STARTING,
		RUNNING,
		ENDED;
	}
	
	public enum Reminders {
		FIVESECONDS,
		THREESECONDS,
		TWOSECONDS,
		ONESECOND,
		SPAWNPLAYERS;
	}
	
	private State state;
	
	private String name;
	
	/**
	 * A list of players who have joined the session, but who are not on a team
	 */
	private List<WWPlayer> unsortedPlayers;
	
	private Set<Team> teams;
	
	private int maxTeams;
	
	private int protectionSize;
	
	private int maxTeamBlock;
	
	/**
	 * Lobby location for all players in this session.
	 */
	private Location sessionLobby;
	
	private BlockListener bListener;
	private PlayerListener dListener;
	
	private Scoreboard sBoard;
	private Objective sideBar;
	
	private List<MaterialData> goalTypes;
	
	public static final double startCountdown = 10.0;
	
	/**
	 * Create a new game session in the default stopped state and with the given name.<br />
	 * <b>PLEASE NOTE</b>: The given name should be unique (see {@link #equals(Object)})
	 */
	public GameSession(String name) {
		this.state = State.STOPPED;
		this.name = name;
		teams = new HashSet<Team>();
		unsortedPlayers = new LinkedList<WWPlayer>();
		this.sessionLobby = null;
		
		// Setup score board
		this.sBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		sideBar = this.sBoard.registerNewObjective("Goals", "dummy");
		this.sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
		
	}
	
	/**
	 * Opens up a game, allowing players to join
	 * @return Whether or not the game was opened as a result of this method. False returns usually come from
	 * a game session that's not in the right state (namely STOPPED)!
	 */
	public boolean open() {
		
		if (state != State.STOPPED) {
			return false;
		}
		
		state = State.OPEN;
		
		this.goalTypes = new ArrayList<>(getTeams().size());
		for (Team t : getTeams()) {
						
			goalTypes.add(t.getGoalType());
		}

		this.dListener = new PlayerListener(this);
		
		if (PluginConfiguration.config.getBroadcastOpen()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (WorkersAndWarriorsPlugin.plugin.getSession(p) == null) {
					p.sendMessage(ChatFormat.SUCCESS.wrap("The session ") + ChatFormat.SESSION.wrap(name)
							+ ChatFormat.SUCCESS.wrap(" has opened!"));
					p.sendMessage(ChatFormat.INFO.wrap("use    /wwp join ") + ChatFormat.SESSION.wrap(name)
							+ ChatFormat.INFO.wrap("    to join"));
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Attempts to start the game.
	 * @return Whether or not the game was started as a result of this command (<b>false</b> if already 
	 * running, not '<i>open</i>, etc)
	 */
	public boolean start() {
		
		if (getState() != State.OPEN) {
			return false;
		}
		
		if (!canStart()) {
			return false;
		}
		
		state = State.STARTING;
		
		this.bListener = new BlockListener(this);
		
		// Team Balance
		this.distributePlayers();
		
		// Assign scorebaord
		for (Team t : teams) {
			this.sideBar.getScore(t.getTeamName()).setScore(0);
			for (WWPlayer p : t.getPlayers()) {
				((Player) p.getPlayer()).setScoreboard(sBoard);
			}
		}
		
		//start timer
		Scheduler.getScheduler().schedule(this, Reminders.FIVESECONDS, startCountdown - 5);
		
		int workers, warriors;
		for (Team t : teams) {
			t.sendMessage(ChatFormat.SESSION.wrap("Game starting in " + startCountdown + " seconds!"));
			workers = warriors = 0;
			
			for (WWPlayer player : t.getPlayers()) {
				if (player.getType() == WWPlayer.Type.WARRIOR) {
					warriors++;
				} else if (player.getType() == WWPlayer.Type.WORKER) {
					workers++;
				}
				
			}
			
			t.sendMessage(ChatFormat.INFO + "Your team has " + ChatFormat.IMPORTANT + workers 
					+ ChatFormat.INFO.wrap(" workers"));
			
			t.sendMessage(ChatFormat.INFO + "Your team has " + ChatFormat.IMPORTANT + warriors 
					+ ChatFormat.INFO.wrap(" warriors"));
			
		}
		
		return true;
	}
	
	/**
	 * Moves players and automatically balances teams.
	 * This method performs the balancing and returns nothing.
	 */
	private void distributePlayers() {
		//first figure out how many players each team should have. Then add unsorted
		//to below that. Then take max, apply to min?
		
		//where do we stop? It won't be exactly even.
		//maybe get number for each, re-add each player and take those that are over and put in pool.
		
		//two pass; First pass, chop extra
		//second pass, fill
		
		int cap = (getAllPlayers().size() / teams.size()); //rounds down
		int extra = (getAllPlayers().size() % teams.size());
		
		ListIterator<WWPlayer> it;
		WWPlayer cache;
		int localCap;

		/*
		 * Look at teams. Unblananced (diff > 1)?
		 * Find teams with diff > 1, chop off extra
		 */
		
		for (Team t : teams) {
			//check if size - cap > 1
			if (t.getPlayers().size() > (extra > 0 ? cap + 1 : cap)) {
				localCap = cap + (extra-- > 0 ? 1 : 0);
				//unbalanced. Chop off extra
				it = t.getPlayers().listIterator(localCap);
				while (it.hasNext()) {
					cache = it.next();
					t.removePlayer(cache);
					unsortedPlayers.add(cache);
					
					if (cache.getPlayer().isOnline()) {
						cache.getPlayer().getPlayer().sendMessage(ChatFormat.WARNING.wrap("You have been removed "
								+ "from your team for balancing."));
					}
				}
			}
		}
		
//		for (Team t : teams) {
//			localCap = cap + (extra-- > 0 ? 1 : 0);
//			if (t.getPlayers().size() > localCap) {
//				it = t.getPlayers().listIterator();
//				for (int i = 0; i < localCap; i++) {
//					it.next();
//				}
//				
//				cache = it.next();
//				t.removePlayer(cache);
//				this.unsortedPlayers.add(cache);
//			}
//		}

		if (unsortedPlayers.isEmpty()) {
			return;
		}
		
		//now, distribute displaced
		it = unsortedPlayers.listIterator();
		while (it.hasNext()) {
			for (Team t : teams) {
				if (t.getPlayers().size() < cap) {
					cache = it.next();
					t.addPlayer(cache);
					it.remove();
					
					if (cache.getPlayer().isOnline()) {
						cache.getPlayer().getPlayer().sendMessage(ChatFormat.WARNING + "You have been moved "
								+ "to Team " + t.getTeamColor() + t.getTeamName() + ChatColor.RESET);
					}
				}
			}
		}
		

//		if (unsortedPlayers.isEmpty()) {
//			return;
//		}
		
//		//finally, distribute extras (remainder)
//		if (!unsortedPlayers.isEmpty()) {
//			//sanity check
//			if (unsortedPlayers.size() >= teams.size()) {
//				WorkersAndWarriorsPlugin.plugin.getLogger().warning("Invalid size in distribution!!!");
//				WorkersAndWarriorsPlugin.plugin.getLogger().warning("unsorted held " + unsortedPlayers.size()
//						+ " players modulus!");
//				return;
//			}
//			
//			it = unsortedPlayers.listIterator();
//			for (Team t : teams) {
//				if(!it.hasNext()) {
//					break;
//				}
//				t.addPlayer(it.next());
//				it.remove();
//			}
//			
//		}
		
	}
	
	/**
	 * Stops (forcefully?) the game session. Deals with players, game blocks, etc
	 * @param force Force the game to stop (if it's not running, for example)
	 * @return Whether the game stopped as a result of this method
	 */
	public boolean stop(boolean force) {
		
		if ((state == State.STARTING || state == State.RUNNING) && force) {
			HandlerList.unregisterAll(bListener);
			HandlerList.unregisterAll(dListener);
			bListener = null;
			dListener = null;

			state = State.ENDED;
			for (WWPlayer p : getAllPlayers()) {
				removePlayer(p.getPlayer(), true);
			}
			
			return true;
		}
		
		if (state == State.OPEN) {
			state = State.ENDED;
			for (WWPlayer p : getAllPlayers()) {
				removePlayer(p.getPlayer(), true);
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * Performs a win for the given team.<br />
	 * This stops the game and refreshes all the players back to non-game status
	 * @param t The team that won
	 * @return true if everything went well, false on error
	 */
	public boolean win(Team t) {
		//TODO
		for (Team team : teams) {
			if (team.getTeamName().equals(t.getTeamName())) {
				printWin(team);
			} else {
				printLose(team);
			}
			// Set everyone to spectator
			for (WWPlayer wp : team.getPlayers()) {
				((Player) wp.getPlayer()).setGameMode(GameMode.SPECTATOR);
			}
		}
		
		Bukkit.getPluginManager().registerEvents(this, WorkersAndWarriorsPlugin.plugin);
		
		// Start Decay
		bListener.startDecay(false);
		return true;
	}
	
	private void printWin(Team t) {
		t.sendMessage(ChatFormat.SUCCESS.wrap("Congratulations! You won!"));
	}
	
	private void printLose(Team t) {
		t.sendMessage(ChatFormat.WARNING.wrap("Conflatulations! You lose!"));		
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLobbyLocation(Location loc) {
		this.sessionLobby = loc;
	}
	
	public Location getLobbyLocation() {
		return sessionLobby;
	}
	
	/**
	 * Returns a pre-generated list (generated on open) of goal material datas, so teams don't
	 * have to be queried every time.
	 * @return
	 */
	public List<MaterialData> getGoalTypes() {
		return goalTypes;
	}
	
	public int getMaxTeams() {
		return maxTeams;
	}

	public void setMaxTeams(int maxTeams) {
		this.maxTeams = maxTeams;
	}

	public int getProtectionSize() {
		return protectionSize;
	}

	public void setProtectionSize(int protectionSize) {
		this.protectionSize = protectionSize;
	}

	public int getMaxTeamBlock() {
		return maxTeamBlock;
	}

	public void setMaxTeamBlock(int maxTeamBlock) {
		this.maxTeamBlock = maxTeamBlock;
	}

	/**
	 * Adds the given team to the session.<br />
	 * Please note that at the time of this comment, generic teams aren't supported. Teams are staticly
	 * defined in {@link nmt.minecraft.WorkersAndWarriors.Config.PluginConfiguration PluginConfiguration}.
	 * @param team
	 */
	public void addTeam(Team team) {
		teams.add(team);
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
	 * Returns a list of all players that are part of this session, regardless of where they're at in
	 * the session (unsorted or on a team).
	 * @return
	 */
	public List<WWPlayer> getAllPlayers() {
		List<WWPlayer> players = new LinkedList<>(unsortedPlayers);
		
		if (!teams.isEmpty()) {
			for (Team t : teams) {
				if (!t.getPlayers().isEmpty()) {
					for (WWPlayer p : t.getPlayers()) {
						players.add(p);
					}
				}
			}
		}
		
		return players;
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
		
		if (state != State.OPEN) {
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
			player.getPlayer().sendMessage(ChatFormat.INFO + "You've been given the " + ChatFormat.CLASS 
						+ newPlayer.getType().name().toLowerCase() + ChatFormat.INFO.wrap(" class."));
			player.getPlayer().teleport(sessionLobby);
		}
		
		this.unsortedPlayers.add(newPlayer);
		
		return newPlayer;
	}
	
	public boolean removePlayer(WWPlayer player) {
		return removePlayer(player.getPlayer());
	}
	
	public boolean removePlayer(OfflinePlayer player) {
		return removePlayer(player, false);
	}
	
	public boolean removePlayer(OfflinePlayer player, boolean restore) {
		if (unsortedPlayers != null && !unsortedPlayers.isEmpty()) {
			Iterator<WWPlayer> it = unsortedPlayers.iterator();
			WWPlayer cache;
			while (it.hasNext()) {
				cache = it.next();
				if (cache.getPlayer().getUniqueId().equals(player.getUniqueId())) {
					it.remove();
					
					System.out.println("Called!");
					if (player.isOnline()) {
						((Player) player).getInventory().clear();
						((Player) player).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
						((Player) player).setGameMode(GameMode.SURVIVAL);
					}
					
					if (restore && player.isOnline()) {
						if (cache.getPregameLocation() != null) {
							((Player) player).teleport(cache.getPregameLocation());
						}
						if (cache.getPregameItems() != null) {
							((Player) player).getInventory().setContents(cache.getPregameItems());
						}
					}
					
					if (state == State.RUNNING)
						checkState();
					return true;
				}
			}
		}
		
		WWPlayer p = getPlayer(player);
		if (p == null) {
			return false;
		}
		
		//they're on a team somewhere!
		for (Team t : teams) {
			if (t.removePlayer(p)) {
				
				if (player.isOnline()) {
					((Player) player).getInventory().clear();
					((Player) player).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					((Player) player).setGameMode(GameMode.SURVIVAL);
				}
				
				if (restore && player.isOnline()) {
					if (p.getPregameLocation() != null) {
						((Player) player).teleport(p.getPregameLocation());
					}
					if (p.getPregameItems() != null) {
						((Player) player).getInventory().setContents(p.getPregameItems());
					}
				}
				
				if (state == State.RUNNING)
					checkState();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Private method used to make sure that the game is okay to keep running.<br />
	 * This is particularly useful when the game state has changed in a way that would make it wise to stop
	 * the game. An easy example is a player just disconnected. Should we keep going? Or do we need to stop
	 * and get some winners?
	 */
	private void checkState() {
		//if we're closing down, don't bother
		if (state == State.ENDED) {
			return;
		}
		
		
		//if only one player is left, make their team win.
		if (getAllPlayers().size() == 1) {
			win(getTeam(getAllPlayers().iterator().next()));
			return;
		}
		
		//else if only one team is left, make that team win.
		boolean multi = false;
		Team wTeam = null;
		for (Team t : teams) {
			if (!t.getPlayers().isEmpty()) {
				if (!multi) {
					multi = true;
					wTeam = t;
					continue;
				} else {
					//multi already was true, so more than one.
					return; //we can keep playing
				}
			}
		}
		
		//if we get here, we only had one team. Or no teams.
		if (wTeam == null) {
			WorkersAndWarriorsPlugin.plugin.getLogger().warning("Ended game but found zero teams!");
			return;
		}
		
		win(wTeam); //else one team, so make them win
	}
	
	public State getState() {
		return this.state;
	}
	
	/**
	 * Checks whether this session is ready to be <i>opened</i>.
	 * @return
	 */
	public boolean canOpen() {
		/*
		 * Need to have some teams (at least 2..?)
		 * state should be stopped. sessionLobby needs to be set.
		 */
		
		if (state != State.STOPPED) {
			return false;
		}
		
		if (teams.isEmpty()) {
			return false;
		}
		
		for (Team team : teams) {
			if (!team.isReady()) {
				return false;
			}
		}
		
		if (sessionLobby == null) {
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Checks whether the provided function is ready to be started
	 * @return
	 */
	public boolean canStart() {
		/*
		 * needs to be opened. so needs to be openable. Half of our checks done there.
		 * Needs to have protectionSize, maxTeamBlock set
		 *                /\____ No way to check. Defaults to 0 (cause it's an int)
		 * Needs to have some players somewhere! (at least 2 players)
		 */
		
		if (teams.isEmpty()) {
			return false;
		}
		
		for (Team team : teams) {
			if (!team.isReady()) {
				return false;
			}
		}
		
		if (sessionLobby == null) {
			return false;
		}
		
		if (this.getAllPlayers().size() < 2) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the {@link Scoreboard} for this session
	 * @return A {@link Scoreboard}
	 */
	public Scoreboard getScoreboard() {
		return this.sBoard;
	}
	
	@EventHandler
	public void onGameFinishAnimationEndEvent(GameFinishAnimationEndEvent e) {
		stop(true);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GameSession) {
			GameSession og = (GameSession) o;
			if (og.name.equals(name)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void alarm(Reminders reference) {
		
		if (reference == Reminders.SPAWNPLAYERS) {
			state = State.RUNNING;
			
			//Distribute Blocks
			for (Team t : teams) {
				t.spawnTeam(this.maxTeamBlock);
				t.resetFlagBlock();
				t.sendMessage(ChatFormat.SUCCESS.wrap("The game has started!"));
				
			}
			
			return;
		}
		
		if (reference == Reminders.FIVESECONDS) {
			for (Team t : teams) {
				t.sendMessage(ChatFormat.WARNING.wrap("Game starting in 5 seconds!"));
			}
			
			Scheduler.getScheduler().schedule(this, Reminders.THREESECONDS, 2);
			return;
		}
		
		if (reference == Reminders.THREESECONDS) {
			for (Team t : teams) {
				t.sendMessage(ChatFormat.ERROR.wrap("Game starting in 3 seconds!"));
			}
			
			Scheduler.getScheduler().schedule(this, Reminders.TWOSECONDS, 1);
			return;
		}
		
		if (reference == Reminders.TWOSECONDS) {
			for (Team t : teams) {
				t.sendMessage(ChatFormat.ERROR.wrap("Game starting in  seconds!"));
			}
			
			Scheduler.getScheduler().schedule(this, Reminders.ONESECOND, 1);
			return;
		}
		
		if (reference == Reminders.ONESECOND) {
			for (Team t : teams) {
				t.sendMessage(ChatFormat.ERROR.wrap("Game starting in  seconds!"));
			}
			
			Scheduler.getScheduler().schedule(this, Reminders.SPAWNPLAYERS, 1);
			return;
		}
	}
	
}

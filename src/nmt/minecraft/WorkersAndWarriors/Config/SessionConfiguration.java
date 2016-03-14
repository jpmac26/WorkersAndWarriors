package nmt.minecraft.WorkersAndWarriors.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.TeamConfiguration.Key;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;
import nmt.minecraft.WorkersAndWarriors.Team.Team;

/**
 * Holds configuration for a specific session.<br />
 * This class is meant to be used as a static helper class which helps load information from
 * a session config into an active session. It is not intended to store configuration information about a 
 * session like {@link PluginConfiguration}'s do.
 * @author Skyler
 *
 */
public abstract class SessionConfiguration {
	
	public static class TeamConfiguration implements ConfigurationSerializable {
		
		private static enum Key {
			SPAWNPOINTS("spawnpoints", new ArrayList<Location>(1)),
			GOALPOINT1("goal.point1", new Vector(0,0,0)),
			GOALPOINT2("goal.point2", new Vector(0,0,0));
			
			private String key;
			
			private Object def;
			
			private Key(String key, Object def) {
				this.key = key;
				this.def = def;
			}
			
			public String getKey() {
				return key;
			}
			
			public Object getDefault() {
				return def;
			}
		}
		/*
		 * spawnpoints: []
		 * goal:
		 * 	point1: 
		 * 		""
		 *  point2:
		 *  	""
		 */
		
		private List<Location> spawns;
		
		private Vector goalPoint1;
		
		private Vector goalPoint2;
		
		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<>();
			
			map.put(Key.SPAWNPOINTS.getKey(), spawns);
			map.put(Key.GOALPOINT1.getKey(), goalPoint1);
			map.put(Key.GOALPOINT2.getKey(), goalPoint2);
			
			return map;
		}
		
		@SuppressWarnings("unchecked")
		public static TeamConfiguration valueOf(Map<String, Object> map) {
			if (map == null) {
				return null;
			}
			
			//for ease of use
			Logger logger = WorkersAndWarriorsPlugin.plugin.getLogger(); 
			
			TeamConfiguration config = new TeamConfiguration();
			
			Object o;
			boolean trip = false;
			for (Key key : Key.values()) {
				if (!map.containsKey(key.getKey())) {
					if (!trip) {
						logger.info("Unable to find some keys in team configuration. Setting default values for:");
						trip = true;
					}
					logger.info(key.name() + " [" + key.getKey() + "]");
					map.put(key.getKey(), key.getDefault());
					continue;
				}
				
				o = map.get(key.getKey());
				if (!o.getClass().equals(key.getDefault().getClass())) {
					//classes don't match! Invalid key entry!
					logger.warning("Unable to load config value for key [" + ChatColor.BOLD + key.key + 
							ChatColor.RESET + "] because of a class mismatch. Default used instead." );
					logger.warning("  -> Class [" + o.getClass().getName() + "] <> [" + key.getDefault().getClass().getName() + "]");
					map.put(key.getKey(), key.getDefault());
					continue;
				}
			}
			
			//have map, now proceed
			config.goalPoint1 = (Vector) map.get(Key.GOALPOINT1.getKey());
			config.goalPoint2 = (Vector) map.get(Key.GOALPOINT2.getKey());
			config.spawns = (List<Location>) map.get(Key.SPAWNPOINTS.getKey());
			
			return config;
		}
	}
	
	private enum Key {
		SESSION_LOBBY("session.lobby"),
		TEAM("teams"),
		TEAMMAX("maxteams");
		
		private String key;
		
		private Key(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
		
		@Override
		public String toString() {
			return getKey();
		}
	}
	
	public static final String sessionDirName = "SessionTemplates";
	
	private static File sessionDir = null;
	
	/**
	 * Takes the provided file and attempts to create a {@link GameSession} from it.
	 * @param templateName The template file name to load
	 * @param sessionName a name to give the session
	 * @return A new game session on success, null of failure (including if sessionFile is null or doesn't
	 * exist)
	 * @throws InvalidConfigurationException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @see {@link GameSession}
	 */
	public static GameSession loadSesson(String templateName, String sessionName) throws FileNotFoundException, IOException, InvalidConfigurationException {
		if (templateName == null) {
			return null;
		}
		
		File sessionFile = new File(getTemplateDirectory(), templateName);
		
		YamlConfiguration config = new YamlConfiguration();
		config.load(sessionFile);
		
		if (!config.contains(Key.TEAMMAX.getKey())) {
			throw new InvalidConfigurationException("Undefined number of teams defined in session config");
		}
		
		if (!config.contains(Key.TEAM.getKey())) {
			throw new InvalidConfigurationException("Team definitions were not found");
		}
		
		@SuppressWarnings("unchecked")
		List<TeamConfiguration> teams = (List<TeamConfiguration>) config.getList(Key.TEAM.getKey());
		
		int teamMax = config.getInt(Key.TEAMMAX.getKey());
		int teamNum = teams.size();
		
		//check for size issues
		if (teamMax > teamNum) {
			throw new InvalidConfigurationException("Team maximum exceeds the number of defined teams");
		}
		
		GameSession session = new GameSession(sessionName);
		
		//Try and grab the session lobby location
		if (config.contains(Key.SESSION_LOBBY.getKey()))
		try {
			session.setLobbyLocation((Location) config.get(Key.SESSION_LOBBY.getKey()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		Team team1, team2;
		
		//Set up team teams
		team1 = loadTeam1(config);
		team2 = loadTeam2(config);
		
		session.addTeam(team1);
		session.addTeam(team2);
		
		return session;
		
	}
	
	/**
	 * Saves a {@link GameSession} out to file to be loaded later quicky.<br />
	 * Note, this saves a <i>template</i> of the session; it doesn't store information about
	 * the players that may be part of the session, or specifics about the state of the Session.
	 * @param templateName what to save the template. Will overwrite conflicts
	 * @param session
	 * @throws IOException 
	 */
	public static void saveSessionTemplate(String templateName, GameSession session) throws IOException {
		
		if (session == null || templateName == null) {
			return;
		}
		
		YamlConfiguration config = new YamlConfiguration();
		Team team1, team2;
		team1 = session.getTeam(PluginConfiguration.config.getTeam1Name());
		team2 = session.getTeam(PluginConfiguration.config.getTeam2Name());
		
		if (team1 == null || team2 == null) {
			WorkersAndWarriorsPlugin.plugin.getLogger().warning("Unable to find defined teams when trying "
					+ "to save session template. Aborting save!");
			return;
		}
		
		config.set(Key.SESSION_LOBBY.getKey(), session.getLobbyLocation());
		
		if (team1.getFlagArea() != null) {
			config.set(Key.TEAM1_GOAL_POINT1.getKey(), team1.getFlagArea().getMin());
			config.set(Key.TEAM1_GOAL_POINT2.getKey(), team1.getFlagArea().getMax());
		}
		
		if (team2.getFlagArea() != null) {
			config.set(Key.TEAM2_GOAL_POINT1.getKey(), team2.getFlagArea().getMin());
			config.set(Key.TEAM2_GOAL_POINT2.getKey(), team2.getFlagArea().getMax());
		}
		
		config.set(Key.TEAM1_SPAWNPOINTS.getKey(), team1.getSpawnPoints());
		config.set(Key.TEAM2_SPAWNPOINTS.getKey(), team2.getSpawnPoints());
		
		File saveFile = new File(getTemplateDirectory(), templateName);
		
		config.save(saveFile);
		
	}
	
	@SuppressWarnings("unchecked")
	private static Team loadTeam1(YamlConfiguration config) {
		Team team1;
		Vector l1, l2;
		if (config.contains(Key.TEAM1_GOAL_POINT1.getKey()) && config.contains(Key.TEAM1_GOAL_POINT2.getKey())) {
			try {
				l1 = (Vector) config.get(Key.TEAM1_GOAL_POINT1.getKey());
				l2 = (Vector) config.get(Key.TEAM1_GOAL_POINT2.getKey());
				team1 = new Team(PluginConfiguration.config.getTeam1Name(), 
						PluginConfiguration.config.getTeam1Color(),
						l1,
						l2
						);
			} catch (Exception e) {
				e.printStackTrace();
				team1 = new Team(PluginConfiguration.config.getTeam1Name(), PluginConfiguration.config.getTeam1Color());
			}
		} else {
			team1 = new Team(PluginConfiguration.config.getTeam1Name(), PluginConfiguration.config.getTeam1Color());
		}
		
		//Try and get a list of spawn locations for team 1
		if (config.contains(Key.TEAM1_SPAWNPOINTS.getKey())) {
			List<Location> locs = null;
			
			try {
				locs = (List<Location>) config.getList(Key.TEAM1_SPAWNPOINTS.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (locs != null && !locs.isEmpty()) {
				for (Location l : locs) {
					team1.addSpawnPoint(l);
				}
			}
		}
		
		return team1;
	}
	
	@SuppressWarnings("unchecked")
	private static Team loadTeam2(YamlConfiguration config) {
		Team team2;
		Vector l1, l2;
		if (config.contains(Key.TEAM2_GOAL_POINT1.getKey()) && config.contains(Key.TEAM2_GOAL_POINT2.getKey())) {
			try {
				l1 = (Vector) config.get(Key.TEAM2_GOAL_POINT1.getKey());
				l2 = (Vector) config.get(Key.TEAM2_GOAL_POINT2.getKey());
				team2 = new Team(PluginConfiguration.config.getTeam2Name(), 
						PluginConfiguration.config.getTeam2Color(),
						l1,
						l2
						);
			} catch (Exception e) {
				e.printStackTrace();
				team2 = new Team(PluginConfiguration.config.getTeam2Name(), PluginConfiguration.config.getTeam2Color());
			}
		} else {
			team2 = new Team(PluginConfiguration.config.getTeam2Name(), PluginConfiguration.config.getTeam2Color());
		}
		
		//Try and get a list of spawn locations for team 1
		if (config.contains(Key.TEAM2_SPAWNPOINTS.getKey())) {
			List<Location> locs = null;
			
			try {
				locs = (List<Location>) config.getList(Key.TEAM2_SPAWNPOINTS.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (locs != null && !locs.isEmpty()) {
				for (Location l : locs) {
					team2.addSpawnPoint(l);
				}
			}
		}
		
		return team2;
	}
	
	public static File getTemplateDirectory() {
		if (sessionDir == null) {
			sessionDir = new File(WorkersAndWarriorsPlugin.plugin.getDataFolder(),
					sessionDirName);
			if (!sessionDir.exists()) {
				sessionDir.mkdirs();
			}
		}
		
		return sessionDir;
	}
	
}

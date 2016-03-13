package nmt.minecraft.WorkersAndWarriors.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
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
	
	private enum Key {
		SESSION_LOBBY("session.lobby"),
		TEAM1_SPAWNPOINTS("team1.spawnpoints"),
		TEAM2_SPAWNPOINTS("team2.spawnpoints"),
		TEAM1_GOAL_POINT1("team1.goal.point1"),
		TEAM1_GOAL_POINT2("team1.goal.point2"),
		TEAM2_GOAL_POINT1("team2.goal.point1"),
		TEAM2_GOAL_POINT2("team2.goal.point2");
		
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
	
	private static File getTemplateDirectory() {
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

package nmt.minecraft.WorkersAndWarriors.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

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
		TEAM("teams"),
		FLAGPROTECTSIZE("flagzone.size"),
		TEAMBLOCKS("teamblocks"),;
		
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
		
		@SuppressWarnings("unchecked")
		List<TeamConfiguration> teams = (List<TeamConfiguration>) config.getList(Key.TEAM.getKey());
		
		GameSession session = new GameSession(sessionName);
		
		//Try and grab the session lobby location
		if (config.contains(Key.SESSION_LOBBY.getKey()))
		try {
			session.setLobbyLocation((Location) config.get(Key.SESSION_LOBBY.getKey()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		
		if (config.contains(Key.TEAMBLOCKS.getKey()))
		try {
			session.setMaxTeamBlock(config.getInt(Key.TEAMBLOCKS.getKey()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (config.contains(Key.FLAGPROTECTSIZE.getKey()))
		try {
			session.setProtectionSize(config.getInt(Key.FLAGPROTECTSIZE.getKey()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Team team;
		if (teams != null && !teams.isEmpty())
		for (TeamConfiguration tf : teams) {
			team = new Team(session);
			if (!tf.getSpawns().isEmpty()) {
				for (Location spawn : tf.getSpawns()) {
					team.addSpawnPoint(spawn);
				}
			}
			
			team.setBlockType(tf.getBlock());
			team.setGoalType(tf.getGoal());
			team.setTeamColor(tf.getColor());
			team.setTeamName(tf.getName());
			team.setFlagArea(tf.getGoalPoint(), session.getProtectionSize());
			
			session.addTeam(team);
		}
		
		
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
		
		if (!session.getTeams().isEmpty()) {
			
			//get configuration for each of these and throw out as a list
			List<TeamConfiguration> confs = new LinkedList<>();
			for (Team team : session.getTeams()) {
				confs.add(
						new TeamConfiguration(team)
						);
			}
			
			config.set(Key.TEAM.getKey(), confs);
		}
		
		config.set(Key.FLAGPROTECTSIZE.getKey(), session.getProtectionSize());
		
		config.set(Key.TEAMBLOCKS.getKey(), session.getMaxTeamBlock());
		
		config.set(Key.SESSION_LOBBY.getKey(), session.getLobbyLocation());
		
		File saveFile = new File(getTemplateDirectory(), templateName);
		
		config.save(saveFile);
		
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

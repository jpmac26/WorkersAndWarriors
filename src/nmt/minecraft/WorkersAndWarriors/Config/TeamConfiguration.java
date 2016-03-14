package nmt.minecraft.WorkersAndWarriors.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Team.Team;

/**
 * Holds configuration about a team.
 * @author Skyler
 *
 */
public class TeamConfiguration implements ConfigurationSerializable {
	
	/**
	 * Class that holds keys and default values
	 * @author Skyler
	 *
	 */
	public enum Key {
		
		COLOR("color", "RED"),
		NAME("name", "Red Team"),
		BLOCK("block.type", "WOOL"),
		BLOCKDATA("block.data", 14),
		GOAL("goal.type", "REDSTONE_BLOCK"),
		GOALDATA("goal.data", 0),
		SPAWNPOINTS("spawnpoints", new ArrayList<Location>(1)),
		GOALPOINT1("goal.point1", new Vector(0,0,0)),
		GOALPOINT2("goal.point2", new Vector(0,0,0));;
		
		private String key;
		
		private Object def;
		
		private Key(String key) {
			this(key, null);
		}
		
		private Key(String key, Object def) {
			this.key = key;
			this.def = def;
		}
		
		public String getKey() {
			return this.key;
		}
		
		public Object getDefault() {
			return this.def;
		}
	}
	
	private String name;
	
	private ChatColor color;
	
	private MaterialData block;
	
	private MaterialData goal;
	
	private List<Location> spawns;
	
	private Vector goalPoint1;
	
	private Vector goalPoint2;
	
	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put(Key.COLOR.getKey(), color.name());
		map.put(Key.NAME.getKey(), name);
		
		map.put(Key.BLOCK.getKey(), block.getItemType().name());
		map.put(Key.BLOCKDATA.getKey(), block.getData());
		
		map.put(Key.GOAL.getKey(), goal.getItemType().name());
		map.put(Key.GOALDATA.getKey(), goal.getData());
		
		map.put(Key.SPAWNPOINTS.getKey(), spawns);
		map.put(Key.GOALPOINT1.getKey(), goalPoint1);
		map.put(Key.GOALPOINT2.getKey(), goalPoint2);
		
		return map;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static TeamConfiguration valueOf(Map<String, Object> map) {
		
		if (map == null) {
			return null;
		}
		

		//for ease of use
		Logger logger = WorkersAndWarriorsPlugin.plugin.getLogger(); 
		
		TeamConfiguration config = new TeamConfiguration();
		
		//everything worked, yaml loaded. Now grab values
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
		
		//map at this point is gonna have all keys with at least default values, or default values if 
		//the current value doesn't match the class type of the default.
		
		config.color = ChatColor.valueOf((String) map.get(Key.COLOR.getKey()));
		config.name = (String) map.get(Key.NAME.getKey());
		
		int data;
		
		data = (Integer) map.get(Key.BLOCKDATA);
		config.block = new MaterialData(
				Material.valueOf((String) map.get(Key.BLOCK)),
				(byte) data		
				);

		data = (Integer) map.get(Key.GOALDATA);
		config.goal = new MaterialData(
				Material.valueOf((String) map.get(Key.GOAL)),
				(byte) data		
				);
		
		config.goalPoint1 = (Vector) map.get(Key.GOALPOINT1.getKey());
		config.goalPoint2 = (Vector) map.get(Key.GOALPOINT2.getKey());
		config.spawns = (List<Location>) map.get(Key.SPAWNPOINTS.getKey());
		
		return config;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ChatColor getColor() {
		return color;
	}

	public void setColor(ChatColor color) {
		this.color = color;
	}

	public MaterialData getBlock() {
		return block;
	}

	public void setBlock(MaterialData block) {
		this.block = block;
	}

	public MaterialData getGoal() {
		return goal;
	}

	public void setGoal(MaterialData goal) {
		this.goal = goal;
	}
	
	private TeamConfiguration() {
		
	}
	
	public TeamConfiguration(Team team) {
		this.name = team.getTeamName();
		this.color = team.getTeamColor();
		this.block = team.getBlockType();
		this.goal = team.getGoalType();
	}
}

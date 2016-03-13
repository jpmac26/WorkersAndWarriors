package nmt.minecraft.WorkersAndWarriors.Config.Util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

/**
 * Dumb utility class for deserializing Vectors.<br />
 * Should be able to be used exactly like a {@link Vector}
 * @author Skyler
 *
 */
public class ParsedVector extends Vector implements ConfigurationSerializable {

	public ParsedVector(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ParsedVector.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ParsedVector.class);
	}
	

	private enum aliases {
		BUKKIT("org.bukkit.util.Vector"),
		LOCATIONUPPER("VECTOR"),
		LOCATIONLOWER("vector"),
		LOCATIONFORMAL("Vector"),
		DEFAULT(ParsedVector.class.getName()),
		SIMPLE("ParsedVector");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	/**
	 * Stores fields and their config keys
	 * @author Skyler
	 *
	 */
	private enum fields {
		X("x"),
		Y("y"),
		Z("z");
		
		private String key;
		
		private fields(String key) {
			this.key = key;
		}
		
		/**
		 * Returns the configuration key mapped to this field
		 * @return
		 */
		public String getKey() {
			return this.key;
		}
	}
	
	/**
	 * Serializes the vector to a format that's able to be saved to a configuration file.
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> config = new HashMap<String, Object>(3);
		config.put(fields.X.getKey(), getX());
		config.put(fields.Y.getKey(), getY());
		config.put(fields.Z.getKey(), getZ());
		return config;
	}
	
	/**
	 * Uses the passed configuration map to instantiate a new vector (and wrapper).
	 * @param configMap
	 * @return
	 */
	public static ParsedVector valueOf(Map<String, Object> configMap) {
		double x,y,z;

		x = (double) configMap.get(fields.X.getKey());
		y = (double) configMap.get(fields.Y.getKey());
		z = (double) configMap.get(fields.Z.getKey());
		
		return new ParsedVector(x, y, z);
	}

}

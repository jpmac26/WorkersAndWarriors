package nmt.minecraft.WorkersAndWarriors.Team;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Holds information about a flag area, and provides helper functions around it.
 * @author Skyler
 *
 */
public class FlagArea {
	
	private Vector min;
	
	private Vector max;
	
	public FlagArea(Vector point1, Vector point2) {
		min = new Vector(
				Math.min(point1.getX(), point2.getX()),
				Math.min(point1.getY(), point2.getY()),
				Math.min(point1.getZ(), point2.getZ())
				);
		max = new Vector(
				Math.max(point1.getX(), point2.getX()),
				Math.max(point1.getY(), point2.getY()),
				Math.max(point1.getZ(), point2.getZ())				
				);
		min = min.toBlockVector();
		max = max.toBlockVector();
	}
	
	public FlagArea(Vector center, int radius) {
		if (center == null) {
			center = new Vector(0,0,0);
		}
		
		if (radius < 0) {
			radius = -radius;
		}
		
		Vector diff = new Vector(radius, radius, radius);
		this.max = center.clone().add(diff).toBlockVector();
		this.min = center.clone().add(diff.multiply(-1).toBlockVector());
	}
	
	/**
	 * Checks whether a given location is in the bounds of this area.<br />
	 * Note, however, that <b>the Flag Area doesn't hold world information</b>. This
	 * is expected to be controlled by either the {@link nmt.minecraft.WorkersAndWarriors.Session.BlockListener BlockListener}
	 * or the {@link nmt.minecraft.WorkersAndWarriors.Session.GameSession GameSession}
	 * @param loc Location to check whether it's in the bounds
	 * @return whether the location is in the bounds
	 */
	public boolean isIn(Location loc) {
		return isIn(loc.toVector());
	}
	
	/**
	 * Checks whether the given coordinates (presented in a {@link org.bukkit.Vector Vector} object) represent a point within
	 * the bounds of this flag area.
	 * @param vect
	 * @return whether the coordinates lie within this area
	 */
	public boolean isIn(Vector vect) {
		return vect.isInAABB(min, max);
	}

	public Vector getMin() {
		return min;
	}

	public Vector getMax() {
		return max;
	}
}

package nmt.minecraft.WorkersAndWarriors.Session;

/**
 * Listens for block changes, and updates the game session.
 * <p>
 * This class is responsible for:
 * <ul>
 * <li>Detecting Flag block breaks, and causing the appropriate update</li>
 * <li>Detecting Flag block placement, and handling</li>
 * <li>Detecting and handling block placement (allowed for workers in appropriate areas, otherwise <b>deny</b>!)</li>
 * </p>
 * @author Skyler
 *
 */
public class BlockListener {

	private GameSession session;
	
	public BlockListener(GameSession session) {
		this.session = session;
	}
}

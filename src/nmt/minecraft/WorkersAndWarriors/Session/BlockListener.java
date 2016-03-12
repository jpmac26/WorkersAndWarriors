package nmt.minecraft.WorkersAndWarriors.Session;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
public class BlockListener implements Listener {

	private GameSession session;
	
	public BlockListener(GameSession session) {
		this.session = session;
	}
	
	//REMEMBER: make sur a player is in a game before trying to handle anything.
	//Do this via session.getPlayer() type thing. 
	
	/**
	 * Called when a NON-FLAG block is broken
	 * @param e
	 */
	private void onBlockBreak(BlockBreakEvent e) {
		//check if player is a worker
		//if not, don't allow it
		//if so, make sure it's the opponent's block type
		//and if it is, allow it. 
		//otherwise, cancel
		//e.setCancelled(true);
	}
	
	/**
	 * Called when a block is placed. NOT A FLAG BLOCK
	 * @param e
	 */
	private void onBlockPlace(BlockPlaceEvent e) {
		//check if player is a worker AND this is in a valid place
		//if not, don't allow it
		//if so, make sure it's the opponent's block type
		//and if it is, allow it. 
		//otherwise, cancel
		//e.setCancelled(true);
		
	}
	
	/**
	 * Called when a Flag block is broken
	 * @param e
	 */
	private void onFlagBreak(BlockBreakEvent e) {
		
	}
	
	/**
	 * Called when a Flag block is being placed
	 * @param e
	 */
	private void onFlagPlace(BlockPlaceEvent e) {
		
	}
}
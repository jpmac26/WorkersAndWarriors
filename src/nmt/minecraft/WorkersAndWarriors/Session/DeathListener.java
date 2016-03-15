/**
 * 
 */
package nmt.minecraft.WorkersAndWarriors.Session;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession.State;
import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * @author williamfong
 *
 */
public class DeathListener implements Listener {

	/**
	 * This method determines if the damage event is appropriate for<br />
	 * a player entity and checks if the damage is fatal and if the player<br />
	 * is even in the session
	 * @param e The damage event
	 */
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		// Check EntityType
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getEntity();
		WorkersAndWarriorsPlugin plugin = WorkersAndWarriorsPlugin.plugin;
		
		// Check to see if player is even in a working session
		GameSession session = plugin.getSession(p);
		if (session == null) {
			// Player was not found!
			return;
		}
		
		//Check to see if session is active
		if (session.getState() != State.RUNNING) {
			// The player's session is currently not running
			// TODO may require additional behavior
			return;
		}
		
		// Check to see if damage is 'fatal'
		if (p.getHealth() - e.getDamage() < 1) {
			this.handleDeath(p, e, session);
		}
	}
	
	/**
	 * This method handles the death event.
	 * @param p
	 */
	private void handleDeath(Player p, EntityDamageByEntityEvent e, GameSession s) {
		// Player's should NOT 'die' by Minecraft definition, we 
		// want to handle death by our own rules
		e.setCancelled(true);
		
		// Obtain WW player for respawn behavior
		WWPlayer wPlayer = s.getPlayer(p);
		Team wTeam = s.getTeam(p);
		Location respawnPoint = wTeam.getRandomSpawn();
		wPlayer.spawn(respawnPoint);
	}
}

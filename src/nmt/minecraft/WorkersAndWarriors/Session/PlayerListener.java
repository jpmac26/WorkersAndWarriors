/**
 * 
 */
package nmt.minecraft.WorkersAndWarriors.Session;

import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.PluginConfiguration;
import nmt.minecraft.WorkersAndWarriors.IO.ChatFormat;
import nmt.minecraft.WorkersAndWarriors.Scheduling.Scheduler;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession.State;
import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * This class handles all player deaths within WorkersAndWarriors. It also handles player disconnects.
 * @author williamfong, Skyler
 *
 */
public class PlayerListener implements Listener {
	
	private GameSession session;
	
	public PlayerListener(GameSession session) {
		this.session = session;
		Bukkit.getPluginManager().registerEvents(this, WorkersAndWarriorsPlugin.plugin);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e instanceof EntityDamageByEntityEvent) {
			return; //handled in onPlayerDamage(EntityDamageByEntityEvent)
		}
		
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getEntity();
		
		if (this.session == null) {
			// Listener was not correctly instantiated
			return;
		}
		
		if (session.getPlayer(p) == null) {
			return;
		}
		
		if (session.getState() == State.OPEN || session.getState() == State.RUNNING)
		if (e.getFinalDamage() >= p.getHealth()) {
			e.setDamage(0);
			return;
		}
	}

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
		
		if (this.session == null) {
			// Listener was not correctly instantiated
			return;
		}
		
		WWPlayer wPlayer = session.getPlayer(p);
		if (wPlayer == null) {
			//player isn't in this session
			return;
		}
		
		Team team = session.getTeam(wPlayer);
		
		if (team != null)
		if (e.getDamager() instanceof Player)
		if (team.getPlayer((Player) e.getDamager()) != null) {
			//the damager is on the same team. Disallow if game is running
			if (session.getState() == State.RUNNING) {
				e.setCancelled(true);
				return; 
			}
		}
		
		//Check to see if session is active
		if (this.session.getState() != State.RUNNING) {
			// The player's session is currently not running
			if (session.getState() == State.OPEN) {
				if (p.getHealth() - e.getDamage() < 1) {
					e.setDamage(0);
				}
			}
			return;
		}
		
		// Check to see if damage is 'fatal'
		if (p.getHealth() - e.getDamage() < 1) {
			this.handleDeath(p, e);
			
			// If the killer was a Player entity, notify them
			if (e.getDamager() instanceof Player) {
				Player killer = ((Player) e.getDamager());
				msgKiller(killer, e);
				
				killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_VILLAGER_DEATH, 10, 1);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_SMALL_FALL, 10, 1);
			}
			
			// For gameplay, play audio for players
			
		}	
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(e.getPlayer());
		
		if (session != null) {
			//that player was in a session!
			//remove them and teleport them back before they leave
			session.removePlayer(e.getPlayer(), true);
		}
			
	}
	
	/**
	 * This method handles the death event.
	 * @param p
	 */
	private void handleDeath(Player p, EntityDamageByEntityEvent e) {
		// Player's should NOT 'die' by Minecraft definition, we 
		// want to handle death by our own rules
		e.setCancelled(true);
		
		//We need to use the player's location as soon as they die. Rather than mess with teleports, we'll
		//store it as soon as they 'die'
		Location deathLocation = p.getEyeLocation();
		
		//set player to intermediate state
		p.setGameMode(GameMode.SPECTATOR);
		p.setSpectatorTarget(e.getDamager());
		
		// Obtain WW player for respawn behavior
		WWPlayer wPlayer = this.session.getPlayer(p);
		Team wTeam = this.session.getTeam(p);
		Respawn respawn = new Respawn(wPlayer, wTeam);
		respawn.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 40, 4));
		Scheduler.getScheduler().schedule(respawn, null, PluginConfiguration.config.getRespawnCooldown());
		
		//Drop any flags they may have
		ListIterator<ItemStack> it = p.getInventory().iterator();
		ItemStack item;
		while (it.hasNext()) {
			item = it.next();
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			
			for (MaterialData data : session.getGoalTypes()) {
				if (item.getData().equals(data)) {
					//drop the goal
					deathLocation.getWorld().dropItemNaturally(deathLocation, item);
				}
			}
		}
		
		wPlayer.setFlag(false);
		
	}
	
	/**
	 * This method messages the killer
	 * TODO If additional behavior is required: e.g player score/stats, this method
	 * should be encapsulated with a larger method.
	 * @param killer The Player killer
	 * @param e The EntityDamageByEntityEvent
	 */
	private void msgKiller(Player killer, EntityDamageByEntityEvent e) {
		// Get victim name
		Player victim = (Player) e.getEntity();
		String vName = victim.getDisplayName();
		killer.sendMessage("Eliminated: " + vName);
	
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		WWPlayer wp = session.getPlayer(e.getPlayer());
		
		if (wp == null) {
			return;
		}
		
		//can pick up flags. That's it.
		for (Team t : session.getTeams()) {
			if (t.getGoalType().equals(e.getItem().getItemStack().getData())) {
				//It's alright. But we need to check if it's their own team's flag
				
				if (t.getPlayer(e.getPlayer()) != null) {
					//they're on that team. E.g. team recovered their flag
					reclaimFlag(wp, t, e);
					
				} else {
					wp.setFlag(true);
				}
				
				return;
			}
		}
		
		//we went through each team, no match. So it's not a flag. It's some debris. Leave it.
		e.setCancelled(true);
	}
	
	/**
	 * A team has just recovered their flag by killing the theif and picking the item up.
	 * @param player
	 * @param team
	 */
	private void reclaimFlag(WWPlayer player, Team team, PlayerPickupItemEvent e) {
		team.sendMessage(ChatFormat.SUCCESS.wrap(player.getPlayer().getName() + " just recovered your flag!"));
		e.setCancelled(true);
		e.getItem().remove();
		team.resetFlagBlock();
	}
	
	@EventHandler
	public void onItemThrow(PlayerDropItemEvent e) {
		if (session.getPlayer(e.getPlayer()) != null)
		if (session.getState() == State.RUNNING || session.getState() == State.OPEN) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatFormat.WARNING.wrap("You cannot discard items!"));
		}
	}
}

package nmt.minecraft.WorkersAndWarriors.Session;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

/**
 * This class handles the respawn mechanics
 * TODO Make it work with the scheduler, so the DeathListener only <br />
 * has to handle scheduling the respawn. The class does the rest.
 * TODO We might also need to make sure the player is still logged <br />
 * into a session, they may disconnect in the time from when the <br />
 * respawn class is instantiated. <--- Will forget, remind me later
 * @author williamfong
 *
 */
public class Respawn {
	
	private WWPlayer wPlayer;
	private Team wTeam;
	private List<PotionEffect> effects;
	
	/**
	 * Default constructor for the Respawn Class, on its own <br />
	 * it will not store any additional information other than <br />
	 * the WWPlayer and the Team the WWPlayer is on.
	 * @param wPlayer
	 * @param wTeam
	 */
	public Respawn (WWPlayer wPlayer, Team wTeam) {
		this.wPlayer = wPlayer;
		this.wTeam = wTeam;
		this.effects = new ArrayList<PotionEffect>();
	}
	
	/**
	 * This method adds a potion effect that will be applied to a player <br />
	 * when they respawn. Any number of effects can be applied; duplicate <br />
	 * effects are not considered and will adopt Bukkit behaviors.
	 * @param effect
	 */
	public void addPotionEffect (PotionEffect effect) {
		this.effects.add(effect);
	}
	
	/**
	 * This method causes a player to respawn.
	 */
	public void respawnPlayer() {
		Location spawnLocation = this.wTeam.getRandomSpawn();
		Player p = (Player) this.wPlayer.getPlayer();
		p.teleport(spawnLocation);
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(20);
		p.setTicksLived(0);
		p.getActivePotionEffects().clear();
		// Apply potion effects
		for (PotionEffect e : this.effects){
			p.addPotionEffect(e);
		}
		
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);
	}
}

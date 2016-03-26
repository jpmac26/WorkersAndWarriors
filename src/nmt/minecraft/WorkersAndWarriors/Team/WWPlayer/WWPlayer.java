package nmt.minecraft.WorkersAndWarriors.Team.WWPlayer;

import static nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;
import nmt.minecraft.WorkersAndWarriors.Team.Team;

/**
 * Player wrapper class.
 * <p>
 * This class wraps player information and holds WW game information about that player.<br />
 * This includes:
 * <ul>
 * <li>Whether the player has a flag</li>
 * <li>Whether the player is currently spawned, and if they're invincible</li>
 * <li><b>The type of player</b> the player is</li>
 * </ul>
 * </p>
 * @author Skyler
 *
 */
public class WWPlayer {
	
	public enum Type {
		WORKER(Material.LEATHER_HELMET, null, null, null, Material.STONE_PICKAXE, null),
		WARRIOR(Material.LEATHER_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.LEATHER_BOOTS, Material.STONE_SWORD, Material.SHIELD);
		
		private Material[] equips;
		
		private Type() {
			equips = new Material[6];
		}
		
		private Type(Material head, Material chest, Material legs, Material boots, Material main, Material off) {
			equips = new Material[6];
			equips[0] = head;
			equips[1] = chest;
			equips[2] = legs;
			equips[3] = boots;
			equips[4] = main;
			equips[5] = off; 
			
			
		}
		
		public void outfitPlayer(WWPlayer player) {
			if (!player.getPlayer().isOnline()) {
				return;
			}
			
			Player p = player.getPlayer().getPlayer();
			Team t = WorkersAndWarriorsPlugin.plugin.getSession(player).getTeam(player);
			Color color = Color.AQUA;
			DyeColor dc;
			
			try {
				dc = DyeColor.valueOf(t.getTeamColor().name());
			} catch (Exception e) {
				//unable to get that color
				dc = null;
			}
			
			ItemStack[] equipment = new ItemStack[6];
			
			
			for (int i = 0; i < equips.length; i++) {
				if (equips[i] == null) {
					continue;
				}
				
				equipment[i] = new ItemStack(equips[i]);
				
				if (dc != null) {
					color = color.mixDyes(dc);
					ItemMeta meta = equipment[i].getItemMeta();
					if (meta instanceof LeatherArmorMeta) {
						LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;
						lMeta.setColor(color);
						equipment[i].setItemMeta(lMeta);
					}
					
				}
			}
			
			EntityEquipment peq = p.getEquipment();
			peq.setHelmet(equipment[0]);
			peq.setChestplate(equipment[1]);
			peq.setLeggings(equipment[2]);
			peq.setBoots(equipment[3]);
			peq.setItemInMainHand(equipment[4]);
			peq.setItemInOffHand(equipment[5]);
		}
	}
	
	private OfflinePlayer player;
	
	private Type type;
	
	private boolean hasFlag;
	
	private Location pregameLocation;
	
	public WWPlayer(OfflinePlayer player, Type type) {
		this.player = player;
		this.hasFlag = false;
		this.type = type;
		if (player.isOnline()) {
			pregameLocation = ((Player) player).getLocation();
		}
	}
	
	public void setPregameLocation(Location l) {
		this.pregameLocation = l;
	}
	
	public Location getPregameLocation() {
		return pregameLocation;
	}
	
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	public boolean hasFlag() {
		return hasFlag;
	}
	
	public void setFlag(boolean flag) {
		this.hasFlag = flag;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	/**
	 * Pretty-spawns a player at a location.<br />
	 * Sets gamemode, sets proper effects, etc
	 * @param spawnLocation
	 */
	public void spawn(Location spawnLocation) {
		// Check if player is still online
		if (!this.getPlayer().isOnline()) {
			// Player is not online
			System.out.println("Error! Attempted to spawn Offline player: " + this.getPlayer().getName());
			return;
		}
		Player p = this.getPlayer().getPlayer();
		p.teleport(spawnLocation);
		
		p.setGameMode(GameMode.SURVIVAL);
		
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(player);
		Team team = session.getTeam(this);
		
		List<MaterialData> goalDatas = new ArrayList<>(session.getTeams().size());
		for (Team t : session.getTeams()) {
			if (t.getTeamName().equals(team.getTeamName())) {
				continue;
			}
			
			goalDatas.add(t.getGoalType());
		}
		
		Iterator<ItemStack> it = p.getInventory().iterator();
		ItemStack item;
		while (it.hasNext()) {
			item = it.next();
			if (item == null || !item.getData().equals(team.getBlockType())) {
				for (MaterialData data : goalDatas) {
					if (item.getData().equals(data)) {
						continue;
					}
				}
				
				it.remove();
			}
		}
		
		
		type.outfitPlayer(this);
	}
	
	/**
	 * "<i>kills</i>" a player, queueing them for respawn.<br />
	 * Handles gamemode, potion effects, etc
	 */
	public void die() {
		
	}
	
	/**
	 * Gives the player blocks to place.<br />
	 * Does not check whether this player is a builder (and therefore would be able to place it) or not.
	 * @param count
	 */
	public void giveBlock(int count) {
            this.getPlayer().getPlayer().getInventory().addItem(plugin.getSession(this).getTeam(this).getBlockType().toItemStack(count));
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof WWPlayer) {
			WWPlayer op = (WWPlayer) o;
			return op.player.getUniqueId().equals(player.getUniqueId());
		}
		
		return false;
	}
	
}

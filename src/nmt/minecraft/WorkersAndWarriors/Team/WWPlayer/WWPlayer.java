package nmt.minecraft.WorkersAndWarriors.Team.WWPlayer;

import static nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin.plugin;

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

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
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
		WORKER(new ItemStack(Material.LEATHER_HELMET), null, null, null, new ItemStack(Material.STONE_PICKAXE), null),
		WARRIOR(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.STONE_SWORD), new ItemStack(Material.SHIELD));
		
		private ItemStack[] equips;
		
		private Type() {
			equips = new ItemStack[6];
		}
		
		private Type(ItemStack head, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack main, ItemStack off) {
			equips = new ItemStack[6];
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
			if (dc != null) {
				color = color.mixDyes(dc);
				for (ItemStack item : equips) {
					if (item == null) {
						continue;
					}
					
					ItemMeta meta = item.getItemMeta();
					if (meta instanceof LeatherArmorMeta) {
						LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;
						lMeta.setColor(color);
						item.setItemMeta(lMeta);
					}
				}
			}
			
			EntityEquipment equipment = p.getEquipment();
			equipment.setHelmet(equips[0]);
			equipment.setChestplate(equips[1]);
			equipment.setLeggings(equips[2]);
			equipment.setBoots(equips[3]);
			equipment.setItemInMainHand(equips[4]);
			equipment.setItemInOffHand(equips[5]);
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
		p.getInventory().clear();
		
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

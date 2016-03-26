package nmt.minecraft.WorkersAndWarriors.Session;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for block changes, and updates the game session.
 * <p>
 * This class is responsible for:
 * <ul>
 * <li>Detecting Flag block breaks, and causing the appropriate update</li>
 * <li>Detecting Flag block placement, and handling</li>
 * <li>Detecting and handling block placement (allowed for workers in
 * appropriate areas, otherwise <b>deny</b>!)</li>
 * </p>
 *
 * @author Skyler
 *
 */
public class BlockListener implements Listener {

    private GameSession session;

    public BlockListener(GameSession session) {
        this.session = session;
        Bukkit.getPluginManager().registerEvents(this, WorkersAndWarriorsPlugin.plugin);
    }

    //REMEMBER: make sur a player is in a game before trying to handle anything.
    //Do this via session.getPlayer() type thing. 
    /**
     * Called when a NON-FLAG block is broken
     *
     * @param e
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        //check if player is a worker
        //if not, don't allow it
        //if so, make sure it's any team's block type
        //and if it is, allow it. 
        //otherwise, cancel
        //e.setCancelled(true);
        boolean flag0 = false;
        boolean flag1 = false;
        if (session.getPlayer(e.getPlayer()) != null) {
            if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WARRIOR) {
                e.setCancelled(true);
            } else if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WORKER) {
                for (Team team : session.getTeams()) {

                    if (team.getGoalType().equals(e.getBlock().getState().getData())) {
                        flag0 = true;

                        onFlagBreak(e);

                        break;
                    }

                    if (team.getBlockType().equals(e.getBlock().getState().getData())) {
                        flag0 = true;
                        do {
                            int player = WorkersAndWarriorsPlugin.random.nextInt(session.getTeam(session.getPlayer(e.getPlayer())).getPlayers().size());
                            WWPlayer wwp = session.getTeam(session.getPlayer(e.getPlayer())).getPlayers().get(player);
                            if (wwp.getType() == WWPlayer.Type.WORKER) {
                                wwp.giveBlock(1);
                                flag1 = true;
                            }
                        } while (flag1 == false);
                    }
                }
                if (flag0 == false) {
                    e.setCancelled(true);

                }
            }

        }
    }

    /**
     * Called when a block is placed. NOT A FLAG BLOCK
     *
     * @param e
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        //check if player is a worker AND this is in a valid place
        //if not, don't allow it
        //if so, make sure it's the opponent's block type
        //and if it is, allow it. 
        //otherwise, cancel
        //e.setCancelled(true);
    	
        boolean flag4 = false;
        if (session.getPlayer(e.getPlayer()) != null) {
            if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WARRIOR) {
                e.setCancelled(true);
            } else if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WORKER) {
                for (Team team : session.getTeams()) {
                    if (team.getGoalType().equals(e.getBlock().getState().getData()) && !team.equals(session.getTeam(session.getPlayer(e.getPlayer())))) {
                        flag4 = true;
                        this.onFlagPlace(e);
                        break;
                    }
                }

                if (!session.getTeam(session.getPlayer(e.getPlayer())).getBlockType().equals(e.getBlock().getState().getData()) || flag4 == false) {

                    e.setCancelled(true);
                    return;
                }
                for (Team team : session.getTeams()) {
                    if (team.getFlagArea().isIn(e.getBlock().getLocation())) {
                        e.setCancelled(true);
                        break;
                    }
                }
            }

        }
    }

    /**
     * Called when a Flag block is broken Only called when a flag has been
     * broken...
     *
     * @param e
     */
    private void onFlagBreak(BlockBreakEvent e) {
        WWPlayer player = session.getPlayer(e.getPlayer());

        if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WARRIOR) {
            e.setCancelled(true);
        } else if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WORKER) {
            player.setFlag(true);
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            player.getPlayer().getPlayer().getInventory().addItem(new ItemStack(e.getBlock().getState().getType(), 1));
        }
    }

    /**
     * Called when a Flag block is being placed
     *
     * @param e
     */
    private void onFlagPlace(BlockPlaceEvent e) {
    	WWPlayer player = session.getPlayer(e.getPlayer());
        for (Team team : session.getTeams()) {
                    if (team.getFlagArea().isIn(e.getBlock().getLocation())) {
                        team.addPoints(1);
                        player.setFlag(false);
                        team.resetFlagBlock();
                        e.getBlockReplacedState().setType(Material.AIR);
                        
                        e.getBlock().getLocation().getWorld().playEffect(e.getBlock().getLocation(), Effect.HAPPY_VILLAGER, 90000);
                        //TODO
                        break;
                    }
        }

    }

    /**
     * Helper function that goes through the dumb process of converting
     * MaterialDate (which you can get from
     * {@link nmt.minecraft.WorkersAndWarriors.Config.PluginConfiguration#getTeam2Block()}
     * and the like) and setting a block to be it. Cause it's dumb and not worth
     * the time to try and figure out over and over.
     *
     * @param block The block to change the type of
     * @param data The MaterialData to set the given block to
     */
    public static void setBlockType(Block block, MaterialData data) {
        block.setType(data.getItemType());
        BlockState state = block.getState();
        state.setData(data);
        state.update();
    }

}

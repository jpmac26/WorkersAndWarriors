package nmt.minecraft.WorkersAndWarriors.Session;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Scheduling.GameFinishAnimationEndEvent;
import nmt.minecraft.WorkersAndWarriors.Scheduling.Scheduler;
import nmt.minecraft.WorkersAndWarriors.Scheduling.Tickable;
import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

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
public class BlockListener implements Listener, Tickable<Integer> {

    private GameSession session;
    
    private List<Location> blockList;

    public BlockListener(GameSession session) {
        this.session = session;
        this.blockList = new LinkedList<Location>();
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
                    	
                    	//make sure it's not their own flag
                    	if (session.getTeam(e.getPlayer()).equals(team)) {
                    		e.setCancelled(true);
                    		return;
                    	}
                    	
                        flag0 = true;

                        onFlagBreak(e);

                        break;
                    }

                    if (team.getBlockType().equals(e.getBlock().getState().getData())) {
                        flag0 = true;
                        e.setCancelled(true);
                        e.getBlock().setType(Material.AIR);
                        blockList.remove(e.getBlock().getLocation());
                        do {
                            int player = WorkersAndWarriorsPlugin.random.nextInt(team.getPlayers().size());
                            WWPlayer wwp = team.getPlayers().get(player);
                            if (wwp.getType() == WWPlayer.Type.WORKER) {
                                wwp.giveBlock(1);
                                flag1 = true;
                            }
                        } while (flag1 == false);
                    }
                }
                if (flag0 == false) {
                    e.setCancelled(true);

                } else {
                	e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);
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

        if (session.getPlayer(e.getPlayer()) != null) {
            if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WARRIOR) {
                e.setCancelled(true);
            } else if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WORKER) {
                for (Team team : session.getTeams()) {
                    if (team.getGoalType().equals(e.getBlock().getState().getData()) && !team.equals(session.getTeam(session.getPlayer(e.getPlayer())))) {
                        this.onFlagPlace(e);
                        return;
                    }
                }

                if (!session.getTeam(session.getPlayer(e.getPlayer())).getBlockType().equals(e.getBlock().getState().getData())) {

                    e.setCancelled(true);
                    return;
                }
                for (Team team : session.getTeams()) {
                    if (team.getFlagArea().isIn(e.getBlock().getLocation())) {
                        e.setCancelled(true);
                        break;
                    }
                }
                
                //block is legal, put it down
                blockList.add(e.getBlock().getLocation());
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
            ItemStack stack = new ItemStack(e.getBlock().getState().getType(), 1);
            stack.setData(session.getTeam(player).getGoalType());
            player.getPlayer().getPlayer().getInventory().addItem(stack);
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            
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
                for (Team team2 : session.getTeams()) {
                    if (team2.getGoalType().equals(e.getBlock().getState().getData())) {
                        team2.resetFlagBlock();
                        break;
                    }
                }
                e.getBlock().setType(Material.AIR);
                
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
    
    /**
     * Calls for the start of block removal from the map.<br />
     * This is <i>intended</i> for use only when the game is over, and the map is cleaning itself up.
     * If the second parameter is true, the cool decay animation is not done and all blocks are just
     * returned to air status.
     */
    public void startDecay(boolean skipAnimation) {
    	
    	if (blockList.isEmpty() || skipAnimation) {
    		//just remove blocks, throw event
    		
    		if (!blockList.isEmpty())
    		for (Location l : blockList) {
    			l.getBlock().setType(Material.AIR);
    		}
    		
    		Bukkit.getPluginManager().callEvent(new GameFinishAnimationEndEvent(session));
    		return;
    	}
    	
    	//do decay animation
    	//start off with big times, then go small
    	Scheduler.getScheduler().schedule(this, 0, 1);
    }
    
    @Override
    public void alarm(Integer key) {
    	//make sure we're not at an empty list
    	if (blockList.isEmpty()) {
    		Bukkit.getPluginManager().callEvent(new GameFinishAnimationEndEvent(session));
    		return;
    	}
    	
    	//start at 1 second, 1, 1, 1, 1, .9, .8, .7, .6, .5... to .1
    	int nextTime = (1/(key / 4)) / 5; //makes a nice pattern
    	
    	//remove block
    	blockList.get(WorkersAndWarriorsPlugin.random.nextInt(blockList.size()))
    		.getBlock().setType(Material.AIR);
    	
    	//schedule next decay
    	Scheduler.getScheduler().schedule(this, key + 1, nextTime);
    }

}

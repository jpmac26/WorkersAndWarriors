package nmt.minecraft.WorkersAndWarriors.Session;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
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
public class BlockListener implements Listener {

    private GameSession session;

    public BlockListener(GameSession session) {
        this.session = session;
    }

    //REMEMBER: make sur a player is in a game before trying to handle anything.
    //Do this via session.getPlayer() type thing. 
    /**
     * Called when a NON-FLAG block is broken
     *
     * @param e
     */
    private void onBlockBreak(BlockBreakEvent e) {
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
                    if (team.getBlockType() == e.getBlock().getState().getData()) {
                        flag0 = true;
                        do {
                            int player = WorkersAndWarriorsPlugin.random.nextInt(session.getTeam(session.getPlayer(e.getPlayer())).getPlayers().size());
                            WWPlayer wwp = session.getTeam(session.getPlayer(e.getPlayer())).getPlayers().get(player);
                            if (wwp.getType() == WWPlayer.Type.WORKER) {
                                wwp.giveBlock(1);
                                flag1 = true;
                            }
                        } while (flag1 = false);
                    }
                }
                if (flag0 = false) {
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
     *
     * @param e
     */
    private void onFlagBreak(BlockBreakEvent e) {
        boolean flag2 = false;
        if (session.getPlayer(e.getPlayer()) != null) {
            if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WARRIOR) {
                e.setCancelled(true);
            } else if (session.getPlayer(e.getPlayer()).getType() == WWPlayer.Type.WORKER) {
                for (Team team : session.getTeams()) {
                    if (team.getGoalType() == e.getBlock().getState().getData() && session.getTeam(session.getPlayer(e.getPlayer())).getGoalType() != e.getBlock().getState().getData()) {
                        flag2 = true;
                    }
                    if (flag2 = false) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
        /**
         * Called when a Flag block is being placed
         *
         * @param e
         */
    private void onFlagPlace(BlockPlaceEvent e) {

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

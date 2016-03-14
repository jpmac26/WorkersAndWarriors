package nmt.minecraft.WorkersAndWarriors.IO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;

public class TeamCommands implements CommandExecutor {

	public static final String baseCommand = "wwteam";
	
	public enum SubCommand {
		CREATE("create"),
		INFO("info"),
		LIST("list"),
		SETBLOCK("setblock"),
		SETGOALBLOCK("setgoalblock"),
		SETGOALAREA("setgoalarea"),
		SETCOLOR("setcolor");
		
		private String commandName;
		
		private SubCommand(String commandName) {
			this.commandName = commandName;
		}
		
		public final String getName() {
			return this.commandName;
		}
	}
	
	public static List<String> getCommandList() {
		List<String> list = new ArrayList<String>(SubCommand.values().length);
		for (SubCommand s : SubCommand.values()) {
			list.add(s.getName());
		}
		return list;
	}
	
	private static TeamCommands executor = null;
	
	public static TeamCommands getExecutor() {
		if (executor == null) {
			executor = new TeamCommands();
		}
		
		return executor;
	}
	
	private TeamCommands() {
		WorkersAndWarriorsPlugin.plugin.getCommand(baseCommand).setExecutor(this);
		WorkersAndWarriorsPlugin.plugin.getCommand(baseCommand).setTabCompleter(
				CommandTabCompleter.getCompleter());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase(baseCommand)) {
			if (args.length == 0) {
				return false;
			}
			String subCmd = args[0];
			
			if (subCmd.equalsIgnoreCase(SubCommand.CREATE.getName())) {
				return createCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.INFO.getName())) {
				return infoCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.LIST.getName())) {
				return listCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.SETBLOCK.getName())) {
				return setBlockCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.SETGOALBLOCK.getName())) {
				return setGoalBlockCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.SETGOALAREA.getName())) {
				return setGoalAreaCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.SETCOLOR.getName())) {
				return setColorCommand(sender, args);
			}
			
			return false;
		} else {
			return false;
		}
		
	}
	
	private boolean createCommand(CommandSender sender, String[] args) {
		
		WorkersAndWarriorsPlugin.plugin.onReload();		
		return true;
	}
	
	private boolean infoCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean listCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean setBlockCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean setGoalBlockCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean setGoalAreaCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean setColorCommand(CommandSender sender, String[] args) {
		return false;
	}

}

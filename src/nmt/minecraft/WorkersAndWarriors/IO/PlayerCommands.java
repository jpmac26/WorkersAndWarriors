package nmt.minecraft.WorkersAndWarriors.IO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;

public class PlayerCommands implements CommandExecutor {

	public static final String baseCommand = "wwplayer";
	
	public enum SubCommand {
		JOIN("join"),
		LEAVE("leave"),
		TEAM("team"),
		MENU("menu");
		
		private String commandName;
		
		private SubCommand(String commandName) {
			this.commandName = commandName;
		}
		
		public final String getName() {
			return this.commandName;
		}
	}
	
	private static PlayerCommands executor = null;
	
	public static PlayerCommands getExecutor() {
		if (executor == null) {
			executor = new PlayerCommands();
		}
		
		return executor;
	}
	
	private PlayerCommands() {
		WorkersAndWarriorsPlugin.plugin.getCommand(baseCommand).setExecutor(this);
		WorkersAndWarriorsPlugin.plugin.getCommand(baseCommand).setTabCompleter(
				CommandTabCompleter.getCompleter());
	}
	
	public static List<String> getCommandList() {
		List<String> list = new ArrayList<String>(SubCommand.values().length);
		for (SubCommand s : SubCommand.values()) {
			list.add(s.getName());
		}
		return list;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase(baseCommand)) {
			if (args.length == 0) {
				return false;
			}
			
			String subCmd = args[0];
			
			if (subCmd.equalsIgnoreCase(SubCommand.JOIN.getName())) {
				return joinCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.LEAVE.getName())) {
				return leaveCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.TEAM.getName())) {
				return teamCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.MENU.getName())) {
				return menuCommand(sender, args);
			}
			
			return false;
		} else {
			return false;
		}
		
	}
	
	private boolean joinCommand(CommandSender sender, String[] args) {	
		return true;
	}
	
	private boolean leaveCommand(CommandSender sender, String[] args) {
		return true;
	}
	
	private boolean teamCommand(CommandSender sender, String[] args) {
		return true;
	}
	
	private boolean menuCommand(CommandSender sender, String[] args) {
		return true;
	}

}

package nmt.minecraft.WorkersAndWarriors.IO;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.PluginConfiguration;

public class PluginCommands implements CommandExecutor {

	public static final String baseCommand = "ww";
	
	public enum SubCommand {
		RELOAD("reload"),
		VERSION("version");
		
		private String commandName;
		
		private SubCommand(String commandName) {
			this.commandName = commandName;
		}
		
		public final String getName() {
			return this.commandName;
		}
	}
	
	private static PluginCommands executor = null;
	
	public static PluginCommands getExecutor() {
		if (executor == null) {
			executor = new PluginCommands();
		}
		
		return executor;
	}
	
	private PluginCommands() {
		WorkersAndWarriorsPlugin.plugin.getCommand(baseCommand).setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase(baseCommand)) {
			if (args.length == 0) {
				return false;
			}
			
			String subCmd = args[0];
			if (subCmd.equalsIgnoreCase(SubCommand.RELOAD.getName())) {
				return reloadCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.VERSION.getName())) {
				return versionCommand(sender, args);
			}
			
			return false;
		} else {
			return false;
		}
		
	}
	
	private boolean reloadCommand(CommandSender sender, String[] args) {
		
		WorkersAndWarriorsPlugin.plugin.onReload();		
		return true;
	}
	
	private boolean versionCommand(CommandSender sender, String[] args) {
		sender.sendMessage(ChatFormat.INFO.wrap("WorkersAndWarriors plugin version " + PluginConfiguration.config.getVersion()));	
		
		return true;
	}

}

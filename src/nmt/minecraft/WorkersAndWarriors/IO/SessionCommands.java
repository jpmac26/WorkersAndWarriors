package nmt.minecraft.WorkersAndWarriors.IO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.SessionConfiguration;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;

public class SessionCommands implements CommandExecutor {

	public static final String baseCommand = "wwsession";
	
	public enum SubCommand {
		CREATE("create"),
		LOADTEMPLATE("load"),
		OPEN("open"),
		START("start"),
		STOP("stop"),
		INFO("info"),
		LIST("list"),
		STOPALL("stopall"),
		SAVETEMPLATE("save"),
		SETBLOCKLIMIT("setblocklimit"),
		SETFLAGPROTECTIONRADIUS("setgoalradius");
		
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
	
	private static SessionCommands executor = null;
	
	public static SessionCommands getExecutor() {
		if (executor == null) {
			executor = new SessionCommands();
		}
		
		return executor;
	}
	
	private SessionCommands() {
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
			
			if (subCmd.equalsIgnoreCase(SubCommand.LOADTEMPLATE.getName())) {
				return loadCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.OPEN.getName())) {
				return openCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.START.getName())) {
				return startCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.STOP.getName())) {
				return stopCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.INFO.getName())) {
				return infoCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.LIST.getName())) {
				return listCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.STOPALL.getName())) {
				return stopAllCommand(sender, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.SAVETEMPLATE.getName())) {
				return saveCommand(sender, args);
			}
			
			return false;
		} else {
			return false;
		}
		
	}
	
	private boolean createCommand(CommandSender sender, String[] args) {
		//wws create [name]
		if (args.length != 2 || args[1].isEmpty()) {
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws create " + ChatFormat.SESSION.wrap("[name]"));
			return true;
		}
		
		String name = args[1];
		if (WorkersAndWarriorsPlugin.plugin.getSession(name) != null) {
			sender.sendMessage(ChatFormat.ERROR.wrap("A session with that name already exists!"));
			return true;
		}
		
		WorkersAndWarriorsPlugin.plugin.addSession(new GameSession(name));
		sender.sendMessage(ChatFormat.SUCCESS + "Session " + ChatFormat.SESSION + name 
				+ ChatFormat.SUCCESS.wrap(" created!"));
			
		return true;
	}
	
	private boolean loadCommand(CommandSender sender, String[] args) {
		//wws load [template name] [session name]
		if (args.length != 3 || args[2].isEmpty()) {
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws load " 
							+ ChatFormat.TEMPLATE + "[template] " + ChatFormat.SESSION.wrap("[name]"));
			return true;
		}
		
		String name = args[2];
		String template = args[1];
		if (WorkersAndWarriorsPlugin.plugin.getSession(name) != null) {
			sender.sendMessage(ChatFormat.ERROR.wrap("A session with that name already exists!"));
			return true;
		}
		
		GameSession session = null;
		try {
			session = SessionConfiguration.loadSesson(template, name);
		} catch (FileNotFoundException e) {
			sender.sendMessage(ChatFormat.ERROR + "Unable to locate template file " + 
					ChatFormat.TEMPLATE.wrap(template));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			sender.sendMessage(ChatFormat.ERROR.wrap("Encountered extreme IO error while attempting to read template!"));
			sender.sendMessage(ChatFormat.INFO.wrap("See console for more information."));
			WorkersAndWarriorsPlugin.plugin.getLogger().warning("IOException when loading template file!");
			return true;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			sender.sendMessage(ChatFormat.ERROR.wrap("An invalid template was detected!"));
			sender.sendMessage(ChatFormat.INFO.wrap("See console for more information."));
			return true;
		}		
		
		WorkersAndWarriorsPlugin.plugin.addSession(session);
		sender.sendMessage(ChatFormat.SUCCESS + "Session " + ChatFormat.SESSION + name 
				+ ChatFormat.SUCCESS.wrap(" created!"));
		
		return true;
	}
	
	private boolean openCommand(CommandSender sender, String[] args) {
		//wws open [session]
		if (args.length != 2 || args[1].isEmpty()) {
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws open " + ChatFormat.SESSION.wrap("[session]"));
			return true;
		}
		
		String name = args[1];
		if (WorkersAndWarriorsPlugin.plugin.getSession(name) == null) {
			sender.sendMessage(ChatFormat.ERROR + "Unable to locate session " + ChatFormat.SESSION.wrap(name));
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws open " + ChatFormat.SESSION.wrap("[session]"));
			return true;
		}
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(name);
				
		if (!session.open()) {
			sender.sendMessage(ChatFormat.ERROR.wrap("Session not opened!"));
			sender.sendMessage(ChatFormat.INFO + "This is usually because the session isn't ready to open. "
					+ "Try using " + ChatFormat.IMPORTANT.wrap("/wws info name"));
			return true;
		}
		sender.sendMessage(ChatFormat.SUCCESS + "The session " + ChatFormat.SESSION + name 
				+ ChatFormat.SUCCESS.wrap(" is now open!"));
			
		return true;
	}
	
	private boolean startCommand(CommandSender sender, String[] args) {
		//wws start [session]
		if (args.length != 2 || args[1].isEmpty()) {
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws start " + ChatFormat.SESSION.wrap("[session]"));
			return true;
		}
		
		String name = args[1];
		if (WorkersAndWarriorsPlugin.plugin.getSession(name) == null) {
			sender.sendMessage(ChatFormat.ERROR + "Unable to locate session " + ChatFormat.SESSION.wrap(name));
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws start " + ChatFormat.SESSION.wrap("[session]"));
			return true;
		}
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(name);
				
		if (!session.start()) {
			sender.sendMessage(ChatFormat.ERROR.wrap("Session not started!"));
			sender.sendMessage(ChatFormat.INFO + "This is usually because the session isn't ready to start. "
					+ "Try using " + ChatFormat.IMPORTANT.wrap("/wws info name"));
			return true;
		}
		sender.sendMessage(ChatFormat.SUCCESS + "The session " + ChatFormat.SESSION + name 
				+ ChatFormat.SUCCESS.wrap(" is now started!"));
		
		return true;
	}
	
	private boolean stopCommand(CommandSender sender, String[] args) {
		//wws stop [session] {force}
		if ((args.length != 2 && args.length != 3) || args[1].isEmpty()) {
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws stop " + ChatFormat.SESSION + "[session]"
						+ ChatFormat.USAGE.wrap(" {force?}"));
			return true;
		}
		
		boolean force = false;
		if (args.length == 3) {
			if (!args[2].isEmpty() && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("force"))) {
				force = true;
			}
		}
		
		String name = args[1];
		if (WorkersAndWarriorsPlugin.plugin.getSession(name) == null) {
			sender.sendMessage(ChatFormat.ERROR + "Unable to locate session " + ChatFormat.SESSION.wrap(name));
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wws stop " + ChatFormat.SESSION + "[session]"
						+ ChatFormat.USAGE.wrap(" {force?}"));
			return true;
		}
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(name);
				
		if (!session.stop(force)) {
			sender.sendMessage(ChatFormat.ERROR.wrap("Session not stopped!"));
			sender.sendMessage(ChatFormat.INFO + "This is usually because the session isn't ready to start. "
					+ "Try using " + ChatFormat.IMPORTANT.wrap("/wws info name"));
			return true;
		}
		sender.sendMessage(ChatFormat.SUCCESS + "The session " + ChatFormat.SESSION + name 
				+ ChatFormat.SUCCESS.wrap(" has been stopped!"));
		
		return true;
	}
	
	private boolean infoCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean listCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean stopAllCommand(CommandSender sender, String[] args) {
		return false;
	}
	
	private boolean saveCommand(CommandSender sender, String[] args) {
		return false;
	}

}

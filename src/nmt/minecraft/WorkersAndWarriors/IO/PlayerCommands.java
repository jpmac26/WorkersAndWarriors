package nmt.minecraft.WorkersAndWarriors.IO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;
import nmt.minecraft.WorkersAndWarriors.Team.Team;
import nmt.minecraft.WorkersAndWarriors.Team.WWPlayer.WWPlayer;

public class PlayerCommands implements CommandExecutor {

	public static final String baseCommand = "wwplayer";
	
	public enum SubCommand {
		JOIN("join"),
		LEAVE("leave"),
		TEAM("team"),
		MENU("menu"),
		CLASS("class");
		
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
			
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can run these commands!");
			}
			
			Player player = (Player) sender;
			
			String subCmd = args[0];
			
			if (subCmd.equalsIgnoreCase(SubCommand.JOIN.getName())) {
				return joinCommand(player, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.LEAVE.getName())) {
				return leaveCommand(player, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.TEAM.getName())) {
				return teamCommand(player, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.MENU.getName())) {
				return menuCommand(player, args);
			}
			
			if (subCmd.equalsIgnoreCase(SubCommand.CLASS.getName())) {
				return classCommand(player, args);
			}
			
			return false;
		} else {
			return false;
		}
		
	}
	
	private boolean joinCommand(Player sender, String[] args) {	
		//wwp join [session]
		if (args.length != 2 || args[1].isEmpty()) {
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wwp " + SubCommand.JOIN.getName() + ChatFormat.SESSION.wrap("[session]"));
			return true;
		}
		
		String name = args[1];
		if (WorkersAndWarriorsPlugin.plugin.getSession(name) == null) {
			sender.sendMessage(ChatFormat.ERROR + "Unable to locate session " + ChatFormat.SESSION.wrap(name));
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wwp " + SubCommand.JOIN.getName() + ChatFormat.SESSION.wrap("[session]"));
			return true;
		}
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(name);
		
		if (session.getState() != GameSession.State.OPEN) {
			sender.sendMessage(ChatFormat.WARNING.wrap("The session is not open! It may not have opened "
					+ "yet, or already have started."));
			return true;
		}
		
		WWPlayer wp = session.addPlayer(sender);
		
		if (wp == null) {
			sender.sendMessage(ChatFormat.ERROR.wrap("You are already part of that session!"));
			return true;
		}
				
		return true;
	}
	
	private boolean leaveCommand(Player sender, String[] args) {
		//wwp leave
		if (args.length != 1) {
			sender.sendMessage(
					ChatFormat.USAGE + "Usage: /wwp " + SubCommand.LEAVE.getName());
			return true;
		}
		
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(sender);
		
		if (session == null) {
			sender.sendMessage(ChatFormat.WARNING.wrap("You are not currently part of a session!"));
			return true;
		}
		
		if (!session.removePlayer(sender)) {
			sender.sendMessage(ChatFormat.ERROR.wrap("Something went wrong, and you were not removed."));
			return true;
		}
		
		sender.sendMessage(ChatFormat.SUCCESS.wrap("You have left ") + ChatFormat.SESSION.wrap(session.getName()));
				
		return true;
	}
	
	private boolean teamCommand(Player sender, String[] args) {
		//wwp team [team]
		if (args.length != 2) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwtp " + SubCommand.TEAM.getName() 
					+ ChatFormat.TEAM.wrap(" [team]"));
			return true;
		}
		
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(sender);
		
		if (session == null) {
			sender.sendMessage(ChatFormat.WARNING.wrap("You must join a session first!"));
			sender.sendMessage(ChatFormat.INFO.wrap("To join, use /wwp join ") 
					+ ChatFormat.SESSION.wrap("[session]"));
			return true;			
		}
		
		WWPlayer wp = session.getPlayer(sender);		
		
		Team team = session.getTeam(args[1]);
		if (team == null) {
			sender.sendMessage(ChatFormat.ERROR.wrap("Could not find team ")
					+ ChatFormat.TEAM.wrap(args[1]));
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwp " + SubCommand.TEAM.getName() 
					+ ChatFormat.TEAM.wrap(" [team]"));
			return true;
		}

		//if they're on a team or unsorted, remove them so we can insert them cleanly
		session.removePlayer(wp);
		
		team.addPlayer(wp);
		sender.sendMessage(ChatFormat.SUCCESS.wrap("You have joined the team ") 
				+ ChatFormat.TEAM.wrap(team.getTeamName()));
		
		
		return true;
	}
	
	private boolean menuCommand(Player sender, String[] args) {
		sender.sendMessage(ChatFormat.INFO.wrap("Not yet implemented!"));
		return true;
	}
	
	private boolean classCommand(Player sender, String[]  args) {
		//wwp class [class]
		if (args.length != 2) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwp " + SubCommand.CLASS.getName() 
					+ ChatFormat.CLASS.wrap(" [class]"));
			return true;
		}
		
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(sender);
		
		if (session == null) {
			sender.sendMessage(ChatFormat.WARNING.wrap("You must join a session first!"));
			sender.sendMessage(ChatFormat.INFO.wrap("To join, use /wwp join ") 
					+ ChatFormat.SESSION.wrap("[session]"));
			return true;			
		}
		
		WWPlayer wp = session.getPlayer(sender);
		
		if (session.getState() != GameSession.State.OPEN) {
			sender.sendMessage(ChatFormat.ERROR.wrap("You cannot change your class once the game has started!"));
			return true;
		}
		
		String className = args[1].trim().toUpperCase();
		
		WWPlayer.Type cType = null;
		try {
			cType = WWPlayer.Type.valueOf(className);
		} catch (Exception e) {
			sender.sendMessage(ChatFormat.ERROR.wrap("Unable to determine which class " + args[1] + " refers to!"));
			return true;
		}
		
		wp.setType(cType);
		sender.sendMessage(ChatFormat.SUCCESS.wrap("Your class has been changed to ") 
				+ ChatFormat.CLASS.wrap(className.toLowerCase()));
				
		return true;
	}

}

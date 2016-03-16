package nmt.minecraft.WorkersAndWarriors.IO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;
import nmt.minecraft.WorkersAndWarriors.Team.Team;

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
		//wwt create [session] [team]
		if (args.length != 3) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.CREATE.getName() 
					+ ChatFormat.SESSION + " [session] " + ChatFormat.TEAM.wrap("[team]"));
			return true;
		}
		
		GameSession session = fetchSession(sender, args[1]);
		if (session == null) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.CREATE.getName() 
					+ ChatFormat.SESSION + " [session] " + ChatFormat.TEAM.wrap("[team]"));
			return true;			
		}
		
		if (session.getTeam(args[2]) != null) {
			sender.sendMessage(ChatFormat.ERROR.wrap("A team with that name already is in that session!"));
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.CREATE.getName() 
					+ ChatFormat.SESSION + " [session] " + ChatFormat.TEAM.wrap("[team]"));
			return true;
		}
		
		Team team = new Team(args[2]);
		session.addTeam(team);
		
		sender.sendMessage(ChatFormat.SUCCESS + "Team " + ChatFormat.TEAM + args[2]
				+ ChatFormat.SUCCESS + " added to " + ChatFormat.SESSION.wrap(args[1]));
	
		return true;
	}
	
	private boolean infoCommand(CommandSender sender, String[] args) {
		//wwt info [session] [team]
		if (args.length != 3) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.INFO.getName() 
					+ ChatFormat.SESSION + " [session] " + ChatFormat.TEAM.wrap("[team]"));
			return true;
		}
		
		GameSession session = fetchSession(sender, args[1]);
		if (session == null) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.INFO.getName() 
					+ ChatFormat.SESSION + " [session] " + ChatFormat.TEAM.wrap("[team]"));
			return true;			
		}
		
		Team team = session.getTeam(args[2]);
		if (team == null) {
			sender.sendMessage(ChatFormat.ERROR.wrap("Could not find team ")
					+ ChatFormat.TEAM.wrap(args[2]));
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.INFO.getName() 
					+ ChatFormat.SESSION + " [session] " + ChatFormat.TEAM.wrap("[team]"));
			return true;
		}
		
		/*
		 * Team - Ready/Not Ready
		 * Color: COLOR
		 * Spawnpoints: #
		 * FlagArea: [set/unset]
		 * Block Type: [set/unset]
		 * Flag Type: [set/unset]
		 * Members: #
		 */
		sender.sendMessage(ChatFormat.TEAM.wrap(team.getTeamName()) + " - "
				+ (team.isReady() ? ChatFormat.SUCCESS.wrap("Ready")
								  : ChatFormat.ERROR.wrap("Not Ready")));
		sender.sendMessage(ChatFormat.INFO.wrap("Color: ")
				+ (team.getTeamColor() == null ? ChatFormat.ERROR.wrap("Unset")
						  : ChatFormat.SUCCESS.wrap(team.getTeamColor() + team.getTeamColor().name())));
		sender.sendMessage(ChatFormat.INFO.wrap("Spawn Points: ")
				+ (team.getSpawnPoints().isEmpty() ? ChatFormat.ERROR.wrap("None")
						  : ChatFormat.SUCCESS.wrap("" + team.getSpawnPoints().size())));
		sender.sendMessage(ChatFormat.INFO.wrap("Flag Area: ")
				+ (team.getFlagArea() == null ? ChatFormat.ERROR.wrap("Unset")
						  : ChatFormat.SUCCESS.wrap(team.getFlagArea().getMin() + " to " + team.getFlagArea().getMax())));
		sender.sendMessage(ChatFormat.INFO.wrap("Block Type: ")
				+ (team.getBlockType() == null ? ChatFormat.ERROR.wrap("Unset")
						  : ChatFormat.SUCCESS.wrap("Set")));
		sender.sendMessage(ChatFormat.INFO.wrap("Flag Type: ")
				+ (team.getGoalType() == null ? ChatFormat.ERROR.wrap("Unset")
											  : ChatFormat.SUCCESS.wrap("Set")));
		sender.sendMessage(ChatFormat.INFO.wrap("Members: ") + 
				(team.getPlayers().isEmpty() ? ChatFormat.WARNING.wrap("No players!") 
											: ChatFormat.SUCCESS.wrap("" + team.getPlayers().size())));
		
		return true;
	}
	
	private boolean listCommand(CommandSender sender, String[] args) {
		//wwt list [session]
		if (args.length != 2) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.LIST.getName() 
					+ ChatFormat.SESSION + " [session] ");
			return true;
		}
		
		GameSession session = fetchSession(sender, args[1]);
		if (session == null) {
			sender.sendMessage(ChatFormat.USAGE + "Usage: /wwt " + SubCommand.LIST.getName() 
					+ ChatFormat.SESSION + " [session] ");
			return true;			
		}
		
		sender.sendMessage(ChatFormat.INFO.wrap("Current Teams for session ") + ChatFormat.SESSION.wrap(args[1]));
		String msg = "";
		
		if (session.getTeams().isEmpty()) {
			msg = ChatFormat.ERROR.wrap("No teams!");
		} else {
			boolean color = false;
			for (Team t : session.getTeams()) {
				msg += (color ? ChatColor.DARK_BLUE : ChatColor.DARK_GREEN) + t.getTeamName();
				color = !color;
			}			
		}
		sender.sendMessage(msg);
		
		
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
	
	private GameSession fetchSession(CommandSender sender, String name) {
		if (WorkersAndWarriorsPlugin.plugin.getSession(name) == null) {
			sender.sendMessage(ChatFormat.ERROR + "Unable to locate session " + ChatFormat.SESSION.wrap(name));
			return null;
		}
		return WorkersAndWarriorsPlugin.plugin.getSession(name);
	}

}

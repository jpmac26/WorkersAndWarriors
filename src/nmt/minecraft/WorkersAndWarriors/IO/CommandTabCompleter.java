package nmt.minecraft.WorkersAndWarriors.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.SessionConfiguration;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;
import nmt.minecraft.WorkersAndWarriors.Team.Team;

public class CommandTabCompleter implements TabCompleter{
	
	private static CommandTabCompleter completer;
	
	private static List<Material> blockList;
	
	public static CommandTabCompleter getCompleter() {
		if (completer == null) {
			completer = new CommandTabCompleter();
		}
		
		return completer;
	}
	
	private CommandTabCompleter() {
		blockList = getBlockList();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase(PluginCommands.baseCommand)){
			return completePluginCommand(args);
		}
		if (cmd.getName().equalsIgnoreCase(SessionCommands.baseCommand)) {
			return completeSessionCommand(args);
		}
		if (cmd.getName().equalsIgnoreCase(PlayerCommands.baseCommand)) {
			return completePlayerCommand(sender, args);
		}
		if (cmd.getName().equalsIgnoreCase(TeamCommands.baseCommand)) {
			return completeTeamCommand(args);
		}
		return null;
	}
	
	private static boolean startsWithIgnoreCase(String string1, String string2){
		string1 = string1.toLowerCase();
		string2 = string2.toLowerCase();
		return string1.startsWith(string2);
	}
	
	private List<String> completePluginCommand(String[] args) {
		List<String> list=new ArrayList<String>();
		if(args.length == 1) {
			List<String> tmpList;
			 tmpList = PluginCommands.getCommandList();//get the list of commands
			 //only put the ones that start with the given
			 
			 if(args[0].isEmpty()){
				 return tmpList;
			 }
			 
			 for(String tmpString : tmpList){
				 String incomplete = args[0].toLowerCase();
				 if(startsWithIgnoreCase(tmpString,incomplete)){
					 list.add(tmpString);
				 }
			 }
			 
		}
		
		return list;
	}
	
	private List<String> completePlayerCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			return new ArrayList<String>(1);
		}
		
		List<String> list = null;
		if(args.length == 1){
			list=new ArrayList<String>();
			List<String> tmpList;
			 tmpList = PlayerCommands.getCommandList();//get the list of commands
			 //only put the ones that start with the given
			 
			 if(args[0].isEmpty()){
				 return tmpList;
			 }
			 
			 for(String tmpString : tmpList){
				 String incomplete = args[0].toLowerCase();
				 if(startsWithIgnoreCase(tmpString,incomplete)){
					 list.add(tmpString);
				 }
			 }
		} else if (args.length == 2) {
			//wpg [cmd] [arg]
			if (args[0].equalsIgnoreCase(PlayerCommands.SubCommand.TEAM.getName())) {
				list = completePlayerTeamCommand((Player) sender, args);
			} else if (args[0].equalsIgnoreCase(PlayerCommands.SubCommand.JOIN.getName())) {
				list = completeSessionJoinCommand((Player) sender, args);
			}
		}
		
		
		
		
		
		return list;
	}
	
	private List<String> completeTeamCommand(String[] args) {
		List<String> list = null;

		if(args.length == 1){
			list=new ArrayList<String>();
			List<String> tmpList;
			 tmpList = TeamCommands.getCommandList();//get the list of commands
			 //only put the ones that start with the given
			 
			 if(args[0].isEmpty()){
				 return tmpList;
			 }
			 
			 for(String tmpString : tmpList){
				 String incomplete = args[0].toLowerCase();
				 if(startsWithIgnoreCase(tmpString,incomplete)){
					 list.add(tmpString);
				 }
			 }
		} else if (args[0].equalsIgnoreCase(TeamCommands.SubCommand.CREATE.getName())) {
			list = completeSimpleSessionCommand(args);
		} else if (args[0].equalsIgnoreCase(TeamCommands.SubCommand.INFO.getName())
				|| args[0].equalsIgnoreCase(TeamCommands.SubCommand.SETGOALAREA.getName())
				|| args[0].equalsIgnoreCase(TeamCommands.SubCommand.SETSPAWN.getName())) {
			list = completeSimpleTeamCommand(args);
		} else if (args[0].equalsIgnoreCase(TeamCommands.SubCommand.SETBLOCK.getName())
			|| args[0].equalsIgnoreCase(TeamCommands.SubCommand.SETGOALBLOCK.getName())) {
			list = completeTeamBlockCommand(args);
		} else if (args[0].equalsIgnoreCase(TeamCommands.SubCommand.SETCOLOR.getName())) {
			list = completeTeamColorCommand(args);
		}
		
		
		return list;
	}
	
	private List<String> completeSessionCommand(String[] args) {
		List<String> list = null;
		if(args.length == 1){
			list=new ArrayList<String>();
			List<String> tmpList;
			 tmpList = SessionCommands.getCommandList();//get the list of commands
			 //only put the ones that start with the given
			 
			 if(args[0].isEmpty()){
				 return tmpList;
			 }
			 
			 for(String tmpString : tmpList){
				 String incomplete = args[0].toLowerCase();
				 if(startsWithIgnoreCase(tmpString,incomplete)){
					 list.add(tmpString);
				 }
			 }
		} else if (args[0].equalsIgnoreCase(SessionCommands.SubCommand.LOADTEMPLATE.getName())
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.SAVETEMPLATE.getName())) {
			list = completeTemplateSubCommand(args);
		} else if (args[0].equalsIgnoreCase(SessionCommands.SubCommand.INFO.getName())
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.OPEN.getName())
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.START.getName())
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.STOP.getName())
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.SETBLOCKLIMIT.getName())
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.SETFLAGPROTECTIONRADIUS.getName())
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.SETLOBBY.getName())) {
			list = completeSimpleSessionCommand(args);
		}		
		
		return list;
	}
	
	/**
	 * Auto completes either a save or load template command
	 * @param args
	 * @return
	 */
	private List<String> completeTemplateSubCommand(String[] args) {
		//just wanna complete template list. 
		List<String> list;
		if (args.length == 2) {
			//complete template name
			File[] files = SessionConfiguration.getTemplateDirectory().listFiles();
			
			list = new ArrayList<String>(files.length);
			for (File f : files) {
				list.add(f.getName());
			}
			
			return list;
		} else if (args[0].equalsIgnoreCase(SessionCommands.SubCommand.SAVETEMPLATE.getName()) 
				&& args.length == 3) {
			//saving takes an existing session. complete those names
			list = new ArrayList<String>(WorkersAndWarriorsPlugin.plugin.getSessions().size());
			for(GameSession game : WorkersAndWarriorsPlugin.plugin.getSessions()){
				//should only match games started with what's already been typed in					
				if(args[2].isEmpty() || startsWithIgnoreCase(game.getName(),args[2])){
					list.add(game.getName());
				}
			}
			return list;
		}
		
		
		return new ArrayList<String>();
		
	}
	
	/**
	 * Completes a sub command that only takes one argument - an active session
	 * @param args
	 * @return
	 */
	private List<String> completeSimpleSessionCommand(String[] args) {
		List<String> list = new ArrayList<>(WorkersAndWarriorsPlugin.plugin.getSessions().size());
		
		if (args.length == 2) {
			for(GameSession game : WorkersAndWarriorsPlugin.plugin.getSessions()){
				//should only match games started with what's already been typed in					
				if(args[1].isEmpty() || startsWithIgnoreCase(game.getName(),args[1])){
					list.add(game.getName());
				}
			}
		}
		
		
		return list;
	}
	
	private List<String> completePlayerTeamCommand(Player sender, String[] args) {
		//wwp team [team]
		List<String> list;
		
		GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(sender);
		if (session == null) {
			return new ArrayList<>(1);
		}
		
		list = new ArrayList<String>(session.getTeams().size());
		
		if (session.getTeams().isEmpty()) {
			return new ArrayList<>(1);
		}
		
		for (Team team : session.getTeams()) {
			if (args[1].isEmpty() || startsWithIgnoreCase(team.getTeamName(), args[1])) {
				list.add(team.getTeamName());
			}
		}
		
		return list;	
	}
	
	private List<String> completeSessionJoinCommand(Player sender, String[] args) {
		//wwp join [session]
		List<String> list = new ArrayList<>(WorkersAndWarriorsPlugin.plugin.getSessions().size());
		
		for(GameSession game : WorkersAndWarriorsPlugin.plugin.getSessions()){
			//should only match games started with what's already been typed in					
			if(args[1].isEmpty() || startsWithIgnoreCase(game.getName(),args[1])){
				list.add(game.getName());
			}
		}
		
		return list;
	}
	
	private List<String> completeSimpleTeamCommand(String[] args) {
		//wwt [subcommand] [session] [team]
		List<String> list = null;
		
		if (args.length == 2) {
			//just need simle list of sessions
			list = completeSimpleSessionCommand(args);
		} else if (args.length == 3) {
			//on the team part, give list of teams
			GameSession session = WorkersAndWarriorsPlugin.plugin.getSession(args[1]);
			if (session == null) {
				return new LinkedList<String>();
			}
			
			list = new ArrayList<String>(session.getTeams().size());
			
			if (session.getTeams().isEmpty()) {
				return new ArrayList<>(1);
			}
			
			for (Team team : session.getTeams()) {
				if (args[2].isEmpty() || startsWithIgnoreCase(team.getTeamName(), args[2])) {
					list.add(team.getTeamName());
				}
			}
			
		}
				
		return list;
	}
	
	private List<String> completeTeamBlockCommand(String[] args) {
		//wwt [subcommand] [session] [team] [material/id] [data]
		List<String> list = null;;
		
		if (args.length < 4) {
			list = completeSimpleTeamCommand(args);
		} else if (args.length == 4) {
			//get material
			list = new ArrayList<String>(Material.values().length);
			for (Material mat : blockList) {
				if (args[3].isEmpty() || startsWithIgnoreCase(mat.name(), args[3])) {
					list.add(mat.name());
				}
			}
		}
		
		return list;
	}
	
	private List<String> completeTeamColorCommand(String[] args) {
		List<String> list = null;
		
		if (args.length < 4) {
			list = completeSimpleTeamCommand(args);
		} else if (args.length == 4) {
			//get color
			list = new ArrayList<String>(ChatColor.values().length);
			for (ChatColor color : ChatColor.values()) {
				if (args[3].isEmpty() || startsWithIgnoreCase(color.name(), args[3])) {
					list.add(color.name());
				}
			}
		}
		
		return list;
	}
	
	private static List<Material> getBlockList() {
		List<Material> list = new ArrayList<Material>(Material.values().length / 2);
		for (Material mat : Material.values()) {
			if (mat.isBlock()) {
				list.add(mat);
			}
		}
		return list;
	}

}

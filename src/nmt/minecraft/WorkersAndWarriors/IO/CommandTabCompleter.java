package nmt.minecraft.WorkersAndWarriors.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import nmt.minecraft.WorkersAndWarriors.WorkersAndWarriorsPlugin;
import nmt.minecraft.WorkersAndWarriors.Config.SessionConfiguration;
import nmt.minecraft.WorkersAndWarriors.Session.GameSession;

public class CommandTabCompleter implements TabCompleter{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase(PluginCommands.baseCommand)){
			return completePluginCommand(args);
		}
		if (cmd.getName().equalsIgnoreCase(SessionCommands.baseCommand)) {
			return completeSessionCommand(args);
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
				|| args[0].equalsIgnoreCase(SessionCommands.SubCommand.STOP.getName())) {
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
				if(args[1].isEmpty() || startsWithIgnoreCase(game.getName(),args[1])){
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

}

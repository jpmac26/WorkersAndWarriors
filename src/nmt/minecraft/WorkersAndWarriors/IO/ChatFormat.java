package nmt.minecraft.WorkersAndWarriors.IO;

import org.bukkit.ChatColor;

/**
 * Holds standardized chat formats
 * @author Skyler
 *
 */
public enum ChatFormat {
	
	ERROR(ChatColor.DARK_RED),
	WARNING(ChatColor.YELLOW),
	SESSION(ChatColor.DARK_PURPLE),
	TEAM(ChatColor.BLUE),
	SUCCESS(ChatColor.GREEN),
	INFO(ChatColor.GRAY),
	USAGE(ChatColor.RED),
	IMPORTANT(ChatColor.DARK_GREEN, ChatColor.BOLD);
	
	private String format;
	
	private ChatFormat(ChatColor color, ChatColor ... colors) {
		this.format = "" + color;
		if (colors.length > 0) {
			for (ChatColor c : colors) {
				format += "" + c;
			}
		}
	}
	
	@Override
	public String toString() {
		return format;
	}
	
	/**
	 * Wraps the passed string in the format, including resetting afterwards
	 * @param msg
	 * @return
	 */
	public String wrap(String msg) {
		return format + msg + ChatColor.RESET;
	}
}

package nmt.minecraft.WorkersAndWarriors.IO;

import org.bukkit.ChatColor;

/**
 * Holds standardized chat formats.<br />
 * Some examples of usage:
 * <p>
 * <i>like {@link ChatColor}</i>: ChatFormat.WARNING + "Careful! " + ChatFormat.INFO + "There are words!"
 * </p>
 * <p>
 * <i>using {@link #wrap(String)}</i>: ChatFormat.SUCCESS.wrap("Great job! Success!");
 * </p>
 * @author Skyler
 *
 */
public enum ChatFormat {
	
	ERROR(ChatColor.DARK_RED),
	WARNING(ChatColor.YELLOW),
	SESSION(ChatColor.DARK_PURPLE),
	TEAM(ChatColor.BLUE),
	TEMPLATE(ChatColor.GOLD),
	SUCCESS(ChatColor.GREEN),
	INFO(ChatColor.GRAY),
	USAGE(ChatColor.RED),
	IMPORTANT(ChatColor.DARK_GREEN, ChatColor.BOLD),
	CLASS(ChatColor.AQUA);
	
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

package nmt.minecraft.WorkersAndWarriors.Scheduling;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nmt.minecraft.WorkersAndWarriors.Session.GameSession;

public class GameFinishAnimationEndEvent extends Event {

private static final HandlerList handlers = new HandlerList();
	
	private GameSession session;
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public GameFinishAnimationEndEvent(GameSession session) {
		this.session = session;
	}
	
	public GameSession getSession() {
		return this.session;
	}
	
}

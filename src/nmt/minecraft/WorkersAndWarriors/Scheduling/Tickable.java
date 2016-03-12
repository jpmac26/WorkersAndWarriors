package nmt.minecraft.WorkersAndWarriors.Scheduling;

/**
 * Marks a class as being able to be ticked. The way this scheduling works is like so:
 * <p>
 * An object asks the {@link Scheduler} to remind it about something in some time. It gives the {@link Scheduler}
 * an object to bring back to it after that time to remind it about something.<br />
 * The scheduler waits, and then 'alarms' the object after the provided time. the {@link Scheduler} also gives
 * the object back the object it was given.
 * </p>
 * @author Skyler
 *
 * @param <T>
 */
public interface Tickable<T> {
	/**
	 * Called when the scheduler is trying to remind the registered Tickable object of something.<br />
	 * The reference passed back is the same provided when registered.<br />
	 * Receipt of this method indicates that the object is no longer registered with the scheduler.
	 * @param reference
	 */
	public void alarm (T reference);
	
	/**
	 * Checks whether this method is equal to the passed.<br />
	 * Tickable objects NEED to be able to tell if another object is the same as them, for lookup in a map.
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o);
	
}

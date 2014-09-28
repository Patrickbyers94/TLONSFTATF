package game;

import org.jdom2.Element;

/**
 * Interface used for items. These are objects the user can pick up and use
 * (potions, weapons,...).
 */

public interface Item extends GameObject {

	/** Triggers the main action of the item. */
	public boolean execute(Player p);

	/** Adds the item to the player's inventory. */
	public void getPickedUp(Player p);

	/** Returns what type of item this is*/
	public String getType();

	/**
	 * Create an XML Element representation of this Item for saving
	 *
	 * @return Element of this Item
	 */
	public Element toElement();

}

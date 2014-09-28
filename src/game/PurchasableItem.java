package game;

/** An item that can be bought from the shop. */
public interface PurchasableItem extends Item {

	/** Return the cost of the item. */
	public int getCost();

}

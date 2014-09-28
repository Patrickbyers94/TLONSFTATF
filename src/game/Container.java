package game;

/** Interface used for containers (Chests, bags). */

public interface Container extends GameObject {

	public void add(Item i);

	public Item remove();

}

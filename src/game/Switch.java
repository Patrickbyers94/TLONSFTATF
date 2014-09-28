package game;

/**
 * A position that unlocks a specific door when a gameObject is placed on it.
 */
public class Switch extends Position {

	private Door door;

	public Switch(int col, int row, Door door) {
		super(col, row);
		this.door = door;
	}

	@Override
	public void setGameObject(GameObject gameObject) {
		super.setGameObject(gameObject);
		if (gameObject == null)
			door.lock();
		else
			door.unlock();
	}

}

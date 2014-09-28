package game;

/**
 * A position that teleports the gameObject that goes on it to another position
 * in the same world.
 */
public class Teleporter extends Position {

	private Position destination;

	public Teleporter(int col, int row, Position destination) {
		super(col, row);
		this.destination = destination;
	}

	@Override
	public void setGameObject(GameObject g) {
		super.setGameObject(g);

		// object needs teleporting
		if (gameObject != null) {
			// destination is free, teleport on
			if (!destination.hasGameObject()) {
				destination.setGameObject(g);
				g.setPosition(destination);
				gameObject = null;
			}
			// destination already has a gameObject
			else {
				System.out
						.println("There is already a gameObject at the destination!"
								+ destination.getGameObject());
			}
		}

	}

}

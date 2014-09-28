package game;

import org.jdom2.Element;

/**
 * A movable object. If a player walks into it, it moves in the direction the
 * user pushed, if the target position does not already contain a gameObject.
 */

public abstract class MovableObject implements GameObject {

	protected Position position;
	protected int orientation; // needed for ice
	protected World room;

	/**
	 * This method is called when a player walks into this object. It is moved
	 * in the direction the player moved, if possible.
	 */
	public void move(int direction) {
		orientation = direction;
		Position newP = room.getAdjacentPosition(position, direction);
		if (newP == null || !newP.isPassable() || newP.hasPlayer()
				|| newP.hasItem() || newP.hasGameObject() || newP.isDoor())
			return;
		position.setGameObject(null);
		setPosition(newP);
		newP.setGameObject(this);

	}

	public void setPosition(Position p) {
		this.position = p;
	}

	public Position getPosition() {
		return this.position;
	}

	public void setRoom(World room) {
		this.room = room;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public Element toElement() {
		return null; // must overwrite this in extending classes
	}

}

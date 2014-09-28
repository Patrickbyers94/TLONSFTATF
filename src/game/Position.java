package game;

import org.jdom2.Element;

/**
 * A position in a world. It may have a gameObject and may or may not be
 * passable.
 */

public class Position {

	private int row;
	private int col;
	private int type = 0;
	protected GameObject gameObject;
	private boolean isPassable = true;

	private Position() {
	}

	public Position(int col, int row) {
		this.row = row;
		this.col = col;
	}

	public void setImpassable() {
		this.isPassable = false;
	}

	public boolean isPassable() {
		return this.isPassable;
	}

	public boolean hasPlayer() {
		return gameObject instanceof Player;
	}

	public boolean hasItem() {
		return gameObject instanceof Item;
	}

	public boolean hasMonster() {
		return gameObject instanceof Monster;
	}

	public boolean isDoor() {
		return gameObject instanceof Door;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean hasGameObject() {
		return gameObject != null;
	}

	public boolean hasMovableObject() {
		return gameObject instanceof MovableObject;
	}

	public GameObject getGameObject() {
		return this.gameObject;
	}

	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return "(" + col + ", " + row + ")";
	}

	public Element toElement() {
		Element me = new Element("position");
		me.addContent(new Element("x").setText(Integer.toString(col)));
		me.addContent(new Element("y").setText(Integer.toString(row)));
		return me;
	}


}

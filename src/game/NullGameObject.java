package game;

import graphics.Camera;

import java.awt.Graphics2D;

import org.jdom2.Element;

/**
 * Class Chris made. Not sure what it's used for.
 */
public class NullGameObject implements GameObject {

	Position position;

	NullGameObject(Position p) {
		this.position = p;
	}

	@Override
	public String getInspected(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void takeHit(int attack) {
		// TODO Auto-generated method stub

	}

	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPosition(Position p) {
		// TODO Auto-generated method stub
		this.position = p;

	}

	@Override
	public void draw(Graphics2D g, int x, int y,int width,int height) {
		// TODO Auto-generated method stub

	}


}

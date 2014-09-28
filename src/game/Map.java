package game;

import graphics.Camera;

import java.awt.Graphics2D;

import org.jdom2.Element;

/** Not sure if using this class yet.*/

public class Map implements Item {

	@Override
	public String getInspected(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean execute(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getPickedUp(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void takeHit(int attack) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPosition(Position p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub

	}
	@Override
	public void draw(Graphics2D g, int x, int y,int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getType() {
		return "Map";
	}

}

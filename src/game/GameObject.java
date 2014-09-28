package game;

import java.awt.Graphics2D;

import org.jdom2.Element;

import graphics.Camera;

/**
 * Interface used for any objects in the game. This includes players, items,
 * decorative objects, ...
 */

public interface GameObject {

	/** Prints out a description of the object. */
	public String getInspected(Player p);

	/** Attacks the object, or tries to anyway. */
	public void takeHit(int attack);

	public Position getPosition();

	public void setPosition(Position p);

	public void draw(Graphics2D g, Camera cam);

	public void draw(Graphics2D g, int x, int y,int width,int height);



	public Element toElement();

}

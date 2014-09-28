package game;

import graphics.Camera;
import graphics.GameCanvas;
import graphics.SpecialEffects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import org.jdom2.Element;

/**
 * A door. Walking into a position which has a door teleports you to the
 * starting position of the room it leads to.
 */

public class Door implements GameObject {

	private World destination;
	private boolean locked;
	private Position position;

	private Door() {
	}

	public Door(World destination) {
		this.destination = destination;
	}

	/** Unlocks the door. */
	public void unlock() {
		System.out.println("Door was unlocked!");
		locked = false;
	}

	/** Locks the door. */
	public void lock() {
		locked = true;
	}

	public World getDestination() {
		return this.destination;
	}

	public void setDestination(World d) {
		this.destination = d;
	}

	public boolean isLocked() {
		return this.locked;
	}

	@Override
	public String getInspected(Player p) {
		if (locked)
			System.out.println("A locked door... Wonder where it leads.");
		else
			System.out.println("A door...");
		return "door";
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("Attacking a door?");

	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public String toString() {
		return "Door";
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub
		// Don't think i'm going to implement this. the doors are rendered to
		// the back ground already - CHris
	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toElement() {
		Element me = new Element("door");
		me.addContent(new Element("destination").setText(destination.getName()));
		me.addContent(new Element("locked").setText(Boolean.toString(locked)));
		me.addContent(position.toElement());
		return me;
	}

}

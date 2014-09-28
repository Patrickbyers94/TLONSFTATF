package game;

import graphics.Camera;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/** A bag. Items can be put into it and it can be put into chests. */

public class Bag implements Container, Item {

	private List<Item> items = new ArrayList<Item>();

	@Override
	public String getInspected(Player p) {
		System.out.println("A bag... It might contain some items.");
		return "A bag... It might contain some items.";
	}

	/** Puts an item into the bag. */
	@Override
	public void add(Item i) {
		items.add(i);
	}

	/** Remove the first item in the bag. */
	@Override
	public Item remove() {
		return items.remove(0);
	}

	@Override
	public boolean execute(Player p) {
		// not sure what happens when a bag is used...
		return true;
	}

	@Override
	public void getPickedUp(Player p) {
		System.out.println(p.getName() + " picked up a bag!");
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("Attacking a bag?");
	}

	@Override
	public Element toElement() {
		Element me = new Element("bag");
		Element contents = new Element("contents");
		for (Item i : items) {
			contents.addContent(i.toElement());
		}
		me.addContent(contents);
		return me;
	}

	public static Bag fromElement(Element e) {
		Bag bag = new Bag();
		Element contents = e.getChild("contents");
		List list = contents.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element elem = (Element) list.get(i);
			if (elem.getName().equals("potion")) {
				bag.items.add(Potion.fromElement(elem));
			} else if (elem.getName().equals("weapon")) {
				bag.items.add(Weapon.fromElement(elem));
			} else if (elem.getName().equals("key")) {
				bag.items.add(Key.fromElement(elem));
			}
			// TODO: other item cases
		}
		return bag;
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

	public String toString() {
		return "Bag";
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
		return "Bag";
	}

}

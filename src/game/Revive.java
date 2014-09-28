package game;

import graphics.Camera;

import java.awt.Graphics2D;

import org.jdom2.Element;

/**
 * An item that can revive a dead player.
 */

public class Revive implements PurchasableItem {

	private int cost = 50;

	public Revive() {
	}

	/**
	 * Revives a dead player.
	 */
	@Override
	public boolean execute(Player p) {
		Position target = p.getWorld().getAdjacentPosition(p.getPosition(),
				p.getOrientation());
		if (!target.hasPlayer() && ((Player) target.getGameObject()).isDead())
			return false;
		((Player) target.getGameObject()).revive();
		return true;
	}

	public void getPickedUp(Player p) {
		System.out.println(p.getName() + " picked up a revive!");
	}

	@Override
	public String getInspected(Player p) {
		System.out.println("An item that can revive a dead player");
		return "An item that can revive a dead player";
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("Attacking a revive?");
	}

	@Override
	public Position getPosition() {
		return null;
	}

	@Override
	public void setPosition(Position p) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCost() {
		return cost;
	}

	public String toString() {
		return "Revive";
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toElement() {
		Element me = new Element("Revive");
		return me;
	}

	public static Revive fromElement(Element e) {
		Revive pot = new Revive();
		return pot;

	}

	@Override
	public String getType() {
		return "Revive";
	}

}

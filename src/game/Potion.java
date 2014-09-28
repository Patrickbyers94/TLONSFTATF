package game;

import graphics.Camera;
import graphics.GameCanvas;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Element;

/**
 * A potion, a type of item. Using it restores the health of the player who used
 * it by a predetermined amount.
 */

public class Potion implements PurchasableItem {

	private int health;
	private int cost = 30;

	Image image;

	private Potion() {
		try {
			image = ImageIO.read(new File("assets/sprites/Potion.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Potion(int health) {
		this.health = health;
		try {
			image = ImageIO.read(new File("assets/sprites/Potion.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restores the player's health by max(health, player's max health - health)
	 */
	@Override
	public boolean execute(Player p) {
		if (p.getHealth() == p.getJob().getHealth()) {
			System.out.println("You're already at max health!");
			return false;
		}
		p.setHealth(p.getHealth() + health);
		if (p.getHealth() > p.getJob().getHealth())
			p.setHealth(p.getJob().getHealth());
		System.out.println(p.getName() + " regained " + health + "!");
		return true;
	}

	public void getPickedUp(Player p) {
		System.out.println(p.getName() + " picked up a potion!");
	}

	@Override
	public String getInspected(Player p) {
		System.out.println("A potion that restores " + health + " health.");
		return "A potion that restores " + health + " health.";
	}

	@Override
	public void takeHit(int attack) {
		System.out.println("Attacking a potion?");
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
		return "Potion: " + health;
	}

	@Override
	public void draw(Graphics2D g, Camera cam) {
		int x = this.getPosition().getCol() * GameCanvas.TILESIZE;
		int y = this.getPosition().getRow() * GameCanvas.TILESIZE;
		if (cam.isOnscreen(x, y)) {
			Point pn = cam.getPosOnScreen(x, y);

			g.drawImage(image, pn.x, pn.y, null);

		}

	}

	@Override
	public void draw(Graphics2D g, int x, int y, int width, int height) {
		image = image.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage buffered = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		buffered.getGraphics().drawImage(image, 0, 0, null);
		g.drawImage(image, x, y, null);
		buffered.getGraphics().dispose();
	}

	@Override
	public Element toElement() {
		Element me = new Element("potion");
		me.addContent(new Element("health").setText(Integer.toString(health)));
		return me;
	}

	public static Potion fromElement(Element e) {
		Potion pot = new Potion();
		pot.health = Integer.parseInt(e.getChild("health").getText());
		return pot;

	}

	@Override
	public String getType() {
		return "Potion";
	}

}
